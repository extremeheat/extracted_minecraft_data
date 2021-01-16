package io.netty.util.internal.logging;

import org.apache.commons.logging.LogFactory;

/** @deprecated */
@Deprecated
public class CommonsLoggerFactory extends InternalLoggerFactory {
   public static final InternalLoggerFactory INSTANCE = new CommonsLoggerFactory();

   /** @deprecated */
   @Deprecated
   public CommonsLoggerFactory() {
      super();
   }

   public InternalLogger newInstance(String var1) {
      return new CommonsLogger(LogFactory.getLog(var1), var1);
   }
}
