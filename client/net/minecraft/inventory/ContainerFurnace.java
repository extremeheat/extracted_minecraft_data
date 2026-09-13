package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerFurnace extends Container {
   private TileEntityFurnace field_75158_e;
   private int field_75156_f;
   private int field_75157_g;
   private int field_75159_h;

   public ContainerFurnace(InventoryPlayer var1, TileEntityFurnace var2) {
      super();
      this.field_75158_e = var2;
      this.func_75146_a(new Slot(var2, 0, 56, 17));
      this.func_75146_a(new Slot(var2, 1, 56, 53));
      this.func_75146_a(new SlotFurnace(var1.field_70458_d, var2, 2, 116, 35));

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
      var1.func_71112_a(this, 0, this.field_75158_e.field_145961_j);
      var1.func_71112_a(this, 1, this.field_75158_e.field_145956_a);
      var1.func_71112_a(this, 2, this.field_75158_e.field_145963_i);
   }

   @Override
   public void func_75142_b() {
      super.func_75142_b();

      for(int var1 = 0; var1 < this.field_75149_d.size(); ++var1) {
         ICrafting var2 = (ICrafting)this.field_75149_d.get(var1);
         if (this.field_75156_f != this.field_75158_e.field_145961_j) {
            var2.func_71112_a(this, 0, this.field_75158_e.field_145961_j);
         }

         if (this.field_75157_g != this.field_75158_e.field_145956_a) {
            var2.func_71112_a(this, 1, this.field_75158_e.field_145956_a);
         }

         if (this.field_75159_h != this.field_75158_e.field_145963_i) {
            var2.func_71112_a(this, 2, this.field_75158_e.field_145963_i);
         }
      }

      this.field_75156_f = this.field_75158_e.field_145961_j;
      this.field_75157_g = this.field_75158_e.field_145956_a;
      this.field_75159_h = this.field_75158_e.field_145963_i;
   }

   @Override
   public void func_75137_b(int var1, int var2) {
      if (var1 == 0) {
         this.field_75158_e.field_145961_j = var2;
      }

      if (var1 == 1) {
         this.field_75158_e.field_145956_a = var2;
      }

      if (var1 == 2) {
         this.field_75158_e.field_145963_i = var2;
      }
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      return this.field_75158_e.func_70300_a(var1);
   }

   @Override
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
         } else if (var2 != 1 && var2 != 0) {
            if (FurnaceRecipes.func_77602_a().func_151395_a(var5) != null) {
               if (!this.func_75135_a(var5, 0, 1, false)) {
                  return null;
               }
            } else if (TileEntityFurnace.func_145954_b(var5)) {
               if (!this.func_75135_a(var5, 1, 2, false)) {
                  return null;
               }
            } else if (var2 >= 3 && var2 < 30) {
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
