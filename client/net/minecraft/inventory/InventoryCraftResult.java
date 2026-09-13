package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryCraftResult implements IInventory {
   private ItemStack[] field_70467_a = new ItemStack[1];

   public InventoryCraftResult() {
      super();
   }

   @Override
   public int func_70302_i_() {
      return 1;
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return this.field_70467_a[0];
   }

   @Override
   public String func_145825_b() {
      return "Result";
   }

   @Override
   public boolean func_145818_k_() {
      return false;
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_70467_a[0] != null) {
         ItemStack var3 = this.field_70467_a[0];
         this.field_70467_a[0] = null;
         return var3;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      if (this.field_70467_a[0] != null) {
         ItemStack var2 = this.field_70467_a[0];
         this.field_70467_a[0] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70467_a[0] = var2;
   }

   @Override
   public int func_70297_j_() {
      return 64;
   }

   @Override
   public void func_70296_d() {
   }

   @Override
   public boolean func_70300_a(EntityPlayer var1) {
      return true;
   }

   @Override
   public void func_70295_k_() {
   }

   @Override
   public void func_70305_f() {
   }

   @Override
   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }
}
