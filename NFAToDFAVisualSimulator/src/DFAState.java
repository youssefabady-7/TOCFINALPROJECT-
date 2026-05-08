import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class DFAState implements Comparable<DFAState> {
    private static int nextId = 0;

    private final int id;
    private final Set<State> nfaStates;
    private final boolean finalState;
    private final Map<Character, DFAState> transitions;

    public DFAState(Set<State> nfaStates, boolean finalState) {
        this.id = nextId++;
        this.nfaStates = new TreeSet<State>(nfaStates);
        this.finalState = finalState;
        this.transitions = new LinkedHashMap<Character, DFAState>();
    }

    public static void resetCounter() {
        nextId = 0;
    }

    public String getName() {
        return "D" + id;
    }

    public Set<State> getNfaStates() {
        return Collections.unmodifiableSet(nfaStates);
    }

    public boolean isFinalState() {
        return finalState;
    }

    public void addTransition(char symbol, DFAState target) {
        transitions.put(symbol, target);
    }

    public DFAState getTransition(char symbol) {
        return transitions.get(symbol);
    }

    public String getSetLabel() {
        if (nfaStates.isEmpty()) {
            return "{}";
        }

        StringBuilder builder = new StringBuilder("{");
        boolean first = true;
        for (State state : nfaStates) {
            if (!first) {
                builder.append(", ");
            }
            builder.append(state.getName());
            first = false;
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public int compareTo(DFAState other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DFAState)) {
            return false;
        }
        DFAState state = (DFAState) object;
        return id == state.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getName();
    }
}
