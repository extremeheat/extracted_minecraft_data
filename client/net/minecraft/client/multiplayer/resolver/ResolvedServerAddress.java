package net.minecraft.client.multiplayer.resolver;

import java.net.InetSocketAddress;

public interface ResolvedServerAddress {
   String getHostName();

   String getHostIp();

   int getPort();

   InetSocketAddress asInetSocketAddress();

   static ResolvedServerAddress from(final InetSocketAddress address) {
      return new ResolvedServerAddress() {
         public String getHostName() {
            return address.getAddress().getHostName();
         }

         public String getHostIp() {
            return address.getAddress().getHostAddress();
         }

         public int getPort() {
            return address.getPort();
         }

         public InetSocketAddress asInetSocketAddress() {
            return address;
         }
      };
   }
}
