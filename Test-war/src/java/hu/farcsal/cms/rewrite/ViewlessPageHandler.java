package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.bean.CachedBeans;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.Site;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.log.Log;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 *
 * @author zoli
 */
public class ViewlessPageHandler extends HttpOperation {

    private static final Log LOGGER = Log.getLogger(ViewlessPageHandler.class);

    private final PageMapping mapping;
    
    public ViewlessPageHandler(PageMapping mapping) {
        this.mapping = mapping;
    }
    
    @Override
    public void performHttp(HttpServletRewrite hsr, EvaluationContext ec) {
        if (hsr instanceof HttpInboundServletRewrite && mapping != null && mapping.getPage() != null && mapping.getLanguage() != null) {
            if (mapping.getPage().getRealViewPath(false) == null) {
                PageMapping firstPage = Pages.getFirstPage(Site.findSiteByDomain(CachedBeans.getPageBeanLocal().getSites(), hsr.getRequest().getServerName()), mapping.getPage(), mapping.getLanguage().getCode(), null, true);
                try {
                    String link = firstPage.getPermalinkWithCtxPath("");
                    LOGGER.i("redirecting from '%s' to '%s'", Pages.getRealRequestURI(hsr.getRequest(), false), link);
                    Redirect.temporary(link).performHttp(hsr, ec);
                }
                catch (Exception ex) {
                    LOGGER.e(firstPage == null ? "there is no page to redirect" : "redirect error", ex);
                }
            }
        }
    }
    
}
