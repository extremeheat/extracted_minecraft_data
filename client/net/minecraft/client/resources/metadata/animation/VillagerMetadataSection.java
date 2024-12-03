package net.minecraft.client.resources.metadata.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.StringRepresentable;

public record VillagerMetadataSection(Hat hat) {
   public static final Codec<VillagerMetadataSection> CODEC = RecordCodecBuilder.create((var0) -> var0.group(VillagerMetadataSection.Hat.CODEC.optionalFieldOf("hat", VillagerMetadataSection.Hat.NONE).forGetter(VillagerMetadataSection::hat)).apply(var0, VillagerMetadataSection::new));
   public static final MetadataSectionType<VillagerMetadataSection> TYPE;

   public VillagerMetadataSection(Hat var1) {
      super();
      this.hat = var1;
   }

   static {
      TYPE = new MetadataSectionType<VillagerMetadataSection>("villager", CODEC);
   }

   public static enum Hat implements StringRepresentable {
      NONE("none"),
      PARTIAL("partial"),
      FULL("full");

      public static final Codec<Hat> CODEC = StringRepresentable.<Hat>fromEnum(Hat::values);
      private final String name;

      private Hat(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Hat[] $values() {
         return new Hat[]{NONE, PARTIAL, FULL};
      }
   }
}
