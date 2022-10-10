package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IBucketPickupHandler {
   Fluid func_204508_a(IWorld var1, BlockPos var2, IBlockState var3);
}
