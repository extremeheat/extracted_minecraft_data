package net.minecraft.world.entity.projectile.windcharge;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BreezeWindCharge extends AbstractWindCharge {
   private static final float RADIUS = 3.0F;

   public BreezeWindCharge(EntityType<? extends AbstractWindCharge> var1, Level var2) {
      super(var1, var2);
   }

   public BreezeWindCharge(Breeze var1, Level var2) {
      super(EntityType.BREEZE_WIND_CHARGE, var2, var1, var1.getX(), var1.getSnoutYPosition(), var1.getZ());
   }

   protected void explode(Vec3 var1) {
      this.level().explode(this, (DamageSource)null, EXPLOSION_DAMAGE_CALCULATOR, var1.x(), var1.y(), var1.z(), 3.0F, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.BREEZE_WIND_CHARGE_BURST);
   }
}
