package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class IcebergPlacement extends BasePlacement<ChanceConfig> {
   public IcebergPlacement() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, ChanceConfig var5, Feature<C> var6, C var7) {
      if (var3.nextFloat() < 1.0F / (float)var5.field_202477_a) {
         int var8 = var3.nextInt(8) + 4;
         int var9 = var3.nextInt(8) + 4;
         BlockPos var10 = var1.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var4.func_177982_a(var8, 0, var9));
         var6.func_212245_a(var1, var2, var3, var10, var7);
      }

      return true;
   }
}
