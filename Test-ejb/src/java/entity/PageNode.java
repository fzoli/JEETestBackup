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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import util.Strings;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="pages")
@DiscriminatorValue("page")
public class PageNode extends Node<PageNode> {
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pretty-name")
    private String prettyName;
    
    @ElementCollection
    @Column(name="name")
    @OrderColumn(name="index")
    @CollectionTable(
        name="page-params",
        joinColumns=@JoinColumn(name="page-id")
    )
    private List<String> parameters = new ArrayList<>();
    
    protected PageNode() {
    }

    public PageNode(String name) {
        this(null, name);
    }
    
    public PageNode(PageNode parent, String name) {
        super(parent);
        this.name = name;
    }
    
    protected PageNode(List<PageNode> children) {
        super(children);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getPrettyName() {
        return Strings.toPrettyString(prettyName == null ? name : prettyName);
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }
    
    public String getPermalink() {
        String link = Strings.join(getWay(true), "/", new Strings.Formatter<PageNode>() {

            @Override
            public String toString(PageNode node) {
                return node.getPrettyName();
            }
            
        });
        return link.startsWith("/") ? link : "/" + link;
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
