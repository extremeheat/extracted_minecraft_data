package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockSeaGrassTall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class SeaGrassFeature extends Feature<SeaGrassConfig> {
   public SeaGrassFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, SeaGrassConfig var5) {
      int var6 = 0;

      for(int var7 = 0; var7 < var5.field_203237_a; ++var7) {
         int var8 = var3.nextInt(8) - var3.nextInt(8);
         int var9 = var3.nextInt(8) - var3.nextInt(8);
         int var10 = var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR, var4.func_177958_n() + var8, var4.func_177952_p() + var9);
         BlockPos var11 = new BlockPos(var4.func_177958_n() + var8, var10, var4.func_177952_p() + var9);
         if (var1.func_180495_p(var11).func_177230_c() == Blocks.field_150355_j) {
            boolean var12 = var3.nextDouble() < var5.field_203238_b;
            IBlockState var13 = var12 ? Blocks.field_203199_aR.func_176223_P() : Blocks.field_203198_aQ.func_176223_P();
            if (var13.func_196955_c(var1, var11)) {
               if (var12) {
                  IBlockState var14 = (IBlockState)var13.func_206870_a(BlockSeaGrassTall.field_208065_c, DoubleBlockHalf.UPPER);
                  BlockPos var15 = var11.func_177984_a();
                  if (var1.func_180495_p(var15).func_177230_c() == Blocks.field_150355_j) {
                     var1.func_180501_a(var11, var13, 2);
                     var1.func_180501_a(var15, var14, 2);
                  }
               } else {
                  var1.func_180501_a(var11, var13, 2);
               }

               ++var6;
            }
         }
      }

      return var6 > 0;
   }
}
