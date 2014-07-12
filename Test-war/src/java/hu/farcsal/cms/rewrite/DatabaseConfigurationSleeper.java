package hu.farcsal.cms.rewrite;

import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;

/**
 *
 * @author zoli
 */
class DatabaseConfigurationSleeper implements Condition {

    private boolean loading;
    
    @Override
    public boolean evaluate(Rewrite rwrt, EvaluationContext ec) {
        while (loading) {
            try { Thread.sleep(1); } catch (Exception ex) {}
        }
        return false;
    }

    void setLoading(boolean loading) {
        this.loading = loading;
    }
    
}
