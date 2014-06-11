package jsf.prettyfaces;

import bean.PageBeanLocal;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.config.mapping.PathValidator;
import com.ocpsoft.pretty.faces.config.mapping.UrlAction;
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
        synchronized (NODES) {
            return NODES.get(mapping);
        }
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
        return getFilterType(false, site, pageMapping, pageFilters);
    }
    
    private static FilterType getFilterType(boolean skipSiteChk, Site site, PageMapping pageMapping, List<PageFilter> pageFilters) {
        if (pageMapping != null) {
            Page page = pageMapping.getPage();
            if (page == null) {
                return FilterType.PAGE_UNKNOWN;
            }
            else {
                if (page.isDisabled(true)) {
                    return FilterType.PAGE_DISABLED;
                }
                else {
                    if (site == null) {
                        if (!skipSiteChk && page.isSiteDependent()) return FilterType.SITE_UNKNOWN;
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
    
    static PageMapping getFirstPage(HttpServletRequest request, String defLanguage, boolean overrideLanguage) {
        String language = request.getLocale().getLanguage();
        Site site = Site.findSiteByDomain(pageBean.getSites(), request.getServerName());
        return getFirstPage(site, overrideLanguage && defLanguage != null ? defLanguage : language, defLanguage);
    }
    
    private static PageMapping getFirstPage(Site site, String language, String defLanguage) {
        if (site == null) return null;
        if (site.getHomePage() != null) {
            PageMapping pm = Page.findPageMapping(site.getHomePage(), language, defLanguage, true);
            if (pm != null) return pm;
        }
        return getFirstPage(site, pageBean.getPageTree(), language, defLanguage);
    }
    
    static PageMapping getFirstPage(Site site, Page page, String language, String defLanguage) {
        return getFirstPage(false, site, page, language, defLanguage);
    }
    
    private static PageMapping getFirstPage(boolean skipSiteChk, Site site, Page page, String language, String defLanguage) {
        List<Page> pages = page.getOrderedChildren();
        List<PageFilter> pageFilters = pageBean.getPageFilters();
        for (Page p : pages) {
            PageMapping pm = Page.findPageMapping(p, language, defLanguage, true);
            if (pm != null && PrettyConfigurationProvider.getFilterType(skipSiteChk, site, pm, pageFilters) == null) {
                if (p.getViewPath() != null) return pm;
                return getFirstPage(skipSiteChk, site, p, language, defLanguage);
            }
        }
        return null;
    }
    
    /**
     * Returns the pretty URL.
     * WARNING: This method returns the first match!
     */
    static String findPrettyURL(String viewId, Locale locale, String requestUri) {
        if (pageRoot == null || locale == null || viewId == null) return null;
        viewId = stripPageRoot(viewId);
        String language = locale.getLanguage();
        synchronized (NODES) {
            Iterator<Map.Entry<UrlMapping, PageMapping>> it = NODES.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UrlMapping, PageMapping> e = it.next();
                PageMapping mapping = e.getValue();
                if (mapping.getLanguage() == null || mapping.getPage() == null) continue;
                Page page = mapping.getPage();
                if (language.equalsIgnoreCase(mapping.getLanguage().getCode())) {
                    String path = page.getViewPath();
                    if (!isPathJSF(path)) continue;
                    path = stripPageRoot(path);
                    if (viewId.equals(path)) {
                        return pageRoot + mapping.getPermalink(requestUri);
                    }
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
        List<UrlMapping> mappings = new ArrayList<>();
        synchronized (NODES) {
            NODES.clear();
            if (pageBean != null) {
                pageBean.clearPagesFromCache();
                Page root = pageBean.getPageTree();
                fillList(root.getChildren(), mappings);
            }
            Collections.reverse(mappings); // solves parameter "bug"
        }
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
    
    private static String getViewPath(Page node) {
        if (node.isViewPathGenerated()) {
            String vp = node.getViewPath();
            if (vp == null) return null;
            return "#{" + vp + "}";
        }
        return node.getViewPath(pageRoot);
    }
    
    private static List<UrlMapping> createMappings(Page node) {
        List<UrlMapping> ls = new ArrayList<>();
        
        if (node.getId() == null) return ls;
        
        String view = getViewPath(node);
        boolean findParentView = view == null;
        
        List<PageMapping> mappings = node.getMappings();
        if (mappings == null || mappings.isEmpty()) return ls;
        
        int index = 0;
        List<PathValidator> validators = new ArrayList<>();
        List<String> paramNames = new ArrayList<>();
        for (Page pg : node.getWay(true)) {
            for (Page.Parameter p : pg.getParameters()) {
                if (p == null) continue;
                String name = p.getName();
                String param = p.getValue();
                String validator = p.getValidator();
                if (param == null || param.trim().isEmpty()) continue;
                if (paramNames.contains(name)) throw new IllegalArgumentException("duplicated parameter name - " + name);
                paramNames.add(name);
                if (validator != null && !validator.trim().isEmpty()) {
                    PathValidator pv = new PathValidator();
                    pv.setIndex(index);
                    pv.setValidatorIds(validator);
                    validators.add(pv);
                }
                index++;
            }
        }
        
        for (PageMapping mapping : mappings) {
            Language lng = mapping.getLanguage();
            List<String> actions = mapping.getPage().getActions();
            if (lng == null || lng.getCode() == null) continue;
            String id = mapping.getLanguage().getCode() + '-' + node.getId();
            if (findParentView) {
                PageMapping parentMapping = getFirstPage(true, null, node, lng.getCode(), null);
                if (parentMapping == null) continue;
                view = getViewPath(parentMapping.getPage());
            }
            int paramCount = mapping.getPage().getParameters().size();
            for (int paramLimit = mapping.getPage().isParameterIncremented() ? 0 : paramCount; paramLimit <= paramCount; paramLimit++) {
                String link = mapping.getPermalink(new PrettyPageFormatter(mapping, paramLimit));
                if (link == null) continue; // the path is broken or the language not matches; next...
                String mappingId =  id + '.' + paramLimit;
                createMapping(ls, mapping, mappingId + "-x", link, view, actions, validators);
                createMapping(ls, mapping, mappingId + "-y", link + '/', view, actions, validators);
            }
        }

        return ls;
    }
    
    private static void createMapping(List<UrlMapping> ls, PageMapping mapping, String id, String link, String view, List<String> actions, List<PathValidator> validators) {
        UrlMapping map = new UrlMapping();
        map.setId(id);
        map.setPattern(link);
        map.setViewId(view);
        if (actions != null) for (String action : actions) {
            map.addAction(new UrlAction("#{" + action + "}"));
        }
        if (!validators.isEmpty()) map.setPathValidators(validators);
        ls.add(map);
        NODES.put(map, mapping);
        LOGGER.i(String.format("Mapping[%s]: %s -> %s", id, link, view));
    }
    
    private static class PrettyPageFormatter extends PageMapping.PageFormatter {

        private final int paramLimit;
        
        public PrettyPageFormatter(PageMapping mapping, int paramLimit) {
            super(mapping, null);
            this.paramLimit = paramLimit;
        }

        @Override
        protected String getParameterString(Page page) {
            int i = 0;
            String ps = "";
            boolean dst = mapping.getPage() == page;
            for (Page.Parameter p : page.getParameters()) {
                if (dst && ++i > paramLimit) break;
                ps += "/#{" + p.getName() + "}";
            }
            return ps;
        }
        
    }
    
}
