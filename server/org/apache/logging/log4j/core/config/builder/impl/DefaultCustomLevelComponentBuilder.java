package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.CustomLevelComponentBuilder;

class DefaultCustomLevelComponentBuilder extends DefaultComponentAndConfigurationBuilder<CustomLevelComponentBuilder> implements CustomLevelComponentBuilder {
   public DefaultCustomLevelComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, int var3) {
      super(var1, var2, "CustomLevel");
      this.addAttribute("intLevel", var3);
   }
}
