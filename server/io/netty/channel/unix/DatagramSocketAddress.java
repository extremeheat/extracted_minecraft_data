package io.netty.channel.unix;

import java.net.InetSocketAddress;

public final class DatagramSocketAddress extends InetSocketAddress {
   private static final long serialVersionUID = 3094819287843178401L;
   private final int receivedAmount;
   private final DatagramSocketAddress localAddress;

   DatagramSocketAddress(String var1, int var2, int var3, DatagramSocketAddress var4) {
      super(var1, var2);
      this.receivedAmount = var3;
      this.localAddress = var4;
   }

   public DatagramSocketAddress localAddress() {
      return this.localAddress;
   }

   public int receivedAmount() {
      return this.receivedAmount;
   }
}
