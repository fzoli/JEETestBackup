package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.bean.CachedBeans;
import hu.farcsal.cms.util.WebHelpers;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;

/**
 *
 * @author zoli
 */
@RewriteConfiguration
public class HomePageConfigurationProvider extends HttpConfigurationProvider {

    @Override
    public int priority() {
        return ConfigOrder.HOME_PAGE.getPriority();
    }
    
    @Override
    public Configuration getConfiguration(ServletContext context) {
        return ConfigurationBuilder.begin()
            .addRule()
            .when(Direction.isInbound().and(Path.matches("/")))
            .perform(new HomePageHandler(context, WebHelpers.getPageHelper(context), CachedBeans.getPageBeanLocal().getSites()));
    }

}
