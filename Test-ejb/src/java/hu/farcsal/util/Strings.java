package hu.farcsal.util;

import java.text.Normalizer;
import java.util.List;

/**
 *
 * @author zoli
 */
public class Strings {
    
    public static interface Formatter<T> {
        public String toString(T obj);
    }
    
    public static <T> String join(List<T> list, String delim, Formatter<T> getter) {
        int len = list.size();
        if (len == 0) return "";
        String str = getter.toString(list.get(0));
        if (str == null) return null;
        StringBuilder sb = new StringBuilder(str);
        for (int i = 1; i < len; i++) {
            str = getter.toString(list.get(i));
            if (str == null) return null;
            if (!str.isEmpty()) {
                sb.append(delim);
                sb.append(str);
            }
        }
        return sb.toString();
    }
    
    public static String toPrettyString(String string) {
        return Normalizer.normalize(string == null ? "" : string.toLowerCase(), Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // normalize all characters and get rid of all diacritical marks (so that e.g. é, ö, à becomes e, o, a)
            .replaceAll("[^\\p{Alnum}]+", "-") // replace all remaining non-alphanumeric characters by - and collapse when necessary
            .replaceAll("[^a-z0-9]+$", "") // remove trailing punctuation
            .replaceAll("^[^a-z0-9]+", ""); // remove leading punctuation
    }
    
    public static void main(String[] args) {
        System.out.println(toPrettyString("/öt szép szűzlány #1 őrült {ír/ót} nyúz!"));
    }
    
}
