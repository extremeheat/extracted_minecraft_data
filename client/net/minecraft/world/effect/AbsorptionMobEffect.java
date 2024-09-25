package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

class AbsorptionMobEffect extends MobEffect {
   protected AbsorptionMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   @Override
   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      return var2.getAbsorptionAmount() > 0.0F;
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
