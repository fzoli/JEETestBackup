package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import util.Strings;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="page-mappings")
@IdClass(PageMapping.Key.class)
public class PageMapping implements Serializable {
    
    @Id
    @ManyToOne
    @JoinColumn(name="page-id")
    private PageNode page;
    
    @Id
    @Column(name="language-code")
    private String languageCode;
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pretty-name")
    private String prettyName;

    public static class Key implements Serializable {
        
        private Long page;
        
        private String languageCode;

        public Key() {
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Key)) return false;
            Key other = (Key) object;
            return Node.equals(page, other.page) && Node.equals(languageCode, other.languageCode);
        }
        
        @Override
        public int hashCode() {
            if (page == null || languageCode == null) return 0;
            return 31 * (31 + page.hashCode()) + languageCode.hashCode();
        }
        
    }
    
    protected PageMapping() {
    }

    public PageMapping(PageNode page, String languageCode, String name) {
        this(page, languageCode, name, null);
    }
    
    public PageMapping(PageNode page, String languageCode, String name, String prettyName) {
        this.page = page;
        this.languageCode = languageCode;
        this.name = name;
        this.prettyName = prettyName;
    }

    public PageNode getPage() {
        return page;
    }
    
    public String getLanguageCode() {
        return languageCode;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getPrettyName() {
        return Strings.toPrettyString(prettyName == null ? name : prettyName);
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }
    
    public String getPermalink() {
        String link = Strings.join(getPage().getWay(true), "/", new Strings.Formatter<PageNode>() {

            @Override
            public String toString(PageNode node) {
                if (getLanguageCode() == null) return null;
                List<PageMapping> mappings = node.getMappings();
                if (mappings == null) return null;
                PageMapping pm = null;
                for (PageMapping mapping : mappings) {
                    if (mapping == null) continue;
                    if (getLanguageCode().equals(mapping.getLanguageCode())) {
                        pm = mapping;
                        break;
                    }
                }
                if (pm == null) return null;
                String pn = pm.getPrettyName();
                if (pn == null || pn.isEmpty()) return null;
                return pn;
            }
            
        });
        return link.startsWith("/") ? link : "/" + link;
    }
    
}
