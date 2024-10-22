package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

class PoisonMobEffect extends MobEffect {
   protected PoisonMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   @Override
   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      if (var2.getHealth() > 1.0F) {
         var2.hurtServer(var1, var2.damageSources().magic(), 1.0F);
      }

      return true;
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      int var3 = 25 >> var2;
      return var3 > 0 ? var1 % var3 == 0 : true;
   }
}
