package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class EnchantmentThorns extends Enchantment {
   public EnchantmentThorns(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.ARMOR_TORSO);
      this.func_77322_b("thorns");
   }

   public int func_77321_a(int var1) {
      return 10 + 20 * (var1 - 1);
   }

   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   public int func_77325_b() {
      return 3;
   }

   public boolean func_92089_a(ItemStack var1) {
      return var1.func_77973_b() instanceof ItemArmor ? true : super.func_92089_a(var1);
   }

   public void func_151367_b(EntityLivingBase var1, Entity var2, int var3) {
      Random var4 = var1.func_70681_au();
      ItemStack var5 = EnchantmentHelper.func_92099_a(Enchantment.field_92091_k, var1);
      if (func_92094_a(var3, var4)) {
         if (var2 != null) {
            var2.func_70097_a(DamageSource.func_92087_a(var1), (float)func_92095_b(var3, var4));
            var2.func_85030_a("damage.thorns", 0.5F, 1.0F);
         }

         if (var5 != null) {
            var5.func_77972_a(3, var1);
         }
      } else if (var5 != null) {
         var5.func_77972_a(1, var1);
      }

   }

   public static boolean func_92094_a(int var0, Random var1) {
      if (var0 <= 0) {
         return false;
      } else {
         return var1.nextFloat() < 0.15F * (float)var0;
      }
   }

   public static int func_92095_b(int var0, Random var1) {
      return var0 > 10 ? var0 - 10 : 1 + var1.nextInt(4);
   }
}
