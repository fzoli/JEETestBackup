package hu.farcsal.cms.entity.spec;

import hu.farcsal.cms.entity.Page;

/**
 *
 * @author zoli
 */
public class Helpers {
    
    public static PageHelper pageHelper;
    
    public static interface PageHelper {
        public String getFacesDir();
        public String getAppCtxPath();
        public String stripAppCtxFromUrl(String url);
        public String getRealViewPath(Page page, boolean withDir);
    }
    
}
