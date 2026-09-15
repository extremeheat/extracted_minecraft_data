package net.minecraft.world.item.component;

import java.util.List;
import net.minecraft.world.item.ItemProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.slot.SlotSelector;

public abstract class GrowableMutableContainer<T> extends SimpleMutableContainer<T> {
   public GrowableMutableContainer(final List<ItemStack> items) {
      super(items);
   }

   public abstract boolean canInsertNewSlots();

   public int replaceSlotItems(final ItemProvider newItems, final SlotSelector slotSelector) {
      int successCount;
      for(successCount = super.replaceSlotItems(newItems, slotSelector); newItems.hasNext() && this.canInsertNewSlots() && slotSelector.trySelectSlot(ItemStack.EMPTY) && this.addSlotWithItem(newItems); ++successCount) {
      }

      return successCount;
   }

   protected boolean addSlotWithItem(final ItemProvider newItems) {
      this.items.add(newItems.next());
      return true;
   }
}
