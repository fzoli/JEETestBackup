package hu.farcsal.cms.rewrite;

/**
 *
 * @author zoli
 */
enum ConfigOrder {
    
    ZIP_OUTPUT(-100000),
    DATABASE,
    HOME_PAGE;
    
    private static final int ADDITION = 10;

    private final Integer priority;
    
    private ConfigOrder() {
        this(null);
    }
    
    private ConfigOrder(Integer priority) {
        this.priority = priority;
    }
    
    public int getPriority() {
        if (priority != null) return priority;
        return ordinal() + ADDITION;
    }
    
}
