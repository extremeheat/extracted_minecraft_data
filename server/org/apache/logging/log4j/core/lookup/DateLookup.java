package org.apache.logging.log4j.core.lookup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "date",
   category = "Lookup"
)
public class DateLookup implements StrLookup {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final Marker LOOKUP = MarkerManager.getMarker("LOOKUP");

   public DateLookup() {
      super();
   }

   public String lookup(String var1) {
      return this.formatDate(System.currentTimeMillis(), var1);
   }

   public String lookup(LogEvent var1, String var2) {
      return this.formatDate(var1.getTimeMillis(), var2);
   }

   private String formatDate(long var1, String var3) {
      Object var4 = null;
      if (var3 != null) {
         try {
            var4 = new SimpleDateFormat(var3);
         } catch (Exception var6) {
            LOGGER.error((Marker)LOOKUP, (String)"Invalid date format: [{}], using default", var3, var6);
         }
      }

      if (var4 == null) {
         var4 = DateFormat.getInstance();
      }

      return ((DateFormat)var4).format(new Date(var1));
   }
}
