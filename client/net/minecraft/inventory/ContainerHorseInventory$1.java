package net.minecraft.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

class ContainerHorseInventory$1 extends Slot {
   ContainerHorseInventory$1(ContainerHorseInventory var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.field_111239_a = var1;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      return super.func_75214_a(var1) && var1.func_77973_b() == Items.field_151141_av && !this.func_75216_d();
   }
}
