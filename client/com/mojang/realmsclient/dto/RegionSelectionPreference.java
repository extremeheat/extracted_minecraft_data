package com.mojang.realmsclient.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import org.slf4j.Logger;

public enum RegionSelectionPreference {
   AUTOMATIC_PLAYER(0, "realms.configuration.region_preference.automatic_player"),
   AUTOMATIC_OWNER(1, "realms.configuration.region_preference.automatic_owner"),
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
      return new RegionSelectionPreference[]{AUTOMATIC_PLAYER, AUTOMATIC_OWNER, MANUAL};
   }

   public static class RegionSelectionPreferenceJsonAdapter extends TypeAdapter<RegionSelectionPreference> {
      private static final Logger LOGGER = LogUtils.getLogger();

      public RegionSelectionPreferenceJsonAdapter() {
         super();
      }

      public void write(JsonWriter var1, RegionSelectionPreference var2) throws IOException {
         var1.value((long)var2.id);
      }

      public RegionSelectionPreference read(JsonReader var1) throws IOException {
         int var2 = var1.nextInt();

         for(RegionSelectionPreference var6 : RegionSelectionPreference.values()) {
            if (var6.id == var2) {
               return var6;
            }
         }

         LOGGER.warn("Unsupported RegionSelectionPreference {}", var2);
         return RegionSelectionPreference.DEFAULT_SELECTION;
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
