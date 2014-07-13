package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.bean.CachedBeans;
import hu.farcsal.cms.bean.PageBeanLocal;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.Site;
import hu.farcsal.cms.util.Pages;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zoli
 */
public class PageMappingFilter extends InboundPageFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageMappingFilter.class);
    
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
                if (LOGGER.isInfoEnabled()) {
                    String reqURI = Pages.getRealRequestURI(rwrt.getRequest(), true);
                    switch (filterType) {
                        case PAGE_DISABLED:
                            LOGGER.info("Page '{}' is disabled", reqURI);
                            break;
                        case PAGE_UNKNOWN:
                            LOGGER.info("URL '{}' is not from the database", reqURI);
                            break;
                        case SITE_DISABLED:
                            LOGGER.info("Site '{}' is disabled", domain);
                            break;
                        case SITE_FILTERED:
                            LOGGER.info("Page '{}' is filtered by site '{}'", reqURI, domain);
                            break;
                        case SITE_UNKNOWN:
                            LOGGER.info("Unknown site '{}'", domain);
                            break;
                    }
                }
                return false;
            }
            return true;
        }
        
    }

}
