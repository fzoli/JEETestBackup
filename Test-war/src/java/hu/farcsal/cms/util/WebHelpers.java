package hu.farcsal.cms.util;

import hu.farcsal.cms.entity.spec.Helpers;
import hu.farcsal.cms.prettyfaces.PrettyPageHelper;
import javax.servlet.ServletContext;

/**
 *
 * @author zoli
 */
public class WebHelpers {
    
    public static PrettyPageHelper getPageHelper(ServletContext ctx) {
        PrettyPageHelper helper = Helpers.getPageHelper(PrettyPageHelper.class);
        if (helper != null) return helper;
        return Helpers.initPageHelper(new PrettyPageHelper(ctx));
    }
    
}
