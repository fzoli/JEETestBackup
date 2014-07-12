package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.entity.PageMapping;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.ocpsoft.rewrite.config.Rule;

/**
 *
 * @author zoli
 */
public final class DatabaseRuleCache {
    
    private static final Map<Rule, DatabaseRuleCache> RULES = new WeakHashMap<>();
    
    private final PageMapping pageMapping;
    
    private DatabaseRuleCache(PageMapping pageMapping) {
        this.pageMapping = pageMapping;
    }

    public PageMapping getPageMapping() {
        return pageMapping;
    }
    
    private boolean matches(String value, boolean url) {
        if (url) {
            // find by URL
            try {
                String link = getPageMapping().getPermalink(value);
                return link.startsWith(value);
            }
            catch (Exception ex) {
                // invalid URL; does not match
                return false;
            }
        }
        else {
            // find by view id
            String view = getPageMapping().getPage().getViewPath(false);
            if (!view.startsWith("/")) view = "/" + view;
            return value.equals(view);
        }
    }
    
    public static DatabaseRuleCache findByUrl(String value) {
        return find(value, true);
    }
    
    public static DatabaseRuleCache findByViewId(String value) {
        return find(value, false);
    }
    
    private static DatabaseRuleCache find(String value, boolean url) {
        DatabaseRuleCache cache = null;
        synchronized (RULES) {
            Iterator<Map.Entry<Rule, DatabaseRuleCache>> it = RULES.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Rule, DatabaseRuleCache> e = it.next();
                if (e.getValue().matches(value, url)) {
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
    
    static DatabaseRuleCache save(Rule rule, PageMapping mapping) {
        DatabaseRuleCache cache = new DatabaseRuleCache(mapping);
        synchronized (RULES) {
            RULES.put(rule, cache);
        }
        return cache;
    }
    
}
