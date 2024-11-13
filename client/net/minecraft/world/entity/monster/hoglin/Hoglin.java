package net.minecraft.world.entity.monster.hoglin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Hoglin extends Animal implements Enemy, HoglinBase {
   private static final EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION;
   private static final int MAX_HEALTH = 40;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3F;
   private static final int ATTACK_KNOCKBACK = 1;
   private static final float KNOCKBACK_RESISTANCE = 0.6F;
   private static final int ATTACK_DAMAGE = 6;
   private static final float BABY_ATTACK_DAMAGE = 0.5F;
   public static final int CONVERSION_TIME = 300;
   private int attackAnimationRemainingTicks;
   private int timeInOverworld;
   private boolean cannotBeHunted;
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Hoglin>>> SENSOR_TYPES;
   protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES;

   public Hoglin(EntityType<? extends Hoglin> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
   }

   @VisibleForTesting
   public void setTimeInOverworld(int var1) {
      this.timeInOverworld = var1;
   }

   public boolean canBeLeashed() {
      return true;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.KNOCKBACK_RESISTANCE, 0.6000000238418579).add(Attributes.ATTACK_KNOCKBACK, 1.0).add(Attributes.ATTACK_DAMAGE, 6.0);
   }

   public boolean doHurtTarget(ServerLevel var1, Entity var2) {
      if (var2 instanceof LivingEntity var3) {
         this.attackAnimationRemainingTicks = 10;
         this.level().broadcastEntityEvent(this, (byte)4);
         this.makeSound(SoundEvents.HOGLIN_ATTACK);
         HoglinAi.onHitTarget(this, var3);
         return HoglinBase.hurtAndThrowTarget(var1, this, var3);
      } else {
         return false;
      }
   }

   protected void blockedByShield(LivingEntity var1) {
      if (this.isAdult()) {
         HoglinBase.throwTarget(this, var1);
      }

   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      boolean var4 = super.hurtServer(var1, var2, var3);
      if (var4) {
         Entity var6 = var2.getEntity();
         if (var6 instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)var6;
            HoglinAi.wasHurtBy(var1, this, var5);
         }
      }

      return var4;
   }

   protected Brain.Provider<Hoglin> brainProvider() {
      return Brain.<Hoglin>provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return HoglinAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<Hoglin> getBrain() {
      return super.getBrain();
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("hoglinBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      HoglinAi.updateActivity(this);
      if (this.isConverting()) {
         ++this.timeInOverworld;
         if (this.timeInOverworld > 300) {
            this.makeSound(SoundEvents.HOGLIN_CONVERTED_TO_ZOMBIFIED);
            this.finishConversion();
         }
      } else {
         this.timeInOverworld = 0;
      }

   }

   public void aiStep() {
      if (this.attackAnimationRemainingTicks > 0) {
         --this.attackAnimationRemainingTicks;
      }

      super.aiStep();
   }

   protected void ageBoundaryReached() {
      if (this.isBaby()) {
         this.xpReward = 3;
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5);
      } else {
         this.xpReward = 5;
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0);
      }

   }

   public static boolean checkHoglinSpawnRules(EntityType<Hoglin> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return !var1.getBlockState(var3.below()).is(Blocks.NETHER_WART_BLOCK);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      if (var1.getRandom().nextFloat() < 0.2F) {
         this.setBaby(true);
      }

      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public boolean removeWhenFarAway(double var1) {
      return !this.isPersistenceRequired();
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      if (HoglinAi.isPosNearNearestRepellent(this, var1)) {
         return -1.0F;
      } else {
         return var2.getBlockState(var1.below()).is(Blocks.CRIMSON_NYLIUM) ? 10.0F : 0.0F;
      }
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      InteractionResult var3 = super.mobInteract(var1, var2);
      if (var3.consumesAction()) {
         this.setPersistenceRequired();
      }

      return var3;
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 4) {
         this.attackAnimationRemainingTicks = 10;
         this.makeSound(SoundEvents.HOGLIN_ATTACK);
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public int getAttackAnimationRemainingTicks() {
      return this.attackAnimationRemainingTicks;
   }

   public boolean shouldDropExperience() {
      return true;
   }

   protected int getBaseExperienceReward(ServerLevel var1) {
      return this.xpReward;
   }

   private void finishConversion() {
      this.convertTo(EntityType.ZOGLIN, ConversionParams.single(this, true, false), (var0) -> var0.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0)));
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.HOGLIN_FOOD);
   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.isImmuneToZombification()) {
         var1.putBoolean("IsImmuneToZombification", true);
      }

      var1.putInt("TimeInOverworld", this.timeInOverworld);
      if (this.cannotBeHunted) {
         var1.putBoolean("CannotBeHunted", true);
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setImmuneToZombification(var1.getBoolean("IsImmuneToZombification"));
      this.timeInOverworld = var1.getInt("TimeInOverworld");
      this.setCannotBeHunted(var1.getBoolean("CannotBeHunted"));
   }

   public void setImmuneToZombification(boolean var1) {
      this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, var1);
   }

   private boolean isImmuneToZombification() {
      return (Boolean)this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
   }

   public boolean isConverting() {
      return !this.level().dimensionType().piglinSafe() && !this.isImmuneToZombification() && !this.isNoAi();
   }

   private void setCannotBeHunted(boolean var1) {
      this.cannotBeHunted = var1;
   }

   public boolean canBeHunted() {
      return this.isAdult() && !this.cannotBeHunted;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Hoglin var3 = EntityType.HOGLIN.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null) {
         var3.setPersistenceRequired();
      }

      return var3;
   }

   public boolean canFallInLove() {
      return !HoglinAi.isPacified(this) && super.canFallInLove();
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return this.level().isClientSide ? null : (SoundEvent)HoglinAi.getSoundForCurrentActivity(this).orElse((Object)null);
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.HOGLIN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HOGLIN_DEATH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.HOSTILE_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.HOSTILE_SPLASH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.HOGLIN_STEP, 0.15F, 1.0F);
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   static {
      DATA_IMMUNE_TO_ZOMBIFICATION = SynchedEntityData.<Boolean>defineId(Hoglin.class, EntityDataSerializers.BOOLEAN);
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ADULT, SensorType.HOGLIN_SPECIFIC_SENSOR);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, new MemoryModuleType[]{MemoryModuleType.AVOID_TARGET, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_REPELLENT, MemoryModuleType.PACIFIED, MemoryModuleType.IS_PANICKING});
   }
}
