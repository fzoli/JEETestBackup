package hu.farcsal.cms.entity;

import hu.farcsal.cms.entity.key.PrimaryObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="shops")
public class Shop extends PrimaryObject<Shop, Site> {
    
    @Id
    @OneToOne
    @JoinColumn(name="site-id")
    private Site site;

    @Column(name="name", nullable=false)
    private String name;
    
    @Column(name="address", nullable=false)
    private String address;
    
    protected Shop() {
        super(Shop.class);
    }
    
    public Shop(Site site) {
        this();
        this.site = site;
    }
    
    @Override
    protected Site getId() {
        return getSite();
    }

    public Site getSite() {
        return site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
}
