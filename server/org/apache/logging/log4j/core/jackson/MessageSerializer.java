package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import org.apache.logging.log4j.message.Message;

final class MessageSerializer extends StdScalarSerializer<Message> {
   private static final long serialVersionUID = 1L;

   MessageSerializer() {
      super(Message.class);
   }

   public void serialize(Message var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonGenerationException {
      var2.writeString(var1.getFormattedMessage());
   }
}
