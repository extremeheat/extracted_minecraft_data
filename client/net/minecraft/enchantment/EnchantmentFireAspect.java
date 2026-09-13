package net.minecraft.enchantment;

public class EnchantmentFireAspect extends Enchantment {
   protected EnchantmentFireAspect(int var1, int var2) {
      super(var1, var2, EnumEnchantmentType.weapon);
      this.func_77322_b("fire");
   }

   @Override
   public int func_77321_a(int var1) {
      return 10 + 20 * (var1 - 1);
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
