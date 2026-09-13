package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

final class EnchantmentHelper$DamageIterator implements EnchantmentHelper$IModifier {
   public EntityLivingBase field_151366_a;
   public Entity field_151365_b;

   private EnchantmentHelper$DamageIterator() {
      super();
   }

   @Override
   public void func_77493_a(Enchantment var1, int var2) {
      var1.func_151368_a(this.field_151366_a, this.field_151365_b, var2);
   }
}
