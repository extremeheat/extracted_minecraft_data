package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;

class DefaultLayoutComponentBuilder extends DefaultComponentAndConfigurationBuilder<LayoutComponentBuilder> implements LayoutComponentBuilder {
   public DefaultLayoutComponentBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2) {
      super(var1, var2);
   }
}
