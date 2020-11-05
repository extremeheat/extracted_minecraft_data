package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;

public class RandomBooleanSelectorFeature extends Feature<RandomBooleanFeatureConfiguration> {
   public RandomBooleanSelectorFeature(Codec<RandomBooleanFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, RandomBooleanFeatureConfiguration var5) {
      boolean var6 = var3.nextBoolean();
      return var6 ? ((ConfiguredFeature)var5.featureTrue.get()).place(var1, var2, var3, var4) : ((ConfiguredFeature)var5.featureFalse.get()).place(var1, var2, var3, var4);
   }
}
