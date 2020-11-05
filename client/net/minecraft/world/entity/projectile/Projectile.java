package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class Projectile extends Entity {
   private UUID ownerUUID;
   private int ownerNetworkId;
   private boolean leftOwner;

   Projectile(EntityType<? extends Projectile> var1, Level var2) {
      super(var1, var2);
   }

   public void setOwner(@Nullable Entity var1) {
      if (var1 != null) {
         this.ownerUUID = var1.getUUID();
         this.ownerNetworkId = var1.getId();
      }

   }

   @Nullable
   public Entity getOwner() {
      if (this.ownerUUID != null && this.level instanceof ServerLevel) {
         return ((ServerLevel)this.level).getEntity(this.ownerUUID);
      } else {
         return this.ownerNetworkId != 0 ? this.level.getEntity(this.ownerNetworkId) : null;
      }
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      if (this.ownerUUID != null) {
         var1.putUUID("Owner", this.ownerUUID);
      }

      if (this.leftOwner) {
         var1.putBoolean("LeftOwner", true);
      }

   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.hasUUID("Owner")) {
         this.ownerUUID = var1.getUUID("Owner");
      }

      this.leftOwner = var1.getBoolean("LeftOwner");
   }

   public void tick() {
      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      super.tick();
   }

   private boolean checkLeftOwner() {
      Entity var1 = this.getOwner();
      if (var1 != null) {
         Iterator var2 = this.level.getEntities((Entity)this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (var0) -> {
            return !var0.isSpectator() && var0.isPickable();
         }).iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            if (var3.getRootVehicle() == var1.getRootVehicle()) {
               return false;
            }
         }
      }

      return true;
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

   public void shootFromRotation(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = -Mth.sin(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      float var8 = -Mth.sin((var2 + var4) * 0.017453292F);
      float var9 = Mth.cos(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      this.shoot((double)var7, (double)var8, (double)var9, var5, var6);
      Vec3 var10 = var1.getDeltaMovement();
      this.setDeltaMovement(this.getDeltaMovement().add(var10.x, var1.isOnGround() ? 0.0D : var10.y, var10.z));
   }

   protected void onHit(HitResult var1) {
      HitResult.Type var2 = var1.getType();
      if (var2 == HitResult.Type.ENTITY) {
         this.onHitEntity((EntityHitResult)var1);
      } else if (var2 == HitResult.Type.BLOCK) {
         this.onHitBlock((BlockHitResult)var1);
      }

   }

   protected void onHitEntity(EntityHitResult var1) {
   }

   protected void onHitBlock(BlockHitResult var1) {
      BlockState var3 = this.level.getBlockState(var1.getBlockPos());
      var3.onProjectileHit(this.level, var3, var1, this);
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var7 = Mth.sqrt(var1 * var1 + var5 * var5);
         this.xRot = (float)(Mth.atan2(var3, (double)var7) * 57.2957763671875D);
         this.yRot = (float)(Mth.atan2(var1, var5) * 57.2957763671875D);
         this.xRotO = this.xRot;
         this.yRotO = this.yRot;
         this.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
      }

   }

   protected boolean canHitEntity(Entity var1) {
      if (!var1.isSpectator() && var1.isAlive() && var1.isPickable()) {
         Entity var2 = this.getOwner();
         return var2 == null || this.leftOwner || !var2.isPassengerOfSameVehicle(var1);
      } else {
         return false;
      }
   }

   protected void updateRotation() {
      Vec3 var1 = this.getDeltaMovement();
      float var2 = Mth.sqrt(getHorizontalDistanceSqr(var1));
      this.xRot = lerpRotation(this.xRotO, (float)(Mth.atan2(var1.y, (double)var2) * 57.2957763671875D));
      this.yRot = lerpRotation(this.yRotO, (float)(Mth.atan2(var1.x, var1.z) * 57.2957763671875D));
   }

   protected static float lerpRotation(float var0, float var1) {
      while(var1 - var0 < -180.0F) {
         var0 -= 360.0F;
      }

      while(var1 - var0 >= 180.0F) {
         var0 += 360.0F;
      }

      return Mth.lerp(0.2F, var0, var1);
   }
}
