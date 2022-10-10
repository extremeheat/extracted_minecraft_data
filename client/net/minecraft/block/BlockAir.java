package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockAir extends Block {
   protected BlockAir(Block.Properties var1) {
      super(var1);
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return VoxelShapes.func_197880_a();
   }

   public boolean func_200293_a(IBlockState var1) {
      return false;
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
   }

   public boolean func_196261_e(IBlockState var1) {
      return true;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
