package net.minecraft.server.level;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;

public class WorldGenTickList<T> implements TickList<T> {
   private final Function<BlockPos, TickList<T>> index;

   public WorldGenTickList(Function<BlockPos, TickList<T>> var1) {
      super();
      this.index = var1;
   }

   public boolean hasScheduledTick(BlockPos var1, T var2) {
      return ((TickList)this.index.apply(var1)).hasScheduledTick(var1, var2);
   }

   public void scheduleTick(BlockPos var1, T var2, int var3, TickPriority var4) {
      ((TickList)this.index.apply(var1)).scheduleTick(var1, var2, var3, var4);
   }

   public boolean willTickThisTick(BlockPos var1, T var2) {
      return false;
   }
}
