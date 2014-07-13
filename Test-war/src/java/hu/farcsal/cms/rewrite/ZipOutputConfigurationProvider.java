package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.rewrite.transform.TidyHtmlTransformer;
import hu.farcsal.util.WebConfig;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.Header;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.RequestParameter;
import org.ocpsoft.rewrite.servlet.config.Response;

/**
 *
 * @author zoli
 */
@RewriteConfiguration
public class ZipOutputConfigurationProvider extends HttpConfigurationProvider {

    private static final String PARAM_COMPRESSING = "hu.farcsal.cms.rewrite.GZIP_COMPRESSING";
    
    private static final String PARAM_NOGZIP = "nogzip", PARAM_TIDY = "tidy";
    
    @Override
    public int priority() {
        return ConfigOrder.ZIP_OUTPUT.getPriority();
    }
    
    private static void initProvider(ServletContext t) {
        TidyHtmlTransformer.setConfiguration(t.getRealPath("/WEB-INF/jtidy.properties"));
    }
    
    @Override
    public Configuration getConfiguration(ServletContext t) {
        initProvider(t);
        ConfigurationBuilder cfg = ConfigurationBuilder.begin();
        if (WebConfig.isTrue(t, PARAM_COMPRESSING)) {
            cfg.addRule()
                .when(Direction.isInbound().and(RequestParameter.exists(PARAM_TIDY)))
                .perform(TidyHtmlTransformer.create());
            
            cfg.addRule()
                .when(Direction.isInbound().and(Header.matches("{Accept-Encoding}", "{gzip}")).andNot(RequestParameter.exists(PARAM_NOGZIP)).andNot(RequestParameter.exists(PARAM_TIDY)))
                .perform(Response.gzipStreamCompression())
                .where("Accept-Encoding").matches("(?i)Accept-Encoding")
                .where("gzip").matches("(?i).*\\bgzip\\b.*");
        }
        return cfg;
    }

}
