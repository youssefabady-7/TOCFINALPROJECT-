import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class NFA {
    private final Map<String, State> states;
    private final Set<Character> alphabet;
    private final Set<State> finalStates;
    private State startState;

    public NFA() {
        this.states = new TreeMap<String, State>();
        this.alphabet = new TreeSet<Character>();
        this.finalStates = new TreeSet<State>();
    }

    public void addState(String name) {
        String cleanName = cleanName(name);
        if (states.containsKey(cleanName)) {
            throw new IllegalArgumentException("State already exists: " + cleanName);
        }
        states.put(cleanName, new State(cleanName));
    }

    public void addSymbol(char symbol) {
        if (symbol == State.EPSILON) {
            throw new IllegalArgumentException("Epsilon is not part of the alphabet.");
        }
        alphabet.add(symbol);
    }

    public void setStartState(String name) {
        startState = getStateOrThrow(name);
    }

    public void addFinalState(String name) {
        finalStates.add(getStateOrThrow(name));
    }

    public void addTransition(String fromName, char symbol, String toName) {
        State from = getStateOrThrow(fromName);
        State to = getStateOrThrow(toName);

        if (symbol != State.EPSILON && !alphabet.contains(symbol)) {
            throw new IllegalArgumentException("Symbol is not in alphabet: " + symbol);
        }

        from.addTransition(symbol, to);
    }

    public boolean hasState(String name) {
        return states.containsKey(cleanName(name));
    }

    public boolean containsSymbol(char symbol) {
        return alphabet.contains(symbol);
    }

    public State getState(String name) {
        return states.get(cleanName(name));
    }

    public Collection<State> getStates() {
        return states.values();
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public State getStartState() {
        return startState;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    public String describe() {
        StringBuilder builder = new StringBuilder();
        builder.append("States: ").append(joinStateNames(getStates())).append(System.lineSeparator());
        builder.append("Alphabet: ").append(joinSymbols(alphabet)).append(System.lineSeparator());
        builder.append("Start state: ").append(startState == null ? "-" : startState.getName()).append(System.lineSeparator());
        builder.append("Final states: ").append(joinStateNames(finalStates)).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(TransitionTablePrinter.printNfaTable(this));
        return builder.toString();
    }

    private State getStateOrThrow(String name) {
        State state = states.get(cleanName(name));
        if (state == null) {
            throw new IllegalArgumentException("Unknown state: " + name);
        }
        return state;
    }

    private String cleanName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("State name cannot be empty.");
        }
        return name.trim();
    }

    private String joinStateNames(Collection<State> stateCollection) {
        if (stateCollection.isEmpty()) {
            return "{}";
        }

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (State state : stateCollection) {
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
