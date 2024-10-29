package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

class RegenerationMobEffect extends MobEffect {
   protected RegenerationMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      if (var2.getHealth() < var2.getMaxHealth()) {
         var2.heal(1.0F);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      int var3 = 50 >> var2;
      if (var3 > 0) {
         return var1 % var3 == 0;
      } else {
         return true;
      }
   }
}
