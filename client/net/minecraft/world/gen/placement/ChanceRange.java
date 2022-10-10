package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ChanceRange extends BasePlacement<ChanceRangeConfig> {
   public ChanceRange() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, ChanceRangeConfig var5, Feature<C> var6, C var7) {
      if (var3.nextFloat() < var5.field_202488_a) {
         int var8 = var3.nextInt(16);
         int var9 = var3.nextInt(var5.field_202491_d - var5.field_202489_b) + var5.field_202490_c;
         int var10 = var3.nextInt(16);
         var6.func_212245_a(var1, var2, var3, var4.func_177982_a(var8, var9, var10), var7);
      }

      return true;
   }
}
