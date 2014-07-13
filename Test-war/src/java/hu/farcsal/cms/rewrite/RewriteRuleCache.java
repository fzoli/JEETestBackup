package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.util.Pages;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.ocpsoft.rewrite.config.Rule;

/**
 *
 * @author zoli
 */
public abstract class RewriteRuleCache {
    
    private static final Map<Rule, RewriteRuleCache> RULES = new WeakHashMap<>();
    
    protected static enum MatcherType { URL, VIEW_ID }
    
    private final String lngCode;
    
    protected RewriteRuleCache(String lngCode) {
        this.lngCode = lngCode;
    }

    public String getLanguageCode() {
        return lngCode;
    }
    
    protected abstract boolean matches(String value, MatcherType type);
    
    /**
     * Find the rule.
     * @param value the URL
     * @return the first match
     */
    public static RewriteRuleCache findByUrl(String value) {
        return find(null, value, MatcherType.URL);
    }
    
    /**
     * Find the rule.
     * @param <T> cache type
     * @param type filter
     * @param value the URL
     * @return the first match
     */
    public static <T extends RewriteRuleCache> T findByUrl(Class<T> type, String value) {
        return find(type, value, MatcherType.URL);
    }
    
    /**
     * Find the rule.
     * Do NOT trust its result!
     * Use it only for check whether there is a rule that uses the view.
     * @param value the view id
     * @return the first match without URL checking
     */
    public static RewriteRuleCache findByViewId(String value) {
        return find(null, value, MatcherType.VIEW_ID);
    }
    
    private static <T extends RewriteRuleCache> T find(Class<T> rtype, String value, MatcherType mtype) {
        if (MatcherType.URL == mtype) value = Pages.stripAppContext(value);
        T cache = null;
        synchronized (RULES) {
            Iterator<Map.Entry<Rule, RewriteRuleCache>> it = RULES.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Rule, RewriteRuleCache> e = it.next();
                boolean typeMatch = rtype == null || rtype.isInstance(e.getValue());
                if (typeMatch && e.getValue().matches(value, mtype)) {
                    cache = (T) e.getValue();
                    break;
                }
            }
        }
        return cache;
    }
    
    static void clear(Class<? extends RewriteRuleCache> clazz) {
        if (clazz != null) synchronized (RULES) {
            Iterator<Map.Entry<Rule, RewriteRuleCache>> it = RULES.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Rule, RewriteRuleCache> e = it.next();
                if (e.getValue() != null && clazz == e.getValue().getClass()) {
                    it.remove();
                }
            }
        }
    }
    
    static RewriteRuleCache save(Rule rule, RewriteRuleCache cache) {
        if (cache != null) synchronized (RULES) {
            RULES.put(rule, cache);
        }
        return cache;
    }
    
}
