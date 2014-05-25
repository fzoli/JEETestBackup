package util.list;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 *
 * @author zoli
 * @param <T>
 */
public class BundleList<T> implements List<T> {

    private final List<T> LIST;

    protected BundleList() {
        LIST = null;
    }

    public BundleList(List<T> list) {
        LIST = list;
    }

    protected List<T> getList() {
        return LIST;
    }
    
    @Override
    public int size() {
        return getList().size();
    }

    @Override
    public boolean isEmpty() {
        return getList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getList().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return getList().iterator();
    }

    @Override
    public Object[] toArray() {
        return getList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return getList().toArray(a);
    }

    @Override
    public boolean add(T e) {
        return getList().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return getList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return getList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return getList().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getList().retainAll(c);
    }

    @Override
    public void clear() {
        getList().clear();
    }

    @Override
    public T get(int index) {
        return getList().get(index);
    }

    @Override
    public T set(int index, T element) {
        return getList().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        getList().add(index, element);
    }

    @Override
    public T remove(int index) {
        return getList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getList().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getList().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return getList().listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new BundleList<>(getList().subList(fromIndex, toIndex));
    }

    @Override
    public boolean equals(Object obj) {
        return getList().equals(obj);
    }

    @Override
    public int hashCode() {
        return getList().hashCode();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        LIST.forEach(action);
    }

    @Override
    public Stream<T> parallelStream() {
        return LIST.parallelStream();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return LIST.removeIf(filter);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        LIST.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        LIST.sort(c);
    }

    @Override
    public Spliterator<T> spliterator() {
        return LIST.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return LIST.stream();
    }

    @Override
    public String toString() {
        return LIST.toString();
    }
    
}
