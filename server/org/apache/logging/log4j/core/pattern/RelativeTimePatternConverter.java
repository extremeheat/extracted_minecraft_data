package org.apache.logging.log4j.core.pattern;

import java.lang.management.ManagementFactory;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "RelativeTimePatternConverter",
   category = "Converter"
)
@ConverterKeys({"r", "relative"})
@PerformanceSensitive({"allocation"})
public class RelativeTimePatternConverter extends LogEventPatternConverter {
   private final long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();

   public RelativeTimePatternConverter() {
      super("Time", "time");
   }

   public static RelativeTimePatternConverter newInstance(String[] var0) {
      return new RelativeTimePatternConverter();
   }

   public void format(LogEvent var1, StringBuilder var2) {
      long var3 = var1.getTimeMillis();
      var2.append(var3 - this.startTime);
   }
}
