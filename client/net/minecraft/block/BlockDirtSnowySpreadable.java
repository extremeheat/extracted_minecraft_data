package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockDirtSnowySpreadable extends BlockDirtSnowy {
   protected BlockDirtSnowySpreadable(Block.Properties var1) {
      super(var1);
   }

   private static boolean func_196383_a(IWorldReaderBase var0, BlockPos var1) {
      BlockPos var2 = var1.func_177984_a();
      return var0.func_201696_r(var2) >= 4 || var0.func_180495_p(var2).func_200016_a(var0, var2) < var0.func_201572_C();
   }

   private static boolean func_196384_b(IWorldReaderBase var0, BlockPos var1) {
      BlockPos var2 = var1.func_177984_a();
      return var0.func_201696_r(var2) >= 4 && var0.func_180495_p(var2).func_200016_a(var0, var2) < var0.func_201572_C() && !var0.func_204610_c(var2).func_206884_a(FluidTags.field_206959_a);
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K) {
         if (!func_196383_a(var2, var3)) {
            var2.func_175656_a(var3, Blocks.field_150346_d.func_176223_P());
         } else {
            if (var2.func_201696_r(var3.func_177984_a()) >= 9) {
               for(int var5 = 0; var5 < 4; ++var5) {
                  BlockPos var6 = var3.func_177982_a(var4.nextInt(3) - 1, var4.nextInt(5) - 3, var4.nextInt(3) - 1);
                  if (!var2.func_195588_v(var6)) {
                     return;
                  }

                  if (var2.func_180495_p(var6).func_177230_c() == Blocks.field_150346_d && func_196384_b(var2, var6)) {
                     var2.func_175656_a(var6, this.func_176223_P());
                  }
               }
            }

         }
      }
   }
}
