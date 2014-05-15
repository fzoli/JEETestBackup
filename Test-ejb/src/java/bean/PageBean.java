package bean;

import entity.Node_;
import entity.PageNode;
import entity.PageNode_;
import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
    public void testPageNode() {
        List<PageNode> nodes = getPageNodes(true);
        PageNode node = nodes.isEmpty() ? new PageNode("test") : nodes.get(0);
        List<String> params = node.getParameters();
        if (params.size() >= 2) {
            Collections.swap(params, 0, 1);
        }
        else {
            params.add("alma" + (params.size() + 1));
        }
        manager.persist(node);
    }

    private List<PageNode> getPageNodes(boolean listAll) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<PageNode> query = builder.createQuery(PageNode.class);
        Root<PageNode> root = query.from(PageNode.class);
        if (!listAll) query.where(builder.isNull(root.get(Node_.parent)));
        query.orderBy(builder.asc(root.get(PageNode_.name)));
        return manager.createQuery(query).getResultList();
    }
    
    public static String toPrettyURL(String string) {
        return Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // normalize all characters and get rid of all diacritical marks (so that e.g. é, ö, à becomes e, o, a)
            .replaceAll("[^\\p{Alnum}]+", "-") // replace all remaining non-alphanumeric characters by - and collapse when necessary
            .replaceAll("[^a-z0-9]+$", "") // remove trailing punctuation
            .replaceAll("^[^a-z0-9]+", ""); // remove leading punctuation
    }
    
    public static void main(String[] args) {
        System.out.println(toPrettyURL("/öt szép szűzlány 1 őrült írót nyúz! <"));
    }
    
}
