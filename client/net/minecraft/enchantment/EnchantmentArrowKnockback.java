package net.minecraft.enchantment;

public class EnchantmentArrowKnockback extends Enchantment {
   public EnchantmentArrowKnockback(int var1, int var2) {
      super(var1, var2, EnumEnchantmentType.bow);
      this.func_77322_b("arrowKnockback");
   }

   @Override
   public int func_77321_a(int var1) {
      return 12 + (var1 - 1) * 20;
   }

   @Override
   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 25;
   }

   @Override
   public int func_77325_b() {
      return 2;
   }
}
