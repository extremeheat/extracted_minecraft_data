package com.mojang.realmsclient.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public enum RealmsRegion {
   AUSTRALIA_EAST("AustraliaEast", "realms.configuration.region.australia_east"),
   AUSTRALIA_SOUTHEAST("AustraliaSoutheast", "realms.configuration.region.australia_southeast"),
   BRAZIL_SOUTH("BrazilSouth", "realms.configuration.region.brazil_south"),
   CENTRAL_INDIA("CentralIndia", "realms.configuration.region.central_india"),
   CENTRAL_US("CentralUs", "realms.configuration.region.central_us"),
   EAST_ASIA("EastAsia", "realms.configuration.region.east_asia"),
   EAST_US("EastUs", "realms.configuration.region.east_us"),
   EAST_US_2("EastUs2", "realms.configuration.region.east_us_2"),
   FRANCE_CENTRAL("FranceCentral", "realms.configuration.region.france_central"),
   JAPAN_EAST("JapanEast", "realms.configuration.region.japan_east"),
   JAPAN_WEST("JapanWest", "realms.configuration.region.japan_west"),
   KOREA_CENTRAL("KoreaCentral", "realms.configuration.region.korea_central"),
   NORTH_CENTRAL_US("NorthCentralUs", "realms.configuration.region.north_central_us"),
   NORTH_EUROPE("NorthEurope", "realms.configuration.region.north_europe"),
   SOUTH_CENTRAL_US("SouthCentralUs", "realms.configuration.region.south_central_us"),
   SOUTHEAST_ASIA("SoutheastAsia", "realms.configuration.region.southeast_asia"),
   SWEDEN_CENTRAL("SwedenCentral", "realms.configuration.region.sweden_central"),
   UAE_NORTH("UAENorth", "realms.configuration.region.uae_north"),
   UK_SOUTH("UKSouth", "realms.configuration.region.uk_south"),
   WEST_CENTRAL_US("WestCentralUs", "realms.configuration.region.west_central_us"),
   WEST_EUROPE("WestEurope", "realms.configuration.region.west_europe"),
   WEST_US("WestUs", "realms.configuration.region.west_us"),
   WEST_US_2("WestUs2", "realms.configuration.region.west_us_2"),
   INVALID_REGION("invalid", "");

   public final String nameId;
   public final String translationKey;

   private RealmsRegion(final String var3, final String var4) {
      this.nameId = var3;
      this.translationKey = var4;
   }

   public static @Nullable RealmsRegion findByNameId(String var0) {
      for(RealmsRegion var4 : values()) {
         if (var4.nameId.equals(var0)) {
            return var4;
         }
      }

      return null;
   }

   // $FF: synthetic method
   private static RealmsRegion[] $values() {
      return new RealmsRegion[]{AUSTRALIA_EAST, AUSTRALIA_SOUTHEAST, BRAZIL_SOUTH, CENTRAL_INDIA, CENTRAL_US, EAST_ASIA, EAST_US, EAST_US_2, FRANCE_CENTRAL, JAPAN_EAST, JAPAN_WEST, KOREA_CENTRAL, NORTH_CENTRAL_US, NORTH_EUROPE, SOUTH_CENTRAL_US, SOUTHEAST_ASIA, SWEDEN_CENTRAL, UAE_NORTH, UK_SOUTH, WEST_CENTRAL_US, WEST_EUROPE, WEST_US, WEST_US_2, INVALID_REGION};
   }

   public static class RealmsRegionJsonAdapter extends TypeAdapter<RealmsRegion> {
      private static final Logger LOGGER = LogUtils.getLogger();

      public RealmsRegionJsonAdapter() {
         super();
      }

      public void write(JsonWriter var1, RealmsRegion var2) throws IOException {
         var1.value(var2.nameId);
      }

      public RealmsRegion read(JsonReader var1) throws IOException {
         String var2 = var1.nextString();
         RealmsRegion var3 = RealmsRegion.findByNameId(var2);
         if (var3 == null) {
            LOGGER.warn("Unsupported RealmsRegion {}", var2);
            return RealmsRegion.INVALID_REGION;
         } else {
            return var3;
         }
      }

      // $FF: synthetic method
      public Object read(final JsonReader var1) throws IOException {
         return this.read(var1);
      }

      // $FF: synthetic method
      public void write(final JsonWriter var1, final Object var2) throws IOException {
         this.write(var1, (RealmsRegion)var2);
      }
   }
}
