import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class DFA {
    private final DFAState startState;
    private final Set<DFAState> states;
    private final Set<DFAState> finalStates;
    private final Set<Character> alphabet;

    public DFA(DFAState startState, Set<DFAState> states, Set<Character> alphabet) {
        this.startState = startState;
        this.states = new TreeSet<DFAState>(states);
        this.alphabet = new TreeSet<Character>(alphabet);
        this.finalStates = new TreeSet<DFAState>();

        for (DFAState state : states) {
            if (state.isFinalState()) {
                this.finalStates.add(state);
            }
        }
    }

    public DFAState getStartState() {
        return startState;
    }

    public Set<DFAState> getStates() {
        return states;
    }

    public Set<DFAState> getFinalStates() {
        return finalStates;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public String describe() {
        StringBuilder builder = new StringBuilder();

        builder.append("States: ").append(joinDfaStateNames(states)).append(System.lineSeparator());
        builder.append("Alphabet: ").append(joinSymbols(alphabet)).append(System.lineSeparator());
        builder.append("Start state: ").append(startState.getName()).append(System.lineSeparator());
        builder.append("Final states: ").append(joinDfaStateNames(finalStates)).append(System.lineSeparator());

        if (hasNfaSetLabels()) {
            builder.append(System.lineSeparator()).append("DFA state meanings:").append(System.lineSeparator());
            for (DFAState state : states) {
                builder.append(state.getName())
                        .append(" = ")
                        .append(state.getNfaStateSetLabel())
                        .append(System.lineSeparator());
            }
        }

        builder.append(System.lineSeparator()).append("Transition table:").append(System.lineSeparator());
        builder.append(createTransitionTable());

        return builder.toString();
    }

    private boolean hasNfaSetLabels() {
        for (DFAState state : states) {
            if (!state.getNfaStates().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private String createTransitionTable() {
        StringBuilder builder = new StringBuilder();
        int firstColumnWidth = 14;
        int columnWidth = 12;

        builder.append(String.format("%-" + firstColumnWidth + "s", "State"));
        for (Character symbol : alphabet) {
            builder.append(String.format("%-" + columnWidth + "s", State.symbolToString(symbol)));
        }
        builder.append(System.lineSeparator());

        int lineLength = firstColumnWidth + (alphabet.size() * columnWidth);
        for (int i = 0; i < lineLength; i++) {
            builder.append("-");
        }
        builder.append(System.lineSeparator());

        for (DFAState state : states) {
            builder.append(String.format("%-" + firstColumnWidth + "s", markedName(state)));
            for (Character symbol : alphabet) {
                DFAState target = state.getTransition(symbol);
                builder.append(String.format("%-" + columnWidth + "s", target == null ? "-" : target.getName()));
            }
            builder.append(System.lineSeparator());
        }

        builder.append(System.lineSeparator())
                .append("Legend: -> start state, * final state")
                .append(System.lineSeparator());

        return builder.toString();
    }

    private String markedName(DFAState state) {
        StringBuilder builder = new StringBuilder();
        if (state.equals(startState)) {
            builder.append("->");
        } else {
            builder.append("  ");
        }

        if (finalStates.contains(state)) {
            builder.append("*");
        } else {
            builder.append(" ");
        }

        builder.append(state.getName());
        return builder.toString();
    }

    private String joinDfaStateNames(Collection<DFAState> stateCollection) {
        if (stateCollection.isEmpty()) {
            return "{}";
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (DFAState state : stateCollection) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(state.getName());
            first = false;
        }
        return builder.toString();
    }

    private String joinSymbols(Collection<Character> symbols) {
        if (symbols.isEmpty()) {
            return "{}";
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Character symbol : symbols) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(State.symbolToString(symbol));
            first = false;
        }
        return builder.toString();
    }
}
