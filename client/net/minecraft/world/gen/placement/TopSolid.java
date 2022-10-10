package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TopSolid extends BasePlacement<FrequencyConfig> {
   public TopSolid() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, FrequencyConfig var5, Feature<C> var6, C var7) {
      for(int var8 = 0; var8 < var5.field_202476_a; ++var8) {
         int var9 = var3.nextInt(16) + var4.func_177958_n();
         int var10 = var3.nextInt(16) + var4.func_177952_p();
         var6.func_212245_a(var1, var2, var3, new BlockPos(var9, var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, var9, var10), var10), var7);
      }

      return true;
   }
}
