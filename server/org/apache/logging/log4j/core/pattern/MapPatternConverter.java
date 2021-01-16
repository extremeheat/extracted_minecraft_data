package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;

@Plugin(
   name = "MapPatternConverter",
   category = "Converter"
)
@ConverterKeys({"K", "map", "MAP"})
public final class MapPatternConverter extends LogEventPatternConverter {
   private final String key;

   private MapPatternConverter(String[] var1) {
      super(var1 != null && var1.length > 0 ? "MAP{" + var1[0] + '}' : "MAP", "map");
      this.key = var1 != null && var1.length > 0 ? var1[0] : null;
   }

   public static MapPatternConverter newInstance(String[] var0) {
      return new MapPatternConverter(var0);
   }

   public void format(LogEvent var1, StringBuilder var2) {
      if (var1.getMessage() instanceof MapMessage) {
         MapMessage var3 = (MapMessage)var1.getMessage();
         IndexedReadOnlyStringMap var4 = var3.getIndexedReadOnlyStringMap();
         if (this.key == null) {
            if (var4.isEmpty()) {
               var2.append("{}");
               return;
            }

            var2.append("{");

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               if (var5 > 0) {
                  var2.append(", ");
               }

               var2.append(var4.getKeyAt(var5)).append('=').append(var4.getValueAt(var5));
            }

            var2.append('}');
         } else {
            String var6 = (String)var4.getValue(this.key);
            if (var6 != null) {
               var2.append(var6);
            }
         }

      }
   }
}
