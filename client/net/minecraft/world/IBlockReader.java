package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public interface IBlockReader {
   @Nullable
   TileEntity func_175625_s(BlockPos var1);

   IBlockState func_180495_p(BlockPos var1);

   IFluidState func_204610_c(BlockPos var1);

   default int func_201572_C() {
      return 15;
   }
}
