package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Locale;

public record RegionPingResult(String regionName, int ping) implements ReflectionBasedSerialization {
   public RegionPingResult(String var1, int var2) {
      super();
      this.regionName = var1;
      this.ping = var2;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s --> %.2f ms", this.regionName, (float)this.ping);
   }

   @SerializedName("regionName")
   public String regionName() {
      return this.regionName;
   }

   @SerializedName("ping")
   public int ping() {
      return this.ping;
   }
}
