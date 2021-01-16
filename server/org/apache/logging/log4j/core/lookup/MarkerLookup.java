package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "marker",
   category = "Lookup"
)
public class MarkerLookup extends AbstractLookup {
   static final String MARKER = "marker";

   public MarkerLookup() {
      super();
   }

   public String lookup(LogEvent var1, String var2) {
      Marker var3 = var1 == null ? null : var1.getMarker();
      return var3 == null ? null : var3.getName();
   }

   public String lookup(String var1) {
      return MarkerManager.exists(var1) ? var1 : null;
   }
}
