package org.apache.logging.log4j.core.layout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "MarkerPatternSelector",
   category = "Core",
   elementType = "patternSelector",
   printObject = true
)
public class MarkerPatternSelector implements PatternSelector {
   private final Map<String, PatternFormatter[]> formatterMap;
   private final Map<String, String> patternMap;
   private final PatternFormatter[] defaultFormatters;
   private final String defaultPattern;
   private static Logger LOGGER = StatusLogger.getLogger();

   /** @deprecated */
   @Deprecated
   public MarkerPatternSelector(PatternMatch[] var1, String var2, boolean var3, boolean var4, Configuration var5) {
      this(var1, var2, var3, false, var4, var5);
   }

   private MarkerPatternSelector(PatternMatch[] var1, String var2, boolean var3, boolean var4, boolean var5, Configuration var6) {
      super();
      this.formatterMap = new HashMap();
      this.patternMap = new HashMap();
      PatternParser var7 = PatternLayout.createPatternParser(var6);
      PatternMatch[] var8 = var1;
      int var9 = var1.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         PatternMatch var11 = var8[var10];

         try {
            List var12 = var7.parse(var11.getPattern(), var3, var4, var5);
            this.formatterMap.put(var11.getKey(), var12.toArray(new PatternFormatter[var12.size()]));
            this.patternMap.put(var11.getKey(), var11.getPattern());
         } catch (RuntimeException var14) {
            throw new IllegalArgumentException("Cannot parse pattern '" + var11.getPattern() + "'", var14);
         }
      }

      try {
         List var15 = var7.parse(var2, var3, var4, var5);
         this.defaultFormatters = (PatternFormatter[])var15.toArray(new PatternFormatter[var15.size()]);
         this.defaultPattern = var2;
      } catch (RuntimeException var13) {
         throw new IllegalArgumentException("Cannot parse pattern '" + var2 + "'", var13);
      }
   }

   public PatternFormatter[] getFormatters(LogEvent var1) {
      Marker var2 = var1.getMarker();
      if (var2 == null) {
         return this.defaultFormatters;
      } else {
         Iterator var3 = this.formatterMap.keySet().iterator();

         String var4;
         do {
            if (!var3.hasNext()) {
               return this.defaultFormatters;
            }

            var4 = (String)var3.next();
         } while(!var2.isInstanceOf(var4));

         return (PatternFormatter[])this.formatterMap.get(var4);
      }
   }

   @PluginBuilderFactory
   public static MarkerPatternSelector.Builder newBuilder() {
      return new MarkerPatternSelector.Builder();
   }

   /** @deprecated */
   @Deprecated
   public static MarkerPatternSelector createSelector(PatternMatch[] var0, String var1, boolean var2, boolean var3, Configuration var4) {
      MarkerPatternSelector.Builder var5 = newBuilder();
      var5.setProperties(var0);
      var5.setDefaultPattern(var1);
      var5.setAlwaysWriteExceptions(var2);
      var5.setNoConsoleNoAnsi(var3);
      var5.setConfiguration(var4);
      return var5.build();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      boolean var2 = true;

      for(Iterator var3 = this.patternMap.entrySet().iterator(); var3.hasNext(); var2 = false) {
         Entry var4 = (Entry)var3.next();
         if (!var2) {
            var1.append(", ");
         }

         var1.append("key=\"").append((String)var4.getKey()).append("\", pattern=\"").append((String)var4.getValue()).append("\"");
      }

      if (!var2) {
         var1.append(", ");
      }

      var1.append("default=\"").append(this.defaultPattern).append("\"");
      return var1.toString();
   }

   // $FF: synthetic method
   MarkerPatternSelector(PatternMatch[] var1, String var2, boolean var3, boolean var4, boolean var5, Configuration var6, Object var7) {
      this(var1, var2, var3, var4, var5, var6);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<MarkerPatternSelector> {
      @PluginElement("PatternMatch")
      private PatternMatch[] properties;
      @PluginBuilderAttribute("defaultPattern")
      private String defaultPattern;
      @PluginBuilderAttribute("alwaysWriteExceptions")
      private boolean alwaysWriteExceptions = true;
      @PluginBuilderAttribute("disableAnsi")
      private boolean disableAnsi;
      @PluginBuilderAttribute("noConsoleNoAnsi")
      private boolean noConsoleNoAnsi;
      @PluginConfiguration
      private Configuration configuration;

      public Builder() {
         super();
      }

      public MarkerPatternSelector build() {
         if (this.defaultPattern == null) {
            this.defaultPattern = "%m%n";
         }

         if (this.properties != null && this.properties.length != 0) {
            return new MarkerPatternSelector(this.properties, this.defaultPattern, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi, this.configuration);
         } else {
            MarkerPatternSelector.LOGGER.warn("No marker patterns were provided with PatternMatch");
            return null;
         }
      }

      public MarkerPatternSelector.Builder setProperties(PatternMatch[] var1) {
         this.properties = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setDefaultPattern(String var1) {
         this.defaultPattern = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setAlwaysWriteExceptions(boolean var1) {
         this.alwaysWriteExceptions = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setDisableAnsi(boolean var1) {
         this.disableAnsi = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setNoConsoleNoAnsi(boolean var1) {
         this.noConsoleNoAnsi = var1;
         return this;
      }

      public MarkerPatternSelector.Builder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }
   }
}
