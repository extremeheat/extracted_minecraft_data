package net.minecraft.enchantment;

public class EnchantmentWaterWorker extends Enchantment {
   public EnchantmentWaterWorker(int var1, int var2) {
      super(var1, var2, EnumEnchantmentType.armor_head);
      this.func_77322_b("waterWorker");
   }

   @Override
   public int func_77321_a(int var1) {
      return 1;
   }

   @Override
   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 40;
   }

   @Override
   public int func_77325_b() {
      return 1;
   }
}
