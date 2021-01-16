package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.script.AbstractScript;

@Plugin(
   name = "scripts",
   category = "Core"
)
public final class ScriptsPlugin {
   private ScriptsPlugin() {
      super();
   }

   @PluginFactory
   public static AbstractScript[] createScripts(@PluginElement("Scripts") AbstractScript[] var0) {
      return var0;
   }
}
