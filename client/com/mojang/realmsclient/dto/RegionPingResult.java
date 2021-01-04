package com.mojang.realmsclient.dto;

import java.util.Locale;

public class RegionPingResult extends ValueObject {
   private final String regionName;
   private final int ping;

   public RegionPingResult(String var1, int var2) {
      super();
      this.regionName = var1;
      this.ping = var2;
   }

   public int ping() {
      return this.ping;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s --> %.2f ms", this.regionName, (float)this.ping);
   }
}
