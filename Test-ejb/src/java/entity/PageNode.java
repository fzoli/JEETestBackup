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
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="pages")
@DiscriminatorValue("page")
public class PageNode extends Node<PageNode> {
    
    @ElementCollection
    @Column(name="name")
    @OrderColumn(name="index")
    @CollectionTable(
        name="page-params",
        joinColumns=@JoinColumn(name="page-id")
    )
    private List<String> parameters = new ArrayList<>();
    
    @OneToMany(mappedBy = "page")
    private List<PageMapping> mappings;
    
    @Column(name="view-path", nullable=false)
    private String viewPath;
    
    protected PageNode() {
    }
    
    public PageNode(String viewPath) {
        this((PageNode) null, viewPath);
    }
    
    public PageNode(PageNode parent, String viewPath) {
        super(parent);
        this.viewPath = viewPath;
    }
    
    protected PageNode(List<PageNode> children) {
        super(children);
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<PageMapping> getMappings() {
        return mappings;
    }

    public String getViewPath() {
        return viewPath;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
    
    public List<PageNode> getWay(boolean fromRoot) {
        PageNode node = this;
        List<PageNode> way = new ArrayList<>();
        while (!node.isRoot()) {
            way.add(node);
            node = node.getParent();
        }
        way.add(node);
        if (fromRoot) Collections.reverse(way);
        return way;
    }
    
}
