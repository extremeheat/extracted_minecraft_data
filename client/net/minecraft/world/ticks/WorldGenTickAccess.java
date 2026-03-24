package net.minecraft.world.ticks;

import java.util.function.Function;
import net.minecraft.core.BlockPos;

public class WorldGenTickAccess<T> implements LevelTickAccess<T> {
   private final Function<BlockPos, TickContainerAccess<T>> containerGetter;

   public WorldGenTickAccess(final Function<BlockPos, TickContainerAccess<T>> containerGetter) {
      super();
      this.containerGetter = containerGetter;
   }

   public boolean hasScheduledTick(final BlockPos pos, final T type) {
      return ((TickContainerAccess)this.containerGetter.apply(pos)).hasScheduledTick(pos, type);
   }

   public void schedule(final ScheduledTick<T> tick) {
      ((TickContainerAccess)this.containerGetter.apply(tick.pos())).schedule(tick);
   }

   public boolean willTickThisTick(final BlockPos pos, final T type) {
      return false;
   }

   public int count() {
      return 0;
   }
}
