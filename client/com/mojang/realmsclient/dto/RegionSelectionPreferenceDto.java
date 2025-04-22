package com.mojang.realmsclient.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import javax.annotation.Nullable;
import org.slf4j.Logger;

public class RegionSelectionPreferenceDto extends ValueObject implements ReflectionBasedSerialization {
   public static final RegionSelectionPreferenceDto DEFAULT;
   static final Logger LOGGER;
   @SerializedName("regionSelectionPreference")
   @JsonAdapter(RegionSelectionPreferenceJsonAdapter.class)
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
      DEFAULT = new RegionSelectionPreferenceDto(RegionSelectionPreferenceDto.RegionSelectionPreference.AUTOMATIC_OWNER, (RealmsRegion)null);
      LOGGER = LogUtils.getLogger();
   }

   public static enum RegionSelectionPreference {
      AUTOMATIC_OWNER(0, "realms.configuration.region_preference.automatic_owner"),
      AUTOMATIC_PLAYER(1, "realms.configuration.region_preference.automatic_player"),
      MANUAL(2, "");

      public static final RegionSelectionPreference DEFAULT_SELECTION = AUTOMATIC_PLAYER;
      public final int id;
      public final String translationKey;

      private RegionSelectionPreference(final int var3, final String var4) {
         this.id = var3;
         this.translationKey = var4;
      }

      // $FF: synthetic method
      private static RegionSelectionPreference[] $values() {
         return new RegionSelectionPreference[]{AUTOMATIC_OWNER, AUTOMATIC_PLAYER, MANUAL};
      }
   }

   static class RegionSelectionPreferenceJsonAdapter extends TypeAdapter<RegionSelectionPreference> {
      private RegionSelectionPreferenceJsonAdapter() {
         super();
      }

      public void write(JsonWriter var1, RegionSelectionPreference var2) throws IOException {
         var1.value((long)var2.id);
      }

      public RegionSelectionPreference read(JsonReader var1) throws IOException {
         int var2 = var1.nextInt();

         for(RegionSelectionPreference var6 : RegionSelectionPreferenceDto.RegionSelectionPreference.values()) {
            if (var6.id == var2) {
               return var6;
            }
         }

         RegionSelectionPreferenceDto.LOGGER.warn("Unsupported RegionSelectionPreference {}", var2);
         return RegionSelectionPreferenceDto.RegionSelectionPreference.DEFAULT_SELECTION;
      }

      // $FF: synthetic method
      public Object read(final JsonReader var1) throws IOException {
         return this.read(var1);
      }

      // $FF: synthetic method
      public void write(final JsonWriter var1, final Object var2) throws IOException {
         this.write(var1, (RegionSelectionPreference)var2);
      }
   }
}
