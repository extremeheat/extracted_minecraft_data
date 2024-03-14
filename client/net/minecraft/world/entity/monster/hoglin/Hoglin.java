package net.minecraft.world.entity.monster.hoglin;

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
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Hoglin extends Animal implements Enemy, HoglinBase {
   private static final EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION = SynchedEntityData.defineId(Hoglin.class, EntityDataSerializers.BOOLEAN);
   private static final float PROBABILITY_OF_SPAWNING_AS_BABY = 0.2F;
   private static final int MAX_HEALTH = 40;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3F;
   private static final int ATTACK_KNOCKBACK = 1;
   private static final float KNOCKBACK_RESISTANCE = 0.6F;
   private static final int ATTACK_DAMAGE = 6;
   private static final float BABY_ATTACK_DAMAGE = 0.5F;
   private static final int CONVERSION_TIME = 300;
   private int attackAnimationRemainingTicks;
   private int timeInOverworld;
   private boolean cannotBeHunted;
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Hoglin>>> SENSOR_TYPES = ImmutableList.of(
      SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ADULT, SensorType.HOGLIN_SPECIFIC_SENSOR
   );
   protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
      MemoryModuleType.BREED_TARGET,
      MemoryModuleType.NEAREST_LIVING_ENTITIES,
      MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
      MemoryModuleType.NEAREST_VISIBLE_PLAYER,
      MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
      MemoryModuleType.LOOK_TARGET,
      MemoryModuleType.WALK_TARGET,
      MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
      MemoryModuleType.PATH,
      MemoryModuleType.ATTACK_TARGET,
      MemoryModuleType.ATTACK_COOLING_DOWN,
      MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN,
      new MemoryModuleType[]{
         MemoryModuleType.AVOID_TARGET,
         MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
         MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
         MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS,
         MemoryModuleType.NEAREST_VISIBLE_ADULT,
         MemoryModuleType.NEAREST_REPELLENT,
         MemoryModuleType.PACIFIED,
         MemoryModuleType.IS_PANICKING
      }
   );

   public Hoglin(EntityType<? extends Hoglin> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
   }

   @Override
   public boolean canBeLeashed(Player var1) {
      return !this.isLeashed();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 40.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.6000000238418579)
         .add(Attributes.ATTACK_KNOCKBACK, 1.0)
         .add(Attributes.ATTACK_DAMAGE, 6.0);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      if (!(var1 instanceof LivingEntity)) {
         return false;
      } else {
         this.attackAnimationRemainingTicks = 10;
         this.level().broadcastEntityEvent(this, (byte)4);
         this.makeSound(SoundEvents.HOGLIN_ATTACK);
         HoglinAi.onHitTarget(this, (LivingEntity)var1);
         return HoglinBase.hurtAndThrowTarget(this, (LivingEntity)var1);
      }
   }

   @Override
   protected void blockedByShield(LivingEntity var1) {
      if (this.isAdult()) {
         HoglinBase.throwTarget(this, var1);
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      boolean var3 = super.hurt(var1, var2);
      if (this.level().isClientSide) {
         return false;
      } else {
         if (var3 && var1.getEntity() instanceof LivingEntity) {
            HoglinAi.wasHurtBy(this, (LivingEntity)var1.getEntity());
         }

         return var3;
      }
   }

   @Override
   protected Brain.Provider<Hoglin> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return HoglinAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   public Brain<Hoglin> getBrain() {
      return super.getBrain();
   }

   @Override
   protected void customServerAiStep() {
      this.level().getProfiler().push("hoglinBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      HoglinAi.updateActivity(this);
      if (this.isConverting()) {
         ++this.timeInOverworld;
         if (this.timeInOverworld > 300) {
            this.makeSound(SoundEvents.HOGLIN_CONVERTED_TO_ZOMBIFIED);
            this.finishConversion((ServerLevel)this.level());
         }
      } else {
         this.timeInOverworld = 0;
      }
   }

   @Override
   public void aiStep() {
      if (this.attackAnimationRemainingTicks > 0) {
         --this.attackAnimationRemainingTicks;
      }

      super.aiStep();
   }

   @Override
   protected void ageBoundaryReached() {
      if (this.isBaby()) {
         this.xpReward = 3;
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(0.5);
      } else {
         this.xpReward = 5;
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6.0);
      }
   }

   public static boolean checkHoglinSpawnRules(EntityType<Hoglin> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return !var1.getBlockState(var3.below()).is(Blocks.NETHER_WART_BLOCK);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      if (var1.getRandom().nextFloat() < 0.2F) {
         this.setBaby(true);
      }

      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return !this.isPersistenceRequired();
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      if (HoglinAi.isPosNearNearestRepellent(this, var1)) {
         return -1.0F;
      } else {
         return var2.getBlockState(var1.below()).is(Blocks.CRIMSON_NYLIUM) ? 10.0F : 0.0F;
      }
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      InteractionResult var3 = super.mobInteract(var1, var2);
      if (var3.consumesAction()) {
         this.setPersistenceRequired();
      }

      return var3;
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 4) {
         this.attackAnimationRemainingTicks = 10;
         this.makeSound(SoundEvents.HOGLIN_ATTACK);
      } else {
         super.handleEntityEvent(var1);
      }
   }

   @Override
   public int getAttackAnimationRemainingTicks() {
      return this.attackAnimationRemainingTicks;
   }

   @Override
   public boolean shouldDropExperience() {
      return true;
   }

   @Override
   public int getExperienceReward() {
      return this.xpReward;
   }

   private void finishConversion(ServerLevel var1) {
      Zoglin var2 = this.convertTo(EntityType.ZOGLIN, true);
      if (var2 != null) {
         var2.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
      }
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(Items.CRIMSON_FUNGUS);
   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
   }

   @Override
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

   @Override
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
      return this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
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
   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Hoglin var3 = EntityType.HOGLIN.create(var1);
      if (var3 != null) {
         var3.setPersistenceRequired();
      }

      return var3;
   }

   @Override
   public boolean canFallInLove() {
      return !HoglinAi.isPacified(this) && super.canFallInLove();
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.level().isClientSide ? null : HoglinAi.getSoundForCurrentActivity(this).orElse(null);
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.HOGLIN_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.HOGLIN_DEATH;
   }

   @Override
   protected SoundEvent getSwimSound() {
      return SoundEvents.HOSTILE_SWIM;
   }

   @Override
   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.HOSTILE_SPLASH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.HOGLIN_STEP, 0.15F, 1.0F);
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }
}
