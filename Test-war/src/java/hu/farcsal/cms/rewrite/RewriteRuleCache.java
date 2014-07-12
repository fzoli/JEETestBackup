package hu.farcsal.cms.rewrite;

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
    
    public static RewriteRuleCache findByUrl(String value) {
        return find(value, MatcherType.URL);
    }
    
    public static RewriteRuleCache findByViewId(String value) {
        return find(value, MatcherType.VIEW_ID);
    }
    
    private static RewriteRuleCache find(String value, MatcherType type) {
        RewriteRuleCache cache = null;
        synchronized (RULES) {
            Iterator<Map.Entry<Rule, RewriteRuleCache>> it = RULES.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Rule, RewriteRuleCache> e = it.next();
                if (e.getValue().matches(value, type)) {
                    cache = e.getValue();
                    break;
                }
            }
        }
        return cache;
    }
    
    static void clear() {
        synchronized (RULES) {
            RULES.clear();
        }
    }
    
    static RewriteRuleCache save(Rule rule, RewriteRuleCache cache) {
        synchronized (RULES) {
            RULES.put(rule, cache);
        }
        return cache;
    }
    
}
