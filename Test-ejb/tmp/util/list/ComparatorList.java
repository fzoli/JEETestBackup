package util.list;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author zoli
 * @param <T>
 */
public class ComparatorList<T> extends BundleList<T> {

    private final Comparator<T> COMPARATOR;
    
    public ComparatorList(List<T> list, Comparator<T> comparator) {
        super(list);
        COMPARATOR = comparator;
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ListComparatorIterator<>(getList(), COMPARATOR);
    }
    
}
