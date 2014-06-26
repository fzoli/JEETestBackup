package entity;

import entity.spec.Helpers;
import java.util.ArrayList;
import java.util.Arrays;
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
    public String getPermalink(String url) throws Exception {
        return getPermalink(url == null || url.isEmpty() ? SIMPLE_FORMATTER : new PageFormatter(this, url));
    }
    
    // returns null if the path is broken!
    public String getPermalink(Strings.Formatter<Page> formatter) {
        String link = Strings.join(getPage().getWay(true), "/", formatter);
        if (link == null) return null;
        return link.startsWith("/") ? link : "/" + link;
    }
    
    public static class PageFormatter implements Strings.Formatter<Page> {

        private final String url;
        protected final PageMapping mapping;
        
        public PageFormatter(PageMapping mapping, String url) {
            this.url = stripAppCtxFromUrl(url);
            this.mapping = mapping;
        }
        
        private String stripAppCtxFromUrl(String url) {
            try {
                return Helpers.pageHelper.stripAppCtxFromUrl(url);
            }
            catch (Exception ex) {
                // do not strip if the helper has not been set yet (e.g. during deploy)
                return url;
            }
        }
        
        @Override
        public String toString(Page page) {
            String name = getPrettyName(page, true);
            if (name == null) return null;
            String param = getParameterString(page);
            if (param == null || param.isEmpty()) return name;
            return name + param;
        }
        
        protected String getParameterString(Page page) {
            String ps = "";
            List<Page.Parameter> params = page.getParameters();
            if ((url == null || url.isEmpty()) && !(params == null || params.isEmpty()) && !page.isParameterIncremented()) {
                throw new RuntimeException("url is not specified");
            }
            if (url != null && params != null && !params.isEmpty()) {

                // /alma/korte/egy/ketto/barack/egy
                // alma: level 1, params 0, return ""
                // korte: level 2, params 2, return "egy/ketto"
                // barack: level 3, params 1, return "egy"
                // mélyebb pl: level 4, params X, exception
                
                List<String> vals = new ArrayList<>(Arrays.asList(url.split("/")));
                if (!vals.isEmpty() && vals.get(0).isEmpty()) vals.remove(0);
                
                List<Page> ways = page.getWay(true);
                for (Page way : ways) {
                    String n = getPrettyName(way, false);
                    if (vals.isEmpty()) return throwException(way, "", new RuntimeException("lowlevel url"));
                    String v = vals.get(0);
                    vals.remove(0);
                    if (!n.equals(v)) return throwException(way, "", new RuntimeException("different url"));
                    for (int i = 0; i < way.getParameters().size(); i++) {
                        if (!vals.isEmpty()) {
                            if (way == page) ps +=  "/" + vals.get(0);
                            vals.remove(0);
                        }
                        else {
                            return throwException(way, ps, new RuntimeException("not enought parameters"));
                        }
                    }
                }
                
            }
            return ps;
        }
        
        private String throwException(Page way, String ret, RuntimeException ex) {
            boolean lastWay = mapping.getPage() == way;
            if (lastWay) {
                if (!way.isParameterIncremented()) throw ex;
            }
            else {
                throw ex;
            }
            return ret;
        }
        
        private String getPrettyName(Page page, boolean allowIncrementedParams) {
            if (getLanguage() == null || getLanguage().getCode() == null) return null;
            PageMapping pm = Page.findPageMapping(page, getLanguage().getCode(), null, false, allowIncrementedParams);
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
