package net.minecraft.world.item.slot;

import net.minecraft.world.item.ItemStack;

public class TrackingSlotSelector implements SlotSelector {
   private int selectedCount;

   public TrackingSlotSelector() {
      super();
   }

   public boolean trySelectSlot(final ItemStack itemInSlot) {
      ++this.selectedCount;
      return true;
   }

   public int selectedCount() {
      return this.selectedCount;
   }
}
