package hu.farcsal.cms.rewrite;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.urlbuilder.AddressBuilder;
import hu.farcsal.util.UrlParameters;

/**
 *
 * @author zoli
 */
class ParameterCondition implements Condition {

    private final String value;
    private final UrlParameters helper;

    public ParameterCondition(UrlParameters helper, String value) {
        this.helper = helper;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean evaluate(Rewrite rwrt, EvaluationContext ec) {
        if (helper.getKey() != null && rwrt instanceof HttpOutboundServletRewrite) {
            HttpOutboundServletRewrite event = (HttpOutboundServletRewrite) rwrt;
            if (value == null) {
                removeParameter(event);
                return true;
            }
            String eventValue = helper.get(event.getAddress().toString());
            if (eventValue == null) {
                removeParameter(event);
                return true;
            }
            boolean enabled = eventValue.equals(value);
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
