package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.PrimitiveCodec;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
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

public class Axolotl extends Animal implements Bucketable {
   public static final int TOTAL_PLAYDEAD_TIME = 200;
   private static final int POSE_ANIMATION_TICKS = 10;
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Axolotl>>> SENSOR_TYPES;
   protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES;
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
   public final BinaryAnimator playingDeadAnimator = new BinaryAnimator(10, Mth::easeInOutSine);
   public final BinaryAnimator inWaterAnimator = new BinaryAnimator(10, Mth::easeInOutSine);
   public final BinaryAnimator onGroundAnimator = new BinaryAnimator(10, Mth::easeInOutSine);
   public final BinaryAnimator movingAnimator = new BinaryAnimator(10, Mth::easeInOutSine);
   private static final int REGEN_BUFF_BASE_DURATION = 100;

   public Axolotl(EntityType<? extends Axolotl> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.moveControl = new AxolotlMoveControl(this);
      this.lookControl = new AxolotlLookControl(this, 20);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT, 0);
      var1.define(DATA_PLAYING_DEAD, false);
      var1.define(FROM_BUCKET, false);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.store("Variant", Axolotl.Variant.LEGACY_CODEC, this.getVariant());
      var1.putBoolean("FromBucket", this.fromBucket());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant((Variant)var1.read("Variant", Axolotl.Variant.LEGACY_CODEC).orElse(Axolotl.Variant.DEFAULT));
      this.setFromBucket(var1.getBooleanOr("FromBucket", false));
   }

   public void playAmbientSound() {
      if (!this.isPlayingDead()) {
         super.playAmbientSound();
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      boolean var5 = false;
      if (var3 == EntitySpawnReason.BUCKET) {
         return (SpawnGroupData)var4;
      } else {
         RandomSource var6 = var1.getRandom();
         if (var4 instanceof AxolotlGroupData) {
            if (((AxolotlGroupData)var4).getGroupSize() >= 2) {
               var5 = true;
            }
         } else {
            var4 = new AxolotlGroupData(new Variant[]{Axolotl.Variant.getCommonSpawnVariant(var6), Axolotl.Variant.getCommonSpawnVariant(var6)});
         }

         this.setVariant(((AxolotlGroupData)var4).getVariant(var6));
         if (var5) {
            this.setAge(-24000);
         }

         return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
      }
   }

   public void baseTick() {
      int var1 = this.getAirSupply();
      super.baseTick();
      if (!this.isNoAi()) {
         Level var3 = this.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            this.handleAirSupply(var2, var1);
         }
      }

      if (this.level().isClientSide()) {
         this.tickAnimations();
      }

   }

   private void tickAnimations() {
      AnimationState var1;
      if (this.isPlayingDead()) {
         var1 = Axolotl.AnimationState.PLAYING_DEAD;
      } else if (this.isInWater()) {
         var1 = Axolotl.AnimationState.IN_WATER;
      } else if (this.onGround()) {
         var1 = Axolotl.AnimationState.ON_GROUND;
      } else {
         var1 = Axolotl.AnimationState.IN_AIR;
      }

      this.playingDeadAnimator.tick(var1 == Axolotl.AnimationState.PLAYING_DEAD);
      this.inWaterAnimator.tick(var1 == Axolotl.AnimationState.IN_WATER);
      this.onGroundAnimator.tick(var1 == Axolotl.AnimationState.ON_GROUND);
      boolean var2 = this.walkAnimation.isMoving() || this.getXRot() != this.xRotO || this.getYRot() != this.yRotO;
      this.movingAnimator.tick(var2);
   }

   protected void handleAirSupply(ServerLevel var1, int var2) {
      if (this.isAlive() && !this.isInWaterOrRain()) {
         this.setAirSupply(var2 - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurtServer(var1, this.damageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAirSupply(this.getMaxAirSupply());
      }

   }

   public void rehydrate() {
      int var1 = this.getAirSupply() + 1800;
      this.setAirSupply(Math.min(var1, this.getMaxAirSupply()));
   }

   public int getMaxAirSupply() {
      return 6000;
   }

   public Variant getVariant() {
      return Axolotl.Variant.byId((Integer)this.entityData.get(DATA_VARIANT));
   }

   private void setVariant(Variant var1) {
      this.entityData.set(DATA_VARIANT, var1.getId());
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.AXOLOTL_VARIANT ? castComponentValue(var1, this.getVariant()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.AXOLOTL_VARIANT);
      super.applyImplicitComponents(var1);
   }

   protected <T> boolean applyImplicitComponent(DataComponentType<T> var1, T var2) {
      if (var1 == DataComponents.AXOLOTL_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponents.AXOLOTL_VARIANT, var2));
         return true;
      } else {
         return super.applyImplicitComponent(var1, var2);
      }
   }

   private static boolean useRareVariant(RandomSource var0) {
      return var0.nextInt(1200) == 0;
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public void setPlayingDead(boolean var1) {
      this.entityData.set(DATA_PLAYING_DEAD, var1);
   }

   public boolean isPlayingDead() {
      return (Boolean)this.entityData.get(DATA_PLAYING_DEAD);
   }

   public boolean fromBucket() {
      return (Boolean)this.entityData.get(FROM_BUCKET);
   }

   public void setFromBucket(boolean var1) {
      this.entityData.set(FROM_BUCKET, var1);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Axolotl var3 = EntityType.AXOLOTL.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null) {
         Variant var4;
         if (useRareVariant(this.random)) {
            var4 = Axolotl.Variant.getRareSpawnVariant(this.random);
         } else {
            var4 = this.random.nextBoolean() ? this.getVariant() : ((Axolotl)var2).getVariant();
         }

         var3.setVariant(var4);
         var3.setPersistenceRequired();
      }

      return var3;
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.AXOLOTL_FOOD);
   }

   public boolean canBeLeashed() {
      return true;
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("axolotlBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      var2.push("axolotlActivityUpdate");
      AxolotlAi.updateActivity(this);
      var2.pop();
      if (!this.isNoAi()) {
         Optional var3 = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
         this.setPlayingDead(var3.isPresent() && (Integer)var3.get() > 0);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.STEP_HEIGHT, 1.0);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new AmphibiousPathNavigation(this, var1);
   }

   public void playAttackSound() {
      this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      float var4 = this.getHealth();
      if (!this.isNoAi() && this.level().random.nextInt(3) == 0 && ((float)this.level().random.nextInt(3) < var3 || var4 / this.getMaxHealth() < 0.5F) && var3 < var4 && this.isInWater() && (var2.getEntity() != null || var2.getDirectEntity() != null) && !this.isPlayingDead()) {
         this.brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, 200);
      }

      return super.hurtServer(var1, var2, var3);
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      return (InteractionResult)Bucketable.bucketMobPickup(var1, var2, this).orElse(super.mobInteract(var1, var2));
   }

   public void saveToBucketTag(ItemStack var1) {
      Bucketable.saveDefaultDataToBucketTag(this, var1);
      var1.copyFrom(DataComponents.AXOLOTL_VARIANT, this);
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, var1, (var1x) -> {
         var1x.putInt("Age", this.getAge());
         Brain var2 = this.getBrain();
         if (var2.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
            var1x.putLong("HuntingCooldown", var2.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
         }

      });
   }

   public void loadFromBucketTag(CompoundTag var1) {
      Bucketable.loadDefaultDataFromBucketTag(this, var1);
      this.setAge(var1.getIntOr("Age", 0));
      var1.getLong("HuntingCooldown").ifPresentOrElse((var2) -> this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, var1.getLongOr("HuntingCooldown", 0L)), () -> this.getBrain().setMemory(MemoryModuleType.HAS_HUNTING_COOLDOWN, Optional.empty()));
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

   public static void onStopAttacking(ServerLevel var0, Axolotl var1, LivingEntity var2) {
      if (var2.isDeadOrDying()) {
         DamageSource var3 = var2.getLastDamageSource();
         if (var3 != null) {
            Entity var4 = var3.getEntity();
            if (var4 != null && var4.getType() == EntityType.PLAYER) {
               Player var5 = (Player)var4;
               List var6 = var0.getEntitiesOfClass(Player.class, var1.getBoundingBox().inflate(20.0));
               if (var6.contains(var5)) {
                  var1.applySupportingEffects(var5);
               }
            }
         }
      }

   }

   public void applySupportingEffects(Player var1) {
      MobEffectInstance var2 = var1.getEffect(MobEffects.REGENERATION);
      if (var2 == null || var2.endsWithin(2399)) {
         int var3 = var2 != null ? var2.getDuration() : 0;
         int var4 = Math.min(2400, 100 + var3);
         var1.addEffect(new MobEffectInstance(MobEffects.REGENERATION, var4, 0), this);
      }

      var1.removeEffect(MobEffects.MINING_FATIGUE);
   }

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.AXOLOTL_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.AXOLOTL_DEATH;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.AXOLOTL_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.AXOLOTL_SWIM;
   }

   protected Brain.Provider<Axolotl> brainProvider() {
      return Brain.<Axolotl>provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return AxolotlAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<Axolotl> getBrain() {
      return super.getBrain();
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public void travel(Vec3 var1) {
      if (this.isInWater()) {
         this.moveRelative(this.getSpeed(), var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
      } else {
         super.travel(var1);
      }

   }

   protected void usePlayerItem(Player var1, InteractionHand var2, ItemStack var3) {
      if (var3.is(Items.TROPICAL_FISH_BUCKET)) {
         var1.setItemInHand(var2, ItemUtils.createFilledResult(var3, var1, new ItemStack(Items.WATER_BUCKET)));
      } else {
         super.usePlayerItem(var1, var2, var3);
      }

   }

   public boolean removeWhenFarAway(double var1) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   public static boolean checkAxolotlSpawnRules(EntityType<? extends LivingEntity> var0, ServerLevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.AXOLOTLS_SPAWNABLE_ON);
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.BREED_TARGET, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, new MemoryModuleType[]{MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN, MemoryModuleType.IS_PANICKING});
      DATA_VARIANT = SynchedEntityData.<Integer>defineId(Axolotl.class, EntityDataSerializers.INT);
      DATA_PLAYING_DEAD = SynchedEntityData.<Boolean>defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
      FROM_BUCKET = SynchedEntityData.<Boolean>defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
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

      private Variant(final int var3, final String var4, final boolean var5) {
         this.id = var3;
         this.name = var4;
         this.common = var5;
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

      public static Variant byId(int var0) {
         return (Variant)BY_ID.apply(var0);
      }

      public static Variant getCommonSpawnVariant(RandomSource var0) {
         return getSpawnVariant(var0, true);
      }

      public static Variant getRareSpawnVariant(RandomSource var0) {
         return getSpawnVariant(var0, false);
      }

      private static Variant getSpawnVariant(RandomSource var0, boolean var1) {
         Variant[] var2 = (Variant[])Arrays.stream(values()).filter((var1x) -> var1x.common == var1).toArray((var0x) -> new Variant[var0x]);
         return (Variant)Util.getRandom(var2, var0);
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

   static class AxolotlMoveControl extends SmoothSwimmingMoveControl {
      private final Axolotl axolotl;

      public AxolotlMoveControl(Axolotl var1) {
         super(var1, 85, 10, 0.1F, 0.5F, false);
         this.axolotl = var1;
      }

      public void tick() {
         if (!this.axolotl.isPlayingDead()) {
            super.tick();
         }

      }
   }

   class AxolotlLookControl extends SmoothSwimmingLookControl {
      public AxolotlLookControl(final Axolotl var2, final int var3) {
         super(var2, var3);
      }

      public void tick() {
         if (!Axolotl.this.isPlayingDead()) {
            super.tick();
         }

      }
   }

   public static class AxolotlGroupData extends AgeableMob.AgeableMobGroupData {
      public final Variant[] types;

      public AxolotlGroupData(Variant... var1) {
         super(false);
         this.types = var1;
      }

      public Variant getVariant(RandomSource var1) {
         return this.types[var1.nextInt(this.types.length)];
      }
   }

   public static enum AnimationState {
      PLAYING_DEAD,
      IN_WATER,
      ON_GROUND,
      IN_AIR;

      private AnimationState() {
      }

      // $FF: synthetic method
      private static AnimationState[] $values() {
         return new AnimationState[]{PLAYING_DEAD, IN_WATER, ON_GROUND, IN_AIR};
      }
   }
}
