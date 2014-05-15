package bean;

import entity.PageNode;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author zoli
 */
@Stateless
public class PageBean implements PageBeanLocal {

    @PersistenceContext(unitName = "Test-ejbPU")
    private EntityManager manager;
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void test() {
        PageNode node = new PageNode("test");
        node.getParameters().add("alma");
        manager.persist(node);
    }

}
