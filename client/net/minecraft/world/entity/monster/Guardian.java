package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Guardian extends Monster {
   protected static final int ATTACK_TIME = 80;
   private static final EntityDataAccessor<Boolean> DATA_ID_MOVING;
   private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET;
   private float clientSideTailAnimation;
   private float clientSideTailAnimationO;
   private float clientSideTailAnimationSpeed;
   private float clientSideSpikesAnimation;
   private float clientSideSpikesAnimationO;
   @Nullable
   private LivingEntity clientSideCachedAttackTarget;
   private int clientSideAttackTime;
   private boolean clientSideTouchedGround;
   @Nullable
   protected RandomStrollGoal randomStrollGoal;

   public Guardian(EntityType<? extends Guardian> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 10;
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.moveControl = new GuardianMoveControl(this);
      this.clientSideTailAnimation = this.random.nextFloat();
      this.clientSideTailAnimationO = this.clientSideTailAnimation;
   }

   protected void registerGoals() {
      MoveTowardsRestrictionGoal var1 = new MoveTowardsRestrictionGoal(this, 1.0);
      this.randomStrollGoal = new RandomStrollGoal(this, 1.0, 80);
      this.goalSelector.addGoal(4, new GuardianAttackGoal(this));
      this.goalSelector.addGoal(5, var1);
      this.goalSelector.addGoal(7, this.randomStrollGoal);
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Guardian.class, 12.0F, 0.01F));
      this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
      this.randomStrollGoal.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      var1.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, LivingEntity.class, 10, true, false, new GuardianAttackSelector(this)));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 6.0).add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.MAX_HEALTH, 30.0);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new WaterBoundPathNavigation(this, var1);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_MOVING, false);
      var1.define(DATA_ID_ATTACK_TARGET, 0);
   }

   public boolean isMoving() {
      return (Boolean)this.entityData.get(DATA_ID_MOVING);
   }

   void setMoving(boolean var1) {
      this.entityData.set(DATA_ID_MOVING, var1);
   }

   public int getAttackDuration() {
      return 80;
   }

   void setActiveAttackTarget(int var1) {
      this.entityData.set(DATA_ID_ATTACK_TARGET, var1);
   }

   public boolean hasActiveAttackTarget() {
      return (Integer)this.entityData.get(DATA_ID_ATTACK_TARGET) != 0;
   }

   @Nullable
   public LivingEntity getActiveAttackTarget() {
      if (!this.hasActiveAttackTarget()) {
         return null;
      } else if (this.level().isClientSide) {
         if (this.clientSideCachedAttackTarget != null) {
            return this.clientSideCachedAttackTarget;
         } else {
            Entity var1 = this.level().getEntity((Integer)this.entityData.get(DATA_ID_ATTACK_TARGET));
            if (var1 instanceof LivingEntity) {
               this.clientSideCachedAttackTarget = (LivingEntity)var1;
               return this.clientSideCachedAttackTarget;
            } else {
               return null;
            }
         }
      } else {
         return this.getTarget();
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_ID_ATTACK_TARGET.equals(var1)) {
         this.clientSideAttackTime = 0;
         this.clientSideCachedAttackTarget = null;
      }

   }

   public int getAmbientSoundInterval() {
      return 160;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWaterOrBubble() ? SoundEvents.GUARDIAN_AMBIENT : SoundEvents.GUARDIAN_AMBIENT_LAND;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isInWaterOrBubble() ? SoundEvents.GUARDIAN_HURT : SoundEvents.GUARDIAN_HURT_LAND;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWaterOrBubble() ? SoundEvents.GUARDIAN_DEATH : SoundEvents.GUARDIAN_DEATH_LAND;
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getFluidState(var1).is(FluidTags.WATER) ? 10.0F + var2.getPathfindingCostFromLightLevels(var1) : super.getWalkTargetValue(var1, var2);
   }

   public void aiStep() {
      if (this.isAlive()) {
         if (this.level().isClientSide) {
            this.clientSideTailAnimationO = this.clientSideTailAnimation;
            if (!this.isInWater()) {
               this.clientSideTailAnimationSpeed = 2.0F;
               Vec3 var1 = this.getDeltaMovement();
               if (var1.y > 0.0 && this.clientSideTouchedGround && !this.isSilent()) {
                  this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getFlopSound(), this.getSoundSource(), 1.0F, 1.0F, false);
               }

               this.clientSideTouchedGround = var1.y < 0.0 && this.level().loadedAndEntityCanStandOn(this.blockPosition().below(), this);
            } else if (this.isMoving()) {
               if (this.clientSideTailAnimationSpeed < 0.5F) {
                  this.clientSideTailAnimationSpeed = 4.0F;
               } else {
                  this.clientSideTailAnimationSpeed += (0.5F - this.clientSideTailAnimationSpeed) * 0.1F;
               }
            } else {
               this.clientSideTailAnimationSpeed += (0.125F - this.clientSideTailAnimationSpeed) * 0.2F;
            }

            this.clientSideTailAnimation += this.clientSideTailAnimationSpeed;
            this.clientSideSpikesAnimationO = this.clientSideSpikesAnimation;
            if (!this.isInWaterOrBubble()) {
               this.clientSideSpikesAnimation = this.random.nextFloat();
            } else if (this.isMoving()) {
               this.clientSideSpikesAnimation += (0.0F - this.clientSideSpikesAnimation) * 0.25F;
            } else {
               this.clientSideSpikesAnimation += (1.0F - this.clientSideSpikesAnimation) * 0.06F;
            }

            if (this.isMoving() && this.isInWater()) {
               Vec3 var14 = this.getViewVector(0.0F);

               for(int var2 = 0; var2 < 2; ++var2) {
                  this.level().addParticle(ParticleTypes.BUBBLE, this.getRandomX(0.5) - var14.x * 1.5, this.getRandomY() - var14.y * 1.5, this.getRandomZ(0.5) - var14.z * 1.5, 0.0, 0.0, 0.0);
               }
            }

            if (this.hasActiveAttackTarget()) {
               if (this.clientSideAttackTime < this.getAttackDuration()) {
                  ++this.clientSideAttackTime;
               }

               LivingEntity var15 = this.getActiveAttackTarget();
               if (var15 != null) {
                  this.getLookControl().setLookAt(var15, 90.0F, 90.0F);
                  this.getLookControl().tick();
                  double var16 = (double)this.getAttackAnimationScale(0.0F);
                  double var4 = var15.getX() - this.getX();
                  double var6 = var15.getY(0.5) - this.getEyeY();
                  double var8 = var15.getZ() - this.getZ();
                  double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
                  var4 /= var10;
                  var6 /= var10;
                  var8 /= var10;
                  double var12 = this.random.nextDouble();

                  while(var12 < var10) {
                     var12 += 1.8 - var16 + this.random.nextDouble() * (1.7 - var16);
                     this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + var4 * var12, this.getEyeY() + var6 * var12, this.getZ() + var8 * var12, 0.0, 0.0, 0.0);
                  }
               }
            }
         }

         if (this.isInWaterOrBubble()) {
            this.setAirSupply(300);
         } else if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F)));
            this.setYRot(this.random.nextFloat() * 360.0F);
            this.setOnGround(false);
            this.hasImpulse = true;
         }

         if (this.hasActiveAttackTarget()) {
            this.setYRot(this.yHeadRot);
         }
      }

      super.aiStep();
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.GUARDIAN_FLOP;
   }

   public float getTailAnimation(float var1) {
      return Mth.lerp(var1, this.clientSideTailAnimationO, this.clientSideTailAnimation);
   }

   public float getSpikesAnimation(float var1) {
      return Mth.lerp(var1, this.clientSideSpikesAnimationO, this.clientSideSpikesAnimation);
   }

   public float getAttackAnimationScale(float var1) {
      return ((float)this.clientSideAttackTime + var1) / (float)this.getAttackDuration();
   }

   public float getClientSideAttackTime() {
      return (float)this.clientSideAttackTime;
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   public static boolean checkGuardianSpawnRules(EntityType<? extends Guardian> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return (var4.nextInt(20) == 0 || !var1.canSeeSkyFromBelowWater(var3)) && var1.getDifficulty() != Difficulty.PEACEFUL && (EntitySpawnReason.isSpawner(var2) || var1.getFluidState(var3).is(FluidTags.WATER)) && var1.getFluidState(var3.below()).is(FluidTags.WATER);
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (!this.isMoving() && !var2.is(DamageTypeTags.AVOIDS_GUARDIAN_THORNS) && !var2.is(DamageTypes.THORNS)) {
         Entity var5 = var2.getDirectEntity();
         if (var5 instanceof LivingEntity) {
            LivingEntity var4 = (LivingEntity)var5;
            var4.hurtServer(var1, this.damageSources().thorns(this), 2.0F);
         }
      }

      if (this.randomStrollGoal != null) {
         this.randomStrollGoal.trigger();
      }

      return super.hurtServer(var1, var2, var3);
   }

   public int getMaxHeadXRot() {
      return 180;
   }

   public void travel(Vec3 var1) {
      if (this.isControlledByLocalInstance() && this.isInWater()) {
         this.moveRelative(0.1F, var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (!this.isMoving() && this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(var1);
      }

   }

   static {
      DATA_ID_MOVING = SynchedEntityData.<Boolean>defineId(Guardian.class, EntityDataSerializers.BOOLEAN);
      DATA_ID_ATTACK_TARGET = SynchedEntityData.<Integer>defineId(Guardian.class, EntityDataSerializers.INT);
   }

   static class GuardianAttackSelector implements TargetingConditions.Selector {
      private final Guardian guardian;

      public GuardianAttackSelector(Guardian var1) {
         super();
         this.guardian = var1;
      }

      public boolean test(@Nullable LivingEntity var1, ServerLevel var2) {
         return (var1 instanceof Player || var1 instanceof Squid || var1 instanceof Axolotl) && var1.distanceToSqr(this.guardian) > 9.0;
      }
   }

   static class GuardianAttackGoal extends Goal {
      private final Guardian guardian;
      private int attackTime;
      private final boolean elder;

      public GuardianAttackGoal(Guardian var1) {
         super();
         this.guardian = var1;
         this.elder = var1 instanceof ElderGuardian;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity var1 = this.guardian.getTarget();
         return var1 != null && var1.isAlive();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && (this.elder || this.guardian.getTarget() != null && this.guardian.distanceToSqr(this.guardian.getTarget()) > 9.0);
      }

      public void start() {
         this.attackTime = -10;
         this.guardian.getNavigation().stop();
         LivingEntity var1 = this.guardian.getTarget();
         if (var1 != null) {
            this.guardian.getLookControl().setLookAt(var1, 90.0F, 90.0F);
         }

         this.guardian.hasImpulse = true;
      }

      public void stop() {
         this.guardian.setActiveAttackTarget(0);
         this.guardian.setTarget((LivingEntity)null);
         this.guardian.randomStrollGoal.trigger();
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity var1 = this.guardian.getTarget();
         if (var1 != null) {
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().setLookAt(var1, 90.0F, 90.0F);
            if (!this.guardian.hasLineOfSight(var1)) {
               this.guardian.setTarget((LivingEntity)null);
            } else {
               ++this.attackTime;
               if (this.attackTime == 0) {
                  this.guardian.setActiveAttackTarget(var1.getId());
                  if (!this.guardian.isSilent()) {
                     this.guardian.level().broadcastEntityEvent(this.guardian, (byte)21);
                  }
               } else if (this.attackTime >= this.guardian.getAttackDuration()) {
                  float var2 = 1.0F;
                  if (this.guardian.level().getDifficulty() == Difficulty.HARD) {
                     var2 += 2.0F;
                  }

                  if (this.elder) {
                     var2 += 2.0F;
                  }

                  ServerLevel var3 = getServerLevel(this.guardian);
                  var1.hurtServer(var3, this.guardian.damageSources().indirectMagic(this.guardian, this.guardian), var2);
                  this.guardian.doHurtTarget(var3, var1);
                  this.guardian.setTarget((LivingEntity)null);
               }

               super.tick();
            }
         }
      }
   }

   static class GuardianMoveControl extends MoveControl {
      private final Guardian guardian;

      public GuardianMoveControl(Guardian var1) {
         super(var1);
         this.guardian = var1;
      }

      public void tick() {
         if (this.operation == MoveControl.Operation.MOVE_TO && !this.guardian.getNavigation().isDone()) {
            Vec3 var1 = new Vec3(this.wantedX - this.guardian.getX(), this.wantedY - this.guardian.getY(), this.wantedZ - this.guardian.getZ());
            double var2 = var1.length();
            double var4 = var1.x / var2;
            double var6 = var1.y / var2;
            double var8 = var1.z / var2;
            float var10 = (float)(Mth.atan2(var1.z, var1.x) * 57.2957763671875) - 90.0F;
            this.guardian.setYRot(this.rotlerp(this.guardian.getYRot(), var10, 90.0F));
            this.guardian.yBodyRot = this.guardian.getYRot();
            float var11 = (float)(this.speedModifier * this.guardian.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float var12 = Mth.lerp(0.125F, this.guardian.getSpeed(), var11);
            this.guardian.setSpeed(var12);
            double var13 = Math.sin((double)(this.guardian.tickCount + this.guardian.getId()) * 0.5) * 0.05;
            double var15 = Math.cos((double)(this.guardian.getYRot() * 0.017453292F));
            double var17 = Math.sin((double)(this.guardian.getYRot() * 0.017453292F));
            double var19 = Math.sin((double)(this.guardian.tickCount + this.guardian.getId()) * 0.75) * 0.05;
            this.guardian.setDeltaMovement(this.guardian.getDeltaMovement().add(var13 * var15, var19 * (var17 + var15) * 0.25 + (double)var12 * var6 * 0.1, var13 * var17));
            LookControl var21 = this.guardian.getLookControl();
            double var22 = this.guardian.getX() + var4 * 2.0;
            double var24 = this.guardian.getEyeY() + var6 / var2;
            double var26 = this.guardian.getZ() + var8 * 2.0;
            double var28 = var21.getWantedX();
            double var30 = var21.getWantedY();
            double var32 = var21.getWantedZ();
            if (!var21.isLookingAtTarget()) {
               var28 = var22;
               var30 = var24;
               var32 = var26;
            }

            this.guardian.getLookControl().setLookAt(Mth.lerp(0.125, var28, var22), Mth.lerp(0.125, var30, var24), Mth.lerp(0.125, var32, var26), 10.0F, 40.0F);
            this.guardian.setMoving(true);
         } else {
            this.guardian.setSpeed(0.0F);
            this.guardian.setMoving(false);
         }
      }
   }
}
