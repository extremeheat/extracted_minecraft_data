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

   public void onMobRemoved(ServerLevel var1, LivingEntity var2, int var3, Entity.RemovalReason var4) {
      if (var4 == Entity.RemovalReason.KILLED) {
         double var5 = var2.getX();
         double var7 = var2.getY() + (double)(var2.getBbHeight() / 2.0F);
         double var9 = var2.getZ();
         float var11 = 3.0F + var2.getRandom().nextFloat() * 2.0F;
         var1.explode(var2, (DamageSource)null, AbstractWindCharge.EXPLOSION_DAMAGE_CALCULATOR, var5, var7, var9, var11, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.BREEZE_WIND_CHARGE_BURST);
      }

   }
}
