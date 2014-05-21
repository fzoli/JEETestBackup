package bean;

import entity.Page;
import javax.ejb.Local;

/**
 *
 * @author zoli
 */
@Local
public interface PageBeanLocal {

    public void testPageNode();
    
    public Page getPageTree();
    
    public void clearPagesFromCache();
    
}
