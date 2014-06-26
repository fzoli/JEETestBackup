package jsf.prettyfaces;

import bean.PageBeanLocal;
import entity.PageMapping;
import entity.Site;
import java.io.File;
import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zoli
 */
//@WebServlet(name = "PrettyHomeServlet", urlPatterns = "")
@WebServlet(name = "PrettyHomeServlet", urlPatterns = {"/"})
public class PrettyHomeServlet extends HttpServlet {
    
    @EJB
    private PageBeanLocal pageBean;

    protected void onHomepageNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO:
        // - allow non-JSF pages
        // - a default page would be better than 404 error
        send404(response);
    }
    
    protected void onPageFileNotExists(HttpServletRequest request, HttpServletResponse response) throws IOException {
        send404(response);
    }
    
    protected void onPermalinkUnavailable(HttpServletRequest request, HttpServletResponse response) throws IOException {
        send404(response);
    }
    
    protected static void send404(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Site site = Site.findSiteByDomain(pageBean.getSites(), request.getServerName());
        String defLanguage = site != null ? (site.getDefLanguage() != null ? site.getDefLanguage().getCode() : null) : null;
        PageMapping mapping = PrettyConfigurationProvider.getFirstPage(request, defLanguage == null ? "en" : defLanguage, defLanguage != null, false);
        if (mapping == null) {
            onHomepageNotFound(request, response);
        }
        else {
            String viewPath = PrettyConfigurationProvider.stripPageRoot(mapping.getPage().getRealViewPath(false));
            if (viewPath == null || !new File(getServletContext().getRealPath(viewPath)).isFile()) {
                onPageFileNotExists(request, response);
            }
            else {
                try {
                    response.sendRedirect(request.getContextPath() + mapping.getPermalink(""));
                }
                catch (Exception ex) {
                    onPermalinkUnavailable(request, response);
                }
            }
        }
    }
    
    @Override
    public final String getServletInfo() {
        return "Redirects to the main page.";
    }

}
