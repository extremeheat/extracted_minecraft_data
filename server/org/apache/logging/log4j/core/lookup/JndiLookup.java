package org.apache.logging.log4j.core.lookup;

import javax.naming.NamingException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.net.JndiManager;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "jndi",
   category = "Lookup"
)
public class JndiLookup extends AbstractLookup {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final Marker LOOKUP = MarkerManager.getMarker("LOOKUP");
   static final String CONTAINER_JNDI_RESOURCE_PATH_PREFIX = "java:comp/env/";

   public JndiLookup() {
      super();
   }

   public String lookup(LogEvent var1, String var2) {
      if (var2 == null) {
         return null;
      } else {
         String var3 = this.convertJndiName(var2);

         try {
            JndiManager var4 = JndiManager.getDefaultManager();
            Throwable var5 = null;

            String var7;
            try {
               Object var6 = var4.lookup(var3);
               var7 = var6 == null ? null : String.valueOf(var6);
            } catch (Throwable var17) {
               var5 = var17;
               throw var17;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var16) {
                        var5.addSuppressed(var16);
                     }
                  } else {
                     var4.close();
                  }
               }

            }

            return var7;
         } catch (NamingException var19) {
            LOGGER.warn((Marker)LOOKUP, (String)"Error looking up JNDI resource [{}].", var3, var19);
            return null;
         }
      }
   }

   private String convertJndiName(String var1) {
      return !var1.startsWith("java:comp/env/") && var1.indexOf(58) == -1 ? "java:comp/env/" + var1 : var1;
   }
}
