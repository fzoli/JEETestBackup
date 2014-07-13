package hu.farcsal.cms.rewrite;

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.servlet.config.HttpCondition;
import org.ocpsoft.rewrite.servlet.http.event.HttpInboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;

/**
 *
 * @author zoli
 */
public class InboundPageFilter extends HttpCondition {

    public static interface FilterCondition {
        public boolean evaluate (HttpInboundServletRewrite rwrt, EvaluationContext ec);
    }
    
    private final FilterCondition helper;
    
    public InboundPageFilter(FilterCondition helper) {
        this.helper = helper;
    }
    
    @Override
    public boolean evaluateHttp(HttpServletRewrite hsr, EvaluationContext ec) {
        if (hsr instanceof HttpInboundServletRewrite) {
            return helper.evaluate((HttpInboundServletRewrite) hsr, ec);
        }
        return true;
    }
    
}
