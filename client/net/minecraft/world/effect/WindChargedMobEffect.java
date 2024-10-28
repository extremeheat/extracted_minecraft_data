package net.minecraft.world.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.level.Level;

class WindChargedMobEffect extends MobEffect {
   protected WindChargedMobEffect(MobEffectCategory var1, int var2) {
      super(var1, var2, ParticleTypes.SMALL_GUST);
   }

   public void onMobRemoved(LivingEntity var1, int var2, Entity.RemovalReason var3) {
      if (var3 == Entity.RemovalReason.KILLED) {
         Level var5 = var1.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var5;
            double var12 = var1.getX();
            double var7 = var1.getY() + (double)(var1.getBbHeight() / 2.0F);
            double var9 = var1.getZ();
            float var11 = 3.0F + var1.getRandom().nextFloat() * 2.0F;
            var4.explode(var1, (DamageSource)null, AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR, var12, var7, var9, var11, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.BREEZE_WIND_CHARGE_BURST);
         }
      }

   }
}
