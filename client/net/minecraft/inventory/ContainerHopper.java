package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHopper extends Container {
   private final IInventory field_94538_a;

   public ContainerHopper(InventoryPlayer var1, IInventory var2) {
      super();
      this.field_94538_a = var2;
      var2.func_70295_k_();
      byte var3 = 51;

      for(int var4 = 0; var4 < var2.func_70302_i_(); ++var4) {
         this.func_75146_a(new Slot(var2, var4, 44 + var4 * 18, 20));
      }

      for(int var6 = 0; var6 < 3; ++var6) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.func_75146_a(new Slot(var1, var5 + var6 * 9 + 9, 8 + var5 * 18, var6 * 18 + var3));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.func_75146_a(new Slot(var1, var7, 8 + var7 * 18, 58 + var3));
      }
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_94538_a.func_70300_a(var1);
   }

   @Override
   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 < this.field_94538_a.func_70302_i_()) {
            if (!this.func_75135_a(var5, this.field_94538_a.func_70302_i_(), this.field_75151_b.size(), true)) {
               return null;
            }
         } else if (!this.func_75135_a(var5, 0, this.field_94538_a.func_70302_i_(), false)) {
            return null;
         }

         if (var5.field_77994_a == 0) {
            var4.func_75215_d(null);
         } else {
            var4.func_75218_e();
         }
      }

      return var3;
   }

   @Override
   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      this.field_94538_a.func_70305_f();
   }
}
