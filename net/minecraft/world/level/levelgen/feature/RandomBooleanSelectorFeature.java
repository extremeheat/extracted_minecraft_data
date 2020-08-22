package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.RandomBooleanFeatureConfiguration;

public class RandomBooleanSelectorFeature extends Feature {
   public RandomBooleanSelectorFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, RandomBooleanFeatureConfiguration var5) {
      boolean var6 = var3.nextBoolean();
      return var6 ? var5.featureTrue.place(var1, var2, var3, var4) : var5.featureFalse.place(var1, var2, var3, var4);
   }
}
