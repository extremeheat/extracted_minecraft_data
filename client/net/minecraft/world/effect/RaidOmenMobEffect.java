package net.minecraft.world.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

class RaidOmenMobEffect extends MobEffect {
   protected RaidOmenMobEffect(MobEffectCategory var1, int var2, ParticleOptions var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean shouldApplyEffectTickThisTick(int var1, int var2) {
      return var1 == 1;
   }

   @Override
   public boolean applyEffectTick(ServerLevel var1, LivingEntity var2, int var3) {
      if (var2 instanceof ServerPlayer var4 && !var2.isSpectator()) {
         BlockPos var5 = var4.getRaidOmenPosition();
         if (var5 != null) {
            var1.getRaids().createOrExtendRaid(var4, var5);
            var4.clearRaidOmenPosition();
            return false;
         }
      }

      return true;
   }
}
