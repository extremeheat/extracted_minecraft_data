package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryCrafting implements IInventory {
   private ItemStack[] field_70466_a;
   private int field_70464_b;
   private Container field_70465_c;

   public InventoryCrafting(Container var1, int var2, int var3) {
      super();
      int var4 = var2 * var3;
      this.field_70466_a = new ItemStack[var4];
      this.field_70465_c = var1;
      this.field_70464_b = var2;
   }

   @Override
   public int func_70302_i_() {
      return this.field_70466_a.length;
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return var1 >= this.func_70302_i_() ? null : this.field_70466_a[var1];
   }

   public ItemStack func_70463_b(int var1, int var2) {
      if (var1 >= 0 && var1 < this.field_70464_b) {
         int var3 = var1 + var2 * this.field_70464_b;
         return this.func_70301_a(var3);
      } else {
         return null;
      }
   }

   @Override
   public String func_145825_b() {
      return "container.crafting";
   }

   @Override
   public boolean func_145818_k_() {
      return false;
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      if (this.field_70466_a[var1] != null) {
         ItemStack var2 = this.field_70466_a[var1];
         this.field_70466_a[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_70466_a[var1] != null) {
         if (this.field_70466_a[var1].field_77994_a <= var2) {
            ItemStack var4 = this.field_70466_a[var1];
            this.field_70466_a[var1] = null;
            this.field_70465_c.func_75130_a(this);
            return var4;
         } else {
            ItemStack var3 = this.field_70466_a[var1].func_77979_a(var2);
            if (this.field_70466_a[var1].field_77994_a == 0) {
               this.field_70466_a[var1] = null;
            }

            this.field_70465_c.func_75130_a(this);
            return var3;
         }
      } else {
         return null;
      }
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70466_a[var1] = var2;
      this.field_70465_c.func_75130_a(this);
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
