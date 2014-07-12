package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.util.Pages;
import hu.farcsal.cms.bean.Beans;
import hu.farcsal.cms.bean.PageBeanLocal;
import hu.farcsal.cms.entity.Language;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.spec.Helpers;
import hu.farcsal.cms.prettyfaces.PrettyPageHelper;
import hu.farcsal.cms.rewrite.cache.PageMappingCache;
import hu.farcsal.log.Log;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 *
 * @author zoli
 */
@RewriteConfiguration
public class DatabaseConfigurationProvider extends HttpConfigurationProvider {
    
    private static class LanguageProcessor extends ParameterProcessor {
        
        public LanguageProcessor(String lngCode) {
            super(Pages.getLanguageParameter(), lngCode);
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
    
    private static PrettyPageHelper pageHelper;
    private static PageBeanLocal pageBean;
    
    private static void initProvider(ServletContext context) {
        if (pageHelper == null) pageHelper = Helpers.initPageHelper(new PrettyPageHelper(context));
        if (pageBean == null) pageBean = Beans.lookupPageBeanLocal();
        RewriteRuleCache.clear();
    }
    
    private static String getViewPath(Page node) {
        if (node.isViewPathGenerated()) {
            String vp = node.getViewPath();
            if (vp == null) return null;
            return "#{" + vp + "}";
        }
        return node.getViewPath(true);
    }
    
    private static List<LanguageProcessor> createLanguageProcessors() {
        List<LanguageProcessor> l = new ArrayList<>();
        for (Language lng : pageBean.getLanguages()) {
            l.add(new LanguageProcessor(lng.getCode()));
        }
        return l;
    }
    
    private static LanguageProcessor getLanguageProcessor(List<LanguageProcessor> l, Language lng) {
        for (LanguageProcessor p : l) {
            if (p.getValue().equals(lng.getCode())) return p;
        }
        return null;
    }
    
    private static void append(ConfigurationBuilder cfg, List<Page> pages, List<LanguageProcessor> lngProcessors) {
        for (Page p : pages) {
            if (p.isChildAvailable()) append(cfg, p.getChildren(), lngProcessors);
            append(cfg, p, lngProcessors);
        }
    }
    
    private static void append(ConfigurationBuilder cfg, Page node, List<LanguageProcessor> lngProcessors) {
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
            LanguageProcessor lngProcessor = getLanguageProcessor(lngProcessors, lng);
            if (findParentView) {
                PageMapping parentMapping = Pages.getFirstPage(true, null, node, lng.getCode(), null, false, true);
                if (parentMapping == null) continue;
                view = getViewPath(parentMapping.getPage());
            }
            int paramCount = mapping.getPage().getParameters().size();
            for (int paramLimit = !mapping.getPage().isParameterIncremented() ? 0 : paramCount; paramLimit >= 0; paramLimit--) {
                String link = mapping.getPermalink(new RewritePageFormatter(mapping, paramLimit));
                if (link == null) continue; // the path is broken or the language not matches; next...
                String mappingId =  id + '.' + paramLimit;
                createRule(cfg, lngProcessor, mapping, mappingId + "-y", link + '/', view, actions);
                createRule(cfg, lngProcessor, mapping, mappingId + "-x", link, view, actions);
            }
        }
        
    }
    
    private static void createRule(final ConfigurationBuilder cfg, final LanguageProcessor lngProcessor, final PageMapping mapping, final String id, final String link, final String view, final List<String> actions) {
        Rule rule = Join.path(link).to(view);
        cfg.addRule(rule).when(lngProcessor).perform(lngProcessor);
        RewriteRuleCache.save(rule, new PageMappingCache(mapping));
        LOGGER.i(String.format("Mapping[%s]: %s -> %s", id, link, view));
    }
    
    @Override
    public Configuration getConfiguration(final ServletContext context) {
        initProvider(context);
        
        List<LanguageProcessor> lngProcessors = createLanguageProcessors();
        ConfigurationBuilder cfg = ConfigurationBuilder.begin(); //return cfg;
        
        append(cfg, pageBean.getPageTree().getChildren(), lngProcessors);
        cfg.addRule().when(Direction.isInbound().and(Path.matches("/"))).perform(new HomePageRedirector(context, pageHelper, pageBean.getSites()));
        
        LanguageProcessor processorHu = new LanguageProcessor("hu");
        LanguageProcessor processorEn = new LanguageProcessor("en");
        Rule ruleHu = Join.path("/tigris").to("/faces/tiger.xhtml");
        Rule ruleEn = Join.path("/tiger").to("/faces/tiger.xhtml");
        return cfg
            .addRule(ruleHu).when(processorHu).perform(processorHu)
            .addRule(ruleEn).when(processorEn).perform(processorEn);
    }
    
}
