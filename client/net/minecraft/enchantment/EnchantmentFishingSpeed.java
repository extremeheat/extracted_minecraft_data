package net.minecraft.enchantment;

public class EnchantmentFishingSpeed extends Enchantment {
   protected EnchantmentFishingSpeed(int var1, int var2, EnumEnchantmentType var3) {
      super(var1, var2, var3);
      this.func_77322_b("fishingSpeed");
   }

   @Override
   public int func_77321_a(int var1) {
      return 15 + (var1 - 1) * 9;
   }

   @Override
   public int func_77317_b(int var1) {
      return super.func_77321_a(var1) + 50;
   }

   @Override
   public int func_77325_b() {
      return 3;
   }
}
