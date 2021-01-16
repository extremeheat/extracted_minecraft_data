package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.util.StringMap;

public class ContextDataAsEntryListDeserializer extends StdDeserializer<StringMap> {
   private static final long serialVersionUID = 1L;

   ContextDataAsEntryListDeserializer() {
      super(Map.class);
   }

   public StringMap deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException {
      List var3 = (List)var1.readValueAs(new TypeReference<List<MapEntry>>() {
      });
      new ContextDataFactory();
      StringMap var4 = ContextDataFactory.createContextData();
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         MapEntry var6 = (MapEntry)var5.next();
         var4.putValue(var6.getKey(), var6.getValue());
      }

      return var4;
   }
}
