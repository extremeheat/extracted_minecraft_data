package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockRedstone extends Block {
   public BlockRedstone(Block.Properties var1) {
      super(var1);
   }

   public boolean func_149744_f(IBlockState var1) {
      return true;
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return 15;
   }
}
