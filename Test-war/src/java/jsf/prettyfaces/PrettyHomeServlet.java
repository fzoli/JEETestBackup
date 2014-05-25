package jsf.prettyfaces;

import entity.PageMapping;
import java.io.File;
import java.io.IOException;
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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PageMapping mapping = PrettyConfigurationProvider.getFirstPage(request);
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
                response.sendRedirect(request.getContextPath() + mapping.getPermalink());
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
