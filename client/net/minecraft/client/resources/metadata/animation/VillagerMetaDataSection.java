package net.minecraft.client.resources.metadata.animation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class VillagerMetaDataSection {
   public static final VillagerMetadataSectionSerializer SERIALIZER = new VillagerMetadataSectionSerializer();
   public static final String SECTION_NAME = "villager";
   private final Hat hat;

   public VillagerMetaDataSection(Hat var1) {
      super();
      this.hat = var1;
   }

   public Hat getHat() {
      return this.hat;
   }

   public static enum Hat {
      NONE("none"),
      PARTIAL("partial"),
      FULL("full");

      private static final Map<String, Hat> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Hat::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Hat(final String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      public static Hat getByName(String var0) {
         return (Hat)BY_NAME.getOrDefault(var0, NONE);
      }

      // $FF: synthetic method
      private static Hat[] $values() {
         return new Hat[]{NONE, PARTIAL, FULL};
      }
   }
}
