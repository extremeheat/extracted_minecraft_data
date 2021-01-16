package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "RootThrowablePatternConverter",
   category = "Converter"
)
@ConverterKeys({"rEx", "rThrowable", "rException"})
public final class RootThrowablePatternConverter extends ThrowablePatternConverter {
   private RootThrowablePatternConverter(String[] var1) {
      super("RootThrowable", "throwable", var1);
   }

   public static RootThrowablePatternConverter newInstance(String[] var0) {
      return new RootThrowablePatternConverter(var0);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      ThrowableProxy var3 = var1.getThrownProxy();
      Throwable var4 = var1.getThrown();
      if (var4 != null && this.options.anyLines()) {
         if (var3 == null) {
            super.format(var1, var2);
            return;
         }

         String var5 = var3.getCauseStackTraceAsString(this.options.getIgnorePackages());
         int var6 = var2.length();
         if (var6 > 0 && !Character.isWhitespace(var2.charAt(var6 - 1))) {
            var2.append(' ');
         }

         if (this.options.allLines() && Strings.LINE_SEPARATOR.equals(this.options.getSeparator())) {
            var2.append(var5);
         } else {
            StringBuilder var7 = new StringBuilder();
            String[] var8 = var5.split(Strings.LINE_SEPARATOR);
            int var9 = this.options.minLines(var8.length) - 1;

            for(int var10 = 0; var10 <= var9; ++var10) {
               var7.append(var8[var10]);
               if (var10 < var9) {
                  var7.append(this.options.getSeparator());
               }
            }

            var2.append(var7.toString());
         }
      }

   }
}
