package net.minecraft.world.item.slot;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

public sealed interface SlotSelector {
   SlotSelector ANY_SLOT = new FilteringSelector((Predicate)null);
   SlotSelector EMPTY_SLOTS = selecting(ItemStack::isEmpty);
   SlotSelector NON_EMPTY_SLOTS = selecting((s) -> !s.isEmpty());

   boolean trySelectSlot(ItemStack itemInSlot);

   SlotSelector filter(Predicate<? super ItemStack> predicate);

   SlotSelector limit(int limit);

   static SlotSelector selecting(final Predicate<? super ItemStack> predicate) {
      return new FilteringSelector(predicate);
   }

   static SlotSelector tracking(final SlotSelector slotSelector, final MutableInt selectedCount) {
      return new TrackingSelector(slotSelector, 2147483647, selectedCount);
   }

   static SlotSelector tracking(final MutableInt selectedCount) {
      return tracking(ANY_SLOT, selectedCount);
   }

   public static record FilteringSelector(@Nullable Predicate<? super ItemStack> filter) implements SlotSelector {
      public FilteringSelector {
         super();
      }

      public boolean trySelectSlot(final ItemStack itemInSlot) {
         return this.filter == null || this.filter.test(itemInSlot);
      }

      public SlotSelector filter(final Predicate<? super ItemStack> predicate) {
         return this.filter == null ? new FilteringSelector(predicate) : new FilteringSelector((t) -> this.filter.test(t) && predicate.test(t));
      }

      public SlotSelector limit(final int limit) {
         return new TrackingSelector(this, limit, new MutableInt());
      }
   }

   public static record TrackingSelector(SlotSelector slotSelector, int limit, MutableInt selectedCount) implements SlotSelector {
      public TrackingSelector {
         super();
      }

      public boolean trySelectSlot(final ItemStack itemInSlot) {
         if (this.selectedCount.intValue() < this.limit && this.slotSelector.trySelectSlot(itemInSlot)) {
            this.selectedCount.increment();
            return true;
         } else {
            return false;
         }
      }

      public SlotSelector filter(final Predicate<? super ItemStack> predicate) {
         return new TrackingSelector(this.slotSelector.filter(predicate), this.limit, this.selectedCount);
      }

      public SlotSelector limit(final int limit) {
         return limit < this.limit ? new TrackingSelector(this.slotSelector, limit, this.selectedCount) : this;
      }
   }
}
