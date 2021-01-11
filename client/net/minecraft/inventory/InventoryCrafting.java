package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class InventoryCrafting implements IInventory {
   private final ItemStack[] field_70466_a;
   private final int field_70464_b;
   private final int field_174924_c;
   private final Container field_70465_c;

   public InventoryCrafting(Container var1, int var2, int var3) {
      super();
      int var4 = var2 * var3;
      this.field_70466_a = new ItemStack[var4];
      this.field_70465_c = var1;
      this.field_70464_b = var2;
      this.field_174924_c = var3;
   }

   public int func_70302_i_() {
      return this.field_70466_a.length;
   }

   public ItemStack func_70301_a(int var1) {
      return var1 >= this.func_70302_i_() ? null : this.field_70466_a[var1];
   }

   public ItemStack func_70463_b(int var1, int var2) {
      return var1 >= 0 && var1 < this.field_70464_b && var2 >= 0 && var2 <= this.field_174924_c ? this.func_70301_a(var1 + var2 * this.field_70464_b) : null;
   }

   public String func_70005_c_() {
      return "container.crafting";
   }

   public boolean func_145818_k_() {
      return false;
   }

   public IChatComponent func_145748_c_() {
      return (IChatComponent)(this.func_145818_k_() ? new ChatComponentText(this.func_70005_c_()) : new ChatComponentTranslation(this.func_70005_c_(), new Object[0]));
   }

   public ItemStack func_70304_b(int var1) {
      if (this.field_70466_a[var1] != null) {
         ItemStack var2 = this.field_70466_a[var1];
         this.field_70466_a[var1] = null;
         return var2;
      } else {
         return null;
      }
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (this.field_70466_a[var1] != null) {
         ItemStack var3;
         if (this.field_70466_a[var1].field_77994_a <= var2) {
            var3 = this.field_70466_a[var1];
            this.field_70466_a[var1] = null;
            this.field_70465_c.func_75130_a(this);
            return var3;
         } else {
            var3 = this.field_70466_a[var1].func_77979_a(var2);
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

   public void func_70299_a(int var1, ItemStack var2) {
      this.field_70466_a[var1] = var2;
      this.field_70465_c.func_75130_a(this);
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
      for(int var1 = 0; var1 < this.field_70466_a.length; ++var1) {
         this.field_70466_a[var1] = null;
      }

   }

   public int func_174923_h() {
      return this.field_174924_c;
   }

   public int func_174922_i() {
      return this.field_70464_b;
   }
}
