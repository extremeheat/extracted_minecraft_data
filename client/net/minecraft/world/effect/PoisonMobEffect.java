package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;

class PoisonMobEffect extends MobEffect {
   protected PoisonMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   public boolean applyEffectTick(LivingEntity var1, int var2) {
      if (var1.getHealth() > 1.0F) {
         var1.hurt(var1.damageSources().magic(), 1.0F);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      int var3 = 25 >> var2;
      if (var3 > 0) {
         return var1 % var3 == 0;
      } else {
         return true;
      }
   }
}
