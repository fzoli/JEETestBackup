package jsf.prettyfaces;

import bean.PageBeanLocal;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;
import entity.Language;
import entity.Page;
import entity.PageFilter;
import entity.PageMapping;
import entity.Site;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.faces.context.FacesContext;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import logging.Log;

/**
 *
 * @author zoli
 */
public class PrettyConfigurationProvider implements ConfigurationProvider {
    
    private static String pageRoot;
    
    private static PageBeanLocal pageBean;
    
    private static final WeakHashMap<UrlMapping, PageMapping> NODES = new WeakHashMap<>();
    
    private static final Log LOGGER = Log.getLogger(PrettyConfigurationProvider.class);
    
    static PageMapping getPageMapping(UrlMapping mapping) {
        return NODES.get(mapping);
    }
    
    static PageMapping getPageMapping(FacesContext context) {
        return getPageMapping(getCurrentMapping(context));
    }
    
    static UrlMapping getCurrentMapping(FacesContext context) {
        return PrettyContext.getCurrentInstance(context).getCurrentMapping();
    }
    
    enum FilterType {
        PAGE_DISABLED, SITE_UNKNOWN, SITE_DISABLED, SITE_FILTERED, PAGE_UNKNOWN
    }
    
    static FilterType getFilterType(Site site, PageMapping pageMapping, List<PageFilter> pageFilters) {
        if (pageMapping != null) {
            Page page = pageMapping.getPage();
            if (page == null) {
                return FilterType.PAGE_UNKNOWN;
            }
            else {
                if (page.isDisabled()) {
                    return FilterType.PAGE_DISABLED;
                }
                else {
                    if (site == null) {
                        if (page.isSiteDependent()) return FilterType.SITE_UNKNOWN;
                    }
                    else {
                        if (site.isDisabled()) {
                            return FilterType.SITE_DISABLED;
                        }
                        else if (PageFilter.isPageFiltered(pageFilters, site)) {
                            return FilterType.SITE_FILTERED;
                        }
                    }
                }
            }
        }
        else {
            return FilterType.PAGE_UNKNOWN;
        }
        return null;
    }
    
    static PageMapping getFirstPage(HttpServletRequest request) {
        String language = request.getLocale().getLanguage();
        Site site = Site.findSiteByDomain(pageBean.getSites(), request.getServerName());
        if (site == null) return null;
        if (site.getHomePage() != null) {
            PageMapping pm = Page.findPageMapping(site.getHomePage(), language, true);
            if (pm != null) return pm;
        }
        List<Page> mainPages = pageBean.getPageTree().getOrderedChildren();
        List<PageFilter> pageFilters = pageBean.getPageFilters();
        for (Page page : mainPages) {
            PageMapping pm = Page.findPageMapping(page, language, true);
            if (pm != null && PrettyConfigurationProvider.getFilterType(site, pm, pageFilters) == null) {
                return pm;
            }
        }
        return null;
    }
    
    /**
     * Returns the pretty URL.
     * WARNING: This method returns the first match!
     */
    static String findPrettyURL(String viewId, Locale locale, List<String> paramValues) {
        if (pageRoot == null || locale == null || viewId == null) return null;
        viewId = stripPageRoot(viewId);
        String language = locale.getLanguage();
        Iterator<Map.Entry<UrlMapping, PageMapping>> it = NODES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UrlMapping, PageMapping> e = it.next();
            PageMapping mapping = e.getValue();
            if (mapping.getLanguage() == null || mapping.getPage() == null) continue;
            Page page = mapping.getPage();
            if (language.equalsIgnoreCase(mapping.getLanguage().getCode()) && page.isParametersValid(paramValues, true)) {
                String path = page.getViewPath();
                if (!isPathJSF(path)) continue;
                path = stripPageRoot(path);
                if (viewId.equals(path)) {
                    StringBuilder prettyURL = new StringBuilder(pageRoot + mapping.getPermalink());
                    if (paramValues != null) {
                        for (String paramValue : paramValues) {
                            if (paramValue == null || paramValue.trim().isEmpty()) continue;
                            prettyURL.append('/').append(paramValue);
                        }
                    }
                    return prettyURL.toString();
                }
            }
        }
        return null;
    }
    
    static String stripPageRoot(String path) {
        if (path == null) return null;
        if (path.startsWith(pageRoot)) path = path.substring(pageRoot.length());
        if (path.startsWith("/")) path = path.substring(1);
        return path;
    }
    
    private static boolean isPathJSF(String path) {
        if (path == null) return false;
        return !path.startsWith("/") || (path.startsWith("/") && path.startsWith(pageRoot));
    }
    
    @Override
    public PrettyConfig loadConfiguration(ServletContext sc) {
        if (pageBean == null) pageBean = Beans.lookupPageBeanLocal();
        if (pageRoot == null) pageRoot = Servlets.getMappingDir(sc, FacesServlet.class);
        PrettyConfig cfg = new PrettyConfig();
        cfg.setMappings(loadMappings());
        return cfg;
    }
    
    public static void reloadConfiguration(ServletContext sc) {
        new PrettyConfigurator(sc).configure();
    }
    
    private static List<UrlMapping> loadMappings() {
        NODES.clear();
        List<UrlMapping> mappings = new ArrayList<>();
        if (pageBean != null) {
            pageBean.clearPagesFromCache();
            Page root = pageBean.getPageTree();
            fillList(root.getChildren(), mappings);
        }
        Collections.reverse(mappings); // solves parameter "bug"
        return mappings;
    }

    private static void fillList(List<Page> nodes, List<UrlMapping> ls) {
        for (Page node : nodes) {
            ls.addAll(createMappings(node));
            if (node.isChildAvailable()) {
                fillList(node.getChildren(), ls);
            }
        }
    }
    
    private static List<UrlMapping> createMappings(Page node) {
        List<UrlMapping> ls = new ArrayList<>();
        
        if (node.getId() == null) return ls;
        
        String view = node.getViewPath(pageRoot);
        if (view == null) return ls;
        
        List<PageMapping> mappings = node.getMappings();
        if (mappings == null || mappings.isEmpty()) return ls;
        
        String paramString = "";
        for (String param : node.getParameterNames()) {
            if (param == null || param.trim().isEmpty()) continue;
            paramString += "/#{" + param.trim() + "}";
        }
        
        for (PageMapping mapping : mappings) {
            String link = mapping.getPermalink();
            Language lng = mapping.getLanguage();
            if (link == null || lng == null || lng.getCode() == null) continue;
            link += paramString;
            String id = mapping.getLanguage().getCode() + node.getId();
            LOGGER.i(String.format("Mapping[%s]: %s -> %s", id, link, view));
            createMapping(ls, mapping, id, link, view);
            createMapping(ls, mapping, id, link + '/', view);
        }

        return ls;
    }
    
    private static void createMapping(List<UrlMapping> ls, PageMapping mapping, String id, String link, String view) {
        UrlMapping map = new UrlMapping();
        map.setId(id);
        map.setPattern(link);
        map.setViewId(view);
        ls.add(map);
        NODES.put(map, mapping);
    }
    
}
