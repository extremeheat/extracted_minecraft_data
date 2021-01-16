package org.apache.logging.log4j.core.pattern;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Patterns;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "LevelPatternConverter",
   category = "Converter"
)
@ConverterKeys({"p", "level"})
@PerformanceSensitive({"allocation"})
public final class LevelPatternConverter extends LogEventPatternConverter {
   private static final String OPTION_LENGTH = "length";
   private static final String OPTION_LOWER = "lowerCase";
   private static final LevelPatternConverter INSTANCE = new LevelPatternConverter((Map)null);
   private final Map<Level, String> levelMap;

   private LevelPatternConverter(Map<Level, String> var1) {
      super("Level", "level");
      this.levelMap = var1;
   }

   public static LevelPatternConverter newInstance(String[] var0) {
      if (var0 != null && var0.length != 0) {
         HashMap var1 = new HashMap();
         int var2 = 2147483647;
         boolean var3 = false;
         String[] var4 = var0[0].split(Patterns.COMMA_SEPARATOR);
         String[] var5 = var4;
         int var6 = var4.length;

         int var7;
         for(var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            String[] var9 = var8.split("=");
            if (var9 != null && var9.length == 2) {
               String var10 = var9[0].trim();
               String var11 = var9[1].trim();
               if ("length".equalsIgnoreCase(var10)) {
                  var2 = Integer.parseInt(var11);
               } else if ("lowerCase".equalsIgnoreCase(var10)) {
                  var3 = Boolean.parseBoolean(var11);
               } else {
                  Level var12 = Level.toLevel(var10, (Level)null);
                  if (var12 == null) {
                     LOGGER.error((String)"Invalid Level {}", (Object)var10);
                  } else {
                     var1.put(var12, var11);
                  }
               }
            } else {
               LOGGER.error((String)"Invalid option {}", (Object)var8);
            }
         }

         if (var1.isEmpty() && var2 == 2147483647 && !var3) {
            return INSTANCE;
         } else {
            Level[] var13 = Level.values();
            var6 = var13.length;

            for(var7 = 0; var7 < var6; ++var7) {
               Level var14 = var13[var7];
               if (!var1.containsKey(var14)) {
                  String var15 = left(var14, var2);
                  var1.put(var14, var3 ? var15.toLowerCase(Locale.US) : var15);
               }
            }

            return new LevelPatternConverter(var1);
         }
      } else {
         return INSTANCE;
      }
   }

   private static String left(Level var0, int var1) {
      String var2 = var0.toString();
      return var1 >= var2.length() ? var2 : var2.substring(0, var1);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      var2.append(this.levelMap == null ? var1.getLevel().toString() : (String)this.levelMap.get(var1.getLevel()));
   }

   public String getStyleClass(Object var1) {
      return var1 instanceof LogEvent ? "level " + ((LogEvent)var1).getLevel().name().toLowerCase(Locale.ENGLISH) : "level";
   }
}
