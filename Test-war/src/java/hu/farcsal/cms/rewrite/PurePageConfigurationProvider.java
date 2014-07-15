package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.prettyfaces.PrettyPageHelper;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.cms.util.WebHelpers;
import hu.farcsal.util.Servlets;
import java.util.List;
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
    
    private static final String[] WHITE_LIST = {
        
    };
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PurePageConfigurationProvider.class);
    
    private static class PurePagePatternCondition extends HttpCondition {

        private final Pattern PATTERN;
        private final List<String> ERROR_PAGES;
        
        public PurePagePatternCondition(PrettyPageHelper helper, List<String> errorPages) {
            //  [1             ] [2        ] [3 [4     ]   [5 [6        ]  ] ]
            // ^(https?://[^/]+)?(/Test-war) (  (/faces)?/?(  (.+\.xhtml).*)?)$
            PATTERN = Pattern.compile(String.format("^(https?://[^/]+)?(%s)((%s)?/?((.+\\.xhtml).*)?)$", helper.getAppCtxPath(), helper.getFacesDir()), Pattern.CASE_INSENSITIVE);
            ERROR_PAGES = errorPages;
        }

        @Override
        public boolean evaluateHttp(HttpServletRewrite hsr, EvaluationContext ec) {
            Matcher m = PATTERN.matcher(Pages.getRealRequestURI(hsr.getRequest(), false));
            if (m.matches()) {
                String req = m.group(3);
                if (req.isEmpty() || req.equals("/")) {
                    LOGGER.debug("Homepage '{}' enabled", req);
                    return false;
                }
                String dir = m.group(6);
                if (dir != null) {
                    for (String s : WHITE_LIST) {
                        if (dir.startsWith(s)) {
                            LOGGER.info("Page '{}' enabled by whitelist", req);
                            return false;
                        }
                    }
                }
                if (ERROR_PAGES.contains(req)) {
                    LOGGER.debug("Errorpage '{}' enabled", req);
                    return false;
                }
                LOGGER.info("Page '{}' filtered", req);
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
        List<String> errorPages = Servlets.getErrorPages(t);
        PrettyPageHelper helper = WebHelpers.getPageHelper(t);
        return ConfigurationBuilder.begin().addRule()
                .when(Direction.isInbound().and(new PurePagePatternCondition(helper, errorPages)))
                .perform(Redirect.temporary(helper.getAppCtxPath() + "/"));
    }
    
}
