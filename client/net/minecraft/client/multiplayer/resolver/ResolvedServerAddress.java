package net.minecraft.client.multiplayer.resolver;

import java.net.InetSocketAddress;

public interface ResolvedServerAddress {
   String getHostName();

   String getHostIp();

   int getPort();

   InetSocketAddress asInetSocketAddress();

   static ResolvedServerAddress from(final InetSocketAddress var0) {
      return new ResolvedServerAddress() {
         public String getHostName() {
            return var0.getAddress().getHostName();
         }

         public String getHostIp() {
            return var0.getAddress().getHostAddress();
         }

         public int getPort() {
            return var0.getPort();
         }

         public InetSocketAddress asInetSocketAddress() {
            return var0;
         }
      };
   }
}
