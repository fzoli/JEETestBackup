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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Site site = Site.findSiteByDomain(pageBean.getSites(), request.getServerName());
        String defLanguage = site != null ? (site.getDefLanguage() != null ? site.getDefLanguage().getCode() : null) : null;
        PageMapping mapping = PrettyConfigurationProvider.getFirstPage(request, defLanguage == null ? "en" : defLanguage, defLanguage != null);
        if (mapping == null) {
            // TODO:
            // - allow non-JSF pages
            // - a default page would be better than 404 error
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else {
            String viewPath = PrettyConfigurationProvider.stripPageRoot(mapping.getPage().getViewPath());
            if (viewPath == null || !new File(getServletContext().getRealPath(viewPath)).isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            else {
                response.sendRedirect(request.getContextPath() + mapping.getPermalink(""));
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Redirects to the main page.";
    }

}
