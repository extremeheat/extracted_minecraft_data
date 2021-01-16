package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;

public class RandomSelectorFeature extends Feature<RandomFeatureConfiguration> {
   public RandomSelectorFeature(Codec<RandomFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, RandomFeatureConfiguration var5) {
      Iterator var6 = var5.features.iterator();

      WeightedConfiguredFeature var7;
      do {
         if (!var6.hasNext()) {
            return ((ConfiguredFeature)var5.defaultFeature.get()).place(var1, var2, var3, var4);
         }

         var7 = (WeightedConfiguredFeature)var6.next();
      } while(var3.nextFloat() >= var7.chance);

      return var7.place(var1, var2, var3, var4);
   }
}
