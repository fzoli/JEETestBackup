package entity.key;

/**
 *
 * @author zoli
 * @param <T>
 */
public abstract class PrimaryStrObject<T extends PrimaryStrObject> extends PrimaryObject<T, String> {
    
    @Deprecated
    protected PrimaryStrObject() {
        super();
    }
    
    protected PrimaryStrObject(Class<T> clazz) {
        super(clazz);
    }
    
}
