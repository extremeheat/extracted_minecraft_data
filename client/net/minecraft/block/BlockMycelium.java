package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMycelium extends BlockDirtSnowySpreadable {
   public BlockMycelium(Block.Properties var1) {
      super(var1);
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      super.func_180655_c(var1, var2, var3, var4);
      if (var4.nextInt(10) == 0) {
         var2.func_195594_a(Particles.field_197596_G, (double)((float)var3.func_177958_n() + var4.nextFloat()), (double)((float)var3.func_177956_o() + 1.1F), (double)((float)var3.func_177952_p() + var4.nextFloat()), 0.0D, 0.0D, 0.0D);
      }

   }
}
