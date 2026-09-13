package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IInventory {
   int func_70302_i_();

   ItemStack func_70301_a(int var1);

   ItemStack func_70298_a(int var1, int var2);

   ItemStack func_70304_b(int var1);

   void func_70299_a(int var1, ItemStack var2);

   String func_145825_b();

   boolean func_145818_k_();

   int func_70297_j_();

   void func_70296_d();

   boolean func_70300_a(EntityPlayer var1);

   void func_70295_k_();

   void func_70305_f();

   boolean func_94041_b(int var1, ItemStack var2);
}
