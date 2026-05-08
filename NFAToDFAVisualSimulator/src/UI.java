import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class UI {
    private final Scanner scanner;
    private NFA nfa;
    private DFA dfa;

    public UI() {
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printHeader();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createNfa();
                    break;
                case "2":
                    showNfa();
                    break;
                case "3":
                    convertToDfa();
                    break;
                case "4":
                    showDfa();
                    break;
                case "5":
                    testString();
                    break;
                case "6":
                    loadProfessorDemoNfa();
                    break;
                case "0":
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid menu choice.");
            }
        }
    }

    private void printHeader() {
        System.out.println("=================================");
        System.out.println(" NFA to DFA Visual Simulator ");
        System.out.println("=================================");
        System.out.println("This project lets you manually enter an NFA and convert it to a DFA.");
        System.out.println("Use eps, epsilon, or # for epsilon transitions.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Menu");
        System.out.println("1. Enter a new NFA");
        System.out.println("2. Display NFA transition table");
        System.out.println("3. Convert NFA to DFA");
        System.out.println("4. Display DFA transition table");
        System.out.println("5. Test input string on DFA");
        System.out.println("6. Load professor demo NFA");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    private void createNfa() {
        NFA newNfa = new NFA();

        readStates(newNfa);
        readAlphabet(newNfa);
        readStartState(newNfa);
        readFinalStates(newNfa);
        readTransitions(newNfa);

        nfa = newNfa;
        dfa = null;

        System.out.println("NFA saved successfully.");
    }

    private void readStates(NFA targetNfa) {
        while (true) {
            System.out.print("Enter states separated by commas (example: q0,q1,q2): ");
            List<String> states = splitCommaList(scanner.nextLine());

            if (states.isEmpty()) {
                System.out.println("Please enter at least one state.");
                continue;
            }

            Set<String> seen = new LinkedHashSet<String>();
            boolean valid = true;
            for (String state : states) {
                if (!seen.add(state)) {
                    System.out.println("Duplicate state: " + state);
                    valid = false;
                    break;
                }
            }

            if (!valid) {
                continue;
            }

            for (String state : states) {
                targetNfa.addState(state);
            }
            return;
        }
    }

    private void readAlphabet(NFA targetNfa) {
        while (true) {
            System.out.print("Enter alphabet symbols separated by commas (example: a,b): ");
            List<String> symbols = splitCommaList(scanner.nextLine());

            if (symbols.isEmpty()) {
                System.out.println("Please enter at least one alphabet symbol.");
                continue;
            }

            try {
                Set<Character> seen = new LinkedHashSet<Character>();
                for (String symbolText : symbols) {
                    char symbol = parseAlphabetSymbol(symbolText);
                    if (!seen.add(symbol)) {
                        throw new IllegalArgumentException("Duplicate symbol: " + symbol);
                    }
                }

                for (Character symbol : seen) {
                    targetNfa.addSymbol(symbol);
                }
                return;
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    private void readStartState(NFA targetNfa) {
        while (true) {
            System.out.print("Enter start state: ");
            String startState = scanner.nextLine().trim();

            if (!targetNfa.hasState(startState)) {
                System.out.println("Unknown state: " + startState);
                continue;
            }

            targetNfa.setStartState(startState);
            return;
        }
    }

    private void readFinalStates(NFA targetNfa) {
        while (true) {
            System.out.print("Enter final states separated by commas (example: q2): ");
            List<String> finals = splitCommaList(scanner.nextLine());

            if (finals.isEmpty()) {
                System.out.println("Please enter at least one final state.");
                continue;
            }

            boolean valid = true;
            for (String state : finals) {
                if (!targetNfa.hasState(state)) {
                    System.out.println("Unknown state: " + state);
                    valid = false;
                    break;
                }
            }

            if (!valid) {
                continue;
            }

            for (String state : finals) {
                targetNfa.addFinalState(state);
            }
            return;
        }
    }

    private void readTransitions(NFA targetNfa) {
        System.out.println();
        System.out.println("Enter transitions in this form: from symbol to1,to2");
        System.out.println("Example: q0 a q0,q1");
        System.out.println("Example epsilon transition: q0 eps q2");
        System.out.println("Type done when finished.");

        while (true) {
            System.out.print("Transition: ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("done")) {
                return;
            }

            try {
                String[] parts = line.split("\\s+");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Use exactly: from symbol to1,to2");
                }

                String from = parts[0];
                char symbol = parseTransitionSymbol(parts[1]);
                List<String> targets = splitCommaList(parts[2]);

                validateTransition(targetNfa, from, symbol, targets);

                for (String target : targets) {
                    targetNfa.addTransition(from, symbol, target);
                }
                System.out.println("Transition added.");
            } catch (IllegalArgumentException exception) {
                System.out.println("Invalid transition: " + exception.getMessage());
            }
        }
    }

    private void validateTransition(NFA targetNfa, String from, char symbol, List<String> targets) {
        if (!targetNfa.hasState(from)) {
            throw new IllegalArgumentException("Unknown from-state: " + from);
        }

        if (symbol != State.EPSILON && !targetNfa.containsSymbol(symbol)) {
            throw new IllegalArgumentException("Symbol is not in alphabet: " + symbol);
        }

        if (targets.isEmpty()) {
            throw new IllegalArgumentException("At least one target state is required.");
        }

        for (String target : targets) {
            if (!targetNfa.hasState(target)) {
                throw new IllegalArgumentException("Unknown target state: " + target);
            }
        }
    }

    private void showNfa() {
        if (!hasNfa()) {
            return;
        }
        System.out.println();
        System.out.println(nfa.describe());
    }

    private void convertToDfa() {
        if (!hasNfa()) {
            return;
        }

        dfa = new SubsetConstruction().convert(nfa);
        System.out.println("Conversion complete.");
    }

    private void showDfa() {
        if (!hasDfa()) {
            return;
        }
        System.out.println();
        System.out.println(dfa.describe());
    }

    private void testString() {
        if (!hasDfa()) {
            return;
        }

        System.out.print("Enter input string (press Enter for empty string): ");
        String input = scanner.nextLine();

        StringTester tester = new StringTester(dfa);
        StringTester.TestResult result = tester.test(input);

        System.out.println("Trace: " + result.getTrace());
        System.out.println(result.getMessage());
        System.out.println("Result: " + (result.isAccepted() ? "ACCEPTED" : "REJECTED"));
    }

    private void loadProfessorDemoNfa() {
        NFA demoNfa = new NFA();
        demoNfa.addState("q0");
        demoNfa.addState("q1");
        demoNfa.addState("q2");
        demoNfa.addSymbol('a');
        demoNfa.addSymbol('b');
        demoNfa.setStartState("q0");
        demoNfa.addFinalState("q2");

        // This NFA guesses which 'a' is the start of the final "ab" suffix.
        demoNfa.addTransition("q0", 'a', "q0");
        demoNfa.addTransition("q0", 'a', "q1");
        demoNfa.addTransition("q0", 'b', "q0");
        demoNfa.addTransition("q1", 'b', "q2");

        nfa = demoNfa;
        dfa = new SubsetConstruction().convert(nfa);

        System.out.println();
        System.out.println("Professor demo NFA loaded.");
        System.out.println("Language: all strings over {a,b} that end with ab");
        System.out.println();
        System.out.println(nfa.describe());
        System.out.println(dfa.describe());

        StringTester tester = new StringTester(dfa);
        System.out.println("Expected accepted strings:");
        printDemoResults(tester, new String[]{"ab", "aab", "babab"});
        System.out.println("Expected rejected strings:");
        printDemoResults(tester, new String[]{"a", "aba", "bbb"});
        System.out.println("The demo DFA is now loaded. You can still choose option 5 to test your own strings.");
    }

    private void printDemoResults(StringTester tester, String[] examples) {
        for (String example : examples) {
            StringTester.TestResult result = tester.test(example);
            System.out.println("  " + printableInput(example) + " -> "
                    + (result.isAccepted() ? "ACCEPTED" : "REJECTED"));
        }
    }

    private String printableInput(String input) {
        if (input == null || input.isEmpty()) {
            return "empty string";
        }
        return input;
    }

    private boolean hasNfa() {
        if (nfa == null) {
            System.out.println("Please enter an NFA first.");
            return false;
        }
        return true;
    }

    private boolean hasDfa() {
        if (dfa == null) {
            System.out.println("Please convert the NFA to a DFA first.");
            return false;
        }
        return true;
    }

    private List<String> splitCommaList(String text) {
        List<String> result = new ArrayList<String>();
        if (text == null || text.trim().isEmpty()) {
            return result;
        }

        String[] parts = text.split(",");
        for (String part : parts) {
            String cleanPart = part.trim();
            if (!cleanPart.isEmpty()) {
                result.add(cleanPart);
            }
        }
        return result;
    }

    private char parseAlphabetSymbol(String text) {
        if (text == null || text.trim().length() != 1) {
            throw new IllegalArgumentException("Alphabet symbols must be one character each.");
        }

        char symbol = text.trim().charAt(0);
        if (symbol == '#') {
            throw new IllegalArgumentException("Do not place epsilon in the alphabet. Use it only in transitions.");
        }
        return symbol;
    }

    private char parseTransitionSymbol(String text) {
        String cleanText = text.trim();
        if (cleanText.equalsIgnoreCase("eps")
                || cleanText.equalsIgnoreCase("epsilon")
                || cleanText.equals("#")) {
            return State.EPSILON;
        }

        if (cleanText.length() != 1) {
            throw new IllegalArgumentException("Transition symbol must be one character or eps.");
        }

        return cleanText.charAt(0);
    }
}
