package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGeneratorEnd;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class EndGateway extends BasePlacement<NoPlacementConfig> {
   public EndGateway() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoPlacementConfig var5, Feature<C> var6, C var7) {
      boolean var8 = false;
      if (var3.nextInt(700) == 0) {
         int var9 = var3.nextInt(16);
         int var10 = var3.nextInt(16);
         int var11 = var1.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var4.func_177982_a(var9, 0, var10)).func_177956_o();
         if (var11 > 0) {
            int var12 = var11 + 3 + var3.nextInt(7);
            BlockPos var13 = var4.func_177982_a(var9, var12, var10);
            var6.func_212245_a(var1, var2, var3, var13, var7);
            TileEntity var14 = var1.func_175625_s(var4);
            if (var14 instanceof TileEntityEndGateway) {
               TileEntityEndGateway var15 = (TileEntityEndGateway)var14;
               var15.func_195489_b(((ChunkGeneratorEnd)var2).func_202112_d());
            }
         }
      }

      return false;
   }
}
