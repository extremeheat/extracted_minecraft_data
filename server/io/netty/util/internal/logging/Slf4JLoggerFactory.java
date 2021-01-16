package io.netty.util.internal.logging;

import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

public class Slf4JLoggerFactory extends InternalLoggerFactory {
   public static final InternalLoggerFactory INSTANCE = new Slf4JLoggerFactory();

   /** @deprecated */
   @Deprecated
   public Slf4JLoggerFactory() {
      super();
   }

   Slf4JLoggerFactory(boolean var1) {
      super();

      assert var1;

      if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
         throw new NoClassDefFoundError("NOPLoggerFactory not supported");
      }
   }

   public InternalLogger newInstance(String var1) {
      return new Slf4JLogger(LoggerFactory.getLogger(var1));
   }
}
