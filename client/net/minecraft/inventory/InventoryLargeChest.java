package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryLargeChest implements IInventory {
   private String field_70479_a;
   private IInventory field_70477_b;
   private IInventory field_70478_c;

   public InventoryLargeChest(String var1, IInventory var2, IInventory var3) {
      super();
      this.field_70479_a = var1;
      if (var2 == null) {
         var2 = var3;
      }

      if (var3 == null) {
         var3 = var2;
      }

      this.field_70477_b = var2;
      this.field_70478_c = var3;
   }

   @Override
   public int func_70302_i_() {
      return this.field_70477_b.func_70302_i_() + this.field_70478_c.func_70302_i_();
   }

   public boolean func_90010_a(IInventory var1) {
      return this.field_70477_b == var1 || this.field_70478_c == var1;
   }

   @Override
   public String func_145825_b() {
      if (this.field_70477_b.func_145818_k_()) {
         return this.field_70477_b.func_145825_b();
      } else {
         return this.field_70478_c.func_145818_k_() ? this.field_70478_c.func_145825_b() : this.field_70479_a;
      }
   }

   @Override
   public boolean func_145818_k_() {
      return this.field_70477_b.func_145818_k_() || this.field_70478_c.func_145818_k_();
   }

   @Override
   public ItemStack func_70301_a(int var1) {
      return var1 >= this.field_70477_b.func_70302_i_()
         ? this.field_70478_c.func_70301_a(var1 - this.field_70477_b.func_70302_i_())
         : this.field_70477_b.func_70301_a(var1);
   }

   @Override
   public ItemStack func_70298_a(int var1, int var2) {
      return var1 >= this.field_70477_b.func_70302_i_()
         ? this.field_70478_c.func_70298_a(var1 - this.field_70477_b.func_70302_i_(), var2)
         : this.field_70477_b.func_70298_a(var1, var2);
   }

   @Override
   public ItemStack func_70304_b(int var1) {
      return var1 >= this.field_70477_b.func_70302_i_()
         ? this.field_70478_c.func_70304_b(var1 - this.field_70477_b.func_70302_i_())
         : this.field_70477_b.func_70304_b(var1);
   }

   @Override
   public void func_70299_a(int var1, ItemStack var2) {
      if (var1 >= this.field_70477_b.func_70302_i_()) {
         this.field_70478_c.func_70299_a(var1 - this.field_70477_b.func_70302_i_(), var2);
      } else {
         this.field_70477_b.func_70299_a(var1, var2);
      }
   }

   @Override
   public int func_70297_j_() {
      return this.field_70477_b.func_70297_j_();
   }

   @Override
   public void func_70296_d() {
      this.field_70477_b.func_70296_d();
      this.field_70478_c.func_70296_d();
   }

   @Override
   public boolean func_70300_a(EntityPlayer var1) {
      return this.field_70477_b.func_70300_a(var1) && this.field_70478_c.func_70300_a(var1);
   }

   @Override
   public void func_70295_k_() {
      this.field_70477_b.func_70295_k_();
      this.field_70478_c.func_70295_k_();
   }

   @Override
   public void func_70305_f() {
      this.field_70477_b.func_70305_f();
      this.field_70478_c.func_70305_f();
   }

   @Override
   public boolean func_94041_b(int var1, ItemStack var2) {
      return true;
   }
}
