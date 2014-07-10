package hu.farcsal.cms.entity;

import hu.farcsal.cms.entity.key.PrimaryStringObject;
import java.util.List;
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
    @JoinColumn(name = "site-id", nullable = false)
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
    
    public static Domain findDomain(List<Domain> domains, String domain) {
        if (domains != null && domain != null) {
            for (Domain d : domains) {
                if (d == null || d.getDomain() == null) continue;
                if (d.getDomain().equalsIgnoreCase(domain)) {
                    return d;
                }
                else if (d.getDomain().startsWith("*.")) {
                    String s = d.getDomain().substring(2).toLowerCase();
                    if (domain.toLowerCase().endsWith(s)) {
                        return d;
                    }
                }
            }
        }
        return null;
    }
    
}
