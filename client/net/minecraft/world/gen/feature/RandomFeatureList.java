package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class RandomFeatureList extends Feature<RandomDefaultFeatureListConfig> {
   public RandomFeatureList() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, RandomDefaultFeatureListConfig var5) {
      for(int var6 = 0; var6 < var5.field_202449_a.length; ++var6) {
         if (var3.nextFloat() < var5.field_202451_c[var6]) {
            return this.func_202362_a(var5.field_202449_a[var6], var5.field_202450_b[var6], var1, var2, var3, var4);
         }
      }

      return this.func_202362_a(var5.field_202452_d, var5.field_202453_f, var1, var2, var3, var4);
   }

   <FC extends IFeatureConfig> boolean func_202362_a(Feature<FC> var1, IFeatureConfig var2, IWorld var3, IChunkGenerator<? extends IChunkGenSettings> var4, Random var5, BlockPos var6) {
      return var1.func_212245_a(var3, var4, var5, var6, var2);
   }
}
