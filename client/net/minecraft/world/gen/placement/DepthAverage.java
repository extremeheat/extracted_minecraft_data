package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class DepthAverage extends BasePlacement<DepthAverageConfig> {
   public DepthAverage() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, DepthAverageConfig var5, Feature<C> var6, C var7) {
      int var8 = var5.field_202483_a;
      int var9 = var5.field_202484_b;
      int var10 = var5.field_202485_c;

      for(int var11 = 0; var11 < var8; ++var11) {
         int var12 = var3.nextInt(16);
         int var13 = var3.nextInt(var10) + var3.nextInt(var10) - var10 + var9;
         int var14 = var3.nextInt(16);
         BlockPos var15 = var4.func_177982_a(var12, var13, var14);
         var6.func_212245_a(var1, var2, var3, var15, var7);
      }

      return true;
   }
}
