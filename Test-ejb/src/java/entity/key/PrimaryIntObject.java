package entity.key;

/**
 *
 * @author zoli
 * @param <T>
 */
public abstract class PrimaryIntObject<T extends PrimaryIntObject> extends PrimaryObject<T, Long> {
    
    @Deprecated
    protected PrimaryIntObject() {
        super();
    }
    
    protected PrimaryIntObject(Class<T> clazz) {
        super(clazz);
    }
    
}
