package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.resolver.AddressResolverGroup;
import java.net.SocketAddress;

public final class BootstrapConfig extends AbstractBootstrapConfig<Bootstrap, Channel> {
   BootstrapConfig(Bootstrap var1) {
      super(var1);
   }

   public SocketAddress remoteAddress() {
      return ((Bootstrap)this.bootstrap).remoteAddress();
   }

   public AddressResolverGroup<?> resolver() {
      return ((Bootstrap)this.bootstrap).resolver();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(super.toString());
      var1.setLength(var1.length() - 1);
      var1.append(", resolver: ").append(this.resolver());
      SocketAddress var2 = this.remoteAddress();
      if (var2 != null) {
         var1.append(", remoteAddress: ").append(var2);
      }

      return var1.append(')').toString();
   }
}
