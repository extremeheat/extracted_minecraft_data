package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TopSolidRange extends BasePlacement<TopSolidRangeConfig> {
   public TopSolidRange() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, TopSolidRangeConfig var5, Feature<C> var6, C var7) {
      int var8 = var3.nextInt(var5.field_204633_b - var5.field_204632_a) + var5.field_204632_a;

      for(int var9 = 0; var9 < var8; ++var9) {
         int var10 = var3.nextInt(16);
         int var11 = var3.nextInt(16);
         int var12 = var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, var4.func_177958_n() + var10, var4.func_177952_p() + var11);
         var6.func_212245_a(var1, var2, var3, new BlockPos(var4.func_177958_n() + var10, var12, var4.func_177952_p() + var11), var7);
      }

      return false;
   }
}
