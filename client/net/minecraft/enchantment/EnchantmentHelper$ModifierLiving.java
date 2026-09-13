package net.minecraft.enchantment;

import net.minecraft.entity.EnumCreatureAttribute;

final class EnchantmentHelper$ModifierLiving implements EnchantmentHelper$IModifier {
   public float field_77495_a;
   public EnumCreatureAttribute field_77494_b;

   private EnchantmentHelper$ModifierLiving() {
      super();
   }

   @Override
   public void func_77493_a(Enchantment var1, int var2) {
      this.field_77495_a += var1.func_152376_a(var2, this.field_77494_b);
   }
}
