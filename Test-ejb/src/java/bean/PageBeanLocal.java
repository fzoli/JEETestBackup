package bean;

import entity.PageNode;
import javax.ejb.Local;

/**
 *
 * @author zoli
 */
@Local
public interface PageBeanLocal {

    public void testPageNode();
    
    public PageNode getPageTree();
    
    public void refreshPageNodes();
    
}
