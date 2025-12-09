package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record PingResult(List<RegionPingResult> pingResults, List<Long> realmIds) implements ReflectionBasedSerialization {
   public PingResult(List<RegionPingResult> var1, List<Long> var2) {
      super();
      this.pingResults = var1;
      this.realmIds = var2;
   }

   @SerializedName("pingResults")
   public List<RegionPingResult> pingResults() {
      return this.pingResults;
   }

   @SerializedName("worldIds")
   public List<Long> realmIds() {
      return this.realmIds;
   }
}
