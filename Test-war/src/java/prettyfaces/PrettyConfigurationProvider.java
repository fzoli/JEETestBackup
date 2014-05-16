package prettyfaces;

import bean.PageBeanLocal;
import com.ocpsoft.pretty.faces.config.PrettyConfig;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.spi.ConfigurationProvider;
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
    
    private List<UrlMapping> loadMappings() {
        PageBeanLocal pageBean = lookupPageBeanLocal();
        List<UrlMapping> mappings = new ArrayList<>();
        if (pageBean != null) {
            PageNode root = pageBean.getPageTree();
            fillList(root.getChildren(), mappings);
        }
        return mappings;
    }

    private void fillList(List<PageNode> nodes, List<UrlMapping> ls) {
        for (PageNode node : nodes) {
            for (UrlMapping mapping : createMapping(node)) {
                ls.add(mapping);
            }
            if (node.isChildAvailable()) {
                fillList(node.getChildren(), ls);
            }
        }
    }
    
    private UrlMapping[] createMapping(PageNode node) {
        UrlMapping map = new UrlMapping();
        String view = "/faces/home.xhtml";
        String link = node.getPermalink();
        for (String param : node.getParameters()) {
            if (param == null || param.trim().isEmpty()) continue;
            link += "/#{" + param.trim() + "}";
        }
        System.out.println("Mapping: " + link + " -> " + view);
        map.setPattern(link);
        map.setViewId(view);
        UrlMapping map2 = new UrlMapping();
        map2.setPattern(link + "/");
        map2.setViewId(view);
        return new UrlMapping[] {map, map2};
    }
    
    private PageBeanLocal lookupPageBeanLocal() {
        try {
            Context c = new InitialContext();
            return (PageBeanLocal) c.lookup("java:global/Test/Test-ejb/PageBean!bean.PageBeanLocal");
        }
        catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            return null;
        }
    }

}
