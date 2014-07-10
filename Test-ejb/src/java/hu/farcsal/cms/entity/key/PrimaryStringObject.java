package hu.farcsal.cms.entity.key;

/**
 *
 * @author zoli
 * @param <T>
 */
public abstract class PrimaryStringObject<T extends PrimaryStringObject> extends PrimaryObject<T, String> {
    
    @Deprecated
    protected PrimaryStringObject() {
        super();
    }
    
    protected PrimaryStringObject(Class<T> clazz) {
        super(clazz);
    }
    
}
