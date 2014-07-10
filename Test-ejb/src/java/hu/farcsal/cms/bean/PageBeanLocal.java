package hu.farcsal.cms.bean;

import hu.farcsal.cms.entity.Language;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.PageFilter;
import hu.farcsal.cms.entity.Site;
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
