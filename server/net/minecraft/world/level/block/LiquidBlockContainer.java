package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public interface LiquidBlockContainer {
   boolean canPlaceLiquid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4);

   boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4);
}
