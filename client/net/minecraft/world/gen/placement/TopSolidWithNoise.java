package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TopSolidWithNoise extends BasePlacement<TopSolidWithNoiseConfig> {
   public TopSolidWithNoise() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, TopSolidWithNoiseConfig var5, Feature<C> var6, C var7) {
      double var8 = Biome.field_180281_af.func_151601_a((double)var4.func_177958_n() / var5.field_204631_b, (double)var4.func_177952_p() / var5.field_204631_b);
      int var10 = (int)Math.ceil(var8 * (double)var5.field_204630_a);

      for(int var11 = 0; var11 < var10; ++var11) {
         int var12 = var3.nextInt(16);
         int var13 = var3.nextInt(16);
         int var14 = var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, var4.func_177958_n() + var12, var4.func_177952_p() + var13);
         var6.func_212245_a(var1, var2, var3, new BlockPos(var4.func_177958_n() + var12, var14, var4.func_177952_p() + var13), var7);
      }

      return false;
   }
}
