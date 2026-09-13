package net.minecraft.enchantment;

import net.minecraft.util.DamageSource;

final class EnchantmentHelper$ModifierDamage implements EnchantmentHelper$IModifier {
   public int field_77497_a;
   public DamageSource field_77496_b;

   private EnchantmentHelper$ModifierDamage() {
      super();
   }

   @Override
   public void func_77493_a(Enchantment var1, int var2) {
      this.field_77497_a += var1.func_77318_a(var2, this.field_77496_b);
   }
}
