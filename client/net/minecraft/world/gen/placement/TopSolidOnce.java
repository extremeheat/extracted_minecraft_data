package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TopSolidOnce extends BasePlacement<NoPlacementConfig> {
   public TopSolidOnce() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoPlacementConfig var5, Feature<C> var6, C var7) {
      int var8 = var3.nextInt(16);
      int var9 = var3.nextInt(16);
      int var10 = var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, var4.func_177958_n() + var8, var4.func_177952_p() + var9);
      var6.func_212245_a(var1, var2, var3, new BlockPos(var4.func_177958_n() + var8, var10, var4.func_177952_p() + var9), var7);
      return false;
   }
}
