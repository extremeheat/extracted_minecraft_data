package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class RandomSelectorFeature extends Feature<RandomFeatureConfig> {
   public RandomSelectorFeature(Function<Dynamic<?>, ? extends RandomFeatureConfig> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, RandomFeatureConfig var5) {
      Iterator var6 = var5.features.iterator();

      WeightedConfiguredFeature var7;
      do {
         if (!var6.hasNext()) {
            return var5.defaultFeature.place(var1, var2, var3, var4);
         }

         var7 = (WeightedConfiguredFeature)var6.next();
      } while(var3.nextFloat() >= var7.chance);

      return var7.place(var1, var2, var3, var4);
   }
}
