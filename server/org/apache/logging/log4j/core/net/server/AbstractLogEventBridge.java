package org.apache.logging.log4j.core.net.server;

import java.io.IOException;
import java.io.InputStream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public abstract class AbstractLogEventBridge<T extends InputStream> implements LogEventBridge<T> {
   protected static final int END = -1;
   protected static final Logger logger = StatusLogger.getLogger();

   public AbstractLogEventBridge() {
      super();
   }

   public T wrapStream(InputStream var1) throws IOException {
      return var1;
   }
}
