package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash.Strategy;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public record ScheduledTick<T>(T d, BlockPos e, long f, TickPriority g, long h) {
   private final T type;
   private final BlockPos pos;
   private final long triggerTick;
   private final TickPriority priority;
   private final long subTickOrder;
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
         } else if (var1 != null && var2 != null) {
            return var1.type() == var2.type() && var1.pos().equals(var2.pos());
         } else {
            return false;
         }
      }

      // $FF: synthetic method
      public boolean equals(@Nullable Object var1, @Nullable Object var2) {
         return this.equals((ScheduledTick)var1, (ScheduledTick)var2);
      }

      // $FF: synthetic method
      public int hashCode(Object var1) {
         return this.hashCode((ScheduledTick)var1);
      }
   };

   public ScheduledTick(T var1, BlockPos var2, long var3, long var5) {
      this(var1, var2, var3, TickPriority.NORMAL, var5);
   }

   public ScheduledTick(T var1, BlockPos var2, long var3, TickPriority var5, long var6) {
      super();
      var2 = var2.immutable();
      this.type = var1;
      this.pos = var2;
      this.triggerTick = var3;
      this.priority = var5;
      this.subTickOrder = var6;
   }

   public static <T> ScheduledTick<T> probe(T var0, BlockPos var1) {
      return new ScheduledTick(var0, var1, 0L, TickPriority.NORMAL, 0L);
   }

   public T type() {
      return this.type;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public long triggerTick() {
      return this.triggerTick;
   }

   public TickPriority priority() {
      return this.priority;
   }

   public long subTickOrder() {
      return this.subTickOrder;
   }
}
