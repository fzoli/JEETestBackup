package entity;

import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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
    
    protected Site() {
        super();
    }

    protected Site(Site parent) {
        super(parent);
    }

    public List<Domain> getDomains() {
        return domains;
    }
    
}
