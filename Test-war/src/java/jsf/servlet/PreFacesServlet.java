package jsf.servlet;

import java.io.IOException;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zoli
 */
//@WebServlet(name = "Pre Faces Servlet", urlPatterns = {"/faces/*"}, loadOnStartup = 1)
public class PreFacesServlet extends HttpServlet {

    private final FacesServlet FACES_SERVLET = new FacesServlet();

    private void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        if (req.getParameter("value1") == null) resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequest(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        FACES_SERVLET.init(config);
        super.init();
    }

    @Override
    public ServletConfig getServletConfig() {
        return FACES_SERVLET.getServletConfig();
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        FACES_SERVLET.service(req, res);
        super.service(req, res);
    }

    @Override
    public String getServletInfo() {
        return FACES_SERVLET.getServletInfo();
    }

    @Override
    public void destroy() {
        FACES_SERVLET.destroy();
        super.destroy();
    }
    
}
