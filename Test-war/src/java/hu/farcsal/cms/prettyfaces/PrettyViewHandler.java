package hu.farcsal.cms.prettyfaces;

import com.sun.faces.application.view.MultiViewHandler;
import hu.farcsal.cms.bean.CachedBeans;
import hu.farcsal.cms.bean.PageBeanLocal;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.Site;
import hu.farcsal.cms.util.Faces;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.cms.util.Pages.FilterType;
import hu.farcsal.log.Log;
import java.util.Locale;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 * @author zoli
 */
public class PrettyViewHandler extends MultiViewHandler {

    private static final Log LOGGER = Log.getLogger(PrettyViewHandler.class);
    
    private PageBeanLocal pageBean;

    protected PageBeanLocal getPageBean() {
        if (pageBean == null) pageBean = CachedBeans.getPageBeanLocal();
        return pageBean;
    }
    
    @Override
    public UIViewRoot createView(FacesContext context, String viewId) {
        if (!filterPages(context)) redirectIfNeed(context);
        return super.createView(context, viewId);
    }
    
    @Override
    public String getActionURL(FacesContext context, String viewId) {
//        String uri = getRealRequestURI(context, false);
//        if (context.getViewRoot().getViewId().equals(viewId)) return uri;
//        String prettyURL = PrettyConfigurationProvider.findPrettyURL(viewId, calculateLocale(context), uri);
//        if (prettyURL != null) return prettyURL;
        String url = super.getActionURL(context, viewId);
        url = Pages.getLanguageParameter().set(url, context.getViewRoot().getLocale().getLanguage(), false);
        System.out.println("getActionURL: " + url);
        return url;
    }
    
    @Override
    public Locale calculateLocale(FacesContext context) {
        Locale locale = super.calculateLocale(context);
        PageMapping pageMapping = PrettyConfigurationProvider.getPageMapping(context);
        return pageMapping == null ? locale : pageMapping.getLanguage().getLocale(locale);
    }
    
    private void redirectIfNeed(FacesContext context) {
        PageMapping mapping = PrettyConfigurationProvider.getPageMapping(context);
        if (mapping != null && mapping.getPage() != null && mapping.getLanguage() != null) {
            if (mapping.getPage().getRealViewPath(false) == null) {
                PageMapping firstPage = Pages.getFirstPage(Site.findSiteByDomain(getPageBean().getSites(), context.getExternalContext().getRequestServerName()), mapping.getPage(), mapping.getLanguage().getCode(), null, true);
                try {
                    context.getExternalContext().redirect(Faces.getContextPath(context) + firstPage.getPermalink(""));
                }
                catch (Exception ex) { // no first page or redirect error
                    LOGGER.e("redirect failed", ex);
                }
            }
        }
    }
    
    private boolean filterPages(FacesContext context) {
        String domain = context.getExternalContext().getRequestServerName();
        Site site = Site.findSiteByDomain(getPageBean().getSites(), domain);
        PageMapping mapping = PrettyConfigurationProvider.getPageMapping(context);
        FilterType filterType = Pages.getFilterType(site, mapping, getPageBean().getPageFilters());
        if (filterType != null) {
            switch (filterType) {
                case PAGE_DISABLED:
                    onPageDisabled(context);
                    break;
                case PAGE_UNKNOWN:
                    onPageUnknown(context);
                    break;
                case SITE_DISABLED:
                    onSiteDisabled(context, domain);
                    break;
                case SITE_FILTERED:
                    onSiteFiltered(context, domain);
                    break;
                case SITE_UNKNOWN:
                    onSiteUnknown(context, domain);
                    break;
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected void send404Error(FacesContext context) {
        Faces.send404Error(context);
    }
    
    protected void onSiteFiltered(FacesContext context, String domain) {
        LOGGER.i(String.format("Page '%s' is filtered by site '%s'", Faces.getRealRequestURI(context, true), domain));
        send404Error(context);
    }
    
    protected void onSiteDisabled(FacesContext context, String domain) {
        LOGGER.i(String.format("Site '%s' is disabled", domain));
        send404Error(context);
    }
    
    protected void onSiteUnknown(FacesContext context, String domain) {
        LOGGER.i(String.format("Unknown site '%s'", domain));
        send404Error(context);
    }
    
    protected void onPageDisabled(FacesContext context) {
        LOGGER.i(String.format("Page '%s' is disabled", Faces.getRealRequestURI(context, true)));
        send404Error(context);
    }
    
    protected void onPageUnknown(FacesContext context) {
        if (PrettyConfigurationProvider.getCurrentMapping(context) == null) {
            LOGGER.i(String.format("URL '%s' is not a pretty URL", Faces.getRealRequestURI(context, true)));
            send404Error(context);
        }
        else {
            LOGGER.i(String.format("URL '%s' is not from the database", Faces.getRealRequestURI(context, true)));
        }
    }
    
}
