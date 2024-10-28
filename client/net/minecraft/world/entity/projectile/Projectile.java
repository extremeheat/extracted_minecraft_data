package net.minecraft.world.entity.projectile;

import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class Projectile extends Entity implements TraceableEntity {
   @Nullable
   private UUID ownerUUID;
   @Nullable
   private Entity cachedOwner;
   private boolean leftOwner;
   private boolean hasBeenShot;
   @Nullable
   private Entity lastDeflectedBy;

   Projectile(EntityType<? extends Projectile> var1, Level var2) {
      super(var1, var2);
   }

   public void setOwner(@Nullable Entity var1) {
      if (var1 != null) {
         this.ownerUUID = var1.getUUID();
         this.cachedOwner = var1;
      }

   }

   protected void disown() {
      this.ownerUUID = null;
      this.cachedOwner = null;
   }

   @Nullable
   public Entity getOwner() {
      if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
         return this.cachedOwner;
      } else {
         if (this.ownerUUID != null) {
            Level var2 = this.level();
            if (var2 instanceof ServerLevel) {
               ServerLevel var1 = (ServerLevel)var2;
               this.cachedOwner = var1.getEntity(this.ownerUUID);
               return this.cachedOwner;
            }
         }

         return null;
      }
   }

   public Entity getEffectSource() {
      return (Entity)MoreObjects.firstNonNull(this.getOwner(), this);
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      if (this.ownerUUID != null) {
         var1.putUUID("Owner", this.ownerUUID);
      }

      if (this.leftOwner) {
         var1.putBoolean("LeftOwner", true);
      }

      var1.putBoolean("HasBeenShot", this.hasBeenShot);
   }

   protected boolean ownedBy(Entity var1) {
      return var1.getUUID().equals(this.ownerUUID);
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      if (var1.hasUUID("Owner")) {
         this.ownerUUID = var1.getUUID("Owner");
         this.cachedOwner = null;
      }

      this.leftOwner = var1.getBoolean("LeftOwner");
      this.hasBeenShot = var1.getBoolean("HasBeenShot");
   }

   public void restoreFrom(Entity var1) {
      super.restoreFrom(var1);
      if (var1 instanceof Projectile var2) {
         this.cachedOwner = var2.cachedOwner;
      }

   }

   public void tick() {
      if (!this.hasBeenShot) {
         this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
         this.hasBeenShot = true;
      }

      if (!this.leftOwner) {
         this.leftOwner = this.checkLeftOwner();
      }

      super.tick();
   }

   private boolean checkLeftOwner() {
      Entity var1 = this.getOwner();
      if (var1 != null) {
         Iterator var2 = this.level().getEntities((Entity)this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0), (var0) -> {
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

   public Vec3 getMovementToShoot(double var1, double var3, double var5, float var7, float var8) {
      return (new Vec3(var1, var3, var5)).normalize().add(this.random.triangle(0.0, 0.0172275 * (double)var8), this.random.triangle(0.0, 0.0172275 * (double)var8), this.random.triangle(0.0, 0.0172275 * (double)var8)).scale((double)var7);
   }

   public void shoot(double var1, double var3, double var5, float var7, float var8) {
      Vec3 var9 = this.getMovementToShoot(var1, var3, var5, var7, var8);
      this.setDeltaMovement(var9);
      this.hasImpulse = true;
      double var10 = var9.horizontalDistance();
      this.setYRot((float)(Mth.atan2(var9.x, var9.z) * 57.2957763671875));
      this.setXRot((float)(Mth.atan2(var9.y, var10) * 57.2957763671875));
      this.yRotO = this.getYRot();
      this.xRotO = this.getXRot();
   }

   public void shootFromRotation(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = -Mth.sin(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      float var8 = -Mth.sin((var2 + var4) * 0.017453292F);
      float var9 = Mth.cos(var3 * 0.017453292F) * Mth.cos(var2 * 0.017453292F);
      this.shoot((double)var7, (double)var8, (double)var9, var5, var6);
      Vec3 var10 = var1.getKnownMovement();
      this.setDeltaMovement(this.getDeltaMovement().add(var10.x, var1.onGround() ? 0.0 : var10.y, var10.z));
   }

   protected ProjectileDeflection hitTargetOrDeflectSelf(HitResult var1) {
      if (var1.getType() == HitResult.Type.ENTITY) {
         EntityHitResult var2 = (EntityHitResult)var1;
         Entity var3 = var2.getEntity();
         ProjectileDeflection var4 = var3.deflection(this);
         if (var4 != ProjectileDeflection.NONE) {
            if (var3 != this.lastDeflectedBy && this.deflect(var4, var3, this.getOwner(), false)) {
               this.lastDeflectedBy = var3;
            }

            return var4;
         }
      }

      this.onHit(var1);
      return ProjectileDeflection.NONE;
   }

   public boolean deflect(ProjectileDeflection var1, @Nullable Entity var2, @Nullable Entity var3, boolean var4) {
      if (!this.level().isClientSide) {
         var1.deflect(this, var2, this.random);
         this.setOwner(var3);
         this.onDeflection(var2, var4);
      }

      return true;
   }

   protected void onDeflection(@Nullable Entity var1, boolean var2) {
   }

   protected void onHit(HitResult var1) {
      HitResult.Type var2 = var1.getType();
      if (var2 == HitResult.Type.ENTITY) {
         EntityHitResult var3 = (EntityHitResult)var1;
         Entity var4 = var3.getEntity();
         if (var4.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && var4 instanceof Projectile) {
            Projectile var5 = (Projectile)var4;
            var5.deflect(ProjectileDeflection.AIM_DEFLECT, this.getOwner(), this.getOwner(), true);
         }

         this.onHitEntity(var3);
         this.level().gameEvent(GameEvent.PROJECTILE_LAND, var1.getLocation(), GameEvent.Context.of(this, (BlockState)null));
      } else if (var2 == HitResult.Type.BLOCK) {
         BlockHitResult var6 = (BlockHitResult)var1;
         this.onHitBlock(var6);
         BlockPos var7 = var6.getBlockPos();
         this.level().gameEvent(GameEvent.PROJECTILE_LAND, var7, GameEvent.Context.of(this, this.level().getBlockState(var7)));
      }

   }

   protected void onHitEntity(EntityHitResult var1) {
   }

   protected void onHitBlock(BlockHitResult var1) {
      BlockState var2 = this.level().getBlockState(var1.getBlockPos());
      var2.onProjectileHit(this.level(), var2, var1, this);
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double var7 = Math.sqrt(var1 * var1 + var5 * var5);
         this.setXRot((float)(Mth.atan2(var3, var7) * 57.2957763671875));
         this.setYRot((float)(Mth.atan2(var1, var5) * 57.2957763671875));
         this.xRotO = this.getXRot();
         this.yRotO = this.getYRot();
         this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
      }

   }

   protected boolean canHitEntity(Entity var1) {
      if (!var1.canBeHitByProjectile()) {
         return false;
      } else {
         Entity var2 = this.getOwner();
         return var2 == null || this.leftOwner || !var2.isPassengerOfSameVehicle(var1);
      }
   }

   protected void updateRotation() {
      Vec3 var1 = this.getDeltaMovement();
      double var2 = var1.horizontalDistance();
      this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(var1.y, var2) * 57.2957763671875)));
      this.setYRot(lerpRotation(this.yRotO, (float)(Mth.atan2(var1.x, var1.z) * 57.2957763671875)));
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

   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      Entity var1 = this.getOwner();
      return new ClientboundAddEntityPacket(this, var1 == null ? 0 : var1.getId());
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      Entity var2 = this.level().getEntity(var1.getData());
      if (var2 != null) {
         this.setOwner(var2);
      }

   }

   public boolean mayInteract(Level var1, BlockPos var2) {
      Entity var3 = this.getOwner();
      if (var3 instanceof Player) {
         return var3.mayInteract(var1, var2);
      } else {
         return var3 == null || var1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
      }
   }

   public boolean mayBreak(Level var1) {
      return this.getType().is(EntityTypeTags.IMPACT_PROJECTILES) && var1.getGameRules().getBoolean(GameRules.RULE_PROJECTILESCANBREAKBLOCKS);
   }

   public boolean isPickable() {
      return this.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE);
   }

   public float getPickRadius() {
      return this.isPickable() ? 1.0F : 0.0F;
   }

   public DoubleDoubleImmutablePair calculateHorizontalHurtKnockbackDirection(LivingEntity var1, DamageSource var2) {
      double var3 = this.getDeltaMovement().x;
      double var5 = this.getDeltaMovement().z;
      return DoubleDoubleImmutablePair.of(var3, var5);
   }
}
