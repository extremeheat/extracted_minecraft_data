package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.BinaryAnimator;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.EasingType;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Axolotl extends Animal implements Bucketable {
   public static final int TOTAL_PLAYDEAD_TIME = 200;
   private static final int POSE_ANIMATION_TICKS = 10;
   private static final Brain.Provider<Axolotl> BRAIN_PROVIDER;
   private static final EntityDataAccessor<Integer> DATA_VARIANT;
   private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD;
   private static final EntityDataAccessor<Boolean> FROM_BUCKET;
   public static final double PLAYER_REGEN_DETECTION_RANGE = 20.0;
   public static final int RARE_VARIANT_CHANCE = 1200;
   private static final int AXOLOTL_TOTAL_AIR_SUPPLY = 6000;
   public static final String VARIANT_TAG = "Variant";
   private static final int REHYDRATE_AIR_SUPPLY = 1800;
   private static final int REGEN_BUFF_MAX_DURATION = 2400;
   private static final boolean DEFAULT_FROM_BUCKET = false;
   public final BinaryAnimator playingDeadAnimator;
   public final BinaryAnimator inWaterAnimator;
   public final BinaryAnimator onGroundAnimator;
   public final BinaryAnimator movingAnimator;
   public final AnimationState swimAnimationState;
   public final AnimationState walkAnimationState;
   public final AnimationState walkUnderWaterAnimationState;
   public final AnimationState idleUnderWaterAnimationState;
   public final AnimationState idleUnderWaterOnGroundAnimationState;
   public final AnimationState idleOnGroundAnimationState;
   public final AnimationState playDeadAnimationState;
   private final ImmutableList<AnimationState> ALL_ANIMATIONS;
   private static final EntityDimensions BABY_DIMENSIONS;
   private static final int REGEN_BUFF_BASE_DURATION = 100;

   public Axolotl(final EntityType<? extends Axolotl> type, final Level level) {
      super(type, level);
      this.playingDeadAnimator = new BinaryAnimator(10, EasingType.IN_OUT_SINE);
      this.inWaterAnimator = new BinaryAnimator(10, EasingType.IN_OUT_SINE);
      this.onGroundAnimator = new BinaryAnimator(10, EasingType.IN_OUT_SINE);
      this.movingAnimator = new BinaryAnimator(10, EasingType.IN_OUT_SINE);
      this.swimAnimationState = new AnimationState();
      this.walkAnimationState = new AnimationState();
      this.walkUnderWaterAnimationState = new AnimationState();
      this.idleUnderWaterAnimationState = new AnimationState();
      this.idleUnderWaterOnGroundAnimationState = new AnimationState();
      this.idleOnGroundAnimationState = new AnimationState();
      this.playDeadAnimationState = new AnimationState();
      this.ALL_ANIMATIONS = ImmutableList.of(this.swimAnimationState, this.walkAnimationState, this.walkUnderWaterAnimationState, this.idleUnderWaterAnimationState, this.idleUnderWaterOnGroundAnimationState, this.idleOnGroundAnimationState, this.playDeadAnimationState);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.moveControl = new AxolotlMoveControl(this);
      this.lookControl = new AxolotlLookControl(this, 20);
   }

   public float getWalkTargetValue(final BlockPos pos, final LevelReader level) {
      return 0.0F;
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_VARIANT, 0);
      entityData.define(DATA_PLAYING_DEAD, false);
      entityData.define(FROM_BUCKET, false);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("Variant", Axolotl.Variant.LEGACY_CODEC, this.getVariant());
      output.putBoolean("FromBucket", this.fromBucket());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setVariant((Variant)input.read("Variant", Axolotl.Variant.LEGACY_CODEC).orElse(Axolotl.Variant.DEFAULT));
      this.setFromBucket(input.getBooleanOr("FromBucket", false));
   }

   public void playAmbientSound() {
      if (!this.isPlayingDead()) {
         super.playAmbientSound();
      }
   }

   public SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
      boolean isBaby = false;
      if (spawnReason == EntitySpawnReason.BUCKET) {
         return groupData;
      } else {
         RandomSource random = level.getRandom();
         if (groupData instanceof AxolotlGroupData) {
            if (((AxolotlGroupData)groupData).getGroupSize() >= 2) {
               isBaby = true;
            }
         } else {
            groupData = new AxolotlGroupData(new Variant[]{Axolotl.Variant.getCommonSpawnVariant(random), Axolotl.Variant.getCommonSpawnVariant(random)});
         }

         this.setVariant(((AxolotlGroupData)groupData).getVariant(random));
         if (isBaby) {
            this.setAge(-24000);
         }

         return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
      }
   }

   public void baseTick() {
      int airSupply = this.getAirSupply();
      super.baseTick();
      if (!this.isNoAi()) {
         Level var3 = this.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)var3;
            this.handleAirSupply(serverLevel, airSupply);
         }
      }

      if (this.level().isClientSide()) {
         if (this.isBaby()) {
            this.tickBabyAnimations();
         } else {
            this.tickAdultAnimations();
         }
      }

   }

   private void tickBabyAnimations() {
      boolean isPlayingDead = this.isPlayingDead();
      boolean isInWater = this.isInWater();
      boolean onGround = this.onGround();
      boolean isMoving = this.walkAnimation.isMoving() || this.getXRot() != this.xRotO || this.getYRot() != this.yRotO;
      this.movingAnimator.tick(isMoving);
      if (isPlayingDead) {
         this.soloAnimation(this.playDeadAnimationState);
      } else {
         if (isMoving) {
            if (isInWater && !onGround) {
               this.soloAnimation(this.swimAnimationState);
            } else if (!isInWater && onGround) {
               this.soloAnimation(this.walkAnimationState);
            } else {
               this.soloAnimation(this.walkUnderWaterAnimationState);
            }
         } else if (isInWater && !onGround) {
            this.soloAnimation(this.idleUnderWaterAnimationState);
         } else if (isInWater && onGround) {
            this.soloAnimation(this.idleUnderWaterOnGroundAnimationState);
         } else {
            this.soloAnimation(this.idleOnGroundAnimationState);
         }

      }
   }

   private void soloAnimation(final AnimationState toStart) {
      UnmodifiableIterator var2 = this.ALL_ANIMATIONS.iterator();

      while(var2.hasNext()) {
         AnimationState animation = (AnimationState)var2.next();
         if (animation == toStart) {
            animation.startIfStopped(this.tickCount);
         } else {
            animation.stop();
         }
      }

   }

   private void tickAdultAnimations() {
      AxolotlAnimationState animationState;
      if (this.isPlayingDead()) {
         animationState = Axolotl.AxolotlAnimationState.PLAYING_DEAD;
      } else if (this.isInWater()) {
         animationState = Axolotl.AxolotlAnimationState.IN_WATER;
      } else if (this.onGround()) {
         animationState = Axolotl.AxolotlAnimationState.ON_GROUND;
      } else {
         animationState = Axolotl.AxolotlAnimationState.IN_AIR;
      }

      this.playingDeadAnimator.tick(animationState == Axolotl.AxolotlAnimationState.PLAYING_DEAD);
      this.inWaterAnimator.tick(animationState == Axolotl.AxolotlAnimationState.IN_WATER);
      this.onGroundAnimator.tick(animationState == Axolotl.AxolotlAnimationState.ON_GROUND);
      boolean isMoving = this.walkAnimation.isMoving() || this.getXRot() != this.xRotO || this.getYRot() != this.yRotO;
      this.movingAnimator.tick(isMoving);
   }

   protected void handleAirSupply(final ServerLevel level, final int preTickAirSupply) {
      if (this.isAlive() && !this.isInWaterOrRain()) {
         this.setAirSupply(preTickAirSupply - 1);
         if (this.shouldTakeDrowningDamage()) {
            this.setAirSupply(0);
            this.hurtServer(level, this.damageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAirSupply(this.getMaxAirSupply());
      }

   }

   public void rehydrate() {
      int newAirSupply = this.getAirSupply() + 1800;
      this.setAirSupply(Math.min(newAirSupply, this.getMaxAirSupply()));
   }

   public int getMaxAirSupply() {
      return 6000;
   }

   public Variant getVariant() {
      return Axolotl.Variant.byId((Integer)this.entityData.get(DATA_VARIANT));
   }

   private void setVariant(final Variant variant) {
      this.entityData.set(DATA_VARIANT, variant.getId());
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.AXOLOTL_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.AXOLOTL_VARIANT);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.AXOLOTL_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponents.AXOLOTL_VARIANT, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   private static boolean useRareVariant(final RandomSource random) {
      return random.nextInt(1200) == 0;
   }

   public boolean checkSpawnObstruction(final LevelReader level) {
      return level.isUnobstructed(this);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public void setPlayingDead(final boolean playingDead) {
      this.entityData.set(DATA_PLAYING_DEAD, playingDead);
   }

   public boolean isPlayingDead() {
      return (Boolean)this.entityData.get(DATA_PLAYING_DEAD);
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(final boolean fromBucket) {
      this.entityData.set(FROM_BUCKET, fromBucket);
   }

   public @Nullable AgeableMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      Axolotl baby = EntityType.AXOLOTL.create(level, EntitySpawnReason.BREEDING);
      if (baby != null) {
         Variant variant;
         if (useRareVariant(this.random)) {
            variant = Axolotl.Variant.getRareSpawnVariant(this.random);
         } else {
            variant = this.random.nextBoolean() ? this.getVariant() : ((Axolotl)partner).getVariant();
         }

         baby.setVariant(variant);
         baby.setPersistenceRequired();
      }

      return baby;
   }

   public boolean isFood(final ItemStack itemStack) {
      return itemStack.is(ItemTags.AXOLOTL_FOOD);
   }

   public boolean canBeLeashed() {
      return true;
   }

   protected void customServerAiStep(final ServerLevel level) {
      ProfilerFiller profiler = Profiler.get();
      profiler.push("axolotlBrain");
      this.getBrain().tick(level, this);
      profiler.pop();
      profiler.push("axolotlActivityUpdate");
      AxolotlAi.updateActivity(this);
      profiler.pop();
      if (!this.isNoAi()) {
         Optional<Integer> playDeadTicks = this.getBrain().<Integer>getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
         this.setPlayingDead(playDeadTicks.isPresent() && (Integer)playDeadTicks.get() > 0);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.STEP_HEIGHT, 1.0);
   }

   protected PathNavigation createNavigation(final Level level) {
      return new AmphibiousPathNavigation(this, level);
   }

   public void playAttackSound() {
      this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      float currentHealth = this.getHealth();
      if (!this.isNoAi() && this.random.nextInt(3) == 0 && ((float)this.random.nextInt(3) < damage || currentHealth / this.getMaxHealth() < 0.5F) && damage < currentHealth && this.isInWater() && (source.getEntity() != null || source.getDirectEntity() != null) && !this.isPlayingDead()) {
         this.brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, 200);
      }

      return super.hurtServer(level, source, damage);
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   public InteractionResult mobInteract(final Player player, final InteractionHand hand) {
      return (InteractionResult)Bucketable.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
   }

   public void saveToBucketTag(final ItemStack bucket) {
      Bucketable.saveDefaultDataToBucketTag(this, bucket);
      bucket.copyFrom(DataComponents.AXOLOTL_VARIANT, this);
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, bucket, (tag) -> {
         tag.putInt("Age", this.getAge());
         tag.putBoolean("AgeLocked", this.isAgeLocked());
         Brain<?> brain = this.getBrain();
         if (brain.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
            tag.putLong("HuntingCooldown", brain.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
         }

      });
   }

   public void loadFromBucketTag(final CompoundTag tag) {
      Bucketable.loadDefaultDataFromBucketTag(this, tag);
      this.setAge(tag.getIntOr("Age", 0));
      this.setAgeLocked(tag.getBooleanOr("AgeLocked", false));
      tag.getLong("HuntingCooldown").ifPresentOrElse((huntingCooldown) -> this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, tag.getLongOr("HuntingCooldown", 0L)), () -> this.getBrain().setMemory(MemoryModuleType.HAS_HUNTING_COOLDOWN, Optional.empty()));
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.AXOLOTL_BUCKET);
   }

   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_AXOLOTL;
   }

   public boolean canBeSeenAsEnemy() {
      return !this.isPlayingDead() && super.canBeSeenAsEnemy();
   }

   public static void onStopAttacking(final ServerLevel level, final Axolotl body, final LivingEntity target) {
      if (target.isDeadOrDying()) {
         DamageSource lastDamageSource = target.getLastDamageSource();
         if (lastDamageSource != null) {
            Entity entity = lastDamageSource.getEntity();
            if (entity instanceof Player) {
               Player player = (Player)entity;
               List<Player> playersInRange = level.getEntitiesOfClass(Player.class, body.getBoundingBox().inflate(20.0));
               if (playersInRange.contains(player)) {
                  body.applySupportingEffects(player);
               }
            }
         }
      }

   }

   public void applySupportingEffects(final Player player) {
      MobEffectInstance regenEffect = player.getEffect(MobEffects.REGENERATION);
      if (regenEffect == null || regenEffect.endsWithin(2399)) {
         int previousDuration = regenEffect != null ? regenEffect.getDuration() : 0;
         int regenDuration = Math.min(2400, 100 + previousDuration);
         player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenDuration, 0), this);
      }

      player.removeEffect(MobEffects.MINING_FATIGUE);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.AXOLOTL_HURT;
   }

   protected @Nullable SoundEvent getDeathSound() {
      return SoundEvents.AXOLOTL_DEATH;
   }

   protected @Nullable SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.AXOLOTL_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.AXOLOTL_SWIM;
   }

   protected Brain<Axolotl> makeBrain(final Brain.Packed packedBrain) {
      return BRAIN_PROVIDER.makeBrain(this, packedBrain);
   }

   public Brain<Axolotl> getBrain() {
      return super.getBrain();
   }

   protected void travelInWater(final Vec3 input, final double baseGravity, final boolean isFalling, final double oldY) {
      this.moveRelative(this.getSpeed(), input);
      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
   }

   protected void usePlayerItem(final Player player, final InteractionHand hand, final ItemStack itemStack) {
      if (itemStack.is(Items.TROPICAL_FISH_BUCKET)) {
         player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.WATER_BUCKET)));
      } else {
         super.usePlayerItem(player, hand, itemStack);
      }

   }

   public boolean removeWhenFarAway(final double distSqr) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public @Nullable LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   public static boolean checkAxolotlSpawnRules(final EntityType<? extends LivingEntity> type, final ServerLevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      return level.getBlockState(pos.below()).is(BlockTags.AXOLOTLS_SPAWNABLE_ON);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   static {
      BRAIN_PROVIDER = Brain.<Axolotl>provider(List.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.FOOD_TEMPTATIONS), (var0) -> AxolotlAi.getActivities());
      DATA_VARIANT = SynchedEntityData.<Integer>defineId(Axolotl.class, EntityDataSerializers.INT);
      DATA_PLAYING_DEAD = SynchedEntityData.<Boolean>defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
      FROM_BUCKET = SynchedEntityData.<Boolean>defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
      BABY_DIMENSIONS = EntityDimensions.scalable(0.5F, 0.25F).withEyeHeight(0.2F);
   }

   public static enum Variant implements StringRepresentable {
      LUCY(0, "lucy", true),
      WILD(1, "wild", true),
      GOLD(2, "gold", true),
      CYAN(3, "cyan", true),
      BLUE(4, "blue", false);

      public static final Variant DEFAULT = LUCY;
      private static final IntFunction<Variant> BY_ID = ByIdMap.<Variant>continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Variant> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Variant::getId);
      public static final Codec<Variant> CODEC = StringRepresentable.<Variant>fromEnum(Variant::values);
      /** @deprecated */
      @Deprecated
      public static final Codec<Variant> LEGACY_CODEC;
      private final int id;
      private final String name;
      private final boolean common;

      private Variant(final int id, final String name, final boolean common) {
         this.id = id;
         this.name = name;
         this.common = common;
      }

      public int getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static Variant byId(final int id) {
         return (Variant)BY_ID.apply(id);
      }

      public static Variant getCommonSpawnVariant(final RandomSource random) {
         return getSpawnVariant(random, true);
      }

      public static Variant getRareSpawnVariant(final RandomSource random) {
         return getSpawnVariant(random, false);
      }

      private static Variant getSpawnVariant(final RandomSource random, final boolean common) {
         Variant[] validVariants = (Variant[])Arrays.stream(values()).filter((v) -> v.common == common).toArray((x$0) -> new Variant[x$0]);
         return (Variant)Util.getRandom(validVariants, random);
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{LUCY, WILD, GOLD, CYAN, BLUE};
      }

      static {
         PrimitiveCodec var10000 = Codec.INT;
         IntFunction var10001 = BY_ID;
         Objects.requireNonNull(var10001);
         LEGACY_CODEC = var10000.xmap(var10001::apply, Variant::getId);
      }
   }

   private static class AxolotlMoveControl extends SmoothSwimmingMoveControl {
      private final Axolotl axolotl;

      public AxolotlMoveControl(final Axolotl axolotl) {
         super(axolotl, 85, 10, 0.1F, 0.5F, false);
         this.axolotl = axolotl;
      }

      public void tick() {
         if (!this.axolotl.isPlayingDead()) {
            super.tick();
         }

      }
   }

   private class AxolotlLookControl extends SmoothSwimmingLookControl {
      public AxolotlLookControl(final Axolotl axolotl, final int maxYRotFromCenter) {
         Objects.requireNonNull(Axolotl.this);
         super(axolotl, maxYRotFromCenter);
      }

      public void tick() {
         if (!Axolotl.this.isPlayingDead()) {
            super.tick();
         }

      }
   }

   public static class AxolotlGroupData extends AgeableMob.AgeableMobGroupData {
      public final Variant[] types;

      public AxolotlGroupData(final Variant... types) {
         super(false);
         this.types = types;
      }

      public Variant getVariant(final RandomSource random) {
         return this.types[random.nextInt(this.types.length)];
      }
   }

   public static enum AxolotlAnimationState {
      PLAYING_DEAD,
      IN_WATER,
      ON_GROUND,
      IN_AIR;

      private AxolotlAnimationState() {
      }

      // $FF: synthetic method
      private static AxolotlAnimationState[] $values() {
         return new AxolotlAnimationState[]{PLAYING_DEAD, IN_WATER, ON_GROUND, IN_AIR};
      }
   }
}
