package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class Slot {
   private final int field_75225_a;
   public final IInventory field_75224_c;
   public int field_75222_d;
   public int field_75223_e;
   public int field_75221_f;

   public Slot(IInventory var1, int var2, int var3, int var4) {
      super();
      this.field_75224_c = var1;
      this.field_75225_a = var2;
      this.field_75223_e = var3;
      this.field_75221_f = var4;
   }

   public void func_75220_a(ItemStack var1, ItemStack var2) {
      if (var1 != null && var2 != null) {
         if (var1.func_77973_b() == var2.func_77973_b()) {
            int var3 = var2.field_77994_a - var1.field_77994_a;
            if (var3 > 0) {
               this.func_75210_a(var1, var3);
            }

         }
      }
   }

   protected void func_75210_a(ItemStack var1, int var2) {
   }

   protected void func_75208_c(ItemStack var1) {
   }

   public void func_82870_a(EntityPlayer var1, ItemStack var2) {
      this.func_75218_e();
   }

   public boolean func_75214_a(ItemStack var1) {
      return true;
   }

   public ItemStack func_75211_c() {
      return this.field_75224_c.func_70301_a(this.field_75225_a);
   }

   public boolean func_75216_d() {
      return this.func_75211_c() != null;
   }

   public void func_75215_d(ItemStack var1) {
      this.field_75224_c.func_70299_a(this.field_75225_a, var1);
      this.func_75218_e();
   }

   public void func_75218_e() {
      this.field_75224_c.func_70296_d();
   }

   public int func_75219_a() {
      return this.field_75224_c.func_70297_j_();
   }

   public int func_178170_b(ItemStack var1) {
      return this.func_75219_a();
   }

   public String func_178171_c() {
      return null;
   }

   public ItemStack func_75209_a(int var1) {
      return this.field_75224_c.func_70298_a(this.field_75225_a, var1);
   }

   public boolean func_75217_a(IInventory var1, int var2) {
      return var1 == this.field_75224_c && var2 == this.field_75225_a;
   }

   public boolean func_82869_a(EntityPlayer var1) {
      return true;
   }

   public boolean func_111238_b() {
      return true;
   }
}
