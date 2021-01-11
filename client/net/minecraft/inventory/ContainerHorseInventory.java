package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container {
   private IInventory field_111243_a;
   private EntityHorse field_111242_f;

   public ContainerHorseInventory(IInventory var1, IInventory var2, final EntityHorse var3, EntityPlayer var4) {
      super();
      this.field_111243_a = var2;
      this.field_111242_f = var3;
      byte var5 = 3;
      var2.func_174889_b(var4);
      int var6 = (var5 - 4) * 18;
      this.func_75146_a(new Slot(var2, 0, 8, 18) {
         public boolean func_75214_a(ItemStack var1) {
            return super.func_75214_a(var1) && var1.func_77973_b() == Items.field_151141_av && !this.func_75216_d();
         }
      });
      this.func_75146_a(new Slot(var2, 1, 8, 36) {
         public boolean func_75214_a(ItemStack var1) {
            return super.func_75214_a(var1) && var3.func_110259_cr() && EntityHorse.func_146085_a(var1.func_77973_b());
         }

         public boolean func_111238_b() {
            return var3.func_110259_cr();
         }
      });
      int var7;
      int var8;
      if (var3.func_110261_ca()) {
         for(var7 = 0; var7 < var5; ++var7) {
            for(var8 = 0; var8 < 5; ++var8) {
               this.func_75146_a(new Slot(var2, 2 + var8 + var7 * 5, 80 + var8 * 18, 18 + var7 * 18));
            }
         }
      }

      for(var7 = 0; var7 < 3; ++var7) {
         for(var8 = 0; var8 < 9; ++var8) {
            this.func_75146_a(new Slot(var1, var8 + var7 * 9 + 9, 8 + var8 * 18, 102 + var7 * 18 + var6));
         }
      }

      for(var7 = 0; var7 < 9; ++var7) {
         this.func_75146_a(new Slot(var1, var7, 8 + var7 * 18, 160 + var6));
      }

   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_111243_a.func_70300_a(var1) && this.field_111242_f.func_70089_S() && this.field_111242_f.func_70032_d(var1) < 8.0F;
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 < this.field_111243_a.func_70302_i_()) {
            if (!this.func_75135_a(var5, this.field_111243_a.func_70302_i_(), this.field_75151_b.size(), true)) {
               return null;
            }
         } else if (this.func_75139_a(1).func_75214_a(var5) && !this.func_75139_a(1).func_75216_d()) {
            if (!this.func_75135_a(var5, 1, 2, false)) {
               return null;
            }
         } else if (this.func_75139_a(0).func_75214_a(var5)) {
            if (!this.func_75135_a(var5, 0, 1, false)) {
               return null;
            }
         } else if (this.field_111243_a.func_70302_i_() <= 2 || !this.func_75135_a(var5, 2, this.field_111243_a.func_70302_i_(), false)) {
            return null;
         }

         if (var5.field_77994_a == 0) {
            var4.func_75215_d((ItemStack)null);
         } else {
            var4.func_75218_e();
         }
      }

      return var3;
   }

   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      this.field_111243_a.func_174886_c(var1);
   }
}
