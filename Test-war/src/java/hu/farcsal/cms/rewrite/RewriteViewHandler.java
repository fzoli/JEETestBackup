package hu.farcsal.cms.rewrite;

import com.sun.faces.application.view.MultiViewHandler;
import hu.farcsal.cms.util.Faces;
import hu.farcsal.cms.util.Pages;
import java.util.Locale;
import javax.faces.context.FacesContext;

/**
 * @author zoli
 */
public class RewriteViewHandler extends MultiViewHandler {

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        String url = super.getActionURL(context, viewId);
        if (DatabaseRuleCache.findByViewId(viewId) != null)
            url = Pages.getLanguageParameter().set(url, context.getViewRoot().getLocale().getLanguage(), false);
        return url;
    }
    
    @Override
    public Locale calculateLocale(FacesContext context) {
        Locale locale = super.calculateLocale(context);
        String url = Faces.getRealRequestURI(context, true);
        DatabaseRuleCache cache = DatabaseRuleCache.findByUrl(url);
        return cache == null ? locale : cache.getPageMapping().getLanguage().getLocale(locale);
    }
    
}
