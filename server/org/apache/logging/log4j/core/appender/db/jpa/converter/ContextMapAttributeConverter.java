package org.apache.logging.log4j.core.appender.db.jpa.converter;

import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(
   autoApply = false
)
public class ContextMapAttributeConverter implements AttributeConverter<Map<String, String>, String> {
   public ContextMapAttributeConverter() {
      super();
   }

   public String convertToDatabaseColumn(Map<String, String> var1) {
      return var1 == null ? null : var1.toString();
   }

   public Map<String, String> convertToEntityAttribute(String var1) {
      throw new UnsupportedOperationException("Log events can only be persisted, not extracted.");
   }
}
