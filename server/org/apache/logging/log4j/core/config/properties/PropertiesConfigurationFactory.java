package org.apache.logging.log4j.core.config.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "PropertiesConfigurationFactory",
   category = "ConfigurationFactory"
)
@Order(8)
public class PropertiesConfigurationFactory extends ConfigurationFactory {
   public PropertiesConfigurationFactory() {
      super();
   }

   protected String[] getSupportedTypes() {
      return new String[]{".properties"};
   }

   public PropertiesConfiguration getConfiguration(LoggerContext var1, ConfigurationSource var2) {
      Properties var3 = new Properties();

      try {
         InputStream var4 = var2.getInputStream();
         Throwable var5 = null;

         try {
            var3.load(var4);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (IOException var17) {
         throw new ConfigurationException("Unable to load " + var2.toString(), var17);
      }

      return (new PropertiesConfigurationBuilder()).setConfigurationSource(var2).setRootProperties(var3).setLoggerContext(var1).build();
   }
}
