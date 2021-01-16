package io.netty.handler.proxy;

import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public final class ProxyConnectionEvent {
   private final String protocol;
   private final String authScheme;
   private final SocketAddress proxyAddress;
   private final SocketAddress destinationAddress;
   private String strVal;

   public ProxyConnectionEvent(String var1, String var2, SocketAddress var3, SocketAddress var4) {
      super();
      if (var1 == null) {
         throw new NullPointerException("protocol");
      } else if (var2 == null) {
         throw new NullPointerException("authScheme");
      } else if (var3 == null) {
         throw new NullPointerException("proxyAddress");
      } else if (var4 == null) {
         throw new NullPointerException("destinationAddress");
      } else {
         this.protocol = var1;
         this.authScheme = var2;
         this.proxyAddress = var3;
         this.destinationAddress = var4;
      }
   }

   public String protocol() {
      return this.protocol;
   }

   public String authScheme() {
      return this.authScheme;
   }

   public <T extends SocketAddress> T proxyAddress() {
      return this.proxyAddress;
   }

   public <T extends SocketAddress> T destinationAddress() {
      return this.destinationAddress;
   }

   public String toString() {
      if (this.strVal != null) {
         return this.strVal;
      } else {
         StringBuilder var1 = (new StringBuilder(128)).append(StringUtil.simpleClassName((Object)this)).append('(').append(this.protocol).append(", ").append(this.authScheme).append(", ").append(this.proxyAddress).append(" => ").append(this.destinationAddress).append(')');
         return this.strVal = var1.toString();
      }
   }
}
