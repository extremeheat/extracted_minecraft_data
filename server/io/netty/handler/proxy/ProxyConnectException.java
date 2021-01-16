package io.netty.handler.proxy;

import java.net.ConnectException;

public class ProxyConnectException extends ConnectException {
   private static final long serialVersionUID = 5211364632246265538L;

   public ProxyConnectException() {
      super();
   }

   public ProxyConnectException(String var1) {
      super(var1);
   }

   public ProxyConnectException(Throwable var1) {
      super();
      this.initCause(var1);
   }

   public ProxyConnectException(String var1, Throwable var2) {
      super(var1);
      this.initCause(var2);
   }
}
