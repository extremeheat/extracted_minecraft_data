package net.minecraft.enchantment;

public class EnchantmentArrowDamage extends Enchantment {
   public EnchantmentArrowDamage(int var1, int var2) {
      super(var1, var2, EnumEnchantmentType.bow);
      this.func_77322_b("arrowDamage");
   }

   @Override
   public int func_77321_a(int var1) {
      return 1 + (var1 - 1) * 10;
   }

   @Override
   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 15;
   }

   @Override
   public int func_77325_b() {
      return 5;
   }
}
