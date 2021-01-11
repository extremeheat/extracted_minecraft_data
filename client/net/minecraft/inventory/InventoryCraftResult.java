package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCraftResult implements IInventory {
   private ItemStack[] field_70467_a = new ItemStack[1];

   public InventoryCraftResult() {
      super();
   }

   public int func_70302_i_() {
      return 1;
   }

   public ItemStack func_70301_a(int var1) {
      return this.field_70467_a[0];
   }

   public String func_70005_c_() {
      return "Result";
   }

   public boolean func_145818_k_() {
      return false;
   }

   public IChatComponent func_145748_c_() {
      return (IChatComponent)(this.func_145818_k_() ? new ChatComponentText(this.func_70005_c_()) : new ChatComponentTranslation(this.func_70005_c_(), new Object[0]));
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_70467_a[0] != null) {
         ItemStack var3 = this.field_70467_a[0];
         this.field_70467_a[0] = null;
         return var3;
      } else {
         return null;
      }
   }

   public ItemStack func_70304_b(int var1) {
      if (this.field_70467_a[0] != null) {
         ItemStack var2 = this.field_70467_a[0];
         this.field_70467_a[0] = null;
         return var2;
      } else {
         return null;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70467_a[0] = var2;
   }

   public int func_70297_j_() {
      return 64;
   }

   public void func_70296_d() {
   }

   public boolean func_70300_a(EntityPlayer var1) {
      return true;
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }

   public int func_174887_a_(int var1) {
      return 0;
   }

   public void func_174885_b(int var1, int var2) {
   }

   public int func_174890_g() {
      return 0;
   }

   public void func_174888_l() {
      for(int var1 = 0; var1 < this.field_70467_a.length; ++var1) {
         this.field_70467_a[var1] = null;
      }

   }
}
