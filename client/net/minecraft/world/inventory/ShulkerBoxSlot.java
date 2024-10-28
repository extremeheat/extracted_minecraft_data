package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class ShulkerBoxSlot extends Slot {
   public ShulkerBoxSlot(Container var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public boolean mayPlace(ItemStack var1) {
      return var1.getItem().canFitInsideContainerItems();
   }
}
