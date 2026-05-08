public class StringTester {
    private final DFA dfa;

    public StringTester(DFA dfa) {
        this.dfa = dfa;
    }

    public TestResult test(String input) {
        if (input == null) {
            input = "";
        }

        DFAState current = dfa.getStartState();
        StringBuilder trace = new StringBuilder(current.getName());

        for (int i = 0; i < input.length(); i++) {
            char symbol = input.charAt(i);

            if (!dfa.getAlphabet().contains(symbol)) {
                return new TestResult(false, trace.toString(),
                        "Symbol '" + symbol + "' is not in the DFA alphabet.");
            }

            DFAState next = current.getTransition(symbol);
            if (next == null) {
                return new TestResult(false, trace.toString(),
                        "No transition from " + current.getName() + " on '" + symbol + "'.");
            }

            trace.append(" --").append(State.symbolToString(symbol)).append("--> ").append(next.getName());
            current = next;
        }

        boolean accepted = dfa.getFinalStates().contains(current);
        String message = accepted
                ? "Input ended in final state " + current.getName() + "."
                : "Input ended in non-final state " + current.getName() + ".";

        return new TestResult(accepted, trace.toString(), message);
    }

    public static class TestResult {
        private final boolean accepted;
        private final String trace;
        private final String message;

        public TestResult(boolean accepted, String trace, String message) {
            this.accepted = accepted;
            this.trace = trace;
            this.message = message;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public String getTrace() {
            return trace;
        }

        public String getMessage() {
            return message;
        }
    }
}
