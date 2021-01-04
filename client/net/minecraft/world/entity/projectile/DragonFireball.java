package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
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
      if (var1.getType() != HitResult.Type.ENTITY || !((EntityHitResult)var1).getEntity().is(this.owner)) {
         if (!this.level.isClientSide) {
            List var2 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
            AreaEffectCloud var3 = new AreaEffectCloud(this.level, this.x, this.y, this.z);
            var3.setOwner(this.owner);
            var3.setParticle(ParticleTypes.DRAGON_BREATH);
            var3.setRadius(3.0F);
            var3.setDuration(600);
            var3.setRadiusPerTick((7.0F - var3.getRadius()) / (float)var3.getDuration());
            var3.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            if (!var2.isEmpty()) {
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  LivingEntity var5 = (LivingEntity)var4.next();
                  double var6 = this.distanceToSqr(var5);
                  if (var6 < 16.0D) {
                     var3.setPos(var5.x, var5.y, var5.z);
                     break;
                  }
               }
            }

            this.level.levelEvent(2006, new BlockPos(this.x, this.y, this.z), 0);
            this.level.addFreshEntity(var3);
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
