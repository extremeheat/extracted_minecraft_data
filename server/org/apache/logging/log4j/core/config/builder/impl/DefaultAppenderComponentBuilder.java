package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.FilterComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;

class DefaultAppenderComponentBuilder extends DefaultComponentAndConfigurationBuilder<AppenderComponentBuilder> implements AppenderComponentBuilder {
   public DefaultAppenderComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public AppenderComponentBuilder add(LayoutComponentBuilder var1) {
      return (AppenderComponentBuilder)this.addComponent(var1);
   }

   public AppenderComponentBuilder add(FilterComponentBuilder var1) {
      return (AppenderComponentBuilder)this.addComponent(var1);
   }
}
