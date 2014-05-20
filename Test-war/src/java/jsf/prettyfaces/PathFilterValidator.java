package jsf.prettyfaces;

import entity.PageMapping;
import entity.PageNode;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author zoli
 */
@FacesValidator(value = PathFilterValidator.NAME)
public class PathFilterValidator implements Validator {

    public static final String NAME = "pathFilterValidator";
    
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        try {
            PageMapping pageMapping = PrettyConfigurationProvider.getPageMapping(context);
            Locale locale = pageMapping.getLanguage().getLocale(context.getViewRoot().getLocale());
            context.getViewRoot().setLocale(locale);
            
            PageNode page = pageMapping.getPage();
            if (!page.getSites().isEmpty()) {
                String domain = context.getExternalContext().getRequestServerName();
                if (page.findSite(domain) == null) {
//                    ((HttpServletResponse) context.getExternalContext().getResponse()).sendError(HttpServletResponse.SC_NOT_FOUND);
//                    context.getExternalContext().setResponseStatus(404);
//                    context.responseComplete();
//                    return;
                    throw new ValidatorException(new FacesMessage("Filtered page", String.format("Domain '%s' is not joined to page '%s'", domain, pageMapping.getPermalink())));
                }
//                System.out.print(domain + " - ");
            }
//            System.out.println(page + ": " + pageMapping.getPermalink());
//            String originalURI = ((String) context.getExternalContext().getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI)).substring(context.getExternalContext().getApplicationContextPath().length());
//            System.out.println(originalURI);
//            System.out.println(locale);
//            System.out.println();
        }
        catch (ValidatorException ex) {
            throw ex;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
