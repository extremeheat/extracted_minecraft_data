package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ThrowableProjectile extends Projectile {
   private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> var1, Level var2) {
      super(var1, var2);
   }

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> var1, double var2, double var4, double var6, Level var8) {
      this(var1, var8);
      this.setPos(var2, var4, var6);
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      if (this.tickCount < 2 && var1 < 12.25) {
         return false;
      } else {
         double var3 = this.getBoundingBox().getSize() * 4.0;
         if (Double.isNaN(var3)) {
            var3 = 4.0;
         }

         var3 *= 64.0;
         return var1 < var3 * var3;
      }
   }

   @Override
   public boolean canUsePortal(boolean var1) {
      return true;
   }

   @Override
   public void tick() {
      super.tick();
      HitResult var1 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      if (var1.getType() != HitResult.Type.MISS) {
         this.hitTargetOrDeflectSelf(var1);
      }

      Vec3 var2 = this.getDeltaMovement();
      double var3 = this.getX() + var2.x;
      double var5 = this.getY() + var2.y;
      double var7 = this.getZ() + var2.z;
      this.updateRotation();
      float var9;
      if (this.isInWater()) {
         for (int var10 = 0; var10 < 4; var10++) {
            float var11 = 0.25F;
            this.level().addParticle(ParticleTypes.BUBBLE, var3 - var2.x * 0.25, var5 - var2.y * 0.25, var7 - var2.z * 0.25, var2.x, var2.y, var2.z);
         }

         var9 = 0.8F;
      } else {
         var9 = 0.99F;
      }

      this.setDeltaMovement(var2.scale((double)var9));
      this.applyGravity();
      this.setPos(var3, var5, var7);
      this.checkInsideBlocks();
   }

   @Override
   protected double getDefaultGravity() {
      return 0.03;
   }
}
