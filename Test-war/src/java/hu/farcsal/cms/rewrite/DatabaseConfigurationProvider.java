package hu.farcsal.cms.rewrite;

import hu.farcsal.cms.bean.CachedBeans;
import hu.farcsal.cms.bean.PageBeanLocal;
import hu.farcsal.cms.entity.Language;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.prettyfaces.PrettyPageHelper;
import hu.farcsal.cms.rewrite.cache.PageMappingCache;
import hu.farcsal.cms.util.Pages;
import hu.farcsal.cms.util.WebHelpers;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.OperationBuilder;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO:
 * - page action
 * - parameter validator
 * - parameter bean variable
 * - page view path generated
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
        return ConfigOrder.DATABASE.getPriority();
    }
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfigurationProvider.class);
    
    private static final ViewlessPageHandler DUMMY_VIEWLESS_PAGE_HANDLER = new ViewlessPageHandler(null);
    
    private static PrettyPageHelper pageHelper;
    private static PageBeanLocal pageBean;
    
    private static void initProvider(ServletContext context) {
        if (pageHelper == null) pageHelper = WebHelpers.getPageHelper(context);
        if (pageBean == null) pageBean = CachedBeans.getPageBeanLocal();
        RewriteRuleCache.clear(PageMappingCache.class);
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
        boolean viewless = view == null;
        
        List<PageMapping> mappings = node.getMappings();
        if (mappings == null || mappings.isEmpty()) return;
        
        for (PageMapping mapping : mappings) {
            Language lng = mapping.getLanguage();
            if (lng == null || lng.getCode() == null) continue;
            String id = mapping.getLanguage().getCode() + '-' + node.getId();
            if (viewless) {
                PageMapping parentMapping = Pages.getFirstPage(true, null, node, lng.getCode(), null, false, true);
                if (parentMapping == null) continue;
                view = getViewPath(parentMapping.getPage());
            }
            PageMappingCache cache = new PageMappingCache(mapping);
            LanguageProcessor lngProcessor = getLanguageProcessor(lngProcessors, lng);
            InboundPageFilter pageFilter = new PageMappingFilter(mapping);
            ViewlessPageHandler viewlessHandler = viewless ? new ViewlessPageHandler(mapping) : DUMMY_VIEWLESS_PAGE_HANDLER;
            List<String> actions = mapping.getPage().getActions();
            int paramCount = mapping.getPage().getParameters().size();
            for (int paramLimit = !mapping.getPage().isParameterIncremented() ? 0 : paramCount; paramLimit >= 0; paramLimit--) {
                String link = mapping.getPermalink(new RewritePageFormatter(mapping, paramLimit));
                if (link == null) continue; // the path is broken or the language not matches; next...
                String mappingId =  id + '.' + paramLimit;
                createRule(cfg, cache, lngProcessor, pageFilter, viewlessHandler, mappingId + "-y", link + '/', view, actions);
                createRule(cfg, cache, lngProcessor, pageFilter, viewlessHandler, mappingId + "-x", link, view, actions);
            }
        }
        
    }
    
    private static void createRule(final ConfigurationBuilder cfg, final PageMappingCache cache, final LanguageProcessor lngProcessor, InboundPageFilter pageFilter, ViewlessPageHandler viewlessHandler, final String id, final String link, final String view, final List<String> actions) {
        Rule rule = Join.path(link).to(view);
        OperationBuilder operation = viewlessHandler.and(lngProcessor);
//        if (actions != null) {
//            for (final String action : actions) {
//                if (action == null || action.isEmpty()) continue;
//                // java.lang.ClassNotFoundException: org.ocpsoft.common.spi.ServiceLocator
//                // org.ocpsoft.rewrite.exception.RewriteException: No registered org.ocpsoft.rewrite.el.spi.ExpressionLanguageProvider could handle the Expression
//                operation = operation.and(
//                    PhaseOperation.enqueue(
//                        Invoke.binding(El.retrievalMethod(action))
//                    ).after(PhaseId.RESTORE_VIEW)
//                );
//            }
//        }
        cfg.addRule(rule).when(lngProcessor.and(pageFilter)).perform(operation);
        RewriteRuleCache.save(rule, cache);
        LOGGER.info("Mapping[{}]: {} -> {}", id, link, view);
    }
    
    @Override
    public Configuration getConfiguration(final ServletContext context) {
        initProvider(context);
        
        List<LanguageProcessor> lngProcessors = createLanguageProcessors();
        ConfigurationBuilder cfg = ConfigurationBuilder.begin(); //return cfg;
        
        append(cfg, pageBean.getPageTree().getChildren(), lngProcessors);
        
        return cfg;
    }
    
}