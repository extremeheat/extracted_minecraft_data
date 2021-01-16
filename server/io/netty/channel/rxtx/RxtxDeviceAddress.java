package io.netty.channel.rxtx;

import java.net.SocketAddress;

/** @deprecated */
@Deprecated
public class RxtxDeviceAddress extends SocketAddress {
   private static final long serialVersionUID = -2907820090993709523L;
   private final String value;

   public RxtxDeviceAddress(String var1) {
      super();
      this.value = var1;
   }

   public String value() {
      return this.value;
   }
}
