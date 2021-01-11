package net.minecraft.inventory;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.MathHelper;

public class SlotFurnaceOutput extends Slot {
   private EntityPlayer field_75229_a;
   private int field_75228_b;

   public SlotFurnaceOutput(EntityPlayer var1, IInventory var2, int var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.field_75229_a = var1;
   }

   public boolean func_75214_a(ItemStack var1) {
      return false;
   }

   public ItemStack func_75209_a(int var1) {
      if (this.func_75216_d()) {
         this.field_75228_b += Math.min(var1, this.func_75211_c().field_77994_a);
      }

      return super.func_75209_a(var1);
   }

   public void func_82870_a(EntityPlayer var1, ItemStack var2) {
      this.func_75208_c(var2);
      super.func_82870_a(var1, var2);
   }

   protected void func_75210_a(ItemStack var1, int var2) {
      this.field_75228_b += var2;
      this.func_75208_c(var1);
   }

   protected void func_75208_c(ItemStack var1) {
      var1.func_77980_a(this.field_75229_a.field_70170_p, this.field_75229_a, this.field_75228_b);
      if (!this.field_75229_a.field_70170_p.field_72995_K) {
         int var2 = this.field_75228_b;
         float var3 = FurnaceRecipes.func_77602_a().func_151398_b(var1);
         int var4;
         if (var3 == 0.0F) {
            var2 = 0;
         } else if (var3 < 1.0F) {
            var4 = MathHelper.func_76141_d((float)var2 * var3);
            if (var4 < MathHelper.func_76123_f((float)var2 * var3) && Math.random() < (double)((float)var2 * var3 - (float)var4)) {
               ++var4;
            }

            var2 = var4;
         }

         while(var2 > 0) {
            var4 = EntityXPOrb.func_70527_a(var2);
            var2 -= var4;
            this.field_75229_a.field_70170_p.func_72838_d(new EntityXPOrb(this.field_75229_a.field_70170_p, this.field_75229_a.field_70165_t, this.field_75229_a.field_70163_u + 0.5D, this.field_75229_a.field_70161_v + 0.5D, var4));
         }
      }

      this.field_75228_b = 0;
      if (var1.func_77973_b() == Items.field_151042_j) {
         this.field_75229_a.func_71029_a(AchievementList.field_76016_k);
      }

      if (var1.func_77973_b() == Items.field_179566_aV) {
         this.field_75229_a.func_71029_a(AchievementList.field_76026_p);
      }

   }
}
