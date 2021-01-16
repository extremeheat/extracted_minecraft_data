package org.apache.logging.log4j.core.config.builder.impl;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;

class DefaultComponentAndConfigurationBuilder<T extends ComponentBuilder<T>> extends DefaultComponentBuilder<T, DefaultConfigurationBuilder<? extends Configuration>> {
   DefaultComponentAndConfigurationBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3, String var4) {
      super(var1, var2, var3, var4);
   }

   DefaultComponentAndConfigurationBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2, String var3) {
      super(var1, var2, var3);
   }

   public DefaultComponentAndConfigurationBuilder(DefaultConfigurationBuilder<? extends Configuration> var1, String var2) {
      super(var1, var2);
   }
}
