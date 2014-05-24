package jsf.prettyfaces;

import bean.PageBeanLocal;
import com.sun.faces.application.view.MultiViewHandler;
import entity.Page;
import entity.PageFilter;
import entity.PageMapping;
import entity.Site;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        filterPages(context);
        return super.createView(context, viewId);
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        if (context.getViewRoot().getViewId().equals(viewId)) {
            return getRealRequestURI(context, false);
        }
        String prettyURL = PrettyConfigurationProvider.findPrettyURL(viewId, calculateLocale(context), null);
        if (prettyURL != null) return prettyURL;
        return super.getActionURL(context, viewId);
    }
    
    @Override
    public Locale calculateLocale(FacesContext context) {
        Locale locale = super.calculateLocale(context);
        PageMapping pageMapping = PrettyConfigurationProvider.getPageMapping(context);
        return pageMapping == null ? locale : pageMapping.getLanguage().getLocale(locale);
    }
    
    private void filterPages(FacesContext context) {
        PageMapping pageMapping = PrettyConfigurationProvider.getPageMapping(context);
        if (pageMapping != null) {
            Page page = pageMapping.getPage();
            if (page.isDisabled()) {
                onPageDisabled(context);
            }
            else {
                String domain = context.getExternalContext().getRequestServerName();
                List<Site> sites = getPageBean().getSites();
                Site site = Site.findSiteByDomain(sites, domain);
                if (site == null) {
                    if (page.isSiteDependent()) onSiteUnknown(context, domain);
                }
                else {
                    if (site.isDisabled()) {
                        onSiteDisabled(context, domain);
                    }
                    else if (PageFilter.isPageFiltered(getPageBean().getPageFilters(), site)) {
                        onSiteFiltered(context, domain);
                    }
                }
            }
        }
        else {
            onPageUnknown(context);
        }
    }
    
    protected String getRealRequestURI(FacesContext context, boolean trimAppContext) {
        try {
            String uri = (String) context.getExternalContext().getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI);
            return trimAppContext ? trimRequestURI(context, uri) : uri;
        }
        catch (Exception ex) {
            return getRequestURI(context, trimAppContext);
        }
    }
    
    protected String getRequestURI(FacesContext context, boolean trimAppContext) {
        String uri = ((HttpServletRequest) context.getExternalContext().getRequest()).getRequestURI();
        return trimAppContext ? trimRequestURI(context, uri) : uri;
    }
    
    private String trimRequestURI(FacesContext context, String requestURI) {
        return requestURI.substring(context.getExternalContext().getApplicationContextPath().length());
    }
    
    @Override
    protected void send404Error(FacesContext context) {
        context.getExternalContext().setResponseStatus(404);
        try {
            ((HttpServletResponse) context.getExternalContext().getResponse()).sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (IOException ex) {
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
        LOGGER.i(String.format("URL '%s' is not a pretty URL", getRealRequestURI(context, true)));
        send404Error(context);
    }
    
}
