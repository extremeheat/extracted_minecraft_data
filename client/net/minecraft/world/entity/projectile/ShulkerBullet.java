package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBullet extends Entity {
   private LivingEntity owner;
   private Entity finalTarget;
   @Nullable
   private Direction currentMoveDirection;
   private int flightSteps;
   private double targetDeltaX;
   private double targetDeltaY;
   private double targetDeltaZ;
   @Nullable
   private UUID ownerId;
   private BlockPos lastKnownOwnerPos;
   @Nullable
   private UUID targetId;
   private BlockPos lastKnownTargetPos;

   public ShulkerBullet(EntityType<? extends ShulkerBullet> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   public ShulkerBullet(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(EntityType.SHULKER_BULLET, var1);
      this.moveTo(var2, var4, var6, this.yRot, this.xRot);
      this.setDeltaMovement(var8, var10, var12);
   }

   public ShulkerBullet(Level var1, LivingEntity var2, Entity var3, Direction.Axis var4) {
      this(EntityType.SHULKER_BULLET, var1);
      this.owner = var2;
      BlockPos var5 = new BlockPos(var2);
      double var6 = (double)var5.getX() + 0.5D;
      double var8 = (double)var5.getY() + 0.5D;
      double var10 = (double)var5.getZ() + 0.5D;
      this.moveTo(var6, var8, var10, this.yRot, this.xRot);
      this.finalTarget = var3;
      this.currentMoveDirection = Direction.UP;
      this.selectNextMoveDirection(var4);
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      BlockPos var2;
      CompoundTag var3;
      if (this.owner != null) {
         var2 = new BlockPos(this.owner);
         var3 = NbtUtils.createUUIDTag(this.owner.getUUID());
         var3.putInt("X", var2.getX());
         var3.putInt("Y", var2.getY());
         var3.putInt("Z", var2.getZ());
         var1.put("Owner", var3);
      }

      if (this.finalTarget != null) {
         var2 = new BlockPos(this.finalTarget);
         var3 = NbtUtils.createUUIDTag(this.finalTarget.getUUID());
         var3.putInt("X", var2.getX());
         var3.putInt("Y", var2.getY());
         var3.putInt("Z", var2.getZ());
         var1.put("Target", var3);
      }

      if (this.currentMoveDirection != null) {
         var1.putInt("Dir", this.currentMoveDirection.get3DDataValue());
      }

      var1.putInt("Steps", this.flightSteps);
      var1.putDouble("TXD", this.targetDeltaX);
      var1.putDouble("TYD", this.targetDeltaY);
      var1.putDouble("TZD", this.targetDeltaZ);
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      this.flightSteps = var1.getInt("Steps");
      this.targetDeltaX = var1.getDouble("TXD");
      this.targetDeltaY = var1.getDouble("TYD");
      this.targetDeltaZ = var1.getDouble("TZD");
      if (var1.contains("Dir", 99)) {
         this.currentMoveDirection = Direction.from3DDataValue(var1.getInt("Dir"));
      }

      CompoundTag var2;
      if (var1.contains("Owner", 10)) {
         var2 = var1.getCompound("Owner");
         this.ownerId = NbtUtils.loadUUIDTag(var2);
         this.lastKnownOwnerPos = new BlockPos(var2.getInt("X"), var2.getInt("Y"), var2.getInt("Z"));
      }

      if (var1.contains("Target", 10)) {
         var2 = var1.getCompound("Target");
         this.targetId = NbtUtils.loadUUIDTag(var2);
         this.lastKnownTargetPos = new BlockPos(var2.getInt("X"), var2.getInt("Y"), var2.getInt("Z"));
      }

   }

   protected void defineSynchedData() {
   }

   private void setMoveDirection(@Nullable Direction var1) {
      this.currentMoveDirection = var1;
   }

   private void selectNextMoveDirection(@Nullable Direction.Axis var1) {
      double var3 = 0.5D;
      BlockPos var2;
      if (this.finalTarget == null) {
         var2 = (new BlockPos(this)).below();
      } else {
         var3 = (double)this.finalTarget.getBbHeight() * 0.5D;
         var2 = new BlockPos(this.finalTarget.x, this.finalTarget.y + var3, this.finalTarget.z);
      }

      double var5 = (double)var2.getX() + 0.5D;
      double var7 = (double)var2.getY() + var3;
      double var9 = (double)var2.getZ() + 0.5D;
      Direction var11 = null;
      if (!var2.closerThan(this.position(), 2.0D)) {
         BlockPos var12 = new BlockPos(this);
         ArrayList var13 = Lists.newArrayList();
         if (var1 != Direction.Axis.X) {
            if (var12.getX() < var2.getX() && this.level.isEmptyBlock(var12.east())) {
               var13.add(Direction.EAST);
            } else if (var12.getX() > var2.getX() && this.level.isEmptyBlock(var12.west())) {
               var13.add(Direction.WEST);
            }
         }

         if (var1 != Direction.Axis.Y) {
            if (var12.getY() < var2.getY() && this.level.isEmptyBlock(var12.above())) {
               var13.add(Direction.UP);
            } else if (var12.getY() > var2.getY() && this.level.isEmptyBlock(var12.below())) {
               var13.add(Direction.DOWN);
            }
         }

         if (var1 != Direction.Axis.Z) {
            if (var12.getZ() < var2.getZ() && this.level.isEmptyBlock(var12.south())) {
               var13.add(Direction.SOUTH);
            } else if (var12.getZ() > var2.getZ() && this.level.isEmptyBlock(var12.north())) {
               var13.add(Direction.NORTH);
            }
         }

         var11 = Direction.getRandomFace(this.random);
         if (var13.isEmpty()) {
            for(int var14 = 5; !this.level.isEmptyBlock(var12.relative(var11)) && var14 > 0; --var14) {
               var11 = Direction.getRandomFace(this.random);
            }
         } else {
            var11 = (Direction)var13.get(this.random.nextInt(var13.size()));
         }

         var5 = this.x + (double)var11.getStepX();
         var7 = this.y + (double)var11.getStepY();
         var9 = this.z + (double)var11.getStepZ();
      }

      this.setMoveDirection(var11);
      double var20 = var5 - this.x;
      double var21 = var7 - this.y;
      double var16 = var9 - this.z;
      double var18 = (double)Mth.sqrt(var20 * var20 + var21 * var21 + var16 * var16);
      if (var18 == 0.0D) {
         this.targetDeltaX = 0.0D;
         this.targetDeltaY = 0.0D;
         this.targetDeltaZ = 0.0D;
      } else {
         this.targetDeltaX = var20 / var18 * 0.15D;
         this.targetDeltaY = var21 / var18 * 0.15D;
         this.targetDeltaZ = var16 / var18 * 0.15D;
      }

      this.hasImpulse = true;
      this.flightSteps = 10 + this.random.nextInt(5) * 10;
   }

   public void tick() {
      if (!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      } else {
         super.tick();
         Vec3 var5;
         if (!this.level.isClientSide) {
            List var1;
            Iterator var2;
            LivingEntity var3;
            if (this.finalTarget == null && this.targetId != null) {
               var1 = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(this.lastKnownTargetPos.offset(-2, -2, -2), this.lastKnownTargetPos.offset(2, 2, 2)));
               var2 = var1.iterator();

               while(var2.hasNext()) {
                  var3 = (LivingEntity)var2.next();
                  if (var3.getUUID().equals(this.targetId)) {
                     this.finalTarget = var3;
                     break;
                  }
               }

               this.targetId = null;
            }

            if (this.owner == null && this.ownerId != null) {
               var1 = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(this.lastKnownOwnerPos.offset(-2, -2, -2), this.lastKnownOwnerPos.offset(2, 2, 2)));
               var2 = var1.iterator();

               while(var2.hasNext()) {
                  var3 = (LivingEntity)var2.next();
                  if (var3.getUUID().equals(this.ownerId)) {
                     this.owner = var3;
                     break;
                  }
               }

               this.ownerId = null;
            }

            if (this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof Player && ((Player)this.finalTarget).isSpectator()) {
               if (!this.isNoGravity()) {
                  this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
               }
            } else {
               this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
               this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
               this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
               var5 = this.getDeltaMovement();
               this.setDeltaMovement(var5.add((this.targetDeltaX - var5.x) * 0.2D, (this.targetDeltaY - var5.y) * 0.2D, (this.targetDeltaZ - var5.z) * 0.2D));
            }

            HitResult var6 = ProjectileUtil.forwardsRaycast(this, true, false, this.owner, ClipContext.Block.COLLIDER);
            if (var6.getType() != HitResult.Type.MISS) {
               this.onHit(var6);
            }
         }

         var5 = this.getDeltaMovement();
         this.setPos(this.x + var5.x, this.y + var5.y, this.z + var5.z);
         ProjectileUtil.rotateTowardsMovement(this, 0.5F);
         if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.END_ROD, this.x - var5.x, this.y - var5.y + 0.15D, this.z - var5.z, 0.0D, 0.0D, 0.0D);
         } else if (this.finalTarget != null && !this.finalTarget.removed) {
            if (this.flightSteps > 0) {
               --this.flightSteps;
               if (this.flightSteps == 0) {
                  this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis());
               }
            }

            if (this.currentMoveDirection != null) {
               BlockPos var7 = new BlockPos(this);
               Direction.Axis var8 = this.currentMoveDirection.getAxis();
               if (this.level.loadedAndEntityCanStandOn(var7.relative(this.currentMoveDirection), this)) {
                  this.selectNextMoveDirection(var8);
               } else {
                  BlockPos var4 = new BlockPos(this.finalTarget);
                  if (var8 == Direction.Axis.X && var7.getX() == var4.getX() || var8 == Direction.Axis.Z && var7.getZ() == var4.getZ() || var8 == Direction.Axis.Y && var7.getY() == var4.getY()) {
                     this.selectNextMoveDirection(var8);
                  }
               }
            }
         }

      }
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      return var1 < 16384.0D;
   }

   public float getBrightness() {
      return 1.0F;
   }

   public int getLightColor() {
      return 15728880;
   }

   protected void onHit(HitResult var1) {
      if (var1.getType() == HitResult.Type.ENTITY) {
         Entity var2 = ((EntityHitResult)var1).getEntity();
         boolean var3 = var2.hurt(DamageSource.indirectMobAttack(this, this.owner).setProjectile(), 4.0F);
         if (var3) {
            this.doEnchantDamageEffects(this.owner, var2);
            if (var2 instanceof LivingEntity) {
               ((LivingEntity)var2).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200));
            }
         }
      } else {
         ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 2, 0.2D, 0.2D, 0.2D, 0.0D);
         this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
      }

      this.remove();
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (!this.level.isClientSide) {
         this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((ServerLevel)this.level).sendParticles(ParticleTypes.CRIT, this.x, this.y, this.z, 15, 0.2D, 0.2D, 0.2D, 0.0D);
         this.remove();
      }

      return true;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
