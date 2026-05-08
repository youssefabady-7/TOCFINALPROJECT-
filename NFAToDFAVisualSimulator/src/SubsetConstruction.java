import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SubsetConstruction {
    public DFA convert(NFA nfa) {
        if (nfa.getStartState() == null) {
            throw new IllegalArgumentException("NFA must have a start state.");
        }

        DFAState.resetCounter();

        Set<State> startClosure = epsilonClosure(Collections.singleton(nfa.getStartState()));
        Map<Set<State>, DFAState> createdStates = new LinkedHashMap<Set<State>, DFAState>();
        Deque<Set<State>> queue = new ArrayDeque<Set<State>>();
        Set<DFAState> dfaStates = new TreeSet<DFAState>();

        DFAState startDfaState = new DFAState(startClosure, containsFinalState(startClosure, nfa.getFinalStates()));
        createdStates.put(startClosure, startDfaState);
        queue.add(startClosure);
        dfaStates.add(startDfaState);

        while (!queue.isEmpty()) {
            Set<State> currentSet = queue.remove();
            DFAState currentDfaState = createdStates.get(currentSet);

            for (Character symbol : nfa.getAlphabet()) {
                Set<State> targetSet = epsilonClosure(move(currentSet, symbol));
                DFAState targetDfaState = createdStates.get(targetSet);

                if (targetDfaState == null) {
                    targetDfaState = new DFAState(targetSet, containsFinalState(targetSet, nfa.getFinalStates()));
                    createdStates.put(targetSet, targetDfaState);
                    queue.add(targetSet);
                    dfaStates.add(targetDfaState);
                }

                currentDfaState.addTransition(symbol, targetDfaState);
            }
        }

        return new DFA(startDfaState, dfaStates, nfa.getAlphabet());
    }

    private Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new TreeSet<State>(states);
        Deque<State> stack = new ArrayDeque<State>(states);

        while (!stack.isEmpty()) {
            State state = stack.pop();
            for (State target : state.getTransitions(State.EPSILON)) {
                if (!closure.contains(target)) {
                    closure.add(target);
                    stack.push(target);
                }
            }
        }

        return closure;
    }

    private Set<State> move(Set<State> states, char symbol) {
        Set<State> result = new TreeSet<State>();

        for (State state : states) {
            result.addAll(state.getTransitions(symbol));
        }

        return result;
    }

    private boolean containsFinalState(Set<State> states, Set<State> finalStates) {
        for (State state : states) {
            if (finalStates.contains(state)) {
                return true;
            }
        }
        return false;
    }
}
