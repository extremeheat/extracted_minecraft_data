package org.apache.logging.log4j.core.layout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.script.SimpleBindings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptRef;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "ScriptPatternSelector",
   category = "Core",
   elementType = "patternSelector",
   printObject = true
)
public class ScriptPatternSelector implements PatternSelector {
   private final Map<String, PatternFormatter[]> formatterMap = new HashMap();
   private final Map<String, String> patternMap = new HashMap();
   private final PatternFormatter[] defaultFormatters;
   private final String defaultPattern;
   private static Logger LOGGER = StatusLogger.getLogger();
   private final AbstractScript script;
   private final Configuration configuration;

   /** @deprecated */
   @Deprecated
   public ScriptPatternSelector(AbstractScript var1, PatternMatch[] var2, String var3, boolean var4, boolean var5, boolean var6, Configuration var7) {
      super();
      this.script = var1;
      this.configuration = var7;
      if (!(var1 instanceof ScriptRef)) {
         var7.getScriptManager().addScript(var1);
      }

      PatternParser var8 = PatternLayout.createPatternParser(var7);
      PatternMatch[] var9 = var2;
      int var10 = var2.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         PatternMatch var12 = var9[var11];

         try {
            List var13 = var8.parse(var12.getPattern(), var4, var5, var6);
            this.formatterMap.put(var12.getKey(), var13.toArray(new PatternFormatter[var13.size()]));
            this.patternMap.put(var12.getKey(), var12.getPattern());
         } catch (RuntimeException var15) {
            throw new IllegalArgumentException("Cannot parse pattern '" + var12.getPattern() + "'", var15);
         }
      }

      try {
         List var16 = var8.parse(var3, var4, var5, var6);
         this.defaultFormatters = (PatternFormatter[])var16.toArray(new PatternFormatter[var16.size()]);
         this.defaultPattern = var3;
      } catch (RuntimeException var14) {
         throw new IllegalArgumentException("Cannot parse pattern '" + var3 + "'", var14);
      }
   }

   public PatternFormatter[] getFormatters(LogEvent var1) {
      SimpleBindings var2 = new SimpleBindings();
      var2.putAll(this.configuration.getProperties());
      var2.put("substitutor", this.configuration.getStrSubstitutor());
      var2.put("logEvent", var1);
      Object var3 = this.configuration.getScriptManager().execute(this.script.getName(), var2);
      if (var3 == null) {
         return this.defaultFormatters;
      } else {
         PatternFormatter[] var4 = (PatternFormatter[])this.formatterMap.get(var3.toString());
         return var4 == null ? this.defaultFormatters : var4;
      }
   }

   @PluginBuilderFactory
   public static ScriptPatternSelector.Builder newBuilder() {
      return new ScriptPatternSelector.Builder();
   }

   /** @deprecated */
   @Deprecated
   public static ScriptPatternSelector createSelector(AbstractScript var0, PatternMatch[] var1, String var2, boolean var3, boolean var4, Configuration var5) {
      ScriptPatternSelector.Builder var6 = newBuilder();
      var6.setScript(var0);
      var6.setProperties(var1);
      var6.setDefaultPattern(var2);
      var6.setAlwaysWriteExceptions(var3);
      var6.setNoConsoleNoAnsi(var4);
      var6.setConfiguration(var5);
      return var6.build();
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

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<ScriptPatternSelector> {
      @PluginElement("Script")
      private AbstractScript script;
      @PluginElement("PatternMatch")
      private PatternMatch[] properties;
      @PluginBuilderAttribute("defaultPattern")
      private String defaultPattern;
      @PluginBuilderAttribute("alwaysWriteExceptions")
      private boolean alwaysWriteExceptions;
      @PluginBuilderAttribute("disableAnsi")
      private boolean disableAnsi;
      @PluginBuilderAttribute("noConsoleNoAnsi")
      private boolean noConsoleNoAnsi;
      @PluginConfiguration
      private Configuration configuration;

      private Builder() {
         super();
         this.alwaysWriteExceptions = true;
      }

      public ScriptPatternSelector build() {
         if (this.script == null) {
            ScriptPatternSelector.LOGGER.error("A Script, ScriptFile or ScriptRef element must be provided for this ScriptFilter");
            return null;
         } else if (this.script instanceof ScriptRef && this.configuration.getScriptManager().getScript(this.script.getName()) == null) {
            ScriptPatternSelector.LOGGER.error((String)"No script with name {} has been declared.", (Object)this.script.getName());
            return null;
         } else {
            if (this.defaultPattern == null) {
               this.defaultPattern = "%m%n";
            }

            if (this.properties != null && this.properties.length != 0) {
               return new ScriptPatternSelector(this.script, this.properties, this.defaultPattern, this.alwaysWriteExceptions, this.disableAnsi, this.noConsoleNoAnsi, this.configuration);
            } else {
               ScriptPatternSelector.LOGGER.warn("No marker patterns were provided");
               return null;
            }
         }
      }

      public ScriptPatternSelector.Builder setScript(AbstractScript var1) {
         this.script = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setProperties(PatternMatch[] var1) {
         this.properties = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setDefaultPattern(String var1) {
         this.defaultPattern = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setAlwaysWriteExceptions(boolean var1) {
         this.alwaysWriteExceptions = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setDisableAnsi(boolean var1) {
         this.disableAnsi = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setNoConsoleNoAnsi(boolean var1) {
         this.noConsoleNoAnsi = var1;
         return this;
      }

      public ScriptPatternSelector.Builder setConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }
   }
}
