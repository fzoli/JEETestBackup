package hu.farcsal.cms.rewrite;

import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;

/**
 *
 * @author zoli
 */
@RewriteConfiguration
public class ViewlessPageConfigurationProvider extends HttpConfigurationProvider {

    @Override
    public int priority() {
        return ConfigOrder.VIEWLESS_PAGE.getPriority();
    }
    
    @Override
    public Configuration getConfiguration(ServletContext context) {
        return ConfigurationBuilder.begin()
            .addRule()
            .when(Direction.isInbound())
            .perform(new ViewlessPageHandler());
    }

}
