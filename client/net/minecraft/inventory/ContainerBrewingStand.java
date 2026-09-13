package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBrewingStand;

public class ContainerBrewingStand extends Container {
   private TileEntityBrewingStand field_75188_e;
   private final Slot field_75186_f;
   private int field_75187_g;

   public ContainerBrewingStand(InventoryPlayer var1, TileEntityBrewingStand var2) {
      super();
      this.field_75188_e = var2;
      this.func_75146_a(new ContainerBrewingStand$Potion(var1.field_70458_d, var2, 0, 56, 46));
      this.func_75146_a(new ContainerBrewingStand$Potion(var1.field_70458_d, var2, 1, 79, 53));
      this.func_75146_a(new ContainerBrewingStand$Potion(var1.field_70458_d, var2, 2, 102, 46));
      this.field_75186_f = this.func_75146_a(new ContainerBrewingStand$Ingredient(this, var2, 3, 79, 17));

      for(int var3 = 0; var3 < 3; ++var3) {
         for(int var4 = 0; var4 < 9; ++var4) {
            this.func_75146_a(new Slot(var1, var4 + var3 * 9 + 9, 8 + var4 * 18, 84 + var3 * 18));
         }
      }

      for(int var5 = 0; var5 < 9; ++var5) {
         this.func_75146_a(new Slot(var1, var5, 8 + var5 * 18, 142));
      }
   }

   @Override
   public void func_75132_a(ICrafting var1) {
      super.func_75132_a(var1);
      var1.func_71112_a(this, 0, this.field_75188_e.func_145935_i());
   }

   @Override
   public void func_75142_b() {
      super.func_75142_b();

      for(int var1 = 0; var1 < this.field_75149_d.size(); ++var1) {
         ICrafting var2 = (ICrafting)this.field_75149_d.get(var1);
         if (this.field_75187_g != this.field_75188_e.func_145935_i()) {
            var2.func_71112_a(this, 0, this.field_75188_e.func_145935_i());
         }
      }

      this.field_75187_g = this.field_75188_e.func_145935_i();
   }

   @Override
   public void func_75137_b(int var1, int var2) {
      if (var1 == 0) {
         this.field_75188_e.func_145938_d(var2);
      }
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_75188_e.func_70300_a(var1);
   }

   @Override
   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if ((var2 < 0 || var2 > 2) && var2 != 3) {
            if (!this.field_75186_f.func_75216_d() && this.field_75186_f.func_75214_a(var5)) {
               if (!this.func_75135_a(var5, 3, 4, false)) {
                  return null;
               }
            } else if (ContainerBrewingStand$Potion.func_75243_a_(var3)) {
               if (!this.func_75135_a(var5, 0, 3, false)) {
                  return null;
               }
            } else if (var2 >= 4 && var2 < 31) {
               if (!this.func_75135_a(var5, 31, 40, false)) {
                  return null;
               }
            } else if (var2 >= 31 && var2 < 40) {
               if (!this.func_75135_a(var5, 4, 31, false)) {
                  return null;
               }
            } else if (!this.func_75135_a(var5, 4, 40, false)) {
               return null;
            }
         } else {
            if (!this.func_75135_a(var5, 4, 40, true)) {
               return null;
            }

            var4.func_75220_a(var5, var3);
         }

         if (var5.field_77994_a == 0) {
            var4.func_75215_d(null);
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
