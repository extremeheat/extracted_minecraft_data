package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.ItemStack;

class ContainerHorseInventory$2 extends Slot {
   ContainerHorseInventory$2(ContainerHorseInventory var1, IInventory var2, int var3, int var4, int var5, EntityHorse var6) {
      super(var2, var3, var4, var5);
      this.field_111240_b = var1;
      this.field_111241_a = var6;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      return super.func_75214_a(var1) && this.field_111241_a.func_110259_cr() && EntityHorse.func_146085_a(var1.func_77973_b());
   }

   @Override
   public boolean func_111238_b() {
      return this.field_111241_a.func_110259_cr();
   }
}
