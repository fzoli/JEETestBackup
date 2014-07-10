package hu.farcsal.cms.entity.key;

/**
 *
 * @author zoli
 * @param <T>
 */
public abstract class PrimaryLongObject<T extends PrimaryLongObject> extends PrimaryObject<T, Long> {
    
    @Deprecated
    protected PrimaryLongObject() {
        super();
    }
    
    protected PrimaryLongObject(Class<T> clazz) {
        super(clazz);
    }
    
}
