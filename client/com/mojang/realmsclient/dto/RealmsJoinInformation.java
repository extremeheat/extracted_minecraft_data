package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public record RealmsJoinInformation(@Nullable String address, @Nullable String resourcePackUrl, @Nullable String resourcePackHash, @Nullable RegionData regionData) implements ReflectionBasedSerialization {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final RealmsJoinInformation EMPTY = new RealmsJoinInformation((String)null, (String)null, (String)null, (RegionData)null);

   public RealmsJoinInformation(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable RegionData var4) {
      super();
      this.address = var1;
      this.resourcePackUrl = var2;
      this.resourcePackHash = var3;
      this.regionData = var4;
   }

   public static RealmsJoinInformation parse(GuardedSerializer var0, String var1) {
      try {
         RealmsJoinInformation var2 = (RealmsJoinInformation)var0.fromJson(var1, RealmsJoinInformation.class);
         if (var2 == null) {
            LOGGER.error("Could not parse RealmsServerAddress: {}", var1);
            return EMPTY;
         } else {
            return var2;
         }
      } catch (Exception var3) {
         LOGGER.error("Could not parse RealmsServerAddress: {}", var3.getMessage());
         return EMPTY;
      }
   }

   @SerializedName("address")
   @Nullable
   public String address() {
      return this.address;
   }

   @SerializedName("resourcePackUrl")
   @Nullable
   public String resourcePackUrl() {
      return this.resourcePackUrl;
   }

   @SerializedName("resourcePackHash")
   @Nullable
   public String resourcePackHash() {
      return this.resourcePackHash;
   }

   @SerializedName("sessionRegionData")
   @Nullable
   public RegionData regionData() {
      return this.regionData;
   }

   public static record RegionData(@Nullable RealmsRegion region, @Nullable ServiceQuality serviceQuality) implements ReflectionBasedSerialization {
      public RegionData(@Nullable RealmsRegion var1, @Nullable ServiceQuality var2) {
         super();
         this.region = var1;
         this.serviceQuality = var2;
      }

      @SerializedName("regionName")
      @Nullable
      public RealmsRegion region() {
         return this.region;
      }

      @SerializedName("serviceQuality")
      @Nullable
      public ServiceQuality serviceQuality() {
         return this.serviceQuality;
      }
   }
}
