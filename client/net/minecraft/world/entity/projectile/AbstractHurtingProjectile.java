package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHurtingProjectile extends Projectile {
   public double xPower;
   public double yPower;
   public double zPower;

   protected AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, Level var2) {
      super(var1, var2);
   }

   public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, double var2, double var4, double var6, double var8, double var10, double var12, Level var14) {
      this(var1, var14);
      this.moveTo(var2, var4, var6, this.yRot, this.xRot);
      this.reapplyPosition();
      double var15 = (double)Mth.sqrt(var8 * var8 + var10 * var10 + var12 * var12);
      if (var15 != 0.0D) {
         this.xPower = var8 / var15 * 0.1D;
         this.yPower = var10 / var15 * 0.1D;
         this.zPower = var12 / var15 * 0.1D;
      }

   }

   public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, LivingEntity var2, double var3, double var5, double var7, Level var9) {
      this(var1, var2.getX(), var2.getY(), var2.getZ(), var3, var5, var7, var9);
      this.setOwner(var2);
      this.setRot(var2.yRot, var2.xRot);
   }

   protected void defineSynchedData() {
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
      Entity var1 = this.getOwner();
      if (this.level.isClientSide || (var1 == null || !var1.removed) && this.level.hasChunkAt(this.blockPosition())) {
         super.tick();
         if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
         }

         HitResult var2 = ProjectileUtil.getHitResult(this, this::canHitEntity);
         if (var2.getType() != HitResult.Type.MISS) {
            this.onHit(var2);
         }

         this.checkInsideBlocks();
         Vec3 var3 = this.getDeltaMovement();
         double var4 = this.getX() + var3.x;
         double var6 = this.getY() + var3.y;
         double var8 = this.getZ() + var3.z;
         ProjectileUtil.rotateTowardsMovement(this, 0.2F);
         float var10 = this.getInertia();
         if (this.isInWater()) {
            for(int var11 = 0; var11 < 4; ++var11) {
               float var12 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, var4 - var3.x * 0.25D, var6 - var3.y * 0.25D, var8 - var3.z * 0.25D, var3.x, var3.y, var3.z);
            }

            var10 = 0.8F;
         }

         this.setDeltaMovement(var3.add(this.xPower, this.yPower, this.zPower).scale((double)var10));
         this.level.addParticle(this.getTrailParticle(), var4, var6 + 0.5D, var8, 0.0D, 0.0D, 0.0D);
         this.setPos(var4, var6, var8);
      } else {
         this.remove();
      }
   }

   protected boolean canHitEntity(Entity var1) {
      return super.canHitEntity(var1) && !var1.noPhysics;
   }

   protected boolean shouldBurn() {
      return true;
   }

   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.SMOKE;
   }

   protected float getInertia() {
      return 0.95F;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("power", 9)) {
         ListTag var2 = var1.getList("power", 6);
         if (var2.size() == 3) {
            this.xPower = var2.getDouble(0);
            this.yPower = var2.getDouble(1);
            this.zPower = var2.getDouble(2);
         }
      }

   }

   public boolean isPickable() {
      return true;
   }

   public float getPickRadius() {
      return 1.0F;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         this.markHurt();
         Entity var3 = var1.getEntity();
         if (var3 != null) {
            Vec3 var4 = var3.getLookAngle();
            this.setDeltaMovement(var4);
            this.xPower = var4.x * 0.1D;
            this.yPower = var4.y * 0.1D;
            this.zPower = var4.z * 0.1D;
            this.setOwner(var3);
            return true;
         } else {
            return false;
         }
      }
   }

   public float getBrightness() {
      return 1.0F;
   }

   public Packet<?> getAddEntityPacket() {
      Entity var1 = this.getOwner();
      int var2 = var1 == null ? 0 : var1.getId();
      return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.xRot, this.yRot, this.getType(), var2, new Vec3(this.xPower, this.yPower, this.zPower));
   }
}
