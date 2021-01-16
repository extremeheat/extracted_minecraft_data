package org.apache.logging.log4j.core.pattern;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "encode",
   category = "Converter"
)
@ConverterKeys({"enc", "encode"})
@PerformanceSensitive({"allocation"})
public final class EncodingPatternConverter extends LogEventPatternConverter {
   private final List<PatternFormatter> formatters;

   private EncodingPatternConverter(List<PatternFormatter> var1) {
      super("encode", "encode");
      this.formatters = var1;
   }

   public static EncodingPatternConverter newInstance(Configuration var0, String[] var1) {
      if (var1.length != 1) {
         LOGGER.error("Incorrect number of options on escape. Expected 1, received " + var1.length);
         return null;
      } else if (var1[0] == null) {
         LOGGER.error("No pattern supplied on escape");
         return null;
      } else {
         PatternParser var2 = PatternLayout.createPatternParser(var0);
         List var3 = var2.parse(var1[0]);
         return new EncodingPatternConverter(var3);
      }
   }

   public void format(LogEvent var1, StringBuilder var2) {
      int var3 = var2.length();

      int var4;
      for(var4 = 0; var4 < this.formatters.size(); ++var4) {
         ((PatternFormatter)this.formatters.get(var4)).format(var1, var2);
      }

      for(var4 = var2.length() - 1; var4 >= var3; --var4) {
         char var5 = var2.charAt(var4);
         switch(var5) {
         case '\n':
            var2.setCharAt(var4, '\\');
            var2.insert(var4 + 1, 'n');
            break;
         case '\r':
            var2.setCharAt(var4, '\\');
            var2.insert(var4 + 1, 'r');
            break;
         case '"':
            var2.setCharAt(var4, '&');
            var2.insert(var4 + 1, "quot;");
            break;
         case '&':
            var2.setCharAt(var4, '&');
            var2.insert(var4 + 1, "amp;");
            break;
         case '\'':
            var2.setCharAt(var4, '&');
            var2.insert(var4 + 1, "apos;");
            break;
         case '/':
            var2.setCharAt(var4, '&');
            var2.insert(var4 + 1, "#x2F;");
            break;
         case '<':
            var2.setCharAt(var4, '&');
            var2.insert(var4 + 1, "lt;");
            break;
         case '>':
            var2.setCharAt(var4, '&');
            var2.insert(var4 + 1, "gt;");
         }
      }

   }
}
