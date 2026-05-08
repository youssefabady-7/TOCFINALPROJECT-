import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class RegexParser {
    public static final char CONCAT = '.';

    private final Set<Character> alphabet;
    private String postfix;

    public RegexParser() {
        this.alphabet = new TreeSet<Character>();
        this.postfix = "";
    }

    public String toPostfix(String regex) {
        alphabet.clear();

        List<Character> tokens = tokenize(regex);
        validate(tokens);
        List<Character> tokensWithConcat = insertConcatOperators(tokens);
        postfix = shuntingYard(tokensWithConcat);

        return postfix;
    }

    public Set<Character> getAlphabet() {
        return new TreeSet<Character>(alphabet);
    }

    public String getFormattedPostfix() {
        return formatExpression(postfix);
    }

    public static String formatExpression(String expression) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char token = expression.charAt(i);
            if (token == CONCAT) {
                builder.append('.');
            } else {
                builder.append(State.symbolToString(token));
            }
            if (i < expression.length() - 1) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    public static boolean isOperand(char token) {
        return token == State.EPSILON || isLiteral(token);
    }

    private List<Character> tokenize(String regex) {
        if (regex == null) {
            throw new IllegalArgumentException("Regular expression cannot be null.");
        }

        List<Character> tokens = new ArrayList<Character>();
        for (int i = 0; i < regex.length(); i++) {
            char current = regex.charAt(i);

            if (Character.isWhitespace(current)) {
                continue;
            }

            if (current == '.') {
                throw new IllegalArgumentException("The dot character is reserved for internal concatenation.");
            }

            if (current == '#' || current == '\u03B5') {
                tokens.add(State.EPSILON);
                continue;
            }

            if (current == '(' || current == ')' || current == '|' || isUnaryOperator(current)) {
                tokens.add(current);
                continue;
            }

            tokens.add(current);
            alphabet.add(current);
        }

        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Regular expression cannot be empty.");
        }

        return tokens;
    }

    private void validate(List<Character> tokens) {
        boolean expectingOperand = true;
        int balance = 0;

        for (int i = 0; i < tokens.size(); i++) {
            char token = tokens.get(i);

            if (isOperand(token)) {
                expectingOperand = false;
            } else if (token == '(') {
                balance++;
                expectingOperand = true;
            } else if (token == ')') {
                if (expectingOperand) {
                    throw new IllegalArgumentException("Missing expression before ')' at position " + (i + 1) + ".");
                }
                balance--;
                if (balance < 0) {
                    throw new IllegalArgumentException("Unmatched ')' at position " + (i + 1) + ".");
                }
                expectingOperand = false;
            } else if (token == '|') {
                if (expectingOperand) {
                    throw new IllegalArgumentException("Union operator '|' is missing a left operand.");
                }
                expectingOperand = true;
            } else if (isUnaryOperator(token)) {
                if (expectingOperand) {
                    throw new IllegalArgumentException("Operator '" + token + "' is missing an operand.");
                }
                expectingOperand = false;
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        if (balance != 0) {
            throw new IllegalArgumentException("Parentheses are not balanced.");
        }

        if (expectingOperand) {
            throw new IllegalArgumentException("Regular expression cannot end with a binary operator.");
        }
    }

    private List<Character> insertConcatOperators(List<Character> tokens) {
        List<Character> result = new ArrayList<Character>();
        Character previous = null;

        for (Character current : tokens) {
            if (previous != null && endsExpression(previous) && beginsExpression(current)) {
                result.add(CONCAT);
            }

            result.add(current);
            previous = current;
        }

        return result;
    }

    private String shuntingYard(List<Character> tokens) {
        StringBuilder output = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<Character>();

        for (Character token : tokens) {
            if (isOperand(token)) {
                output.append(token);
            } else if (isUnaryOperator(token)) {
                output.append(token);
            } else if (token == '(') {
                stack.push(token);
            } else if (token == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.append(stack.pop());
                }

                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Parentheses are not balanced.");
                }
                stack.pop();
            } else if (isBinaryOperator(token)) {
                while (!stack.isEmpty()
                        && isBinaryOperator(stack.peek())
                        && precedence(stack.peek()) >= precedence(token)) {
                    output.append(stack.pop());
                }
                stack.push(token);
            }
        }

        while (!stack.isEmpty()) {
            char operator = stack.pop();
            if (operator == '(' || operator == ')') {
                throw new IllegalArgumentException("Parentheses are not balanced.");
            }
            output.append(operator);
        }

        return output.toString();
    }

    private static boolean beginsExpression(char token) {
        return isOperand(token) || token == '(';
    }

    private static boolean endsExpression(char token) {
        return isOperand(token) || token == ')' || isUnaryOperator(token);
    }

    private static boolean isLiteral(char token) {
        return token != '('
                && token != ')'
                && token != '|'
                && token != CONCAT
                && !isUnaryOperator(token);
    }

    private static boolean isUnaryOperator(char token) {
        return token == '*' || token == '+' || token == '?';
    }

    private static boolean isBinaryOperator(char token) {
        return token == '|' || token == CONCAT;
    }

    private static int precedence(char operator) {
        if (operator == CONCAT) {
            return 2;
        }
        if (operator == '|') {
            return 1;
        }
        return 0;
    }
}
