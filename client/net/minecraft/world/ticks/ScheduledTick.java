package net.minecraft.world.ticks;

import it.unimi.dsi.fastutil.Hash;
import java.util.Comparator;
import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public record ScheduledTick<T>(T type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
   public static final Comparator<ScheduledTick<?>> DRAIN_ORDER = (o1, o2) -> {
      int compare = Long.compare(o1.triggerTick, o2.triggerTick);
      if (compare != 0) {
         return compare;
      } else {
         compare = o1.priority.compareTo(o2.priority);
         return compare != 0 ? compare : Long.compare(o1.subTickOrder, o2.subTickOrder);
      }
   };
   public static final Comparator<ScheduledTick<?>> INTRA_TICK_DRAIN_ORDER = (o1, o2) -> {
      int compare = o1.priority.compareTo(o2.priority);
      return compare != 0 ? compare : Long.compare(o1.subTickOrder, o2.subTickOrder);
   };
   public static final Hash.Strategy<ScheduledTick<?>> UNIQUE_TICK_HASH = new Hash.Strategy<ScheduledTick<?>>() {
      public int hashCode(final ScheduledTick<?> o) {
         return 31 * o.pos().hashCode() + o.type().hashCode();
      }

      public boolean equals(final @Nullable ScheduledTick<?> a, final @Nullable ScheduledTick<?> b) {
         if (a == b) {
            return true;
         } else if (a != null && b != null) {
            return a.type() == b.type() && a.pos().equals(b.pos());
         } else {
            return false;
         }
      }
   };

   public ScheduledTick(final T type, final BlockPos pos, final long triggerTick, final long subTickOrder) {
      this(type, pos, triggerTick, TickPriority.NORMAL, subTickOrder);
   }

   public ScheduledTick(T type, BlockPos pos, long triggerTick, TickPriority priority, long subTickOrder) {
      super();
      pos = pos.immutable();
      this.type = type;
      this.pos = pos;
      this.triggerTick = triggerTick;
      this.priority = priority;
      this.subTickOrder = subTickOrder;
   }

   public static <T> ScheduledTick<T> probe(final T type, final BlockPos pos) {
      return new ScheduledTick<T>(type, pos, 0L, TickPriority.NORMAL, 0L);
   }

   public SavedTick<T> toSavedTick(final long currentTick) {
      return new SavedTick<T>(this.type, this.pos, (int)(this.triggerTick - currentTick), this.priority);
   }
}
