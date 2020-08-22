package net.minecraft.world.level;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;

public class EmptyTickList implements TickList {
   private static final EmptyTickList INSTANCE = new EmptyTickList();

   public static EmptyTickList empty() {
      return INSTANCE;
   }

   public boolean hasScheduledTick(BlockPos var1, Object var2) {
      return false;
   }

   public void scheduleTick(BlockPos var1, Object var2, int var3) {
   }

   public void scheduleTick(BlockPos var1, Object var2, int var3, TickPriority var4) {
   }

   public boolean willTickThisTick(BlockPos var1, Object var2) {
      return false;
   }

   public void addAll(Stream var1) {
   }
}
