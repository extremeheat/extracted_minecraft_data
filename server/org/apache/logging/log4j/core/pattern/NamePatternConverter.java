package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive({"allocation"})
public abstract class NamePatternConverter extends LogEventPatternConverter {
   private final NameAbbreviator abbreviator;

   protected NamePatternConverter(String var1, String var2, String[] var3) {
      super(var1, var2);
      if (var3 != null && var3.length > 0) {
         this.abbreviator = NameAbbreviator.getAbbreviator(var3[0]);
      } else {
         this.abbreviator = NameAbbreviator.getDefaultAbbreviator();
      }

   }

   protected final void abbreviate(String var1, StringBuilder var2) {
      this.abbreviator.abbreviate(var1, var2);
   }
}
