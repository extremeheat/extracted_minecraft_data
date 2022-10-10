package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class BlockBreakable extends Block {
   protected BlockBreakable(Block.Properties var1) {
      super(var1);
   }

   public boolean func_200122_a(IBlockState var1, IBlockState var2, EnumFacing var3) {
      return var2.func_177230_c() == this ? true : super.func_200122_a(var1, var2, var3);
   }
}
