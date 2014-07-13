package hu.farcsal.cms.prettyfaces;

import com.ocpsoft.pretty.faces.spi.DevelopmentModeDetector;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zoli
 */
public class PrettyDevelopmentModeDetector implements DevelopmentModeDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrettyDevelopmentModeDetector.class);
    
    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public Boolean isDevelopmentMode(ServletContext servletContext) {
        LOGGER.info("pretty faces development mode: disabled");
        return false;
    }

}
