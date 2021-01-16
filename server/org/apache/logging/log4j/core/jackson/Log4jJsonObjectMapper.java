package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Log4jJsonObjectMapper extends ObjectMapper {
   private static final long serialVersionUID = 1L;

   public Log4jJsonObjectMapper() {
      this(false, true);
   }

   public Log4jJsonObjectMapper(boolean var1, boolean var2) {
      super();
      this.registerModule(new Log4jJsonModule(var1, var2));
      this.setSerializationInclusion(Include.NON_EMPTY);
   }
}
