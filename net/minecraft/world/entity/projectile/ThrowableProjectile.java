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

   protected ThrowableProjectile(EntityType var1, Level var2) {
      super(var1, var2);
      this.xBlock = -1;
      this.yBlock = -1;
      this.zBlock = -1;
   }

   protected ThrowableProjectile(EntityType var1, double var2, double var4, double var6, Level var8) {
      this(var1, var8);
      this.setPos(var2, var4, var6);
   }

   protected ThrowableProjectile(EntityType var1, LivingEntity var2, Level var3) {
      this(var1, var2.getX(), var2.getEyeY() - 0.10000000149011612D, var2.getZ(), var3);
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

      HitResult var14 = ProjectileUtil.getHitResult(this, var1, (var1x) -> {
         return !var1x.isSpectator() && var1x.isPickable() && var1x != this.entityToIgnore;
      }, ClipContext.Block.OUTLINE, true);
      if (this.entityToIgnore != null && this.timeToIgnore-- <= 0) {
         this.entityToIgnore = null;
      }

      if (var14.getType() != HitResult.Type.MISS) {
         if (var14.getType() == HitResult.Type.BLOCK && this.level.getBlockState(((BlockHitResult)var14).getBlockPos()).getBlock() == Blocks.NETHER_PORTAL) {
            this.handleInsidePortal(((BlockHitResult)var14).getBlockPos());
         } else {
            this.onHit(var14);
         }
      }

      Vec3 var15 = this.getDeltaMovement();
      double var4 = this.getX() + var15.x;
      double var6 = this.getY() + var15.y;
      double var8 = this.getZ() + var15.z;
      float var10 = Mth.sqrt(getHorizontalDistanceSqr(var15));
      this.yRot = (float)(Mth.atan2(var15.x, var15.z) * 57.2957763671875D);

      for(this.xRot = (float)(Mth.atan2(var15.y, (double)var10) * 57.2957763671875D); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
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
      float var11;
      if (this.isInWater()) {
         for(int var12 = 0; var12 < 4; ++var12) {
            float var13 = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, var4 - var15.x * 0.25D, var6 - var15.y * 0.25D, var8 - var15.z * 0.25D, var15.x, var15.y, var15.z);
         }

         var11 = 0.8F;
      } else {
         var11 = 0.99F;
      }

      this.setDeltaMovement(var15.scale((double)var11));
      if (!this.isNoGravity()) {
         Vec3 var16 = this.getDeltaMovement();
         this.setDeltaMovement(var16.x, var16.y - (double)this.getGravity(), var16.z);
      }

      this.setPos(var4, var6, var8);
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
      var1.putBoolean("inGround", this.inGround);
      if (this.ownerId != null) {
         var1.put("owner", NbtUtils.createUUIDTag(this.ownerId));
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.xBlock = var1.getInt("xTile");
      this.yBlock = var1.getInt("yTile");
      this.zBlock = var1.getInt("zTile");
      this.shakeTime = var1.getByte("shake") & 255;
      this.inGround = var1.getBoolean("inGround");
      this.owner = null;
      if (var1.contains("owner", 10)) {
         this.ownerId = NbtUtils.loadUUIDTag(var1.getCompound("owner"));
      }

   }

   @Nullable
   public LivingEntity getOwner() {
      if ((this.owner == null || this.owner.removed) && this.ownerId != null && this.level instanceof ServerLevel) {
         Entity var1 = ((ServerLevel)this.level).getEntity(this.ownerId);
         if (var1 instanceof LivingEntity) {
            this.owner = (LivingEntity)var1;
         } else {
            this.owner = null;
         }
      }

      return this.owner;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
