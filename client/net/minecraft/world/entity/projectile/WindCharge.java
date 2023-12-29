package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class WindCharge extends AbstractHurtingProjectile implements ItemSupplier {
   public static final WindCharge.WindChargeExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new WindCharge.WindChargeExplosionDamageCalculator();

   public WindCharge(EntityType<? extends WindCharge> var1, Level var2) {
      super(var1, var2);
   }

   public WindCharge(EntityType<? extends WindCharge> var1, Breeze var2, Level var3) {
      super(var1, var2.getX(), var2.getSnoutYPosition(), var2.getZ(), var3);
      this.setOwner(var2);
   }

   @Override
   protected AABB makeBoundingBox() {
      float var1 = this.getType().getDimensions().width / 2.0F;
      float var2 = this.getType().getDimensions().height;
      float var3 = 0.15F;
      return new AABB(
         this.position().x - (double)var1,
         this.position().y - 0.15000000596046448,
         this.position().z - (double)var1,
         this.position().x + (double)var1,
         this.position().y - 0.15000000596046448 + (double)var2,
         this.position().z + (double)var1
      );
   }

   @Override
   protected float getEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.0F;
   }

   @Override
   public boolean canCollideWith(Entity var1) {
      return var1 instanceof WindCharge ? false : super.canCollideWith(var1);
   }

   @Override
   protected boolean canHitEntity(Entity var1) {
      return var1 instanceof WindCharge ? false : super.canHitEntity(var1);
   }

   @Override
   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      if (!this.level().isClientSide) {
         Entity var10000 = var1.getEntity();
         Entity var3 = this.getOwner();
         var10000.hurt(this.damageSources().mobProjectile(this, var3 instanceof LivingEntity var2 ? var2 : null), 1.0F);
         this.explode();
      }
   }

   private void explode() {
      this.level()
         .explode(
            this,
            null,
            EXPLOSION_DAMAGE_CALCULATOR,
            this.getX(),
            this.getY(),
            this.getZ(),
            (float)(3.0 + this.random.nextDouble()),
            false,
            Level.ExplosionInteraction.BLOW,
            ParticleTypes.GUST,
            ParticleTypes.GUST_EMITTER,
            SoundEvents.WIND_BURST
         );
   }

   @Override
   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      this.explode();
      this.discard();
   }

   @Override
   protected void onHit(HitResult var1) {
      super.onHit(var1);
      if (!this.level().isClientSide) {
         this.discard();
      }
   }

   @Override
   protected boolean shouldBurn() {
      return false;
   }

   @Override
   public ItemStack getItem() {
      return ItemStack.EMPTY;
   }

   @Override
   protected float getInertia() {
      return 1.0F;
   }

   @Override
   protected float getLiquidInertia() {
      return this.getInertia();
   }

   @Nullable
   @Override
   protected ParticleOptions getTrailParticle() {
      return null;
   }

   @Override
   protected ClipContext.Block getClipType() {
      return ClipContext.Block.OUTLINE;
   }

   public static final class WindChargeExplosionDamageCalculator extends ExplosionDamageCalculator {
      public WindChargeExplosionDamageCalculator() {
         super();
      }

      @Override
      public boolean shouldDamageEntity(Explosion var1, Entity var2) {
         return false;
      }
   }
}
