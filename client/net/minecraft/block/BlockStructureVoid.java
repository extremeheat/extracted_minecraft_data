package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockStructureVoid extends Block {
   private static final VoxelShape field_196525_a = Block.func_208617_a(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

   protected BlockStructureVoid(Block.Properties var1) {
      super(var1);
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196525_a;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public float func_185485_f(IBlockState var1) {
      return 1.0F;
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
   }

   public EnumPushReaction func_149656_h(IBlockState var1) {
      return EnumPushReaction.DESTROY;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
