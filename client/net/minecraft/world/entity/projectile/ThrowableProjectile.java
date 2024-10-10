package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
      this.handleFirstTickBubbleColumn();
      this.applyGravity();
      this.applyInertia();
      HitResult var1 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
      Vec3 var2;
      if (var1.getType() != HitResult.Type.MISS) {
         var2 = var1.getLocation();
      } else {
         var2 = this.position().add(this.getDeltaMovement());
      }

      this.setPos(var2);
      this.updateRotation();
      this.applyEffectsFromBlocks();
      super.tick();
      if (var1.getType() != HitResult.Type.MISS && this.isAlive()) {
         this.hitTargetOrDeflectSelf(var1);
      }
   }

   private void applyInertia() {
      Vec3 var1 = this.getDeltaMovement();
      Vec3 var2 = this.position();
      float var3;
      if (this.isInWater()) {
         for (int var4 = 0; var4 < 4; var4++) {
            float var5 = 0.25F;
            this.level().addParticle(ParticleTypes.BUBBLE, var2.x - var1.x * 0.25, var2.y - var1.y * 0.25, var2.z - var1.z * 0.25, var1.x, var1.y, var1.z);
         }

         var3 = 0.8F;
      } else {
         var3 = 0.99F;
      }

      this.setDeltaMovement(var1.scale((double)var3));
   }

   private void handleFirstTickBubbleColumn() {
      if (this.firstTick) {
         for (BlockPos var2 : BlockPos.betweenClosed(this.getBoundingBox())) {
            BlockState var3 = this.level().getBlockState(var2);
            if (var3.is(Blocks.BUBBLE_COLUMN)) {
               var3.entityInside(this.level(), var2, this);
            }
         }
      }
   }

   @Override
   protected double getDefaultGravity() {
      return 0.03;
   }
}
