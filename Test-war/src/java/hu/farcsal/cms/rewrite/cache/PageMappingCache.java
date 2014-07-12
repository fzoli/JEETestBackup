package hu.farcsal.cms.rewrite.cache;

import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.rewrite.RewriteRuleCache;

/**
 *
 * @author zoli
 */
public class PageMappingCache extends RewriteRuleCache {
    
    private final PageMapping pageMapping;
    
    public PageMappingCache(PageMapping pageMapping) {
        super(pageMapping.getLanguage().getCode());
        this.pageMapping = pageMapping;
    }

    public PageMapping getPageMapping() {
        return pageMapping;
    }
    
    @Override
    protected boolean matches(String value, MatcherType type) {
        switch (type) {
            case URL:
                // find by URL
                try {
                    String link = getPageMapping().getPermalink(value);
                    return (link + "/").equalsIgnoreCase(value) || link.equalsIgnoreCase(value);
                }
                catch (Exception ex) {
                    // invalid URL; does not match
                    return false;
                }
            case VIEW_ID:
                // find by view id
                String view = getPageMapping().getPage().getViewPath(false);
                if (view == null) return false;
                if (!view.startsWith("/")) view = "/" + view;
                return value.equals(view);
            default:
                return false;
        }
    }
    
}
