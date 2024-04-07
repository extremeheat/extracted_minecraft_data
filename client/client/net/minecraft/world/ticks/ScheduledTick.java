package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public record ScheduledTick<T>(T type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
   public static final Comparator<ScheduledTick<?>> DRAIN_ORDER = (var0, var1) -> {
      int var2 = Long.compare(var0.triggerTick, var1.triggerTick);
      if (var2 != 0) {
         return var2;
      } else {
         var2 = var0.priority.compareTo(var1.priority);
         return var2 != 0 ? var2 : Long.compare(var0.subTickOrder, var1.subTickOrder);
      }
   };
   public static final Comparator<ScheduledTick<?>> INTRA_TICK_DRAIN_ORDER = (var0, var1) -> {
      int var2 = var0.priority.compareTo(var1.priority);
      return var2 != 0 ? var2 : Long.compare(var0.subTickOrder, var1.subTickOrder);
   };
   public static final Strategy<ScheduledTick<?>> UNIQUE_TICK_HASH = new Strategy<ScheduledTick<?>>() {
      public int hashCode(ScheduledTick<?> var1) {
         return 31 * var1.pos().hashCode() + var1.type().hashCode();
      }

      public boolean equals(@Nullable ScheduledTick<?> var1, @Nullable ScheduledTick<?> var2) {
         if (var1 == var2) {
            return true;
         } else {
            return var1 != null && var2 != null ? var1.type() == var2.type() && var1.pos().equals(var2.pos()) : false;
         }
      }
   };

   public ScheduledTick(T var1, BlockPos var2, long var3, long var5) {
      this((T)var1, var2, var3, TickPriority.NORMAL, var5);
   }

   public ScheduledTick(T type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
      super();
      pos = pos.immutable();
      this.type = (T)type;
      this.pos = pos;
      this.triggerTick = triggerTick;
      this.priority = priority;
      this.subTickOrder = subTickOrder;
   }

   public static <T> ScheduledTick<T> probe(T var0, BlockPos var1) {
      return new ScheduledTick<>((T)var0, var1, 0L, TickPriority.NORMAL, 0L);
   }
}
