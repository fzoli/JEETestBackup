package entity;

import entity.spec.NodeObject;
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
public class Node<NodeType extends Node> implements NodeObject<NodeType> {
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="parent-id", nullable = true)
    private Node parent;
    
    @OneToMany(mappedBy = "parent")
    private List<Node> children;

    public Node() {
        this((NodeType) null);
    }

    public Node(NodeType parent) {
        this(parent, null);
    }
    
    protected Node(List<NodeType> children) {
        this(null, children);
    }
    
    private Node(NodeType parent, List<NodeType> children) {
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
    public String toString() {
        return getInfo();
    }
    
    public String getInfo() {
        return "Node(id=" + id + ", parent=" + (parent == null ? "null" : parent.getId()) + ")";
    }
    
//    @Override
//    public int hashCode() {
//        int hash = 0;
//        hash += (id != null ? id.hashCode() : 0);
//        return hash;
//    }
//
//    @Override
//    public boolean equals(Object object) {
//        // TODO: Warning - this method won't work in the case the id fields are not set
//        if (!(object instanceof Node)) {
//            return false;
//        }
//        Node other = (Node) object;
//        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
//            return false;
//        }
//        return true;
//    }
    
}
