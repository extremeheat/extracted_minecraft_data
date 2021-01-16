package io.netty.util.internal.logging;

import org.apache.logging.log4j.LogManager;

public final class Log4J2LoggerFactory extends InternalLoggerFactory {
   public static final InternalLoggerFactory INSTANCE = new Log4J2LoggerFactory();

   /** @deprecated */
   @Deprecated
   public Log4J2LoggerFactory() {
      super();
   }

   public InternalLogger newInstance(String var1) {
      return new Log4J2Logger(LogManager.getLogger(var1));
   }
}
