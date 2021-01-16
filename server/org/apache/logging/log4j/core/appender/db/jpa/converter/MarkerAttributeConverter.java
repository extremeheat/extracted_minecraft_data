package org.apache.logging.log4j.core.appender.db.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.util.Strings;

@Converter(
   autoApply = false
)
public class MarkerAttributeConverter implements AttributeConverter<Marker, String> {
   public MarkerAttributeConverter() {
      super();
   }

   public String convertToDatabaseColumn(Marker var1) {
      return var1 == null ? null : var1.toString();
   }

   public Marker convertToEntityAttribute(String var1) {
      if (Strings.isEmpty(var1)) {
         return null;
      } else {
         int var2 = var1.indexOf("[");
         return var2 < 1 ? MarkerManager.getMarker(var1) : MarkerManager.getMarker(var1.substring(0, var2));
      }
   }
}
