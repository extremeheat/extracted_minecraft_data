package net.minecraft.world.effect;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AttackDamageMobEffect extends MobEffect {
   protected final double multiplier;

   protected AttackDamageMobEffect(MobEffectCategory var1, int var2, double var3) {
      super(var1, var2);
      this.multiplier = var3;
   }

   public double getAttributeModifierValue(int var1, AttributeModifier var2) {
      return this.multiplier * (double)(var1 + 1);
   }
}
