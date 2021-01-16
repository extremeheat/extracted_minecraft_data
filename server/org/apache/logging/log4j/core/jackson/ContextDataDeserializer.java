package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.util.StringMap;

public class ContextDataDeserializer extends StdDeserializer<StringMap> {
   private static final long serialVersionUID = 1L;

   ContextDataDeserializer() {
      super(Map.class);
   }

   public StringMap deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException {
      StringMap var3 = ContextDataFactory.createContextData();

      while(var1.nextToken() != JsonToken.END_OBJECT) {
         String var4 = var1.getCurrentName();
         var1.nextToken();
         var3.putValue(var4, var1.getText());
      }

      return var3;
   }
}
