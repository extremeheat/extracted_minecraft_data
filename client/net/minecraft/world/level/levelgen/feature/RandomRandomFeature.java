package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class RandomRandomFeature extends Feature<RandomRandomFeatureConfig> {
   public RandomRandomFeature(Function<Dynamic<?>, ? extends RandomRandomFeatureConfig> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, RandomRandomFeatureConfig var5) {
      int var6 = var3.nextInt(5) - 3 + var5.count;

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var3.nextInt(var5.features.size());
         ConfiguredFeature var9 = (ConfiguredFeature)var5.features.get(var8);
         var9.place(var1, var2, var3, var4);
      }

      return true;
   }
}
