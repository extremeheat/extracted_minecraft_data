package net.minecraft.world.entity.animal.frog;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;

public class Frog extends Animal implements VariantHolder<FrogVariant> {
   public static final Ingredient TEMPTATION_ITEM = Ingredient.of(Items.SLIME_BALL);
   protected static final ImmutableList<SensorType<? extends Sensor<? super Frog>>> SENSOR_TYPES = ImmutableList.of(
      SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_ATTACKABLES, SensorType.FROG_TEMPTATIONS, SensorType.IS_IN_WATER
   );
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
      MemoryModuleType.LOOK_TARGET,
      MemoryModuleType.NEAREST_LIVING_ENTITIES,
      MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
      MemoryModuleType.WALK_TARGET,
      MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
      MemoryModuleType.PATH,
      MemoryModuleType.BREED_TARGET,
      MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
      MemoryModuleType.LONG_JUMP_MID_JUMP,
      MemoryModuleType.ATTACK_TARGET,
      MemoryModuleType.TEMPTING_PLAYER,
      MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
      new MemoryModuleType[]{
         MemoryModuleType.IS_TEMPTED,
         MemoryModuleType.HURT_BY,
         MemoryModuleType.HURT_BY_ENTITY,
         MemoryModuleType.NEAREST_ATTACKABLE,
         MemoryModuleType.IS_IN_WATER,
         MemoryModuleType.IS_PREGNANT,
         MemoryModuleType.IS_PANICKING,
         MemoryModuleType.UNREACHABLE_TONGUE_TARGETS
      }
   );
   private static final EntityDataAccessor<FrogVariant> DATA_VARIANT_ID = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.FROG_VARIANT);
   private static final EntityDataAccessor<OptionalInt> DATA_TONGUE_TARGET_ID = SynchedEntityData.defineId(
      Frog.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT
   );
   private static final int FROG_FALL_DAMAGE_REDUCTION = 5;
   public static final String VARIANT_KEY = "variant";
   public final AnimationState jumpAnimationState = new AnimationState();
   public final AnimationState croakAnimationState = new AnimationState();
   public final AnimationState tongueAnimationState = new AnimationState();
   public final AnimationState walkAnimationState = new AnimationState();
   public final AnimationState swimAnimationState = new AnimationState();
   public final AnimationState swimIdleAnimationState = new AnimationState();

   public Frog(EntityType<? extends Animal> var1, Level var2) {
      super(var1, var2);
      this.lookControl = new Frog.FrogLookControl(this);
      this.setPathfindingMalus(BlockPathTypes.WATER, 4.0F);
      this.setPathfindingMalus(BlockPathTypes.TRAPDOOR, -1.0F);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
      this.maxUpStep = 1.0F;
   }

   @Override
   protected Brain.Provider<Frog> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return FrogAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   public Brain<Frog> getBrain() {
      return super.getBrain();
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VARIANT_ID, FrogVariant.TEMPERATE);
      this.entityData.define(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
   }

   public void eraseTongueTarget() {
      this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
   }

   public Optional<Entity> getTongueTarget() {
      return this.entityData.get(DATA_TONGUE_TARGET_ID).stream().mapToObj(this.level::getEntity).filter(Objects::nonNull).findFirst();
   }

   public void setTongueTarget(Entity var1) {
      this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.of(var1.getId()));
   }

   @Override
   public int getHeadRotSpeed() {
      return 35;
   }

   @Override
   public int getMaxHeadYRot() {
      return 5;
   }

   public FrogVariant getVariant() {
      return this.entityData.get(DATA_VARIANT_ID);
   }

   public void setVariant(FrogVariant var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("variant", BuiltInRegistries.FROG_VARIANT.getKey(this.getVariant()).toString());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      FrogVariant var2 = BuiltInRegistries.FROG_VARIANT.get(ResourceLocation.tryParse(var1.getString("variant")));
      if (var2 != null) {
         this.setVariant(var2);
      }
   }

   @Override
   public boolean canBreatheUnderwater() {
      return true;
   }

   private boolean isMovingOnLand() {
      return this.onGround && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && !this.isInWaterOrBubble();
   }

   private boolean isMovingInWater() {
      return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6 && this.isInWaterOrBubble();
   }

   @Override
   protected void customServerAiStep() {
      this.level.getProfiler().push("frogBrain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().pop();
      this.level.getProfiler().push("frogActivityUpdate");
      FrogAi.updateActivity(this);
      this.level.getProfiler().pop();
      super.customServerAiStep();
   }

   @Override
   public void tick() {
      if (this.level.isClientSide()) {
         if (this.isMovingOnLand()) {
            this.walkAnimationState.startIfStopped(this.tickCount);
         } else {
            this.walkAnimationState.stop();
         }

         if (this.isMovingInWater()) {
            this.swimIdleAnimationState.stop();
            this.swimAnimationState.startIfStopped(this.tickCount);
         } else if (this.isInWaterOrBubble()) {
            this.swimAnimationState.stop();
            this.swimIdleAnimationState.startIfStopped(this.tickCount);
         } else {
            this.swimAnimationState.stop();
            this.swimIdleAnimationState.stop();
         }
      }

      super.tick();
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_POSE.equals(var1)) {
         Pose var2 = this.getPose();
         if (var2 == Pose.LONG_JUMPING) {
            this.jumpAnimationState.start(this.tickCount);
         } else {
            this.jumpAnimationState.stop();
         }

         if (var2 == Pose.CROAKING) {
            this.croakAnimationState.start(this.tickCount);
         } else {
            this.croakAnimationState.stop();
         }

         if (var2 == Pose.USING_TONGUE) {
            this.tongueAnimationState.start(this.tickCount);
         } else {
            this.tongueAnimationState.stop();
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Frog var3 = EntityType.FROG.create(var1);
      if (var3 != null) {
         FrogAi.initMemories(var3, var1.getRandom());
      }

      return var3;
   }

   @Override
   public boolean isBaby() {
      return false;
   }

   @Override
   public void setBaby(boolean var1) {
   }

   @Override
   public void spawnChildFromBreeding(ServerLevel var1, Animal var2) {
      ServerPlayer var3 = this.getLoveCause();
      if (var3 == null) {
         var3 = var2.getLoveCause();
      }

      if (var3 != null) {
         var3.awardStat(Stats.ANIMALS_BRED);
         CriteriaTriggers.BRED_ANIMALS.trigger(var3, this, var2, null);
      }

      this.setAge(6000);
      var2.setAge(6000);
      this.resetLove();
      var2.resetLove();
      this.getBrain().setMemory(MemoryModuleType.IS_PREGNANT, Unit.INSTANCE);
      var1.broadcastEntityEvent(this, (byte)18);
      if (var1.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         var1.addFreshEntity(new ExperienceOrb(var1, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
      }
   }

   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      Holder var6 = var1.getBiome(this.blockPosition());
      if (var6.is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
         this.setVariant(FrogVariant.COLD);
      } else if (var6.is(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
         this.setVariant(FrogVariant.WARM);
      } else {
         this.setVariant(FrogVariant.TEMPERATE);
      }

      FrogAi.initMemories(this, var1.getRandom());
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.MAX_HEALTH, 10.0).add(Attributes.ATTACK_DAMAGE, 10.0);
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.FROG_AMBIENT;
   }

   @Nullable
   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.FROG_HURT;
   }

   @Nullable
   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.FROG_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.FROG_STEP, 0.15F, 1.0F);
   }

   @Override
   public boolean isPushedByFluid() {
      return false;
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   @Override
   protected int calculateFallDamage(float var1, float var2) {
      return super.calculateFallDamage(var1, var2) - 5;
   }

   @Override
   public void travel(Vec3 var1) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
      } else {
         super.travel(var1);
      }
   }

   @Override
   public boolean canCutCorner(BlockPathTypes var1) {
      return super.canCutCorner(var1) && var1 != BlockPathTypes.WATER_BORDER;
   }

   public static boolean canEat(LivingEntity var0) {
      if (var0 instanceof Slime var1 && var1.getSize() != 1) {
         return false;
      }

      return var0.getType().is(EntityTypeTags.FROG_FOOD);
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new Frog.FrogPathNavigation(this, var1);
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return TEMPTATION_ITEM.test(var1);
   }

   public static boolean checkFrogSpawnRules(EntityType<? extends Animal> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.FROGS_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   class FrogLookControl extends LookControl {
      FrogLookControl(Mob var2) {
         super(var2);
      }

      @Override
      protected boolean resetXRotOnTick() {
         return Frog.this.getTongueTarget().isEmpty();
      }
   }

   static class FrogNodeEvaluator extends AmphibiousNodeEvaluator {
      private final BlockPos.MutableBlockPos belowPos = new BlockPos.MutableBlockPos();

      public FrogNodeEvaluator(boolean var1) {
         super(var1);
      }

      @Override
      public Node getStart() {
         return !this.mob.isInWater()
            ? super.getStart()
            : this.getStartNode(
               new BlockPos(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY), Mth.floor(this.mob.getBoundingBox().minZ))
            );
      }

      @Override
      public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
         this.belowPos.set(var2, var3 - 1, var4);
         BlockState var5 = var1.getBlockState(this.belowPos);
         return var5.is(BlockTags.FROG_PREFER_JUMP_TO) ? BlockPathTypes.OPEN : super.getBlockPathType(var1, var2, var3, var4);
      }
   }

   static class FrogPathNavigation extends AmphibiousPathNavigation {
      FrogPathNavigation(Frog var1, Level var2) {
         super(var1, var2);
      }

      @Override
      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = new Frog.FrogNodeEvaluator(true);
         this.nodeEvaluator.setCanPassDoors(true);
         return new PathFinder(this.nodeEvaluator, var1);
      }
   }
}
