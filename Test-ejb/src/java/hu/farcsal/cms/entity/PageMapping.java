package hu.farcsal.cms.entity;

import hu.farcsal.cms.entity.spec.Helpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import hu.farcsal.util.Strings;

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
        return getPermalink(url == null || url.isEmpty() ? SIMPLE_URL_FORMATTER : new PageFormatterByUrl(this, url));
    }
    
    public String getPermalink(Map<String, String> params) throws Exception {
        return getPermalink(new PageFormatterByParams(this, params));
    }
    
    public String getPermalink(String url, Map<String, String> params) throws Exception {
        return getPermalink(new PageFormatterByUrlAndParams(this, url, params));
    }
    
    // returns null if the path is broken!
    public String getPermalink(Strings.Formatter<Page> formatter) {
        String link = Strings.join(getPage().getWay(true), "/", formatter);
        if (link == null) return null;
        return link.startsWith("/") ? link : "/" + link;
    }
    
    public static class PageFormatter implements Strings.Formatter<Page> {

        protected final PageMapping mapping;
        
        public PageFormatter(PageMapping mapping) {
            this.mapping = mapping;
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
            return "";
        }
        
        protected String getPrettyName(Page page, boolean allowIncrementedParams) {
            if (getLanguage() == null || getLanguage().getCode() == null) return null;
            PageMapping pm = Page.findPageMapping(page, getLanguage().getCode(), null, false, allowIncrementedParams);
            if (pm == null) return null;
            String pn = pm.getPrettyName();
            if (pn == null || pn.isEmpty()) return null;
            return pn;
        }
        
        protected String throwException(Page way, String ret, RuntimeException ex) {
            if (isLastWay(way)) {
                if (!way.isParameterIncremented()) throw ex;
            }
            else {
                throw ex;
            }
            return ret;
        }
        
        protected boolean isLastWay(Page way) {
            return mapping.getPage() == way;
        }
        
        private Language getLanguage() {
            if (mapping == null) return null;
            return mapping.getLanguage();
        }
        
    }
    
    private static class PageFormatterByUrl extends PageFormatter {

        private final String url;
        
        public PageFormatterByUrl(PageMapping mapping, String url) {
            super(mapping);
            this.url = stripAppCtxFromUrl(url);
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
        
    }
    
    private static class PageFormatterByParams extends PageFormatter {

        protected final Map<String, String> PARAMS;
        
        public PageFormatterByParams(PageMapping mapping, Map<String, String> params) {
            super(mapping);
            PARAMS = params;
        }

        @Override
        protected String getParameterString(Page page) {
            String ps = "";
            for (Page.Parameter param : page.getParameters()) {
                String val = PARAMS.get(param.getName());
                if (val == null) return throwException(page, ps, new RuntimeException("not enought parameters"));
                ps += "/" + val;
            }
            return ps;
        }
        
    }
    
    private static class PageFormatterByUrlAndParams extends PageFormatterByParams {

        private final PageFormatter fmtUrl;
        
        public PageFormatterByUrlAndParams(PageMapping mapping, String url, Map<String, String> params) {
            super(mapping, params);
            fmtUrl = new PageFormatterByUrl(mapping, url);
        }

        @Override
        protected String getParameterString(Page page) {
            List<Page.Parameter> params = page.getParameters();
            if (params.isEmpty()) return "";
            try {
                // try loading url params as default values
                Map<String, String> urlParams = new HashMap<>();
                String[] urlSplit = fmtUrl.getParameterString(page).split("/");
                for (int i = 1; i < urlSplit.length; i++) {
                    urlParams.put(params.get(i).getName(), urlSplit[i]);
                }
                // default values are loaded
                if (!urlParams.isEmpty()) {
                    // override default values
                    urlParams.putAll(PARAMS);
                    // refresh the original map
                    PARAMS.putAll(urlParams);
                }
            }
            catch (Exception ex) {
                // loading params by url is failed
            }
            return super.getParameterString(page);
        }
        
    }
    
    private final transient Strings.Formatter<Page> SIMPLE_URL_FORMATTER = new PageFormatterByUrl(this, null);
    
}
