package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;

public class BlackholeTickAccess {
   private static final TickContainerAccess<Object> CONTAINER_BLACKHOLE = new TickContainerAccess<Object>() {
      public void schedule(final ScheduledTick<Object> tick) {
      }

      public boolean hasScheduledTick(final BlockPos pos, final Object type) {
         return false;
      }

      public int count() {
         return 0;
      }
   };
   private static final LevelTickAccess<Object> LEVEL_BLACKHOLE = new LevelTickAccess<Object>() {
      public void schedule(final ScheduledTick<Object> tick) {
      }

      public boolean hasScheduledTick(final BlockPos pos, final Object type) {
         return false;
      }

      public boolean willTickThisTick(final BlockPos pos, final Object type) {
         return false;
      }

      public int count() {
         return 0;
      }
   };

   public BlackholeTickAccess() {
      super();
   }

   public static <T> TickContainerAccess<T> emptyContainer() {
      return CONTAINER_BLACKHOLE;
   }

   public static <T> LevelTickAccess<T> emptyLevelList() {
      return LEVEL_BLACKHOLE;
   }
}
