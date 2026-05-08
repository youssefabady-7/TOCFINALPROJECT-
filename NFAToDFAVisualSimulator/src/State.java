import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class State implements Comparable<State> {
    public static final char EPSILON = '\0';

    private final String name;
    private final Map<Character, Set<State>> transitions;

    public State(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("State name cannot be empty.");
        }
        this.name = name.trim();
        this.transitions = new LinkedHashMap<Character, Set<State>>();
    }

    public String getName() {
        return name;
    }

    public void addTransition(char symbol, State target) {
        transitions.computeIfAbsent(symbol, key -> new TreeSet<State>()).add(target);
    }

    public Set<State> getTransitions(char symbol) {
        Set<State> result = transitions.get(symbol);
        if (result == null) {
            return Collections.emptySet();
        }
        return result;
    }

    public Map<Character, Set<State>> getAllTransitions() {
        return transitions;
    }

    public static String symbolToString(char symbol) {
        if (symbol == EPSILON) {
            return "eps";
        }
        return Character.toString(symbol);
    }

    @Override
    public int compareTo(State other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof State)) {
            return false;
        }
        State state = (State) object;
        return name.equals(state.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
