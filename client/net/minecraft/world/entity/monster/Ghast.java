package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.function.BooleanSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Ghast extends Mob implements Enemy {
   private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING;
   private static final byte DEFAULT_EXPLOSION_POWER = 1;
   private int explosionPower = 1;

   public Ghast(EntityType<? extends Ghast> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
      this.moveControl = new GhastMoveControl(this, false, () -> false);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(5, new RandomFloatAroundGoal(this));
      this.goalSelector.addGoal(7, new GhastLookGoal(this));
      this.goalSelector.addGoal(7, new GhastShootFireballGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (var1, var2) -> Math.abs(var1.getY() - this.getY()) <= 4.0));
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

   private static boolean isReflectedFireball(DamageSource var0) {
      return var0.getDirectEntity() instanceof LargeFireball && var0.getEntity() instanceof Player;
   }

   public boolean isInvulnerableTo(ServerLevel var1, DamageSource var2) {
      return this.isInvulnerable() && !var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || !isReflectedFireball(var2) && super.isInvulnerableTo(var1, var2);
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   public boolean onClimbable() {
      return false;
   }

   public void travel(Vec3 var1) {
      this.travelFlying(var1, 0.02F);
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (isReflectedFireball(var2)) {
         super.hurtServer(var1, var2, 1000.0F);
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
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FOLLOW_RANGE, 100.0).add(Attributes.CAMERA_DISTANCE, 8.0).add(Attributes.FLYING_SPEED, 0.06);
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

   public static boolean checkGhastSpawnRules(EntityType<Ghast> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return var1.getDifficulty() != Difficulty.PEACEFUL && var4.nextInt(20) == 0 && checkMobSpawnRules(var0, var1, var2, var3, var4);
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.putByte("ExplosionPower", (byte)this.explosionPower);
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.explosionPower = var1.getByteOr("ExplosionPower", (byte)1);
   }

   public boolean supportQuadLeashAsHolder() {
      return true;
   }

   public double leashElasticDistance() {
      return 10.0;
   }

   public double leashSnapDistance() {
      return 16.0;
   }

   public static void faceMovementDirection(Mob var0) {
      if (var0.getTarget() == null) {
         Vec3 var1 = var0.getDeltaMovement();
         var0.setYRot(-((float)Mth.atan2(var1.x, var1.z)) * 57.295776F);
         var0.yBodyRot = var0.getYRot();
      } else {
         LivingEntity var8 = var0.getTarget();
         double var2 = 64.0;
         if (var8.distanceToSqr(var0) < 4096.0) {
            double var4 = var8.getX() - var0.getX();
            double var6 = var8.getZ() - var0.getZ();
            var0.setYRot(-((float)Mth.atan2(var4, var6)) * 57.295776F);
            var0.yBodyRot = var0.getYRot();
         }
      }

   }

   static {
      DATA_IS_CHARGING = SynchedEntityData.<Boolean>defineId(Ghast.class, EntityDataSerializers.BOOLEAN);
   }

   public static class GhastMoveControl extends MoveControl {
      private final Mob ghast;
      private int floatDuration;
      private final boolean careful;
      private final BooleanSupplier shouldBeStopped;

      public GhastMoveControl(Mob var1, boolean var2, BooleanSupplier var3) {
         super(var1);
         this.ghast = var1;
         this.careful = var2;
         this.shouldBeStopped = var3;
      }

      public void tick() {
         if (this.shouldBeStopped.getAsBoolean()) {
            this.operation = MoveControl.Operation.WAIT;
            this.ghast.stopInPlace();
         }

         if (this.operation == MoveControl.Operation.MOVE_TO) {
            if (this.floatDuration-- <= 0) {
               this.floatDuration += this.ghast.getRandom().nextInt(5) + 2;
               Vec3 var1 = new Vec3(this.wantedX - this.ghast.getX(), this.wantedY - this.ghast.getY(), this.wantedZ - this.ghast.getZ());
               if (this.canReach(var1)) {
                  this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(var1.normalize().scale(this.ghast.getAttributeValue(Attributes.FLYING_SPEED) * 5.0 / 3.0)));
               } else {
                  this.operation = MoveControl.Operation.WAIT;
               }
            }

         }
      }

      private boolean canReach(Vec3 var1) {
         AABB var2 = this.ghast.getBoundingBox();
         AABB var3 = var2.move(var1);
         if (this.careful) {
            for(BlockPos var5 : BlockPos.betweenClosed(var3.inflate(1.0))) {
               if (!this.blockTraversalPossible(this.ghast.level(), (Vec3)null, (Vec3)null, var5, false, false)) {
                  return false;
               }
            }
         }

         boolean var8 = this.ghast.isInWater();
         boolean var9 = this.ghast.isInLava();
         Vec3 var6 = this.ghast.position();
         Vec3 var7 = var6.add(var1);
         return BlockGetter.forEachBlockIntersectedBetween(var6, var7, var3, (var6x, var7x) -> var2.intersects(var6x) ? true : this.blockTraversalPossible(this.ghast.level(), var6, var7, var6x, var8, var9));
      }

      private boolean blockTraversalPossible(BlockGetter var1, @Nullable Vec3 var2, @Nullable Vec3 var3, BlockPos var4, boolean var5, boolean var6) {
         BlockState var7 = var1.getBlockState(var4);
         if (var7.isAir()) {
            return true;
         } else {
            boolean var8 = var2 != null && var3 != null;
            boolean var9 = var8 ? !this.ghast.collidedWithShapeMovingFrom(var2, var3, var7.getCollisionShape(var1, var4).move(new Vec3(var4)).toAabbs()) : var7.getCollisionShape(var1, var4).isEmpty();
            if (!this.careful) {
               return var9;
            } else if (var7.is(BlockTags.HAPPY_GHAST_AVOIDS)) {
               return false;
            } else {
               FluidState var10 = var1.getFluidState(var4);
               if (!var10.isEmpty() && (!var8 || this.ghast.collidedWithFluid(var10, var4, var2, var3))) {
                  if (var10.is(FluidTags.WATER)) {
                     return var5;
                  }

                  if (var10.is(FluidTags.LAVA)) {
                     return var6;
                  }
               }

               return var9;
            }
         }
      }
   }

   public static class RandomFloatAroundGoal extends Goal {
      private static final int MAX_ATTEMPTS = 64;
      private final Mob ghast;
      private final int distanceToBlocks;

      public RandomFloatAroundGoal(Mob var1) {
         this(var1, 0);
      }

      public RandomFloatAroundGoal(Mob var1, int var2) {
         super();
         this.ghast = var1;
         this.distanceToBlocks = var2;
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
         Vec3 var1 = getSuitableFlyToPosition(this.ghast, this.distanceToBlocks);
         this.ghast.getMoveControl().setWantedPosition(var1.x(), var1.y(), var1.z(), 1.0);
      }

      public static Vec3 getSuitableFlyToPosition(Mob var0, int var1) {
         Level var2 = var0.level();
         RandomSource var3 = var0.getRandom();
         Vec3 var4 = var0.position();
         Vec3 var5 = null;

         for(int var6 = 0; var6 < 64; ++var6) {
            var5 = chooseRandomPositionWithRestriction(var0, var4, var3);
            if (var5 != null && isGoodTarget(var2, var5, var1)) {
               return var5;
            }
         }

         if (var5 == null) {
            var5 = chooseRandomPosition(var4, var3);
         }

         BlockPos var8 = BlockPos.containing(var5);
         int var7 = var2.getHeight(Heightmap.Types.MOTION_BLOCKING, var8.getX(), var8.getZ());
         if (var7 < var8.getY() && var7 > var2.getMinY()) {
            var5 = new Vec3(var5.x(), var0.getY() - Math.abs(var0.getY() - var5.y()), var5.z());
         }

         return var5;
      }

      private static boolean isGoodTarget(Level var0, Vec3 var1, int var2) {
         if (var2 <= 0) {
            return true;
         } else {
            BlockPos var3 = BlockPos.containing(var1);
            if (!var0.getBlockState(var3).isAir()) {
               return false;
            } else {
               for(Direction var7 : Direction.values()) {
                  for(int var8 = 1; var8 < var2; ++var8) {
                     BlockPos var9 = var3.relative(var7, var8);
                     if (!var0.getBlockState(var9).isAir()) {
                        return true;
                     }
                  }
               }

               return false;
            }
         }
      }

      private static Vec3 chooseRandomPosition(Vec3 var0, RandomSource var1) {
         double var2 = var0.x() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var4 = var0.y() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         double var6 = var0.z() + (double)((var1.nextFloat() * 2.0F - 1.0F) * 16.0F);
         return new Vec3(var2, var4, var6);
      }

      private static @Nullable Vec3 chooseRandomPositionWithRestriction(Mob var0, Vec3 var1, RandomSource var2) {
         Vec3 var3 = chooseRandomPosition(var1, var2);
         return var0.hasHome() && !var0.isWithinHome(var3) ? null : var3;
      }
   }

   public static class GhastLookGoal extends Goal {
      private final Mob ghast;

      public GhastLookGoal(Mob var1) {
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
         Ghast.faceMovementDirection(this.ghast);
      }
   }

   static class GhastShootFireballGoal extends Goal {
      private final Ghast ghast;
      public int chargeTime;

      public GhastShootFireballGoal(Ghast var1) {
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
            double var2 = 64.0;
            if (var1.distanceToSqr(this.ghast) < 4096.0 && this.ghast.hasLineOfSight(var1)) {
               Level var4 = this.ghast.level();
               ++this.chargeTime;
               if (this.chargeTime == 10 && !this.ghast.isSilent()) {
                  var4.levelEvent((Entity)null, 1015, this.ghast.blockPosition(), 0);
               }

               if (this.chargeTime == 20) {
                  double var5 = 4.0;
                  Vec3 var7 = this.ghast.getViewVector(1.0F);
                  double var8 = var1.getX() - (this.ghast.getX() + var7.x * 4.0);
                  double var10 = var1.getY(0.5) - (0.5 + this.ghast.getY(0.5));
                  double var12 = var1.getZ() - (this.ghast.getZ() + var7.z * 4.0);
                  Vec3 var14 = new Vec3(var8, var10, var12);
                  if (!this.ghast.isSilent()) {
                     var4.levelEvent((Entity)null, 1016, this.ghast.blockPosition(), 0);
                  }

                  LargeFireball var15 = new LargeFireball(var4, this.ghast, var14.normalize(), this.ghast.getExplosionPower());
                  var15.setPos(this.ghast.getX() + var7.x * 4.0, this.ghast.getY(0.5) + 0.5, var15.getZ() + var7.z * 4.0);
                  var4.addFreshEntity(var15);
                  this.chargeTime = -40;
               }
            } else if (this.chargeTime > 0) {
               --this.chargeTime;
            }

            this.ghast.setCharging(this.chargeTime > 10);
         }
      }
   }
}
