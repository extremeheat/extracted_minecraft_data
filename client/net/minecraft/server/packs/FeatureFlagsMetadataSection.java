package net.minecraft.server.packs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public record FeatureFlagsMetadataSection(FeatureFlagSet flags) {
   private static final Codec<FeatureFlagsMetadataSection> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(FeatureFlags.CODEC.fieldOf("enabled").forGetter(FeatureFlagsMetadataSection::flags)).apply(var0, FeatureFlagsMetadataSection::new)
   );
   public static final MetadataSectionType<FeatureFlagsMetadataSection> TYPE = MetadataSectionType.fromCodec("features", CODEC);

   public FeatureFlagsMetadataSection(FeatureFlagSet flags) {
      super();
      this.flags = flags;
   }
}
