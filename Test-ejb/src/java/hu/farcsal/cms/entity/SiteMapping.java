package hu.farcsal.cms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="site-mappings")
public class SiteMapping extends NodeMapping<Site> {

    @Column(name = "title", nullable = false)
    private String title;
    
    protected SiteMapping() {
    }

    public SiteMapping(Site site, Language language, String title) {
        super(site, language);
        this.title = title;
    }
    
    public Site getSite() {
        return getNode();
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
}
