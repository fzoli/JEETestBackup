package entity;

import entity.key.PrimaryStringObject;
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
public class Domain extends PrimaryStringObject<Domain> {
    
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
    protected String getId() {
        return getDomain();
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
