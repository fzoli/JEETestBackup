package hu.farcsal.util;

import javax.servlet.ServletContext;

/**
 *
 * @author zoli
 */
public class WebConfig {
    
    public static boolean isTrue(ServletContext context, String paramName) {
        String value = context.getInitParameter(paramName);
        if (value == null) return false;
        return value.equalsIgnoreCase(Boolean.toString(true));
    }
    
}
