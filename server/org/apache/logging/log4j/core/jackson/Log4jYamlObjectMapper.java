package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class Log4jYamlObjectMapper extends YAMLMapper {
   private static final long serialVersionUID = 1L;

   public Log4jYamlObjectMapper() {
      this(false, true);
   }

   public Log4jYamlObjectMapper(boolean var1, boolean var2) {
      super();
      this.registerModule(new Log4jYamlModule(var1, var2));
      this.setSerializationInclusion(Include.NON_EMPTY);
   }
}
