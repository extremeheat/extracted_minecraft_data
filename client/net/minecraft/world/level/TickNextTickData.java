package net.minecraft.world.level;

import java.util.Comparator;
import net.minecraft.core.BlockPos;

public class TickNextTickData<T> {
   private static long counter;
   private final T type;
   public final BlockPos pos;
   public final long delay;
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
      this.delay = var3;
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
      return (var0, var1) -> {
         int var2 = Long.compare(var0.delay, var1.delay);
         if (var2 != 0) {
            return var2;
         } else {
            var2 = var0.priority.compareTo(var1.priority);
            return var2 != 0 ? var2 : Long.compare(var0.c, var1.c);
         }
      };
   }

   public String toString() {
      return this.type + ": " + this.pos + ", " + this.delay + ", " + this.priority + ", " + this.c;
   }

   public T getType() {
      return this.type;
   }
}
