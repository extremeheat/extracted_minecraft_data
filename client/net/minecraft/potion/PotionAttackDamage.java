package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class PotionAttackDamage extends Potion {
   protected PotionAttackDamage(int var1, boolean var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public double func_111183_a(int var1, AttributeModifier var2) {
      return this.field_76415_H == Potion.field_76437_t.field_76415_H ? (double)(-0.5F * (float)(var1 + 1)) : 1.3 * (double)(var1 + 1);
   }
}
