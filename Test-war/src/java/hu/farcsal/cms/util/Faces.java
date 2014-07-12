package hu.farcsal.cms.util;

import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zoli
 */
public class Faces {
    
    public static void send404Error(FacesContext context) {
        try {
            context.getExternalContext().setResponseStatus(404);
            ((HttpServletResponse) context.getExternalContext().getResponse()).sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (Exception ex) {
            // IllegalState or IOException
        }
        context.responseComplete();
    }
    
    public static String getRealRequestURI(FacesContext context, boolean stripAppContext) {
        try {
            String uri = (String) context.getExternalContext().getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI);
            return stripAppContext ? stripContextPath(context, uri) : uri;
        }
        catch (Exception ex) {
            return getRequestURI(context, stripAppContext);
        }
    }
    
    public static String getRequestURI(FacesContext context, boolean stripAppContext) {
        String uri = ((HttpServletRequest) context.getExternalContext().getRequest()).getRequestURI();
        return stripAppContext ? stripContextPath(context, uri) : uri;
    }
    
    private static String stripContextPath(FacesContext context, String requestURI) {
        return requestURI.substring(getContextPath(context).length());
    }
    
    public static String getContextPath(FacesContext context) {
        return context.getExternalContext().getApplicationContextPath();
    }
    
}
