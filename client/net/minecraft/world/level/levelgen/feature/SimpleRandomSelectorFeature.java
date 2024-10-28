package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class SimpleRandomSelectorFeature extends Feature<SimpleRandomFeatureConfiguration> {
   public SimpleRandomSelectorFeature(Codec<SimpleRandomFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<SimpleRandomFeatureConfiguration> var1) {
      RandomSource var2 = var1.random();
      SimpleRandomFeatureConfiguration var3 = (SimpleRandomFeatureConfiguration)var1.config();
      WorldGenLevel var4 = var1.level();
      BlockPos var5 = var1.origin();
      ChunkGenerator var6 = var1.chunkGenerator();
      int var7 = var2.nextInt(var3.features.size());
      PlacedFeature var8 = (PlacedFeature)var3.features.get(var7).value();
      return var8.place(var4, var6, var2, var5);
   }
}
