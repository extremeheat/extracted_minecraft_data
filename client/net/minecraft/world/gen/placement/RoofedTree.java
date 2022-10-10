package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class RoofedTree extends BasePlacement<NoPlacementConfig> {
   public RoofedTree() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoPlacementConfig var5, Feature<C> var6, C var7) {
      for(int var8 = 0; var8 < 4; ++var8) {
         for(int var9 = 0; var9 < 4; ++var9) {
            int var10 = var8 * 4 + 1 + var3.nextInt(3);
            int var11 = var9 * 4 + 1 + var3.nextInt(3);
            var6.func_212245_a(var1, var2, var3, var1.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var4.func_177982_a(var10, 0, var11)), var7);
         }
      }

      return true;
   }
}
