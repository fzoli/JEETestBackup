package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.prettyfaces.PrettyPageHelper;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.cms.util.WebHelpers;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpCondition;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zoli
 */
@RewriteConfiguration
public class PurePageConfigurationProvider extends HttpConfigurationProvider {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PurePageConfigurationProvider.class);
    
    private static class PurePagePatternCondition extends HttpCondition {

        private final Pattern PATTERN;
        
        public PurePagePatternCondition(PrettyPageHelper helper) {
            PATTERN = Pattern.compile(String.format("^(http://[^/]+)*(%s)(%s/.*\\.xhtml(\\?.*)*)$", helper.getAppCtxPath(), helper.getFacesDir()), Pattern.CASE_INSENSITIVE);
        }

        @Override
        public boolean evaluateHttp(HttpServletRewrite hsr, EvaluationContext ec) {
            Matcher m = PATTERN.matcher(Pages.getRealRequestURI(hsr.getRequest(), false));
            if (m.matches()) {
                if (LOGGER.isInfoEnabled()) LOGGER.info("Page '{}' filtered", m.group(3));
                return true;
            }
            return false;
        }
        
    }
    
    @Override
    public int priority() {
        return ConfigOrder.PURE_PAGE_FILTER.getPriority();
    }
    
    @Override
    public Configuration getConfiguration(ServletContext t) {
        PrettyPageHelper helper = WebHelpers.getPageHelper(t);
        return ConfigurationBuilder.begin().addRule()
                .when(Direction.isInbound().and(new PurePagePatternCondition(helper)))
                .perform(Redirect.temporary(helper.getAppCtxPath() + "/"));
    }
    
}