package entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="`pages`")
@DiscriminatorValue("page")
public class PageNode extends Node<PageNode> {
    
    @Column(name = "`name`", nullable = false)
    private String name;

    @ElementCollection
    @Column(name="`param-name`")
    @OrderColumn(name="`index`")
    @CollectionTable(
        name="`page-parameters`",
        joinColumns=@JoinColumn(name="`page-id`")
    )
    private List<String> parameters = new ArrayList();
    
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

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
    
}
