package org.apache.logging.log4j.core.pattern;

import java.util.Date;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "IntegerPatternConverter",
   category = "FileConverter"
)
@ConverterKeys({"i", "index"})
@PerformanceSensitive({"allocation"})
public final class IntegerPatternConverter extends AbstractPatternConverter implements ArrayPatternConverter {
   private static final IntegerPatternConverter INSTANCE = new IntegerPatternConverter();

   private IntegerPatternConverter() {
      super("Integer", "integer");
   }

   public static IntegerPatternConverter newInstance(String[] var0) {
      return INSTANCE;
   }

   public void format(StringBuilder var1, Object... var2) {
      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var2[var3] instanceof Integer) {
            this.format(var2[var3], var1);
            break;
         }

         if (var2[var3] instanceof NotANumber) {
            var1.append("\u0000");
            break;
         }
      }

   }

   public void format(Object var1, StringBuilder var2) {
      if (var1 instanceof Integer) {
         var2.append((Integer)var1);
      } else if (var1 instanceof Date) {
         var2.append(((Date)var1).getTime());
      }

   }
}
