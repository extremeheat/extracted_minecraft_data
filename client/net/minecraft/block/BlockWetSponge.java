package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockWetSponge extends Block {
   protected BlockWetSponge(Block.Properties var1) {
      super(var1);
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      EnumFacing var5 = EnumFacing.func_176741_a(var4);
      if (var5 != EnumFacing.UP && !var2.func_180495_p(var3.func_177972_a(var5)).func_185896_q()) {
         double var6 = (double)var3.func_177958_n();
         double var8 = (double)var3.func_177956_o();
         double var10 = (double)var3.func_177952_p();
         if (var5 == EnumFacing.DOWN) {
            var8 -= 0.05D;
            var6 += var4.nextDouble();
            var10 += var4.nextDouble();
         } else {
            var8 += var4.nextDouble() * 0.8D;
            if (var5.func_176740_k() == EnumFacing.Axis.X) {
               var10 += var4.nextDouble();
               if (var5 == EnumFacing.EAST) {
                  ++var6;
               } else {
                  var6 += 0.05D;
               }
            } else {
               var6 += var4.nextDouble();
               if (var5 == EnumFacing.SOUTH) {
                  ++var10;
               } else {
                  var10 += 0.05D;
               }
            }
         }

         var2.func_195594_a(Particles.field_197618_k, var6, var8, var10, 0.0D, 0.0D, 0.0D);
      }
   }
}
