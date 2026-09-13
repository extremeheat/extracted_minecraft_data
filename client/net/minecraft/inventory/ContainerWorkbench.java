package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container {
   public InventoryCrafting field_75162_e = new InventoryCrafting(this, 3, 3);
   public IInventory field_75160_f = new InventoryCraftResult();
   private World field_75161_g;
   private int field_75164_h;
   private int field_75165_i;
   private int field_75163_j;

   public ContainerWorkbench(InventoryPlayer var1, World var2, int var3, int var4, int var5) {
      super();
      this.field_75161_g = var2;
      this.field_75164_h = var3;
      this.field_75165_i = var4;
      this.field_75163_j = var5;
      this.func_75146_a(new SlotCrafting(var1.field_70458_d, this.field_75162_e, this.field_75160_f, 0, 124, 35));

      for(int var6 = 0; var6 < 3; ++var6) {
         for(int var7 = 0; var7 < 3; ++var7) {
            this.func_75146_a(new Slot(this.field_75162_e, var7 + var6 * 3, 30 + var7 * 18, 17 + var6 * 18));
         }
      }

      for(int var8 = 0; var8 < 3; ++var8) {
         for(int var10 = 0; var10 < 9; ++var10) {
            this.func_75146_a(new Slot(var1, var10 + var8 * 9 + 9, 8 + var10 * 18, 84 + var8 * 18));
         }
      }

      for(int var9 = 0; var9 < 9; ++var9) {
         this.func_75146_a(new Slot(var1, var9, 8 + var9 * 18, 142));
      }

      this.func_75130_a(this.field_75162_e);
   }

   @Override
   public void func_75130_a(IInventory var1) {
      this.field_75160_f.func_70299_a(0, CraftingManager.func_77594_a().func_82787_a(this.field_75162_e, this.field_75161_g));
   }

   @Override
   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      if (!this.field_75161_g.field_72995_K) {
         for(int var2 = 0; var2 < 9; ++var2) {
            ItemStack var3 = this.field_75162_e.func_70304_b(var2);
            if (var3 != null) {
               var1.func_71019_a(var3, false);
            }
         }
      }
   }

   @Override
   public boolean func_75145_c(EntityPlayer var1) {
      if (this.field_75161_g.func_147439_a(this.field_75164_h, this.field_75165_i, this.field_75163_j) != Blocks.field_150462_ai) {
         return false;
      } else {
         return !(var1.func_70092_e((double)this.field_75164_h + 0.5, (double)this.field_75165_i + 0.5, (double)this.field_75163_j + 0.5) > 64.0);
      }
   }

   @Override
   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 0) {
            if (!this.func_75135_a(var5, 10, 46, true)) {
               return null;
            }

            var4.func_75220_a(var5, var3);
         } else if (var2 >= 10 && var2 < 37) {
            if (!this.func_75135_a(var5, 37, 46, false)) {
               return null;
            }
         } else if (var2 >= 37 && var2 < 46) {
            if (!this.func_75135_a(var5, 10, 37, false)) {
               return null;
            }
         } else if (!this.func_75135_a(var5, 10, 46, false)) {
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

   @Override
   public boolean func_94530_a(ItemStack var1, Slot var2) {
      return var2.field_75224_c != this.field_75160_f && super.func_94530_a(var1, var2);
   }
}
