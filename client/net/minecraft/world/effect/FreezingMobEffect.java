package net.minecraft.world.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class FreezingMobEffect extends MobEffect {
   public static final int TICKS_ADDED = 1;
   public static final float DAMAGE_VALUE = 1.5F;

   protected FreezingMobEffect(final MobEffectCategory category, final int color) {
      super(category, color, ParticleTypes.FREEZING);
   }

   public boolean applyEffectTick(final ServerLevel serverLevel, final LivingEntity mob, final int amplification) {
      mob.freezeForTicks(1);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(final int tickCount, final int amplification) {
      return true;
   }
}
