package net.minecraft.world.entity.monster;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class Zoglin extends Monster implements HoglinBase {
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID;
   private static final EntityDimensions BABY_DIMENSIONS;
   private static final int MAX_HEALTH = 40;
   private static final int ATTACK_KNOCKBACK = 1;
   private static final float KNOCKBACK_RESISTANCE = 0.6F;
   private static final int ATTACK_DAMAGE = 6;
   private static final float BABY_ATTACK_DAMAGE = 0.5F;
   private static final int ATTACK_INTERVAL = 40;
   private static final int BABY_ATTACK_INTERVAL = 15;
   private static final int ATTACK_DURATION = 200;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3F;
   private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.4F;
   private static final boolean DEFAULT_BABY = false;
   private int attackAnimationRemainingTicks;
   private static final Brain.Provider<Zoglin> BRAIN_PROVIDER;

   public Zoglin(final EntityType<? extends Zoglin> type, final Level level) {
      super(type, level);
      this.xpReward = 5;
   }

   protected Brain<Zoglin> makeBrain(final Brain.Packed packedBrain) {
      return BRAIN_PROVIDER.makeBrain(this, packedBrain);
   }

   protected static List<ActivityData<Zoglin>> getActivities() {
      return List.of(initCoreActivity(), initIdleActivity(), initFightActivity());
   }

   private static ActivityData<Zoglin> initCoreActivity() {
      return ActivityData.<Zoglin>create(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink()));
   }

   private static ActivityData<Zoglin> initIdleActivity() {
      return ActivityData.<Zoglin>create(Activity.IDLE, 10, ImmutableList.of(StartAttacking.create(Zoglin::findNearestValidAttackTarget), SetEntityLookTargetSometimes.create(8.0F, UniformInt.of(30, 60)), new RunOne(ImmutableList.of(Pair.of(RandomStroll.stroll(0.4F), 2), Pair.of(SetWalkTargetFromLookTarget.create(0.4F, 3), 2), Pair.of(new DoNothing(30, 60), 1)))));
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   private static ActivityData<Zoglin> initFightActivity() {
      return ActivityData.create(Activity.FIGHT, 10, ImmutableList.of(SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F), BehaviorBuilder.triggerIf(Zoglin::isAdult, MeleeAttack.create(40)), BehaviorBuilder.triggerIf(Zoglin::isBaby, MeleeAttack.create(15)), StopAttackingIfTargetInvalid.create()), MemoryModuleType.ATTACK_TARGET);
   }

   private static Optional<? extends LivingEntity> findNearestValidAttackTarget(final ServerLevel level, final Mob mob) {
      return ((NearestVisibleLivingEntities)mob.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse(NearestVisibleLivingEntities.empty())).findClosest((target) -> !target.is(EntityTypes.ZOGLIN) && !target.is(EntityTypes.CREEPER) && Sensor.isEntityAttackable(level, mob, target));
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_BABY_ID, false);
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      super.onSyncedDataUpdated(accessor);
      if (DATA_BABY_ID.equals(accessor)) {
         this.refreshDimensions();
      }

   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      if (level.getRandom().nextFloat() < 0.2F) {
         this.setBaby(true);
      }

      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.KNOCKBACK_RESISTANCE, 0.6000000238418579).add(Attributes.ATTACK_KNOCKBACK, 1.0).add(Attributes.ATTACK_DAMAGE, 6.0);
   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   public boolean doHurtTarget(final ServerLevel level, final Entity target) {
      if (target instanceof LivingEntity entity) {
         this.attackAnimationRemainingTicks = 10;
         level.broadcastEntityEvent(this, (byte)4);
         this.makeSound(SoundEvents.ZOGLIN_ATTACK);
         return HoglinBase.hurtAndThrowTarget(level, this, entity);
      } else {
         return false;
      }
   }

   public boolean canBeLeashed() {
      return true;
   }

   protected void blockedByItem(final LivingEntity defender, final DamageSource source, final float damage) {
      if (!this.isBaby()) {
         HoglinBase.throwTarget(this, defender);
      }

   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      boolean wasHurt = super.hurtServer(level, source, damage);
      if (wasHurt) {
         Entity var6 = source.getEntity();
         if (var6 instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity)var6;
            if (this.canAttack(attacker) && !BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(this, attacker, 4.0)) {
               this.setAttackTarget(attacker);
            }

            return true;
         }
      }

      return wasHurt;
   }

   private void setAttackTarget(final LivingEntity target) {
      this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      this.brain.setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, target, 200L);
   }

   public Brain<Zoglin> getBrain() {
      return super.getBrain();
   }

   protected void updateActivity() {
      Activity oldActivity = (Activity)this.brain.getActiveNonCoreActivity().orElse((Object)null);
      this.brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
      Activity newActivity = (Activity)this.brain.getActiveNonCoreActivity().orElse((Object)null);
      if (newActivity == Activity.FIGHT && oldActivity != Activity.FIGHT) {
         this.playAngrySound();
      }

      this.setAggressive(this.brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
   }

   protected void customServerAiStep(final ServerLevel level) {
      ProfilerFiller profiler = Profiler.get();
      profiler.push("zoglinBrain");
      this.getBrain().tick(level, this);
      profiler.pop();
      this.updateActivity();
   }

   public void setBaby(final boolean baby) {
      this.getEntityData().set(DATA_BABY_ID, baby);
      if (!this.level().isClientSide() && baby) {
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5);
      }

   }

   public boolean isBaby() {
      return (Boolean)this.getEntityData().get(DATA_BABY_ID);
   }

   public void aiStep() {
      if (this.attackAnimationRemainingTicks > 0) {
         --this.attackAnimationRemainingTicks;
      }

      super.aiStep();
   }

   public void handleEntityEvent(final @EntityEvent.Value byte id) {
      if (id == 4) {
         this.attackAnimationRemainingTicks = 10;
         this.makeSound(SoundEvents.ZOGLIN_ATTACK);
      } else {
         super.handleEntityEvent(id);
      }

   }

   public int getAttackAnimationRemainingTicks() {
      return this.attackAnimationRemainingTicks;
   }

   protected SoundEvent getAmbientSound() {
      if (this.level().isClientSide()) {
         return null;
      } else {
         return this.brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET) ? SoundEvents.ZOGLIN_ANGRY : SoundEvents.ZOGLIN_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.ZOGLIN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOGLIN_DEATH;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      this.playSound(SoundEvents.ZOGLIN_STEP, 0.15F, 1.0F);
   }

   protected void playAngrySound() {
      this.makeSound(SoundEvents.ZOGLIN_ANGRY);
   }

   public @Nullable LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("IsBaby", this.isBaby());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setBaby(input.getBooleanOr("IsBaby", false));
   }

   static {
      DATA_BABY_ID = SynchedEntityData.<Boolean>defineId(Zoglin.class, EntityDataSerializers.BOOLEAN);
      BABY_DIMENSIONS = EntityDimensions.scalable(0.75F, 0.85F).withEyeHeight(0.625F).withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, 0.875F, 0.0F));
      BRAIN_PROVIDER = Brain.<Zoglin>provider(List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS), (var0) -> getActivities());
   }
}
