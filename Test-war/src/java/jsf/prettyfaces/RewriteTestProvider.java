package jsf.prettyfaces;

import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.urlbuilder.AddressBuilder;
import util.UrlParameters;

/**
 *
 * @author zoli
 */
@RewriteConfiguration
public class RewriteTestProvider extends HttpConfigurationProvider {
    
    private static class CCondition implements Condition {

        private final String ruleClass;
        private final UrlParameters helper;
        
        public CCondition(UrlParameters helper, String ruleClass) {
            this.helper = helper;
            this.ruleClass = ruleClass;
        }
        
        private void removeClass(HttpOutboundServletRewrite event) {
            event.setOutboundAddress(AddressBuilder.create(helper.remove(event.getAddress().toString())));
        }
        
        @Override
        public boolean evaluate(Rewrite rwrt, EvaluationContext ec) {
            String classKey = helper.getKey();
            if (classKey != null && rwrt instanceof HttpOutboundServletRewrite) {
                HttpOutboundServletRewrite event = (HttpOutboundServletRewrite) rwrt;
                if (ruleClass == null) {
                    removeClass(event);
                    return true;
                }
                String eventClass = helper.get(event.getAddress().toString());
                if (eventClass == null) {
                    removeClass(event);
                    return true;
                }
                boolean enabled = eventClass.equals(ruleClass);
                if (enabled) {
                    removeClass(event);
                }
                return enabled;
            }
            return true;
        }
        
    }
    
    private static class LCondition extends CCondition {
        
        private static final UrlParameters helper = new UrlParameters(PrettyViewHandler.KEY_LANGUAGE);
        
        public LCondition(String ruleClass) {
            super(helper, ruleClass);
        }
        
    }
    
    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Configuration getConfiguration(final ServletContext context) {
        Condition conditionHu = new LCondition("hu");
        Condition conditionEn = new LCondition("en");
        Rule ruleHu = Join.path("/tigris").to("/faces/tiger.xhtml");
        Rule ruleEn = Join.path("/tiger").to("/faces/tiger.xhtml");
        return ConfigurationBuilder.begin()
            .addRule(ruleHu).when(conditionHu)
            .addRule(ruleEn).when(conditionEn);
    }
    
}
