package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class CrafterSlot extends Slot {
   private final CrafterMenu menu;

   public CrafterSlot(Container var1, int var2, int var3, int var4, CrafterMenu var5) {
      super(var1, var2, var3, var4);
      this.menu = var5;
   }

   public boolean mayPlace(ItemStack var1) {
      return !this.menu.isSlotDisabled(this.index) && super.mayPlace(var1);
   }

   public void setChanged() {
      super.setChanged();
      this.menu.slotsChanged(this.container);
   }
}
