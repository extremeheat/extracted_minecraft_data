package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.ScriptComponentBuilder;

class DefaultScriptComponentBuilder extends DefaultComponentAndConfigurationBuilder<ScriptComponentBuilder> implements ScriptComponentBuilder {
   public DefaultScriptComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3, String var4) {
      super(var1, var2, "Script");
      if (var3 != null) {
         this.addAttribute("language", var3);
      }

      if (var4 != null) {
         this.addAttribute("text", var4);
      }

   }
}
