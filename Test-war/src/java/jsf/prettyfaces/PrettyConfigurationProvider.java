package jsf.prettyfaces;

import bean.PageBeanLocal;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.PrettyConfigurator;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;
import entity.Language;
import entity.Page;
import entity.PageMapping;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import javax.faces.context.FacesContext;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
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
        return getPageMapping(PrettyContext.getCurrentInstance(context).getCurrentMapping());
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
        for (String param : node.getParameters()) {
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
