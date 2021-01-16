package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "sys",
   category = "Lookup"
)
public class SystemPropertiesLookup extends AbstractLookup {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final Marker LOOKUP = MarkerManager.getMarker("LOOKUP");

   public SystemPropertiesLookup() {
      super();
   }

   public String lookup(LogEvent var1, String var2) {
      try {
         return System.getProperty(var2);
      } catch (Exception var4) {
         LOGGER.warn((Marker)LOOKUP, (String)"Error while getting system property [{}].", var2, var4);
         return null;
      }
   }
}
