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
import org.ocpsoft.rewrite.servlet.config.HttpOperation;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 *
 * @author zoli
 */
public class HomePageRedirector extends HttpOperation {

    protected static final Log LOGGER = Log.getLogger(HomePageRedirector.class);
    
    private final ServletContext context;
    private final PrettyPageHelper pageHelper;
    private final List<Site> sites;
    
    public HomePageRedirector(ServletContext context, PrettyPageHelper pageHelper, List<Site> sites) {
        this.context = context;
        this.pageHelper = pageHelper;
        this.sites = sites;
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
    
    protected static void performRedirect(HttpServletRewrite hsr, EvaluationContext ec, String url) {
        Redirect.temporary(
            hsr.getRequest().getContextPath() + (url.startsWith("/") ? "" : "/") + url
        ).performHttp(hsr, ec);
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
                try {
                    performRedirect(hsr, ec, mapping.getPermalink(""));
                }
                catch (Exception ex) {
                    onPermalinkUnavailable(hsr, ec);
                }
            }
        }
    }
            
}
