package hu.farcsal.cms.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author zoli
 * @param <NodeType>
 */
@Entity
@Table(name="node-mappings")
@IdClass(NodeMapping.Key.class)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class NodeMapping<NodeType extends Node> implements Serializable {
    
    @Id
    @ManyToOne
    @JoinColumn(name="node-id")
    private Node node;
    
    @Id
    @OneToOne
    @JoinColumn(name="language-code")
    private Language language;
    
    public static class Key implements Serializable {
        
        private Long node;
        
        private String language;

        protected Key() {
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof Key)) return false;
            Key other = (Key) object;
            return Objects.equals(node, other.node) && Objects.equals(language, other.language);
        }
        
        @Override
        public int hashCode() {
            if (node == null || language == null) return 0;
            return 31 * (31 + node.hashCode()) + language.hashCode();
        }
        
    }
    
    protected NodeMapping() {
    }

    public NodeMapping(Node node, Language language) {
        this.node = node;
        this.language = language;
    }

    public NodeType getNode() {
        return (NodeType) node;
    }
    
    public Language getLanguage() {
        return language;
    }
    
}
