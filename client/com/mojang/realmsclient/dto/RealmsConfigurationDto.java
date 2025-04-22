package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import javax.annotation.Nullable;

public record RealmsConfigurationDto(RealmsSlotUpdateDto options, List<RealmsSetting> settings, @Nullable RegionSelectionPreferenceDto regionSelectionPreference, @Nullable RealmsDescriptionDto description) implements ReflectionBasedSerialization {
   public RealmsConfigurationDto(RealmsSlotUpdateDto var1, List<RealmsSetting> var2, @Nullable RegionSelectionPreferenceDto var3, @Nullable RealmsDescriptionDto var4) {
      super();
      this.options = var1;
      this.settings = var2;
      this.regionSelectionPreference = var3;
      this.description = var4;
   }

   @SerializedName("options")
   public RealmsSlotUpdateDto options() {
      return this.options;
   }

   @SerializedName("settings")
   public List<RealmsSetting> settings() {
      return this.settings;
   }

   @Nullable
   @SerializedName("regionSelectionPreference")
   public RegionSelectionPreferenceDto regionSelectionPreference() {
      return this.regionSelectionPreference;
   }

   @Nullable
   @SerializedName("description")
   public RealmsDescriptionDto description() {
      return this.description;
   }
}
