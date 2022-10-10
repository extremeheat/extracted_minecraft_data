package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class RandomDefaultFeatureList extends Feature<RandomFeatureListConfig> {
   public RandomDefaultFeatureList() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, RandomFeatureListConfig var5) {
      int var6 = var3.nextInt(5) - 3 + var5.field_202456_c;

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var3.nextInt(var5.field_202454_a.length);
         this.func_202361_a(var5.field_202454_a[var8], var5.field_202455_b[var8], var1, var2, var3, var4);
      }

      return true;
   }

   <FC extends IFeatureConfig> boolean func_202361_a(Feature<FC> var1, IFeatureConfig var2, IWorld var3, IChunkGenerator<? extends IChunkGenSettings> var4, Random var5, BlockPos var6) {
      return var1.func_212245_a(var3, var4, var5, var6, var2);
   }
}
