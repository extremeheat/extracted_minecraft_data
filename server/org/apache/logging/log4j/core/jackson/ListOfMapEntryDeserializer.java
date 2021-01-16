package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListOfMapEntryDeserializer extends StdDeserializer<Map<String, String>> {
   private static final long serialVersionUID = 1L;

   ListOfMapEntryDeserializer() {
      super(Map.class);
   }

   public Map<String, String> deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException {
      List var3 = (List)var1.readValueAs(new TypeReference<List<MapEntry>>() {
      });
      HashMap var4 = new HashMap(var3.size());
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         MapEntry var6 = (MapEntry)var5.next();
         var4.put(var6.getKey(), var6.getValue());
      }

      return var4;
   }
}
