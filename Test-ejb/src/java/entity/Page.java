package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="pages")
@DiscriminatorValue("page")
public class Page extends Node<Page, PageMapping> {
    
    @ElementCollection
    @Column(name="name", nullable = false)
    @OrderColumn(name="index", nullable = false)
    @CollectionTable(
        name="page-params",
        joinColumns=@JoinColumn(name="page-id", nullable = false)
    )
    private List<String> parameters = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name="site-pages",
        joinColumns={@JoinColumn(name="page-id", referencedColumnName="id")},
        inverseJoinColumns={@JoinColumn(name="site-id", referencedColumnName="id")})
    private List<Site> sites = new ArrayList<>();
    
    @Column(name="view-path", nullable=false)
    private String viewPath;
    
    protected Page() {
    }
    
    public Page(String viewPath) {
        this((Page) null, viewPath);
    }
    
    public Page(Page parent, String viewPath) {
        super(parent);
        this.viewPath = viewPath;
    }
    
    protected Page(List<Page> children) {
        super(children);
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<Site> getSites() {
        return sites;
    }
    
    public String getViewPath() {
        return getViewPath(null);
    }
    
    public String getViewPath(String root) {
        if (viewPath == null) return null;
        String view = viewPath.trim();
        if (view.isEmpty()) return null;
        if (root != null && !view.startsWith("/")) return root + (root.endsWith("/") ? "" : "/") + view;
        return view;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
    
    public List<Page> getWay(boolean fromRoot) {
        Page node = this;
        List<Page> way = new ArrayList<>();
        while (!node.isRoot()) {
            way.add(node);
            node = node.getParent();
        }
        way.add(node);
        if (fromRoot) Collections.reverse(way);
        return way;
    }
    
    public Site findSite(String domain) {
        Site found = null;
        if (sites != null && !sites.isEmpty()) {
            for (Site site : sites) {
                for (Domain d : site.getDomains()) {
                    if (d.getDomain().equalsIgnoreCase(domain)) {
                        found = site;
                        break;
                    }
                }
            }
        }
        return found;
    }
    
}
