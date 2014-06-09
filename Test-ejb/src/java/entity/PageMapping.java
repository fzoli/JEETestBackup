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
    
    // a menürendszerben minden page mapping látható lesz mindaddig míg egy olyan oldal nem jön, aminek paramétere van
    // a praméterneveknek egyedinek kell lenniük egy útvonalon belül
    public String getPermalink(String url) {
        return getPermalink(url == null || url.isEmpty() ? SIMPLE_FORMATTER : new PageFormatter(this, url));
    }
    
    public String getPermalink(Strings.Formatter<Page> formatter) {
        String link = Strings.join(getPage().getWay(true), "/", formatter);
        return link.startsWith("/") ? link : "/" + link;
    }
    
    public static class PageFormatter implements Strings.Formatter<Page> {

        private final String url;
        private final PageMapping mapping;
        
        public PageFormatter(PageMapping mapping, String url) {
            this.url = url;
            this.mapping = mapping;
        }
        
        @Override
        public String toString(Page page) {
            String name = getPrettyName(page);
            if (name == null) return null;
            String param = getParameterString(page);
            if (param == null || param.isEmpty()) return name;
            return name + param;
        }
        
        protected String getParameterString(Page page) {
            String ps = "";
            List<Page.Parameter> params = page.getParameters();
            if (url != null && params != null && !params.isEmpty()) {
                int level = page.getLevel(true);
                int param = params.size();
                // /alma/korte/egy/ketto/barack/egy
                // alma: level 1, params 0, return ""
                // korte: level 2, params 2, return "egy/ketto"
                // barack: level 3, params 1, return "egy"
                // mélyebb pl: level 4, params X, exception
                int first = level - param + 1;
                int last = first + param - 1;
                int index = 0, count = 0;
                for (String value : url.split("/")) {
                    if (value.isEmpty()) continue;
                    index++;
                    if (index >= first && index <= last) {
                        count++;
                        ps += "/" + value;
                    }
                }
                if (count < param) throw new IllegalArgumentException(getPrettyName(page) + " lowlevel url");
            }
            return ps;
        }
        
        private String getPrettyName(Page page) {
            if (getLanguage() == null || getLanguage().getCode() == null) return null;
            PageMapping pm = Page.findPageMapping(page, getLanguage().getCode(), false);
            if (pm == null) return null;
            String pn = pm.getPrettyName();
            if (pn == null || pn.isEmpty()) return null;
            return pn;
        }
        
        private Language getLanguage() {
            if (mapping == null) return null;
            return mapping.getLanguage();
        }
        
    }
    
    private final transient Strings.Formatter<Page> SIMPLE_FORMATTER = new PageFormatter(this, null);
    
}
