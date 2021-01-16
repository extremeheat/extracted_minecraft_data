package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ListOfMapEntrySerializer extends StdSerializer<Map<String, String>> {
   private static final long serialVersionUID = 1L;

   protected ListOfMapEntrySerializer() {
      super(Map.class, false);
   }

   public void serialize(Map<String, String> var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonGenerationException {
      Set var4 = var1.entrySet();
      MapEntry[] var5 = new MapEntry[var4.size()];
      int var6 = 0;

      Entry var8;
      for(Iterator var7 = var4.iterator(); var7.hasNext(); var5[var6++] = new MapEntry((String)var8.getKey(), (String)var8.getValue())) {
         var8 = (Entry)var7.next();
      }

      var2.writeObject(var5);
   }
}
