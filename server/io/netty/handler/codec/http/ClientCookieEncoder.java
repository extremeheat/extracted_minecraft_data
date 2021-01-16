package io.netty.handler.codec.http;

/** @deprecated */
@Deprecated
public final class ClientCookieEncoder {
   /** @deprecated */
   @Deprecated
   public static String encode(String var0, String var1) {
      return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static String encode(Cookie var0) {
      return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode((io.netty.handler.codec.http.cookie.Cookie)var0);
   }

   /** @deprecated */
   @Deprecated
   public static String encode(Cookie... var0) {
      return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode((io.netty.handler.codec.http.cookie.Cookie[])var0);
   }

   /** @deprecated */
   @Deprecated
   public static String encode(Iterable<Cookie> var0) {
      return io.netty.handler.codec.http.cookie.ClientCookieEncoder.LAX.encode(var0);
   }

   private ClientCookieEncoder() {
      super();
   }
}
