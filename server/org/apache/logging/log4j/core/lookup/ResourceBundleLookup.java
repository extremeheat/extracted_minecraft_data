package org.apache.logging.log4j.core.lookup;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "bundle",
   category = "Lookup"
)
public class ResourceBundleLookup extends AbstractLookup {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final Marker LOOKUP = MarkerManager.getMarker("LOOKUP");

   public ResourceBundleLookup() {
      super();
   }

   public String lookup(LogEvent var1, String var2) {
      if (var2 == null) {
         return null;
      } else {
         String[] var3 = var2.split(":");
         int var4 = var3.length;
         if (var4 != 2) {
            LOGGER.warn((Marker)LOOKUP, (String)"Bad ResourceBundle key format [{}]. Expected format is BundleName:KeyName.", (Object)var2);
            return null;
         } else {
            String var5 = var3[0];
            String var6 = var3[1];

            try {
               return ResourceBundle.getBundle(var5).getString(var6);
            } catch (MissingResourceException var8) {
               LOGGER.warn((Marker)LOOKUP, (String)"Error looking up ResourceBundle [{}].", var5, var8);
               return null;
            }
         }
      }
   }
}
