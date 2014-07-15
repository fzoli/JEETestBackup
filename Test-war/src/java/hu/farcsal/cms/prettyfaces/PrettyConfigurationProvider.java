package hu.farcsal.cms.prettyfaces;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.config.mapping.PathValidator;
import com.ocpsoft.pretty.faces.config.mapping.UrlAction;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;
import hu.farcsal.cms.bean.CachedBeans;
import hu.farcsal.cms.bean.PageBeanLocal;
import hu.farcsal.cms.entity.Language;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.rewrite.ConfigurationCacheProvider;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.cms.util.WebHelpers;
import hu.farcsal.util.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zoli
 */
public class PrettyConfigurationProvider implements ConfigurationProvider {
    
    private static String pageRoot, ctxPath;
    
    private static PageBeanLocal pageBean;
    
    private static final WeakHashMap<UrlMapping, PageMapping> NODES = new WeakHashMap<>();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PrettyConfigurationProvider.class);
    
    static UrlMapping getUrlMapping(PageMapping mapping) {
        synchronized (NODES) {
            Iterator<Map.Entry<UrlMapping, PageMapping>> it = NODES.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UrlMapping, PageMapping> e = it.next();
                if (e.getValue() == mapping) return e.getKey();
            }
            return null;
        }
    }
    
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
    
    /**
     * Returns the pretty URL.
     * WARNING: This method returns the first match!
     */
    static String findPrettyURL(String viewId, Locale locale, String requestUri) {
        return findPrettyData(viewId, locale, new PageMappingUrlFormatterByUri(requestUri));
    }
    
    /**
     * Returns the pretty URL.
     * WARNING: This method returns the first match!
     */
    static String findPrettyURL(String viewId, Locale locale, String requestUri, Map<String, String> requestParams) {
        return findPrettyData(viewId, locale, new PageMappingUrlFormatterByUriAndParams(requestUri, requestParams));
    }
    
    /**
     * Returns the PageMapping.
     * WARNING: This method returns the first match!
     */
    static PageMapping findPageMapping(String viewId, Locale locale) {
        return findPrettyData(viewId, locale, PAGE_MAPPING_DUMMY_FORMATTER);
    }
    
    /**
     * Returns the UrlMapping.
     * WARNING: This method returns the first match!
     */
    static UrlMapping findUrlMapping(String viewId, Locale locale) {
        return getUrlMapping(findPageMapping(viewId, locale));
    }
    
    /**
     * Returns the pretty URL.
     * WARNING: This method returns the first match!
     */
    private static <T> T findPrettyData(String viewId, Locale locale, PageMappingFormatter<T> formatter) {
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
                    String path = page.getRealViewPath(false);
                    if (!isPathJSF(path)) continue;
                    path = stripPageRoot(path);
                    if (viewId.equals(path)) {
                        try {
                            return formatter.format(mapping);
                        }
                        catch (Exception ex) {
                            // ignore lowlevel URL
                        }
                    }
                }
            }
        }
        return null;
    }
    
    static String stripPageRoot(String path) {
        if (path == null) return null;
        return Strings.ltrim(path, pageRoot, "/");
    }
    
    private static boolean isPathJSF(String path) {
        if (path == null) return false;
        return !path.startsWith("/") || (path.startsWith("/") && path.startsWith(pageRoot));
    }
    
    @Override
    public PrettyConfig loadConfiguration(ServletContext sc) {
        PrettyPageHelper helper = WebHelpers.getPageHelper(sc);
        if (pageRoot == null) pageRoot = helper.getFacesDir();
        if (ctxPath == null) ctxPath = helper.getAppCtxPath();
        if (pageBean == null) pageBean = CachedBeans.getPageBeanLocal();
        PrettyConfig cfg = new PrettyConfig();
//        cfg.setMappings(loadMappings());
        return cfg;
    }
    
    public static void reloadConfiguration(ServletContext sc) {
        new PrettyConfigurator(sc).configure();
        ConfigurationCacheProvider.reset();
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
        return node.getViewPath(true);
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
                PageMapping parentMapping = Pages.getFirstPage(true, null, node, lng.getCode(), null, false, true);
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
        LOGGER.info("Mapping[{}]: {} -> {}", id, link, view);
    }
    
    private static interface PageMappingFormatter<T> {
        public T format(PageMapping mapping) throws Exception;
    }
    
    private static interface PageMappingUrlFormatter extends PageMappingFormatter<String> {}
    
    private static final PageMappingFormatter<PageMapping> PAGE_MAPPING_DUMMY_FORMATTER = new PageMappingFormatter<PageMapping>() {

        @Override
        public PageMapping format(PageMapping mapping) throws Exception {
            return mapping;
        }
        
    };
    
    private static class PageMappingUrlFormatterByUri implements PageMappingUrlFormatter {

        protected final String requestUri;
        
        public PageMappingUrlFormatterByUri(String requestUri) {
            this.requestUri = requestUri;
        }
        
        @Override
        public String format(PageMapping mapping) throws Exception {
            return ctxPath + mapping.getPermalink(requestUri);
        }
        
    }
    
    private static class PageMappingUrlFormatterByUriAndParams extends PageMappingUrlFormatterByUri {

        private final Map<String, String> requestParams;
        
        public PageMappingUrlFormatterByUriAndParams(String requestUri, Map<String, String> requestParams) {
            super(requestUri);
            this.requestParams = requestParams;
        }
        
        @Override
        public String format(PageMapping mapping) throws Exception {
            return ctxPath + mapping.getPermalink(requestUri, requestParams);
        }
        
    }
    
    private static class PrettyPageFormatter extends PageMapping.PageFormatter {

        private final int paramLimit;
        
        public PrettyPageFormatter(PageMapping mapping, int paramLimit) {
            super(mapping);
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
