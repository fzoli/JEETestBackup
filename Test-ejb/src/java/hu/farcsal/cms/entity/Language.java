package hu.farcsal.cms.entity;

import hu.farcsal.cms.entity.key.PrimaryStringObject;
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
public class Language extends PrimaryStringObject<Language> {
    
    @Id
    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "english-name", nullable = false)
    private String englishName;
    
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

    public String getEnglishName() {
        return englishName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }
    
    public String getText() {
        if (getName() == null) return getEnglishName();
        if ((getEnglishName() == null || getEnglishName().trim().isEmpty()) || getName().equals(getEnglishName())) return getName();
        return getName() + " (" + getEnglishName() + ")";
    }
    
    public Locale getLocale() {
        return getLocale(null);
    }
    
    public Locale getLocale(Locale defLocale) {
        return defLocale == null ? new Locale(getCode()) : new Locale(getCode(), defLocale.getCountry());
    }
    
}
