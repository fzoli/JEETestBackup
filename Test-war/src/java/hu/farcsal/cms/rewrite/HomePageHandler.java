package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.Site;
import hu.farcsal.cms.prettyfaces.PrettyPageHelper;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.log.Log;
import java.io.File;
import java.util.List;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 *
 * @author zoli
 */
class HomePageHandler extends HttpOperation {

    private static final String PARAM_REDIRECTING = "hu.farcsal.cms.rewrite.HOME_PAGE_REDIRECTING";
    
    private static final Log LOGGER = Log.getLogger(HomePageHandler.class);
    
    private final ServletContext context;
    private final PrettyPageHelper pageHelper;
    private final List<Site> sites;
    
    private final boolean redirecting;

    public HomePageHandler(ServletContext context, PrettyPageHelper pageHelper, List<Site> sites) {
        this(context, pageHelper, sites, isRedirecting(context));
    }
    
    public HomePageHandler(ServletContext context, PrettyPageHelper pageHelper, List<Site> sites, boolean redirecting) {
        this.context = context;
        this.pageHelper = pageHelper;
        this.sites = sites;
        this.redirecting = redirecting;
    }
    
    private static boolean isRedirecting(ServletContext context) {
        String value = context.getInitParameter(PARAM_REDIRECTING);
        if (value == null) return false;
        return value.equalsIgnoreCase(Boolean.toString(true));
    }
    
    protected void onHomepageNotFound(HttpServletRewrite hsr, EvaluationContext ec) {
        LOGGER.w("homepage not found");
    }
    
    protected void onPageFileNotExists(HttpServletRewrite hsr, EvaluationContext ec) {
        LOGGER.e("page file not exists");
    }
    
    protected void onPermalinkUnavailable(HttpServletRewrite hsr, EvaluationContext ec) {
        LOGGER.w("permalink unavailable");
    }
    
    private void handle(HttpServletRewrite hsr, EvaluationContext ec, PageMapping mapping) {
        if (redirecting) performRedirect(hsr, ec, mapping);
        else performForward(hsr, ec, mapping);
    }
    
    private void performRedirect(HttpServletRewrite hsr, EvaluationContext ec, PageMapping mapping) {
        try {
            String url = mapping.getPermalink("");
            url = hsr.getRequest().getContextPath() + (url.startsWith("/") ? "" : "/") + url;
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
            String viewPath = pageHelper.stripFacesDir(mapping.getPage().getRealViewPath(false));
            if (viewPath == null || !new File(context.getRealPath(viewPath)).isFile()) {
                onPageFileNotExists(hsr, ec);
            }
            else {
                handle(hsr, ec, mapping);
            }
        }
    }
            
}
