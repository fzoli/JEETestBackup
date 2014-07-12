package hu.farcsal.cms.rewrite;

/**
 *
 * @author zoli
 */
enum ConfigOrder {
    
    VIEWLESS_PAGE,
    DATABASE,
    HOME_PAGE;
    
    private static final int ADDITION = 10;
    
    public int getPriority() {
        return ordinal() + ADDITION;
    }
    
}
