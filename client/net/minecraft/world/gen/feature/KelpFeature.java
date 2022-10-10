package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.BlockKelpTop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class KelpFeature extends Feature<NoFeatureConfig> {
   public KelpFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      int var6 = 0;
      int var7 = var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR, var4.func_177958_n(), var4.func_177952_p());
      BlockPos var8 = new BlockPos(var4.func_177958_n(), var7, var4.func_177952_p());
      if (var1.func_180495_p(var8).func_177230_c() == Blocks.field_150355_j) {
         IBlockState var9 = Blocks.field_203214_jx.func_176223_P();
         IBlockState var10 = Blocks.field_203215_jy.func_176223_P();
         int var11 = 1 + var3.nextInt(10);

         for(int var12 = 0; var12 <= var11; ++var12) {
            if (var1.func_180495_p(var8).func_177230_c() == Blocks.field_150355_j && var1.func_180495_p(var8.func_177984_a()).func_177230_c() == Blocks.field_150355_j && var10.func_196955_c(var1, var8)) {
               if (var12 == var11) {
                  var1.func_180501_a(var8, (IBlockState)var9.func_206870_a(BlockKelpTop.field_203163_a, var3.nextInt(23)), 2);
                  ++var6;
               } else {
                  var1.func_180501_a(var8, var10, 2);
               }
            } else if (var12 > 0) {
               BlockPos var13 = var8.func_177977_b();
               if (var9.func_196955_c(var1, var13) && var1.func_180495_p(var13.func_177977_b()).func_177230_c() != Blocks.field_203214_jx) {
                  var1.func_180501_a(var13, (IBlockState)var9.func_206870_a(BlockKelpTop.field_203163_a, var3.nextInt(23)), 2);
                  ++var6;
               }
               break;
            }

            var8 = var8.func_177984_a();
         }
      }

      return var6 > 0;
   }
}
