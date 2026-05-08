import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class StateSetComparator implements Comparator<Set<State>> {
    @Override
    public int compare(Set<State> first, Set<State> second) {
        if (first == second) {
            return 0;
        }

        Iterator<State> firstIterator = first.iterator();
        Iterator<State> secondIterator = second.iterator();

        while (firstIterator.hasNext() && secondIterator.hasNext()) {
            int comparison = firstIterator.next().compareTo(secondIterator.next());
            if (comparison != 0) {
                return comparison;
            }
        }

        return Integer.compare(first.size(), second.size());
    }
}
