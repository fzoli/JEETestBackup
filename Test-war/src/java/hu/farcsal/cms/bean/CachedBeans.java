package hu.farcsal.cms.bean;

/**
 *
 * @author zoli
 */
public class CachedBeans {
    
    private static PageBeanLocal pageBean;
    
    public static PageBeanLocal getPageBeanLocal() {
        if (pageBean != null) return pageBean;
        return pageBean = Beans.lookupPageBeanLocal();
    }
    
}
