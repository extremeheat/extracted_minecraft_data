package org.apache.logging.log4j.core.config.xml;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "XmlConfigurationFactory",
   category = "ConfigurationFactory"
)
@Order(5)
public class XmlConfigurationFactory extends ConfigurationFactory {
   public static final String[] SUFFIXES = new String[]{".xml", "*"};

   public XmlConfigurationFactory() {
      super();
   }

   public Configuration getConfiguration(LoggerContext var1, ConfigurationSource var2) {
      return new XmlConfiguration(var1, var2);
   }

   public String[] getSupportedTypes() {
      return SUFFIXES;
   }
}
