package org.apache.logging.log4j.core.pattern;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "notEmpty",
   category = "Converter"
)
@ConverterKeys({"notEmpty", "varsNotEmpty", "variablesNotEmpty"})
@PerformanceSensitive({"allocation"})
public final class VariablesNotEmptyReplacementConverter extends LogEventPatternConverter {
   private final List<PatternFormatter> formatters;

   private VariablesNotEmptyReplacementConverter(List<PatternFormatter> var1) {
      super("notEmpty", "notEmpty");
      this.formatters = var1;
   }

   public static VariablesNotEmptyReplacementConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length != 1) {
         LOGGER.error("Incorrect number of options on varsNotEmpty. Expected 1 received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on varsNotEmpty");
         return null;
      } else {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         List var3 = var2.parse(var1[0]);
         return new VariablesNotEmptyReplacementConverter(var3);
      }
   }

   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = var2.length();
      boolean var4 = true;
      boolean var5 = false;

      for(int var6 = 0; var6 < this.formatters.size(); ++var6) {
         PatternFormatter var7 = (PatternFormatter)this.formatters.get(var6);
         int var8 = var2.length();
         var7.format(var1, var2);
         if (var7.getConverter().isVariable()) {
            var5 = true;
            var4 = var4 && var2.length() == var8;
         }
      }

      if (!var5 || var4) {
         var2.setLength(var3);
      }

   }
}
