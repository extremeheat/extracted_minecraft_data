package net.minecraft.server.level;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickPriority;

public class WorldGenTickList implements TickList {
   private final Function index;

   public WorldGenTickList(Function var1) {
      this.index = var1;
   }

   public boolean hasScheduledTick(BlockPos var1, Object var2) {
      return ((TickList)this.index.apply(var1)).hasScheduledTick(var1, var2);
   }

   public void scheduleTick(BlockPos var1, Object var2, int var3, TickPriority var4) {
      ((TickList)this.index.apply(var1)).scheduleTick(var1, var2, var3, var4);
   }

   public boolean willTickThisTick(BlockPos var1, Object var2) {
      return false;
   }

   public void addAll(Stream var1) {
      var1.forEach((var1x) -> {
         ((TickList)this.index.apply(var1x.pos)).addAll(Stream.of(var1x));
      });
   }
}
