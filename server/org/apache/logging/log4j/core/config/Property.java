package org.apache.logging.log4j.core.config;

import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "property",
   category = "Core",
   printObject = true
)
public final class Property {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String name;
   private final String value;
   private final boolean valueNeedsLookup;

   private Property(String var1, String var2) {
      super();
      this.name = var1;
      this.value = var2;
      this.valueNeedsLookup = var2 != null && var2.contains("${");
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return Objects.toString(this.value, "");
   }

   public boolean isValueNeedsLookup() {
      return this.valueNeedsLookup;
   }

   @PluginFactory
   public static Property createProperty(@PluginAttribute("name") String var0, @PluginValue("value") String var1) {
      if (var0 == null) {
         LOGGER.error("Property name cannot be null");
      }

      return new Property(var0, var1);
   }

   public String toString() {
      return this.name + '=' + this.getValue();
   }
}
