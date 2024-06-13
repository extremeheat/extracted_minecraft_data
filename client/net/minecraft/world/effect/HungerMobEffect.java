package net.minecraft.world.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

class HungerMobEffect extends MobEffect {
   protected HungerMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   @Override
   public boolean applyEffectTick(LivingEntity var1, int var2) {
      if (var1 instanceof Player var3) {
         var3.causeFoodExhaustion(0.005F * (float)(var2 + 1));
      }

      return true;
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return true;
   }
}
