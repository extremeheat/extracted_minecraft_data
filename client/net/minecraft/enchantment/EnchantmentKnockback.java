package net.minecraft.enchantment;

public class EnchantmentKnockback extends Enchantment {
   protected EnchantmentKnockback(int var1, int var2) {
      super(var1, var2, EnumEnchantmentType.weapon);
      this.func_77322_b("knockback");
   }

   @Override
   public int func_77321_a(int var1) {
      return 5 + 20 * (var1 - 1);
   }

   @Override
   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   @Override
   public int func_77325_b() {
      return 2;
   }
}
