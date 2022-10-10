package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockSeaPickle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.placement.CountConfig;

public class SeaPickleFeature extends Feature<CountConfig> {
   public SeaPickleFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<?> var2, Random var3, BlockPos var4, CountConfig var5) {
      int var6 = 0;

      for(int var7 = 0; var7 < var5.field_204915_a; ++var7) {
         int var8 = var3.nextInt(8) - var3.nextInt(8);
         int var9 = var3.nextInt(8) - var3.nextInt(8);
         int var10 = var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR, var4.func_177958_n() + var8, var4.func_177952_p() + var9);
         BlockPos var11 = new BlockPos(var4.func_177958_n() + var8, var10, var4.func_177952_p() + var9);
         IBlockState var12 = (IBlockState)Blocks.field_204913_jW.func_176223_P().func_206870_a(BlockSeaPickle.field_204902_a, var3.nextInt(4) + 1);
         if (var1.func_180495_p(var11).func_177230_c() == Blocks.field_150355_j && var12.func_196955_c(var1, var11)) {
            var1.func_180501_a(var11, var12, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
