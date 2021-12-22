package net.minecraft.client.server;

import net.minecraft.Util;

public class LanServer {
   private final String motd;
   private final String address;
   private long pingTime;

   public LanServer(String var1, String var2) {
      super();
      this.motd = var1;
      this.address = var2;
      this.pingTime = Util.getMillis();
   }

   public String getMotd() {
      return this.motd;
   }

   public String getAddress() {
      return this.address;
   }

   public void updatePingTime() {
      this.pingTime = Util.getMillis();
   }
}
