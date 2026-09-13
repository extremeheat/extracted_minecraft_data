package net.minecraft.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

class ContainerBeacon$BeaconSlot extends Slot {
   public ContainerBeacon$BeaconSlot(ContainerBeacon var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.field_82876_a = var1;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.func_77973_b() == Items.field_151166_bC
            || var1.func_77973_b() == Items.field_151045_i
            || var1.func_77973_b() == Items.field_151043_k
            || var1.func_77973_b() == Items.field_151042_j;
      }
   }

   @Override
   public int func_75219_a() {
      return 1;
   }
}
