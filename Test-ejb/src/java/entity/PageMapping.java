package entity;

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
    
    public String getPrettyName(Page page) {
        if (getLanguage() == null || getLanguage().getCode() == null) return null;
        PageMapping pm = Page.findPageMapping(page, getLanguage().getCode(), false);
        if (pm == null) return null;
        String pn = pm.getPrettyName();
        if (pn == null || pn.isEmpty()) return null;
        return pn;
    }
    
    public String getPermalink() {
        String link = Strings.join(getPage().getWay(true), "/", FORMATTER);
        return link.startsWith("/") ? link : "/" + link;
    }
    
    private final transient Strings.Formatter<Page> FORMATTER = new Strings.Formatter<Page>() {

        @Override
        public String toString(Page page) {
            return getPrettyName(page);
        }

    };
    
}
