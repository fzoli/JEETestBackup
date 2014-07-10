package hu.farcsal.cms.entity;

import hu.farcsal.cms.entity.key.PrimaryLongObject;
import hu.farcsal.cms.entity.spec.NodeObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="nodes")
@DiscriminatorValue("node")
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.JOINED)
public class Node<NodeType extends Node, MappingType extends NodeMapping> extends PrimaryLongObject<Node> implements NodeObject<NodeType> {
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="parent-id", nullable = true)
    private Node parent;
    
    @OneToMany(mappedBy = "parent")
    private List<Node> children;
    
    @OneToMany(mappedBy = "node")
    private List<NodeMapping> mappings;
    
    @Column(name = "priority", nullable = false)
    private int priority = 0;
    
    @Column(name = "disabled", nullable = false)
    private boolean disabled;
    
    protected Node() {
        this((NodeType) null);
    }

    public Node(NodeType parent) {
        this(parent, null);
    }
    
    protected Node(List<NodeType> children) {
        this(null, children);
    }
    
    private Node(NodeType parent, List<NodeType> children) {
        super(Node.class);
        this.parent = parent;
        this.children = (List<Node>) children;
    }
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isRoot() {
        return getParent() == null;
    }
    
    @Override
    public boolean isChildAvailable() {
        List<NodeType> children = getChildren();
        return children != null && !children.isEmpty();
    }
    
    @Override
    public NodeType getParent() {
        return (NodeType) parent;
    }
    
    @Override
    public List<NodeType> getChildren() {
        return (List<NodeType>) children;
    }
    
    @Override
    public List<NodeType> getOrderedChildren() {
        List<NodeType> l = getChildren();
        if (l != null) {
            l = new ArrayList<>(l);
            Collections.sort(l, COMPARATOR);
        }
        return l;
    }
    
    public List<MappingType> getMappings() {
        return (List<MappingType>) mappings;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    public boolean isDisabled(boolean withParents) {
        if (!withParents) return isDisabled();
        for (NodeType n : getWay(false)) {
            if (n.isDisabled()) return true;
        }
        return false;
    }
    
    public List<NodeType> getWay(boolean fromRoot) {
        NodeType node = (NodeType) this;
        List<NodeType> way = new ArrayList<>();
        while (!node.isRoot()) {
            way.add(node);
            node = (NodeType) node.getParent();
        }
        way.add(node);
        if (fromRoot) Collections.reverse(way);
        return way;
    }
    
    @Override
    public String toString() {
        return getInfo();
    }
    
    public String getInfo() {
        return "Node(id=" + getId() + ", parent=" + (parent == null ? "null" : parent.getId()) + ")";
    }
    
    private final transient Comparator<NodeType> COMPARATOR = new Comparator<NodeType>() {

        @Override
        public int compare(NodeType o1, NodeType o2) {
            if (o1 == null && o2 == null) return 0;
            if (o1 == null && o2 != null) return -1;
            if (o1 != null && o2 == null) return 1;
            return Integer.compare(o1.getPriority(), o2.getPriority());
        }

    };
    
}
