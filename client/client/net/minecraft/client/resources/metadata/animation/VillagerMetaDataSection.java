package net.minecraft.client.resources.metadata.animation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class VillagerMetaDataSection {
   public static final VillagerMetadataSectionSerializer SERIALIZER = new VillagerMetadataSectionSerializer();
   public static final String SECTION_NAME = "villager";
   private final VillagerMetaDataSection.Hat hat;

   public VillagerMetaDataSection(VillagerMetaDataSection.Hat var1) {
      super();
      this.hat = var1;
   }

   public VillagerMetaDataSection.Hat getHat() {
      return this.hat;
   }

   public static enum Hat {
      NONE("none"),
      PARTIAL("partial"),
      FULL("full");

      private static final Map<String, VillagerMetaDataSection.Hat> BY_NAME = Arrays.stream(values())
         .collect(Collectors.toMap(VillagerMetaDataSection.Hat::getName, var0 -> (VillagerMetaDataSection.Hat)var0));
      private final String name;

      private Hat(final String param3) {
         this.name = nullxx;
      }

      public String getName() {
         return this.name;
      }

      public static VillagerMetaDataSection.Hat getByName(String var0) {
         return BY_NAME.getOrDefault(var0, NONE);
      }
   }
}
