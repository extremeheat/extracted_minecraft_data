package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ThrowableProjectile extends Entity implements Projectile {
   private int xBlock;
   private int yBlock;
   private int zBlock;
   protected boolean inGround;
   public int shakeTime;
   protected LivingEntity owner;
   private UUID ownerId;
   private Entity entityToIgnore;
   private int timeToIgnore;

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> var1, Level var2) {
      super(var1, var2);
      this.xBlock = -1;
      this.yBlock = -1;
      this.zBlock = -1;
   }

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> var1, double var2, double var4, double var6, Level var8) {
      this(var1, var8);
      this.setPos(var2, var4, var6);
   }

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> var1, LivingEntity var2, Level var3) {
      this(var1, var2.x, var2.y + (double)var2.getEyeHeight() - 0.10000000149011612D, var2.z, var3);
      this.owner = var2;
      this.ownerId = var2.getUUID();
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(var3)) {
         var3 = 4.0D;
      }

      var3 *= 64.0D;
      return var1 < var3 * var3;
   }

   public void shootFromRotation(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = -Mth.sin(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      float var8 = -Mth.sin((var2 + var4) * 0.017453292F);
      float var9 = Mth.cos(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      this.shoot((double)var7, (double)var8, (double)var9, var5, var6);
      Vec3 var10 = var1.getDeltaMovement();
      this.setDeltaMovement(this.getDeltaMovement().add(var10.x, var1.onGround ? 0.0D : var10.y, var10.z));
   }

   public void shoot(double var1, double var3, double var5, float var7, float var8) {
      Vec3 var9 = (new Vec3(var1, var3, var5)).normalize().add(this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8, this.random.nextGaussian() * 0.007499999832361937D * (double)var8).scale((double)var7);
      this.setDeltaMovement(var9);
      float var10 = Mth.sqrt(getHorizontalDistanceSqr(var9));
      this.yRot = (float)(Mth.atan2(var9.x, var9.z) * 57.2957763671875D);
      this.xRot = (float)(Mth.atan2(var9.y, (double)var10) * 57.2957763671875D);
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var7 = Mth.sqrt(var1 * var1 + var5 * var5);
         this.yRot = (float)(Mth.atan2(var1, var5) * 57.2957763671875D);
         this.xRot = (float)(Mth.atan2(var3, (double)var7) * 57.2957763671875D);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }

   }

   public void tick() {
      this.xOld = this.x;
      this.yOld = this.y;
      this.zOld = this.z;
      super.tick();
      if (this.shakeTime > 0) {
         --this.shakeTime;
      }

      if (this.inGround) {
         this.inGround = false;
         this.setDeltaMovement(this.getDeltaMovement().multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      }

      AABB var1 = this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D);
      Iterator var2 = this.level.getEntities((Entity)this, var1, (var0) -> {
         return !var0.isSpectator() && var0.isPickable();
      }).iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         if (var3 == this.entityToIgnore) {
            ++this.timeToIgnore;
            break;
         }

         if (this.owner != null && this.tickCount < 2 && this.entityToIgnore == null) {
            this.entityToIgnore = var3;
            this.timeToIgnore = 3;
            break;
         }
      }

      HitResult var8 = ProjectileUtil.getHitResult(this, var1, (var1x) -> {
         return !var1x.isSpectator() && var1x.isPickable() && var1x != this.entityToIgnore;
      }, ClipContext.Block.OUTLINE, true);
      if (this.entityToIgnore != null && this.timeToIgnore-- <= 0) {
         this.entityToIgnore = null;
      }

      if (var8.getType() != HitResult.Type.MISS) {
         if (var8.getType() == HitResult.Type.BLOCK && this.level.getBlockState(((BlockHitResult)var8).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
            this.handleInsidePortal(((BlockHitResult)var8).getBlockPos());
         } else {
            this.onHit(var8);
         }
      }

      Vec3 var9 = this.getDeltaMovement();
      this.x += var9.x;
      this.y += var9.y;
      this.z += var9.z;
      float var4 = Mth.sqrt(getHorizontalDistanceSqr(var9));
      this.yRot = (float)(Mth.atan2(var9.x, var9.z) * 57.2957763671875D);

      for(this.xRot = (float)(Mth.atan2(var9.y, (double)var4) * 57.2957763671875D); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
      }

      while(this.xRot - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }

      while(this.yRot - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while(this.yRot - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      this.xRot = Mth.lerp(0.2F, this.xRotO, this.xRot);
      this.yRot = Mth.lerp(0.2F, this.yRotO, this.yRot);
      float var5;
      if (this.isInWater()) {
         for(int var6 = 0; var6 < 4; ++var6) {
            float var7 = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, this.x - var9.x * 0.25D, this.y - var9.y * 0.25D, this.z - var9.z * 0.25D, var9.x, var9.y, var9.z);
         }

         var5 = 0.8F;
      } else {
         var5 = 0.99F;
      }

      this.setDeltaMovement(var9.scale((double)var5));
      if (!this.isNoGravity()) {
         Vec3 var10 = this.getDeltaMovement();
         this.setDeltaMovement(var10.x, var10.y - (double)this.getGravity(), var10.z);
      }

      this.setPos(this.x, this.y, this.z);
   }

   protected float getGravity() {
      return 0.03F;
   }

   protected abstract void onHit(HitResult var1);

   public void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("xTile", this.xBlock);
      var1.putInt("yTile", this.yBlock);
      var1.putInt("zTile", this.zBlock);
      var1.putByte("shake", (byte)this.shakeTime);
      var1.putByte("inGround", (byte)(this.inGround ? 1 : 0));
      if (this.ownerId != null) {
         var1.put("owner", NbtUtils.createUUIDTag(this.ownerId));
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.xBlock = var1.getInt("xTile");
      this.yBlock = var1.getInt("yTile");
      this.zBlock = var1.getInt("zTile");
      this.shakeTime = var1.getByte("shake") & 255;
      this.inGround = var1.getByte("inGround") == 1;
      this.owner = null;
      if (var1.contains("owner", 10)) {
         this.ownerId = NbtUtils.loadUUIDTag(var1.getCompound("owner"));
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerId != null && this.level instanceof ServerLevel) {
         Entity var1 = ((ServerLevel)this.level).getEntity(this.ownerId);
         if (var1 instanceof LivingEntity) {
            this.owner = (LivingEntity)var1;
         } else {
            this.ownerId = null;
         }
      }

      return this.owner;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
