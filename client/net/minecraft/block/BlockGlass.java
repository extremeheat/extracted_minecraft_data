package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockGlass extends BlockBreakable {
   public BlockGlass(Block.Properties var1) {
      super(var1);
   }

   public boolean func_200123_i(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return true;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   protected boolean func_149700_E() {
      return true;
   }
}
