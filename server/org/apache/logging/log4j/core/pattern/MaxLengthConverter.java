package org.apache.logging.log4j.core.pattern;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "maxLength",
   category = "Converter"
)
@ConverterKeys({"maxLength", "maxLen"})
@PerformanceSensitive({"allocation"})
public final class MaxLengthConverter extends LogEventPatternConverter {
   private final List<PatternFormatter> formatters;
   private final int maxLength;

   public static MaxLengthConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length != 2) {
         LOGGER.error((String)"Incorrect number of options on maxLength: expected 2 received {}: {}", (Object)var1.length, (Object)var1);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on maxLength");
         return null;
      } else if (var1[1] == null) {
         LOGGER.error("No length supplied on maxLength");
         return null;
      } else {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         List var3 = var2.parse(var1[0]);
         return new MaxLengthConverter(var3, AbstractAppender.parseInt(var1[1], 100));
      }
   }

   private MaxLengthConverter(List<PatternFormatter> var1, int var2) {
      super("MaxLength", "maxLength");
      this.maxLength = var2;
      this.formatters = var1;
      LOGGER.trace((String)"new MaxLengthConverter with {}", (Object)var2);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = var2.length();

      for(int var4 = 0; var4 < this.formatters.size(); ++var4) {
         PatternFormatter var5 = (PatternFormatter)this.formatters.get(var4);
         var5.format(var1, var2);
         if (var2.length() > var3 + this.maxLength) {
            break;
         }
      }

      if (var2.length() > var3 + this.maxLength) {
         var2.setLength(var3 + this.maxLength);
         if (this.maxLength > 20) {
            var2.append("...");
         }
      }

   }
}
