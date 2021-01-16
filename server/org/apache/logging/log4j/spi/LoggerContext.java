package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.message.MessageFactory;

public interface LoggerContext {
   Object getExternalContext();

   ExtendedLogger getLogger(String var1);

   ExtendedLogger getLogger(String var1, MessageFactory var2);

   boolean hasLogger(String var1);

   boolean hasLogger(String var1, MessageFactory var2);

   boolean hasLogger(String var1, Class<? extends MessageFactory> var2);
}
