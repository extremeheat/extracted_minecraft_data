package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "NanoTimePatternConverter",
   category = "Converter"
)
@ConverterKeys({"N", "nano"})
@PerformanceSensitive({"allocation"})
public final class NanoTimePatternConverter extends LogEventPatternConverter {
   private NanoTimePatternConverter(String[] var1) {
      super("Nanotime", "nanotime");
   }

   public static NanoTimePatternConverter newInstance(String[] var0) {
      return new NanoTimePatternConverter(var0);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      var2.append(var1.getNanoTime());
   }
}
