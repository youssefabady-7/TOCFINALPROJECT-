import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.TreeSet;

public class ThompsonConstruction {
    public NFA build(String postfixRegex, Set<Character> alphabet) {
        Deque<NFA> stack = new ArrayDeque<NFA>();

        for (int i = 0; i < postfixRegex.length(); i++) {
            char token = postfixRegex.charAt(i);

            if (RegexParser.isOperand(token)) {
                stack.push(createBasicNFA(token, alphabet));
            } else if (token == RegexParser.CONCAT) {
                requireStackSize(stack, 2, "concatenation");
                NFA right = stack.pop();
                NFA left = stack.pop();
                stack.push(concatenate(left, right));
            } else if (token == '|') {
                requireStackSize(stack, 2, "union");
                NFA right = stack.pop();
                NFA left = stack.pop();
                stack.push(union(left, right));
            } else if (token == '*') {
                requireStackSize(stack, 1, "Kleene star");
                stack.push(kleeneStar(stack.pop()));
            } else if (token == '+') {
                requireStackSize(stack, 1, "one-or-more");
                stack.push(oneOrMore(stack.pop()));
            } else if (token == '?') {
                requireStackSize(stack, 1, "optional");
                stack.push(optional(stack.pop()));
            } else {
                throw new IllegalArgumentException("Unsupported postfix token: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid regular expression.");
        }

        return stack.pop();
    }

    private NFA createBasicNFA(char symbol, Set<Character> alphabet) {
        State start = new State();
        State end = new State();
        start.addTransition(symbol, end);

        Set<State> states = new TreeSet<State>();
        states.add(start);
        states.add(end);

        Set<State> finals = new TreeSet<State>();
        finals.add(end);

        return new NFA(start, finals, states, alphabet);
    }

    private NFA concatenate(NFA left, NFA right) {
        for (State finalState : left.getFinalStates()) {
            finalState.addTransition(State.EPSILON, right.getStartState());
        }

        Set<State> states = mergeStates(left, right);
        return new NFA(left.getStartState(), right.getFinalStates(), states, left.getAlphabet());
    }

    private NFA union(NFA left, NFA right) {
        State start = new State();
        State end = new State();

        start.addTransition(State.EPSILON, left.getStartState());
        start.addTransition(State.EPSILON, right.getStartState());

        for (State finalState : left.getFinalStates()) {
            finalState.addTransition(State.EPSILON, end);
        }
        for (State finalState : right.getFinalStates()) {
            finalState.addTransition(State.EPSILON, end);
        }

        Set<State> states = mergeStates(left, right);
        states.add(start);
        states.add(end);

        Set<State> finals = new TreeSet<State>();
        finals.add(end);

        return new NFA(start, finals, states, left.getAlphabet());
    }

    private NFA kleeneStar(NFA nfa) {
        State start = new State();
        State end = new State();

        start.addTransition(State.EPSILON, nfa.getStartState());
        start.addTransition(State.EPSILON, end);

        for (State finalState : nfa.getFinalStates()) {
            finalState.addTransition(State.EPSILON, nfa.getStartState());
            finalState.addTransition(State.EPSILON, end);
        }

        Set<State> states = new TreeSet<State>(nfa.getStates());
        states.add(start);
        states.add(end);

        Set<State> finals = new TreeSet<State>();
        finals.add(end);

        return new NFA(start, finals, states, nfa.getAlphabet());
    }

    private NFA oneOrMore(NFA nfa) {
        State start = new State();
        State end = new State();

        start.addTransition(State.EPSILON, nfa.getStartState());

        for (State finalState : nfa.getFinalStates()) {
            finalState.addTransition(State.EPSILON, nfa.getStartState());
            finalState.addTransition(State.EPSILON, end);
        }

        Set<State> states = new TreeSet<State>(nfa.getStates());
        states.add(start);
        states.add(end);

        Set<State> finals = new TreeSet<State>();
        finals.add(end);

        return new NFA(start, finals, states, nfa.getAlphabet());
    }

    private NFA optional(NFA nfa) {
        State start = new State();
        State end = new State();

        start.addTransition(State.EPSILON, nfa.getStartState());
        start.addTransition(State.EPSILON, end);

        for (State finalState : nfa.getFinalStates()) {
            finalState.addTransition(State.EPSILON, end);
        }

        Set<State> states = new TreeSet<State>(nfa.getStates());
        states.add(start);
        states.add(end);

        Set<State> finals = new TreeSet<State>();
        finals.add(end);

        return new NFA(start, finals, states, nfa.getAlphabet());
    }

    private Set<State> mergeStates(NFA left, NFA right) {
        Set<State> states = new TreeSet<State>();
        states.addAll(left.getStates());
        states.addAll(right.getStates());
        return states;
    }

    private void requireStackSize(Deque<NFA> stack, int size, String operation) {
        if (stack.size() < size) {
            throw new IllegalArgumentException("Not enough operands for " + operation + ".");
        }
    }
}
