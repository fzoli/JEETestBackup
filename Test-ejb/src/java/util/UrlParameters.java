package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author zoli
 */
public class UrlParameters {

    private final Pattern PARAM_PATTERN/*, PARAM_REPLACER*/, PARAM_REMOVER_0, PARAM_REMOVER_1, PARAM_REMOVER_2, PARAM_REMOVER_3;
    
    private final String CLASS_KEY;
    
    public UrlParameters(String classKey) {
        CLASS_KEY = classKey;
        PARAM_PATTERN = Pattern.compile(String.format("(.*)([?&])(%s=([^&\\s]+))(.*)", classKey));
//        PARAM_REPLACER = Pattern.compile(String.format("([?&]%s=)([^&\\s]+)", classKey));
        PARAM_REMOVER_0 = Pattern.compile(String.format("([?&])(%s=([^&\\s]+))", classKey));
        PARAM_REMOVER_1 = Pattern.compile("&{2,}");
        PARAM_REMOVER_2 = Pattern.compile("\\?&");
        PARAM_REMOVER_3 = Pattern.compile("[?&]$");
    }

    public String get(String url) {
        if (CLASS_KEY == null) return null;
        Matcher m = PARAM_PATTERN.matcher(url);
        if (m.matches()) {
            return m.group(4);
        }
        return null;
    }
    
    public String remove(String url) {
        if (CLASS_KEY == null) return url;
        Matcher matcher = PARAM_REMOVER_0.matcher(url);
        url = matcher.replaceAll("$1"); // removes the class parameter
        matcher = PARAM_REMOVER_1.matcher(url);
        url = matcher.replaceAll("&"); // replaces && to &
        matcher = PARAM_REMOVER_2.matcher(url);
        url = matcher.replaceFirst("?"); // replaces ?& to ?
        matcher = PARAM_REMOVER_3.matcher(url);
        return matcher.replaceFirst(""); // removes ? or & at the end of the string
    }

    public String set(String url, String value) {
        if (CLASS_KEY == null) {
            return url;
        }
        if (get(url) != null) {
            url = remove(url);
        }
        return url + (url.contains("?") ? (url.charAt(url.length() - 1) == '?'  ? "" : (url.charAt(url.length() - 1) == '&' ? "" : "&")) : "?") + CLASS_KEY + "=" + value;
//        if (get(url) == null) {
//            url += (url.contains("?") ? "&" : "?") + CLASS_KEY + "=" + value;
//        }
//        else {
//            url = PARAM_REPLACER.matcher(url).replaceAll("$1" + value);
//        }
//        return url;
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
