package net.minecraft.dispenser;

import net.minecraft.item.ItemStack;

public interface IBehaviorDispenseItem {
   IBehaviorDispenseItem field_82483_a = new IBehaviorDispenseItem() {
      public ItemStack func_82482_a(IBlockSource var1, ItemStack var2) {
         return var2;
      }
   };

   ItemStack func_82482_a(IBlockSource var1, ItemStack var2);
}
