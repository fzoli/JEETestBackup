package entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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
    private Page page;
    
    @Id
    @OneToOne
    @JoinColumn(name="language-code")
    private Language language;
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pretty-name")
    private String prettyName;

    public static class Key implements Serializable {
        
        private Long page;
        
        private String language;

        protected Key() {
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof Key)) return false;
            Key other = (Key) object;
            return Objects.equals(page, other.page) && Objects.equals(language, other.language);
        }
        
        @Override
        public int hashCode() {
            if (page == null || language == null) return 0;
            return 31 * (31 + page.hashCode()) + language.hashCode();
        }
        
    }
    
    protected PageMapping() {
    }

    public PageMapping(Page page, Language language, String name) {
        this(page, language, name, null);
    }
    
    public PageMapping(Page page, Language language, String name, String prettyName) {
        this.page = page;
        this.language = language;
        this.name = name;
        this.prettyName = prettyName;
    }

    public Page getPage() {
        return page;
    }
    
    public Language getLanguage() {
        return language;
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
        String link = Strings.join(getPage().getWay(true), "/", new Strings.Formatter<Page>() {

            @Override
            public String toString(Page node) {
                if (getLanguage() == null || getLanguage().getCode() == null) return null;
                List<PageMapping> mappings = node.getMappings();
                if (mappings == null) return null;
                PageMapping pm = null;
                for (PageMapping mapping : mappings) {
                    if (mapping == null) continue;
                    if (getLanguage().equals(mapping.getLanguage())) {
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
