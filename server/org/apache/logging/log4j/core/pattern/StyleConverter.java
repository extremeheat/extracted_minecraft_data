package org.apache.logging.log4j.core.pattern;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "style",
   category = "Converter"
)
@ConverterKeys({"style"})
@PerformanceSensitive({"allocation"})
public final class StyleConverter extends LogEventPatternConverter implements AnsiConverter {
   private final List<PatternFormatter> patternFormatters;
   private final boolean noAnsi;
   private final String style;
   private final String defaultStyle;

   private StyleConverter(List<PatternFormatter> var1, String var2, boolean var3) {
      super("style", "style");
      this.patternFormatters = var1;
      this.style = var2;
      this.defaultStyle = AnsiEscape.getDefaultStyle();
      this.noAnsi = var3;
   }

   public static StyleConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length < 1) {
         LOGGER.error("Incorrect number of options on style. Expected at least 1, received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on style");
         return null;
      } else if (var1[1] == null) {
         LOGGER.error("No style attributes provided");
         return null;
      } else {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         List var3 = var2.parse(var1[0]);
         String var4 = AnsiEscape.createSequence(var1[1].split(Patterns.COMMA_SEPARATOR));
         boolean var5 = Arrays.toString(var1).contains("disableAnsi=true");
         boolean var6 = Arrays.toString(var1).contains("noConsoleNoAnsi=true");
         boolean var7 = var5 || var6 && System.console() == null;
         return new StyleConverter(var3, var4, var7);
      }
   }

   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = 0;
      int var4 = 0;
      if (!this.noAnsi) {
         var3 = var2.length();
         var2.append(this.style);
         var4 = var2.length();
      }

      int var5 = 0;

      for(int var6 = this.patternFormatters.size(); var5 < var6; ++var5) {
         ((PatternFormatter)this.patternFormatters.get(var5)).format(var1, var2);
      }

      if (!this.noAnsi) {
         if (var2.length() == var4) {
            var2.setLength(var3);
         } else {
            var2.append(this.defaultStyle);
         }
      }

   }

   public boolean handlesThrowable() {
      Iterator var1 = this.patternFormatters.iterator();

      PatternFormatter var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (PatternFormatter)var1.next();
      } while(!var2.handlesThrowable());

      return true;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(super.toString());
      var1.append("[style=");
      var1.append(this.style);
      var1.append(", defaultStyle=");
      var1.append(this.defaultStyle);
      var1.append(", patternFormatters=");
      var1.append(this.patternFormatters);
      var1.append(", noAnsi=");
      var1.append(this.noAnsi);
      var1.append(']');
      return var1.toString();
   }
}
