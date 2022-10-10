package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container {
   private final IInventory field_75155_e;
   private final int field_75154_f;

   public ContainerChest(IInventory var1, IInventory var2, EntityPlayer var3) {
      super();
      this.field_75155_e = var2;
      this.field_75154_f = var2.func_70302_i_() / 9;
      var2.func_174889_b(var3);
      int var4 = (this.field_75154_f - 4) * 18;

      int var5;
      int var6;
      for(var5 = 0; var5 < this.field_75154_f; ++var5) {
         for(var6 = 0; var6 < 9; ++var6) {
            this.func_75146_a(new Slot(var2, var6 + var5 * 9, 8 + var6 * 18, 18 + var5 * 18));
         }
      }

      for(var5 = 0; var5 < 3; ++var5) {
         for(var6 = 0; var6 < 9; ++var6) {
            this.func_75146_a(new Slot(var1, var6 + var5 * 9 + 9, 8 + var6 * 18, 103 + var5 * 18 + var4));
         }
      }

      for(var5 = 0; var5 < 9; ++var5) {
         this.func_75146_a(new Slot(var1, var5, 8 + var5 * 18, 161 + var4));
      }

   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_75155_e.func_70300_a(var1);
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = ItemStack.field_190927_a;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 < this.field_75154_f * 9) {
            if (!this.func_75135_a(var5, this.field_75154_f * 9, this.field_75151_b.size(), true)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(var5, 0, this.field_75154_f * 9, false)) {
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
      this.field_75155_e.func_174886_c(var1);
   }

   public IInventory func_85151_d() {
      return this.field_75155_e;
   }
}
