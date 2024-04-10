package net.minecraft.world.entity.animal.camel;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Camel extends AbstractHorse implements PlayerRideableJumping, Saddleable {
   public static final float BABY_SCALE = 0.45F;
   public static final int DASH_COOLDOWN_TICKS = 55;
   public static final int MAX_HEAD_Y_ROT = 30;
   private static final float RUNNING_SPEED_BONUS = 0.1F;
   private static final float DASH_VERTICAL_MOMENTUM = 1.4285F;
   private static final float DASH_HORIZONTAL_MOMENTUM = 22.2222F;
   private static final int DASH_MINIMUM_DURATION_TICKS = 5;
   private static final int SITDOWN_DURATION_TICKS = 40;
   private static final int STANDUP_DURATION_TICKS = 52;
   private static final int IDLE_MINIMAL_DURATION_TICKS = 80;
   private static final float SITTING_HEIGHT_DIFFERENCE = 1.43F;
   public static final EntityDataAccessor<Boolean> DASH = SynchedEntityData.defineId(Camel.class, EntityDataSerializers.BOOLEAN);
   public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(Camel.class, EntityDataSerializers.LONG);
   public final AnimationState sitAnimationState = new AnimationState();
   public final AnimationState sitPoseAnimationState = new AnimationState();
   public final AnimationState sitUpAnimationState = new AnimationState();
   public final AnimationState idleAnimationState = new AnimationState();
   public final AnimationState dashAnimationState = new AnimationState();
   private static final EntityDimensions SITTING_DIMENSIONS = EntityDimensions.scalable(EntityType.CAMEL.getWidth(), EntityType.CAMEL.getHeight() - 1.43F)
      .withEyeHeight(0.845F);
   private int dashCooldown = 0;
   private int idleAnimationTimeout = 0;

   public Camel(EntityType<? extends Camel> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new Camel.CamelMoveControl();
      this.lookControl = new Camel.CamelLookControl();
      GroundPathNavigation var3 = (GroundPathNavigation)this.getNavigation();
      var3.setCanFloat(true);
      var3.setCanWalkOverFences(true);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      long var2 = var1.getLong("LastPoseTick");
      if (var2 < 0L) {
         this.setPose(Pose.SITTING);
      }

      this.resetLastPoseChangeTick(var2);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return createBaseHorseAttributes()
         .add(Attributes.MAX_HEALTH, 32.0)
         .add(Attributes.MOVEMENT_SPEED, 0.09000000357627869)
         .add(Attributes.JUMP_STRENGTH, 0.41999998688697815)
         .add(Attributes.STEP_HEIGHT, 1.5);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DASH, false);
      var1.define(LAST_POSE_CHANGE_TICK, 0L);
   }

   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      CamelAi.initMemories(this, var1.getRandom());
      this.resetLastPoseChangeTickToFullStand(var1.getLevel().getGameTime());
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   @Override
   protected Brain.Provider<Camel> brainProvider() {
      return CamelAi.brainProvider();
   }

   @Override
   protected void registerGoals() {
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return CamelAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return var1 == Pose.SITTING ? SITTING_DIMENSIONS.scale(this.getAgeScale()) : super.getDefaultDimensions(var1);
   }

   @Override
   protected void customServerAiStep() {
      this.level().getProfiler().push("camelBrain");
      Brain var1 = this.getBrain();
      var1.tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      this.level().getProfiler().push("camelActivityUpdate");
      CamelAi.updateActivity(this);
      this.level().getProfiler().pop();
      super.customServerAiStep();
   }

   @Override
   public void tick() {
      super.tick();
      if (this.isDashing() && this.dashCooldown < 50 && (this.onGround() || this.isInLiquid() || this.isPassenger())) {
         this.setDashing(false);
      }

      if (this.dashCooldown > 0) {
         this.dashCooldown--;
         if (this.dashCooldown == 0) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.CAMEL_DASH_READY, SoundSource.NEUTRAL, 1.0F, 1.0F);
         }
      }

      if (this.level().isClientSide()) {
         this.setupAnimationStates();
      }

      if (this.refuseToMove()) {
         this.clampHeadRotationToBody();
      }

      if (this.isCamelSitting() && this.isInWater()) {
         this.standUpInstantly();
      }
   }

   private void setupAnimationStates() {
      if (this.idleAnimationTimeout <= 0) {
         this.idleAnimationTimeout = this.random.nextInt(40) + 80;
         this.idleAnimationState.start(this.tickCount);
      } else {
         this.idleAnimationTimeout--;
      }

      if (this.isCamelVisuallySitting()) {
         this.sitUpAnimationState.stop();
         this.dashAnimationState.stop();
         if (this.isVisuallySittingDown()) {
            this.sitAnimationState.startIfStopped(this.tickCount);
            this.sitPoseAnimationState.stop();
         } else {
            this.sitAnimationState.stop();
            this.sitPoseAnimationState.startIfStopped(this.tickCount);
         }
      } else {
         this.sitAnimationState.stop();
         this.sitPoseAnimationState.stop();
         this.dashAnimationState.animateWhen(this.isDashing(), this.tickCount);
         this.sitUpAnimationState.animateWhen(this.isInPoseTransition() && this.getPoseTime() >= 0L, this.tickCount);
      }
   }

   @Override
   protected void updateWalkAnimation(float var1) {
      float var2;
      if (this.getPose() == Pose.STANDING && !this.dashAnimationState.isStarted()) {
         var2 = Math.min(var1 * 6.0F, 1.0F);
      } else {
         var2 = 0.0F;
      }

      this.walkAnimation.update(var2, 0.2F);
   }

   @Override
   public void travel(Vec3 var1) {
      if (this.refuseToMove() && this.onGround()) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.0, 1.0, 0.0));
         var1 = var1.multiply(0.0, 1.0, 0.0);
      }

      super.travel(var1);
   }

   @Override
   protected void tickRidden(Player var1, Vec3 var2) {
      super.tickRidden(var1, var2);
      if (var1.zza > 0.0F && this.isCamelSitting() && !this.isInPoseTransition()) {
         this.standUp();
      }
   }

   public boolean refuseToMove() {
      return this.isCamelSitting() || this.isInPoseTransition();
   }

   @Override
   protected float getRiddenSpeed(Player var1) {
      float var2 = var1.isSprinting() && this.getJumpCooldown() == 0 ? 0.1F : 0.0F;
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) + var2;
   }

   @Override
   protected Vec2 getRiddenRotation(LivingEntity var1) {
      return this.refuseToMove() ? new Vec2(this.getXRot(), this.getYRot()) : super.getRiddenRotation(var1);
   }

   @Override
   protected Vec3 getRiddenInput(Player var1, Vec3 var2) {
      return this.refuseToMove() ? Vec3.ZERO : super.getRiddenInput(var1, var2);
   }

   @Override
   public boolean canJump() {
      return !this.refuseToMove() && super.canJump();
   }

   @Override
   public void onPlayerJump(int var1) {
      if (this.isSaddled() && this.dashCooldown <= 0 && this.onGround()) {
         super.onPlayerJump(var1);
      }
   }

   @Override
   public boolean canSprint() {
      return true;
   }

   @Override
   protected void executeRidersJump(float var1, Vec3 var2) {
      double var3 = (double)this.getJumpPower();
      this.addDeltaMovement(
         this.getLookAngle()
            .multiply(1.0, 0.0, 1.0)
            .normalize()
            .scale((double)(22.2222F * var1) * this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)this.getBlockSpeedFactor())
            .add(0.0, (double)(1.4285F * var1) * var3, 0.0)
      );
      this.dashCooldown = 55;
      this.setDashing(true);
      this.hasImpulse = true;
   }

   public boolean isDashing() {
      return this.entityData.get(DASH);
   }

   public void setDashing(boolean var1) {
      this.entityData.set(DASH, var1);
   }

   @Override
   public void handleStartJump(int var1) {
      this.makeSound(SoundEvents.CAMEL_DASH);
      this.gameEvent(GameEvent.ENTITY_ACTION);
      this.setDashing(true);
   }

   @Override
   public void handleStopJump() {
   }

   @Override
   public int getJumpCooldown() {
      return this.dashCooldown;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.CAMEL_AMBIENT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.CAMEL_DEATH;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CAMEL_HURT;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      if (var2.is(BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS)) {
         this.playSound(SoundEvents.CAMEL_STEP_SAND, 1.0F, 1.0F);
      } else {
         this.playSound(SoundEvents.CAMEL_STEP, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.CAMEL_FOOD);
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var1.isSecondaryUseActive() && !this.isBaby()) {
         this.openCustomInventoryScreen(var1);
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         InteractionResult var4 = var3.interactLivingEntity(var1, this, var2);
         if (var4.consumesAction()) {
            return var4;
         } else if (this.isFood(var3)) {
            return this.fedFood(var1, var3);
         } else {
            if (this.getPassengers().size() < 2 && !this.isBaby()) {
               this.doPlayerRide(var1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }
      }
   }

   @Override
   protected void onLeashDistance(float var1) {
      if (var1 > 6.0F && this.isCamelSitting() && !this.isInPoseTransition() && this.canCamelChangePose()) {
         this.standUp();
      }
   }

   public boolean canCamelChangePose() {
      return this.wouldNotSuffocateAtTargetPose(this.isCamelSitting() ? Pose.STANDING : Pose.SITTING);
   }

   @Override
   protected boolean handleEating(Player var1, ItemStack var2) {
      if (!this.isFood(var2)) {
         return false;
      } else {
         boolean var3 = this.getHealth() < this.getMaxHealth();
         if (var3) {
            this.heal(2.0F);
         }

         boolean var4 = this.isTamed() && this.getAge() == 0 && this.canFallInLove();
         if (var4) {
            this.setInLove(var1);
         }

         boolean var5 = this.isBaby();
         if (var5) {
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
            if (!this.level().isClientSide) {
               this.ageUp(10);
            }
         }

         if (!var3 && !var4 && !var5) {
            return false;
         } else {
            if (!this.isSilent()) {
               SoundEvent var6 = this.getEatingSound();
               if (var6 != null) {
                  this.level()
                     .playSound(
                        null,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        var6,
                        this.getSoundSource(),
                        1.0F,
                        1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
                     );
               }
            }

            this.gameEvent(GameEvent.EAT);
            return true;
         }
      }
   }

   @Override
   protected boolean canPerformRearing() {
      return false;
   }

   @Override
   public boolean canMate(Animal var1) {
      if (var1 != this && var1 instanceof Camel var2 && this.canParent() && var2.canParent()) {
         return true;
      }

      return false;
   }

   @Nullable
   public Camel getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.CAMEL.create(var1);
   }

   @Nullable
   @Override
   protected SoundEvent getEatingSound() {
      return SoundEvents.CAMEL_EAT;
   }

   @Override
   protected void actuallyHurt(DamageSource var1, float var2) {
      this.standUpInstantly();
      super.actuallyHurt(var1, var2);
   }

   @Override
   protected Vec3 getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      int var4 = Math.max(this.getPassengers().indexOf(var1), 0);
      boolean var5 = var4 == 0;
      float var6 = 0.5F;
      float var7 = (float)(this.isRemoved() ? 0.009999999776482582 : this.getBodyAnchorAnimationYOffset(var5, 0.0F, var2, var3));
      if (this.getPassengers().size() > 1) {
         if (!var5) {
            var6 = -0.7F;
         }

         if (var1 instanceof Animal) {
            var6 += 0.2F;
         }
      }

      return new Vec3(0.0, (double)var7, (double)(var6 * var3)).yRot(-this.getYRot() * 0.017453292F);
   }

   @Override
   public float getAgeScale() {
      return this.isBaby() ? 0.45F : 1.0F;
   }

   private double getBodyAnchorAnimationYOffset(boolean var1, float var2, EntityDimensions var3, float var4) {
      double var5 = (double)(var3.height() - 0.375F * var4);
      float var7 = var4 * 1.43F;
      float var8 = var7 - var4 * 0.2F;
      float var9 = var7 - var8;
      boolean var10 = this.isInPoseTransition();
      boolean var11 = this.isCamelSitting();
      if (var10) {
         int var12 = var11 ? 40 : 52;
         int var13;
         float var14;
         if (var11) {
            var13 = 28;
            var14 = var1 ? 0.5F : 0.1F;
         } else {
            var13 = var1 ? 24 : 32;
            var14 = var1 ? 0.6F : 0.35F;
         }

         float var15 = Mth.clamp((float)this.getPoseTime() + var2, 0.0F, (float)var12);
         boolean var16 = var15 < (float)var13;
         float var17 = var16 ? var15 / (float)var13 : (var15 - (float)var13) / (float)(var12 - var13);
         float var18 = var7 - var14 * var8;
         var5 += var11
            ? (double)Mth.lerp(var17, var16 ? var7 : var18, var16 ? var18 : var9)
            : (double)Mth.lerp(var17, var16 ? var9 - var7 : var9 - var18, var16 ? var9 - var18 : 0.0F);
      }

      if (var11 && !var10) {
         var5 += (double)var9;
      }

      return var5;
   }

   @Override
   public Vec3 getLeashOffset(float var1) {
      EntityDimensions var2 = this.getDimensions(this.getPose());
      float var3 = this.getAgeScale();
      return new Vec3(0.0, this.getBodyAnchorAnimationYOffset(true, var1, var2, var3) - (double)(0.2F * var3), (double)(var2.width() * 0.56F));
   }

   @Override
   public int getMaxHeadYRot() {
      return 30;
   }

   @Override
   protected boolean canAddPassenger(Entity var1) {
      return this.getPassengers().size() <= 2;
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public boolean isCamelSitting() {
      return this.entityData.get(LAST_POSE_CHANGE_TICK) < 0L;
   }

   public boolean isCamelVisuallySitting() {
      return this.getPoseTime() < 0L != this.isCamelSitting();
   }

   public boolean isInPoseTransition() {
      long var1 = this.getPoseTime();
      return var1 < (long)(this.isCamelSitting() ? 40 : 52);
   }

   private boolean isVisuallySittingDown() {
      return this.isCamelSitting() && this.getPoseTime() < 40L && this.getPoseTime() >= 0L;
   }

   public void sitDown() {
      if (!this.isCamelSitting()) {
         this.makeSound(SoundEvents.CAMEL_SIT);
         this.setPose(Pose.SITTING);
         this.gameEvent(GameEvent.ENTITY_ACTION);
         this.resetLastPoseChangeTick(-this.level().getGameTime());
      }
   }

   public void standUp() {
      if (this.isCamelSitting()) {
         this.makeSound(SoundEvents.CAMEL_STAND);
         this.setPose(Pose.STANDING);
         this.gameEvent(GameEvent.ENTITY_ACTION);
         this.resetLastPoseChangeTick(this.level().getGameTime());
      }
   }

   public void standUpInstantly() {
      this.setPose(Pose.STANDING);
      this.gameEvent(GameEvent.ENTITY_ACTION);
      this.resetLastPoseChangeTickToFullStand(this.level().getGameTime());
   }

   @VisibleForTesting
   public void resetLastPoseChangeTick(long var1) {
      this.entityData.set(LAST_POSE_CHANGE_TICK, var1);
   }

   private void resetLastPoseChangeTickToFullStand(long var1) {
      this.resetLastPoseChangeTick(Math.max(0L, var1 - 52L - 1L));
   }

   public long getPoseTime() {
      return this.level().getGameTime() - Math.abs(this.entityData.get(LAST_POSE_CHANGE_TICK));
   }

   @Override
   public SoundEvent getSaddleSoundEvent() {
      return SoundEvents.CAMEL_SADDLE;
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (!this.firstTick && DASH.equals(var1)) {
         this.dashCooldown = this.dashCooldown == 0 ? 55 : this.dashCooldown;
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   public boolean isTamed() {
      return true;
   }

   @Override
   public void openCustomInventoryScreen(Player var1) {
      if (!this.level().isClientSide) {
         var1.openHorseInventory(this, this.inventory);
      }
   }

   @Override
   protected BodyRotationControl createBodyControl() {
      return new Camel.CamelBodyRotationControl(this);
   }

   class CamelBodyRotationControl extends BodyRotationControl {
      public CamelBodyRotationControl(final Camel param2) {
         super(nullx);
      }

      @Override
      public void clientTick() {
         if (!Camel.this.refuseToMove()) {
            super.clientTick();
         }
      }
   }

   class CamelLookControl extends LookControl {
      CamelLookControl() {
         super(Camel.this);
      }

      @Override
      public void tick() {
         if (!Camel.this.hasControllingPassenger()) {
            super.tick();
         }
      }
   }

   class CamelMoveControl extends MoveControl {
      public CamelMoveControl() {
         super(Camel.this);
      }

      @Override
      public void tick() {
         if (this.operation == MoveControl.Operation.MOVE_TO
            && !Camel.this.isLeashed()
            && Camel.this.isCamelSitting()
            && !Camel.this.isInPoseTransition()
            && Camel.this.canCamelChangePose()) {
            Camel.this.standUp();
         }

         super.tick();
      }
   }
}
