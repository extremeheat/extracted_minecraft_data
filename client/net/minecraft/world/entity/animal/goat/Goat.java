package net.minecraft.world.entity.animal.goat;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.InstrumentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class Goat extends Animal {
   public static final EntityDimensions LONG_JUMPING_DIMENSIONS = EntityDimensions.scalable(0.9F, 1.3F).scale(0.7F);
   private static final int ADULT_ATTACK_DAMAGE = 2;
   private static final int BABY_ATTACK_DAMAGE = 1;
   protected static final ImmutableList<SensorType<? extends Sensor<? super Goat>>> SENSOR_TYPES = ImmutableList.of(
      SensorType.NEAREST_LIVING_ENTITIES,
      SensorType.NEAREST_PLAYERS,
      SensorType.NEAREST_ITEMS,
      SensorType.NEAREST_ADULT,
      SensorType.HURT_BY,
      SensorType.GOAT_TEMPTATIONS
   );
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
      MemoryModuleType.LOOK_TARGET,
      MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
      MemoryModuleType.WALK_TARGET,
      MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
      MemoryModuleType.PATH,
      MemoryModuleType.ATE_RECENTLY,
      MemoryModuleType.BREED_TARGET,
      MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS,
      MemoryModuleType.LONG_JUMP_MID_JUMP,
      MemoryModuleType.TEMPTING_PLAYER,
      MemoryModuleType.NEAREST_VISIBLE_ADULT,
      MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
      new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET, MemoryModuleType.IS_PANICKING}
   );
   public static final int GOAT_FALL_DAMAGE_REDUCTION = 10;
   public static final double GOAT_SCREAMING_CHANCE = 0.02;
   public static final double UNIHORN_CHANCE = 0.10000000149011612;
   private static final EntityDataAccessor<Boolean> DATA_IS_SCREAMING_GOAT = SynchedEntityData.defineId(Goat.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_HAS_LEFT_HORN = SynchedEntityData.defineId(Goat.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_HAS_RIGHT_HORN = SynchedEntityData.defineId(Goat.class, EntityDataSerializers.BOOLEAN);
   private boolean isLoweringHead;
   private int lowerHeadTick;

   public Goat(EntityType<? extends Goat> var1, Level var2) {
      super(var1, var2);
      this.getNavigation().setCanFloat(true);
      this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
   }

   public ItemStack createHorn() {
      RandomSource var1 = RandomSource.create((long)this.getUUID().hashCode());
      TagKey var2 = this.isScreamingGoat() ? InstrumentTags.SCREAMING_GOAT_HORNS : InstrumentTags.REGULAR_GOAT_HORNS;
      HolderSet.Named var3 = BuiltInRegistries.INSTRUMENT.getOrCreateTag(var2);
      return InstrumentItem.create(Items.GOAT_HORN, (Holder<Instrument>)var3.getRandomElement(var1).get());
   }

   @Override
   protected Brain.Provider<Goat> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return GoatAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224).add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   @Override
   protected void ageBoundaryReached() {
      if (this.isBaby()) {
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0);
         this.removeHorns();
      } else {
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0);
         this.addHorns();
      }
   }

   @Override
   protected int calculateFallDamage(float var1, float var2) {
      return super.calculateFallDamage(var1, var2) - 10;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_AMBIENT : SoundEvents.GOAT_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_HURT : SoundEvents.GOAT_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_DEATH : SoundEvents.GOAT_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.GOAT_STEP, 0.15F, 1.0F);
   }

   protected SoundEvent getMilkingSound() {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_MILK : SoundEvents.GOAT_MILK;
   }

   @Nullable
   public Goat getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Goat var3 = EntityType.GOAT.create(var1);
      if (var3 != null) {
         GoatAi.initMemories(var3, var1.getRandom());
         Object var4 = var1.getRandom().nextBoolean() ? this : var2;
         boolean var5 = var4 instanceof Goat var6 && var6.isScreamingGoat() || var1.getRandom().nextDouble() < 0.02;
         var3.setScreamingGoat(var5);
      }

      return var3;
   }

   @Override
   public Brain<Goat> getBrain() {
      return super.getBrain();
   }

   @Override
   protected void customServerAiStep() {
      this.level().getProfiler().push("goatBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      this.level().getProfiler().push("goatActivityUpdate");
      GoatAi.updateActivity(this);
      this.level().getProfiler().pop();
      super.customServerAiStep();
   }

   @Override
   public int getMaxHeadYRot() {
      return 15;
   }

   @Override
   public void setYHeadRot(float var1) {
      int var2 = this.getMaxHeadYRot();
      float var3 = Mth.degreesDifference(this.yBodyRot, var1);
      float var4 = Mth.clamp(var3, (float)(-var2), (float)var2);
      super.setYHeadRot(this.yBodyRot + var4);
   }

   @Override
   public SoundEvent getEatingSound(ItemStack var1) {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_EAT : SoundEvents.GOAT_EAT;
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.BUCKET) && !this.isBaby()) {
         var1.playSound(this.getMilkingSound(), 1.0F, 1.0F);
         ItemStack var5 = ItemUtils.createFilledResult(var3, var1, Items.MILK_BUCKET.getDefaultInstance());
         var1.setItemInHand(var2, var5);
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         InteractionResult var4 = super.mobInteract(var1, var2);
         if (var4.consumesAction() && this.isFood(var3)) {
            this.level().playSound(null, this, this.getEatingSound(var3), SoundSource.NEUTRAL, 1.0F, Mth.randomBetween(this.level().random, 0.8F, 1.2F));
         }

         return var4;
      }
   }

   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      RandomSource var6 = var1.getRandom();
      GoatAi.initMemories(this, var6);
      this.setScreamingGoat(var6.nextDouble() < 0.02);
      this.ageBoundaryReached();
      if (!this.isBaby() && (double)var6.nextFloat() < 0.10000000149011612) {
         EntityDataAccessor var7 = var6.nextBoolean() ? DATA_HAS_LEFT_HORN : DATA_HAS_RIGHT_HORN;
         this.entityData.set(var7, false);
      }

      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   @Override
   public EntityDimensions getDimensions(Pose var1) {
      return var1 == Pose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(var1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("IsScreamingGoat", this.isScreamingGoat());
      var1.putBoolean("HasLeftHorn", this.hasLeftHorn());
      var1.putBoolean("HasRightHorn", this.hasRightHorn());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setScreamingGoat(var1.getBoolean("IsScreamingGoat"));
      this.entityData.set(DATA_HAS_LEFT_HORN, var1.getBoolean("HasLeftHorn"));
      this.entityData.set(DATA_HAS_RIGHT_HORN, var1.getBoolean("HasRightHorn"));
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 58) {
         this.isLoweringHead = true;
      } else if (var1 == 59) {
         this.isLoweringHead = false;
      } else {
         super.handleEntityEvent(var1);
      }
   }

   @Override
   public void aiStep() {
      if (this.isLoweringHead) {
         ++this.lowerHeadTick;
      } else {
         this.lowerHeadTick -= 2;
      }

      this.lowerHeadTick = Mth.clamp(this.lowerHeadTick, 0, 20);
      super.aiStep();
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IS_SCREAMING_GOAT, false);
      this.entityData.define(DATA_HAS_LEFT_HORN, true);
      this.entityData.define(DATA_HAS_RIGHT_HORN, true);
   }

   public boolean hasLeftHorn() {
      return this.entityData.get(DATA_HAS_LEFT_HORN);
   }

   public boolean hasRightHorn() {
      return this.entityData.get(DATA_HAS_RIGHT_HORN);
   }

   public boolean dropHorn() {
      boolean var1 = this.hasLeftHorn();
      boolean var2 = this.hasRightHorn();
      if (!var1 && !var2) {
         return false;
      } else {
         EntityDataAccessor var3;
         if (!var1) {
            var3 = DATA_HAS_RIGHT_HORN;
         } else if (!var2) {
            var3 = DATA_HAS_LEFT_HORN;
         } else {
            var3 = this.random.nextBoolean() ? DATA_HAS_LEFT_HORN : DATA_HAS_RIGHT_HORN;
         }

         this.entityData.set(var3, false);
         Vec3 var4 = this.position();
         ItemStack var5 = this.createHorn();
         double var6 = (double)Mth.randomBetween(this.random, -0.2F, 0.2F);
         double var8 = (double)Mth.randomBetween(this.random, 0.3F, 0.7F);
         double var10 = (double)Mth.randomBetween(this.random, -0.2F, 0.2F);
         ItemEntity var12 = new ItemEntity(this.level(), var4.x(), var4.y(), var4.z(), var5, var6, var8, var10);
         this.level().addFreshEntity(var12);
         return true;
      }
   }

   public void addHorns() {
      this.entityData.set(DATA_HAS_LEFT_HORN, true);
      this.entityData.set(DATA_HAS_RIGHT_HORN, true);
   }

   public void removeHorns() {
      this.entityData.set(DATA_HAS_LEFT_HORN, false);
      this.entityData.set(DATA_HAS_RIGHT_HORN, false);
   }

   public boolean isScreamingGoat() {
      return this.entityData.get(DATA_IS_SCREAMING_GOAT);
   }

   public void setScreamingGoat(boolean var1) {
      this.entityData.set(DATA_IS_SCREAMING_GOAT, var1);
   }

   public float getRammingXHeadRot() {
      return (float)this.lowerHeadTick / 20.0F * 30.0F * 0.017453292F;
   }

   public static boolean checkGoatSpawnRules(EntityType<? extends Animal> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.GOATS_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   @Override
   protected Vector3f getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      return new Vector3f(0.0F, var2.height - 0.1875F * var3, 0.0F);
   }
}
