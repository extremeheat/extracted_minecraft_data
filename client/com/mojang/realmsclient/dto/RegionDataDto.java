package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;

public record RegionDataDto(RealmsRegion region, ServiceQuality serviceQuality) implements ReflectionBasedSerialization {
   public RegionDataDto {
      super();
   }

   @SerializedName("regionName")
   public RealmsRegion region() {
      return this.region;
   }

   @SerializedName("serviceQuality")
   public ServiceQuality serviceQuality() {
      return this.serviceQuality;
   }
}
