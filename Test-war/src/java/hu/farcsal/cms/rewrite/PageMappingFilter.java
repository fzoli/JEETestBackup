package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.bean.CachedBeans;
import hu.farcsal.cms.bean.PageBeanLocal;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.Site;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.log.Log;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;

/**
 *
 * @author zoli
 */
public class PageMappingFilter extends InboundPageFilter {

    private static final Log LOGGER = Log.getLogger(PageMappingFilter.class);
    
    public PageMappingFilter(PageMapping mapping) {
        super(new PageMappingHelper(mapping));
    }
    
    private static class PageMappingHelper implements InboundPageFilter.FilterCondition {

        private static final PageBeanLocal pageBean = CachedBeans.getPageBeanLocal();
        
        private final PageMapping mapping;
        
        public PageMappingHelper(PageMapping mapping) {
            this.mapping = mapping;
        }

        @Override
        public boolean evaluate(HttpInboundServletRewrite rwrt, EvaluationContext ec) {
            String domain = rwrt.getRequest().getServerName();
            Site site = Site.findSiteByDomain(pageBean.getSites(), domain);
            Pages.FilterType filterType = Pages.getFilterType(site, mapping, pageBean.getPageFilters());
            if (filterType != null) {
                String reqURI = Pages.getRealRequestURI(rwrt.getRequest(), true);
                switch (filterType) {
                    case PAGE_DISABLED:
                        LOGGER.i("Page '%s' is disabled", reqURI);
                        break;
                    case PAGE_UNKNOWN:
                        LOGGER.i("URL '%s' is not from the database", reqURI);
                        break;
                    case SITE_DISABLED:
                        LOGGER.i("Site '%s' is disabled", domain);
                        break;
                    case SITE_FILTERED:
                        LOGGER.i("Page '%s' is filtered by site '%s'", reqURI, domain);
                        break;
                    case SITE_UNKNOWN:
                        LOGGER.i("Unknown site '%s'", domain);
                        break;
                }
                return false;
            }
            return true;
        }
        
    }

}
