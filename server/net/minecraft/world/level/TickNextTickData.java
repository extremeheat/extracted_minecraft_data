package net.minecraft.world.level;

import java.util.Comparator;
import net.minecraft.core.BlockPos;

public class TickNextTickData<T> {
   private static long counter;
   private final T type;
   public final BlockPos pos;
   public final long triggerTick;
   public final TickPriority priority;
   private final long c;

   public TickNextTickData(BlockPos var1, T var2) {
      this(var1, var2, 0L, TickPriority.NORMAL);
   }

   public TickNextTickData(BlockPos var1, T var2, long var3, TickPriority var5) {
      super();
      this.c = (long)(counter++);
      this.pos = var1.immutable();
      this.type = var2;
      this.triggerTick = var3;
      this.priority = var5;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof TickNextTickData)) {
         return false;
      } else {
         TickNextTickData var2 = (TickNextTickData)var1;
         return this.pos.equals(var2.pos) && this.type == var2.type;
      }
   }

   public int hashCode() {
      return this.pos.hashCode();
   }

   public static <T> Comparator<TickNextTickData<T>> createTimeComparator() {
      return Comparator.comparingLong((var0) -> {
         return var0.triggerTick;
      }).thenComparing((var0) -> {
         return var0.priority;
      }).thenComparingLong((var0) -> {
         return var0.c;
      });
   }

   public String toString() {
      return this.type + ": " + this.pos + ", " + this.triggerTick + ", " + this.priority + ", " + this.c;
   }

   public T getType() {
      return this.type;
   }
}
