package net.minecraft.world.level;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public interface TickList<T> {
   boolean hasScheduledTick(BlockPos var1, T var2);

   default void scheduleTick(BlockPos var1, T var2, int var3) {
      this.scheduleTick(var1, var2, var3, TickPriority.NORMAL);
   }

   void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4);

   boolean willTickThisTick(BlockPos var1, T var2);

   void addAll(Stream<TickNextTickData<T>> var1);
}
