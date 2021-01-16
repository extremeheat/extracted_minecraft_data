package io.netty.handler.ssl;

public enum SslProvider {
   JDK,
   OPENSSL,
   OPENSSL_REFCNT;

   private SslProvider() {
   }
}
