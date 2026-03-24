package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record RealmsJoinInformation(@Nullable String address, @Nullable String resourcePackUrl, @Nullable String resourcePackHash, @Nullable RegionData regionData) implements ReflectionBasedSerialization {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RealmsJoinInformation EMPTY = new RealmsJoinInformation((String)null, (String)null, (String)null, (RegionData)null);

   public RealmsJoinInformation {
      super();
   }

   public static RealmsJoinInformation parse(final GuardedSerializer gson, final String json) {
      try {
         RealmsJoinInformation server = (RealmsJoinInformation)gson.fromJson(json, RealmsJoinInformation.class);
         if (server == null) {
            LOGGER.error("Could not parse RealmsServerAddress: {}", json);
            return EMPTY;
         } else {
            return server;
         }
      } catch (Exception e) {
         LOGGER.error("Could not parse RealmsServerAddress", e);
         return EMPTY;
      }
   }

   @SerializedName("address")
   public @Nullable String address() {
      return this.address;
   }

   @SerializedName("resourcePackUrl")
   public @Nullable String resourcePackUrl() {
      return this.resourcePackUrl;
   }

   @SerializedName("resourcePackHash")
   public @Nullable String resourcePackHash() {
      return this.resourcePackHash;
   }

   @SerializedName("sessionRegionData")
   public @Nullable RegionData regionData() {
      return this.regionData;
   }

   public static record RegionData(@Nullable RealmsRegion region, @Nullable ServiceQuality serviceQuality) implements ReflectionBasedSerialization {
      public RegionData {
         super();
      }

      @SerializedName("regionName")
      public @Nullable RealmsRegion region() {
         return this.region;
      }

      @SerializedName("serviceQuality")
      public @Nullable ServiceQuality serviceQuality() {
         return this.serviceQuality;
      }
   }
}
