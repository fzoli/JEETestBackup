package jsf.prettyfaces;

import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
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
    
    private static abstract class CJoin extends Join {
        
        private String classValue;
        private UrlParameters classHelper;
        
        protected CJoin(String pattern, boolean requestBinding) {
            super(pattern, requestBinding);
        }
        
        public CJoin withClass(String classValue) {
            this.classValue = classValue;
            return this;
        }
        
        @Override
        public CJoin to(String resource) {
            super.to(resource);
            return this;
        }
        
        public abstract String getClassKey();

        private UrlParameters getClassHelper() {
            if (classHelper != null) return classHelper;
            return classHelper = new UrlParameters(getClassKey());
        }
        
        public String getClassValue() {
            return classValue;
        }
        
        public String getClassValue(String url) {
            return getClassHelper().get(url);
        }
        
        public void removeClass(HttpOutboundServletRewrite event) {
            event.setOutboundAddress(AddressBuilder.create(getClassHelper().remove(event.getAddress().toString())));
        }
        
    }
    
    private static class LJoin extends CJoin {
        
        protected LJoin(String pattern, boolean requestBinding) {
            super(pattern, requestBinding);
        }

        @Override
        public String getClassKey() {
            return PrettyViewHandler.KEY_LANGUAGE;
        }
        
        public static LJoin path(final String pattern) {
            return new LJoin(pattern, true);
        }
        
    }
    
    private static class CCondition implements Condition {

        private final CJoin rule;
        
        public CCondition(CJoin rule) {
            this.rule = rule;
        }
        
        @Override
        public boolean evaluate(Rewrite rwrt, EvaluationContext ec) {
            String classKey = rule.getClassKey();
            if (classKey != null && rwrt instanceof HttpOutboundServletRewrite) {
                HttpOutboundServletRewrite event = (HttpOutboundServletRewrite) rwrt;
                String eventClass = rule.getClassValue(event.getAddress().toString());
                if (eventClass == null) {
                    rule.removeClass(event);
                    return true;
                }
                String ruleClass = rule.getClassValue();
                if (ruleClass == null) {
                    rule.removeClass(event);
                    return true;
                }
                boolean enabled = eventClass.equals(ruleClass);
                if (enabled) {
                    rule.removeClass(event);
                }
                return enabled;
            }
            return true;
        }
        
    }
    
    @Override
    public int priority() {
        return 0;
    }

    @Override
    public Configuration getConfiguration(final ServletContext context) {
        final CJoin ruleHu = LJoin.path("/tigris").to("/faces/tiger.xhtml").withClass("hu");
        final CJoin ruleEn = LJoin.path("/tiger").to("/faces/tiger.xhtml").withClass("en");
        return ConfigurationBuilder.begin()
            .addRule(ruleHu).when(new CCondition(ruleHu))
            .addRule(ruleEn).when(new CCondition(ruleEn));
    }
    
}
