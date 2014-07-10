package hu.farcsal.cms.bean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import hu.farcsal.log.Log;

/**
 *
 * @author zoli
 */
public class Beans {
    
    private static final Log LOGGER = Log.getLogger(Beans.class);
    
    public static PageBeanLocal lookupPageBeanLocal() {
        try {
            Context c = new InitialContext();
            return (PageBeanLocal) c.lookup("java:global/Test/Test-ejb/PageBean!hu.farcsal.cms.bean.PageBeanLocal");
        }
        catch (NamingException ne) {
            LOGGER.e("exception caught", ne);
            return null;
        }
    }
    
}
