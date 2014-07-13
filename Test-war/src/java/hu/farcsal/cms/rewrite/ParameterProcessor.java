package hu.farcsal.cms.rewrite;

import hu.farcsal.util.UrlParameters;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.config.HttpCondition;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * 
 * @author zoli
 */
class ParameterProcessor extends HttpCondition implements Operation {

    private final String value;
    private final UrlParameters helper;

    public ParameterProcessor(UrlParameters helper, String value) {
        this.helper = helper;
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    private boolean isOutbound(Rewrite rwrt) {
        return helper.getKey() != null && rwrt instanceof HttpOutboundServletRewrite;
    }
    
    @Override
    public boolean evaluateHttp(HttpServletRewrite rwrt, EvaluationContext ec) {
        if (isOutbound(rwrt)) {
            HttpOutboundServletRewrite event = (HttpOutboundServletRewrite) rwrt;
            if (value == null) return true;
            String eventValue = helper.get(event.getAddress().toString());
            if (eventValue == null) return true;
            return eventValue.equals(value);
        }
        return true;
    }

    @Override
    public void perform(Rewrite rwrt, EvaluationContext ec) {
        if (isOutbound(rwrt)) {
            removeParameter((HttpOutboundServletRewrite) rwrt);
        }
    }
    
    private void removeParameter(HttpOutboundServletRewrite event) {
        event.setOutboundAddress(AddressBuilder.create(helper.remove(event.getAddress().toString())));
    }

}
