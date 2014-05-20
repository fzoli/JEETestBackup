package jsf.prettyfaces;

import entity.PageNode;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.RequestDispatcher;

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
            String originalURI = ((String) context.getExternalContext().getRequestMap().get(RequestDispatcher.FORWARD_REQUEST_URI)).substring(context.getExternalContext().getApplicationContextPath().length());
            System.out.println(originalURI);
            PageNode page = PrettyConfigurationProvider.getPage(context);
            String domain = context.getExternalContext().getRequestServerName();
            System.out.println(domain + " - " + page);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
