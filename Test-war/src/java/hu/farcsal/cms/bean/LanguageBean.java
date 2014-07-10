package hu.farcsal.cms.bean;

import hu.farcsal.cms.entity.Language;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Test.
 * Source: http://www.mkyong.com/jsf2/jsf-2-internationalization-example/
 */
@ManagedBean(name="language")
@SessionScoped
public class LanguageBean implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private static final String[][] LNG_HINTS = {
        // ISO 639-1 code; Native name; International (English) name
        {"ab", "аҧсуа бызшәа", "Abkhaz"},
        {"aa", "Afaraf", "Afar"},
        {"af", "Afrikaans", "Afrikaans"},
        {"ak", "Akan", "Akan"},
        {"sq", "Shqip", "Albanian"},
        {"am", "አማርኛ", "Amharic"},
        {"ar", "العربية", "Arabic"},
        {"an", "aragonés", "Aragonese"},
        {"hy", "Հայերեն", "Armenian"},
        {"as", "অসমীয়া", "Assamese"},
        {"av", "авар мацӀ", "Avaric"},
        {"ae", "avesta", "Avestan"},
        {"ay", "aymar aru", "Aymara"},
        {"az", "azərbaycan dili", "Azerbaijani"},
        {"bm", "bamanankan", "Bambara"},
        {"ba", "башҡорт теле", "Bashkir"},
        {"eu", "euskara", "Basque"},
        {"be", "беларуская мова", "Belarusian"},
        {"bn", "বাংলা", "Bengali"},
        {"bh", "भोजपुरी", "Bihari"},
        {"bi", "Bislama", "Bislama"},
        {"bs", "bosanski jezik", "Bosnian"},
        {"br", "brezhoneg", "Breton"},
        {"bg", "български език", "Bulgarian"},
        {"my", "ဗမာစာ", "Burmese"},
        {"ca", "català", "Catalan"},
        {"ch", "Chamoru", "Chamorro"},
        {"ce", "нохчийн мотт", "Chechen"},
        {"ny", "chiCheŵa", "Chichewa"},
        {"zh", "中文 (Zhōngwén)", "Chinese"},
        {"cv", "чӑваш чӗлхи", "Chuvash"},
        {"kw", "Kernewek", "Cornish"},
        {"co", "corsu", "Corsican"},
        {"cr", "ᓀᐦᐃᔭᐍᐏᐣ", "Cree"},
        {"hr", "hrvatski jezik", "Croatian"},
        {"cs", "čeština", "Czech"},
        {"da", "dansk", "Danish"},
        {"dv", "ދިވެހި", "Divehi"},
        {"nl", "Nederlands", "Dutch"},
        {"dz", "རྫོང་ཁ", "Dzongkha"},
        {"en", "English", "English"},
        {"eo", "Esperanto", "Esperanto"},
        {"et", "eesti", "Estonian"},
        {"ee", "Eʋegbe", "Ewe"},
        {"fo", "føroyskt", "Faroese"},
        {"fj", "vosa Vakaviti", "Fijian"},
        {"fi", "suomi", "Finnish"},
        {"fr", "français", "French"},
        {"ff", "Fulfulde", "Fula"},
        {"gl", "galego", "Galician"},
        {"ka", "ქართული", "Georgian"},
        {"de", "Deutsch", "German"},
        {"el", "ελληνικά", "Greek"},
        {"gn", "Avañe'ẽ", "Guaraní"},
        {"gu", "ગુજરાતી", "Gujarati"},
        {"ht", "Kreyòl ayisyen", "Haitian"},
        {"ha", "هَوُسَ", "Hausa"},
        {"he", "עברית", "Hebrew"},
        {"hz", "Otjiherero", "Herero"},
        {"hi", "हिन्दी", "Hindi"},
        {"ho", "Hiri Motu", "Hiri Motu"},
        {"hu", "magyar", "Hungarian"},
        {"ia", "Interlingua", "Interlingua"},
        {"id", "Bahasa Indonesia", "Indonesian"},
        {"ie", "Interlingue", "Interlingue"},
        {"ga", "Gaeilge", "Irish"},
        {"ig", "Asụsụ Igbo", "Igbo"},
        {"ik", "Iñupiaq", "Inupiaq"},
        {"io", "Ido", "Ido"},
        {"is", "Íslenska", "Icelandic"},
        {"it", "italiano", "Italian"},
        {"iu", "ᐃᓄᒃᑎᑐᑦ", "Inuktitut"},
        {"ja", "日本語 (にほんご)", "Japanese"},
        {"jv", "basa Jawa", "Javanese"},
        {"kl", "kalaallisut", "Kalaallisut"},
        {"kn", "ಕನ್ನಡ", "Kannada"},
        {"kr", "Kanuri", "Kanuri"},
        {"ks", "कश्मीरी", "Kashmiri"},
        {"kk", "қазақ тілі", "Kazakh"},
        {"km", "ខ្មែរ", "Khmer"},
        {"ki", "Gĩkũyũ", "Kikuyu"},
        {"rw", "Ikinyarwanda", "Kinyarwanda"},
        {"ky", "Кыргызча", "Kyrgyz"},
        {"kv", "коми кыв", "Komi"},
        {"kg", "Kikongo", "Kongo"},
        {"ko", "한국어", "Korean"},
        {"ku", "Kurdî", "Kurdish"},
        {"kj", "Kuanyama", "Kwanyama"},
        {"la", "latine", "Latin"},
        {"lb", "Lëtzebuergesch", "Luxembourgish"},
        {"lg", "Luganda", "Ganda"},
        {"li", "Limburgs", "Limburgish"},
        {"ln", "Lingála", "Lingala"},
        {"lo", "ພາສາລາວ", "Lao"},
        {"lt", "lietuvių kalba", "Lithuanian"},
        {"lu", "Tshiluba", "Luba-Katanga"},
        {"lv", "latviešu valoda", "Latvian"},
        {"gv", "Gaelg", "Manx"},
        {"mk", "македонски јазик", "Macedonian"},
        {"mg", "fiteny malagasy", "Malagasy"},
        {"ms", "bahasa Melayu", "Malay"},
        {"ml", "മലയാളം", "Malayalam"},
        {"mt", "Malti", "Maltese"},
        {"mi", "te reo Māori", "Māori"},
        {"mr", "मराठी", "Marathi (Marāṭhī)"},
        {"mh", "Kajin M̧ajeļ", "Marshallese"},
        {"mn", "монгол", "Mongolian"},
        {"na", "Ekakairũ Naoero", "Nauru"},
        {"nv", "Diné bizaad", "Navajo"},
        {"nd", "isiNdebele", "Northern Ndebele"},
        {"ne", "नेपाली", "Nepali"},
        {"ng", "Owambo", "Ndonga"},
        {"nb", "Norsk bokmål", "Norwegian Bokmål"},
        {"nn", "Norsk nynorsk", "Norwegian Nynorsk"},
        {"no", "Norsk", "Norwegian"},
        {"ii", "ꆈꌠ꒿ Nuosuhxop", "Nuosu"},
        {"nr", "isiNdebele", "Southern Ndebele"},
        {"oc", "occitan", "Occitan"},
        {"oj", "ᐊᓂᔑᓈᐯᒧᐎᓐ", "Ojibwe"},
        {"cu", "ѩзыкъ словѣньскъ", "Old Church Slavonic"},
        {"om", "Afaan Oromoo", "Oromo"},
        {"or", "ଓଡ଼ିଆ", "Oriya"},
        {"os", "ирон æвзаг", "Ossetian"},
        {"pa", "ਪੰਜਾਬੀ", "Panjabi"},
        {"pi", "पाऴि", "Pāli"},
        {"fa", "فارسی", "Persian (Farsi)"},
        {"pl", "język polski", "Polish"},
        {"ps", "پښتو", "Pashto"},
        {"pt", "português", "Portuguese"},
        {"qu", "Runa Simi", "Quechua"},
        {"rm", "rumantsch grischun", "Romansh"},
        {"rn", "Ikirundi", "Kirundi"},
        {"ro", "limba română", "Romanian"},
        {"ru", "русский язык", "Russian"},
        {"sa", "संस्कृतम्", "Sanskrit (Saṁskṛta)"},
        {"sc", "sardu", "Sardinian"},
        {"sd", "सिन्धी", "Sindhi"},
        {"se", "Davvisámegiella", "Northern Sami"},
        {"sm", "gagana fa'a Samoa", "Samoan"},
        {"sg", "yângâ tî sängö", "Sango"},
        {"sr", "српски језик", "Serbian"},
        {"gd", "Gàidhlig", "Scottish Gaelic"},
        {"sn", "chiShona", "Shona"},
        {"si", "සිංහල", "Sinhala"},
        {"sk", "slovenčina", "Slovak"},
        {"sl", "slovenski jezik", "Slovene"},
        {"so", "Soomaaliga", "Somali"},
        {"st", "Sesotho", "Southern Sotho"},
        {"es", "español", "Spanish"},
        {"su", "Basa Sunda", "Sundanese"},
        {"sw", "Kiswahili", "Swahili"},
        {"ss", "SiSwati", "Swati"},
        {"sv", "Svenska", "Swedish"},
        {"ta", "தமிழ்", "Tamil"},
        {"te", "తెలుగు", "Telugu"},
        {"tg", "тоҷикӣ", "Tajik"},
        {"th", "ไทย", "Thai"},
        {"ti", "ትግርኛ", "Tigrinya"},
        {"bo", "བོད་ཡིག", "Tibetan Standard"},
        {"tk", "Türkmen", "Turkmen"},
        {"tl", "Wikang Tagalog", "Tagalog"},
        {"tn", "Setswana", "Tswana"},
        {"to", "faka Tonga", "Tonga"},
        {"tr", "Türkçe", "Turkish"},
        {"ts", "Xitsonga", "Tsonga"},
        {"tt", "татар теле", "Tatar"},
        {"tw", "Twi", "Twi"},
        {"ty", "Reo Tahiti", "Tahitian"},
        {"ug", "Uyƣurqə", "Uyghur"},
        {"uk", "українська мова", "Ukrainian"},
        {"ur", "اردو", "Urdu"},
        {"uz", "O‘zbek", "Uzbek"},
        {"ve", "Tshivenḓa", "Venda"},
        {"vi", "Tiếng Việt", "Vietnamese"},
        {"vo", "Volapük", "Volapük"},
        {"wa", "walon", "Walloon"},
        {"cy", "Cymraeg", "Welsh"},
        {"wo", "Wollof", "Wolof"},
        {"fy", "Frysk", "Western Frisian"},
        {"xh", "isiXhosa", "Xhosa"},
        {"yi", "ייִדיש", "Yiddish"},
        {"yo", "Yorùbá", "Yoruba"},
        {"za", "Saɯ cueŋƅ", "Zhuang"},
        {"zu", "isiZulu", "Zulu"}
    };
    
    @EJB
    private PageBeanLocal bean;
    
    private String lngCodeCms;
    private Language lngCms;
    
    public List<Language> getLanguages() {
        return bean.getLanguages();
    }

    public Boolean getCodeHintInvalid() {
        return !validateCodeHint();
    }

    public String getCmsLanguageCode() {
        return lngCodeCms;
    }
    
    public void test() {
        System.out.println("language bean test method");
    }

    public void testSave() {
        System.out.println("language bean test save method");
        RequestContext rcontext = RequestContext.getCurrentInstance();
        FacesContext fcontext = FacesContext.getCurrentInstance();
        boolean saved = validateCodeHint();
        if (rcontext != null) {
            rcontext.addCallbackParam("saved", saved);
        }
        if (fcontext != null) {
            if (!saved) fcontext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid language code", "Language is not saved."));
        }
    }
    
    public void setLocaleCode(String localeCode) {
        System.out.println("set locale code: " + localeCode);
    }

    public void cmsLanguageChanged(ValueChangeEvent e) {
        setCmsLanguage(e.getNewValue().toString());
    }

    public void setCmsLanguage(String lngCode) {
        if (lngCode == null) return;
        for (Language l : getLanguages()) {
            if (l != null && l.getCode().equals(lngCode)) {
                lngCodeCms = lngCode;
                lngCms = l;
                System.out.println("cms lng changed to " + l.getName());
                break;
            }
        }
    }
    
    public boolean validateCodeHint() {
        return validateLanguageCode(codeHint);
    }
    
    public boolean validateLanguageCode(String value) {
        if (value == null) return false;
        Pattern p = Pattern.compile("^[a-z]{2}$");
        return p.matcher(value).matches();
    }
    
    public List<String> completeText(String query) {
        List<String> results = new ArrayList<>();
        for (Language l : getLanguages()) {
            String lng = l.getName();
            if (lng.toLowerCase().startsWith(query.toLowerCase())) results.add(lng);
        }
        return results;
    }
    
    public List<String> completeInternationalHint(String query) {
        return completeHint(query, 2);
    }
    
    public List<String> completeNativeHint(String query) {
        return completeHint(query, 1);
    }
    
    private String codeHint;
    
    public List<String> completeCodeHint(String query) {
        codeHint = query;
        System.out.println("typed code hint: " + codeHint);
        return completeHint(query, 0);
    }
    
    public void hintSelected(SelectEvent event) {
        codeHint = event.getObject().toString();
        System.out.println("selected code hint: " + codeHint);
    }
    
    private List<String> completeHint(String query, int hintIndex) {
        List<String> results = new ArrayList<>();
        for (String[] hint : LNG_HINTS) {
            String lng = hint[hintIndex];
            if (lng.toLowerCase().startsWith(query.toLowerCase())) results.add(lng);
        }
        Collections.sort(results);
        return results;
    }
    
}
