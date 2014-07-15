package hu.farcsal.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author zoli
 */
public class Servlets {
    
    public static String getMappingDir(ServletContext sc, Class<? extends Servlet> servletClass) {
        return getMapping(sc, servletClass, "/", "/*", true);
    }
    
    public static String getMapping(ServletContext sc, Class<? extends Servlet> servletClass, String mapStart, String mapEnd, boolean trimEnd) {
        String mapping = null;
        Iterator it = sc.getServletRegistrations().entrySet().iterator();
        mapFinder : while (it.hasNext()) {
            Map.Entry<String, ? extends ServletRegistration> e = (Map.Entry) it.next();
            ServletRegistration reg = e.getValue();
            if (servletClass.getCanonicalName().equals(reg.getClassName())) {
                for (String map : reg.getMappings()) {
                    if ((mapStart == null || map.startsWith(mapStart)) && (mapEnd == null || map.endsWith(mapEnd))) {
                        mapping = map;
                        if (trimEnd && mapEnd != null) {
                            mapping = mapping.substring(0, mapping.length() - mapEnd.length());
                        }
                        break mapFinder;
                    }
                }
            }
        }
        return mapping;
    }
    
    public static List<String> getErrorPages(ServletContext sc) {
        List<String> l = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(sc.getRealPath("/WEB-INF/web.xml"));
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("error-page");
            for (int i = 0; i < nodes.getLength(); i++) {
                    Node n = nodes.item(i);
                    if (!n.hasChildNodes()) continue;
                    NodeList children = n.getChildNodes();
                    for (int j = 0; j < children.getLength(); j++) {
                        n = children.item(j);
                        if ("location".equals(n.getNodeName())) {
                            l.add(n.getTextContent());
                            break;
                        }
                    }
            }
        }
        catch (Exception ex) {
            ;
        }
        return l;
    }
    
}
