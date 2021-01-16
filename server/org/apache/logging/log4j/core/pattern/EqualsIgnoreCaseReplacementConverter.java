package org.apache.logging.log4j.core.pattern;

import java.util.List;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

@Plugin(
   name = "equalsIgnoreCase",
   category = "Converter"
)
@ConverterKeys({"equalsIgnoreCase"})
@PerformanceSensitive({"allocation"})
public final class EqualsIgnoreCaseReplacementConverter extends EqualsBaseReplacementConverter {
   public static EqualsIgnoreCaseReplacementConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length != 3) {
         LOGGER.error("Incorrect number of options on equalsIgnoreCase. Expected 3 received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on equalsIgnoreCase");
         return null;
      } else if (var1[1] == null) {
         LOGGER.error("No test string supplied on equalsIgnoreCase");
         return null;
      } else if (var1[2] == null) {
         LOGGER.error("No substitution supplied on equalsIgnoreCase");
         return null;
      } else {
         String var2 = var1[1];
         PatternParser var3 = PatternLayout.createPatternParser(var0);
         List var4 = var3.parse(var1[0]);
         return new EqualsIgnoreCaseReplacementConverter(var4, var2, var1[2], var3);
      }
   }

   private EqualsIgnoreCaseReplacementConverter(List<PatternFormatter> var1, String var2, String var3, PatternParser var4) {
      super("equalsIgnoreCase", "equalsIgnoreCase", var1, var2, var3, var4);
   }

   protected boolean equals(String var1, StringBuilder var2, int var3, int var4) {
      return StringBuilders.equalsIgnoreCase(var1, 0, var1.length(), var2, var3, var4);
   }
}
