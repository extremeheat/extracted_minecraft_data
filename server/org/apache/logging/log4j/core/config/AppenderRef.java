package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "AppenderRef",
   category = "Core",
   printObject = true
)
@PluginAliases({"appender-ref"})
public final class AppenderRef {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String ref;
   private final Level level;
   private final Filter filter;

   private AppenderRef(String var1, Level var2, Filter var3) {
      super();
      this.ref = var1;
      this.level = var2;
      this.filter = var3;
   }

   public String getRef() {
      return this.ref;
   }

   public Level getLevel() {
      return this.level;
   }

   public Filter getFilter() {
      return this.filter;
   }

   public String toString() {
      return this.ref;
   }

   @PluginFactory
   public static AppenderRef createAppenderRef(@PluginAttribute("ref") String var0, @PluginAttribute("level") Level var1, @PluginElement("Filter") Filter var2) {
      if (var0 == null) {
         LOGGER.error("Appender references must contain a reference");
         return null;
      } else {
         return new AppenderRef(var0, var1, var2);
      }
   }
}
