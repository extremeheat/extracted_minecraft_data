package net.minecraft.enchantment;

import net.minecraft.util.ResourceLocation;

public class EnchantmentArrowKnockback extends Enchantment {
   public EnchantmentArrowKnockback(int var1, ResourceLocation var2, int var3) {
      super(var1, var2, var3, EnumEnchantmentType.BOW);
      this.func_77322_b("arrowKnockback");
   }

   public int func_77321_a(int var1) {
      return 12 + (var1 - 1) * 20;
   }

   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 25;
   }

   public int func_77325_b() {
      return 2;
   }
}
