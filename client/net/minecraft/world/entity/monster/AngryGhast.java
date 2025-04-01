package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AngryGhast extends FlyingMob implements Enemy {
   private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING;
   private static final byte DEFAULT_EXPLOSION_POWER = 3;
   private int explosionPower = 3;

   public AngryGhast(EntityType<? extends AngryGhast> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 50;
      this.moveControl = new AngryGhastMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new FloatTowardsPlayerGoal(this));
      this.goalSelector.addGoal(7, new AngryGhastLookGoal(this));
      this.goalSelector.addGoal(7, new AngryGhastShootFireballGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (TargetingConditions.Selector)null));
   }

   public boolean isCharging() {
      return (Boolean)this.entityData.get(DATA_IS_CHARGING);
   }

   public void setCharging(boolean var1) {
      this.entityData.set(DATA_IS_CHARGING, var1);
   }

   public int getExplosionPower() {
      return this.explosionPower;
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   private static boolean isReflectedFireball(DamageSource var0) {
      return var0.getDirectEntity() instanceof LargeFireball && var0.getEntity() instanceof Player;
   }

   public boolean isInvulnerableTo(ServerLevel var1, DamageSource var2) {
      return this.isInvulnerable() && !var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !isReflectedFireball(var2) && super.isInvulnerableTo(var1, var2);
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (isReflectedFireball(var2)) {
         return true;
      } else {
         return this.isInvulnerableTo(var1, var2) ? false : super.hurtServer(var1, var2, var3);
      }
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_IS_CHARGING, false);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 200.0).add(Attributes.SCALE, 2.0).add(Attributes.FOLLOW_RANGE, 200.0);
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.GHAST_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.GHAST_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.GHAST_DEATH;
   }

   protected float getSoundVolume() {
      return 5.0F;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putByte("ExplosionPower", (byte)this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.explosionPower = var1.getByteOr("ExplosionPower", (byte)3);
   }

   static {
      DATA_IS_CHARGING = SynchedEntityData.<Boolean>defineId(AngryGhast.class, EntityDataSerializers.BOOLEAN);
   }

   static class AngryGhastMoveControl extends MoveControl {
      private final AngryGhast ghast;
      private int floatDuration;

      public AngryGhastMoveControl(AngryGhast var1) {
         super(var1);
         this.ghast = var1;
      }

      public void tick() {
         if (this.operation == MoveControl.Operation.MOVE_TO) {
            if (this.floatDuration-- <= 0) {
               this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
               Vec3 var1 = new Vec3(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
               double var2 = var1.length();
               var1 = var1.normalize();
               if (this.canReach(var1, Mth.ceil(var2))) {
                  this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(var1.scale(0.1)));
               } else {
                  this.operation = MoveControl.Operation.WAIT;
               }
            }

         }
      }

      private boolean canReach(Vec3 var1, int var2) {
         AABB var3 = this.ghast.getBoundingBox();

         for(int var4 = 1; var4 < var2; ++var4) {
            var3 = var3.move(var1);
            if (!this.ghast.level().noCollision(this.ghast, var3)) {
               return false;
            }
         }

         return true;
      }
   }

   static class FloatTowardsPlayerGoal extends Goal {
      private final AngryGhast ghast;

      public FloatTowardsPlayerGoal(AngryGhast var1) {
         super();
         this.ghast = var1;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         MoveControl var1 = this.ghast.getMoveControl();
         if (!var1.hasWanted()) {
            return true;
         } else {
            double var2 = var1.getWantedX() - this.ghast.getX();
            double var4 = var1.getWantedY() - this.ghast.getY();
            double var6 = var1.getWantedZ() - this.ghast.getZ();
            double var8 = var2 * var2 + var4 * var4 + var6 * var6;
            return var8 < 1.0 || var8 > 3600.0;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         RandomSource var1 = this.ghast.getRandom();
         Player var2 = this.ghast.level().getNearestPlayer(this.ghast, 200.0);
         if (var2 == null) {
            double var3 = this.ghast.getX() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double var5 = this.ghast.getY() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double var7 = this.ghast.getZ() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.ghast.getMoveControl().setWantedPosition(var3, var5, var7, 1.0);
         } else {
            BlockPos var9 = var2.blockPosition().offset(new Vec3i(var1.nextInt(20), var1.nextInt(20), var1.nextInt(20)));
            this.ghast.getMoveControl().setWantedPosition((double)var9.getX(), (double)var9.getY(), (double)var9.getZ(), 1.0);
         }

      }
   }

   static class AngryGhastLookGoal extends Goal {
      private final AngryGhast ghast;

      public AngryGhastLookGoal(AngryGhast var1) {
         super();
         this.ghast = var1;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return true;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (this.ghast.getTarget() == null) {
            Vec3 var1 = this.ghast.getDeltaMovement();
            this.ghast.setYRot(-((float)Mth.atan2(var1.x, var1.z)) * 57.295776F);
            this.ghast.yBodyRot = this.ghast.getYRot();
         } else {
            LivingEntity var8 = this.ghast.getTarget();
            double var2 = 64.0;
            if (var8.distanceToSqr(this.ghast) < 4096.0) {
               double var4 = var8.getX() - this.ghast.getX();
               double var6 = var8.getZ() - this.ghast.getZ();
               this.ghast.setYRot(-((float)Mth.atan2(var4, var6)) * 57.295776F);
               this.ghast.yBodyRot = this.ghast.getYRot();
            }
         }

      }
   }

   static class AngryGhastShootFireballGoal extends Goal {
      private final AngryGhast ghast;
      public int chargeTime;

      public AngryGhastShootFireballGoal(AngryGhast var1) {
         super();
         this.ghast = var1;
      }

      public boolean canUse() {
         return this.ghast.getTarget() != null;
      }

      public void start() {
         this.chargeTime = 0;
      }

      public void stop() {
         this.ghast.setCharging(false);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity var1 = this.ghast.getTarget();
         if (var1 != null) {
            double var2 = 100.0;
            if (var1.distanceToSqr(this.ghast) < 10000.0 && this.ghast.hasLineOfSight(var1)) {
               Level var4 = this.ghast.level();
               ++this.chargeTime;
               if (this.chargeTime == 10 && !this.ghast.isSilent()) {
                  var4.levelEvent((Entity)null, 1015, this.ghast.blockPosition(), 0);
               }

               if (this.chargeTime == 20 || this.chargeTime == 25) {
                  this.fire(var1, var4);
               }

               if (this.chargeTime == 35) {
                  this.fire(var1, var4);
                  this.chargeTime = -60;
               }
            } else if (this.chargeTime > 0) {
               --this.chargeTime;
            }

            this.ghast.setCharging(this.chargeTime > 10);
         }
      }

      private void fire(LivingEntity var1, Level var2) {
         double var3 = 4.0;
         Vec3 var5 = this.ghast.getViewVector(1.0F);
         double var6 = var1.getX() - (this.ghast.getX() + var5.x * 4.0);
         double var8 = var1.getY(0.5) - (0.5 + this.ghast.getY(0.5));
         double var10 = var1.getZ() - (this.ghast.getZ() + var5.z * 4.0);
         Vec3 var12 = new Vec3(var6, var8, var10);
         if (!this.ghast.isSilent()) {
            var2.levelEvent((Entity)null, 1016, this.ghast.blockPosition(), 0);
         }

         LargeFireball var13 = new LargeFireball(var2, this.ghast, var12.normalize(), this.ghast.getExplosionPower());
         var13.setPos(this.ghast.getX() + var5.x * 4.0, this.ghast.getY(0.5) + 0.5, var13.getZ() + var5.z * 4.0);
         var2.addFreshEntity(var13);
      }
   }
}
