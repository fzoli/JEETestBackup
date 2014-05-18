package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="languages")
public class Language implements Serializable {
    
    @Id
    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;

    protected Language() {
    }

    public Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return code == null ? 0 : code.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Language)) return false;
        return java.util.Objects.equals(code, ((Language) object).code);
    }
    
}
