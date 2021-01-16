package io.netty.handler.codec.http.websocketx;

import io.netty.util.AsciiString;

public final class WebSocketScheme {
   public static final WebSocketScheme WS = new WebSocketScheme(80, "ws");
   public static final WebSocketScheme WSS = new WebSocketScheme(443, "wss");
   private final int port;
   private final AsciiString name;

   private WebSocketScheme(int var1, String var2) {
      super();
      this.port = var1;
      this.name = AsciiString.cached(var2);
   }

   public AsciiString name() {
      return this.name;
   }

   public int port() {
      return this.port;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof WebSocketScheme)) {
         return false;
      } else {
         WebSocketScheme var2 = (WebSocketScheme)var1;
         return var2.port() == this.port && var2.name().equals(this.name);
      }
   }

   public int hashCode() {
      return this.port * 31 + this.name.hashCode();
   }

   public String toString() {
      return this.name.toString();
   }
}
