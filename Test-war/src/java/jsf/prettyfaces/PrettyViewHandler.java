package jsf.prettyfaces;

import bean.PageBeanLocal;
import com.sun.faces.application.view.MultiViewHandler;
import entity.PageMapping;
import entity.Site;
import java.util.Locale;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jsf.prettyfaces.PrettyConfigurationProvider.FilterType;
import logging.Log;

/**
 * @author zoli
 */
public class PrettyViewHandler extends MultiViewHandler {

    private static final Log LOGGER = Log.getLogger(PrettyViewHandler.class);
    
    private PageBeanLocal pageBean;

    protected PageBeanLocal getPageBean() {
        if (pageBean == null) pageBean = Beans.lookupPageBeanLocal();
        return pageBean;
    }
    
    @Override
    public UIViewRoot createView(FacesContext context, String viewId) {
        if (!filterPages(context)) redirectIfNeed(context);
        return super.createView(context, viewId);
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        String uri = getRealRequestURI(context, false);
        if (context.getViewRoot().getViewId().equals(viewId)) {
            // TODO: ha vannak extra paraméterek és ebben a metódusban törődni kell velük, akkor ez nem jó megoldás (mert nem módosul az url)
            return uri;
        }
        // TODO: ha itt megszerezhetőek az esetleges extra paraméterek, akkor használni kéne; egyébként megnézni, a prettyfaces hol generálja az url-t
        String prettyURL = PrettyConfigurationProvider.findPrettyURL(viewId, calculateLocale(context), uri);
        if (prettyURL != null) return prettyURL;
        return super.getActionURL(context, viewId);
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
                PageMapping firstPage = PrettyConfigurationProvider.getFirstPage(Site.findSiteByDomain(getPageBean().getSites(), context.getExternalContext().getRequestServerName()), mapping.getPage(), mapping.getLanguage().getCode(), null, true);
                try {
                    context.getExternalContext().redirect(getContextPath(context) + firstPage.getPermalink(""));
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
        FilterType filterType = PrettyConfigurationProvider.getFilterType(site, mapping, getPageBean().getPageFilters());
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
    
    protected String getRealRequestURI(FacesContext context, boolean stripAppContext) {
        try {
            String uri = (String) context.getExternalContext().getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI);
            return stripAppContext ? stripContextPath(context, uri) : uri;
        }
        catch (Exception ex) {
            return getRequestURI(context, stripAppContext);
        }
    }
    
    protected String getRequestURI(FacesContext context, boolean stripAppContext) {
        String uri = ((HttpServletRequest) context.getExternalContext().getRequest()).getRequestURI();
        return stripAppContext ? stripContextPath(context, uri) : uri;
    }
    
    private String stripContextPath(FacesContext context, String requestURI) {
        return requestURI.substring(getContextPath(context).length());
    }
    
    private String getContextPath(FacesContext context) {
        return context.getExternalContext().getApplicationContextPath();
    }
    
    @Override
    protected void send404Error(FacesContext context) {
        try {
            context.getExternalContext().setResponseStatus(404);
            ((HttpServletResponse) context.getExternalContext().getResponse()).sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (Exception ex) { // IllegalState vagy IOException
            LOGGER.e("404 error failed", ex);
        }
        context.responseComplete();
    }
    
    protected void onSiteFiltered(FacesContext context, String domain) {
        LOGGER.i(String.format("Page '%s' is filtered by site '%s'", getRealRequestURI(context, true), domain));
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
        LOGGER.i(String.format("Page '%s' is disabled", getRealRequestURI(context, true)));
        send404Error(context);
    }
    
    protected void onPageUnknown(FacesContext context) {
        if (PrettyConfigurationProvider.getCurrentMapping(context) == null) {
            LOGGER.i(String.format("URL '%s' is not a pretty URL", getRealRequestURI(context, true)));
            send404Error(context);
        }
        else {
            LOGGER.i(String.format("URL '%s' is not from the database", getRealRequestURI(context, true)));
        }
    }
    
}
