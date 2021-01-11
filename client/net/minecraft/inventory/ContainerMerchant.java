package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerMerchant extends Container {
   private IMerchant field_75178_e;
   private InventoryMerchant field_75176_f;
   private final World field_75177_g;

   public ContainerMerchant(InventoryPlayer var1, IMerchant var2, World var3) {
      super();
      this.field_75178_e = var2;
      this.field_75177_g = var3;
      this.field_75176_f = new InventoryMerchant(var1.field_70458_d, var2);
      this.func_75146_a(new Slot(this.field_75176_f, 0, 36, 53));
      this.func_75146_a(new Slot(this.field_75176_f, 1, 62, 53));
      this.func_75146_a(new SlotMerchantResult(var1.field_70458_d, var2, this.field_75176_f, 2, 120, 53));

      int var4;
      for(var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.func_75146_a(new Slot(var1, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.func_75146_a(new Slot(var1, var4, 8 + var4 * 18, 142));
      }

   }

   public InventoryMerchant func_75174_d() {
      return this.field_75176_f;
   }

   public void func_75132_a(ICrafting var1) {
      super.func_75132_a(var1);
   }

   public void func_75142_b() {
      super.func_75142_b();
   }

   public void func_75130_a(IInventory var1) {
      this.field_75176_f.func_70470_g();
      super.func_75130_a(var1);
   }

   public void func_75175_c(int var1) {
      this.field_75176_f.func_70471_c(var1);
   }

   public void func_75137_b(int var1, int var2) {
   }

   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_75178_e.func_70931_l_() == var1;
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 2) {
            if (!this.func_75135_a(var5, 3, 39, true)) {
               return null;
            }

            var4.func_75220_a(var5, var3);
         } else if (var2 != 0 && var2 != 1) {
            if (var2 >= 3 && var2 < 30) {
               if (!this.func_75135_a(var5, 30, 39, false)) {
                  return null;
               }
            } else if (var2 >= 30 && var2 < 39 && !this.func_75135_a(var5, 3, 30, false)) {
               return null;
            }
         } else if (!this.func_75135_a(var5, 3, 39, false)) {
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

   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      this.field_75178_e.func_70932_a_((EntityPlayer)null);
      super.func_75134_a(var1);
      if (!this.field_75177_g.field_72995_K) {
         ItemStack var2 = this.field_75176_f.func_70304_b(0);
         if (var2 != null) {
            var1.func_71019_a(var2, false);
         }

         var2 = this.field_75176_f.func_70304_b(1);
         if (var2 != null) {
            var1.func_71019_a(var2, false);
         }

      }
   }
}
