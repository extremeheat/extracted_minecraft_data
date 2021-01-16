package io.netty.channel.embedded;

import java.net.SocketAddress;

final class EmbeddedSocketAddress extends SocketAddress {
   private static final long serialVersionUID = 1400788804624980619L;

   EmbeddedSocketAddress() {
      super();
   }

   public String toString() {
      return "embedded";
   }
}
