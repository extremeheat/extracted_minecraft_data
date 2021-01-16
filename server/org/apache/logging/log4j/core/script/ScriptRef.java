package org.apache.logging.log4j.core.script;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "ScriptRef",
   category = "Core",
   printObject = true
)
public class ScriptRef extends AbstractScript {
   private final ScriptManager scriptManager;

   public ScriptRef(String var1, ScriptManager var2) {
      super(var1, (String)null, (String)null);
      this.scriptManager = var2;
   }

   public String getLanguage() {
      AbstractScript var1 = this.scriptManager.getScript(this.getName());
      return var1 != null ? var1.getLanguage() : null;
   }

   public String getScriptText() {
      AbstractScript var1 = this.scriptManager.getScript(this.getName());
      return var1 != null ? var1.getScriptText() : null;
   }

   @PluginFactory
   public static ScriptRef createReference(@PluginAttribute("ref") String var0, @PluginConfiguration Configuration var1) {
      if (var0 == null) {
         LOGGER.error("No script name provided");
         return null;
      } else {
         return new ScriptRef(var0, var1.getScriptManager());
      }
   }

   public String toString() {
      return "ref=" + this.getName();
   }
}
