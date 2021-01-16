package org.apache.logging.log4j.core.script;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.PluginValue;

@Plugin(
   name = "Script",
   category = "Core",
   printObject = true
)
public class Script extends AbstractScript {
   public Script(String var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   @PluginFactory
   public static Script createScript(@PluginAttribute("name") String var0, @PluginAttribute("language") String var1, @PluginValue("scriptText") String var2) {
      if (var1 == null) {
         LOGGER.info((String)"No script language supplied, defaulting to {}", (Object)"JavaScript");
         var1 = "JavaScript";
      }

      if (var2 == null) {
         LOGGER.error((String)"No scriptText attribute provided for ScriptFile {}", (Object)var0);
         return null;
      } else {
         return new Script(var0, var1, var2);
      }
   }

   public String toString() {
      return this.getName() != null ? this.getName() : super.toString();
   }
}
