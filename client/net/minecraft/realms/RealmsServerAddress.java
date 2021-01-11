package net.minecraft.realms;

import net.minecraft.client.multiplayer.ServerAddress;

public class RealmsServerAddress {
   private final String host;
   private final int port;

   protected RealmsServerAddress(String var1, int var2) {
      super();
      this.host = var1;
      this.port = var2;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public static RealmsServerAddress parseString(String var0) {
      ServerAddress var1 = ServerAddress.func_78860_a(var0);
      return new RealmsServerAddress(var1.func_78861_a(), var1.func_78864_b());
   }
}
