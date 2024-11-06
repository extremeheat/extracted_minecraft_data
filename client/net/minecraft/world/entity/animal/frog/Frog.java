package net.minecraft.world.entity.animal.frog;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.Vec3;

public class Frog extends Animal implements VariantHolder<Holder<FrogVariant>> {
   protected static final ImmutableList<SensorType<? extends Sensor<? super Frog>>> SENSOR_TYPES;
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;
   private static final EntityDataAccessor<Holder<FrogVariant>> DATA_VARIANT_ID;
   private static final EntityDataAccessor<OptionalInt> DATA_TONGUE_TARGET_ID;
   private static final int FROG_FALL_DAMAGE_REDUCTION = 5;
   public static final String VARIANT_KEY = "variant";
   private static final ResourceKey<FrogVariant> DEFAULT_VARIANT;
   public final AnimationState jumpAnimationState = new AnimationState();
   public final AnimationState croakAnimationState = new AnimationState();
   public final AnimationState tongueAnimationState = new AnimationState();
   public final AnimationState swimIdleAnimationState = new AnimationState();

   public Frog(EntityType<? extends Animal> var1, Level var2) {
      super(var1, var2);
      this.lookControl = new FrogLookControl(this);
      this.setPathfindingMalus(PathType.WATER, 4.0F);
      this.setPathfindingMalus(PathType.TRAPDOOR, -1.0F);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
   }

   protected Brain.Provider<Frog> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return FrogAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<Frog> getBrain() {
      return super.getBrain();
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT_ID, BuiltInRegistries.FROG_VARIANT.getOrThrow(DEFAULT_VARIANT));
      var1.define(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
   }

