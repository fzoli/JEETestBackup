package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.Site;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.util.WebConfig;
import java.util.List;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zoli
 */
class HomePageHandler extends HttpOperation {

    private static final String PARAM_REDIRECTING = "hu.farcsal.cms.rewrite.HOME_PAGE_REDIRECTING";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePageHandler.class);
    
    private final ServletContext context;
    private final List<Site> sites;
    
    private final boolean redirecting;

    public HomePageHandler(ServletContext context, List<Site> sites) {
        this(context, sites, WebConfig.isTrue(context, PARAM_REDIRECTING));
    }
    
    public HomePageHandler(ServletContext context, List<Site> sites, boolean redirecting) {
        this.context = context;
        this.sites = sites;
        this.redirecting = redirecting;
    }
    
    protected void onHomepageNotFound(HttpServletRewrite hsr, EvaluationContext ec) {
        LOGGER.warn("homepage not found");
    }
    
    protected void onPageFileNotExists(HttpServletRewrite hsr, EvaluationContext ec) {
        LOGGER.error("page file not exists");
    }
    
    protected void onPermalinkUnavailable(HttpServletRewrite hsr, EvaluationContext ec) {
        LOGGER.warn("permalink unavailable");
    }
    
    private void handle(HttpServletRewrite hsr, EvaluationContext ec, PageMapping mapping) {
        if (redirecting) performRedirect(hsr, ec, mapping);
        else performForward(hsr, ec, mapping);
    }
    
    private void performRedirect(HttpServletRewrite hsr, EvaluationContext ec, PageMapping mapping) {
        try {
            String url = mapping.getPermalinkWithCtxPath("");
            Redirect.temporary(url).performHttp(hsr, ec);
        }
        catch (Exception ex) {
            onPermalinkUnavailable(hsr, ec);
        }
    }
    
    private void performForward(HttpServletRewrite hsr, EvaluationContext ec, PageMapping mapping) {
        Forward.to(Pages.getLanguageParameter().set(mapping.getPage().getRealViewPath(true), mapping.getLanguage().getCode())).performHttp(hsr, ec);
    }
    
    @Override
    public void performHttp(HttpServletRewrite hsr, EvaluationContext ec) {
        Site site = Site.findSiteByDomain(sites, hsr.getRequest().getServerName());
        String defLanguage = site != null ? (site.getDefLanguage() != null ? site.getDefLanguage().getCode() : null) : null;
        PageMapping mapping = Pages.getFirstPage(hsr.getRequest(), defLanguage == null ? "en" : defLanguage, defLanguage != null, false);
        if (mapping == null) {
            onHomepageNotFound(hsr, ec);
        }
        else {
            if (!Pages.isViewFileExists(context, mapping.getPage())) {
                onPageFileNotExists(hsr, ec);
            }
            else {
                handle(hsr, ec, mapping);
            }
        }
    }
            
}
