package hu.farcsal.cms.entity.key;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author zoli
 * @param <T>
 * @param <K>
 */
public abstract class PrimaryObject<T extends PrimaryObject, K extends Serializable> implements Serializable {
    
    private final transient Class<T> clazz;
    
    protected abstract K getId();

    @Deprecated
    protected PrimaryObject() {
        this.clazz = null;
    }
    
    protected PrimaryObject(Class<T> clazz) {
        if (clazz == null) throw new NullPointerException("clazz can not be null");
        this.clazz = clazz;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getId() != null ? getId().hashCode() : 0);
        return hash;
    }

    /**
     * Warning - this method won't work in the case the id fields are not set.
     * @param object
     * @return 
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PrimaryObject)) return false;
        if (clazz != null && !clazz.isInstance(object)) return false;
        return Objects.equals(getId(), ((PrimaryObject) object).getId());
    }
    
}
