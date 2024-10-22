package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public interface ScheduledTickAccess {
   <T> ScheduledTick<T> createTick(BlockPos var1, T var2, int var3, TickPriority var4);

   <T> ScheduledTick<T> createTick(BlockPos var1, T var2, int var3);

   LevelTickAccess<Block> getBlockTicks();

   default void scheduleTick(BlockPos var1, Block var2, int var3, TickPriority var4) {
      this.getBlockTicks().schedule(this.createTick(var1, var2, var3, var4));
   }

   default void scheduleTick(BlockPos var1, Block var2, int var3) {
      this.getBlockTicks().schedule(this.createTick(var1, var2, var3));
   }

   LevelTickAccess<Fluid> getFluidTicks();

   default void scheduleTick(BlockPos var1, Fluid var2, int var3, TickPriority var4) {
      this.getFluidTicks().schedule(this.createTick(var1, var2, var3, var4));
   }

   default void scheduleTick(BlockPos var1, Fluid var2, int var3) {
      this.getFluidTicks().schedule(this.createTick(var1, var2, var3));
   }
}
