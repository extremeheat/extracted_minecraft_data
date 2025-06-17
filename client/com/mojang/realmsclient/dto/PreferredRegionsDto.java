package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record PreferredRegionsDto(List<RegionDataDto> regionData) implements ReflectionBasedSerialization {
   public PreferredRegionsDto(List<RegionDataDto> var1) {
      super();
      this.regionData = var1;
   }

   public static PreferredRegionsDto empty() {
      return new PreferredRegionsDto(List.of());
   }

   @SerializedName("regionDataList")
   public List<RegionDataDto> regionData() {
      return this.regionData;
   }
}
