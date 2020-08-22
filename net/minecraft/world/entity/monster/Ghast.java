package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ghast extends FlyingMob implements Enemy {
   private static final EntityDataAccessor DATA_IS_CHARGING;
   private int explosionPower = 1;

   public Ghast(EntityType var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
      this.moveControl = new Ghast.GhastMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new Ghast.RandomFloatAroundGoal(this));
      this.goalSelector.addGoal(7, new Ghast.GhastLookGoal(this));
      this.goalSelector.addGoal(7, new Ghast.GhastShootFireballGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (var1) -> {
         return Math.abs(var1.getY() - this.getY()) <= 4.0D;
      }));
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

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (var1.getDirectEntity() instanceof LargeFireball && var1.getEntity() instanceof Player) {
         super.hurt(var1, 1000.0F);
         return true;
      } else {
         return super.hurt(var1, var2);
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IS_CHARGING, false);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(100.0D);
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
      return 10.0F;
   }

   public static boolean checkGhastSpawnRules(EntityType var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return var1.getDifficulty() != Difficulty.PEACEFUL && var4.nextInt(20) == 0 && checkMobSpawnRules(var0, var1, var2, var3, var4);
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("ExplosionPower", this.explosionPower);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("ExplosionPower", 99)) {
         this.explosionPower = var1.getInt("ExplosionPower");
      }

   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 2.6F;
   }

   static {
      DATA_IS_CHARGING = SynchedEntityData.defineId(Ghast.class, EntityDataSerializers.BOOLEAN);
   }

   static class GhastShootFireballGoal extends Goal {
      private final Ghast ghast;
      public int chargeTime;

      public GhastShootFireballGoal(Ghast var1) {
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

      public void tick() {
         LivingEntity var1 = this.ghast.getTarget();
         double var2 = 64.0D;
         if (var1.distanceToSqr(this.ghast) < 4096.0D && this.ghast.canSee(var1)) {
            Level var4 = this.ghast.level;
            ++this.chargeTime;
            if (this.chargeTime == 10) {
               var4.levelEvent((Player)null, 1015, new BlockPos(this.ghast), 0);
            }

            if (this.chargeTime == 20) {
               double var5 = 4.0D;
               Vec3 var7 = this.ghast.getViewVector(1.0F);
               double var8 = var1.getX() - (this.ghast.getX() + var7.x * 4.0D);
               double var10 = var1.getY(0.5D) - (0.5D + this.ghast.getY(0.5D));
               double var12 = var1.getZ() - (this.ghast.getZ() + var7.z * 4.0D);
               var4.levelEvent((Player)null, 1016, new BlockPos(this.ghast), 0);
               LargeFireball var14 = new LargeFireball(var4, this.ghast, var8, var10, var12);
               var14.explosionPower = this.ghast.getExplosionPower();
               var14.setPos(this.ghast.getX() + var7.x * 4.0D, this.ghast.getY(0.5D) + 0.5D, var14.getZ() + var7.z * 4.0D);
               var4.addFreshEntity(var14);
               this.chargeTime = -40;
            }
         } else if (this.chargeTime > 0) {
            --this.chargeTime;
         }

         this.ghast.setCharging(this.chargeTime > 10);
      }
   }

   static class GhastLookGoal extends Goal {
      private final Ghast ghast;

      public GhastLookGoal(Ghast var1) {
         this.ghast = var1;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         if (this.ghast.getTarget() == null) {
            Vec3 var1 = this.ghast.getDeltaMovement();
            this.ghast.yRot = -((float)Mth.atan2(var1.x, var1.z)) * 57.295776F;
            this.ghast.yBodyRot = this.ghast.yRot;
         } else {
            LivingEntity var8 = this.ghast.getTarget();
            double var2 = 64.0D;
            if (var8.distanceToSqr(this.ghast) < 4096.0D) {
               double var4 = var8.getX() - this.ghast.getX();
               double var6 = var8.getZ() - this.ghast.getZ();
               this.ghast.yRot = -((float)Mth.atan2(var4, var6)) * 57.295776F;
               this.ghast.yBodyRot = this.ghast.yRot;
            }
         }

      }
   }

   static class RandomFloatAroundGoal extends Goal {
      private final Ghast ghast;

      public RandomFloatAroundGoal(Ghast var1) {
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
            return var8 < 1.0D || var8 > 3600.0D;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         Random var1 = this.ghast.getRandom();
         double var2 = this.ghast.getX() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var4 = this.ghast.getY() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var6 = this.ghast.getZ() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         this.ghast.getMoveControl().setWantedPosition(var2, var4, var6, 1.0D);
      }
   }

   static class GhastMoveControl extends MoveControl {
      private final Ghast ghast;
      private int floatDuration;

      public GhastMoveControl(Ghast var1) {
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
                  this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(var1.scale(0.1D)));
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
            if (!this.ghast.level.noCollision(this.ghast, var3)) {
               return false;
            }
         }

         return true;
      }
   }
}
