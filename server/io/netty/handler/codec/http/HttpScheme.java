package io.netty.handler.codec.http;

import io.netty.util.AsciiString;

public final class HttpScheme {
   public static final HttpScheme HTTP = new HttpScheme(80, "http");
   public static final HttpScheme HTTPS = new HttpScheme(443, "https");
   private final int port;
   private final AsciiString name;

   private HttpScheme(int var1, String var2) {
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
      if (!(var1 instanceof HttpScheme)) {
         return false;
      } else {
         HttpScheme var2 = (HttpScheme)var1;
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
