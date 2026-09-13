package net.minecraft.enchantment;

public class EnchantmentOxygen extends Enchantment {
   public EnchantmentOxygen(int var1, int var2) {
      super(var1, var2, EnumEnchantmentType.armor_head);
      this.func_77322_b("oxygen");
   }

   @Override
   public int func_77321_a(int var1) {
      return 10 * var1;
   }

   @Override
   public int func_77317_b(int var1) {
      return this.func_77321_a(var1) + 30;
   }

   @Override
   public int func_77325_b() {
      return 3;
   }
}
