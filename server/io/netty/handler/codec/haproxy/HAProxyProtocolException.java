package io.netty.handler.codec.haproxy;

import io.netty.handler.codec.DecoderException;

public class HAProxyProtocolException extends DecoderException {
   private static final long serialVersionUID = 713710864325167351L;

   public HAProxyProtocolException() {
      super();
   }

   public HAProxyProtocolException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public HAProxyProtocolException(String var1) {
      super(var1);
   }

   public HAProxyProtocolException(Throwable var1) {
      super(var1);
   }
}
