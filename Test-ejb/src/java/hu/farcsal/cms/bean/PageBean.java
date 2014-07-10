package hu.farcsal.cms.bean;

import hu.farcsal.cms.entity.Language;
import hu.farcsal.cms.entity.Language_;
import hu.farcsal.cms.entity.Node;
import hu.farcsal.cms.entity.Node_;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.PageFilter;
import hu.farcsal.cms.entity.PageFilter_;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.Site;
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
    public void testPages() {
        
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
        
        List<Page> nodes = getPages(true);
        Page node = nodes.isEmpty() ? new Page("home.xhtml") : nodes.get(0);
        List<Page.Parameter> params = node.getParameters();
        if (params.size() >= 2) {
            Collections.swap(params, 0, 1); // TODO: it has no effect when persist
        }
        else {
            params.add(new Page.Parameter("value" + (params.size() + 1)));
        }
        manager.persist(node);
        
        if (node.getMappings() == null || node.getMappings().isEmpty()) {
            PageMapping mapping = new PageMapping(node, lang, "alma");
            manager.persist(mapping);
        }
        
    }

    @Override
    public Page getPageTree() {
        return new Page(getPages(false)) {

            @Override
            public String getInfo() {
                return "Root";
            }
            
        };
    }
    
    @Override
    public List<Language> getLanguages() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Language> query = builder.createQuery(Language.class);
        Root<Language> root = query.from(Language.class);
        query.orderBy(builder.asc(root.get(Language_.code)));
        return manager.createQuery(query).getResultList();
    }
    
    @Override
    public List<PageFilter> getPageFilters() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<PageFilter> query = builder.createQuery(PageFilter.class);
        Root<PageFilter> root = query.from(PageFilter.class);
        query.orderBy(builder.asc(root.get(PageFilter_.page)), builder.asc(root.get(PageFilter_.site)));
        return manager.createQuery(query).getResultList();
    }
    
    @Override
    public List<Site> getSites() {
        return getNodes(Site.class, true);
    }
    
    private List<Page> getPages(boolean listAll) {
        return getNodes(Page.class, listAll);
    }
    
    private <T extends Node> List<T> getNodes(Class<T> nodeClass, boolean listAll) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(nodeClass);
        Root<T> root = query.from(nodeClass);
        if (!listAll) query.where(builder.isNull(root.get(Node_.parent)));
        query.orderBy(builder.asc(root.get(Node_.id)));
        return manager.createQuery(query).getResultList();
    }
    
    @Override
    public void clearPagesFromCache() {
        clearFromCache(Page.class);
    }
    
    private void clearFromCache(Class<?> clazz) {
        manager.getEntityManagerFactory().getCache().evict(clazz);
    }
    
}
