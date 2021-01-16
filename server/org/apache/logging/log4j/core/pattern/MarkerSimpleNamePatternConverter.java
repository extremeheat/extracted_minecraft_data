package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "MarkerNamePatternConverter",
   category = "Converter"
)
@ConverterKeys({"markerSimpleName"})
@PerformanceSensitive({"allocation"})
public final class MarkerSimpleNamePatternConverter extends LogEventPatternConverter {
   private MarkerSimpleNamePatternConverter(String[] var1) {
      super("MarkerSimpleName", "markerSimpleName");
   }

   public static MarkerSimpleNamePatternConverter newInstance(String[] var0) {
      return new MarkerSimpleNamePatternConverter(var0);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      Marker var3 = var1.getMarker();
      if (var3 != null) {
         var2.append(var3.getName());
      }

   }
}
