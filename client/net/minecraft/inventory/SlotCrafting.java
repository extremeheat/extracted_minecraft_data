package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SlotCrafting extends Slot {
   private final InventoryCrafting field_75239_a;
   private final EntityPlayer field_75238_b;
   private int field_75237_g;

   public SlotCrafting(EntityPlayer var1, InventoryCrafting var2, IInventory var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.field_75238_b = var1;
      this.field_75239_a = var2;
   }

   public boolean func_75214_a(ItemStack var1) {
      return false;
   }

   public ItemStack func_75209_a(int var1) {
      if (this.func_75216_d()) {
         this.field_75237_g += Math.min(var1, this.func_75211_c().func_190916_E());
      }

      return super.func_75209_a(var1);
   }

   protected void func_75210_a(ItemStack var1, int var2) {
      this.field_75237_g += var2;
      this.func_75208_c(var1);
   }

   protected void func_190900_b(int var1) {
      this.field_75237_g += var1;
   }

   protected void func_75208_c(ItemStack var1) {
      if (this.field_75237_g > 0) {
         var1.func_77980_a(this.field_75238_b.field_70170_p, this.field_75238_b, this.field_75237_g);
      }

      ((IRecipeHolder)this.field_75224_c).func_201560_d(this.field_75238_b);
      this.field_75237_g = 0;
   }

   public ItemStack func_190901_a(EntityPlayer var1, ItemStack var2) {
      this.func_75208_c(var2);
      NonNullList var3 = var1.field_70170_p.func_199532_z().func_199513_c(this.field_75239_a, var1.field_70170_p);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         ItemStack var5 = this.field_75239_a.func_70301_a(var4);
         ItemStack var6 = (ItemStack)var3.get(var4);
         if (!var5.func_190926_b()) {
            this.field_75239_a.func_70298_a(var4, 1);
            var5 = this.field_75239_a.func_70301_a(var4);
         }

         if (!var6.func_190926_b()) {
            if (var5.func_190926_b()) {
               this.field_75239_a.func_70299_a(var4, var6);
            } else if (ItemStack.func_179545_c(var5, var6) && ItemStack.func_77970_a(var5, var6)) {
               var6.func_190917_f(var5.func_190916_E());
               this.field_75239_a.func_70299_a(var4, var6);
            } else if (!this.field_75238_b.field_71071_by.func_70441_a(var6)) {
               this.field_75238_b.func_71019_a(var6, false);
            }
         }
      }

      return var2;
   }
}
