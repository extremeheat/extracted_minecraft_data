package org.apache.logging.log4j.core.appender.routing;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import javax.script.Bindings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "Routes",
   category = "Core",
   printObject = true
)
public final class Routes {
   private static final String LOG_EVENT_KEY = "logEvent";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Configuration configuration;
   private final String pattern;
   private final AbstractScript patternScript;
   private final Route[] routes;

   /** @deprecated */
   @Deprecated
   public static Routes createRoutes(String var0, Route... var1) {
      if (var1 != null && var1.length != 0) {
         return new Routes((Configuration)null, (AbstractScript)null, var0, var1);
      } else {
         LOGGER.error("No routes configured");
         return null;
      }
   }

   @PluginBuilderFactory
   public static Routes.Builder newBuilder() {
      return new Routes.Builder();
   }

   private Routes(Configuration var1, AbstractScript var2, String var3, Route... var4) {
      super();
      this.configuration = var1;
      this.patternScript = var2;
      this.pattern = var3;
      this.routes = var4;
   }

   public String getPattern(LogEvent var1, ConcurrentMap<Object, Object> var2) {
      if (this.patternScript != null) {
         ScriptManager var3 = this.configuration.getScriptManager();
         Bindings var4 = var3.createBindings(this.patternScript);
         var4.put("staticVariables", var2);
         var4.put("logEvent", var1);
         Object var5 = var3.execute(this.patternScript.getName(), var4);
         var4.remove("logEvent");
         return Objects.toString(var5, (String)null);
      } else {
         return this.pattern;
      }
   }

   public AbstractScript getPatternScript() {
      return this.patternScript;
   }

   public Route getRoute(String var1) {
      Route[] var2 = this.routes;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Route var5 = var2[var4];
         if (Objects.equals(var5.getKey(), var1)) {
            return var5;
         }
      }

      return null;
   }

   public Route[] getRoutes() {
      return this.routes;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      boolean var2 = true;
      Route[] var3 = this.routes;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Route var6 = var3[var5];
         if (!var2) {
            var1.append(',');
         }

         var2 = false;
         var1.append(var6.toString());
      }

      var1.append('}');
      return var1.toString();
   }

   // $FF: synthetic method
   Routes(Configuration var1, AbstractScript var2, String var3, Route[] var4, Object var5) {
      this(var1, var2, var3, var4);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<Routes> {
      @PluginConfiguration
      private Configuration configuration;
      @PluginAttribute("pattern")
      private String pattern;
      @PluginElement("Script")
      private AbstractScript patternScript;
      @PluginElement("Routes")
      @Required
      private Route[] routes;

      public Builder() {
         super();
      }

      public Routes build() {
         if (this.routes != null && this.routes.length != 0) {
            if (this.patternScript != null && this.pattern != null) {
               Routes.LOGGER.warn("In a Routes element, you must configure either a Script element or a pattern attribute.");
            }

            if (this.patternScript != null) {
               if (this.configuration == null) {
                  Routes.LOGGER.error("No Configuration defined for Routes; required for Script");
               } else {
                  this.configuration.getScriptManager().addScript(this.patternScript);
               }
            }

            return new Routes(this.configuration, this.patternScript, this.pattern, this.routes);
         } else {
            Routes.LOGGER.error("No Routes configured.");
            return null;
         }
      }

      public Configuration getConfiguration() {
         return this.configuration;
      }

      public String getPattern() {
         return this.pattern;
      }

      public AbstractScript getPatternScript() {
         return this.patternScript;
      }

      public Route[] getRoutes() {
         return this.routes;
      }

      public Routes.Builder withConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public Routes.Builder withPattern(String var1) {
         this.pattern = var1;
         return this;
      }

      public Routes.Builder withPatternScript(AbstractScript var1) {
         this.patternScript = var1;
         return this;
      }

      public Routes.Builder withRoutes(Route[] var1) {
         this.routes = var1;
         return this;
      }
   }
}
