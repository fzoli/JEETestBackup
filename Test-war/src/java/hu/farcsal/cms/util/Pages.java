package hu.farcsal.cms.util;

import hu.farcsal.cms.bean.Beans;
import hu.farcsal.cms.bean.PageBeanLocal;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.PageFilter;
import hu.farcsal.cms.entity.PageMapping;
import hu.farcsal.cms.entity.Site;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author zoli
 */
public class Pages {
    
    private static final PageBeanLocal pageBean = Beans.lookupPageBeanLocal();
    
    public enum FilterType {
        PAGE_DISABLED, SITE_UNKNOWN, SITE_DISABLED, SITE_FILTERED, PAGE_UNKNOWN
    }
    
    public static FilterType getFilterType(Site site, PageMapping pageMapping, List<PageFilter> pageFilters) {
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
    
    public static PageMapping getFirstPage(HttpServletRequest request, String defLanguage, boolean overrideLanguage, boolean allowIncrementedParams) {
        String language = request.getLocale().getLanguage();
        Site site = Site.findSiteByDomain(pageBean.getSites(), request.getServerName());
        return getFirstPage(site, overrideLanguage && defLanguage != null ? defLanguage : language, defLanguage, allowIncrementedParams);
    }
    
    private static PageMapping getFirstPage(Site site, String language, String defLanguage, boolean allowIncrementedParams) {
        if (site == null) return null;
        if (site.getHomePage() != null) {
            PageMapping pm = Page.findPageMapping(site.getHomePage(), language, defLanguage, true, allowIncrementedParams);
            if (pm != null) return pm;
        }
        return getFirstPage(site, pageBean.getPageTree(), language, defLanguage, allowIncrementedParams);
    }
    
    public static PageMapping getFirstPage(Site site, Page page, String language, String defLanguage, boolean allowIncrementedParams) {
        return getFirstPage(false, site, page, language, defLanguage, allowIncrementedParams);
    }
    
    private static PageMapping getFirstPage(boolean skipSiteChk, Site site, Page page, String language, String defLanguage, boolean allowIncrementedParams) {
        return getFirstPage(skipSiteChk, site, page, language, defLanguage, true, allowIncrementedParams);
    }
    
    public static PageMapping getFirstPage(boolean skipSiteChk, Site site, Page page, String language, String defLanguage, boolean skipParam, boolean allowIncrementedParams) {
        List<Page> pages = page.getOrderedChildren();
        List<PageFilter> pageFilters = pageBean.getPageFilters();
        for (Page p : pages) {
            PageMapping pm = Page.findPageMapping(p, language, defLanguage, skipParam, allowIncrementedParams);
            if (pm != null && getFilterType(skipSiteChk, site, pm, pageFilters) == null) {
                if (p.getRealViewPath(false) != null) return pm;
                return getFirstPage(skipSiteChk, site, p, language, defLanguage, skipParam, allowIncrementedParams);
            }
        }
        return null;
    }
    
}
