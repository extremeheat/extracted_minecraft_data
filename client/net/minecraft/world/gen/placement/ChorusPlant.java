package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ChorusPlant extends BasePlacement<NoPlacementConfig> {
   public ChorusPlant() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoPlacementConfig var5, Feature<C> var6, C var7) {
      boolean var8 = false;
      int var9 = var3.nextInt(5);

      for(int var10 = 0; var10 < var9; ++var10) {
         int var11 = var3.nextInt(16);
         int var12 = var3.nextInt(16);
         int var13 = var1.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var4.func_177982_a(var11, 0, var12)).func_177956_o();
         if (var13 > 0) {
            int var14 = var13 - 1;
            var8 |= var6.func_212245_a(var1, var2, var3, new BlockPos(var4.func_177958_n() + var11, var14, var4.func_177952_p() + var12), var7);
         }
      }

      return var8;
   }
}
