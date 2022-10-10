package net.minecraft.dispenser;

import net.minecraft.item.ItemStack;

public interface IBehaviorDispenseItem {
   IBehaviorDispenseItem NOOP = (var0, var1) -> {
      return var1;
   };

   ItemStack dispense(IBlockSource var1, ItemStack var2);
}
