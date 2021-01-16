package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.ScriptFileComponentBuilder;

class DefaultScriptFileComponentBuilder extends DefaultComponentAndConfigurationBuilder<ScriptFileComponentBuilder> implements ScriptFileComponentBuilder {
   public DefaultScriptFileComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3) {
      super(var1, var2 != null ? var2 : var3, "ScriptFile");
      this.addAttribute("path", var3);
   }

   public DefaultScriptFileComponentBuilder addLanguage(String var1) {
      this.addAttribute("language", var1);
      return this;
   }

   public DefaultScriptFileComponentBuilder addIsWatched(boolean var1) {
      this.addAttribute("isWatched", Boolean.toString(var1));
      return this;
   }

   public DefaultScriptFileComponentBuilder addIsWatched(String var1) {
      this.addAttribute("isWatched", var1);
      return this;
   }

   public DefaultScriptFileComponentBuilder addCharset(String var1) {
      this.addAttribute("charset", var1);
      return this;
   }
}
