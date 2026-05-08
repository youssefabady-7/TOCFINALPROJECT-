import java.util.Scanner;

public class UI {
    private final Scanner scanner;

    private String originalRegex;
    private String postfixRegex;
    private RegexParser parser;
    private NFA nfa;
    private DFA dfa;
    private DFA minimizedDfa;

    public UI() {
        this.scanner = new Scanner(System.in);
        this.parser = new RegexParser();
    }

    public void start() {
        printHeader();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    readAndConvertRegex();
                    break;
                case "2":
                    showNfa();
                    break;
                case "3":
                    showDfa();
                    break;
                case "4":
                    showMinimizedDfa();
                    break;
                case "5":
                    testString();
                    break;
                case "6":
                    runProfessorDemoTestCase();
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
        System.out.println("==============================================");
        System.out.println(" Regular Expression to DFA Conversion Project ");
        System.out.println("==============================================");
        System.out.println("Supported regex operators:");
        System.out.println("  |  union");
        System.out.println("  *  zero or more");
        System.out.println("  +  one or more");
        System.out.println("  ?  optional");
        System.out.println("  () grouping");
        System.out.println("Concatenation is implicit, for example: ab or a(b|c)");
        System.out.println("Use # for epsilon.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Menu");
        System.out.println("1. Enter regular expression and convert");
        System.out.println("2. Display NFA");
        System.out.println("3. Display DFA");
        System.out.println("4. Display minimized DFA");
        System.out.println("5. Test input string using minimized DFA");
        System.out.println("6. Run professor demo test case");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    private void readAndConvertRegex() {
        System.out.print("Enter regular expression: ");
        String regex = scanner.nextLine();

        try {
            State.resetCounter();
            DFAState.resetCounter();

            parser = new RegexParser();
            postfixRegex = parser.toPostfix(regex);
            nfa = new ThompsonConstruction().build(postfixRegex, parser.getAlphabet());
            dfa = new SubsetConstruction().convert(nfa);
            minimizedDfa = new DFAMinimizer().minimize(dfa);
            originalRegex = regex;

            System.out.println();
            System.out.println("Regex accepted.");
            System.out.println("Original regex: " + originalRegex);
            System.out.println("Postfix regex:  " + parser.getFormattedPostfix());
            System.out.println("Conversion complete: Regex -> NFA -> DFA -> Minimized DFA");
        } catch (IllegalArgumentException exception) {
            clearCurrentAutomata();
            System.out.println("Invalid regular expression: " + exception.getMessage());
        }
    }

    private void showNfa() {
        if (!hasAutomata()) {
            return;
        }
        System.out.println();
        System.out.println("NFA built by Thompson's Construction");
        System.out.println(nfa.describe());
    }

    private void showDfa() {
        if (!hasAutomata()) {
            return;
        }
        System.out.println();
        System.out.println("DFA built by Subset Construction");
        System.out.println(dfa.describe());
    }

    private void showMinimizedDfa() {
        if (!hasAutomata()) {
            return;
        }
        System.out.println();
        System.out.println("Minimized DFA");
        System.out.println(minimizedDfa.describe());
    }

    private void testString() {
        if (!hasAutomata()) {
            return;
        }

        System.out.print("Enter input string (press Enter for empty string): ");
        String input = scanner.nextLine();

        StringTester tester = new StringTester(minimizedDfa);
        StringTester.TestResult result = tester.test(input);

        System.out.println("Trace: " + result.getTrace());
        System.out.println(result.getMessage());
        System.out.println("Result: " + (result.isAccepted() ? "ACCEPTED" : "REJECTED"));
    }

    private void runProfessorDemoTestCase() {
        String demoRegex = "(a|b)*abb";
        String[] acceptedExamples = {"abb", "aabb", "bababb"};
        String[] rejectedExamples = {"ab", "aba", "bbb"};

        System.out.println();
        System.out.println("Professor demo test case");
        System.out.println("Language: all strings over {a,b} that end with abb");
        System.out.println("Regular expression: " + demoRegex);

        try {
            State.resetCounter();
            DFAState.resetCounter();

            parser = new RegexParser();
            postfixRegex = parser.toPostfix(demoRegex);
            nfa = new ThompsonConstruction().build(postfixRegex, parser.getAlphabet());
            dfa = new SubsetConstruction().convert(nfa);
            minimizedDfa = new DFAMinimizer().minimize(dfa);
            originalRegex = demoRegex;

            System.out.println("Postfix regex: " + parser.getFormattedPostfix());
            System.out.println();
            System.out.println("Minimized DFA for the demo:");
            System.out.println(minimizedDfa.describe());

            StringTester tester = new StringTester(minimizedDfa);
            System.out.println("Expected accepted strings:");
            printDemoResults(tester, acceptedExamples);
            System.out.println("Expected rejected strings:");
            printDemoResults(tester, rejectedExamples);

            System.out.println("The demo DFA is now loaded. You can still choose option 5 to test your own strings.");
        } catch (IllegalArgumentException exception) {
            clearCurrentAutomata();
            System.out.println("Demo failed: " + exception.getMessage());
        }
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

    private boolean hasAutomata() {
        if (minimizedDfa == null) {
            System.out.println("Please enter and convert a regular expression first.");
            return false;
        }
        return true;
    }

    private void clearCurrentAutomata() {
        originalRegex = null;
        postfixRegex = null;
        nfa = null;
        dfa = null;
        minimizedDfa = null;
    }
}
