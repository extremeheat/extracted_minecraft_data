package net.minecraft.world.entity.projectile;

import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DragonFireball extends AbstractHurtingProjectile {
   public static final float SPLASH_RANGE = 4.0F;

   public DragonFireball(EntityType<? extends DragonFireball> var1, Level var2) {
      super(var1, var2);
   }

   public DragonFireball(Level var1, LivingEntity var2, double var3, double var5, double var7) {
      super(EntityType.DRAGON_FIREBALL, var2, var3, var5, var7, var1);
   }

   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (var1.getType() != HitResult.Type.ENTITY || !this.ownedBy(((EntityHitResult)var1).getEntity())) {
         if (!this.level().isClientSide) {
            List var2 = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0, 2.0, 4.0));
            AreaEffectCloud var3 = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            Entity var4 = this.getOwner();
            if (var4 instanceof LivingEntity) {
               var3.setOwner((LivingEntity)var4);
            }

            var3.setParticle(ParticleTypes.DRAGON_BREATH);
            var3.setRadius(3.0F);
            var3.setDuration(600);
            var3.setRadiusPerTick((7.0F - var3.getRadius()) / (float)var3.getDuration());
            var3.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            if (!var2.isEmpty()) {
               for(LivingEntity var6 : var2) {
                  double var7 = this.distanceToSqr(var6);
                  if (var7 < 16.0) {
                     var3.setPos(var6.getX(), var6.getY(), var6.getZ());
                     break;
                  }
               }
            }

            this.level().levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
            this.level().addFreshEntity(var3);
            this.discard();
         }
      }
   }

   @Override
   public boolean isPickable() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }

   @Override
   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.DRAGON_BREATH;
   }

   @Override
   protected boolean shouldBurn() {
      return false;
   }
}
