package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;

public class BlackholeTickAccess {
   private static final TickContainerAccess<Object> CONTAINER_BLACKHOLE = new TickContainerAccess<Object>() {
      public void schedule(ScheduledTick<Object> var1) {
      }

      public boolean hasScheduledTick(BlockPos var1, Object var2) {
         return false;
      }

      public int count() {
         return 0;
      }
   };
   private static final LevelTickAccess<Object> LEVEL_BLACKHOLE = new LevelTickAccess<Object>() {
      public void schedule(ScheduledTick<Object> var1) {
      }

      public boolean hasScheduledTick(BlockPos var1, Object var2) {
         return false;
      }

      public boolean willTickThisTick(BlockPos var1, Object var2) {
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
