package hu.farcsal.cms.prettyfaces;

import com.ocpsoft.pretty.faces.spi.DevelopmentModeDetector;
import hu.farcsal.log.Log;
import javax.servlet.ServletContext;

/**
 *
 * @author zoli
 */
public class PrettyDevelopmentModeDetector implements DevelopmentModeDetector {

    private static final Log LOGGER = Log.getLogger(PrettyDevelopmentModeDetector.class);
    
    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public Boolean isDevelopmentMode(ServletContext servletContext) {
        LOGGER.i("pretty faces development mode: disabled");
        return false;
    }

}
