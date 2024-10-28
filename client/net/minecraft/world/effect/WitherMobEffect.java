package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

class WitherMobEffect extends MobEffect {
   protected WitherMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      var2.hurtServer(var1, var2.damageSources().wither(), 1.0F);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      int var3 = 40 >> var2;
      if (var3 > 0) {
         return var1 % var3 == 0;
      } else {
         return true;
      }
   }
}
