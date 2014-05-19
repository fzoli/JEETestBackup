package jsf.prettyfaces;

import com.ocpsoft.pretty.faces.spi.DevelopmentModeDetector;
import javax.servlet.ServletContext;

/**
 *
 * @author zoli
 */
public class PrettyDevelopmentModeDetector implements DevelopmentModeDetector {

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public Boolean isDevelopmentMode(ServletContext servletContext) {
        System.out.println("pretty faces development mode: disabled");
        return false;
//        return System.getProperty("devmode", "false").equals("true");
    }

}
