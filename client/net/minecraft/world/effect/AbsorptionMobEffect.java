package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;

class AbsorptionMobEffect extends MobEffect {
   protected AbsorptionMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   public boolean applyEffectTick(LivingEntity var1, int var2) {
      return var1.getAbsorptionAmount() > 0.0F || var1.level().isClientSide;
   }

   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return true;
   }

   public void onEffectStarted(LivingEntity var1, int var2) {
      super.onEffectStarted(var1, var2);
      var1.setAbsorptionAmount(Math.max(var1.getAbsorptionAmount(), (float)(4 * (1 + var2))));
   }
}
