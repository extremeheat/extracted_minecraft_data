package org.apache.logging.log4j.core.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.message.MapMessage;

@Plugin(
   name = "map",
   category = "Lookup"
)
public class MapLookup implements StrLookup {
   private final Map<String, String> map;

   public MapLookup() {
      super();
      this.map = null;
   }

   public MapLookup(Map<String, String> var1) {
      super();
      this.map = var1;
   }

   static Map<String, String> initMap(String[] var0, Map<String, String> var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         int var3 = var2 + 1;
         String var4 = var0[var2];
         var1.put(Integer.toString(var2), var4);
         var1.put(var4, var3 < var0.length ? var0[var3] : null);
      }

      return var1;
   }

   static HashMap<String, String> newMap(int var0) {
      return new HashMap(var0);
   }

   /** @deprecated */
   @Deprecated
   public static void setMainArguments(String... var0) {
      MainMapLookup.setMainArguments(var0);
   }

   static Map<String, String> toMap(List<String> var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.size();
         return initMap((String[])var0.toArray(new String[var1]), newMap(var1));
      }
   }

   static Map<String, String> toMap(String[] var0) {
      return var0 == null ? null : initMap(var0, newMap(var0.length));
   }

   protected Map<String, String> getMap() {
      return this.map;
   }

   public String lookup(LogEvent var1, String var2) {
      boolean var3 = var1 != null && var1.getMessage() instanceof MapMessage;
      if (this.map == null && !var3) {
         return null;
      } else {
         if (this.map != null && this.map.containsKey(var2)) {
            String var4 = (String)this.map.get(var2);
            if (var4 != null) {
               return var4;
            }
         }

         return var3 ? ((MapMessage)var1.getMessage()).get(var2) : null;
      }
   }

   public String lookup(String var1) {
      return this.map == null ? null : (String)this.map.get(var1);
   }
}
