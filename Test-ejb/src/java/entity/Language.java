package entity;

import entity.key.PrimaryStrObject;
import java.util.Locale;
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
public class Language extends PrimaryStrObject<Language> {
    
    @Id
    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;

    protected Language() {
        super(Language.class);
    }

    public Language(String code, String name) {
        this();
        this.code = code;
        this.name = name;
    }

    @Override
    public String getId() {
        return getCode();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
    
    public Locale getLocale() {
        return getLocale(null);
    }
    
    public Locale getLocale(Locale defLocale) {
        return defLocale == null ? new Locale(getCode()) : new Locale(getCode(), defLocale.getCountry());
    }
    
}
