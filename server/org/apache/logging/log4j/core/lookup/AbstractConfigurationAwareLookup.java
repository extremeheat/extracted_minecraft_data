package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationAware;

public abstract class AbstractConfigurationAwareLookup extends AbstractLookup implements ConfigurationAware {
   protected Configuration configuration;

   public AbstractConfigurationAwareLookup() {
      super();
   }

   public void setConfiguration(Configuration var1) {
      this.configuration = var1;
   }
}
