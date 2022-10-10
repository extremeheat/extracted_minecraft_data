package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class BlockCoralFan extends BlockCoralPlantBase {
   private static final VoxelShape field_211883_b = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

   protected BlockCoralFan(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_211883_b;
   }
}
