package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ThrowableProjectile extends Projectile {
   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> var1, Level var2) {
      super(var1, var2);
   }

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> var1, double var2, double var4, double var6, Level var8) {
      this(var1, var8);
      this.setPos(var2, var4, var6);
   }

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> var1, LivingEntity var2, Level var3) {
      this(var1, var2.getX(), var2.getEyeY() - 0.10000000149011612D, var2.getZ(), var3);
      this.setOwner(var2);
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(var3)) {
         var3 = 4.0D;
      }

      var3 *= 64.0D;
      return var1 < var3 * var3;
   }

   public void tick() {
      super.tick();
      HitResult var1 = ProjectileUtil.getHitResult(this, this::canHitEntity);
      boolean var2 = false;
      if (var1.getType() == HitResult.Type.BLOCK) {
         BlockPos var3 = ((BlockHitResult)var1).getBlockPos();
         BlockState var4 = this.level.getBlockState(var3);
         if (var4.is(Blocks.NETHER_PORTAL)) {
            this.handleInsidePortal(var3);
            var2 = true;
         } else if (var4.is(Blocks.END_GATEWAY)) {
            BlockEntity var5 = this.level.getBlockEntity(var3);
            if (var5 instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
               TheEndGatewayBlockEntity.teleportEntity(this.level, var3, var4, this, (TheEndGatewayBlockEntity)var5);
            }

            var2 = true;
         }
      }

      if (var1.getType() != HitResult.Type.MISS && !var2) {
         this.onHit(var1);
      }

      this.checkInsideBlocks();
      Vec3 var13 = this.getDeltaMovement();
      double var14 = this.getX() + var13.x;
      double var6 = this.getY() + var13.y;
      double var8 = this.getZ() + var13.z;
      this.updateRotation();
      float var10;
      if (this.isInWater()) {
         for(int var11 = 0; var11 < 4; ++var11) {
            float var12 = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, var14 - var13.x * 0.25D, var6 - var13.y * 0.25D, var8 - var13.z * 0.25D, var13.x, var13.y, var13.z);
         }

         var10 = 0.8F;
      } else {
         var10 = 0.99F;
      }

      this.setDeltaMovement(var13.scale((double)var10));
      if (!this.isNoGravity()) {
         Vec3 var15 = this.getDeltaMovement();
         this.setDeltaMovement(var15.x, var15.y - (double)this.getGravity(), var15.z);
      }

      this.setPos(var14, var6, var8);
   }

   protected float getGravity() {
      return 0.03F;
   }
}
