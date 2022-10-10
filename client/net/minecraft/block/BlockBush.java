package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockBush extends Block {
   protected BlockBush(Block.Properties var1) {
      super(var1);
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      Block var4 = var1.func_177230_c();
      return var4 == Blocks.field_196658_i || var4 == Blocks.field_150346_d || var4 == Blocks.field_196660_k || var4 == Blocks.field_196661_l || var4 == Blocks.field_150458_ak;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      BlockPos var4 = var3.func_177977_b();
      return this.func_200014_a_(var2.func_180495_p(var4), var2, var4);
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return 0;
   }
}
