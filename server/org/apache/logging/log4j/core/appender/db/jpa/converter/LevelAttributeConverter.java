package org.apache.logging.log4j.core.appender.db.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.util.Strings;

@Converter(
   autoApply = false
)
public class LevelAttributeConverter implements AttributeConverter<Level, String> {
   public LevelAttributeConverter() {
      super();
   }

   public String convertToDatabaseColumn(Level var1) {
      return var1 == null ? null : var1.name();
   }

   public Level convertToEntityAttribute(String var1) {
      return Strings.isEmpty(var1) ? null : Level.toLevel(var1, (Level)null);
   }
}
