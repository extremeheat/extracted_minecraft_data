package net.minecraft.inventory;

import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerHorseInventory extends Container {
   private IInventory field_111243_a;
   private EntityHorse field_111242_f;

   public ContainerHorseInventory(IInventory var1, IInventory var2, EntityHorse var3) {
      super();
      this.field_111243_a = var2;
      this.field_111242_f = var3;
      byte var4 = 3;
      var2.func_70295_k_();
      int var5 = (var4 - 4) * 18;
      this.func_75146_a(new ContainerHorseInventory$1(this, var2, 0, 8, 18));
      this.func_75146_a(new ContainerHorseInventory$2(this, var2, 1, 8, 36, var3));
      if (var3.func_110261_ca()) {
         for(int var6 = 0; var6 < var4; ++var6) {
            for(int var7 = 0; var7 < 5; ++var7) {
               this.func_75146_a(new Slot(var2, 2 + var7 + var6 * 5, 80 + var7 * 18, 18 + var6 * 18));
            }
         }
      }

      for(int var8 = 0; var8 < 3; ++var8) {
         for(int var10 = 0; var10 < 9; ++var10) {
            this.func_75146_a(new Slot(var1, var10 + var8 * 9 + 9, 8 + var10 * 18, 102 + var8 * 18 + var5));
         }
      }

      for(int var9 = 0; var9 < 9; ++var9) {
         this.func_75146_a(new Slot(var1, var9, 8 + var9 * 18, 160 + var5));
      }
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_111243_a.func_70300_a(var1) && this.field_111242_f.func_70089_S() && this.field_111242_f.func_70032_d(var1) < 8.0F;
   }

   @Override
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
      this.field_111243_a.func_70305_f();
   }
}
