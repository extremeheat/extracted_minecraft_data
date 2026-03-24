package net.minecraft.world.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class PoisonMobEffect extends MobEffect {
   public static final int DAMAGE_INTERVAL = 25;

   protected PoisonMobEffect(final MobEffectCategory category, final int color) {
      super(category, color);
   }

   public boolean applyEffectTick(final ServerLevel level, final LivingEntity mob, final int amplification) {
      if (mob.getHealth() > 1.0F) {
         mob.hurtServer(level, mob.damageSources().magic(), 1.0F);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(final int tickCount, final int amplification) {
      int interval = 25 >> amplification;
      if (interval > 0) {
         return tickCount % interval == 0;
      } else {
         return true;
      }
   }
}
