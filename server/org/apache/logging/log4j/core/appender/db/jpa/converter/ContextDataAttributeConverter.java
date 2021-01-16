package org.apache.logging.log4j.core.appender.db.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

@Converter(
   autoApply = false
)
public class ContextDataAttributeConverter implements AttributeConverter<ReadOnlyStringMap, String> {
   public ContextDataAttributeConverter() {
      super();
   }

   public String convertToDatabaseColumn(ReadOnlyStringMap var1) {
      return var1 == null ? null : var1.toString();
   }

   public ReadOnlyStringMap convertToEntityAttribute(String var1) {
      throw new UnsupportedOperationException("Log events can only be persisted, not extracted.");
   }
}
