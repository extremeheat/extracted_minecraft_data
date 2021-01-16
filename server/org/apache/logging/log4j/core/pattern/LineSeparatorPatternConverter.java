package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "LineSeparatorPatternConverter",
   category = "Converter"
)
@ConverterKeys({"n"})
@PerformanceSensitive({"allocation"})
public final class LineSeparatorPatternConverter extends LogEventPatternConverter {
   private static final LineSeparatorPatternConverter INSTANCE = new LineSeparatorPatternConverter();
   private final String lineSep;

   private LineSeparatorPatternConverter() {
      super("Line Sep", "lineSep");
      this.lineSep = Strings.LINE_SEPARATOR;
   }

   public static LineSeparatorPatternConverter newInstance(String[] var0) {
      return INSTANCE;
   }

   public void format(LogEvent var1, StringBuilder var2) {
      var2.append(this.lineSep);
   }
}
