package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public interface ILiquidContainer {
   boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4);

   boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4);
}
