package net.minecraft.inventory;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.village.MerchantRecipe;

public class SlotMerchantResult extends Slot {
   private final InventoryMerchant field_75233_a;
   private final EntityPlayer field_75232_b;
   private int field_75231_g;
   private final IMerchant field_75234_h;

   public SlotMerchantResult(EntityPlayer var1, IMerchant var2, InventoryMerchant var3, int var4, int var5, int var6) {
      super(var3, var4, var5, var6);
      this.field_75232_b = var1;
      this.field_75234_h = var2;
      this.field_75233_a = var3;
   }

   public boolean func_75214_a(ItemStack var1) {
      return false;
   }

   public ItemStack func_75209_a(int var1) {
      if (this.func_75216_d()) {
         this.field_75231_g += Math.min(var1, this.func_75211_c().func_190916_E());
      }

      return super.func_75209_a(var1);
   }

   protected void func_75210_a(ItemStack var1, int var2) {
      this.field_75231_g += var2;
      this.func_75208_c(var1);
   }

   protected void func_75208_c(ItemStack var1) {
      var1.func_77980_a(this.field_75232_b.field_70170_p, this.field_75232_b, this.field_75231_g);
      this.field_75231_g = 0;
   }

   public ItemStack func_190901_a(EntityPlayer var1, ItemStack var2) {
      this.func_75208_c(var2);
      MerchantRecipe var3 = this.field_75233_a.func_70468_h();
      if (var3 != null) {
         ItemStack var4 = this.field_75233_a.func_70301_a(0);
         ItemStack var5 = this.field_75233_a.func_70301_a(1);
         if (this.func_75230_a(var3, var4, var5) || this.func_75230_a(var3, var5, var4)) {
            this.field_75234_h.func_70933_a(var3);
            var1.func_195066_a(StatList.field_188075_I);
            this.field_75233_a.func_70299_a(0, var4);
            this.field_75233_a.func_70299_a(1, var5);
         }
      }

      return var2;
   }

   private boolean func_75230_a(MerchantRecipe var1, ItemStack var2, ItemStack var3) {
      ItemStack var4 = var1.func_77394_a();
      ItemStack var5 = var1.func_77396_b();
      if (var2.func_77973_b() == var4.func_77973_b() && var2.func_190916_E() >= var4.func_190916_E()) {
         if (!var5.func_190926_b() && !var3.func_190926_b() && var5.func_77973_b() == var3.func_77973_b() && var3.func_190916_E() >= var5.func_190916_E()) {
            var2.func_190918_g(var4.func_190916_E());
            var3.func_190918_g(var5.func_190916_E());
            return true;
         }

         if (var5.func_190926_b() && var3.func_190926_b()) {
            var2.func_190918_g(var4.func_190916_E());
            return true;
         }
      }

      return false;
   }
}
