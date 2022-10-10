package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class RandomFeatureWithConfigFeature extends Feature<RandomFeatureWithConfigConfig> {
   public RandomFeatureWithConfigFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, RandomFeatureWithConfigConfig var5) {
      int var6 = var3.nextInt(var5.field_204628_a.length);
      return this.func_204627_a(var5.field_204628_a[var6], var5.field_204629_b[var6], var1, var2, var3, var4);
   }

   <FC extends IFeatureConfig> boolean func_204627_a(Feature<FC> var1, IFeatureConfig var2, IWorld var3, IChunkGenerator<? extends IChunkGenSettings> var4, Random var5, BlockPos var6) {
      return var1.func_212245_a(var3, var4, var5, var6, var2);
   }
}
