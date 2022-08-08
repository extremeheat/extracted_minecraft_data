package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class HugeMushroomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<HugeMushroomFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("cap_provider").forGetter((var0x) -> {
         return var0x.capProvider;
      }), BlockStateProvider.CODEC.fieldOf("stem_provider").forGetter((var0x) -> {
         return var0x.stemProvider;
      }), Codec.INT.fieldOf("foliage_radius").orElse(2).forGetter((var0x) -> {
         return var0x.foliageRadius;
      })).apply(var0, HugeMushroomFeatureConfiguration::new);
   });
   public final BlockStateProvider capProvider;
   public final BlockStateProvider stemProvider;
   public final int foliageRadius;

   public HugeMushroomFeatureConfiguration(BlockStateProvider var1, BlockStateProvider var2, int var3) {
      super();
      this.capProvider = var1;
      this.stemProvider = var2;
      this.foliageRadius = var3;
   }
}
