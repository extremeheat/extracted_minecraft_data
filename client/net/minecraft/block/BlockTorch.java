package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockTorch extends Block {
   protected static final VoxelShape field_196526_y = Block.func_208617_a(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);

   protected BlockTorch(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196526_y;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2 == EnumFacing.DOWN && !this.func_196260_a(var1, var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
      Block var5 = var4.func_177230_c();
      boolean var6 = var5 instanceof BlockFence || var5 instanceof BlockStainedGlass || var5 == Blocks.field_150359_w || var5 == Blocks.field_150463_bK || var5 == Blocks.field_196723_eg || var4.func_185896_q();
      return var6 && var5 != Blocks.field_185775_db;
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      double var5 = (double)var3.func_177958_n() + 0.5D;
      double var7 = (double)var3.func_177956_o() + 0.7D;
      double var9 = (double)var3.func_177952_p() + 0.5D;
      var2.func_195594_a(Particles.field_197601_L, var5, var7, var9, 0.0D, 0.0D, 0.0D);
      var2.func_195594_a(Particles.field_197631_x, var5, var7, var9, 0.0D, 0.0D, 0.0D);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
