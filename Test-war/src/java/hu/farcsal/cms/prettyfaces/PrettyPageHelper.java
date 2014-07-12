package hu.farcsal.cms.prettyfaces;

import hu.farcsal.util.Servlets;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.spec.Helpers;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;

/**
 *
 * @author zoli
 */
public class PrettyPageHelper implements Helpers.PageHelper {

    private final ServletContext SC;

    private String facesDir;

    public PrettyPageHelper(ServletContext sc) {
        SC = sc;
    }

    @Override
    public String getFacesDir() {
        if (facesDir == null) facesDir = Servlets.getMappingDir(SC, FacesServlet.class);
        return facesDir;
    }

    @Override
    public String getAppCtxPath() {
        return SC.getContextPath();
    }

    @Override
    public String stripAppCtxFromUrl(String url) {
        if (url == null) return null;
        if (url.startsWith(getAppCtxPath())) return url.substring(getAppCtxPath().length());
        return url;
    }

    @Override
    public String getRealViewPath(Page page, boolean withDir) {
        String vp = page.getViewPath(withDir);
        if (!page.isViewPathGenerated()) return vp;
        try {
            ELContext elContext = FacesContext.getCurrentInstance().getELContext();
            MethodExpression method = ExpressionFactory.newInstance().createMethodExpression(elContext, "#{" + vp + "}", String.class, new Class[] {});
            return (String) method.invoke(elContext, new Object[]{});
        }
        catch (Exception ex) {
            return vp;
        }
    }

    public String stripFacesDir(String path) {
        if (path == null) return null;
        if (path.startsWith(getFacesDir())) path = path.substring(getFacesDir().length());
        if (path.startsWith("/")) path = path.substring(1);
        return path;
    }

}
