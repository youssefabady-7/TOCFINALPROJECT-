import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TransitionTablePrinter {
    private static final int FIRST_COLUMN_WIDTH = 16;
    private static final int COLUMN_WIDTH = 16;

    public static String printNfaTable(NFA nfa) {
        List<Character> symbols = new ArrayList<Character>(nfa.getAlphabet());
        if (hasEpsilonTransition(nfa)) {
            symbols.add(State.EPSILON);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("NFA transition table:").append(System.lineSeparator());
        appendHeader(builder, symbols);

        for (State state : nfa.getStates()) {
            builder.append(String.format("%-" + FIRST_COLUMN_WIDTH + "s", markNfaState(nfa, state)));
            for (Character symbol : symbols) {
                builder.append(String.format("%-" + COLUMN_WIDTH + "s",
                        joinStateNames(state.getTransitions(symbol))));
            }
            builder.append(System.lineSeparator());
        }

        builder.append(System.lineSeparator());
        builder.append("Legend: -> start state, * final state").append(System.lineSeparator());
        return builder.toString();
    }

    public static String printDfaTable(DFA dfa) {
        List<Character> symbols = new ArrayList<Character>(dfa.getAlphabet());

        StringBuilder builder = new StringBuilder();
        builder.append("DFA transition table:").append(System.lineSeparator());
        appendHeader(builder, symbols);

        for (DFAState state : dfa.getStates()) {
            builder.append(String.format("%-" + FIRST_COLUMN_WIDTH + "s", markDfaState(dfa, state)));
            for (Character symbol : symbols) {
                DFAState target = state.getTransition(symbol);
                builder.append(String.format("%-" + COLUMN_WIDTH + "s",
                        target == null ? "-" : target.getName()));
            }
            builder.append(System.lineSeparator());
        }

        builder.append(System.lineSeparator());
        builder.append("Legend: -> start state, * final state").append(System.lineSeparator());
        return builder.toString();
    }

    private static void appendHeader(StringBuilder builder, List<Character> symbols) {
        builder.append(String.format("%-" + FIRST_COLUMN_WIDTH + "s", "State"));
        for (Character symbol : symbols) {
            builder.append(String.format("%-" + COLUMN_WIDTH + "s", State.symbolToString(symbol)));
        }
        builder.append(System.lineSeparator());

        int width = FIRST_COLUMN_WIDTH + (symbols.size() * COLUMN_WIDTH);
        for (int i = 0; i < width; i++) {
            builder.append("-");
        }
        builder.append(System.lineSeparator());
    }

    private static String markNfaState(NFA nfa, State state) {
        StringBuilder builder = new StringBuilder();
        if (state.equals(nfa.getStartState())) {
            builder.append("->");
        } else {
            builder.append("  ");
        }

        if (nfa.getFinalStates().contains(state)) {
            builder.append("*");
        } else {
            builder.append(" ");
        }

        builder.append(state.getName());
        return builder.toString();
    }

    private static String markDfaState(DFA dfa, DFAState state) {
        StringBuilder builder = new StringBuilder();
        if (state.equals(dfa.getStartState())) {
            builder.append("->");
        } else {
            builder.append("  ");
        }

        if (dfa.getFinalStates().contains(state)) {
            builder.append("*");
        } else {
            builder.append(" ");
        }

        builder.append(state.getName());
        return builder.toString();
    }

    private static boolean hasEpsilonTransition(NFA nfa) {
        for (State state : nfa.getStates()) {
            if (!state.getTransitions(State.EPSILON).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static String joinStateNames(Collection<State> states) {
        if (states.isEmpty()) {
            return "-";
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (State state : states) {
            if (!first) {
                builder.append(",");
            }
            builder.append(state.getName());
            first = false;
        }
        return builder.toString();
    }
}
