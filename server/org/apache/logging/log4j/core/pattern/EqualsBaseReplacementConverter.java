package org.apache.logging.log4j.core.pattern;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public abstract class EqualsBaseReplacementConverter extends LogEventPatternConverter {
   private final List<PatternFormatter> formatters;
   private final List<PatternFormatter> substitutionFormatters;
   private final String substitution;
   private final String testString;

   protected EqualsBaseReplacementConverter(String var1, String var2, List<PatternFormatter> var3, String var4, String var5, PatternParser var6) {
      super(var1, var2);
      this.testString = var4;
      this.substitution = var5;
      this.formatters = var3;
      this.substitutionFormatters = var5.contains("%") ? var6.parse(var5) : null;
   }

   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = var2.length();

      for(int var4 = 0; var4 < this.formatters.size(); ++var4) {
         PatternFormatter var5 = (PatternFormatter)this.formatters.get(var4);
         var5.format(var1, var2);
      }

      if (this.equals(this.testString, var2, var3, var2.length() - var3)) {
         var2.setLength(var3);
         this.parseSubstitution(var1, var2);
      }

   }

   protected abstract boolean equals(String var1, StringBuilder var2, int var3, int var4);

   void parseSubstitution(LogEvent var1, StringBuilder var2) {
      if (this.substitutionFormatters != null) {
         for(int var3 = 0; var3 < this.substitutionFormatters.size(); ++var3) {
            PatternFormatter var4 = (PatternFormatter)this.substitutionFormatters.get(var3);
            var4.format(var1, var2);
         }
      } else {
         var2.append(this.substitution);
      }

   }
}
