package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

class SaturationMobEffect extends InstantenousMobEffect {
   protected SaturationMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2);
   }

   @Override
   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      if (var2 instanceof Player var4) {
         var4.getFoodData().eat(var3 + 1, 1.0F);
      }

      return true;
   }
}