   public void eraseTongueTarget() {
      this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.empty());
   }

   public Optional<Entity> getTongueTarget() {
      IntStream var10000 = ((OptionalInt)this.entityData.get(DATA_TONGUE_TARGET_ID)).stream();
      Level var10001 = this.level();
      Objects.requireNonNull(var10001);
      return var10000.mapToObj(var10001::getEntity).filter(Objects::nonNull).findFirst();
   }

   public void setTongueTarget(Entity var1) {
      this.entityData.set(DATA_TONGUE_TARGET_ID, OptionalInt.of(var1.getId()));
   }

   public int getHeadRotSpeed() {
      return 35;
   }

   public int getMaxHeadYRot() {
      return 5;
   }

   public Holder<FrogVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   public void setVariant(Holder<FrogVariant> var1) {
      this.entityData.set(DATA_VARIANT_ID, var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("variant", ((ResourceKey)this.getVariant().unwrapKey().orElse(DEFAULT_VARIANT)).location().toString());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(var1.getString("variant"))).map((var0) -> {
         return ResourceKey.create(Registries.FROG_VARIANT, var0);
      });
      Registry var10001 = BuiltInRegistries.FROG_VARIANT;
      Objects.requireNonNull(var10001);
      var10000.flatMap(var10001::get).ifPresent(this::setVariant);
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("frogBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      var2.push("frogActivityUpdate");
      FrogAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   public void tick() {
      if (this.level().isClientSide()) {
         this.swimIdleAnimationState.animateWhen(this.isInWaterOrBubble() && !this.walkAnimation.isMoving(), this.tickCount);
      }

      super.tick();
   }

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

   protected void updateWalkAnimation(float var1) {
      float var2;
      if (this.jumpAnimationState.isStarted()) {
         var2 = 0.0F;
      } else {
         var2 = Math.min(var1 * 25.0F, 1.0F);
      }

      this.walkAnimation.update(var2, 0.4F, this.isBaby() ? 3.0F : 1.0F);
   }

   public void playEatingSound() {
      this.level().playSound((Player)null, (Entity)this, SoundEvents.FROG_EAT, SoundSource.NEUTRAL, 2.0F, 1.0F);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Frog var3 = (Frog)EntityType.FROG.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null) {
         FrogAi.initMemories(var3, var1.getRandom());
      }

      return var3;
   }

   public boolean isBaby() {
      return false;
   }

   public void setBaby(boolean var1) {
   }

   public void spawnChildFromBreeding(ServerLevel var1, Animal var2) {
      this.finalizeSpawnChildFromBreeding(var1, var2, (AgeableMob)null);
      this.getBrain().setMemory(MemoryModuleType.IS_PREGNANT, (Object)Unit.INSTANCE);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      Holder var5 = var1.getBiome(this.blockPosition());
      if (var5.is(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)) {
         this.setVariant((Holder)BuiltInRegistries.FROG_VARIANT.getOrThrow(FrogVariant.COLD));
      } else if (var5.is(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)) {
         this.setVariant((Holder)BuiltInRegistries.FROG_VARIANT.getOrThrow(FrogVariant.WARM));
      } else {
         this.setVariant((Holder)BuiltInRegistries.FROG_VARIANT.getOrThrow(DEFAULT_VARIANT));
      }

      FrogAi.initMemories(this, var1.getRandom());
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.MAX_HEALTH, 10.0).add(Attributes.ATTACK_DAMAGE, 10.0).add(Attributes.STEP_HEIGHT, 1.0);
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.FROG_AMBIENT;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.FROG_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.FROG_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.FROG_STEP, 0.15F, 1.0F);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   protected int calculateFallDamage(float var1, float var2) {
      return super.calculateFallDamage(var1, var2) - 5;
   }

   public void travel(Vec3 var1) {
      if (this.isControlledByLocalInstance() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
      } else {
         super.travel(var1);
      }

   }

   public static boolean canEat(LivingEntity var0) {
      if (var0 instanceof Slime var1) {
         if (var1.getSize() != 1) {
            return false;
         }
      }

      return var0.getType().is(EntityTypeTags.FROG_FOOD);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new FrogPathNavigation(this, var1);
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.FROG_FOOD);
   }

   public static boolean checkFrogSpawnRules(EntityType<? extends Animal> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.FROGS_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   // $FF: synthetic method
   public Object getVariant() {
      return this.getVariant();
   }

   // $FF: synthetic method
   public void setVariant(final Object var1) {
      this.setVariant((Holder)var1);
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.FROG_ATTACKABLES, SensorType.FROG_TEMPTATIONS, SensorType.IS_IN_WATER);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.IS_IN_WATER, MemoryModuleType.IS_PREGNANT, MemoryModuleType.IS_PANICKING, MemoryModuleType.UNREACHABLE_TONGUE_TARGETS});
      DATA_VARIANT_ID = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.FROG_VARIANT);
      DATA_TONGUE_TARGET_ID = SynchedEntityData.defineId(Frog.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
      DEFAULT_VARIANT = FrogVariant.TEMPERATE;
   }

   private class FrogLookControl extends LookControl {
      FrogLookControl(final Mob var2) {
         super(var2);
      }

      protected boolean resetXRotOnTick() {
         return Frog.this.getTongueTarget().isEmpty();
      }
   }

   private static class FrogPathNavigation extends AmphibiousPathNavigation {
      FrogPathNavigation(Frog var1, Level var2) {
         super(var1, var2);
      }

      public boolean canCutCorner(PathType var1) {
         return var1 != PathType.WATER_BORDER && super.canCutCorner(var1);
      }

      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = new FrogNodeEvaluator(true);
         return new PathFinder(this.nodeEvaluator, var1);
      }
   }

   private static class FrogNodeEvaluator extends AmphibiousNodeEvaluator {
      private final BlockPos.MutableBlockPos belowPos = new BlockPos.MutableBlockPos();

      public FrogNodeEvaluator(boolean var1) {
         super(var1);
      }

      public Node getStart() {
         return !this.mob.isInWater() ? super.getStart() : this.getStartNode(new BlockPos(Mth.floor(this.mob.getBoundingBox().minX), Mth.floor(this.mob.getBoundingBox().minY), Mth.floor(this.mob.getBoundingBox().minZ)));
      }

      public PathType getPathType(PathfindingContext var1, int var2, int var3, int var4) {
         this.belowPos.set(var2, var3 - 1, var4);
         BlockState var5 = var1.getBlockState(this.belowPos);
         return var5.is(BlockTags.FROG_PREFER_JUMP_TO) ? PathType.OPEN : super.getPathType(var1, var2, var3, var4);
      }
   }
}
