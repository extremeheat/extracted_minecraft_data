package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "LevelRangeFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class LevelRangeFilter extends AbstractFilter {
   private final Level maxLevel;
   private final Level minLevel;

   @PluginFactory
   public static LevelRangeFilter createFilter(@PluginAttribute("minLevel") Level var0, @PluginAttribute("maxLevel") Level var1, @PluginAttribute("onMatch") Filter.Result var2, @PluginAttribute("onMismatch") Filter.Result var3) {
      Level var4 = var0 == null ? Level.ERROR : var0;
      Level var5 = var1 == null ? Level.ERROR : var1;
      Filter.Result var6 = var2 == null ? Filter.Result.NEUTRAL : var2;
      Filter.Result var7 = var3 == null ? Filter.Result.DENY : var3;
      return new LevelRangeFilter(var4, var5, var6, var7);
   }

   private LevelRangeFilter(Level var1, Level var2, Filter.Result var3, Filter.Result var4) {
      super(var3, var4);
      this.minLevel = var1;
      this.maxLevel = var2;
   }

   private Filter.Result filter(Level var1) {
      return var1.isInRange(this.minLevel, this.maxLevel) ? this.onMatch : this.onMismatch;
   }

   public Filter.Result filter(LogEvent var1) {
      return this.filter(var1.getLevel());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      return this.filter(var2);
   }

   public Level getMinLevel() {
      return this.minLevel;
   }

   public String toString() {
      return this.minLevel.toString();
   }
}
