package net.minecraft.world.entity.projectile;

import java.util.Iterator;
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
   public DragonFireball(EntityType<? extends DragonFireball> var1, Level var2) {
      super(var1, var2);
   }

   public DragonFireball(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.DRAGON_FIREBALL, var2, var4, var6, var8, var10, var12, var1);
   }

   public DragonFireball(Level var1, LivingEntity var2, double var3, double var5, double var7) {
      super(EntityType.DRAGON_FIREBALL, var2, var3, var5, var7, var1);
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      Entity var2 = this.getOwner();
      if (var1.getType() != HitResult.Type.ENTITY || !((EntityHitResult)var1).getEntity().is(var2)) {
         if (!this.level.isClientSide) {
            List var3 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
            AreaEffectCloud var4 = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            if (var2 instanceof LivingEntity) {
               var4.setOwner((LivingEntity)var2);
            }

            var4.setParticle(ParticleTypes.DRAGON_BREATH);
            var4.setRadius(3.0F);
            var4.setDuration(600);
            var4.setRadiusPerTick((7.0F - var4.getRadius()) / (float)var4.getDuration());
            var4.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            if (!var3.isEmpty()) {
               Iterator var5 = var3.iterator();

               while(var5.hasNext()) {
                  LivingEntity var6 = (LivingEntity)var5.next();
                  double var7 = this.distanceToSqr(var6);
                  if (var7 < 16.0D) {
                     var4.setPos(var6.getX(), var6.getY(), var6.getZ());
                     break;
                  }
               }
            }

            this.level.levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
            this.level.addFreshEntity(var4);
            this.remove();
         }

      }
   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }

   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.DRAGON_BREATH;
   }

   protected boolean shouldBurn() {
      return false;
   }
}
