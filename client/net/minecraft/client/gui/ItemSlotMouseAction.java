package net.minecraft.client.gui;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public interface ItemSlotMouseAction {
   boolean matches(Slot var1);

   boolean onMouseScrolled(double var1, double var3, int var5, ItemStack var6);

   void onStopHovering(Slot var1);

   boolean onKeyPressed(ItemStack var1, int var2, int var3, int var4);
}
