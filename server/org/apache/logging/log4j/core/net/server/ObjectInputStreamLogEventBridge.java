package org.apache.logging.log4j.core.net.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LogEventListener;

public class ObjectInputStreamLogEventBridge extends AbstractLogEventBridge<ObjectInputStream> {
   public ObjectInputStreamLogEventBridge() {
      super();
   }

   public void logEvents(ObjectInputStream var1, LogEventListener var2) throws IOException {
      try {
         var2.log((LogEvent)var1.readObject());
      } catch (ClassNotFoundException var4) {
         throw new IOException(var4);
      }
   }

   public ObjectInputStream wrapStream(InputStream var1) throws IOException {
      return new ObjectInputStream(var1);
   }
}
