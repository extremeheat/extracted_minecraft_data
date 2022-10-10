package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.CompositeFlowerFeature;

public class BlockGrass extends BlockDirtSnowySpreadable implements IGrowable {
   public BlockGrass(Block.Properties var1) {
      super(var1);
   }

   public boolean func_176473_a(IBlockReader var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var1.func_180495_p(var2.func_177984_a()).func_196958_f();
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      BlockPos var5 = var3.func_177984_a();
      IBlockState var6 = Blocks.field_150349_c.func_176223_P();

      label48:
      for(int var7 = 0; var7 < 128; ++var7) {
         BlockPos var8 = var5;

         for(int var9 = 0; var9 < var7 / 16; ++var9) {
            var8 = var8.func_177982_a(var2.nextInt(3) - 1, (var2.nextInt(3) - 1) * var2.nextInt(3) / 2, var2.nextInt(3) - 1);
            if (var1.func_180495_p(var8.func_177977_b()).func_177230_c() != this || var1.func_180495_p(var8).func_185898_k()) {
               continue label48;
            }
         }

         IBlockState var12 = var1.func_180495_p(var8);
         if (var12.func_177230_c() == var6.func_177230_c() && var2.nextInt(10) == 0) {
            ((IGrowable)var6.func_177230_c()).func_176474_b(var1, var2, var8, var12);
         }

         if (var12.func_196958_f()) {
            IBlockState var10;
            if (var2.nextInt(8) == 0) {
               List var11 = var1.func_180494_b(var8).func_201853_g();
               if (var11.isEmpty()) {
                  continue;
               }

               var10 = ((CompositeFlowerFeature)var11.get(0)).func_202354_a(var2, var8);
            } else {
               var10 = var6;
            }

            if (var10.func_196955_c(var1, var8)) {
               var1.func_180501_a(var8, var10, 3);
            }
         }
      }

   }

   public boolean func_200124_e(IBlockState var1) {
      return true;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }
}
