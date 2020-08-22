package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHurtingProjectile extends Entity {
   public LivingEntity owner;
   private int life;
   private int flightTime;
   public double xPower;
   public double yPower;
   public double zPower;

   protected AbstractHurtingProjectile(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public AbstractHurtingProjectile(EntityType var1, double var2, double var4, double var6, double var8, double var10, double var12, Level var14) {
      this(var1, var14);
      this.moveTo(var2, var4, var6, this.yRot, this.xRot);
      this.setPos(var2, var4, var6);
      double var15 = (double)Mth.sqrt(var8 * var8 + var10 * var10 + var12 * var12);
      this.xPower = var8 / var15 * 0.1D;
      this.yPower = var10 / var15 * 0.1D;
      this.zPower = var12 / var15 * 0.1D;
   }

   public AbstractHurtingProjectile(EntityType var1, LivingEntity var2, double var3, double var5, double var7, Level var9) {
      this(var1, var9);
      this.owner = var2;
      this.moveTo(var2.getX(), var2.getY(), var2.getZ(), var2.yRot, var2.xRot);
      this.reapplyPosition();
      this.setDeltaMovement(Vec3.ZERO);
      var3 += this.random.nextGaussian() * 0.4D;
      var5 += this.random.nextGaussian() * 0.4D;
      var7 += this.random.nextGaussian() * 0.4D;
      double var10 = (double)Mth.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
      this.xPower = var3 / var10 * 0.1D;
      this.yPower = var5 / var10 * 0.1D;
      this.zPower = var7 / var10 * 0.1D;
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
      if (!this.level.isClientSide && (this.owner != null && this.owner.removed || !this.level.hasChunkAt(new BlockPos(this)))) {
         this.remove();
      } else {
         super.tick();
         if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
         }

         ++this.flightTime;
         HitResult var1 = ProjectileUtil.forwardsRaycast(this, true, this.flightTime >= 25, this.owner, ClipContext.Block.COLLIDER);
         if (var1.getType() != HitResult.Type.MISS) {
            this.onHit(var1);
         }

         Vec3 var2 = this.getDeltaMovement();
         double var3 = this.getX() + var2.x;
         double var5 = this.getY() + var2.y;
         double var7 = this.getZ() + var2.z;
         ProjectileUtil.rotateTowardsMovement(this, 0.2F);
         float var9 = this.getInertia();
         if (this.isInWater()) {
            for(int var10 = 0; var10 < 4; ++var10) {
               float var11 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, var3 - var2.x * 0.25D, var5 - var2.y * 0.25D, var7 - var2.z * 0.25D, var2.x, var2.y, var2.z);
            }

            var9 = 0.8F;
         }

         this.setDeltaMovement(var2.add(this.xPower, this.yPower, this.zPower).scale((double)var9));
         this.level.addParticle(this.getTrailParticle(), var3, var5 + 0.5D, var7, 0.0D, 0.0D, 0.0D);
         this.setPos(var3, var5, var7);
      }
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

   protected void onHit(HitResult var1) {
      HitResult.Type var2 = var1.getType();
      if (var2 == HitResult.Type.BLOCK) {
         BlockHitResult var3 = (BlockHitResult)var1;
         BlockState var4 = this.level.getBlockState(var3.getBlockPos());
         var4.onProjectileHit(this.level, var4, var3, this);
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      Vec3 var2 = this.getDeltaMovement();
      var1.put("direction", this.newDoubleList(new double[]{var2.x, var2.y, var2.z}));
      var1.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
      var1.putInt("life", this.life);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      ListTag var2;
      if (var1.contains("power", 9)) {
         var2 = var1.getList("power", 6);
         if (var2.size() == 3) {
            this.xPower = var2.getDouble(0);
            this.yPower = var2.getDouble(1);
            this.zPower = var2.getDouble(2);
         }
      }

      this.life = var1.getInt("life");
      if (var1.contains("direction", 9) && var1.getList("direction", 6).size() == 3) {
         var2 = var1.getList("direction", 6);
         this.setDeltaMovement(var2.getDouble(0), var2.getDouble(1), var2.getDouble(2));
      } else {
         this.remove();
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
         if (var1.getEntity() != null) {
            Vec3 var3 = var1.getEntity().getLookAngle();
            this.setDeltaMovement(var3);
            this.xPower = var3.x * 0.1D;
            this.yPower = var3.y * 0.1D;
            this.zPower = var3.z * 0.1D;
            if (var1.getEntity() instanceof LivingEntity) {
               this.owner = (LivingEntity)var1.getEntity();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public float getBrightness() {
      return 1.0F;
   }

   public Packet getAddEntityPacket() {
      int var1 = this.owner == null ? 0 : this.owner.getId();
      return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.xRot, this.yRot, this.getType(), var1, new Vec3(this.xPower, this.yPower, this.zPower));
   }
}
