package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

class ContainerEnchantment$2 extends Slot {
   ContainerEnchantment$2(ContainerEnchantment var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.field_75227_a = var1;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      return true;
   }
}
