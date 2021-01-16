package io.netty.util.internal.logging;

import org.apache.log4j.Logger;

public class Log4JLoggerFactory extends InternalLoggerFactory {
   public static final InternalLoggerFactory INSTANCE = new Log4JLoggerFactory();

   /** @deprecated */
   @Deprecated
   public Log4JLoggerFactory() {
      super();
   }

   public InternalLogger newInstance(String var1) {
      return new Log4JLogger(Logger.getLogger(var1));
   }
}
