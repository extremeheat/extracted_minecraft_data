package net.minecraft.server.packs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public record FeatureFlagsMetadataSection(FeatureFlagSet b) {
   private final FeatureFlagSet flags;
   private static final Codec<FeatureFlagsMetadataSection> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(FeatureFlags.CODEC.fieldOf("enabled").forGetter(FeatureFlagsMetadataSection::flags)).apply(var0, FeatureFlagsMetadataSection::new)
   );
   public static final MetadataSectionType<FeatureFlagsMetadataSection> TYPE = MetadataSectionType.fromCodec("features", CODEC);

   public FeatureFlagsMetadataSection(FeatureFlagSet var1) {
      super();
      this.flags = var1;
   }
}
