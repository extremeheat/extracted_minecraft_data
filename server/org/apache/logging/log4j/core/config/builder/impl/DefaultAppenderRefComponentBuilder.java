package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderRefComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;

class DefaultAppenderRefComponentBuilder extends DefaultComponentAndConfigurationBuilder<AppenderRefComponentBuilder> implements AppenderRefComponentBuilder {
   public DefaultAppenderRefComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2) {
      super(var1, "AppenderRef");
      this.addAttribute("ref", var2);
   }

   public AppenderRefComponentBuilder add(FilterComponentBuilder var1) {
      return (AppenderRefComponentBuilder)this.addComponent(var1);
   }
}
