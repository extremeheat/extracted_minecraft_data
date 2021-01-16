package org.apache.logging.log4j.core.appender.db.jpa.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.PersistenceException;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.apache.logging.log4j.util.Strings;

@Converter(
   autoApply = false
)
public class ContextDataJsonAttributeConverter implements AttributeConverter<ReadOnlyStringMap, String> {
   static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   public ContextDataJsonAttributeConverter() {
      super();
   }

   public String convertToDatabaseColumn(ReadOnlyStringMap var1) {
      if (var1 == null) {
         return null;
      } else {
         try {
            JsonNodeFactory var2 = OBJECT_MAPPER.getNodeFactory();
            final ObjectNode var3 = var2.objectNode();
            var1.forEach(new BiConsumer<String, Object>() {
               public void accept(String var1, Object var2) {
                  var3.put(var1, String.valueOf(var2));
               }
            });
            return OBJECT_MAPPER.writeValueAsString(var3);
         } catch (Exception var4) {
            throw new PersistenceException("Failed to convert contextData to JSON string.", var4);
         }
      }
   }

   public ReadOnlyStringMap convertToEntityAttribute(String var1) {
      if (Strings.isEmpty(var1)) {
         return null;
      } else {
         try {
            StringMap var2 = ContextDataFactory.createContextData();
            ObjectNode var3 = (ObjectNode)OBJECT_MAPPER.readTree(var1);
            Iterator var4 = var3.fields();

            while(var4.hasNext()) {
               Entry var5 = (Entry)var4.next();
               String var6 = ((JsonNode)var5.getValue()).textValue();
               var2.putValue((String)var5.getKey(), var6);
            }

            return var2;
         } catch (IOException var7) {
            throw new PersistenceException("Failed to convert JSON string to map.", var7);
         }
      }
   }
}
