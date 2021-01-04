package net.minecraft.world.level;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class EmptyTickList<T> implements TickList<T> {
   private static final EmptyTickList<Object> INSTANCE = new EmptyTickList();

   public EmptyTickList() {
      super();
   }

   public static <T> EmptyTickList<T> empty() {
      return INSTANCE;
   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return false;
   }

   public void scheduleTick(BlockPos var1, T var2, int var3) {
   }

   public void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4) {
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      return false;
   }

   public void addAll(Stream<TickNextTickData<T>> var1) {
   }
}
