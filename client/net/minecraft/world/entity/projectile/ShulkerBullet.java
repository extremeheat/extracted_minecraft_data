package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBullet extends Projectile {
   private Entity finalTarget;
   @Nullable
   private Direction currentMoveDirection;
   private int flightSteps;
   private double targetDeltaX;
   private double targetDeltaY;
   private double targetDeltaZ;
   @Nullable
   private UUID targetId;

   public ShulkerBullet(EntityType<? extends ShulkerBullet> var1, Level var2) {
      super(var1, var2);
      this.noPhysics = true;
   }

   public ShulkerBullet(Level var1, LivingEntity var2, Entity var3, Direction.Axis var4) {
      this(EntityType.SHULKER_BULLET, var1);
      this.setOwner(var2);
      BlockPos var5 = var2.blockPosition();
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
      super.addAdditionalSaveData(var1);
      if (this.finalTarget != null) {
         var1.putUUID("Target", this.finalTarget.getUUID());
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
      super.readAdditionalSaveData(var1);
      this.flightSteps = var1.getInt("Steps");
      this.targetDeltaX = var1.getDouble("TXD");
      this.targetDeltaY = var1.getDouble("TYD");
      this.targetDeltaZ = var1.getDouble("TZD");
      if (var1.contains("Dir", 99)) {
         this.currentMoveDirection = Direction.from3DDataValue(var1.getInt("Dir"));
      }

      if (var1.hasUUID("Target")) {
         this.targetId = var1.getUUID("Target");
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
         var2 = this.blockPosition().below();
      } else {
         var3 = (double)this.finalTarget.getBbHeight() * 0.5D;
         var2 = new BlockPos(this.finalTarget.getX(), this.finalTarget.getY() + var3, this.finalTarget.getZ());
      }

      double var5 = (double)var2.getX() + 0.5D;
      double var7 = (double)var2.getY() + var3;
      double var9 = (double)var2.getZ() + 0.5D;
      Direction var11 = null;
      if (!var2.closerThan(this.position(), 2.0D)) {
         BlockPos var12 = this.blockPosition();
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

         var11 = Direction.getRandom(this.random);
         if (var13.isEmpty()) {
            for(int var14 = 5; !this.level.isEmptyBlock(var12.relative(var11)) && var14 > 0; --var14) {
               var11 = Direction.getRandom(this.random);
            }
         } else {
            var11 = (Direction)var13.get(this.random.nextInt(var13.size()));
         }

         var5 = this.getX() + (double)var11.getStepX();
         var7 = this.getY() + (double)var11.getStepY();
         var9 = this.getZ() + (double)var11.getStepZ();
      }

      this.setMoveDirection(var11);
      double var20 = var5 - this.getX();
      double var21 = var7 - this.getY();
      double var16 = var9 - this.getZ();
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

   public void checkDespawn() {
      if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
         this.discard();
      }

   }

   public void tick() {
      super.tick();
      Vec3 var1;
      if (!this.level.isClientSide) {
         if (this.finalTarget == null && this.targetId != null) {
            this.finalTarget = ((ServerLevel)this.level).getEntity(this.targetId);
            if (this.finalTarget == null) {
               this.targetId = null;
            }
         }

         if (this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof Player && ((Player)this.finalTarget).isSpectator()) {
            if (!this.isNoGravity()) {
               this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
            }
         } else {
            this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
            this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
            this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
            var1 = this.getDeltaMovement();
            this.setDeltaMovement(var1.add((this.targetDeltaX - var1.x) * 0.2D, (this.targetDeltaY - var1.y) * 0.2D, (this.targetDeltaZ - var1.z) * 0.2D));
         }

         HitResult var5 = ProjectileUtil.getHitResult(this, this::canHitEntity);
         if (var5.getType() != HitResult.Type.MISS) {
            this.onHit(var5);
         }
      }

      this.checkInsideBlocks();
      var1 = this.getDeltaMovement();
      this.setPos(this.getX() + var1.x, this.getY() + var1.y, this.getZ() + var1.z);
      ProjectileUtil.rotateTowardsMovement(this, 0.5F);
      if (this.level.isClientSide) {
         this.level.addParticle(ParticleTypes.END_ROD, this.getX() - var1.x, this.getY() - var1.y + 0.15D, this.getZ() - var1.z, 0.0D, 0.0D, 0.0D);
      } else if (this.finalTarget != null && !this.finalTarget.isRemoved()) {
         if (this.flightSteps > 0) {
            --this.flightSteps;
            if (this.flightSteps == 0) {
               this.selectNextMoveDirection(this.currentMoveDirection == null ? null : this.currentMoveDirection.getAxis());
            }
         }

         if (this.currentMoveDirection != null) {
            BlockPos var2 = this.blockPosition();
            Direction.Axis var3 = this.currentMoveDirection.getAxis();
            if (this.level.loadedAndEntityCanStandOn(var2.relative(this.currentMoveDirection), this)) {
               this.selectNextMoveDirection(var3);
            } else {
               BlockPos var4 = this.finalTarget.blockPosition();
               if (var3 == Direction.Axis.X && var2.getX() == var4.getX() || var3 == Direction.Axis.Z && var2.getZ() == var4.getZ() || var3 == Direction.Axis.Y && var2.getY() == var4.getY()) {
                  this.selectNextMoveDirection(var3);
               }
            }
         }
      }

   }

   protected boolean canHitEntity(Entity var1) {
      return super.canHitEntity(var1) && !var1.noPhysics;
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

   protected void onHitEntity(EntityHitResult var1) {
      super.onHitEntity(var1);
      Entity var2 = var1.getEntity();
      Entity var3 = this.getOwner();
      LivingEntity var4 = var3 instanceof LivingEntity ? (LivingEntity)var3 : null;
      boolean var5 = var2.hurt(DamageSource.indirectMobAttack(this, var4).setProjectile(), 4.0F);
      if (var5) {
         this.doEnchantDamageEffects(var4, var2);
         if (var2 instanceof LivingEntity) {
            ((LivingEntity)var2).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200));
         }
      }

   }

   protected void onHitBlock(BlockHitResult var1) {
      super.onHitBlock(var1);
      ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
      this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
   }

   protected void onHit(HitResult var1) {
      super.onHit(var1);
      this.discard();
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (!this.level.isClientSide) {
         this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((ServerLevel)this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
         this.discard();
      }

      return true;
   }

   public void recreateFromPacket(ClientboundAddEntityPacket var1) {
      super.recreateFromPacket(var1);
      double var2 = var1.getXa();
      double var4 = var1.getYa();
      double var6 = var1.getZa();
      this.setDeltaMovement(var2, var4, var6);
   }
}
