package jsf.prettyfaces;

import java.util.Iterator;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 *
 * @author zoli
 */
class Servlets {
    
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
    
}
