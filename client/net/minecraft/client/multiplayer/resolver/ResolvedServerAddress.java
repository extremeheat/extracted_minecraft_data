package net.minecraft.client.multiplayer.resolver;

import java.net.InetSocketAddress;

public interface ResolvedServerAddress {
   String getHostName();

   String getHostIp();

   int getPort();

   InetSocketAddress asInetSocketAddress();

   static ResolvedServerAddress from(final InetSocketAddress var0) {
      return new ResolvedServerAddress() {
         @Override
         public String getHostName() {
            return var0.getAddress().getHostName();
         }

         @Override
         public String getHostIp() {
            return var0.getAddress().getHostAddress();
         }

         @Override
         public int getPort() {
            return var0.getPort();
         }

         @Override
         public InetSocketAddress asInetSocketAddress() {
            return var0;
         }
      };
   }
}
