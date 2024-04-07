package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.StrollThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Fox extends Animal implements VariantHolder<Fox.Type> {
   private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Fox.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Fox.class, EntityDataSerializers.BYTE);
   private static final int FLAG_SITTING = 1;
   public static final int FLAG_CROUCHING = 4;
   public static final int FLAG_INTERESTED = 8;
   public static final int FLAG_POUNCING = 16;
   private static final int FLAG_SLEEPING = 32;
   private static final int FLAG_FACEPLANTED = 64;
   private static final int FLAG_DEFENDING = 128;
   private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_0 = SynchedEntityData.defineId(Fox.class, EntityDataSerializers.OPTIONAL_UUID);
   private static final EntityDataAccessor<Optional<UUID>> DATA_TRUSTED_ID_1 = SynchedEntityData.defineId(Fox.class, EntityDataSerializers.OPTIONAL_UUID);
   static final Predicate<ItemEntity> ALLOWED_ITEMS = var0 -> !var0.hasPickUpDelay() && var0.isAlive();
   private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR = var0 -> !(var0 instanceof LivingEntity var1)
         ? false
         : var1.getLastHurtMob() != null && var1.getLastHurtMobTimestamp() < var1.tickCount + 600;
   static final Predicate<Entity> STALKABLE_PREY = var0 -> var0 instanceof Chicken || var0 instanceof Rabbit;
   private static final Predicate<Entity> AVOID_PLAYERS = var0 -> !var0.isDiscrete() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(var0);
   private static final int MIN_TICKS_BEFORE_EAT = 600;
   private static final EntityDimensions BABY_DIMENSIONS = EntityType.FOX.getDimensions().scale(0.5F).withEyeHeight(0.2975F);
   private Goal landTargetGoal;
   private Goal turtleEggTargetGoal;
   private Goal fishTargetGoal;
   private float interestedAngle;
   private float interestedAngleO;
   float crouchAmount;
   float crouchAmountO;
   private int ticksSinceEaten;

   public Fox(EntityType<? extends Fox> var1, Level var2) {
      super(var1, var2);
      this.lookControl = new Fox.FoxLookControl();
      this.moveControl = new Fox.FoxMoveControl();
      this.setPathfindingMalus(PathType.DANGER_OTHER, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_OTHER, 0.0F);
      this.setCanPickUpLoot(true);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_TRUSTED_ID_0, Optional.empty());
      var1.define(DATA_TRUSTED_ID_1, Optional.empty());
      var1.define(DATA_TYPE_ID, 0);
      var1.define(DATA_FLAGS_ID, (byte)0);
   }

   @Override
   protected void registerGoals() {
      this.landTargetGoal = new NearestAttackableTargetGoal<>(this, Animal.class, 10, false, false, var0 -> var0 instanceof Chicken || var0 instanceof Rabbit);
      this.turtleEggTargetGoal = new NearestAttackableTargetGoal<>(this, Turtle.class, 10, false, false, Turtle.BABY_ON_LAND_SELECTOR);
      this.fishTargetGoal = new NearestAttackableTargetGoal<>(this, AbstractFish.class, 20, false, false, var0 -> var0 instanceof AbstractSchoolingFish);
      this.goalSelector.addGoal(0, new Fox.FoxFloatGoal());
      this.goalSelector.addGoal(0, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
      this.goalSelector.addGoal(1, new Fox.FaceplantGoal());
      this.goalSelector.addGoal(2, new Fox.FoxPanicGoal(2.2));
      this.goalSelector.addGoal(3, new Fox.FoxBreedGoal(1.0));
      this.goalSelector
         .addGoal(
            4,
            new AvoidEntityGoal<>(this, Player.class, 16.0F, 1.6, 1.4, var1 -> AVOID_PLAYERS.test(var1) && !this.trusts(var1.getUUID()) && !this.isDefending())
         );
      this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Wolf.class, 8.0F, 1.6, 1.4, var1 -> !((Wolf)var1).isTame() && !this.isDefending()));
      this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, PolarBear.class, 8.0F, 1.6, 1.4, var1 -> !this.isDefending()));
      this.goalSelector.addGoal(5, new Fox.StalkPreyGoal());
      this.goalSelector.addGoal(6, new Fox.FoxPounceGoal());
      this.goalSelector.addGoal(6, new Fox.SeekShelterGoal(1.25));
      this.goalSelector.addGoal(7, new Fox.FoxMeleeAttackGoal(1.2000000476837158, true));
      this.goalSelector.addGoal(7, new Fox.SleepGoal());
      this.goalSelector.addGoal(8, new Fox.FoxFollowParentGoal(this, 1.25));
      this.goalSelector.addGoal(9, new Fox.FoxStrollThroughVillageGoal(32, 200));
      this.goalSelector.addGoal(10, new Fox.FoxEatBerriesGoal(1.2000000476837158, 12, 1));
      this.goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(11, new Fox.FoxSearchForItemsGoal());
      this.goalSelector.addGoal(12, new Fox.FoxLookAtPlayerGoal(this, Player.class, 24.0F));
      this.goalSelector.addGoal(13, new Fox.PerchAndSearchGoal());
      this.targetSelector
         .addGoal(
            3, new Fox.DefendTrustedTargetGoal(LivingEntity.class, false, false, var1 -> TRUSTED_TARGET_SELECTOR.test(var1) && !this.trusts(var1.getUUID()))
         );
   }

   @Override
   public SoundEvent getEatingSound(ItemStack var1) {
      return SoundEvents.FOX_EAT;
   }

   @Override
   public void aiStep() {
      if (!this.level().isClientSide && this.isAlive() && this.isEffectiveAi()) {
         this.ticksSinceEaten++;
         ItemStack var1 = this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (this.canEat(var1)) {
            if (this.ticksSinceEaten > 600) {
               ItemStack var2 = var1.finishUsingItem(this.level(), this);
               if (!var2.isEmpty()) {
                  this.setItemSlot(EquipmentSlot.MAINHAND, var2);
               }

               this.ticksSinceEaten = 0;
            } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1F) {
               this.playSound(this.getEatingSound(var1), 1.0F, 1.0F);
               this.level().broadcastEntityEvent(this, (byte)45);
            }
         }

         LivingEntity var3 = this.getTarget();
         if (var3 == null || !var3.isAlive()) {
            this.setIsCrouching(false);
            this.setIsInterested(false);
         }
      }

      if (this.isSleeping() || this.isImmobile()) {
         this.jumping = false;
         this.xxa = 0.0F;
         this.zza = 0.0F;
      }

      super.aiStep();
      if (this.isDefending() && this.random.nextFloat() < 0.05F) {
         this.playSound(SoundEvents.FOX_AGGRO, 1.0F, 1.0F);
      }
   }

   @Override
   protected boolean isImmobile() {
      return this.isDeadOrDying();
   }

   private boolean canEat(ItemStack var1) {
      return var1.has(DataComponents.FOOD) && this.getTarget() == null && this.onGround() && !this.isSleeping();
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      if (var1.nextFloat() < 0.2F) {
         float var3 = var1.nextFloat();
         ItemStack var4;
         if (var3 < 0.05F) {
            var4 = new ItemStack(Items.EMERALD);
         } else if (var3 < 0.2F) {
            var4 = new ItemStack(Items.EGG);
         } else if (var3 < 0.4F) {
            var4 = var1.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
         } else if (var3 < 0.6F) {
            var4 = new ItemStack(Items.WHEAT);
         } else if (var3 < 0.8F) {
            var4 = new ItemStack(Items.LEATHER);
         } else {
            var4 = new ItemStack(Items.FEATHER);
         }

         this.setItemSlot(EquipmentSlot.MAINHAND, var4);
      }
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 45) {
         ItemStack var2 = this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!var2.isEmpty()) {
            for (int var3 = 0; var3 < 8; var3++) {
               Vec3 var4 = new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
                  .xRot(-this.getXRot() * 0.017453292F)
                  .yRot(-this.getYRot() * 0.017453292F);
               this.level()
                  .addParticle(
                     new ItemParticleOption(ParticleTypes.ITEM, var2),
                     this.getX() + this.getLookAngle().x / 2.0,
                     this.getY(),
                     this.getZ() + this.getLookAngle().z / 2.0,
                     var4.x,
                     var4.y + 0.05,
                     var4.z
                  );
            }
         }
      } else {
         super.handleEntityEvent(var1);
      }
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.MAX_HEALTH, 10.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   @Nullable
   public Fox getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Fox var3 = EntityType.FOX.create(var1);
      if (var3 != null) {
         var3.setVariant(this.random.nextBoolean() ? this.getVariant() : ((Fox)var2).getVariant());
      }

      return var3;
   }

   public static boolean checkFoxSpawnRules(EntityType<Fox> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.FOXES_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      Holder var5 = var1.getBiome(this.blockPosition());
      Fox.Type var6 = Fox.Type.byBiome(var5);
      boolean var7 = false;
      if (var4 instanceof Fox.FoxGroupData var8) {
         var6 = var8.type;
         if (var8.getGroupSize() >= 2) {
            var7 = true;
         }
      } else {
         var4 = new Fox.FoxGroupData(var6);
      }

      this.setVariant(var6);
      if (var7) {
         this.setAge(-24000);
      }

      if (var1 instanceof ServerLevel) {
         this.setTargetGoals();
      }

      this.populateDefaultEquipmentSlots(var1.getRandom(), var2);
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
   }

   private void setTargetGoals() {
      if (this.getVariant() == Fox.Type.RED) {
         this.targetSelector.addGoal(4, this.landTargetGoal);
         this.targetSelector.addGoal(4, this.turtleEggTargetGoal);
         this.targetSelector.addGoal(6, this.fishTargetGoal);
      } else {
         this.targetSelector.addGoal(4, this.fishTargetGoal);
         this.targetSelector.addGoal(6, this.landTargetGoal);
         this.targetSelector.addGoal(6, this.turtleEggTargetGoal);
      }
   }

   @Override
   protected void usePlayerItem(Player var1, InteractionHand var2, ItemStack var3) {
      if (this.isFood(var3)) {
         this.playSound(this.getEatingSound(var3), 1.0F, 1.0F);
      }

      super.usePlayerItem(var1, var2, var3);
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(var1);
   }

   public Fox.Type getVariant() {
      return Fox.Type.byId(this.entityData.get(DATA_TYPE_ID));
   }

   public void setVariant(Fox.Type var1) {
      this.entityData.set(DATA_TYPE_ID, var1.getId());
   }

   List<UUID> getTrustedUUIDs() {
      ArrayList var1 = Lists.newArrayList();
      var1.add(this.entityData.get(DATA_TRUSTED_ID_0).orElse(null));
      var1.add(this.entityData.get(DATA_TRUSTED_ID_1).orElse(null));
      return var1;
   }

   void addTrustedUUID(@Nullable UUID var1) {
      if (this.entityData.get(DATA_TRUSTED_ID_0).isPresent()) {
         this.entityData.set(DATA_TRUSTED_ID_1, Optional.ofNullable(var1));
      } else {
         this.entityData.set(DATA_TRUSTED_ID_0, Optional.ofNullable(var1));
      }
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      List var2 = this.getTrustedUUIDs();
      ListTag var3 = new ListTag();

      for (UUID var5 : var2) {
         if (var5 != null) {
            var3.add(NbtUtils.createUUID(var5));
         }
      }

      var1.put("Trusted", var3);
      var1.putBoolean("Sleeping", this.isSleeping());
      var1.putString("Type", this.getVariant().getSerializedName());
      var1.putBoolean("Sitting", this.isSitting());
      var1.putBoolean("Crouching", this.isCrouching());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);

      for (Tag var4 : var1.getList("Trusted", 11)) {
         this.addTrustedUUID(NbtUtils.loadUUID(var4));
      }

      this.setSleeping(var1.getBoolean("Sleeping"));
      this.setVariant(Fox.Type.byName(var1.getString("Type")));
      this.setSitting(var1.getBoolean("Sitting"));
      this.setIsCrouching(var1.getBoolean("Crouching"));
      if (this.level() instanceof ServerLevel) {
         this.setTargetGoals();
      }
   }

   public boolean isSitting() {
      return this.getFlag(1);
   }

   public void setSitting(boolean var1) {
      this.setFlag(1, var1);
   }

   public boolean isFaceplanted() {
      return this.getFlag(64);
   }

   void setFaceplanted(boolean var1) {
      this.setFlag(64, var1);
   }

   boolean isDefending() {
      return this.getFlag(128);
   }

   void setDefending(boolean var1) {
      this.setFlag(128, var1);
   }

   @Override
   public boolean isSleeping() {
      return this.getFlag(32);
   }

   void setSleeping(boolean var1) {
      this.setFlag(32, var1);
   }

   private void setFlag(int var1, boolean var2) {
      if (var2) {
         this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) | var1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)(this.entityData.get(DATA_FLAGS_ID) & ~var1));
      }
   }

   private boolean getFlag(int var1) {
      return (this.entityData.get(DATA_FLAGS_ID) & var1) != 0;
   }

   @Override
   public boolean canTakeItem(ItemStack var1) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(var1);
      return !this.getItemBySlot(var2).isEmpty() ? false : var2 == EquipmentSlot.MAINHAND && super.canTakeItem(var1);
   }

   @Override
   public boolean canHoldItem(ItemStack var1) {
      ItemStack var2 = this.getItemBySlot(EquipmentSlot.MAINHAND);
      return var2.isEmpty() || this.ticksSinceEaten > 0 && var1.has(DataComponents.FOOD) && !var2.has(DataComponents.FOOD);
   }

   private void spitOutItem(ItemStack var1) {
      if (!var1.isEmpty() && !this.level().isClientSide) {
         ItemEntity var2 = new ItemEntity(this.level(), this.getX() + this.getLookAngle().x, this.getY() + 1.0, this.getZ() + this.getLookAngle().z, var1);
         var2.setPickUpDelay(40);
         var2.setThrower(this);
         this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
         this.level().addFreshEntity(var2);
      }
   }

   private void dropItemStack(ItemStack var1) {
      ItemEntity var2 = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), var1);
      this.level().addFreshEntity(var2);
   }

   @Override
   protected void pickUpItem(ItemEntity var1) {
      ItemStack var2 = var1.getItem();
      if (this.canHoldItem(var2)) {
         int var3 = var2.getCount();
         if (var3 > 1) {
            this.dropItemStack(var2.split(var3 - 1));
         }

         this.spitOutItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
         this.onItemPickup(var1);
         this.setItemSlot(EquipmentSlot.MAINHAND, var2.split(1));
         this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
         this.take(var1, var2.getCount());
         var1.discard();
         this.ticksSinceEaten = 0;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.isEffectiveAi()) {
         boolean var1 = this.isInWater();
         if (var1 || this.getTarget() != null || this.level().isThundering()) {
            this.wakeUp();
         }

         if (var1 || this.isSleeping()) {
            this.setSitting(false);
         }

         if (this.isFaceplanted() && this.level().random.nextFloat() < 0.2F) {
            BlockPos var2 = this.blockPosition();
            BlockState var3 = this.level().getBlockState(var2);
            this.level().levelEvent(2001, var2, Block.getId(var3));
         }
      }

      this.interestedAngleO = this.interestedAngle;
      if (this.isInterested()) {
         this.interestedAngle = this.interestedAngle + (1.0F - this.interestedAngle) * 0.4F;
      } else {
         this.interestedAngle = this.interestedAngle + (0.0F - this.interestedAngle) * 0.4F;
      }

      this.crouchAmountO = this.crouchAmount;
      if (this.isCrouching()) {
         this.crouchAmount += 0.2F;
         if (this.crouchAmount > 3.0F) {
            this.crouchAmount = 3.0F;
         }
      } else {
         this.crouchAmount = 0.0F;
      }
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.FOX_FOOD);
   }

   @Override
   protected void onOffspringSpawnedFromEgg(Player var1, Mob var2) {
      ((Fox)var2).addTrustedUUID(var1.getUUID());
   }

   public boolean isPouncing() {
      return this.getFlag(16);
   }

   public void setIsPouncing(boolean var1) {
      this.setFlag(16, var1);
   }

   public boolean isJumping() {
      return this.jumping;
   }

   public boolean isFullyCrouched() {
      return this.crouchAmount == 3.0F;
   }

   public void setIsCrouching(boolean var1) {
      this.setFlag(4, var1);
   }

   @Override
   public boolean isCrouching() {
      return this.getFlag(4);
   }

   public void setIsInterested(boolean var1) {
      this.setFlag(8, var1);
   }

   public boolean isInterested() {
      return this.getFlag(8);
   }

   public float getHeadRollAngle(float var1) {
      return Mth.lerp(var1, this.interestedAngleO, this.interestedAngle) * 0.11F * 3.1415927F;
   }

   public float getCrouchAmount(float var1) {
      return Mth.lerp(var1, this.crouchAmountO, this.crouchAmount);
   }

   @Override
   public void setTarget(@Nullable LivingEntity var1) {
      if (this.isDefending() && var1 == null) {
         this.setDefending(false);
      }

      super.setTarget(var1);
   }

   @Override
   protected int calculateFallDamage(float var1, float var2) {
      return Mth.ceil((var1 - 5.0F) * var2);
   }

   void wakeUp() {
      this.setSleeping(false);
   }

   void clearStates() {
      this.setIsInterested(false);
      this.setIsCrouching(false);
      this.setSitting(false);
      this.setSleeping(false);
      this.setDefending(false);
      this.setFaceplanted(false);
   }

   boolean canMove() {
      return !this.isSleeping() && !this.isSitting() && !this.isFaceplanted();
   }

   @Override
   public void playAmbientSound() {
      SoundEvent var1 = this.getAmbientSound();
      if (var1 == SoundEvents.FOX_SCREECH) {
         this.playSound(var1, 2.0F, this.getVoicePitch());
      } else {
         super.playAmbientSound();
      }
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return SoundEvents.FOX_SLEEP;
      } else {
         if (!this.level().isDay() && this.random.nextFloat() < 0.1F) {
            List var1 = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16.0, 16.0, 16.0), EntitySelector.NO_SPECTATORS);
            if (var1.isEmpty()) {
               return SoundEvents.FOX_SCREECH;
            }
         }

         return SoundEvents.FOX_AMBIENT;
      }
   }

   @Nullable
   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.FOX_HURT;
   }

   @Nullable
   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.FOX_DEATH;
   }

   boolean trusts(UUID var1) {
      return this.getTrustedUUIDs().contains(var1);
   }

   @Override
   protected void dropAllDeathLoot(DamageSource var1) {
      ItemStack var2 = this.getItemBySlot(EquipmentSlot.MAINHAND);
      if (!var2.isEmpty()) {
         this.spawnAtLocation(var2);
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }

      super.dropAllDeathLoot(var1);
   }

   public static boolean isPathClear(Fox var0, LivingEntity var1) {
      double var2 = var1.getZ() - var0.getZ();
      double var4 = var1.getX() - var0.getX();
      double var6 = var2 / var4;
      byte var8 = 6;

      for (int var9 = 0; var9 < 6; var9++) {
         double var10 = var6 == 0.0 ? 0.0 : var2 * (double)((float)var9 / 6.0F);
         double var12 = var6 == 0.0 ? var4 * (double)((float)var9 / 6.0F) : var10 / var6;

         for (int var14 = 1; var14 < 4; var14++) {
            if (!var0.level().getBlockState(BlockPos.containing(var0.getX() + var12, var0.getY() + (double)var14, var0.getZ() + var10)).canBeReplaced()) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.55F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   class DefendTrustedTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
      @Nullable
      private LivingEntity trustedLastHurtBy;
      @Nullable
      private LivingEntity trustedLastHurt;
      private int timestamp;

      public DefendTrustedTargetGoal(Class<LivingEntity> var2, boolean var3, boolean var4, @Nullable Predicate<LivingEntity> var5) {
         super(Fox.this, var2, 10, var3, var4, var5);
      }

      @Override
      public boolean canUse() {
         if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
         } else {
            for (UUID var2 : Fox.this.getTrustedUUIDs()) {
               if (var2 != null && Fox.this.level() instanceof ServerLevel && ((ServerLevel)Fox.this.level()).getEntity(var2) instanceof LivingEntity var4) {
                  this.trustedLastHurt = var4;
                  this.trustedLastHurtBy = var4.getLastHurtByMob();
                  int var5 = var4.getLastHurtByMobTimestamp();
                  return var5 != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions);
               }
            }

            return false;
         }
      }

      @Override
      public void start() {
         this.setTarget(this.trustedLastHurtBy);
         this.target = this.trustedLastHurtBy;
         if (this.trustedLastHurt != null) {
            this.timestamp = this.trustedLastHurt.getLastHurtByMobTimestamp();
         }

         Fox.this.playSound(SoundEvents.FOX_AGGRO, 1.0F, 1.0F);
         Fox.this.setDefending(true);
         Fox.this.wakeUp();
         super.start();
      }
   }

   class FaceplantGoal extends Goal {
      int countdown;

      public FaceplantGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         return Fox.this.isFaceplanted();
      }

      @Override
      public boolean canContinueToUse() {
         return this.canUse() && this.countdown > 0;
      }

      @Override
      public void start() {
         this.countdown = this.adjustedTickDelay(40);
      }

      @Override
      public void stop() {
         Fox.this.setFaceplanted(false);
      }

      @Override
      public void tick() {
         this.countdown--;
      }
   }

   public class FoxAlertableEntitiesSelector implements Predicate<LivingEntity> {
      public FoxAlertableEntitiesSelector() {
         super();
      }

      public boolean test(LivingEntity var1) {
         if (var1 instanceof Fox) {
            return false;
         } else if (var1 instanceof Chicken || var1 instanceof Rabbit || var1 instanceof Monster) {
            return true;
         } else if (var1 instanceof TamableAnimal) {
            return !((TamableAnimal)var1).isTame();
         } else if (!(var1 instanceof Player) || !var1.isSpectator() && !((Player)var1).isCreative()) {
            return Fox.this.trusts(var1.getUUID()) ? false : !var1.isSleeping() && !var1.isDiscrete();
         } else {
            return false;
         }
      }
   }

   abstract class FoxBehaviorGoal extends Goal {
      private final TargetingConditions alertableTargeting = TargetingConditions.forCombat()
         .range(12.0)
         .ignoreLineOfSight()
         .selector(Fox.this.new FoxAlertableEntitiesSelector());

      FoxBehaviorGoal() {
         super();
      }

      protected boolean hasShelter() {
         BlockPos var1 = BlockPos.containing(Fox.this.getX(), Fox.this.getBoundingBox().maxY, Fox.this.getZ());
         return !Fox.this.level().canSeeSky(var1) && Fox.this.getWalkTargetValue(var1) >= 0.0F;
      }

      protected boolean alertable() {
         return !Fox.this.level()
            .getNearbyEntities(LivingEntity.class, this.alertableTargeting, Fox.this, Fox.this.getBoundingBox().inflate(12.0, 6.0, 12.0))
            .isEmpty();
      }
   }

   class FoxBreedGoal extends BreedGoal {
      public FoxBreedGoal(double var2) {
         super(Fox.this, var2);
      }

      @Override
      public void start() {
         ((Fox)this.animal).clearStates();
         ((Fox)this.partner).clearStates();
         super.start();
      }

      @Override
      protected void breed() {
         ServerLevel var1 = (ServerLevel)this.level;
         Fox var2 = (Fox)this.animal.getBreedOffspring(var1, this.partner);
         if (var2 != null) {
            ServerPlayer var3 = this.animal.getLoveCause();
            ServerPlayer var4 = this.partner.getLoveCause();
            ServerPlayer var5 = var3;
            if (var3 != null) {
               var2.addTrustedUUID(var3.getUUID());
            } else {
               var5 = var4;
            }

            if (var4 != null && var3 != var4) {
               var2.addTrustedUUID(var4.getUUID());
            }

            if (var5 != null) {
               var5.awardStat(Stats.ANIMALS_BRED);
               CriteriaTriggers.BRED_ANIMALS.trigger(var5, this.animal, this.partner, var2);
            }

            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            var2.setAge(-24000);
            var2.moveTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
            var1.addFreshEntityWithPassengers(var2);
            this.level.broadcastEntityEvent(this.animal, (byte)18);
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
               this.level
                  .addFreshEntity(
                     new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1)
                  );
            }
         }
      }
   }

   public class FoxEatBerriesGoal extends MoveToBlockGoal {
      private static final int WAIT_TICKS = 40;
      protected int ticksWaited;

      public FoxEatBerriesGoal(double var2, int var4, int var5) {
         super(Fox.this, var2, var4, var5);
      }

      @Override
      public double acceptedDistance() {
         return 2.0;
      }

      @Override
      public boolean shouldRecalculatePath() {
         return this.tryTicks % 100 == 0;
      }

      @Override
      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         BlockState var3 = var1.getBlockState(var2);
         return var3.is(Blocks.SWEET_BERRY_BUSH) && var3.getValue(SweetBerryBushBlock.AGE) >= 2 || CaveVines.hasGlowBerries(var3);
      }

      @Override
      public void tick() {
         if (this.isReachedTarget()) {
            if (this.ticksWaited >= 40) {
               this.onReachedTarget();
            } else {
               this.ticksWaited++;
            }
         } else if (!this.isReachedTarget() && Fox.this.random.nextFloat() < 0.05F) {
            Fox.this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
         }

         super.tick();
      }

      protected void onReachedTarget() {
         if (Fox.this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            BlockState var1 = Fox.this.level().getBlockState(this.blockPos);
            if (var1.is(Blocks.SWEET_BERRY_BUSH)) {
               this.pickSweetBerries(var1);
            } else if (CaveVines.hasGlowBerries(var1)) {
               this.pickGlowBerry(var1);
            }
         }
      }

      private void pickGlowBerry(BlockState var1) {
         CaveVines.use(Fox.this, var1, Fox.this.level(), this.blockPos);
      }

      private void pickSweetBerries(BlockState var1) {
         int var2 = var1.getValue(SweetBerryBushBlock.AGE);
         var1.setValue(SweetBerryBushBlock.AGE, Integer.valueOf(1));
         int var3 = 1 + Fox.this.level().random.nextInt(2) + (var2 == 3 ? 1 : 0);
         ItemStack var4 = Fox.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (var4.isEmpty()) {
            Fox.this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
            var3--;
         }

         if (var3 > 0) {
            Block.popResource(Fox.this.level(), this.blockPos, new ItemStack(Items.SWEET_BERRIES, var3));
         }

         Fox.this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
         Fox.this.level().setBlock(this.blockPos, var1.setValue(SweetBerryBushBlock.AGE, Integer.valueOf(1)), 2);
         Fox.this.level().gameEvent(GameEvent.BLOCK_CHANGE, this.blockPos, GameEvent.Context.of(Fox.this));
      }

      @Override
      public boolean canUse() {
         return !Fox.this.isSleeping() && super.canUse();
      }

      @Override
      public void start() {
         this.ticksWaited = 0;
         Fox.this.setSitting(false);
         super.start();
      }
   }

   class FoxFloatGoal extends FloatGoal {
      public FoxFloatGoal() {
         super(Fox.this);
      }

      @Override
      public void start() {
         super.start();
         Fox.this.clearStates();
      }

      @Override
      public boolean canUse() {
         return Fox.this.isInWater() && Fox.this.getFluidHeight(FluidTags.WATER) > 0.25 || Fox.this.isInLava();
      }
   }

   class FoxFollowParentGoal extends FollowParentGoal {
      private final Fox fox;

      public FoxFollowParentGoal(Fox var2, double var3) {
         super(var2, var3);
         this.fox = var2;
      }

      @Override
      public boolean canUse() {
         return !this.fox.isDefending() && super.canUse();
      }

      @Override
      public boolean canContinueToUse() {
         return !this.fox.isDefending() && super.canContinueToUse();
      }

      @Override
      public void start() {
         this.fox.clearStates();
         super.start();
      }
   }

   public static class FoxGroupData extends AgeableMob.AgeableMobGroupData {
      public final Fox.Type type;

      public FoxGroupData(Fox.Type var1) {
         super(false);
         this.type = var1;
      }
   }

   class FoxLookAtPlayerGoal extends LookAtPlayerGoal {
      public FoxLookAtPlayerGoal(Mob var2, Class<? extends LivingEntity> var3, float var4) {
         super(var2, var3, var4);
      }

      @Override
      public boolean canUse() {
         return super.canUse() && !Fox.this.isFaceplanted() && !Fox.this.isInterested();
      }

      @Override
      public boolean canContinueToUse() {
         return super.canContinueToUse() && !Fox.this.isFaceplanted() && !Fox.this.isInterested();
      }
   }

   public class FoxLookControl extends LookControl {
      public FoxLookControl() {
         super(Fox.this);
      }

      @Override
      public void tick() {
         if (!Fox.this.isSleeping()) {
            super.tick();
         }
      }

      @Override
      protected boolean resetXRotOnTick() {
         return !Fox.this.isPouncing() && !Fox.this.isCrouching() && !Fox.this.isInterested() && !Fox.this.isFaceplanted();
      }
   }

   class FoxMeleeAttackGoal extends MeleeAttackGoal {
      public FoxMeleeAttackGoal(double var2, boolean var4) {
         super(Fox.this, var2, var4);
      }

      @Override
      protected void checkAndPerformAttack(LivingEntity var1) {
         if (this.canPerformAttack(var1)) {
            this.resetAttackCooldown();
            this.mob.doHurtTarget(var1);
            Fox.this.playSound(SoundEvents.FOX_BITE, 1.0F, 1.0F);
         }
      }

      @Override
      public void start() {
         Fox.this.setIsInterested(false);
         super.start();
      }

      @Override
      public boolean canUse() {
         return !Fox.this.isSitting() && !Fox.this.isSleeping() && !Fox.this.isCrouching() && !Fox.this.isFaceplanted() && super.canUse();
      }
   }

   class FoxMoveControl extends MoveControl {
      public FoxMoveControl() {
         super(Fox.this);
      }

      @Override
      public void tick() {
         if (Fox.this.canMove()) {
            super.tick();
         }
      }
   }

   class FoxPanicGoal extends PanicGoal {
      public FoxPanicGoal(double var2) {
         super(Fox.this, var2);
      }

      @Override
      public boolean shouldPanic() {
         return !Fox.this.isDefending() && super.shouldPanic();
      }
   }

   public class FoxPounceGoal extends JumpGoal {
      public FoxPounceGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         if (!Fox.this.isFullyCrouched()) {
            return false;
         } else {
            LivingEntity var1 = Fox.this.getTarget();
            if (var1 != null && var1.isAlive()) {
               if (var1.getMotionDirection() != var1.getDirection()) {
                  return false;
               } else {
                  boolean var2 = Fox.isPathClear(Fox.this, var1);
                  if (!var2) {
                     Fox.this.getNavigation().createPath(var1, 0);
                     Fox.this.setIsCrouching(false);
                     Fox.this.setIsInterested(false);
                  }

                  return var2;
               }
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean canContinueToUse() {
         LivingEntity var1 = Fox.this.getTarget();
         if (var1 != null && var1.isAlive()) {
            double var2 = Fox.this.getDeltaMovement().y;
            return (!(var2 * var2 < 0.05000000074505806) || !(Math.abs(Fox.this.getXRot()) < 15.0F) || !Fox.this.onGround()) && !Fox.this.isFaceplanted();
         } else {
            return false;
         }
      }

      @Override
      public boolean isInterruptable() {
         return false;
      }

      @Override
      public void start() {
         Fox.this.setJumping(true);
         Fox.this.setIsPouncing(true);
         Fox.this.setIsInterested(false);
         LivingEntity var1 = Fox.this.getTarget();
         if (var1 != null) {
            Fox.this.getLookControl().setLookAt(var1, 60.0F, 30.0F);
            Vec3 var2 = new Vec3(var1.getX() - Fox.this.getX(), var1.getY() - Fox.this.getY(), var1.getZ() - Fox.this.getZ()).normalize();
            Fox.this.setDeltaMovement(Fox.this.getDeltaMovement().add(var2.x * 0.8, 0.9, var2.z * 0.8));
         }

         Fox.this.getNavigation().stop();
      }

      @Override
      public void stop() {
         Fox.this.setIsCrouching(false);
         Fox.this.crouchAmount = 0.0F;
         Fox.this.crouchAmountO = 0.0F;
         Fox.this.setIsInterested(false);
         Fox.this.setIsPouncing(false);
      }

      @Override
      public void tick() {
         LivingEntity var1 = Fox.this.getTarget();
         if (var1 != null) {
            Fox.this.getLookControl().setLookAt(var1, 60.0F, 30.0F);
         }

         if (!Fox.this.isFaceplanted()) {
            Vec3 var2 = Fox.this.getDeltaMovement();
            if (var2.y * var2.y < 0.029999999329447746 && Fox.this.getXRot() != 0.0F) {
               Fox.this.setXRot(Mth.rotLerp(0.2F, Fox.this.getXRot(), 0.0F));
            } else {
               double var3 = var2.horizontalDistance();
               double var5 = Math.signum(-var2.y) * Math.acos(var3 / var2.length()) * 57.2957763671875;
               Fox.this.setXRot((float)var5);
            }
         }

         if (var1 != null && Fox.this.distanceTo(var1) <= 2.0F) {
            Fox.this.doHurtTarget(var1);
         } else if (Fox.this.getXRot() > 0.0F
            && Fox.this.onGround()
            && (float)Fox.this.getDeltaMovement().y != 0.0F
            && Fox.this.level().getBlockState(Fox.this.blockPosition()).is(Blocks.SNOW)) {
            Fox.this.setXRot(60.0F);
            Fox.this.setTarget(null);
            Fox.this.setFaceplanted(true);
         }
      }
   }

   class FoxSearchForItemsGoal extends Goal {
      public FoxSearchForItemsGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      @Override
      public boolean canUse() {
         if (!Fox.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            return false;
         } else if (Fox.this.getTarget() != null || Fox.this.getLastHurtByMob() != null) {
            return false;
         } else if (!Fox.this.canMove()) {
            return false;
         } else if (Fox.this.getRandom().nextInt(reducedTickDelay(10)) != 0) {
            return false;
         } else {
            List var1 = Fox.this.level().getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Fox.ALLOWED_ITEMS);
            return !var1.isEmpty() && Fox.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
         }
      }

      @Override
      public void tick() {
         List var1 = Fox.this.level().getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Fox.ALLOWED_ITEMS);
         ItemStack var2 = Fox.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (var2.isEmpty() && !var1.isEmpty()) {
            Fox.this.getNavigation().moveTo((Entity)var1.get(0), 1.2000000476837158);
         }
      }

      @Override
      public void start() {
         List var1 = Fox.this.level().getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Fox.ALLOWED_ITEMS);
         if (!var1.isEmpty()) {
            Fox.this.getNavigation().moveTo((Entity)var1.get(0), 1.2000000476837158);
         }
      }
   }

   class FoxStrollThroughVillageGoal extends StrollThroughVillageGoal {
      public FoxStrollThroughVillageGoal(int var2, int var3) {
         super(Fox.this, var3);
      }

      @Override
      public void start() {
         Fox.this.clearStates();
         super.start();
      }

      @Override
      public boolean canUse() {
         return super.canUse() && this.canFoxMove();
      }

      @Override
      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.canFoxMove();
      }

      private boolean canFoxMove() {
         return !Fox.this.isSleeping() && !Fox.this.isSitting() && !Fox.this.isDefending() && Fox.this.getTarget() == null;
      }
   }

   class PerchAndSearchGoal extends Fox.FoxBehaviorGoal {
      private double relX;
      private double relZ;
      private int lookTime;
      private int looksRemaining;

      public PerchAndSearchGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         return Fox.this.getLastHurtByMob() == null
            && Fox.this.getRandom().nextFloat() < 0.02F
            && !Fox.this.isSleeping()
            && Fox.this.getTarget() == null
            && Fox.this.getNavigation().isDone()
            && !this.alertable()
            && !Fox.this.isPouncing()
            && !Fox.this.isCrouching();
      }

      @Override
      public boolean canContinueToUse() {
         return this.looksRemaining > 0;
      }

      @Override
      public void start() {
         this.resetLook();
         this.looksRemaining = 2 + Fox.this.getRandom().nextInt(3);
         Fox.this.setSitting(true);
         Fox.this.getNavigation().stop();
      }

      @Override
      public void stop() {
         Fox.this.setSitting(false);
      }

      @Override
      public void tick() {
         this.lookTime--;
         if (this.lookTime <= 0) {
            this.looksRemaining--;
            this.resetLook();
         }

         Fox.this.getLookControl()
            .setLookAt(
               Fox.this.getX() + this.relX, Fox.this.getEyeY(), Fox.this.getZ() + this.relZ, (float)Fox.this.getMaxHeadYRot(), (float)Fox.this.getMaxHeadXRot()
            );
      }

      private void resetLook() {
         double var1 = 6.283185307179586 * Fox.this.getRandom().nextDouble();
         this.relX = Math.cos(var1);
         this.relZ = Math.sin(var1);
         this.lookTime = this.adjustedTickDelay(80 + Fox.this.getRandom().nextInt(20));
      }
   }

   class SeekShelterGoal extends FleeSunGoal {
      private int interval = reducedTickDelay(100);

      public SeekShelterGoal(double var2) {
         super(Fox.this, var2);
      }

      @Override
      public boolean canUse() {
         if (!Fox.this.isSleeping() && this.mob.getTarget() == null) {
            if (Fox.this.level().isThundering() && Fox.this.level().canSeeSky(this.mob.blockPosition())) {
               return this.setWantedPos();
            } else if (this.interval > 0) {
               this.interval--;
               return false;
            } else {
               this.interval = 100;
               BlockPos var1 = this.mob.blockPosition();
               return Fox.this.level().isDay() && Fox.this.level().canSeeSky(var1) && !((ServerLevel)Fox.this.level()).isVillage(var1) && this.setWantedPos();
            }
         } else {
            return false;
         }
      }

      @Override
      public void start() {
         Fox.this.clearStates();
         super.start();
      }
   }

   class SleepGoal extends Fox.FoxBehaviorGoal {
      private static final int WAIT_TIME_BEFORE_SLEEP = reducedTickDelay(140);
      private int countdown = Fox.this.random.nextInt(WAIT_TIME_BEFORE_SLEEP);

      public SleepGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
      }

      @Override
      public boolean canUse() {
         return Fox.this.xxa == 0.0F && Fox.this.yya == 0.0F && Fox.this.zza == 0.0F ? this.canSleep() || Fox.this.isSleeping() : false;
      }

      @Override
      public boolean canContinueToUse() {
         return this.canSleep();
      }

      private boolean canSleep() {
         if (this.countdown > 0) {
            this.countdown--;
            return false;
         } else {
            return Fox.this.level().isDay() && this.hasShelter() && !this.alertable() && !Fox.this.isInPowderSnow;
         }
      }

      @Override
      public void stop() {
         this.countdown = Fox.this.random.nextInt(WAIT_TIME_BEFORE_SLEEP);
         Fox.this.clearStates();
      }

      @Override
      public void start() {
         Fox.this.setSitting(false);
         Fox.this.setIsCrouching(false);
         Fox.this.setIsInterested(false);
         Fox.this.setJumping(false);
         Fox.this.setSleeping(true);
         Fox.this.getNavigation().stop();
         Fox.this.getMoveControl().setWantedPosition(Fox.this.getX(), Fox.this.getY(), Fox.this.getZ(), 0.0);
      }
   }

   class StalkPreyGoal extends Goal {
      public StalkPreyGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         if (Fox.this.isSleeping()) {
            return false;
         } else {
            LivingEntity var1 = Fox.this.getTarget();
            return var1 != null
               && var1.isAlive()
               && Fox.STALKABLE_PREY.test(var1)
               && Fox.this.distanceToSqr(var1) > 36.0
               && !Fox.this.isCrouching()
               && !Fox.this.isInterested()
               && !Fox.this.jumping;
         }
      }

      @Override
      public void start() {
         Fox.this.setSitting(false);
         Fox.this.setFaceplanted(false);
      }

      @Override
      public void stop() {
         LivingEntity var1 = Fox.this.getTarget();
         if (var1 != null && Fox.isPathClear(Fox.this, var1)) {
            Fox.this.setIsInterested(true);
            Fox.this.setIsCrouching(true);
            Fox.this.getNavigation().stop();
            Fox.this.getLookControl().setLookAt(var1, (float)Fox.this.getMaxHeadYRot(), (float)Fox.this.getMaxHeadXRot());
         } else {
            Fox.this.setIsInterested(false);
            Fox.this.setIsCrouching(false);
         }
      }

      @Override
      public void tick() {
         LivingEntity var1 = Fox.this.getTarget();
         if (var1 != null) {
            Fox.this.getLookControl().setLookAt(var1, (float)Fox.this.getMaxHeadYRot(), (float)Fox.this.getMaxHeadXRot());
            if (Fox.this.distanceToSqr(var1) <= 36.0) {
               Fox.this.setIsInterested(true);
               Fox.this.setIsCrouching(true);
               Fox.this.getNavigation().stop();
            } else {
               Fox.this.getNavigation().moveTo(var1, 1.5);
            }
         }
      }
   }

   public static enum Type implements StringRepresentable {
      RED(0, "red"),
      SNOW(1, "snow");

      public static final StringRepresentable.EnumCodec<Fox.Type> CODEC = StringRepresentable.fromEnum(Fox.Type::values);
      private static final IntFunction<Fox.Type> BY_ID = ByIdMap.continuous(Fox.Type::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      private final int id;
      private final String name;

      private Type(int var3, String var4) {
         this.id = var3;
         this.name = var4;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public int getId() {
         return this.id;
      }

      public static Fox.Type byName(String var0) {
         return CODEC.byName(var0, RED);
      }

      public static Fox.Type byId(int var0) {
         return BY_ID.apply(var0);
      }

      public static Fox.Type byBiome(Holder<Biome> var0) {
         return var0.is(BiomeTags.SPAWNS_SNOW_FOXES) ? SNOW : RED;
      }
   }
}
