package jsf.prettyfaces;

import bean.PageBeanLocal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author zoli
 */
class Beans {
    
    public static PageBeanLocal lookupPageBeanLocal() {
        try {
            Context c = new InitialContext();
            return (PageBeanLocal) c.lookup("java:global/Test/Test-ejb/PageBean!bean.PageBeanLocal");
        }
        catch (NamingException ne) {
            Logger.getLogger(Beans.class.getName()).log(Level.SEVERE, "exception caught", ne);
            return null;
        }
    }
    
}
