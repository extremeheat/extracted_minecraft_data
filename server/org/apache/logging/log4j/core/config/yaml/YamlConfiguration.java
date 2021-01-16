package org.apache.logging.log4j.core.config.yaml;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.json.JsonConfiguration;

public class YamlConfiguration extends JsonConfiguration {
   public YamlConfiguration(LoggerContext var1, ConfigurationSource var2) {
      super(var1, var2);
   }

   protected ObjectMapper getObjectMapper() {
      return (new ObjectMapper(new YAMLFactory())).configure(Feature.ALLOW_COMMENTS, true);
   }

   public Configuration reconfigure() {
      try {
         ConfigurationSource var1 = this.getConfigurationSource().resetInputStream();
         return var1 == null ? null : new YamlConfiguration(this.getLoggerContext(), var1);
      } catch (IOException var2) {
         LOGGER.error((String)"Cannot locate file {}", (Object)this.getConfigurationSource(), (Object)var2);
         return null;
      }
   }
}
