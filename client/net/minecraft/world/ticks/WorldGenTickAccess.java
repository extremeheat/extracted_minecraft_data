package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.core.BlockPos;

public class WorldGenTickAccess<T> implements LevelTickAccess<T> {
   private final Function<BlockPos, TickContainerAccess<T>> containerGetter;

   public WorldGenTickAccess(Function<BlockPos, TickContainerAccess<T>> var1) {
      super();
      this.containerGetter = var1;
   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return ((TickContainerAccess)this.containerGetter.apply(var1)).hasScheduledTick(var1, var2);
   }

   public void schedule(ScheduledTick<T> var1) {
      ((TickContainerAccess)this.containerGetter.apply(var1.pos())).schedule(var1);
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      return false;
   }

   public int count() {
      return 0;
   }
}
