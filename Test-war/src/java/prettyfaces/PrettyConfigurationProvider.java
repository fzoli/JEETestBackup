package prettyfaces;

import bean.PageBeanLocal;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;
import entity.PageMapping;
import entity.PageNode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

/**
 *
 * @author zoli
 */
public class PrettyConfigurationProvider implements ConfigurationProvider {
    
    @Override
    public PrettyConfig loadConfiguration(ServletContext sc) {
        PrettyConfig cfg = new PrettyConfig();
        cfg.setMappings(loadMappings());
        return cfg;
    }
    
    private static List<UrlMapping> loadMappings() {
        PageBeanLocal pageBean = lookupPageBeanLocal();
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
        String view = node.getViewPath();
        
        List<UrlMapping> ls = new ArrayList<>();
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
            createMapping(ls, link, view);
            createMapping(ls, link + '/', view);
        }

        return ls;
    }
    
    private static void createMapping(List<UrlMapping> ls, String link, String view) {
        UrlMapping map = new UrlMapping();
        map.setPattern(link);
        map.setViewId(view);
        ls.add(map);
    }
    
    private static PageBeanLocal lookupPageBeanLocal() {
        try {
            Context c = new InitialContext();
            return (PageBeanLocal) c.lookup("java:global/Test/Test-ejb/PageBean!bean.PageBeanLocal");
        }
        catch (NamingException ne) {
            Logger.getLogger(PrettyConfigurationProvider.class.getName()).log(Level.SEVERE, "exception caught", ne);
            return null;
        }
    }

}
