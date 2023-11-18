package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
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
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LerpingModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class Axolotl extends Animal implements LerpingModel, VariantHolder<Axolotl.Variant>, Bucketable {
   public static final int TOTAL_PLAYDEAD_TIME = 200;
   protected static final ImmutableList<? extends SensorType<? extends Sensor<? super Axolotl>>> SENSOR_TYPES = ImmutableList.of(
      SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.AXOLOTL_ATTACKABLES, SensorType.AXOLOTL_TEMPTATIONS
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
      MemoryModuleType.NEAREST_VISIBLE_ADULT,
      new MemoryModuleType[]{
         MemoryModuleType.HURT_BY_ENTITY,
         MemoryModuleType.PLAY_DEAD_TICKS,
         MemoryModuleType.NEAREST_ATTACKABLE,
         MemoryModuleType.TEMPTING_PLAYER,
         MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
         MemoryModuleType.IS_TEMPTED,
         MemoryModuleType.HAS_HUNTING_COOLDOWN,
         MemoryModuleType.IS_PANICKING
      }
   );
   private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_PLAYING_DEAD = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(Axolotl.class, EntityDataSerializers.BOOLEAN);
   public static final double PLAYER_REGEN_DETECTION_RANGE = 20.0;
   public static final int RARE_VARIANT_CHANCE = 1200;
   private static final int AXOLOTL_TOTAL_AIR_SUPPLY = 6000;
   public static final String VARIANT_TAG = "Variant";
   private static final int REHYDRATE_AIR_SUPPLY = 1800;
   private static final int REGEN_BUFF_MAX_DURATION = 2400;
   private final Map<String, Vector3f> modelRotationValues = Maps.newHashMap();
   private static final int REGEN_BUFF_BASE_DURATION = 100;

   public Axolotl(EntityType<? extends Axolotl> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.moveControl = new Axolotl.AxolotlMoveControl(this);
      this.lookControl = new Axolotl.AxolotlLookControl(this, 20);
      this.setMaxUpStep(1.0F);
   }

   @Override
   public Map<String, Vector3f> getModelRotationValues() {
      return this.modelRotationValues;
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VARIANT, 0);
      this.entityData.define(DATA_PLAYING_DEAD, false);
      this.entityData.define(FROM_BUCKET, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Variant", this.getVariant().getId());
      var1.putBoolean("FromBucket", this.fromBucket());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant(Axolotl.Variant.byId(var1.getInt("Variant")));
      this.setFromBucket(var1.getBoolean("FromBucket"));
   }

   @Override
   public void playAmbientSound() {
      if (!this.isPlayingDead()) {
         super.playAmbientSound();
      }
   }

   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      boolean var6 = false;
      if (var3 == MobSpawnType.BUCKET) {
         return (SpawnGroupData)var4;
      } else {
         RandomSource var7 = var1.getRandom();
         if (var4 instanceof Axolotl.AxolotlGroupData) {
            if (((Axolotl.AxolotlGroupData)var4).getGroupSize() >= 2) {
               var6 = true;
            }
         } else {
            var4 = new Axolotl.AxolotlGroupData(Axolotl.Variant.getCommonSpawnVariant(var7), Axolotl.Variant.getCommonSpawnVariant(var7));
         }

         this.setVariant(((Axolotl.AxolotlGroupData)var4).getVariant(var7));
         if (var6) {
            this.setAge(-24000);
         }

         return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
      }
   }

   @Override
   public void baseTick() {
      int var1 = this.getAirSupply();
      super.baseTick();
      if (!this.isNoAi()) {
         this.handleAirSupply(var1);
      }
   }

   protected void handleAirSupply(int var1) {
      if (this.isAlive() && !this.isInWaterRainOrBubble()) {
         this.setAirSupply(var1 - 1);
         if (this.getAirSupply() == -20) {
            this.setAirSupply(0);
            this.hurt(this.damageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAirSupply(this.getMaxAirSupply());
      }
   }

   public void rehydrate() {
      int var1 = this.getAirSupply() + 1800;
      this.setAirSupply(Math.min(var1, this.getMaxAirSupply()));
   }

   @Override
   public int getMaxAirSupply() {
      return 6000;
   }

   public Axolotl.Variant getVariant() {
      return Axolotl.Variant.byId(this.entityData.get(DATA_VARIANT));
   }

   public void setVariant(Axolotl.Variant var1) {
      this.entityData.set(DATA_VARIANT, var1.getId());
   }

   private static boolean useRareVariant(RandomSource var0) {
      return var0.nextInt(1200) == 0;
   }

   @Override
   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   @Override
   public boolean canBreatheUnderwater() {
      return true;
   }

   @Override
   public boolean isPushedByFluid() {
      return false;
   }

   @Override
   public MobType getMobType() {
      return MobType.WATER;
   }

   public void setPlayingDead(boolean var1) {
      this.entityData.set(DATA_PLAYING_DEAD, var1);
   }

   public boolean isPlayingDead() {
      return this.entityData.get(DATA_PLAYING_DEAD);
   }

   @Override
   public boolean fromBucket() {
      return this.entityData.get(FROM_BUCKET);
   }

   @Override
   public void setFromBucket(boolean var1) {
      this.entityData.set(FROM_BUCKET, var1);
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Axolotl var3 = EntityType.AXOLOTL.create(var1);
      if (var3 != null) {
         Axolotl.Variant var4;
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

   @Override
   public double getMeleeAttackRangeSqr(LivingEntity var1) {
      return 1.5 + (double)var1.getBbWidth() * 2.0;
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.AXOLOTL_TEMPT_ITEMS);
   }

   @Override
   public boolean canBeLeashed(Player var1) {
      return true;
   }

   @Override
   protected void customServerAiStep() {
      this.level().getProfiler().push("axolotlBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      this.level().getProfiler().push("axolotlActivityUpdate");
      AxolotlAi.updateActivity(this);
      this.level().getProfiler().pop();
      if (!this.isNoAi()) {
         Optional var1 = this.getBrain().getMemory(MemoryModuleType.PLAY_DEAD_TICKS);
         this.setPlayingDead(var1.isPresent() && var1.get() > 0);
      }
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.MOVEMENT_SPEED, 1.0).add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new AmphibiousPathNavigation(this, var1);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      boolean var2 = var1.hurt(this.damageSources().mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (var2) {
         this.doEnchantDamageEffects(this, var1);
         this.playSound(SoundEvents.AXOLOTL_ATTACK, 1.0F, 1.0F);
      }

      return var2;
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      float var3 = this.getHealth();
      if (!this.level().isClientSide
         && !this.isNoAi()
         && this.level().random.nextInt(3) == 0
         && ((float)this.level().random.nextInt(3) < var2 || var3 / this.getMaxHealth() < 0.5F)
         && var2 < var3
         && this.isInWater()
         && (var1.getEntity() != null || var1.getDirectEntity() != null)
         && !this.isPlayingDead()) {
         this.brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, 200);
      }

      return super.hurt(var1, var2);
   }

   @Override
   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return var2.height * 0.655F;
   }

   @Override
   public int getMaxHeadXRot() {
      return 1;
   }

   @Override
   public int getMaxHeadYRot() {
      return 1;
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      return Bucketable.bucketMobPickup(var1, var2, this).orElse(super.mobInteract(var1, var2));
   }

   @Override
   public void saveToBucketTag(ItemStack var1) {
      Bucketable.saveDefaultDataToBucketTag(this, var1);
      CompoundTag var2 = var1.getOrCreateTag();
      var2.putInt("Variant", this.getVariant().getId());
      var2.putInt("Age", this.getAge());
      Brain var3 = this.getBrain();
      if (var3.hasMemoryValue(MemoryModuleType.HAS_HUNTING_COOLDOWN)) {
         var2.putLong("HuntingCooldown", var3.getTimeUntilExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN));
      }
   }

   @Override
   public void loadFromBucketTag(CompoundTag var1) {
      Bucketable.loadDefaultDataFromBucketTag(this, var1);
      this.setVariant(Axolotl.Variant.byId(var1.getInt("Variant")));
      if (var1.contains("Age")) {
         this.setAge(var1.getInt("Age"));
      }

      if (var1.contains("HuntingCooldown")) {
         this.getBrain().setMemoryWithExpiry(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, var1.getLong("HuntingCooldown"));
      }
   }

   @Override
   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.AXOLOTL_BUCKET);
   }

   @Override
   public SoundEvent getPickupSound() {
      return SoundEvents.BUCKET_FILL_AXOLOTL;
   }

   @Override
   public boolean canBeSeenAsEnemy() {
      return !this.isPlayingDead() && super.canBeSeenAsEnemy();
   }

   public static void onStopAttacking(Axolotl var0, LivingEntity var1) {
      Level var2 = var0.level();
      if (var1.isDeadOrDying()) {
         DamageSource var3 = var1.getLastDamageSource();
         if (var3 != null) {
            Entity var4 = var3.getEntity();
            if (var4 != null && var4.getType() == EntityType.PLAYER) {
               Player var5 = (Player)var4;
               List var6 = var2.getEntitiesOfClass(Player.class, var0.getBoundingBox().inflate(20.0));
               if (var6.contains(var5)) {
                  var0.applySupportingEffects(var5);
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

      var1.removeEffect(MobEffects.DIG_SLOWDOWN);
   }

   @Override
   public boolean requiresCustomPersistence() {
      return super.requiresCustomPersistence() || this.fromBucket();
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.AXOLOTL_HURT;
   }

   @Nullable
   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.AXOLOTL_DEATH;
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.AXOLOTL_IDLE_WATER : SoundEvents.AXOLOTL_IDLE_AIR;
   }

   @Override
   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.AXOLOTL_SPLASH;
   }

   @Override
   protected SoundEvent getSwimSound() {
      return SoundEvents.AXOLOTL_SWIM;
   }

   @Override
   protected Brain.Provider<Axolotl> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return AxolotlAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   public Brain<Axolotl> getBrain() {
      return super.getBrain();
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   @Override
   public void travel(Vec3 var1) {
      if (this.isControlledByLocalInstance() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
      } else {
         super.travel(var1);
      }
   }

   @Override
   protected void usePlayerItem(Player var1, InteractionHand var2, ItemStack var3) {
      if (var3.is(Items.TROPICAL_FISH_BUCKET)) {
         var1.setItemInHand(var2, new ItemStack(Items.WATER_BUCKET));
      } else {
         super.usePlayerItem(var1, var2, var3);
      }
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return !this.fromBucket() && !this.hasCustomName();
   }

   public static boolean checkAxolotlSpawnRules(
      EntityType<? extends LivingEntity> var0, ServerLevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4
   ) {
      return var1.getBlockState(var3.below()).is(BlockTags.AXOLOTLS_SPAWNABLE_ON);
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

   class AxolotlLookControl extends SmoothSwimmingLookControl {
      public AxolotlLookControl(Axolotl var2, int var3) {
         super(var2, var3);
      }

      @Override
      public void tick() {
         if (!Axolotl.this.isPlayingDead()) {
            super.tick();
         }
      }
   }

   static class AxolotlMoveControl extends SmoothSwimmingMoveControl {
      private final Axolotl axolotl;

      public AxolotlMoveControl(Axolotl var1) {
         super(var1, 85, 10, 0.1F, 0.5F, false);
         this.axolotl = var1;
      }

      @Override
      public void tick() {
         if (!this.axolotl.isPlayingDead()) {
            super.tick();
         }
      }
   }

   public static enum Variant implements StringRepresentable {
      LUCY(0, "lucy", true),
      WILD(1, "wild", true),
      GOLD(2, "gold", true),
      CYAN(3, "cyan", true),
      BLUE(4, "blue", false);

      private static final IntFunction<Axolotl.Variant> BY_ID = ByIdMap.continuous(Axolotl.Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final Codec<Axolotl.Variant> CODEC = StringRepresentable.fromEnum(Axolotl.Variant::values);
      private final int id;
      private final String name;
      private final boolean common;

      private Variant(int var3, String var4, boolean var5) {
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

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public static Axolotl.Variant byId(int var0) {
         return BY_ID.apply(var0);
      }

      public static Axolotl.Variant getCommonSpawnVariant(RandomSource var0) {
         return getSpawnVariant(var0, true);
      }

      public static Axolotl.Variant getRareSpawnVariant(RandomSource var0) {
         return getSpawnVariant(var0, false);
      }

      private static Axolotl.Variant getSpawnVariant(RandomSource var0, boolean var1) {
         Axolotl.Variant[] var2 = Arrays.stream(values()).filter(var1x -> var1x.common == var1).toArray(var0x -> new Axolotl.Variant[var0x]);
         return Util.getRandom(var2, var0);
      }
   }
}
