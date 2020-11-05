package net.minecraft.world.entity.monster;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.IntRange;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.hoglin.HoglinBase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Zoglin extends Monster implements Enemy, HoglinBase {
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID;
   private int attackAnimationRemainingTicks;
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Zoglin>>> SENSOR_TYPES;
   protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES;

   public Zoglin(EntityType<? extends Zoglin> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
   }

   protected Brain.Provider<Zoglin> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      Brain var2 = this.brainProvider().makeBrain(var1);
      initCoreActivity(var2);
      initIdleActivity(var2);
      initFightActivity(var2);
      var2.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var2.setDefaultActivity(Activity.IDLE);
      var2.useDefaultActivity();
      return var2;
   }

   private static void initCoreActivity(Brain<Zoglin> var0) {
      var0.addActivity(Activity.CORE, 0, ImmutableList.of(new LookAtTargetSink(45, 90), new MoveToTargetSink()));
   }

   private static void initIdleActivity(Brain<Zoglin> var0) {
      var0.addActivity(Activity.IDLE, 10, ImmutableList.of(new StartAttacking(Zoglin::findNearestValidAttackTarget), new RunSometimes(new SetEntityLookTarget(8.0F), IntRange.of(30, 60)), new RunOne(ImmutableList.of(Pair.of(new RandomStroll(0.4F), 2), Pair.of(new SetWalkTargetFromLookTarget(0.4F, 3), 2), Pair.of(new DoNothing(30, 60), 1)))));
   }

   private static void initFightActivity(Brain<Zoglin> var0) {
      var0.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(new SetWalkTargetFromAttackTargetIfTargetOutOfReach(1.0F), new RunIf(Zoglin::isAdult, new MeleeAttack(40)), new RunIf(Zoglin::isBaby, new MeleeAttack(15)), new StopAttackingIfTargetInvalid()), MemoryModuleType.ATTACK_TARGET);
   }

   private Optional<? extends LivingEntity> findNearestValidAttackTarget() {
      return ((List)this.getBrain().getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES).orElse(ImmutableList.of())).stream().filter(Zoglin::isTargetable).findFirst();
   }

   private static boolean isTargetable(LivingEntity var0) {
      EntityType var1 = var0.getType();
      return var1 != EntityType.ZOGLIN && var1 != EntityType.CREEPER && EntitySelector.ATTACK_ALLOWED.test(var0);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BABY_ID, false);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_BABY_ID.equals(var1)) {
         this.refreshDimensions();
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896D).add(Attributes.KNOCKBACK_RESISTANCE, 0.6000000238418579D).add(Attributes.ATTACK_KNOCKBACK, 1.0D).add(Attributes.ATTACK_DAMAGE, 6.0D);
   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   public boolean doHurtTarget(Entity var1) {
      if (!(var1 instanceof LivingEntity)) {
         return false;
      } else {
         this.attackAnimationRemainingTicks = 10;
         this.level.broadcastEntityEvent(this, (byte)4);
         this.playSound(SoundEvents.ZOGLIN_ATTACK, 1.0F, this.getVoicePitch());
         return HoglinBase.hurtAndThrowTarget(this, (LivingEntity)var1);
      }
   }

   public boolean canBeLeashed(Player var1) {
      return !this.isLeashed();
   }

   protected void blockedByShield(LivingEntity var1) {
      if (!this.isBaby()) {
         HoglinBase.throwTarget(this, var1);
      }

   }

   public double getPassengersRidingOffset() {
      return (double)this.getBbHeight() - (this.isBaby() ? 0.2D : 0.15D);
   }

   public boolean hurt(DamageSource var1, float var2) {
      boolean var3 = super.hurt(var1, var2);
      if (this.level.isClientSide) {
         return false;
      } else if (var3 && var1.getEntity() instanceof LivingEntity) {
         LivingEntity var4 = (LivingEntity)var1.getEntity();
         if (EntitySelector.ATTACK_ALLOWED.test(var4) && !BehaviorUtils.isOtherTargetMuchFurtherAwayThanCurrentAttackTarget(this, var4, 4.0D)) {
            this.setAttackTarget(var4);
         }

         return var3;
      } else {
         return var3;
      }
   }

   private void setAttackTarget(LivingEntity var1) {
      this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      this.brain.setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, var1, 200L);
   }

   public Brain<Zoglin> getBrain() {
      return super.getBrain();
   }

   protected void updateActivity() {
      Activity var1 = (Activity)this.brain.getActiveNonCoreActivity().orElse((Object)null);
      this.brain.setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
      Activity var2 = (Activity)this.brain.getActiveNonCoreActivity().orElse((Object)null);
      if (var2 == Activity.FIGHT && var1 != Activity.FIGHT) {
         this.playAngrySound();
      }

      this.setAggressive(this.brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
   }

   protected void customServerAiStep() {
      this.level.getProfiler().push("zoglinBrain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().pop();
      this.updateActivity();
   }

   public void setBaby(boolean var1) {
      this.getEntityData().set(DATA_BABY_ID, var1);
      if (!this.level.isClientSide && var1) {
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5D);
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

   public void handleEntityEvent(byte var1) {
      if (var1 == 4) {
         this.attackAnimationRemainingTicks = 10;
         this.playSound(SoundEvents.ZOGLIN_ATTACK, 1.0F, this.getVoicePitch());
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public int getAttackAnimationRemainingTicks() {
      return this.attackAnimationRemainingTicks;
   }

   protected SoundEvent getAmbientSound() {
      if (this.level.isClientSide) {
         return null;
      } else {
         return this.brain.hasMemoryValue(MemoryModuleType.ATTACK_TARGET) ? SoundEvents.ZOGLIN_ANGRY : SoundEvents.ZOGLIN_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ZOGLIN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOGLIN_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.ZOGLIN_STEP, 0.15F, 1.0F);
   }

   protected void playAngrySound() {
      this.playSound(SoundEvents.ZOGLIN_ANGRY, 1.0F, this.getVoicePitch());
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.isBaby()) {
         var1.putBoolean("IsBaby", true);
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.getBoolean("IsBaby")) {
         this.setBaby(true);
      }

   }

   static {
      DATA_BABY_ID = SynchedEntityData.defineId(Zoglin.class, EntityDataSerializers.BOOLEAN);
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN);
   }
}
