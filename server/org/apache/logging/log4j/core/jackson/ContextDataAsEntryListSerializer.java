package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Map;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

public class ContextDataAsEntryListSerializer extends StdSerializer<ReadOnlyStringMap> {
   private static final long serialVersionUID = 1L;

   protected ContextDataAsEntryListSerializer() {
      super(Map.class, false);
   }

   public void serialize(ReadOnlyStringMap var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonGenerationException {
      final MapEntry[] var4 = new MapEntry[var1.size()];
      var1.forEach(new BiConsumer<String, Object>() {
         int i = 0;

         public void accept(String var1, Object var2) {
            var4[this.i++] = new MapEntry(var1, String.valueOf(var2));
         }
      });
      var2.writeObject(var4);
   }
}
