import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class NFA {
    private final State startState;
    private final Set<State> finalStates;
    private final Set<State> states;
    private final Set<Character> alphabet;

    public NFA(State startState, Set<State> finalStates, Set<State> states, Set<Character> alphabet) {
        this.startState = startState;
        this.finalStates = new TreeSet<State>(finalStates);
        this.states = new TreeSet<State>(states);
        this.alphabet = new TreeSet<Character>(alphabet);
    }

    public State getStartState() {
        return startState;
    }

    public Set<State> getFinalStates() {
        return finalStates;
    }

    public Set<State> getStates() {
        return states;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public String describe() {
        StringBuilder builder = new StringBuilder();

        builder.append("States: ").append(joinStateNames(states)).append(System.lineSeparator());
        builder.append("Alphabet: ").append(joinSymbols(alphabet)).append(System.lineSeparator());
        builder.append("Start state: ").append(startState.getName()).append(System.lineSeparator());
        builder.append("Final states: ").append(joinStateNames(finalStates)).append(System.lineSeparator());
        builder.append(System.lineSeparator()).append("Transitions:").append(System.lineSeparator());

        boolean hasTransition = false;
        builder.append(String.format("%-12s %-10s %-20s%n", "From", "Symbol", "To"));
        builder.append("---------------------------------------------").append(System.lineSeparator());

        for (State state : states) {
            List<Character> symbols = new ArrayList<Character>(state.getAllTransitions().keySet());
            symbols.sort(new Comparator<Character>() {
                @Override
                public int compare(Character first, Character second) {
                    if (first == State.EPSILON && second != State.EPSILON) {
                        return -1;
                    }
                    if (second == State.EPSILON && first != State.EPSILON) {
                        return 1;
                    }
                    return Character.compare(first, second);
                }
            });

            for (Character symbol : symbols) {
                hasTransition = true;
                builder.append(String.format(
                        "%-12s %-10s %-20s%n",
                        state.getName(),
                        State.symbolToString(symbol),
                        joinStateNames(state.getTransitions(symbol))));
            }
        }

        if (!hasTransition) {
            builder.append("No transitions.").append(System.lineSeparator());
        }

        return builder.toString();
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
