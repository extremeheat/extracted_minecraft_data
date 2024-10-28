package net.minecraft.server.packs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public record FeatureFlagsMetadataSection(FeatureFlagSet flags) {
   private static final Codec<FeatureFlagsMetadataSection> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(FeatureFlags.CODEC.fieldOf("enabled").forGetter(FeatureFlagsMetadataSection::flags)).apply(var0, FeatureFlagsMetadataSection::new);
   });
   public static final MetadataSectionType<FeatureFlagsMetadataSection> TYPE;

   public FeatureFlagsMetadataSection(FeatureFlagSet var1) {
      super();
      this.flags = var1;
   }

   public FeatureFlagSet flags() {
      return this.flags;
   }

   static {
      TYPE = MetadataSectionType.fromCodec("features", CODEC);
   }
}
