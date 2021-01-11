package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerDispenser extends Container {
   private IInventory field_178146_a;

   public ContainerDispenser(IInventory var1, IInventory var2) {
      super();
      this.field_178146_a = var2;

      int var3;
      int var4;
      for(var3 = 0; var3 < 3; ++var3) {
         for(var4 = 0; var4 < 3; ++var4) {
            this.func_75146_a(new Slot(var2, var4 + var3 * 3, 62 + var4 * 18, 17 + var3 * 18));
         }
      }

      for(var3 = 0; var3 < 3; ++var3) {
         for(var4 = 0; var4 < 9; ++var4) {
            this.func_75146_a(new Slot(var1, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
         }
      }

      for(var3 = 0; var3 < 9; ++var3) {
         this.func_75146_a(new Slot(var1, var3, 8 + var3 * 18, 142));
      }

   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_178146_a.func_70300_a(var1);
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 < 9) {
            if (!this.func_75135_a(var5, 9, 45, true)) {
               return null;
            }
         } else if (!this.func_75135_a(var5, 0, 9, false)) {
            return null;
         }

         if (var5.field_77994_a == 0) {
            var4.func_75215_d((ItemStack)null);
         } else {
            var4.func_75218_e();
         }

         if (var5.field_77994_a == var3.field_77994_a) {
            return null;
         }

         var4.func_82870_a(var1, var5);
      }

      return var3;
   }
}
