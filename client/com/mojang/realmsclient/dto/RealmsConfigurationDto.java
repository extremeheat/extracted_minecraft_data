package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record RealmsConfigurationDto(RealmsSlotUpdateDto options, List<RealmsSetting> settings, @Nullable RegionSelectionPreferenceDto regionSelectionPreference, @Nullable RealmsDescriptionDto description) implements ReflectionBasedSerialization {
   public RealmsConfigurationDto {
      super();
   }

   @SerializedName("options")
   public RealmsSlotUpdateDto options() {
      return this.options;
   }

   @SerializedName("settings")
   public List<RealmsSetting> settings() {
      return this.settings;
   }

   @SerializedName("regionSelectionPreference")
   public @Nullable RegionSelectionPreferenceDto regionSelectionPreference() {
      return this.regionSelectionPreference;
   }

   @SerializedName("description")
   public @Nullable RealmsDescriptionDto description() {
      return this.description;
   }
}
