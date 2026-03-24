package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record PingResult(List<RegionPingResult> pingResults, List<Long> realmIds) implements ReflectionBasedSerialization {
   public PingResult {
      super();
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
