package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class BlockEndPortal extends BlockContainer {
   protected static final VoxelShape field_196323_a = Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   protected BlockEndPortal(Block.Properties var1) {
      super(var1);
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityEndPortal();
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196323_a;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      if (!var2.field_72995_K && !var4.func_184218_aH() && !var4.func_184207_aI() && var4.func_184222_aU() && VoxelShapes.func_197879_c(VoxelShapes.func_197881_a(var4.func_174813_aQ().func_72317_d((double)(-var3.func_177958_n()), (double)(-var3.func_177956_o()), (double)(-var3.func_177952_p()))), var1.func_196954_c(var2, var3), IBooleanFunction.AND)) {
         var4.func_212321_a(DimensionType.THE_END);
      }

   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      double var5 = (double)((float)var3.func_177958_n() + var4.nextFloat());
      double var7 = (double)((float)var3.func_177956_o() + 0.8F);
      double var9 = (double)((float)var3.func_177952_p() + var4.nextFloat());
      double var11 = 0.0D;
      double var13 = 0.0D;
      double var15 = 0.0D;
      var2.func_195594_a(Particles.field_197601_L, var5, var7, var9, 0.0D, 0.0D, 0.0D);
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return ItemStack.field_190927_a;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
