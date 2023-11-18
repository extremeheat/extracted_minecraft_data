package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;

class AbsorptionMobEffect extends MobEffect {
   protected AbsorptionMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   @Override
   public void applyEffectTick(LivingEntity var1, int var2) {
      super.applyEffectTick(var1, var2);
      if (var1.getAbsorptionAmount() <= 0.0F && !var1.level().isClientSide) {
         var1.removeEffect(this);
      }
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return true;
   }

   @Override
   public void onEffectStarted(LivingEntity var1, int var2) {
      super.onEffectStarted(var1, var2);
      var1.setAbsorptionAmount(Math.max(var1.getAbsorptionAmount(), (float)(4 * (1 + var2))));
   }
}
