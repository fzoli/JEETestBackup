package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.util.Pages;
import hu.farcsal.cms.bean.Beans;
import hu.farcsal.cms.bean.PageBeanLocal;
import hu.farcsal.cms.entity.Language;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.spec.Helpers;
import hu.farcsal.cms.prettyfaces.PrettyPageHelper;
import hu.farcsal.cms.prettyfaces.PrettyViewHandler;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import hu.farcsal.log.Log;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import hu.farcsal.util.UrlParameters;

/**
 *
 * @author zoli
 */
@RewriteConfiguration
public class DatabaseConfigurationProvider extends HttpConfigurationProvider {
    
    private static final UrlParameters LNG_PARAM = new UrlParameters(PrettyViewHandler.KEY_LANGUAGE);
    
    private static class LanguageProcessor extends ParameterProcessor {
        
        public LanguageProcessor(String lngCode) {
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
    
    private static final Log LOGGER = Log.getLogger(DatabaseConfigurationProvider.class);
    
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
    
    private static List<LanguageProcessor> createLanguageConditions() {
        List<LanguageProcessor> l = new ArrayList<>();
        for (Language lng : pageBean.getLanguages()) {
            l.add(new LanguageProcessor(lng.getCode()));
        }
        return l;
    }
    
    private static LanguageProcessor getLanguageCondition(List<LanguageProcessor> l, Language lng) {
        for (LanguageProcessor c : l) {
            if (c.getValue().equals(lng.getCode())) return c;
        }
        return null;
    }
    
    private static void append(ConfigurationBuilder cfg, List<Page> pages, List<LanguageProcessor> lngConditions) {
        for (Page p : pages) {
            append(cfg, p, lngConditions);
            if (p.isChildAvailable()) {
                append(cfg, p.getChildren(), lngConditions);
            }
        }
    }
    
    private static void append(ConfigurationBuilder cfg, Page node, List<LanguageProcessor> lngConditions) {
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
            LanguageProcessor lngCondition = getLanguageCondition(lngConditions, lng);
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
    
    private static void createRule(ConfigurationBuilder cfg, LanguageProcessor lngCondition, PageMapping mapping, String id, String link, String view, List<String> actions) {
        cfg.addRule(Join.path(link).to(view)).when(lngCondition).perform(lngCondition);
        LOGGER.i(String.format("Mapping[%s]: %s -> %s", id, link, view));
    }
    
    @Override
    public Configuration getConfiguration(final ServletContext context) {
        initProvider(context);
        
        List<LanguageProcessor> lngConditions = createLanguageConditions();
        ConfigurationBuilder cfg = ConfigurationBuilder.begin(); //return cfg;
        
//        append(cfg, pageBean.getPageTree().getChildren(), lngConditions);
        
        LanguageProcessor conditionHu = new LanguageProcessor("hu");
        LanguageProcessor conditionEn = new LanguageProcessor("en");
        Rule ruleHu = Join.path("/tigris").to("/faces/tiger.xhtml");
        Rule ruleEn = Join.path("/tiger").to("/faces/tiger.xhtml");
        return cfg
            .addRule(ruleHu).when(conditionHu).perform(conditionHu)
            .addRule(ruleEn).when(conditionEn).perform(conditionEn);
    }
    
}
