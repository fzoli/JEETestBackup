import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
 
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

/**
 * Test.
 * Source: http://www.mkyong.com/jsf2/jsf-2-internationalization-example/
 */
@ManagedBean(name="language")
@SessionScoped
public class LanguageBean implements Serializable{

    private static final long serialVersionUID = 1L;

    private String localeCode;

    private static Map<String,Object> countries;
    static{
        countries = new LinkedHashMap<String,Object>();
        countries.put("English", Locale.ENGLISH); //label, value
        countries.put("Hungarian", new Locale("hu","HU"));
    }

    public Map<String, Object> getCountriesInMap() {
        return countries;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void test() {
        System.out.println("language bean test method");
    }

    public void setLocaleCode(String localeCode) {
        System.out.println("set locale code: " + localeCode);
        this.localeCode = localeCode;
    }

    //value change event listener
    public void countryLocaleCodeChanged(ValueChangeEvent e) {
        System.out.println("locale changed");
        applyLocaleCode(e.getNewValue().toString());
    }

    public void applyLocaleCode(String newLocaleValue) {
        //loop country map to compare the locale code
        for (Map.Entry<String, Object> entry : countries.entrySet()) {
            if (entry.getValue().toString().equals(newLocaleValue)) {
                Locale locale = (Locale) entry.getValue();
                localeCode = newLocaleValue;
                FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
            }
        }
    }
    
}
