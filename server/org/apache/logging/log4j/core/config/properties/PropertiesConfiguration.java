package org.apache.logging.log4j.core.config.properties;

import java.io.IOException;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

public class PropertiesConfiguration extends BuiltConfiguration implements Reconfigurable {
   public PropertiesConfiguration(LoggerContext var1, ConfigurationSource var2, Component var3) {
      super(var1, var2, var3);
   }

   public Configuration reconfigure() {
      try {
         ConfigurationSource var1 = this.getConfigurationSource().resetInputStream();
         if (var1 == null) {
            return null;
         } else {
            PropertiesConfigurationFactory var2 = new PropertiesConfigurationFactory();
            PropertiesConfiguration var3 = var2.getConfiguration(this.getLoggerContext(), var1);
            return var3 != null && var3.getState() == LifeCycle.State.INITIALIZING ? var3 : null;
         }
      } catch (IOException var4) {
         LOGGER.error((String)"Cannot locate file {}: {}", (Object)this.getConfigurationSource(), (Object)var4);
         return null;
      }
   }
}
