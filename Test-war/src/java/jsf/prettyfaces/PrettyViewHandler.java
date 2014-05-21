package jsf.prettyfaces;

import com.sun.faces.application.view.MultiViewHandler;
import entity.Page;
import entity.PageMapping;
import entity.Site;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zoli
 */
public class PrettyViewHandler extends MultiViewHandler {

    @Override
    public UIViewRoot createView(FacesContext context, String viewId) {
        filterPages(context);
        return super.createView(context, viewId);
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
            else if (!page.getSites().isEmpty()) {
                String domain = context.getExternalContext().getRequestServerName();
                Site site = page.findSite(domain);
                if (site == null) {
                    onSiteFiltered(context, domain);
                }
                else if (site.isDisabled()) {
                    onSiteDisabled(context);
                }
            }
        }
    }
    
    protected String getRealRequestURL(FacesContext context) {
        return ((String) context.getExternalContext().getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI)).substring(context.getExternalContext().getApplicationContextPath().length());
    }
    
    @Override
    protected void send404Error(FacesContext context) {
        try {
            ((HttpServletResponse) context.getExternalContext().getResponse()).sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "404 error failed", ex);
        }
        context.getExternalContext().setResponseStatus(404);
        context.responseComplete();
    }
    
    private void onSiteFiltered(FacesContext context, String domain) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, String.format("Page '%s' is filtered by site '%s'", getRealRequestURL(context), domain));
        send404Error(context);
    }
    
    private void onSiteDisabled(FacesContext context) {
        send404Error(context);
    }
    
    private void onPageDisabled(FacesContext context) {
        send404Error(context);
    }
    
}
