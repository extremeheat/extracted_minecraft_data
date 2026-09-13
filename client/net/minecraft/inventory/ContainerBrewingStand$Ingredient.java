package net.minecraft.inventory;

import net.minecraft.item.ItemStack;

class ContainerBrewingStand$Ingredient extends Slot {
   public ContainerBrewingStand$Ingredient(ContainerBrewingStand var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.field_75226_a = var1;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      return var1 != null ? var1.func_77973_b().func_150892_m(var1) : false;
   }

   @Override
   public int func_75219_a() {
      return 64;
   }
}
