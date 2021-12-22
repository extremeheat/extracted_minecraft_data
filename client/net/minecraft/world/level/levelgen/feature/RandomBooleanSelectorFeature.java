package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomBooleanSelectorFeature extends Feature<RandomBooleanFeatureConfiguration> {
   public RandomBooleanSelectorFeature(Codec<RandomBooleanFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<RandomBooleanFeatureConfiguration> var1) {
      Random var2 = var1.random();
      RandomBooleanFeatureConfiguration var3 = (RandomBooleanFeatureConfiguration)var1.config();
      WorldGenLevel var4 = var1.level();
      ChunkGenerator var5 = var1.chunkGenerator();
      BlockPos var6 = var1.origin();
      boolean var7 = var2.nextBoolean();
      return var7 ? ((PlacedFeature)var3.featureTrue.get()).place(var4, var5, var2, var6) : ((PlacedFeature)var3.featureFalse.get()).place(var4, var5, var2, var6);
   }
}
