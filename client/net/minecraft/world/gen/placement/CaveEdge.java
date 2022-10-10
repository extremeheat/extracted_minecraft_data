package net.minecraft.world.gen.placement;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class CaveEdge extends BasePlacement<CaveEdgeConfig> {
   public CaveEdge() {
      super();
   }

   public <C extends IFeatureConfig> boolean func_201491_a_(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, CaveEdgeConfig var5, Feature<C> var6, C var7) {
      IChunk var8 = var1.func_205771_y(var4);
      ChunkPos var9 = var8.func_76632_l();
      BitSet var10 = var8.func_205749_a(var5.field_206928_a);

      for(int var11 = 0; var11 < var10.length(); ++var11) {
         if (var10.get(var11) && var3.nextFloat() < var5.field_206929_b) {
            int var12 = var11 & 15;
            int var13 = var11 >> 4 & 15;
            int var14 = var11 >> 8;
            var6.func_212245_a(var1, var2, var3, new BlockPos(var9.func_180334_c() + var12, var14, var9.func_180333_d() + var13), var7);
         }
      }

      return true;
   }
}
