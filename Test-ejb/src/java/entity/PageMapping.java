package entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import util.Strings;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="page-mappings")
public class PageMapping extends NodeMapping<Page> {
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "pretty-name")
    private String prettyName;
    
    protected PageMapping() {
    }

    public PageMapping(Page page, Language language, String name) {
        this(page, language, name, null);
    }
    
    public PageMapping(Page page, Language language, String name, String prettyName) {
        super(page, language);
        this.name = name;
        this.prettyName = prettyName;
    }

    public Page getPage() {
        return getNode();
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
