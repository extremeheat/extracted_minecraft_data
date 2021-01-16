package org.apache.logging.log4j.core.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;

@Plugin(
   name = "MapFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public class MapFilter extends AbstractFilter {
   private final IndexedStringMap map;
   private final boolean isAnd;

   protected MapFilter(Map<String, List<String>> var1, boolean var2, Filter.Result var3, Filter.Result var4) {
      super(var3, var4);
      this.isAnd = var2;
      Objects.requireNonNull(var1, "map cannot be null");
      this.map = new SortedArrayStringMap(var1.size());
      Iterator var5 = var1.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         this.map.putValue((String)var6.getKey(), var6.getValue());
      }

   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      if (var4 instanceof MapMessage) {
         return this.filter((MapMessage)var4) ? this.onMatch : this.onMismatch;
      } else {
         return Filter.Result.NEUTRAL;
      }
   }

   public Filter.Result filter(LogEvent var1) {
      Message var2 = var1.getMessage();
      if (var2 instanceof MapMessage) {
         return this.filter((MapMessage)var2) ? this.onMatch : this.onMismatch;
      } else {
         return Filter.Result.NEUTRAL;
      }
   }

   protected boolean filter(MapMessage var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < this.map.size(); ++var3) {
         String var4 = var1.get(this.map.getKeyAt(var3));
         var2 = var4 != null && ((List)this.map.getValueAt(var3)).contains(var4);
         if (!this.isAnd && var2 || this.isAnd && !var2) {
            break;
         }
      }

      return var2;
   }

   protected boolean filter(Map<String, String> var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < this.map.size(); ++var3) {
         String var4 = (String)var1.get(this.map.getKeyAt(var3));
         var2 = var4 != null && ((List)this.map.getValueAt(var3)).contains(var4);
         if (!this.isAnd && var2 || this.isAnd && !var2) {
            break;
         }
      }

      return var2;
   }

   protected boolean filter(ReadOnlyStringMap var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < this.map.size(); ++var3) {
         String var4 = (String)var1.getValue(this.map.getKeyAt(var3));
         var2 = var4 != null && ((List)this.map.getValueAt(var3)).contains(var4);
         if (!this.isAnd && var2 || this.isAnd && !var2) {
            break;
         }
      }

      return var2;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      return Filter.Result.NEUTRAL;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("isAnd=").append(this.isAnd);
      if (this.map.size() > 0) {
         var1.append(", {");

         for(int var2 = 0; var2 < this.map.size(); ++var2) {
            if (var2 > 0) {
               var1.append(", ");
            }

            List var3 = (List)this.map.getValueAt(var2);
            String var4 = var3.size() > 1 ? (String)var3.get(0) : var3.toString();
            var1.append(this.map.getKeyAt(var2)).append('=').append(var4);
         }

         var1.append('}');
      }

      return var1.toString();
   }

   protected boolean isAnd() {
      return this.isAnd;
   }

   /** @deprecated */
   @Deprecated
   protected Map<String, List<String>> getMap() {
      final HashMap var1 = new HashMap(this.map.size());
      this.map.forEach(new BiConsumer<String, List<String>>() {
         public void accept(String var1x, List<String> var2) {
            var1.put(var1x, var2);
         }
      });
      return var1;
   }

   protected IndexedReadOnlyStringMap getStringMap() {
      return this.map;
   }

   @PluginFactory
   public static MapFilter createFilter(@PluginElement("Pairs") KeyValuePair[] var0, @PluginAttribute("operator") String var1, @PluginAttribute("onMatch") Filter.Result var2, @PluginAttribute("onMismatch") Filter.Result var3) {
      if (var0 != null && var0.length != 0) {
         HashMap var4 = new HashMap();
         KeyValuePair[] var5 = var0;
         int var6 = var0.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            KeyValuePair var8 = var5[var7];
            String var9 = var8.getKey();
            if (var9 == null) {
               LOGGER.error("A null key is not valid in MapFilter");
            } else {
               String var10 = var8.getValue();
               if (var10 == null) {
                  LOGGER.error("A null value for key " + var9 + " is not allowed in MapFilter");
               } else {
                  List var11 = (List)var4.get(var8.getKey());
                  if (var11 != null) {
                     var11.add(var10);
                  } else {
                     ArrayList var12 = new ArrayList();
                     var12.add(var10);
                     var4.put(var8.getKey(), var12);
                  }
               }
            }
         }

         if (var4.isEmpty()) {
            LOGGER.error("MapFilter is not configured with any valid key value pairs");
            return null;
         } else {
            boolean var13 = var1 == null || !var1.equalsIgnoreCase("or");
            return new MapFilter(var4, var13, var2, var3);
         }
      } else {
         LOGGER.error("keys and values must be specified for the MapFilter");
         return null;
      }
   }
}
