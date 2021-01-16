package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "FileDatePatternConverter",
   category = "FileConverter"
)
@ConverterKeys({"d", "date"})
@PerformanceSensitive({"allocation"})
public final class FileDatePatternConverter {
   private FileDatePatternConverter() {
      super();
   }

   public static PatternConverter newInstance(String[] var0) {
      return var0 != null && var0.length != 0 ? DatePatternConverter.newInstance(var0) : DatePatternConverter.newInstance(new String[]{"yyyy-MM-dd"});
   }
}
