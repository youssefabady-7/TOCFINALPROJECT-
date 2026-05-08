import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DFAMinimizer {
    public DFA minimize(DFA dfa) {
        Set<Character> alphabet = dfa.getAlphabet();
        DFAState trapState = createTrapStateIfNeeded(dfa);

        Set<DFAState> completeStates = new TreeSet<DFAState>(dfa.getStates());
        if (trapState != null) {
            completeStates.add(trapState);
        }

        List<Set<DFAState>> partitions = createInitialPartitions(completeStates);
        boolean changed;

        do {
            changed = false;
            List<Set<DFAState>> newPartitions = new ArrayList<Set<DFAState>>();

            for (Set<DFAState> block : partitions) {
                Map<String, Set<DFAState>> groups = new LinkedHashMap<String, Set<DFAState>>();

                for (DFAState state : block) {
                    String signature = createSignature(state, alphabet, partitions, trapState);
                    groups.computeIfAbsent(signature, key -> new LinkedHashSet<DFAState>()).add(state);
                }

                newPartitions.addAll(groups.values());
                if (groups.size() > 1) {
                    changed = true;
                }
            }

            partitions = newPartitions;
        } while (changed);

        return buildMinimizedDFA(dfa, alphabet, partitions, trapState);
    }

    private DFAState createTrapStateIfNeeded(DFA dfa) {
        for (DFAState state : dfa.getStates()) {
            for (Character symbol : dfa.getAlphabet()) {
                if (state.getTransition(symbol) == null) {
                    DFAState trapState = new DFAState(Collections.<State>emptySet(), false);
                    for (Character trapSymbol : dfa.getAlphabet()) {
                        trapState.addTransition(trapSymbol, trapState);
                    }
                    return trapState;
                }
            }
        }
        return null;
    }

    private List<Set<DFAState>> createInitialPartitions(Set<DFAState> states) {
        Set<DFAState> finals = new LinkedHashSet<DFAState>();
        Set<DFAState> nonFinals = new LinkedHashSet<DFAState>();

        for (DFAState state : states) {
            if (state.isFinalState()) {
                finals.add(state);
            } else {
                nonFinals.add(state);
            }
        }

        List<Set<DFAState>> partitions = new ArrayList<Set<DFAState>>();
        if (!finals.isEmpty()) {
            partitions.add(finals);
        }
        if (!nonFinals.isEmpty()) {
            partitions.add(nonFinals);
        }

        return partitions;
    }

    private String createSignature(
            DFAState state,
            Set<Character> alphabet,
            List<Set<DFAState>> partitions,
            DFAState trapState) {

        StringBuilder builder = new StringBuilder();

        for (Character symbol : alphabet) {
            DFAState target = transitionOf(state, symbol, trapState);
            builder.append(findPartitionIndex(target, partitions)).append('|');
        }

        return builder.toString();
    }

    private DFA buildMinimizedDFA(
            DFA original,
            Set<Character> alphabet,
            List<Set<DFAState>> partitions,
            DFAState trapState) {

        DFAState.resetCounter();
        List<Set<DFAState>> displayPartitions = orderPartitionsWithStartFirst(partitions, original.getStartState());

        Map<Set<DFAState>, DFAState> newStatesByBlock = new LinkedHashMap<Set<DFAState>, DFAState>();

        for (Set<DFAState> block : displayPartitions) {
            boolean finalBlock = containsFinalState(block);
            DFAState newState = new DFAState(Collections.<State>emptySet(), finalBlock);
            newStatesByBlock.put(block, newState);
        }

        for (Set<DFAState> block : displayPartitions) {
            DFAState representative = block.iterator().next();
            DFAState newState = newStatesByBlock.get(block);

            for (Character symbol : alphabet) {
                DFAState oldTarget = transitionOf(representative, symbol, trapState);
                Set<DFAState> targetBlock = findPartition(oldTarget, displayPartitions);
                newState.addTransition(symbol, newStatesByBlock.get(targetBlock));
            }
        }

        Set<DFAState> minimizedStates = new TreeSet<DFAState>(newStatesByBlock.values());
        Set<DFAState> startBlock = findPartition(original.getStartState(), displayPartitions);
        DFAState minimizedStart = newStatesByBlock.get(startBlock);

        return new DFA(minimizedStart, minimizedStates, alphabet);
    }

    private List<Set<DFAState>> orderPartitionsWithStartFirst(
            List<Set<DFAState>> partitions,
            DFAState startState) {

        List<Set<DFAState>> ordered = new ArrayList<Set<DFAState>>();
        Set<DFAState> startPartition = findPartition(startState, partitions);
        ordered.add(startPartition);

        for (Set<DFAState> partition : partitions) {
            if (partition != startPartition) {
                ordered.add(partition);
            }
        }

        return ordered;
    }

    private boolean containsFinalState(Set<DFAState> block) {
        for (DFAState state : block) {
            if (state.isFinalState()) {
                return true;
            }
        }
        return false;
    }

    private DFAState transitionOf(DFAState state, char symbol, DFAState trapState) {
        if (state == trapState) {
            return trapState;
        }

        DFAState target = state.getTransition(symbol);
        if (target == null && trapState != null) {
            return trapState;
        }
        return target;
    }

    private int findPartitionIndex(DFAState state, List<Set<DFAState>> partitions) {
        for (int i = 0; i < partitions.size(); i++) {
            if (partitions.get(i).contains(state)) {
                return i;
            }
        }
        return -1;
    }

    private Set<DFAState> findPartition(DFAState state, List<Set<DFAState>> partitions) {
        for (Set<DFAState> partition : partitions) {
            if (partition.contains(state)) {
                return partition;
            }
        }
        throw new IllegalStateException("Could not find partition for state " + state);
    }
}
