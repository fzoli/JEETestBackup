package hu.farcsal.cms.entity;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="sites")
@DiscriminatorValue("site")
public class Site extends Node<Site, SiteMapping> {
    
    @OneToMany(mappedBy = "site")
    private List<Domain> domains;
    
    @OneToMany(mappedBy = "site")
    private List<PageFilter> pageFilters;
    
    @JoinColumn(name="home-page", nullable = true)
    private Page homePage;
    
    @JoinColumn(name="def-lang", nullable = true)
    private Language defLanguage;
    
    protected Site() {
        super();
    }

    protected Site(Site parent) {
        super(parent);
    }

    public Page getHomePage() {
        return homePage;
    }

    public void setHomePage(Page homePage) {
        this.homePage = homePage;
    }

    public Language getDefLanguage() {
        return defLanguage;
    }

    public void setDefLanguage(Language defLanguage) {
        this.defLanguage = defLanguage;
    }

    public List<Domain> getDomains() {
        return domains;
    }
    
    public List<PageFilter> getPageFilters() {
        return pageFilters;
    }
    
    public static Site findSiteByDomain(List<Site> sites, String domain) {
        if (sites != null && domain != null) {
            for (Site site : sites) {
                if (site == null || site.getDomains() == null) continue;
                Domain d = Domain.findDomain(site.getDomains(), domain);
                if (d != null) return site;
            }
        }
        return null;
    }
    
}
