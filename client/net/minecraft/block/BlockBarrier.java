package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBarrier extends Block {
   protected BlockBarrier(Block.Properties var1) {
      super(var1);
   }

   public boolean func_200123_i(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return true;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public boolean func_200124_e(IBlockState var1) {
      return false;
   }

   public float func_185485_f(IBlockState var1) {
      return 1.0F;
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
   }
}
