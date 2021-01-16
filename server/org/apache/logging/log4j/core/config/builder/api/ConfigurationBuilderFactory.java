package org.apache.logging.log4j.core.config.builder.api;

import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.impl.DefaultConfigurationBuilder;

public abstract class ConfigurationBuilderFactory {
   public ConfigurationBuilderFactory() {
      super();
   }

   public static ConfigurationBuilder<BuiltConfiguration> newConfigurationBuilder() {
      return new DefaultConfigurationBuilder();
   }

   public static <T extends BuiltConfiguration> ConfigurationBuilder<T> newConfigurationBuilder(Class<T> var0) {
      return new DefaultConfigurationBuilder(var0);
   }
}
