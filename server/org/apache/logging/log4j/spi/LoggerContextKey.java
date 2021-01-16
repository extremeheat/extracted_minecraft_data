package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.message.MessageFactory;

/** @deprecated */
@Deprecated
public class LoggerContextKey {
   public LoggerContextKey() {
      super();
   }

   public static String create(String var0) {
      return create(var0, AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS);
   }

   public static String create(String var0, MessageFactory var1) {
      Class var2 = var1 != null ? var1.getClass() : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS;
      return create(var0, var2);
   }

   public static String create(String var0, Class<? extends MessageFactory> var1) {
      Class var2 = var1 != null ? var1 : AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS;
      return var0 + "." + var2.getName();
   }
}
