package entity;

import entity.key.PrimaryStrObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="domains")
public class Domain extends PrimaryStrObject<Domain> {
    
    @Id
    @Column(name = "domain", nullable = false)
    private String domain;
    
    @ManyToOne
    @JoinColumn(name = "site", nullable = false)
    private Site site;
    
    protected Domain() {
        super(Domain.class);
    }

    public Domain(Site site) {
        this();
        this.site = site;
    }
    
    @Override
    public String getId() {
        return domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
    
}
