package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class HugeMushroomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<HugeMushroomFeatureConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateProvider.CODEC.fieldOf("cap_provider").forGetter((c) -> c.capProvider), BlockStateProvider.CODEC.fieldOf("stem_provider").forGetter((c) -> c.stemProvider), Codec.INT.fieldOf("foliage_radius").orElse(2).forGetter((c) -> c.foliageRadius)).apply(i, HugeMushroomFeatureConfiguration::new));
   public final BlockStateProvider capProvider;
   public final BlockStateProvider stemProvider;
   public final int foliageRadius;

   public HugeMushroomFeatureConfiguration(final BlockStateProvider capProvider, final BlockStateProvider stemProvider, final int foliageRadius) {
      super();
      this.capProvider = capProvider;
      this.stemProvider = stemProvider;
      this.foliageRadius = foliageRadius;
   }
}
