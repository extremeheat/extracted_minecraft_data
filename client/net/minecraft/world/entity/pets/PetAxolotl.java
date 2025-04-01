package net.minecraft.world.entity.pets;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class PetAxolotl extends AbstractPet {
   public static final int TOTAL_PLAYDEAD_TIME = 200;
   private static final int POSE_ANIMATION_TICKS = 10;
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super PetAxolotl>>> SENSOR_TYPES;
   protected static final ImmutableList<? extends MemoryModuleType<?>> MEMORY_TYPES;
   private static final EntityDataAccessor<Integer> DATA_VARIANT;
   public static final int RARE_VARIANT_CHANCE = 1200;
   public static final String VARIANT_TAG = "Variant";
   public final BinaryAnimator inWaterAnimator = new BinaryAnimator(10, Mth::easeInOutSine);
   public final BinaryAnimator onGroundAnimator = new BinaryAnimator(10, Mth::easeInOutSine);
   public final BinaryAnimator movingAnimator = new BinaryAnimator(10, Mth::easeInOutSine);
   private static final int REGEN_BUFF_BASE_DURATION = 100;

   public PetAxolotl(EntityType<? extends PetAxolotl> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.moveControl = new AxolotlMoveControl(this);
      this.lookControl = new AxolotlLookControl(this, 20);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 8.0F, 2.0F));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.store("Variant", Axolotl.Variant.LEGACY_CODEC, this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant((Axolotl.Variant)var1.read("Variant", Axolotl.Variant.LEGACY_CODEC).orElse(Axolotl.Variant.DEFAULT));
   }

   public void playAmbientSound() {
      super.playAmbientSound();
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      if (var3 == EntitySpawnReason.BUCKET) {
         return var4;
      } else {
         RandomSource var5 = var1.getRandom();
         var4 = new AxolotlGroupData(new Axolotl.Variant[]{Axolotl.Variant.getCommonSpawnVariant(var5), Axolotl.Variant.getCommonSpawnVariant(var5)});
         this.setVariant(((AxolotlGroupData)var4).getVariant(var5));
         return super.finalizeSpawn(var1, var2, var3, var4);
      }
   }

   public void baseTick() {
      super.baseTick();
      if (this.level().isClientSide()) {
         this.tickAnimations();
      }

   }

   private void tickAnimations() {
      AnimationState var1;
      if (this.isInWater()) {
         var1 = PetAxolotl.AnimationState.IN_WATER;
      } else if (this.onGround()) {
         var1 = PetAxolotl.AnimationState.ON_GROUND;
      } else {
         var1 = PetAxolotl.AnimationState.IN_AIR;
      }

      this.inWaterAnimator.tick(var1 == PetAxolotl.AnimationState.IN_WATER);
      this.onGroundAnimator.tick(var1 == PetAxolotl.AnimationState.ON_GROUND);
      boolean var2 = this.walkAnimation.isMoving() || this.getXRot() != this.xRotO || this.getYRot() != this.yRotO;
      this.movingAnimator.tick(var2);
   }

   public Axolotl.Variant getVariant() {
      return Axolotl.Variant.byId((Integer)this.entityData.get(DATA_VARIANT));
   }

   private void setVariant(Axolotl.Variant var1) {
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
         this.setVariant((Axolotl.Variant)castComponentValue(DataComponents.AXOLOTL_VARIANT, var2));
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

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.AXOLOTL_FOOD);
   }

   public boolean canBeLeashed() {
      return true;
   }

   protected void customServerAiStep(ServerLevel var1) {
      super.customServerAiStep(var1);
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
      if (!this.isNoAi() && this.level().random.nextInt(3) == 0 && ((float)this.level().random.nextInt(3) < var3 || var4 / this.getMaxHealth() < 0.5F) && var3 < var4 && this.isInWater() && (var2.getEntity() != null || var2.getDirectEntity() != null)) {
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

   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence();
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
      return !this.hasCustomName();
   }

   public static boolean checkAxolotlSpawnRules(EntityType<? extends LivingEntity> var0, ServerLevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.AXOLOTLS_SPAWNABLE_ON);
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_TEMPTATIONS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, MemoryModuleType.LOOK_TARGET, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.HURT_BY_ENTITY, new MemoryModuleType[]{MemoryModuleType.NEAREST_ATTACKABLE, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryModuleType.IS_TEMPTED, MemoryModuleType.HAS_HUNTING_COOLDOWN, MemoryModuleType.IS_PANICKING});
      DATA_VARIANT = SynchedEntityData.<Integer>defineId(PetAxolotl.class, EntityDataSerializers.INT);
   }

   static class AxolotlMoveControl extends SmoothSwimmingMoveControl {
      private final PetAxolotl axolotl;

      public AxolotlMoveControl(PetAxolotl var1) {
         super(var1, 85, 10, 0.1F, 0.5F, false);
         this.axolotl = var1;
      }

      public void tick() {
         super.tick();
      }
   }

   class AxolotlLookControl extends SmoothSwimmingLookControl {
      public AxolotlLookControl(final PetAxolotl var2, final int var3) {
         super(var2, var3);
      }

      public void tick() {
         super.tick();
      }
   }

   public static class AxolotlGroupData extends AgeableMob.AgeableMobGroupData {
      public final Axolotl.Variant[] types;

      public AxolotlGroupData(Axolotl.Variant... var1) {
         super(false);
         this.types = var1;
      }

      public Axolotl.Variant getVariant(RandomSource var1) {
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
