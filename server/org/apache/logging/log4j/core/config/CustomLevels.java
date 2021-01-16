package org.apache.logging.log4j.core.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "CustomLevels",
   category = "Core",
   printObject = true
)
public final class CustomLevels {
   private final List<CustomLevelConfig> customLevels;

   private CustomLevels(CustomLevelConfig[] var1) {
      super();
      this.customLevels = new ArrayList(Arrays.asList(var1));
   }

   @PluginFactory
   public static CustomLevels createCustomLevels(@PluginElement("CustomLevels") CustomLevelConfig[] var0) {
      return new CustomLevels(var0 == null ? new CustomLevelConfig[0] : var0);
   }

   public List<CustomLevelConfig> getCustomLevels() {
      return this.customLevels;
   }
}
