package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
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

   protected AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, double var2, double var4, double var6, Level var8) {
      this(var1, var8);
      this.setPos(var2, var4, var6);
   }

   public AbstractHurtingProjectile(
      EntityType<? extends AbstractHurtingProjectile> var1, double var2, double var4, double var6, double var8, double var10, double var12, Level var14
   ) {
      this(var1, var14);
      this.moveTo(var2, var4, var6, this.getYRot(), this.getXRot());
      this.reapplyPosition();
      this.assignPower(var8, var10, var12);
   }

   public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, LivingEntity var2, double var3, double var5, double var7, Level var9) {
      this(var1, var2.getX(), var2.getY(), var2.getZ(), var3, var5, var7, var9);
      this.setOwner(var2);
      this.setRot(var2.getYRot(), var2.getXRot());
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 4.0;
      if (Double.isNaN(var3)) {
         var3 = 4.0;
      }

      var3 *= 64.0;
      return var1 < var3 * var3;
   }

   protected ClipContext.Block getClipType() {
      return ClipContext.Block.COLLIDER;
   }

   @Override
   public void tick() {
      Entity var1 = this.getOwner();
      if (this.level().isClientSide || (var1 == null || !var1.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
         super.tick();
         if (this.shouldBurn()) {
            this.igniteForSeconds(1);
         }

         HitResult var2 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity, this.getClipType());
         if (var2.getType() != HitResult.Type.MISS) {
            this.onHit(var2);
         }

         this.checkInsideBlocks();
         Vec3 var3 = this.getDeltaMovement();
         double var4 = this.getX() + var3.x;
         double var6 = this.getY() + var3.y;
         double var8 = this.getZ() + var3.z;
         ProjectileUtil.rotateTowardsMovement(this, 0.2F);
         float var10;
         if (this.isInWater()) {
            for(int var11 = 0; var11 < 4; ++var11) {
               float var12 = 0.25F;
               this.level().addParticle(ParticleTypes.BUBBLE, var4 - var3.x * 0.25, var6 - var3.y * 0.25, var8 - var3.z * 0.25, var3.x, var3.y, var3.z);
            }

            var10 = this.getLiquidInertia();
         } else {
            var10 = this.getInertia();
         }

         this.setDeltaMovement(var3.add(this.xPower, this.yPower, this.zPower).scale((double)var10));
         ParticleOptions var13 = this.getTrailParticle();
         if (var13 != null) {
            this.level().addParticle(var13, var4, var6 + 0.5, var8, 0.0, 0.0, 0.0);
         }

         this.setPos(var4, var6, var8);
      } else {
         this.discard();
      }
   }

   @Override
   public void lerpMotion(double var1, double var3, double var5) {
      super.lerpMotion(var1, var3, var5);
      this.assignPower(var1, var3, var5);
   }

   @Override
   protected boolean canHitEntity(Entity var1) {
      return super.canHitEntity(var1) && !var1.noPhysics;
   }

   protected boolean shouldBurn() {
      return true;
   }

   @Nullable
   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.SMOKE;
   }

   protected float getInertia() {
      return 0.95F;
   }

   protected float getLiquidInertia() {
      return 0.8F;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
   }

   @Override
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

   @Override
   public boolean isPickable() {
      return true;
   }

   @Override
   public float getPickRadius() {
      return 1.0F;
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         this.markHurt();
         Entity var3 = var1.getEntity();
         if (var3 != null) {
            if (!this.level().isClientSide) {
               Vec3 var4 = var3.getLookAngle();
               this.setDeltaMovement(var4);
               this.xPower = var4.x * 0.1;
               this.yPower = var4.y * 0.1;
               this.zPower = var4.z * 0.1;
               this.setOwner(var3);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      Entity var1 = this.getOwner();
      int var2 = var1 == null ? 0 : var1.getId();
      return new ClientboundAddEntityPacket(
         this.getId(),
         this.getUUID(),
         this.getX(),
         this.getY(),
         this.getZ(),
         this.getXRot(),
         this.getYRot(),
         this.getType(),
         var2,
         new Vec3(this.xPower, this.yPower, this.zPower),
         0.0
      );
   }

   @Override
   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      double var2 = var1.getXa();
      double var4 = var1.getYa();
      double var6 = var1.getZa();
      this.assignPower(var2, var4, var6);
   }

   private void assignPower(double var1, double var3, double var5) {
      double var7 = Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
      if (var7 != 0.0) {
         this.xPower = var1 / var7 * 0.1;
         this.yPower = var3 / var7 * 0.1;
         this.zPower = var5 / var7 * 0.1;
      }
   }
}
