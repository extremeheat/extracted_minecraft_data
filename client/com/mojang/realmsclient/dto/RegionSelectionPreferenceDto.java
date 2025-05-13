package com.mojang.realmsclient.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class RegionSelectionPreferenceDto extends ValueObject implements ReflectionBasedSerialization {
   public static final RegionSelectionPreferenceDto DEFAULT;
   private static final Logger LOGGER;
   @SerializedName("regionSelectionPreference")
   @JsonAdapter(RegionSelectionPreference.RegionSelectionPreferenceJsonAdapter.class)
   public RegionSelectionPreference regionSelectionPreference;
   @SerializedName("preferredRegion")
   @JsonAdapter(RealmsRegion.RealmsRegionJsonAdapter.class)
   @Nullable
   public RealmsRegion preferredRegion;

   public RegionSelectionPreferenceDto(RegionSelectionPreference var1, @Nullable RealmsRegion var2) {
      super();
      this.regionSelectionPreference = var1;
      this.preferredRegion = var2;
   }

   private RegionSelectionPreferenceDto() {
      super();
   }

   public static RegionSelectionPreferenceDto parse(GuardedSerializer var0, String var1) {
      try {
         RegionSelectionPreferenceDto var2 = (RegionSelectionPreferenceDto)var0.fromJson(var1, RegionSelectionPreferenceDto.class);
         if (var2 == null) {
            LOGGER.error("Could not parse RegionSelectionPreference: {}", var1);
            return new RegionSelectionPreferenceDto();
         } else {
            return var2;
         }
      } catch (Exception var3) {
         LOGGER.error("Could not parse RegionSelectionPreference: {}", var3.getMessage());
         return new RegionSelectionPreferenceDto();
      }
   }

   public RegionSelectionPreferenceDto clone() {
      return new RegionSelectionPreferenceDto(this.regionSelectionPreference, this.preferredRegion);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }

   static {
      DEFAULT = new RegionSelectionPreferenceDto(RegionSelectionPreference.AUTOMATIC_OWNER, (RealmsRegion)null);
      LOGGER = LogUtils.getLogger();
   }
}
