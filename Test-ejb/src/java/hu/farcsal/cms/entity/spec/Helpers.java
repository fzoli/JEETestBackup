package hu.farcsal.cms.entity.spec;

import hu.farcsal.cms.entity.Page;

/**
 *
 * @author zoli
 */
public class Helpers {
    
    private static final Object syn = new Object();
    
    private static PageHelper pageHelper;
    
    public static interface PageHelper {
        public String getFacesDir();
        public String getAppCtxPath();
        public String stripAppCtxFromUrl(String url);
        public String getRealViewPath(Page page, boolean withDir);
    }
    
    public static <T extends PageHelper> T initPageHelper(T helper) {
        synchronized (syn) {
            if (pageHelper == null) pageHelper = helper;
        }
        return helper;
    }
    
    public static <T extends PageHelper> T getPageHelper(Class<T> type) {
        synchronized (syn) {
            if (type.isInstance(pageHelper)) {
                return type.cast(pageHelper);
            }
            return null;
        }
    }
    
    public static PageHelper getPageHelper() {
        synchronized (syn) {
            return pageHelper;
        }
    }
    
}
