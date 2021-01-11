package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerPlayer extends Container {
   public InventoryCrafting field_75181_e = new InventoryCrafting(this, 2, 2);
   public IInventory field_75179_f = new InventoryCraftResult();
   public boolean field_75180_g;
   private final EntityPlayer field_82862_h;

   public ContainerPlayer(InventoryPlayer var1, boolean var2, EntityPlayer var3) {
      super();
      this.field_75180_g = var2;
      this.field_82862_h = var3;
      this.func_75146_a(new SlotCrafting(var1.field_70458_d, this.field_75181_e, this.field_75179_f, 0, 144, 36));

      final int var4;
      int var5;
      for(var4 = 0; var4 < 2; ++var4) {
         for(var5 = 0; var5 < 2; ++var5) {
            this.func_75146_a(new Slot(this.field_75181_e, var5 + var4 * 2, 88 + var5 * 18, 26 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 4; ++var4) {
         this.func_75146_a(new Slot(var1, var1.func_70302_i_() - 1 - var4, 8, 8 + var4 * 18) {
            public int func_75219_a() {
               return 1;
            }

            public boolean func_75214_a(ItemStack var1) {
               if (var1 == null) {
                  return false;
               } else if (var1.func_77973_b() instanceof ItemArmor) {
                  return ((ItemArmor)var1.func_77973_b()).field_77881_a == var4;
               } else if (var1.func_77973_b() != Item.func_150898_a(Blocks.field_150423_aK) && var1.func_77973_b() != Items.field_151144_bL) {
                  return false;
               } else {
                  return var4 == 0;
               }
            }

            public String func_178171_c() {
               return ItemArmor.field_94603_a[var4];
            }
         });
      }

      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = 0; var5 < 9; ++var5) {
            this.func_75146_a(new Slot(var1, var5 + (var4 + 1) * 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.func_75146_a(new Slot(var1, var4, 8 + var4 * 18, 142));
      }

      this.func_75130_a(this.field_75181_e);
   }

   public void func_75130_a(IInventory var1) {
      this.field_75179_f.func_70299_a(0, CraftingManager.func_77594_a().func_82787_a(this.field_75181_e, this.field_82862_h.field_70170_p));
   }

   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);

      for(int var2 = 0; var2 < 4; ++var2) {
         ItemStack var3 = this.field_75181_e.func_70304_b(var2);
         if (var3 != null) {
            var1.func_71019_a(var3, false);
         }
      }

      this.field_75179_f.func_70299_a(0, (ItemStack)null);
   }

   public boolean func_75145_c(EntityPlayer var1) {
      return true;
   }

   public ItemStack func_82846_b(EntityPlayer var1, int var2) {
      ItemStack var3 = null;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 0) {
            if (!this.func_75135_a(var5, 9, 45, true)) {
               return null;
            }

            var4.func_75220_a(var5, var3);
         } else if (var2 >= 1 && var2 < 5) {
            if (!this.func_75135_a(var5, 9, 45, false)) {
               return null;
            }
         } else if (var2 >= 5 && var2 < 9) {
            if (!this.func_75135_a(var5, 9, 45, false)) {
               return null;
            }
         } else if (var3.func_77973_b() instanceof ItemArmor && !((Slot)this.field_75151_b.get(5 + ((ItemArmor)var3.func_77973_b()).field_77881_a)).func_75216_d()) {
            int var6 = 5 + ((ItemArmor)var3.func_77973_b()).field_77881_a;
            if (!this.func_75135_a(var5, var6, var6 + 1, false)) {
               return null;
            }
         } else if (var2 >= 9 && var2 < 36) {
            if (!this.func_75135_a(var5, 36, 45, false)) {
               return null;
            }
         } else if (var2 >= 36 && var2 < 45) {
            if (!this.func_75135_a(var5, 9, 36, false)) {
               return null;
            }
         } else if (!this.func_75135_a(var5, 9, 45, false)) {
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

   public boolean func_94530_a(ItemStack var1, Slot var2) {
      return var2.field_75224_c != this.field_75179_f && super.func_94530_a(var1, var2);
   }
}
