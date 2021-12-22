package net.minecraft.world.entity.animal.goat;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class Goat extends Animal {
   public static final EntityDimensions LONG_JUMPING_DIMENSIONS = EntityDimensions.scalable(0.9F, 1.3F).scale(0.7F);
   private static final int ADULT_ATTACK_DAMAGE = 2;
   private static final int BABY_ATTACK_DAMAGE = 1;
   protected static final ImmutableList<SensorType<? extends Sensor<? super Goat>>> SENSOR_TYPES;
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;
   public static final int GOAT_FALL_DAMAGE_REDUCTION = 10;
   public static final double GOAT_SCREAMING_CHANCE = 0.02D;
   private static final EntityDataAccessor<Boolean> DATA_IS_SCREAMING_GOAT;
   private boolean isLoweringHead;
   private int lowerHeadTick;

   public Goat(EntityType<? extends Goat> var1, Level var2) {
      super(var1, var2);
      this.getNavigation().setCanFloat(true);
   }

   protected Brain.Provider<Goat> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return GoatAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224D).add(Attributes.ATTACK_DAMAGE, 2.0D);
   }

   protected void ageBoundaryReached() {
      if (this.isBaby()) {
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0D);
      } else {
         this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0D);
      }

   }

   protected int calculateFallDamage(float var1, float var2) {
      return super.calculateFallDamage(var1, var2) - 10;
   }

   protected SoundEvent getAmbientSound() {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_AMBIENT : SoundEvents.GOAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_HURT : SoundEvents.GOAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_DEATH : SoundEvents.GOAT_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.GOAT_STEP, 0.15F, 1.0F);
   }

   protected SoundEvent getMilkingSound() {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_MILK : SoundEvents.GOAT_MILK;
   }

   public Goat getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Goat var3 = (Goat)EntityType.GOAT.create(var1);
      if (var3 != null) {
         GoatAi.initMemories(var3);
         boolean var4 = var2 instanceof Goat && ((Goat)var2).isScreamingGoat();
         var3.setScreamingGoat(var4 || var1.getRandom().nextDouble() < 0.02D);
      }

      return var3;
   }

   public Brain<Goat> getBrain() {
      return super.getBrain();
   }

   protected void customServerAiStep() {
      this.level.getProfiler().push("goatBrain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().pop();
      this.level.getProfiler().push("goatActivityUpdate");
      GoatAi.updateActivity(this);
      this.level.getProfiler().pop();
      super.customServerAiStep();
   }

   public int getMaxHeadYRot() {
      return 15;
   }

   public void setYHeadRot(float var1) {
      int var2 = this.getMaxHeadYRot();
      float var3 = Mth.degreesDifference(this.yBodyRot, var1);
      float var4 = Mth.clamp(var3, (float)(-var2), (float)var2);
      super.setYHeadRot(this.yBodyRot + var4);
   }

   public SoundEvent getEatingSound(ItemStack var1) {
      return this.isScreamingGoat() ? SoundEvents.GOAT_SCREAMING_EAT : SoundEvents.GOAT_EAT;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.method_87(Items.BUCKET) && !this.isBaby()) {
         var1.playSound(this.getMilkingSound(), 1.0F, 1.0F);
         ItemStack var5 = ItemUtils.createFilledResult(var3, var1, Items.MILK_BUCKET.getDefaultInstance());
         var1.setItemInHand(var2, var5);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         InteractionResult var4 = super.mobInteract(var1, var2);
         if (var4.consumesAction() && this.isFood(var3)) {
            this.level.playSound((Player)null, (Entity)this, this.getEatingSound(var3), SoundSource.NEUTRAL, 1.0F, Mth.randomBetween(this.level.random, 0.8F, 1.2F));
         }

         return var4;
      }
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      GoatAi.initMemories(this);
      this.setScreamingGoat(var1.getRandom().nextDouble() < 0.02D);
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public EntityDimensions getDimensions(Pose var1) {
      return var1 == Pose.LONG_JUMPING ? LONG_JUMPING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("IsScreamingGoat", this.isScreamingGoat());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setScreamingGoat(var1.getBoolean("IsScreamingGoat"));
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 58) {
         this.isLoweringHead = true;
      } else if (var1 == 59) {
         this.isLoweringHead = false;
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public void aiStep() {
      if (this.isLoweringHead) {
         ++this.lowerHeadTick;
      } else {
         this.lowerHeadTick -= 2;
      }

      this.lowerHeadTick = Mth.clamp((int)this.lowerHeadTick, (int)0, (int)20);
      super.aiStep();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IS_SCREAMING_GOAT, false);
   }

   public boolean isScreamingGoat() {
      return (Boolean)this.entityData.get(DATA_IS_SCREAMING_GOAT);
   }

   public void setScreamingGoat(boolean var1) {
      this.entityData.set(DATA_IS_SCREAMING_GOAT, var1);
   }

   public float getRammingXHeadRot() {
      return (float)this.lowerHeadTick / 20.0F * 30.0F * 0.017453292F;
   }

   protected PathNavigation createNavigation(Level var1) {
      return new Goat.GoatPathNavigation(this, var1);
   }

   public static boolean checkGoatSpawnRules(EntityType<? extends Animal> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.GOATS_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   // $FF: synthetic method
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.NEAREST_ADULT, SensorType.HURT_BY, SensorType.GOAT_TEMPTATIONS);
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.ATE_RECENTLY, MemoryModuleType.BREED_TARGET, MemoryModuleType.LONG_JUMP_COOLDOWN_TICKS, MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleType.TEMPTING_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, new MemoryModuleType[]{MemoryModuleType.IS_TEMPTED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleType.RAM_TARGET});
      DATA_IS_SCREAMING_GOAT = SynchedEntityData.defineId(Goat.class, EntityDataSerializers.BOOLEAN);
   }

   static class GoatPathNavigation extends GroundPathNavigation {
      GoatPathNavigation(Goat var1, Level var2) {
         super(var1, var2);
      }

      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = new Goat.GoatNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, var1);
      }
   }

   private static class GoatNodeEvaluator extends WalkNodeEvaluator {
      private final BlockPos.MutableBlockPos belowPos = new BlockPos.MutableBlockPos();

      GoatNodeEvaluator() {
         super();
      }

      public BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4) {
         this.belowPos.set(var2, var3 - 1, var4);
         BlockPathTypes var5 = getBlockPathTypeRaw(var1, this.belowPos);
         return var5 == BlockPathTypes.POWDER_SNOW ? BlockPathTypes.BLOCKED : getBlockPathTypeStatic(var1, this.belowPos.move(Direction.field_526));
      }
   }
}
