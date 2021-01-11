package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowDamage extends Enchantment {
   public EnchantmentArrowDamage(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.BOW);
      this.func_77322_b("arrowDamage");
   }

   public int func_77321_a(int var1) {
      return 1 + (var1 - 1) * 10;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 15;
   }

   public int func_77325_b() {
      return 5;
   }
}
