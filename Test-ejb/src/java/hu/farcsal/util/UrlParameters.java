package hu.farcsal.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zoli
 */
public class UrlParameters {

    private final Pattern PARAM_PATTERN, PARAM_REMOVER_0, PARAM_REMOVER_1, PARAM_REMOVER_2, PARAM_REMOVER_3;
    
    private final String KEY;
    
    public UrlParameters(String key) {
        KEY = key;
        PARAM_PATTERN = Pattern.compile(String.format("(.*)([?&])(%s=([^&\\s]+))(.*)", key));
        PARAM_REMOVER_0 = Pattern.compile(String.format("([?&])(%s=([^&\\s]+))", key));
        PARAM_REMOVER_1 = Pattern.compile("&{2,}");
        PARAM_REMOVER_2 = Pattern.compile("\\?&");
        PARAM_REMOVER_3 = Pattern.compile("[?&]$");
    }

    public final String getKey() {
        return KEY;
    }
    
    public String get(String url) {
        if (KEY == null) return null;
        Matcher m = PARAM_PATTERN.matcher(url);
        if (m.matches()) {
            return m.group(4);
        }
        return null;
    }
    
    public String remove(String url) {
        if (KEY == null) return url;
        Matcher matcher = PARAM_REMOVER_0.matcher(url);
        url = matcher.replaceAll("$1"); // removes the parameter
        matcher = PARAM_REMOVER_1.matcher(url);
        url = matcher.replaceAll("&"); // replaces && to &
        matcher = PARAM_REMOVER_2.matcher(url);
        url = matcher.replaceFirst("?"); // replaces ?& to ?
        matcher = PARAM_REMOVER_3.matcher(url);
        return matcher.replaceFirst(""); // removes ? or & at the end of the string
    }

    public String set(String url, String value) {
        return set(url, value, true);
    }
    
    public String set(String url, String value, boolean override) {
        if (KEY == null) {
            return url;
        }
        if (get(url) != null) {
            if (!override) return url;
            url = remove(url);
        }
        char last = url.length() > 0 ? url.charAt(url.length() - 1) : '\0';
        return url + (url.contains("?") ? (last == '?' ? "" : (last == '&' ? "" : "&")) : "?") + KEY + "=" + value;
    }
    
    private static final String[] test = {
        "a",
        "a?",
        "a?asd=bar&",
        "a?lang=hu&asd=bar&",
        "a?lang=hu",
        "a?lang=hu&b=1",
        "a?b=1&lang=hu",
        "a?b=1&lang=hu&c=2",
        "a?b=1&lang=hu&lang=en&lang=de&c=2",
        ""
    };
    
    public static void main(String[] args) {
        UrlParameters helper = new UrlParameters("lang");
        System.out.println("Get:");
        for (String t : test) {
            System.out.println(helper.get(t));
        }
        System.out.println("\nSet:");
        for (String t : test) {
            System.out.println(helper.set(t, "foo"));
        }
        System.out.println("\nRemove:");
        for (String t : test) {
            System.out.println(helper.remove(t));
        }
    }
    
}
