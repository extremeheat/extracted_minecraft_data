package net.minecraft.world.item.slot;

import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;

public interface SlotSelector {
   SlotSelector ANY_SLOT = (var0) -> true;

   boolean trySelectSlot(ItemStack itemInSlot);

   default SlotSelector filter(final Predicate<? super ItemStack> predicate) {
      return new FilteringSelector(this, predicate);
   }

   default SlotSelector limit(final int limit) {
      return new LimitingSelector(this, limit);
   }

   public static record FilteringSelector(SlotSelector slotSelector, Predicate<? super ItemStack> filter) implements SlotSelector {
      public FilteringSelector {
         super();
      }

      public boolean trySelectSlot(final ItemStack itemInSlot) {
         return this.filter.test(itemInSlot) && this.slotSelector.trySelectSlot(itemInSlot);
      }
   }

   public static class LimitingSelector implements SlotSelector {
      private final SlotSelector slotSelector;
      private final int limit;
      private int encountered;

      public LimitingSelector(final SlotSelector slotSelector, final int limit) {
         super();
         this.slotSelector = slotSelector;
         this.limit = limit;
      }

      public boolean trySelectSlot(final ItemStack itemInSlot) {
         if (this.encountered < this.limit) {
            ++this.encountered;
            return this.slotSelector.trySelectSlot(itemInSlot);
         } else {
            return false;
         }
      }
   }
}
