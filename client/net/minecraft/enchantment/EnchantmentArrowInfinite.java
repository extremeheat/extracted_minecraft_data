package net.minecraft.enchantment;

public class EnchantmentArrowInfinite extends Enchantment {
   public EnchantmentArrowInfinite(int var1, int var2) {
      super(var1, var2, EnumEnchantmentType.bow);
      this.func_77322_b("arrowInfinite");
   }

   @Override
   public int func_77321_a(int var1) {
      return 20;
   }

   @Override
   public int func_77317_b(int var1) {
      return 50;
   }

   @Override
   public int func_77325_b() {
      return 1;
   }
}
