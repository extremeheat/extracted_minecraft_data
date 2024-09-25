package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

class HungerMobEffect extends MobEffect {
   protected HungerMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   @Override
   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      if (var2 instanceof Player var4) {
         var4.causeFoodExhaustion(0.005F * (float)(var3 + 1));
      }

      return true;
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return true;
   }
}
