package util.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 *
 * @author zoli
 * @param <T>
 */
public class ListComparatorIterator<T> implements ListIterator<T> {

    private final List<T> ORDERED_LIST, CLONE_LIST, DEF_LIST;
    private final Comparator<T> COMPARATOR;
    
    private final int LAST_INDEX;
    
    private final T MIN, MAX;
    
    private int next = 0;

    private T obj;
    
    public ListComparatorIterator(List<T> list, Comparator<T> comparator) {
        DEF_LIST = list;
        COMPARATOR = comparator;
        if (list == null || list.isEmpty() || comparator == null) {
            ORDERED_LIST = CLONE_LIST = null;
            MIN = MAX = null;
            LAST_INDEX = -1;
        }
        else {
            LAST_INDEX = DEF_LIST.size() - 1;
            ORDERED_LIST = new ArrayList<>(list.size());
            CLONE_LIST = new ArrayList<>(list.size());
            int maxi = 0;
            int mini = 0;
            T max = list.get(0);
            T min = max;
            CLONE_LIST.add(max);
            for (int i = 1; i < list.size(); i++) {
                T obj = list.get(i);
                if (comparator.compare(obj, max) > 0) {
                    max = obj;
                    maxi = i;
                }
                else if (comparator.compare(obj, min) < 0) {
                    min = obj;
                    mini = i;
                }
                CLONE_LIST.add(obj);
            }
            CLONE_LIST.remove(maxi);
            if (maxi < mini) CLONE_LIST.remove(mini - 1);
            else if (maxi > mini) CLONE_LIST.remove(mini);
            ORDERED_LIST.add(max);
            MIN = min;
            MAX = max;
        }
    }
    
    @Override
    public boolean hasNext() {
        return next <= LAST_INDEX;
    }

    @Override
    public T next() {
        obj = loadNext();
        return obj;
    }

    private T loadNext() {
        if (!hasNext()) throw new NoSuchElementException();
        if (next == 0) {
            next++;
            return MAX;
        }
        if (ORDERED_LIST.size() - 1 >= next) {
            return ORDERED_LIST.get(next++);
        }
        T max;
        if (next == LAST_INDEX) {
            max = MIN;
        }
        else {
            int maxi = 0;
            max = CLONE_LIST.get(0);
            for (int i = 1; i < CLONE_LIST.size(); i++) {
                T obj = CLONE_LIST.get(i);
                if (COMPARATOR.compare(obj, max) > 0) {
                    max = obj;
                    maxi = i;
                }
            }
            CLONE_LIST.remove(maxi);
        }
        ORDERED_LIST.add(max);
        next++;
        return max;
    }
    
    @Override
    public boolean hasPrevious() {
        return previousIndex() >= 0;
    }

    @Override
    public T previous() {
        if (!hasPrevious()) throw new NoSuchElementException();
        return ORDERED_LIST.get(next-- - 2);
    }

    @Override
    public int nextIndex() {
        return next;
    }

    private int index() {
        return next - 1;
    }
    
    @Override
    public int previousIndex() {
        return next - 2;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
//        DEF_LIST.remove(obj);
//        ORDERED_LIST.remove(index());
    }

    @Override
    public void set(T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(T e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public static void main(String[] args) {
        
        List<Integer> numberList = new ArrayList<>();
        
        for (int i = 0; i < 10000; i++) {
            numberList.add((int)(Math.random() * 100000));
        }
        
        Comparator<Integer> cmp = new Comparator<Integer>() {

            @Override
            public int compare(Integer o1, Integer o2) {
                return Integer.compare(o1, o2);
            }
            
        };
        
        long test1Start = System.currentTimeMillis();
        
        List<Integer> numbers = new ComparatorList<>(numberList, cmp);
//        for (Integer i : numbers) {
//            i.toString();
//        }
        ListIterator<Integer> it = numbers.listIterator();
//        it.next();
//        it.next();
//        it.next();
//        it.next();
//        it.previous();
//        it.next();
        
        long test1Stop = System.currentTimeMillis();
        
        System.out.print("Test1: ");
        System.out.println(test1Stop - test1Start);
        
        long test2Start = System.currentTimeMillis();
        
        Collections.sort(numberList, cmp);
        
//        for (Integer i : numberList) {
//            i.toString();
//        }
        
        it = numberList.listIterator();
//        it.next();
//        it.next();
//        it.next();
//        it.next();
//        it.previous();
//        it.next();
        
        long test2Stop = System.currentTimeMillis();
        
        System.out.print("Test2: ");
        System.out.println(test2Stop - test2Start);
        
    }
    
}
