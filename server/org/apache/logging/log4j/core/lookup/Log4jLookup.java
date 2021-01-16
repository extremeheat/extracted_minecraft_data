package org.apache.logging.log4j.core.lookup;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "log4j",
   category = "Lookup"
)
public class Log4jLookup extends AbstractConfigurationAwareLookup {
   public static final String KEY_CONFIG_LOCATION = "configLocation";
   public static final String KEY_CONFIG_PARENT_LOCATION = "configParentLocation";
   private static final Logger LOGGER = StatusLogger.getLogger();

   public Log4jLookup() {
      super();
   }

   private static String asPath(URI var0) {
      return var0.getScheme() != null && !var0.getScheme().equals("file") ? var0.toString() : var0.getPath();
   }

   private static URI getParent(URI var0) throws URISyntaxException {
      String var1 = var0.toString();
      int var2 = var1.lastIndexOf(47);
      return var2 > -1 ? new URI(var1.substring(0, var2)) : new URI("../");
   }

   public String lookup(LogEvent var1, String var2) {
      if (this.configuration != null) {
         ConfigurationSource var3 = this.configuration.getConfigurationSource();
         File var4 = var3.getFile();
         if (var4 != null) {
            byte var6 = -1;
            switch(var2.hashCode()) {
            case -1277483753:
               if (var2.equals("configLocation")) {
                  var6 = 0;
               }
               break;
            case -1024117151:
               if (var2.equals("configParentLocation")) {
                  var6 = 1;
               }
            }

            switch(var6) {
            case 0:
               return var4.getAbsolutePath();
            case 1:
               return var4.getParentFile().getAbsolutePath();
            default:
               return null;
            }
         }

         URL var5 = var3.getURL();
         if (var5 != null) {
            try {
               byte var7 = -1;
               switch(var2.hashCode()) {
               case -1277483753:
                  if (var2.equals("configLocation")) {
                     var7 = 0;
                  }
                  break;
               case -1024117151:
                  if (var2.equals("configParentLocation")) {
                     var7 = 1;
                  }
               }

               switch(var7) {
               case 0:
                  return asPath(var5.toURI());
               case 1:
                  return asPath(getParent(var5.toURI()));
               default:
                  return null;
               }
            } catch (URISyntaxException var8) {
               LOGGER.error((Object)var8);
               return null;
            }
         }
      }

      return null;
   }
}
