package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.IOException;

public final class Log4jStackTraceElementDeserializer extends StdScalarDeserializer<StackTraceElement> {
   private static final long serialVersionUID = 1L;

   public Log4jStackTraceElementDeserializer() {
      super(StackTraceElement.class);
   }

   public StackTraceElement deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException {
      JsonToken var3 = var1.getCurrentToken();
      if (var3 == JsonToken.START_OBJECT) {
         String var4 = null;
         String var5 = null;
         String var6 = null;
         int var7 = -1;

         while((var3 = var1.nextValue()) != JsonToken.END_OBJECT) {
            String var8 = var1.getCurrentName();
            if ("class".equals(var8)) {
               var4 = var1.getText();
            } else if ("file".equals(var8)) {
               var6 = var1.getText();
            } else if ("line".equals(var8)) {
               if (var3.isNumeric()) {
                  var7 = var1.getIntValue();
               } else {
                  try {
                     var7 = Integer.parseInt(var1.getText().trim());
                  } catch (NumberFormatException var10) {
                     throw JsonMappingException.from(var1, "Non-numeric token (" + var3 + ") for property 'line'", var10);
                  }
               }
            } else if ("method".equals(var8)) {
               var5 = var1.getText();
            } else if (!"nativeMethod".equals(var8)) {
               this.handleUnknownProperty(var1, var2, this._valueClass, var8);
            }
         }

         return new StackTraceElement(var4, var5, var6, var7);
      } else {
         throw var2.mappingException(this._valueClass, var3);
      }
   }
}
