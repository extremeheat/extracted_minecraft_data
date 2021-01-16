package org.apache.logging.log4j.core.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.ContextDataInjectorFactory;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

@Plugin(
   name = "DynamicThresholdFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class DynamicThresholdFilter extends AbstractFilter {
   private Level defaultThreshold;
   private final String key;
   private final ContextDataInjector injector;
   private Map<String, Level> levelMap;

   @PluginFactory
   public static DynamicThresholdFilter createFilter(@PluginAttribute("key") String var0, @PluginElement("Pairs") KeyValuePair[] var1, @PluginAttribute("defaultThreshold") Level var2, @PluginAttribute("onMatch") Filter.Result var3, @PluginAttribute("onMismatch") Filter.Result var4) {
      HashMap var5 = new HashMap();
      KeyValuePair[] var6 = var1;
      int var7 = var1.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         KeyValuePair var9 = var6[var8];
         var5.put(var9.getKey(), Level.toLevel(var9.getValue()));
      }

      Level var10 = var2 == null ? Level.ERROR : var2;
      return new DynamicThresholdFilter(var0, var5, var10, var3, var4);
   }

   private DynamicThresholdFilter(String var1, Map<String, Level> var2, Level var3, Filter.Result var4, Filter.Result var5) {
      super(var4, var5);
      this.defaultThreshold = Level.ERROR;
      this.injector = ContextDataInjectorFactory.createInjector();
      this.levelMap = new HashMap();
      Objects.requireNonNull(var1, "key cannot be null");
      this.key = var1;
      this.levelMap = var2;
      this.defaultThreshold = var3;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!super.equalsImpl(var1)) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         DynamicThresholdFilter var2 = (DynamicThresholdFilter)var1;
         if (this.defaultThreshold == null) {
            if (var2.defaultThreshold != null) {
               return false;
            }
         } else if (!this.defaultThreshold.equals(var2.defaultThreshold)) {
            return false;
         }

         if (this.key == null) {
            if (var2.key != null) {
               return false;
            }
         } else if (!this.key.equals(var2.key)) {
            return false;
         }

         if (this.levelMap == null) {
            if (var2.levelMap != null) {
               return false;
            }
         } else if (!this.levelMap.equals(var2.levelMap)) {
            return false;
         }

         return true;
      }
   }

   private Filter.Result filter(Level var1, ReadOnlyStringMap var2) {
      String var3 = (String)var2.getValue(this.key);
      if (var3 != null) {
         Level var4 = (Level)this.levelMap.get(var3);
         if (var4 == null) {
            var4 = this.defaultThreshold;
         }

         return var1.isMoreSpecificThan(var4) ? this.onMatch : this.onMismatch;
      } else {
         return Filter.Result.NEUTRAL;
      }
   }

   public Filter.Result filter(LogEvent var1) {
      return this.filter(var1.getLevel(), var1.getContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter(var2, this.currentContextData());
   }

   private ReadOnlyStringMap currentContextData() {
      return this.injector.rawContextData();
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return this.filter(var2, this.currentContextData());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      return this.filter(var2, this.currentContextData());
   }

   public String getKey() {
      return this.key;
   }

   public Map<String, Level> getLevelMap() {
      return this.levelMap;
   }

   public int hashCode() {
      boolean var1 = true;
      int var2 = super.hashCodeImpl();
      var2 = 31 * var2 + (this.defaultThreshold == null ? 0 : this.defaultThreshold.hashCode());
      var2 = 31 * var2 + (this.key == null ? 0 : this.key.hashCode());
      var2 = 31 * var2 + (this.levelMap == null ? 0 : this.levelMap.hashCode());
      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("key=").append(this.key);
      var1.append(", default=").append(this.defaultThreshold);
      if (this.levelMap.size() > 0) {
         var1.append('{');
         boolean var2 = true;

         Entry var4;
         for(Iterator var3 = this.levelMap.entrySet().iterator(); var3.hasNext(); var1.append((String)var4.getKey()).append('=').append(var4.getValue())) {
            var4 = (Entry)var3.next();
            if (!var2) {
               var1.append(", ");
               var2 = false;
            }
         }

         var1.append('}');
      }

      return var1.toString();
   }
}
