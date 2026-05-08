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
                finalStates.add(state);
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
        builder.append("Start state: ").append(startState.getName()).append(System.lineSeparator());
        builder.append("Final states: ").append(joinDfaNames(finalStates)).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append("DFA state meanings:").append(System.lineSeparator());
        for (DFAState state : states) {
            builder.append(state.getName()).append(" = ").append(state.getSetLabel()).append(System.lineSeparator());
        }
        builder.append(System.lineSeparator());
        builder.append(TransitionTablePrinter.printDfaTable(this));
        return builder.toString();
    }

    private String joinDfaNames(Set<DFAState> dfaStates) {
        if (dfaStates.isEmpty()) {
            return "{}";
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (DFAState state : dfaStates) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(state.getName());
            first = false;
        }
        return builder.toString();
    }
}
