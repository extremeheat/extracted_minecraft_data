package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container {
   private final IInventory field_94538_a;

   public ContainerHopper(InventoryPlayer var1, IInventory var2, EntityPlayer var3) {
      super();
      this.field_94538_a = var2;
      var2.func_174889_b(var3);
      boolean var4 = true;

      int var5;
      for(var5 = 0; var5 < var2.func_70302_i_(); ++var5) {
         this.func_75146_a(new Slot(var2, var5, 44 + var5 * 18, 20));
      }

      for(var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.func_75146_a(new Slot(var1, var6 + var5 * 9 + 9, 8 + var6 * 18, var5 * 18 + 51));
         }
      }

      for(var5 = 0; var5 < 9; ++var5) {
         this.func_75146_a(new Slot(var1, var5, 8 + var5 * 18, 109));
      }

   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_94538_a.func_70300_a(var1);
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = ItemStack.field_190927_a;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 < this.field_94538_a.func_70302_i_()) {
            if (!this.func_75135_a(var5, this.field_94538_a.func_70302_i_(), this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(var5, 0, this.field_94538_a.func_70302_i_(), false)) {
            return ItemStack.field_190927_a;
         }

         if (var5.func_190926_b()) {
            var4.func_75215_d(ItemStack.field_190927_a);
         } else {
            var4.func_75218_e();
         }
      }

      return var3;
   }

   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      this.field_94538_a.func_174886_c(var1);
   }
}
