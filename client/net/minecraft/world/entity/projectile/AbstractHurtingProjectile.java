package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHurtingProjectile extends Projectile {
   public static final double INITAL_ACCELERATION_POWER = 0.1;
   public static final double DEFLECTION_SCALE = 0.5;
   public double accelerationPower = 0.1;

   protected AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, Level var2) {
      super(var1, var2);
   }

   protected AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, double var2, double var4, double var6, Level var8) {
      this(var1, var8);
      this.setPos(var2, var4, var6);
   }

   public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, double var2, double var4, double var6, Vec3 var8, Level var9) {
      this(var1, var9);
      this.moveTo(var2, var4, var6, this.getYRot(), this.getXRot());
      this.reapplyPosition();
      this.assignDirectionalMovement(var8, this.accelerationPower);
   }

   public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> var1, LivingEntity var2, Vec3 var3, Level var4) {
      this(var1, var2.getX(), var2.getY(), var2.getZ(), var3, var4);
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
            this.igniteForSeconds(1.0F);
         }

         HitResult var2 = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity, this.getClipType());
         if (var2.getType() != HitResult.Type.MISS) {
            this.hitTargetOrDeflectSelf(var2);
         }

         if (!this.level().isClientSide()) {
            this.applyEffectsFromBlocks();
         }

         Vec3 var3 = this.getDeltaMovement();
         double var4 = this.getX() + var3.x;
         double var6 = this.getY() + var3.y;
         double var8 = this.getZ() + var3.z;
         ProjectileUtil.rotateTowardsMovement(this, 0.2F);
         float var10;
         if (this.isInWater()) {
            for (int var11 = 0; var11 < 4; var11++) {
               float var12 = 0.25F;
               this.level().addParticle(ParticleTypes.BUBBLE, var4 - var3.x * 0.25, var6 - var3.y * 0.25, var8 - var3.z * 0.25, var3.x, var3.y, var3.z);
            }

            var10 = this.getLiquidInertia();
         } else {
            var10 = this.getInertia();
         }

         this.setDeltaMovement(var3.add(var3.normalize().scale(this.accelerationPower)).scale((double)var10));
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
   public boolean hurt(DamageSource var1, float var2) {
      return !this.isInvulnerableTo(var1);
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
      var1.putDouble("acceleration_power", this.accelerationPower);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("acceleration_power", 6)) {
         this.accelerationPower = var1.getDouble("acceleration_power");
      }
   }

   @Override
   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity var1) {
      Entity var2 = this.getOwner();
      int var3 = var2 == null ? 0 : var2.getId();
      Vec3 var4 = var1.getPositionBase();
      return new ClientboundAddEntityPacket(
         this.getId(),
         this.getUUID(),
         var4.x(),
         var4.y(),
         var4.z(),
         var1.getLastSentXRot(),
         var1.getLastSentYRot(),
         this.getType(),
         var3,
         var1.getLastSentMovement(),
         0.0
      );
   }

   @Override
   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      Vec3 var2 = new Vec3(var1.getXa(), var1.getYa(), var1.getZa());
      this.setDeltaMovement(var2);
   }

   private void assignDirectionalMovement(Vec3 var1, double var2) {
      this.setDeltaMovement(var1.normalize().scale(var2));
      this.hasImpulse = true;
   }

   @Override
   protected void onDeflection(@Nullable Entity var1, boolean var2) {
      super.onDeflection(var1, var2);
      if (var2) {
         this.accelerationPower = 0.1;
      } else {
         this.accelerationPower *= 0.5;
      }
   }
}
