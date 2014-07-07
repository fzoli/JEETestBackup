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
    
    private static class ParameterCondition implements Condition {

        private final String value;
        private final UrlParameters helper;
        
        public ParameterCondition(UrlParameters helper, String value) {
            this.helper = helper;
            this.value = value;
        }
        
        @Override
        public boolean evaluate(Rewrite rwrt, EvaluationContext ec) {
            if (helper.getKey() != null && rwrt instanceof HttpOutboundServletRewrite) {
                HttpOutboundServletRewrite event = (HttpOutboundServletRewrite) rwrt;
                if (value == null) {
                    removeParameter(event);
                    return true;
                }
                String eventClass = helper.get(event.getAddress().toString());
                if (eventClass == null) {
                    removeParameter(event);
                    return true;
                }
                boolean enabled = eventClass.equals(value);
                if (enabled) {
                    removeParameter(event);
                }
                return enabled;
            }
            return true;
        }
        
        private void removeParameter(HttpOutboundServletRewrite event) {
            event.setOutboundAddress(AddressBuilder.create(helper.remove(event.getAddress().toString())));
        }
        
    }
    
    private static class LngCondition extends ParameterCondition {
        
        private static final UrlParameters helper = new UrlParameters(PrettyViewHandler.KEY_LANGUAGE);
        
        public LngCondition(String value) {
            super(helper, value);
        }
        
    }
    
    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Configuration getConfiguration(final ServletContext context) {
        Condition conditionHu = new LngCondition("hu");
        Condition conditionEn = new LngCondition("en");
        Rule ruleHu = Join.path("/tigris").to("/faces/tiger.xhtml");
        Rule ruleEn = Join.path("/tiger").to("/faces/tiger.xhtml");
        return ConfigurationBuilder.begin()
            .addRule(ruleHu).when(conditionHu)
            .addRule(ruleEn).when(conditionEn);
    }
    
}
