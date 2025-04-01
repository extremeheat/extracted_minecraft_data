package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class MineCraftingHintSlot extends Slot {
   final MineCraftingMenu menu;

   public MineCraftingHintSlot(Container var1, int var2, int var3, int var4, MineCraftingMenu var5) {
      super(var1, var2, var3, var4);
      this.menu = var5;
   }

   public boolean mayPickup(Player var1) {
      return false;
   }

   public boolean mayPlace(ItemStack var1) {
      return false;
   }

   public boolean isActive() {
      return super.isActive() && !this.getItem().isEmpty() && (!this.menu.isBossMine() || this.menu.isMineCompleted());
   }
}
