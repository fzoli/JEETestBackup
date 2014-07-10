package jsf.prettyfaces;

import bean.PageBeanLocal;
import entity.Language;
import entity.Page;
import entity.PageMapping;
import entity.spec.Helpers;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import logging.Log;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import util.UrlParameters;

/**
 *
 * @author zoli
 */
@RewriteConfiguration
public class RewriteTestProvider extends HttpConfigurationProvider {
    
    private static final UrlParameters LNG_PARAM = new UrlParameters(PrettyViewHandler.KEY_LANGUAGE);
    
    private static class LngCondition extends ParameterCondition {
        
        public LngCondition(String lngCode) {
            super(LNG_PARAM, lngCode);
        }
        
    }
    
    private static class RewritePageFormatter extends PageMapping.PageFormatter {

        private final int paramLimit;
        
        public RewritePageFormatter(PageMapping mapping, int paramLimit) {
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
                ps += "/{" + p.getName() + "}";
            }
            return ps;
        }
        
    }
    
    @Override
    public int priority() {
        return 0;
    }
    
    private static final Log LOGGER = Log.getLogger(RewriteTestProvider.class);
    
    private static String pageRoot, ctxPath;
    public static PageBeanLocal pageBean;
    
    private static void initProvider(ServletContext context) {
        if (Helpers.pageHelper == null) Helpers.pageHelper = new PrettyPageHelper(context);
        if (pageRoot == null) pageRoot = Helpers.pageHelper.getFacesDir();
        if (ctxPath == null) ctxPath = Helpers.pageHelper.getAppCtxPath();
        if (pageBean == null) pageBean = Beans.lookupPageBeanLocal();
    }
    
    private static String getViewPath(Page node) {
        if (node.isViewPathGenerated()) {
            String vp = node.getViewPath();
            if (vp == null) return null;
            return "#{" + vp + "}";
        }
        return node.getViewPath(true);
    }
    
    private static List<LngCondition> createLanguageConditions() {
        List<LngCondition> l = new ArrayList<>();
        for (Language lng : pageBean.getLanguages()) {
            l.add(new LngCondition(lng.getCode()));
        }
        return l;
    }
    
    private static LngCondition getLanguageCondition(List<LngCondition> l, Language lng) {
        for (LngCondition c : l) {
            if (c.getValue().equals(lng.getCode())) return c;
        }
        return null;
    }
    
    private static void append(ConfigurationBuilder cfg, List<Page> pages, List<LngCondition> lngConditions) {
        for (Page p : pages) {
            append(cfg, p, lngConditions);
            if (p.isChildAvailable()) {
                append(cfg, p.getChildren(), lngConditions);
            }
        }
    }
    
    private static void append(ConfigurationBuilder cfg, Page node, List<LngCondition> lngConditions) {
        if (node.getId() == null) return;
        
        String view = getViewPath(node);
        boolean findParentView = view == null;
        
        List<PageMapping> mappings = node.getMappings();
        if (mappings == null || mappings.isEmpty()) return;
        
        for (PageMapping mapping : mappings) {
            Language lng = mapping.getLanguage();
            List<String> actions = mapping.getPage().getActions();
            if (lng == null || lng.getCode() == null) continue;
            String id = mapping.getLanguage().getCode() + '-' + node.getId();
            LngCondition lngCondition = getLanguageCondition(lngConditions, lng);
            if (findParentView) {
                PageMapping parentMapping = Pages.getFirstPage(true, null, node, lng.getCode(), null, false, true);
                if (parentMapping == null) continue;
                view = getViewPath(parentMapping.getPage());
            }
            int paramCount = mapping.getPage().getParameters().size();
            for (int paramLimit = mapping.getPage().isParameterIncremented() ? 0 : paramCount; paramLimit <= paramCount; paramLimit++) {
                String link = mapping.getPermalink(new RewritePageFormatter(mapping, paramLimit));
                if (link == null) continue; // the path is broken or the language not matches; next...
                String mappingId =  id + '.' + paramLimit;
                createRule(cfg, lngCondition, mapping, mappingId + "-x", link, view, actions);
                createRule(cfg, lngCondition, mapping, mappingId + "-y", link + '/', view, actions);
            }
        }
        
    }
    
    private static void createRule(ConfigurationBuilder cfg, LngCondition lngCondition, PageMapping mapping, String id, String link, String view, List<String> actions) {
        cfg.addRule(Join.path(link).to(view)).when(lngCondition);
        LOGGER.i(String.format("Mapping[%s]: %s -> %s", id, link, view));
    }
    
    @Override
    public Configuration getConfiguration(final ServletContext context) {
        initProvider(context);
        List<LngCondition> lngConditions = createLanguageConditions();
        ConfigurationBuilder cfg = ConfigurationBuilder.begin();
        
        append(cfg, pageBean.getPageTree().getChildren(), lngConditions);
        
        Condition conditionHu = new LngCondition("hu");
        Condition conditionEn = new LngCondition("en");
        Rule ruleHu = Join.path("/tigris").to("/faces/tiger.xhtml");
        Rule ruleEn = Join.path("/tiger").to("/faces/tiger.xhtml");
        return cfg
            .addRule(ruleHu).when(conditionHu)
            .addRule(ruleEn).when(conditionEn);
    }
    
}
