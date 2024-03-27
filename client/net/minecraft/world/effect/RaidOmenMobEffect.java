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
   public boolean applyEffectTick(LivingEntity var1, int var2) {
      if (var1 instanceof ServerPlayer var3 && !var1.isSpectator()) {
         ServerLevel var4 = ((ServerPlayer)var3).serverLevel();
         BlockPos var5 = ((ServerPlayer)var3).getRaidOmenPosition();
         if (var5 != null) {
            var4.getRaids().createOrExtendRaid((ServerPlayer)var3, var5);
            ((ServerPlayer)var3).clearRaidOmenPosition();
            return false;
         }
      }

      return true;
   }
}
