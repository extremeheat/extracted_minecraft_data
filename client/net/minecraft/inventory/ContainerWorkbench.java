package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerWorkbench extends ContainerRecipeBook {
   public InventoryCrafting field_75162_e = new InventoryCrafting(this, 3, 3);
   public InventoryCraftResult field_75160_f = new InventoryCraftResult();
   private final World field_75161_g;
   private final BlockPos field_178145_h;
   private final EntityPlayer field_192390_i;

   public ContainerWorkbench(InventoryPlayer var1, World var2, BlockPos var3) {
      super();
      this.field_75161_g = var2;
      this.field_178145_h = var3;
      this.field_192390_i = var1.field_70458_d;
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

   }

   public void func_75130_a(IInventory var1) {
      this.func_192389_a(this.field_75161_g, this.field_192390_i, this.field_75162_e, this.field_75160_f);
   }

   public void func_201771_a(RecipeItemHelper var1) {
      this.field_75162_e.func_194018_a(var1);
   }

   public void func_201768_e() {
      this.field_75162_e.func_174888_l();
      this.field_75160_f.func_174888_l();
   }

   public boolean func_201769_a(IRecipe var1) {
      return var1.func_77569_a(this.field_75162_e, this.field_192390_i.field_70170_p);
   }

   public void func_75134_a(EntityPlayer var1) {
      super.func_75134_a(var1);
      if (!this.field_75161_g.field_72995_K) {
         this.func_193327_a(var1, this.field_75161_g, this.field_75162_e);
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
      ItemStack var3 = ItemStack.field_190927_a;
      Slot var4 = (Slot)this.field_75151_b.get(var2);
      if (var4 != null && var4.func_75216_d()) {
         ItemStack var5 = var4.func_75211_c();
         var3 = var5.func_77946_l();
         if (var2 == 0) {
            var5.func_77973_b().func_77622_d(var5, this.field_75161_g, var1);
            if (!this.func_75135_a(var5, 10, 46, true)) {
               return ItemStack.field_190927_a;
            }

            var4.func_75220_a(var5, var3);
         } else if (var2 >= 10 && var2 < 37) {
            if (!this.func_75135_a(var5, 37, 46, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (var2 >= 37 && var2 < 46) {
            if (!this.func_75135_a(var5, 10, 37, false)) {
               return ItemStack.field_190927_a;
            }
         } else if (!this.func_75135_a(var5, 10, 46, false)) {
            return ItemStack.field_190927_a;
         }

         if (var5.func_190926_b()) {
            var4.func_75215_d(ItemStack.field_190927_a);
         } else {
            var4.func_75218_e();
         }

         if (var5.func_190916_E() == var3.func_190916_E()) {
            return ItemStack.field_190927_a;
         }

         ItemStack var6 = var4.func_190901_a(var1, var5);
         if (var2 == 0) {
            var1.func_71019_a(var6, false);
         }
      }

      return var3;
   }

   public boolean func_94530_a(ItemStack var1, Slot var2) {
      return var2.field_75224_c != this.field_75160_f && super.func_94530_a(var1, var2);
   }

   public int func_201767_f() {
      return 0;
   }

   public int func_201770_g() {
      return this.field_75162_e.func_174922_i();
   }

   public int func_201772_h() {
      return this.field_75162_e.func_174923_h();
   }

   public int func_203721_h() {
      return 10;
   }
}
