package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Log4jXmlObjectMapper extends XmlMapper {
   private static final long serialVersionUID = 1L;

   public Log4jXmlObjectMapper() {
      this(true);
   }

   public Log4jXmlObjectMapper(boolean var1) {
      super(new Log4jXmlModule(var1));
      this.setSerializationInclusion(Include.NON_EMPTY);
   }
}
