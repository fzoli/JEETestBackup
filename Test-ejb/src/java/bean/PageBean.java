package bean;

import entity.Language;
import entity.Language_;
import entity.Node_;
import entity.PageMapping;
import entity.Page;
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
        
        List<Language> langs = getLanguages();
        Language lang;
        if (langs.isEmpty()) {
            lang = new Language("hu", "Magyar");
            manager.persist(lang);
        }
        else {
            lang = langs.get(0);
        }
        
        clearPagesFromCache();
        
        List<Page> nodes = getPageNodes(true);
        Page node = nodes.isEmpty() ? new Page("home.xhtml") : nodes.get(0);
        List<String> params = node.getParameters();
        if (params.size() >= 2) {
            Collections.swap(params, 0, 1);
        }
        else {
            params.add("value" + (params.size() + 1));
        }
        manager.persist(node);
        
        if (node.getMappings() == null || node.getMappings().isEmpty()) {
            PageMapping mapping = new PageMapping(node, lang, "alma");
            manager.persist(mapping);
        }
        
    }

    @Override
    public Page getPageTree() {
        return new Page(getPageNodes(false)) {

            @Override
            public String getInfo() {
                return "Root";
            }
            
        };
    }
    
    private List<Language> getLanguages() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Language> query = builder.createQuery(Language.class);
        Root<Language> root = query.from(Language.class);
        query.orderBy(builder.asc(root.get(Language_.code)));
        return manager.createQuery(query).getResultList();
    }
    
    private List<Page> getPageNodes(boolean listAll) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Page> query = builder.createQuery(Page.class);
        Root<Page> root = query.from(Page.class);
        if (!listAll) query.where(builder.isNull(root.get(Node_.parent)));
        query.orderBy(builder.asc(root.get(Node_.id)));
        return manager.createQuery(query).getResultList();
    }
    
    @Override
    public void clearPagesFromCache() {
        manager.getEntityManagerFactory().getCache().evict(Page.class);
    }
    
}
