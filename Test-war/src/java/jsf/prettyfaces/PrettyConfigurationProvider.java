package jsf.prettyfaces;

import bean.PageBeanLocal;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.PathValidator;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;
import entity.PageMapping;
import entity.PageNode;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 *
 * @author zoli
 */
public class PrettyConfigurationProvider implements ConfigurationProvider {
    
    private static final WeakHashMap<UrlMapping, PageNode> NODES = new WeakHashMap<>();
    
    static PageNode getPage(UrlMapping mapping) {
        return NODES.get(mapping);
    }
    
    static PageNode getPage(FacesContext context) {
        return getPage(PrettyContext.getCurrentInstance(context).getCurrentMapping());
    }
    
    @Override
    public PrettyConfig loadConfiguration(ServletContext sc) {
        PrettyConfig cfg = new PrettyConfig();
        cfg.setMappings(loadMappings());
        return cfg;
    }
    
    private static List<UrlMapping> loadMappings() {
        PageBeanLocal pageBean = Beans.lookupPageBeanLocal();
        List<UrlMapping> mappings = new ArrayList<>();
        if (pageBean != null) {
            PageNode root = pageBean.getPageTree();
            fillList(root.getChildren(), mappings);
        }
        return mappings;
    }

    private static void fillList(List<PageNode> nodes, List<UrlMapping> ls) {
        for (PageNode node : nodes) {
            ls.addAll(createMappings(node));
            if (node.isChildAvailable()) {
                fillList(node.getChildren(), ls);
            }
        }
    }
    
    private static List<UrlMapping> createMappings(PageNode node) {
        List<UrlMapping> ls = new ArrayList<>();
        
        String view = node.getViewPath();
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
            if (link == null) continue;
            link += paramString;
            System.out.println("Mapping: " + link + " -> " + view);
            createMapping(ls, node, link, view);
            createMapping(ls, node, link + '/', view);
        }

        return ls;
    }
    
    private static void createMapping(List<UrlMapping> ls, PageNode node, String link, String view) {
        UrlMapping map = new UrlMapping();
        map.setPattern(link);
        map.setViewId(view);
        PathValidator lngValidator = new PathValidator();
        lngValidator.setValidatorIds(PathFilterValidator.NAME);
        map.addPathValidator(lngValidator);
        ls.add(map);
        NODES.put(map, node);
    }

}
