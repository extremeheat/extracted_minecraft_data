package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ContainerWorkbench extends Container {
   public InventoryCrafting field_75162_e = new InventoryCrafting(this, 3, 3);
   public IInventory field_75160_f = new InventoryCraftResult();
   private World field_75161_g;
   private BlockPos field_178145_h;

   public ContainerWorkbench(InventoryPlayer var1, World var2, BlockPos var3) {
      super();
      this.field_75161_g = var2;
      this.field_178145_h = var3;
      this.func_75146_a(new SlotCrafting(var1.field_70458_d, this.field_75162_e, this.field_75160_f, 0, 124, 35));

      int var4;
      int var5;
      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = 0; var5 < 3; ++var5) {
            this.func_75146_a(new Slot(this.field_75162_e, var5 + var4 * 3, 30 + var5 * 18, 17 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 3; ++var4) {
         for(var5 = 0; var5 < 9; ++var5) {
            this.func_75146_a(new Slot(var1, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.func_75146_a(new Slot(var1, var4, 8 + var4 * 18, 142));
      }

      this.func_75130_a(this.field_75162_e);
   }

   public void func_75130_a(IInventory var1) {
      this.field_75160_f.func_70299_a(0, CraftingManager.func_77594_a().func_82787_a(this.field_75162_e, this.field_75161_g));
   }

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

   public boolean func_75145_c(EntityPlayer var1) {
      if (this.field_75161_g.func_180495_p(this.field_178145_h).func_177230_c() != Blocks.field_150462_ai) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_178145_h.func_177958_n() + 0.5D, (double)this.field_178145_h.func_177956_o() + 0.5D, (double)this.field_178145_h.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

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
      return var2.field_75224_c != this.field_75160_f && super.func_94530_a(var1, var2);
   }
}
