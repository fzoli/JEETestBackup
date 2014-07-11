package hu.farcsal.cms.rewrite;

import java.util.Map;
import java.util.WeakHashMap;
import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationCacheProvider;

/**
 *
 * @author zoli
 */
public class ConfigurationCacheProvider extends HttpConfigurationCacheProvider {

   private static final String KEY = ConfigurationCacheProvider.class.getName() + "_cachedConfig";

   private static final Map<ConfigurationCacheProvider, Boolean> RESETS = new WeakHashMap<>();
   
   public static void reset() {
       RESETS.clear();
   }
   
   @Override
   public Configuration getConfiguration(ServletContext context) {
      if (needReset()) {
         setConfiguration(context, null);
         return null;
      }
      return (Configuration) context.getAttribute(KEY);
   }

   @Override
   public void setConfiguration(ServletContext context, Configuration configuration) {
      context.setAttribute(KEY, configuration);
   }

   private boolean needReset() {
       Boolean b = RESETS.get(this);
       boolean ret = b == null ? true : b;
       if (ret) RESETS.put(this, false);
       return ret;
   }
   
   @Override
   public int priority() {
      return 0;
   }
   
}
