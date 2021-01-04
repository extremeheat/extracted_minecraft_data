package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class RandomBooleanSelectorFeature extends Feature<RandomBooleanFeatureConfig> {
   public RandomBooleanSelectorFeature(Function<Dynamic<?>, ? extends RandomBooleanFeatureConfig> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, RandomBooleanFeatureConfig var5) {
      boolean var6 = var3.nextBoolean();
      return var6 ? var5.featureTrue.place(var1, var2, var3, var4) : var5.featureFalse.place(var1, var2, var3, var4);
   }
}
