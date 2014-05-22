package bean;

import entity.Language;
import entity.Page;
import entity.PageFilter;
import entity.Site;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author zoli
 */
@Local
public interface PageBeanLocal {
    
    public Page getPageTree();
    
    public List<Site> getSites();
    
    public List<Language> getLanguages();
    
    public List<PageFilter> getPageFilters();
    
    public void clearPagesFromCache();
    
    public void testPages();
    
}
