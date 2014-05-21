package entity;

import entity.key.PrimaryLongObject;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="sites")
public class Site extends PrimaryLongObject {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @OneToMany(mappedBy = "site")
    private List<Domain> domains;
    
    protected Site() {
        super(Site.class);
    }

    public Site(String title) {
        this();
        this.title = title;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Domain> getDomains() {
        return domains;
    }
    
}
