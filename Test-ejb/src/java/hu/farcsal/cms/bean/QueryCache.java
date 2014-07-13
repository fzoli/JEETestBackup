package hu.farcsal.cms.bean;

import hu.farcsal.cms.entity.Language;
import hu.farcsal.cms.entity.Page;
import hu.farcsal.cms.entity.PageFilter;
import hu.farcsal.cms.entity.Site;
import java.util.List;

/**
 * Temporary solution for database caching - TODO
 * @author zoli
 */
class QueryCache {
    
    static List<PageFilter> pageFilters;
    
    static List<Language> languages;
    
    static List<Site> sites;
    
    static Page pageTree;
    
}
