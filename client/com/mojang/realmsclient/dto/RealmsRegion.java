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
   CANADA_CENTRAL("CanadaCentral", "realms.configuration.region.canada_central"),
   CENTRAL_INDIA("CentralIndia", "realms.configuration.region.central_india"),
   CENTRAL_US("CentralUs", "realms.configuration.region.central_us"),
   EAST_ASIA("EastAsia", "realms.configuration.region.east_asia"),
   EAST_US("EastUs", "realms.configuration.region.east_us"),
   EAST_US_2("EastUs2", "realms.configuration.region.east_us_2"),
   FRANCE_CENTRAL("FranceCentral", "realms.configuration.region.france_central"),
   JAPAN_EAST("JapanEast", "realms.configuration.region.japan_east"),
   JAPAN_WEST("JapanWest", "realms.configuration.region.japan_west"),
   KOREA_CENTRAL("KoreaCentral", "realms.configuration.region.korea_central"),
   MEXICO_CENTRAL("MexicoCentral", "realms.configuration.region.mexico_central"),
   NORTH_CENTRAL_US("NorthCentralUs", "realms.configuration.region.north_central_us"),
   NORTH_EUROPE("NorthEurope", "realms.configuration.region.north_europe"),
   SOUTH_CENTRAL_US("SouthCentralUs", "realms.configuration.region.south_central_us"),
   SOUTHEAST_ASIA("SoutheastAsia", "realms.configuration.region.southeast_asia"),
   SOUTH_AFRICA_NORTH("SouthAfricaNorth", "realms.configuration.region.south_africa_north"),
   SWEDEN_CENTRAL("SwedenCentral", "realms.configuration.region.sweden_central"),
   UAE_NORTH("UAENorth", "realms.configuration.region.uae_north"),
   UK_SOUTH("UKSouth", "realms.configuration.region.uk_south"),
   WEST_CENTRAL_US("WestCentralUs", "realms.configuration.region.west_central_us"),
   WEST_EUROPE("WestEurope", "realms.configuration.region.west_europe"),
   WEST_US("WestUs", "realms.configuration.region.west_us"),
   WEST_US_2("WestUs2", "realms.configuration.region.west_us_2"),
   WEST_US_3("WestUs3", "realms.configuration.region.west_us_3"),
   INVALID_REGION("invalid", "");

   public final String nameId;
   public final String translationKey;

   private RealmsRegion(final String nameId, final String translationKey) {
      this.nameId = nameId;
      this.translationKey = translationKey;
   }

   public static @Nullable RealmsRegion findByNameId(final String nameIdStr) {
      for(RealmsRegion value : values()) {
         if (value.nameId.equals(nameIdStr)) {
            return value;
         }
      }

      return null;
   }

   // $FF: synthetic method
   private static RealmsRegion[] $values() {
      return new RealmsRegion[]{AUSTRALIA_EAST, AUSTRALIA_SOUTHEAST, BRAZIL_SOUTH, CANADA_CENTRAL, CENTRAL_INDIA, CENTRAL_US, EAST_ASIA, EAST_US, EAST_US_2, FRANCE_CENTRAL, JAPAN_EAST, JAPAN_WEST, KOREA_CENTRAL, MEXICO_CENTRAL, NORTH_CENTRAL_US, NORTH_EUROPE, SOUTH_CENTRAL_US, SOUTHEAST_ASIA, SOUTH_AFRICA_NORTH, SWEDEN_CENTRAL, UAE_NORTH, UK_SOUTH, WEST_CENTRAL_US, WEST_EUROPE, WEST_US, WEST_US_2, WEST_US_3, INVALID_REGION};
   }

   public static class RealmsRegionJsonAdapter extends TypeAdapter<RealmsRegion> {
      private static final Logger LOGGER = LogUtils.getLogger();

      public RealmsRegionJsonAdapter() {
         super();
      }

      public void write(final JsonWriter jsonWriter, final RealmsRegion realmsRegion) throws IOException {
         jsonWriter.value(realmsRegion.nameId);
      }

      public RealmsRegion read(final JsonReader jsonReader) throws IOException {
         String nameId = jsonReader.nextString();
         RealmsRegion realmsRegion = RealmsRegion.findByNameId(nameId);
         if (realmsRegion == null) {
            LOGGER.warn("Unsupported RealmsRegion {}", nameId);
            return RealmsRegion.INVALID_REGION;
         } else {
            return realmsRegion;
         }
      }
   }
}
