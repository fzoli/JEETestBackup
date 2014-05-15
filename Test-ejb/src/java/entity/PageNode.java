package entity;

import java.text.Normalizer;
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
@Table(name="pages")
@DiscriminatorValue("page")
public class PageNode extends Node<PageNode> {
    
    @Column(name = "name", nullable = false)
    private String name;

    @ElementCollection
    @Column(name="name")
    @OrderColumn(name="index")
    @CollectionTable(
        name="page-params",
        joinColumns=@JoinColumn(name="page-id")
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
    
    public static String toPrettyURLString(String string) {
        return Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // normalize all characters and get rid of all diacritical marks (so that e.g. é, ö, à becomes e, o, a)
            .replaceAll("[^\\p{Alnum}]+", "-") // replace all remaining non-alphanumeric characters by - and collapse when necessary
            .replaceAll("[^a-z0-9]+$", "") // remove trailing punctuation
            .replaceAll("^[^a-z0-9]+", ""); // remove leading punctuation
    }
    
}
