import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class State implements Comparable<State> {
    public static final char EPSILON = '\0';

    private static int nextId = 0;

    private final int id;
    private final Map<Character, Set<State>> transitions;

    public State() {
        this.id = nextId++;
        this.transitions = new LinkedHashMap<Character, Set<State>>();
    }

    public static void resetCounter() {
        nextId = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return "q" + id;
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
        return Integer.compare(this.id, other.id);
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
