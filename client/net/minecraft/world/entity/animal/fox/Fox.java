package net.minecraft.world.entity.animal.fox;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Fox extends Animal {
   private static final EntityDataAccessor<Integer> DATA_TYPE_ID;
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
   private static final float BABY_SCALE = 0.6F;
   private static final int FLAG_SITTING = 1;
   public static final int FLAG_CROUCHING = 4;
   public static final int FLAG_INTERESTED = 8;
   public static final int FLAG_POUNCING = 16;
   private static final int FLAG_SLEEPING = 32;
   private static final int FLAG_FACEPLANTED = 64;
   private static final int FLAG_DEFENDING = 128;
   private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_TRUSTED_ID_0;
   private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_TRUSTED_ID_1;
   private static final Predicate<ItemEntity> ALLOWED_ITEMS;
   private static final Predicate<Entity> TRUSTED_TARGET_SELECTOR;
   private static final Predicate<Entity> STALKABLE_PREY;
   private static final Predicate<Entity> AVOID_PLAYERS;
   private static final int MIN_TICKS_BEFORE_EAT = 600;
   private static final EntityDimensions BABY_DIMENSIONS;
   private static final Codec<List<EntityReference<LivingEntity>>> TRUSTED_LIST_CODEC;
   private static final boolean DEFAULT_SLEEPING = false;
   private static final boolean DEFAULT_SITTING = false;
   private static final boolean DEFAULT_CROUCHING = false;
   private Goal landTargetGoal;
   private Goal turtleEggTargetGoal;
   private Goal fishTargetGoal;
   private float interestedAngle;
   private float interestedAngleO;
   private float crouchAmount;
   private float crouchAmountO;
   private static final float MAX_CROUCH_AMOUNT = 5.0F;
   private int ticksSinceEaten;

   public Fox(final EntityType<? extends Fox> type, final Level level) {
      super(type, level);
      this.lookControl = new FoxLookControl();
      this.moveControl = new FoxMoveControl(this);
      this.setPathfindingMalus(PathType.DAMAGING_IN_NEIGHBOR, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGING, 0.0F);
      this.setCanPickUpLoot(true);
      this.getNavigation().setRequiredPathLength(32.0F);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_TRUSTED_ID_0, Optional.empty());
      entityData.define(DATA_TRUSTED_ID_1, Optional.empty());
      entityData.define(DATA_TYPE_ID, Fox.Variant.DEFAULT.getId());
      entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   protected void registerGoals() {
      this.landTargetGoal = new NearestAttackableTargetGoal(this, Animal.class, 10, false, false, (target, level) -> target instanceof Chicken || target instanceof Rabbit);
      this.turtleEggTargetGoal = new NearestAttackableTargetGoal(this, Turtle.class, 10, false, false, Turtle.BABY_ON_LAND_SELECTOR);
      this.fishTargetGoal = new NearestAttackableTargetGoal(this, AbstractFish.class, 20, false, false, (target, level) -> target instanceof AbstractSchoolingFish);
      this.goalSelector.addGoal(0, new FoxFloatGoal());
      this.goalSelector.addGoal(0, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
      this.goalSelector.addGoal(1, new FaceplantGoal());
      this.goalSelector.addGoal(2, new FoxPanicGoal(2.2));
      this.goalSelector.addGoal(3, new FoxBreedGoal(1.0));
      this.goalSelector.addGoal(4, new AvoidEntityGoal(this, Player.class, 16.0F, 1.6, 1.4, (entity) -> AVOID_PLAYERS.test(entity) && !this.trusts(entity) && !this.isDefending()));
      this.goalSelector.addGoal(4, new AvoidEntityGoal(this, Wolf.class, 8.0F, 1.6, 1.4, (entity) -> !((Wolf)entity).isTame() && !this.isDefending()));
      this.goalSelector.addGoal(4, new AvoidEntityGoal(this, PolarBear.class, 8.0F, 1.6, 1.4, (entity) -> !this.isDefending()));
      this.goalSelector.addGoal(5, new StalkPreyGoal());
      this.goalSelector.addGoal(6, new FoxPounceGoal());
      this.goalSelector.addGoal(6, new SeekShelterGoal(1.25));
      this.goalSelector.addGoal(7, new FoxMeleeAttackGoal(1.2000000476837158, true));
      this.goalSelector.addGoal(7, new SleepGoal());
      this.goalSelector.addGoal(8, new FoxFollowParentGoal(this, 1.25));
      this.goalSelector.addGoal(9, new FoxStrollThroughVillageGoal(32, 200));
      this.goalSelector.addGoal(10, new FoxEatBerriesGoal(1.2000000476837158, 12, 1));
      this.goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(11, new FoxSearchForItemsGoal());
      this.goalSelector.addGoal(12, new FoxLookAtPlayerGoal(this, Player.class, 24.0F));
      this.goalSelector.addGoal(13, new PerchAndSearchGoal());
      this.targetSelector.addGoal(3, new DefendTrustedTargetGoal(LivingEntity.class, false, false, (target, level) -> TRUSTED_TARGET_SELECTOR.test(target) && !this.trusts(target)));
   }

   public void aiStep() {
      if (!this.level().isClientSide() && this.isAlive() && this.isEffectiveAi()) {
         ++this.ticksSinceEaten;
         ItemStack itemInMouth = this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (this.canEat(itemInMouth)) {
            if (this.ticksSinceEaten > 600) {
               ItemStack remainingFood = itemInMouth.finishUsingItem(this.level(), this);
               if (!remainingFood.isEmpty()) {
                  this.setItemSlot(EquipmentSlot.MAINHAND, remainingFood);
               }

               this.ticksSinceEaten = 0;
            } else if (this.ticksSinceEaten > 560 && this.random.nextFloat() < 0.1F) {
               this.playEatingSound();
               this.level().broadcastEntityEvent(this, (byte)45);
            }
         }

         LivingEntity target = this.getTarget();
         if (target == null || !target.isAlive()) {
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

   protected boolean isImmobile() {
      return this.isDeadOrDying();
   }

   private boolean canEat(final ItemStack itemInMouth) {
      return this.isConsumableFood(itemInMouth) && this.getTarget() == null && this.onGround() && !this.isSleeping();
   }

   private boolean isConsumableFood(final ItemStack itemStack) {
      return itemStack.has(DataComponents.FOOD) && itemStack.has(DataComponents.CONSUMABLE);
   }

   protected void populateDefaultEquipmentSlots(final RandomSource random, final DifficultyInstance difficulty) {
      if (random.nextFloat() < 0.2F) {
         float odds = random.nextFloat();
         ItemStack heldInMouth;
         if (odds < 0.05F) {
            heldInMouth = new ItemStack(Items.EMERALD);
         } else if (odds < 0.2F) {
            heldInMouth = new ItemStack(Items.EGG);
         } else if (odds < 0.4F) {
            heldInMouth = random.nextBoolean() ? new ItemStack(Items.RABBIT_FOOT) : new ItemStack(Items.RABBIT_HIDE);
         } else if (odds < 0.6F) {
            heldInMouth = new ItemStack(Items.WHEAT);
         } else if (odds < 0.8F) {
            heldInMouth = new ItemStack(Items.LEATHER);
         } else {
            heldInMouth = new ItemStack(Items.FEATHER);
         }

         this.setItemSlot(EquipmentSlot.MAINHAND, heldInMouth);
      }

   }

   public void handleEntityEvent(final byte id) {
      if (id == 45) {
         ItemStack mouthItem = this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!mouthItem.isEmpty()) {
            ItemParticleOption breakParticle = new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(mouthItem));

            for(int i = 0; i < 8; ++i) {
               Vec3 direction = (new Vec3(((double)this.random.nextFloat() - 0.5) * 0.1, (double)this.random.nextFloat() * 0.1 + 0.1, 0.0)).xRot(-this.getXRot() * 0.017453292F).yRot(-this.getYRot() * 0.017453292F);
               this.level().addParticle(breakParticle, this.getX() + this.getLookAngle().x / 2.0, this.getY(), this.getZ() + this.getLookAngle().z / 2.0, direction.x, direction.y + 0.05, direction.z);
            }
         }
      } else {
         super.handleEntityEvent(id);
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.MAX_HEALTH, 10.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.SAFE_FALL_DISTANCE, 5.0).add(Attributes.FOLLOW_RANGE, 32.0);
   }

   public @Nullable Fox getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      Fox baby = EntityTypes.FOX.create(level, EntitySpawnReason.BREEDING);
      if (baby != null) {
         baby.setVariant(this.random.nextBoolean() ? this.getVariant() : ((Fox)partner).getVariant());
      }

      return baby;
   }

   public static boolean checkFoxSpawnRules(final EntityType<Fox> type, final LevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      return level.getBlockState(pos.below()).is(BlockTags.FOXES_SPAWNABLE_ON) && isBrightEnoughToSpawn(level, pos);
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
      Holder<Biome> biome = level.getBiome(this.blockPosition());
      Variant variant = Fox.Variant.byBiome(biome);
      boolean isBaby = false;
      if (groupData instanceof FoxGroupData foxGroupData) {
         variant = foxGroupData.variant;
         if (foxGroupData.getGroupSize() >= 2) {
            isBaby = true;
         }
      } else {
         groupData = new FoxGroupData(variant);
      }

      this.setVariant(variant);
      if (isBaby) {
         this.setAge(-24000);
      }

      if (level instanceof ServerLevel) {
         this.setTargetGoals();
      }

      this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   private void setTargetGoals() {
      if (this.getVariant() == Fox.Variant.RED) {
         this.targetSelector.addGoal(4, this.landTargetGoal);
         this.targetSelector.addGoal(4, this.turtleEggTargetGoal);
         this.targetSelector.addGoal(6, this.fishTargetGoal);
      } else {
         this.targetSelector.addGoal(4, this.fishTargetGoal);
         this.targetSelector.addGoal(6, this.landTargetGoal);
         this.targetSelector.addGoal(6, this.turtleEggTargetGoal);
      }

   }

   protected void playEatingSound() {
      this.playSound(SoundEvents.FOX_EAT, 1.0F, 1.0F);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   public Variant getVariant() {
      return Fox.Variant.byId((Integer)this.entityData.get(DATA_TYPE_ID));
   }

   private void setVariant(final Variant variant) {
      this.entityData.set(DATA_TYPE_ID, variant.getId());
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.FOX_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.FOX_VARIANT);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.FOX_VARIANT) {
         this.setVariant((Variant)castComponentValue(DataComponents.FOX_VARIANT, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   private Stream<EntityReference<LivingEntity>> getTrustedEntities() {
      return Stream.concat(((Optional)this.entityData.get(DATA_TRUSTED_ID_0)).stream(), ((Optional)this.entityData.get(DATA_TRUSTED_ID_1)).stream());
   }

   private void addTrustedEntity(final LivingEntity entity) {
      this.addTrustedEntity(EntityReference.of(entity));
   }

   private void addTrustedEntity(final EntityReference<LivingEntity> reference) {
      if (((Optional)this.entityData.get(DATA_TRUSTED_ID_0)).isPresent()) {
         this.entityData.set(DATA_TRUSTED_ID_1, Optional.of(reference));
      } else {
         this.entityData.set(DATA_TRUSTED_ID_0, Optional.of(reference));
      }

   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("Trusted", TRUSTED_LIST_CODEC, this.getTrustedEntities().toList());
      output.putBoolean("Sleeping", this.isSleeping());
      output.store("Type", Fox.Variant.CODEC, this.getVariant());
      output.putBoolean("Sitting", this.isSitting());
      output.putBoolean("Crouching", this.isCrouching());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.clearTrusted();
      ((List)input.read("Trusted", TRUSTED_LIST_CODEC).orElse(List.of())).forEach(this::addTrustedEntity);
      this.setSleeping(input.getBooleanOr("Sleeping", false));
      this.setVariant((Variant)input.read("Type", Fox.Variant.CODEC).orElse(Fox.Variant.DEFAULT));
      this.setSitting(input.getBooleanOr("Sitting", false));
      this.setIsCrouching(input.getBooleanOr("Crouching", false));
      if (this.level() instanceof ServerLevel) {
         this.setTargetGoals();
      }

   }

   private void clearTrusted() {
      this.entityData.set(DATA_TRUSTED_ID_0, Optional.empty());
      this.entityData.set(DATA_TRUSTED_ID_1, Optional.empty());
   }

   public boolean isSitting() {
      return this.getFlag(1);
   }

   public void setSitting(final boolean value) {
      this.setFlag(1, value);
   }

   public boolean isFaceplanted() {
      return this.getFlag(64);
   }

   private void setFaceplanted(final boolean faceplanted) {
      this.setFlag(64, faceplanted);
   }

   private boolean isDefending() {
      return this.getFlag(128);
   }

   private void setDefending(final boolean defending) {
      this.setFlag(128, defending);
   }

   public boolean isSleeping() {
      return this.getFlag(32);
   }

   private void setSleeping(final boolean sleeping) {
      this.setFlag(32, sleeping);
   }

   private void setFlag(final int flag, final boolean value) {
      if (value) {
         this.entityData.set(DATA_FLAGS_ID, (byte)((Byte)this.entityData.get(DATA_FLAGS_ID) | flag));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)((Byte)this.entityData.get(DATA_FLAGS_ID) & ~flag));
      }

   }

   private boolean getFlag(final int flag) {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & flag) != 0;
   }

   protected boolean canDispenserEquipIntoSlot(final EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND && this.canPickUpLoot();
   }

   public boolean canHoldItem(final ItemStack itemStack) {
      ItemStack heldItemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
      return heldItemStack.isEmpty() || this.ticksSinceEaten > 0 && this.isConsumableFood(itemStack) && !this.isConsumableFood(heldItemStack);
   }

   private void spitOutItem(final ItemStack itemStack) {
      if (!itemStack.isEmpty() && !this.level().isClientSide()) {
         ItemEntity thrownItem = new ItemEntity(this.level(), this.getX() + this.getLookAngle().x, this.getY() + 1.0, this.getZ() + this.getLookAngle().z, itemStack);
         thrownItem.setPickUpDelay(40);
         thrownItem.setThrower(this);
         this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
         this.level().addFreshEntity(thrownItem);
      }
   }

   private void dropItemStack(final ItemStack itemStack) {
      ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), itemStack);
      this.level().addFreshEntity(itemEntity);
   }

   protected void pickUpItem(final ServerLevel level, final ItemEntity entity) {
      ItemStack itemStack = entity.getItem();
      if (this.canHoldItem(itemStack)) {
         int count = itemStack.getCount();
         if (count > 1) {
            this.dropItemStack(itemStack.split(count - 1));
         }

         this.spitOutItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
         this.onItemPickup(entity);
         this.setItemSlot(EquipmentSlot.MAINHAND, itemStack.split(1));
         this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
         this.take(entity, itemStack.getCount());
         entity.discard();
         this.ticksSinceEaten = 0;
      }

   }

   public void tick() {
      super.tick();
      if (this.isEffectiveAi()) {
         boolean inWater = this.isInWater();
         if (inWater || this.getTarget() != null || this.level().isThundering()) {
            this.wakeUp();
         }

         if (inWater || this.isSleeping()) {
            this.setSitting(false);
         }

         if (this.isFaceplanted() && this.level().getRandom().nextFloat() < 0.2F) {
            BlockPos pos = this.blockPosition();
            BlockState state = this.level().getBlockState(pos);
            this.level().levelEvent(2001, pos, Block.getId(state));
         }
      }

      this.interestedAngleO = this.interestedAngle;
      if (this.isInterested()) {
         this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
      } else {
         this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
      }

      this.crouchAmountO = this.crouchAmount;
      if (this.isCrouching()) {
         this.crouchAmount += 0.2F;
         if (this.crouchAmount > 5.0F) {
            this.crouchAmount = 5.0F;
         }
      } else {
         this.crouchAmount = 0.0F;
      }

   }

   public boolean isFood(final ItemStack itemStack) {
      return itemStack.is(ItemTags.FOX_FOOD);
   }

   protected void onOffspringSpawnedFromEgg(final Player spawner, final Mob offspring) {
      ((Fox)offspring).addTrustedEntity(spawner);
   }

   public boolean isPouncing() {
      return this.getFlag(16);
   }

   public void setIsPouncing(final boolean pouncing) {
      this.setFlag(16, pouncing);
   }

   public boolean isFullyCrouched() {
      return this.crouchAmount == 5.0F;
   }

   public void setIsCrouching(final boolean isCrouching) {
      this.setFlag(4, isCrouching);
   }

   public boolean isCrouching() {
      return this.getFlag(4);
   }

   public void setIsInterested(final boolean value) {
      this.setFlag(8, value);
   }

   public boolean isInterested() {
      return this.getFlag(8);
   }

   public float getHeadRollAngle(final float a) {
      return Mth.lerp(a, this.interestedAngleO, this.interestedAngle) * 0.11F * 3.1415927F;
   }

   public float getCrouchAmount(final float a) {
      return Mth.lerp(a, this.crouchAmountO, this.crouchAmount);
   }

   public void setTarget(final @Nullable LivingEntity target) {
      if (this.isDefending() && target == null) {
         this.setDefending(false);
      }

      super.setTarget(target);
   }

   private void wakeUp() {
      this.setSleeping(false);
   }

   private void clearStates() {
      this.setIsInterested(false);
      this.setIsCrouching(false);
      this.setSitting(false);
      this.setSleeping(false);
      this.setDefending(false);
      this.setFaceplanted(false);
   }

   public boolean canMove() {
      return !this.isSleeping() && !this.isSitting() && !this.isFaceplanted();
   }

   public void playAmbientSound() {
      SoundEvent ambient = this.getAmbientSound();
      if (ambient == SoundEvents.FOX_SCREECH) {
         this.playSound(ambient, 2.0F, this.getVoicePitch());
      } else {
         super.playAmbientSound();
      }

   }

   protected @Nullable SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return SoundEvents.FOX_SLEEP;
      } else {
         if (!this.level().isBrightOutside() && this.random.nextFloat() < 0.1F) {
            List<Player> nearbyEntities = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(16.0, 16.0, 16.0), EntitySelector.NO_SPECTATORS);
            if (nearbyEntities.isEmpty()) {
               return SoundEvents.FOX_SCREECH;
            }
         }

         return SoundEvents.FOX_AMBIENT;
      }
   }

   protected @Nullable SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.FOX_HURT;
   }

   protected @Nullable SoundEvent getDeathSound() {
      return SoundEvents.FOX_DEATH;
   }

   private boolean trusts(final LivingEntity entity) {
      return this.getTrustedEntities().anyMatch((trusted) -> trusted.matches(entity));
   }

   protected void dropAllDeathLoot(final ServerLevel level, final DamageSource source) {
      ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
      if (!itemStack.isEmpty()) {
         this.spawnAtLocation(level, itemStack);
         this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
      }

      super.dropAllDeathLoot(level, source);
   }

   public static boolean isPathClear(final Fox fox, final LivingEntity target) {
      double zdiff = target.getZ() - fox.getZ();
      double xdiff = target.getX() - fox.getX();
      double slope = zdiff / xdiff;
      int increments = 6;

      for(int i = 0; i < 6; ++i) {
         double z = slope == 0.0 ? 0.0 : zdiff * (double)((float)i / 6.0F);
         double x = slope == 0.0 ? xdiff * (double)((float)i / 6.0F) : z / slope;

         for(int j = 1; j < 4; ++j) {
            if (!fox.level().getBlockState(BlockPos.containing(fox.getX() + x, fox.getY() + (double)j, fox.getZ() + z)).canBeReplaced()) {
               return false;
            }
         }
      }

      return true;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.55F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   static {
      DATA_TYPE_ID = SynchedEntityData.<Integer>defineId(Fox.class, EntityDataSerializers.INT);
      DATA_FLAGS_ID = SynchedEntityData.<Byte>defineId(Fox.class, EntityDataSerializers.BYTE);
      DATA_TRUSTED_ID_0 = SynchedEntityData.<Optional<EntityReference<LivingEntity>>>defineId(Fox.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
      DATA_TRUSTED_ID_1 = SynchedEntityData.<Optional<EntityReference<LivingEntity>>>defineId(Fox.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
      ALLOWED_ITEMS = (e) -> !e.hasPickUpDelay() && e.isAlive();
      TRUSTED_TARGET_SELECTOR = (entity) -> {
         if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
         } else {
            return livingEntity.getLastHurtMob() != null && livingEntity.getLastHurtMobTimestamp() < livingEntity.tickCount + 600;
         }
      };
      STALKABLE_PREY = (entity) -> entity instanceof Chicken || entity instanceof Rabbit;
      AVOID_PLAYERS = (entity) -> !entity.isDiscrete() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity);
      BABY_DIMENSIONS = EntityTypes.FOX.getDimensions().scale(0.6F).withEyeHeight(0.34375F).withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, 0.375F, 0.0F));
      TRUSTED_LIST_CODEC = EntityReference.codec().listOf();
   }

   public static enum Variant implements StringRepresentable {
      RED(0, "red"),
      SNOW(1, "snow");

      public static final Variant DEFAULT = RED;
      public static final Codec<Variant> CODEC = StringRepresentable.<Variant>fromEnum(Variant::values);
      private static final IntFunction<Variant> BY_ID = ByIdMap.<Variant>continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Variant> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Variant::getId);
      private final int id;
      private final String name;

      private Variant(final int id, final String name) {
         this.id = id;
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      public int getId() {
         return this.id;
      }

      public static Variant byId(final int id) {
         return (Variant)BY_ID.apply(id);
      }

      public static Variant byBiome(final Holder<Biome> biome) {
         return biome.is(BiomeTags.SPAWNS_SNOW_FOXES) ? SNOW : RED;
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{RED, SNOW};
      }
   }

   private class FoxSearchForItemsGoal extends Goal {
      public FoxSearchForItemsGoal() {
         Objects.requireNonNull(Fox.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (!Fox.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            return false;
         } else if (Fox.this.getTarget() == null && Fox.this.getLastHurtByMob() == null) {
            if (!Fox.this.canMove()) {
               return false;
            } else if (Fox.this.getRandom().nextInt(reducedTickDelay(10)) != 0) {
               return false;
            } else {
               List<ItemEntity> items = Fox.this.level().getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Fox.ALLOWED_ITEMS);
               return !items.isEmpty() && Fox.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
            }
         } else {
            return false;
         }
      }

      public void tick() {
         List<ItemEntity> items = Fox.this.level().getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Fox.ALLOWED_ITEMS);
         ItemStack itemStack = Fox.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (itemStack.isEmpty() && !items.isEmpty()) {
            Fox.this.getNavigation().moveTo((Entity)items.get(0), 1.2000000476837158);
         }

      }

      public void start() {
         List<ItemEntity> items = Fox.this.level().getEntitiesOfClass(ItemEntity.class, Fox.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Fox.ALLOWED_ITEMS);
         if (!items.isEmpty()) {
            Fox.this.getNavigation().moveTo((Entity)items.get(0), 1.2000000476837158);
         }

      }
   }

   private static class FoxMoveControl<T extends Fox> extends MoveControl<T> {
      public FoxMoveControl(final T fox) {
         super(fox);
      }

      public void tick() {
         if (((Fox)this.mob).canMove()) {
            super.tick();
         }

      }
   }

   private class StalkPreyGoal extends Goal {
      public StalkPreyGoal() {
         Objects.requireNonNull(Fox.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         if (Fox.this.isSleeping()) {
            return false;
         } else {
            LivingEntity target = Fox.this.getTarget();
            return target != null && target.isAlive() && Fox.STALKABLE_PREY.test(target) && Fox.this.distanceToSqr(target) > 36.0 && !Fox.this.isCrouching() && !Fox.this.isInterested() && !Fox.this.jumping;
         }
      }

      public void start() {
         Fox.this.setSitting(false);
         Fox.this.setFaceplanted(false);
      }

      public void stop() {
         LivingEntity target = Fox.this.getTarget();
         if (target != null && Fox.isPathClear(Fox.this, target)) {
            Fox.this.setIsInterested(true);
            Fox.this.setIsCrouching(true);
            Fox.this.getNavigation().stop();
            Fox.this.getLookControl().setLookAt(target, (float)Fox.this.getMaxHeadYRot(), (float)Fox.this.getMaxHeadXRot());
         } else {
            Fox.this.setIsInterested(false);
            Fox.this.setIsCrouching(false);
         }

      }

      public void tick() {
         LivingEntity target = Fox.this.getTarget();
         if (target != null) {
            Fox.this.getLookControl().setLookAt(target, (float)Fox.this.getMaxHeadYRot(), (float)Fox.this.getMaxHeadXRot());
            if (Fox.this.distanceToSqr(target) <= 36.0) {
               Fox.this.setIsInterested(true);
               Fox.this.setIsCrouching(true);
               Fox.this.getNavigation().stop();
            } else {
               Fox.this.getNavigation().moveTo((Entity)target, 1.5);
            }

         }
      }
   }

   private class FoxMeleeAttackGoal extends MeleeAttackGoal {
      public FoxMeleeAttackGoal(final double speedModifier, final boolean trackTarget) {
         Objects.requireNonNull(Fox.this);
         super(Fox.this, speedModifier, trackTarget);
      }

      protected void checkAndPerformAttack(final LivingEntity target) {
         if (this.canPerformAttack(target)) {
            this.resetAttackCooldown();
            this.mob.doHurtTarget(getServerLevel(this.mob), target);
            Fox.this.playSound(SoundEvents.FOX_BITE, 1.0F, 1.0F);
         }

      }

      public void start() {
         Fox.this.setIsInterested(false);
         super.start();
      }

      public boolean canUse() {
         return !Fox.this.isSitting() && !Fox.this.isSleeping() && !Fox.this.isCrouching() && !Fox.this.isFaceplanted() && super.canUse();
      }
   }

   private class FoxBreedGoal extends BreedGoal {
      public FoxBreedGoal(final double speedModifier) {
         Objects.requireNonNull(Fox.this);
         super(Fox.this, speedModifier);
      }

      public void start() {
         ((Fox)this.animal).clearStates();
         ((Fox)this.partner).clearStates();
         super.start();
      }

      protected void breed() {
         Fox offspring = (Fox)this.animal.getBreedOffspring(this.level, this.partner);
         if (offspring != null) {
            ServerPlayer animalLoveCause = this.animal.getLoveCause();
            ServerPlayer partnerLoveCause = this.partner.getLoveCause();
            ServerPlayer loveCause = animalLoveCause;
            if (animalLoveCause != null) {
               offspring.addTrustedEntity(animalLoveCause);
            } else {
               loveCause = partnerLoveCause;
            }

            if (partnerLoveCause != null && animalLoveCause != partnerLoveCause) {
               offspring.addTrustedEntity(partnerLoveCause);
            }

            if (loveCause != null) {
               loveCause.awardStat(Stats.ANIMALS_BRED);
               CriteriaTriggers.BRED_ANIMALS.trigger(loveCause, this.animal, this.partner, offspring);
            }

            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            offspring.setAge(-24000);
            offspring.snapTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
            this.level.addFreshEntityWithPassengers(offspring);
            this.level.broadcastEntityEvent(this.animal, (byte)18);
            if ((Boolean)this.level.getGameRules().get(GameRules.MOB_DROPS)) {
               this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
            }

         }
      }
   }

   private class DefendTrustedTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {
      private @Nullable LivingEntity trustedLastHurtBy;
      private @Nullable LivingEntity trustedLastHurt;
      private int timestamp;

      public DefendTrustedTargetGoal(final Class<LivingEntity> targetType, final boolean mustSee, final @Nullable boolean mustReach, final TargetingConditions.Selector subselector) {
         Objects.requireNonNull(Fox.this);
         super(Fox.this, targetType, 10, mustSee, mustReach, subselector);
      }

      public boolean canUse() {
         if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
         } else {
            ServerLevel level = getServerLevel(Fox.this.level());

            for(EntityReference<LivingEntity> trustedReference : Fox.this.getTrustedEntities().toList()) {
               LivingEntity trustedEntity = (LivingEntity)trustedReference.getEntity(level, LivingEntity.class);
               if (trustedEntity != null) {
                  this.trustedLastHurt = trustedEntity;
                  this.trustedLastHurtBy = trustedEntity.getLastHurtByMob();
                  int timestamp = trustedEntity.getLastHurtByMobTimestamp();
                  return timestamp != this.timestamp && this.canAttack(this.trustedLastHurtBy, this.targetConditions);
               }
            }

            return false;
         }
      }

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

   private class SeekShelterGoal extends FleeSunGoal {
      private int interval;

      public SeekShelterGoal(final double speedModifier) {
         Objects.requireNonNull(Fox.this);
         super(Fox.this, speedModifier);
         this.interval = reducedTickDelay(100);
      }

      public boolean canUse() {
         if (!Fox.this.isSleeping() && this.mob.getTarget() == null) {
            if (Fox.this.level().isThundering() && Fox.this.level().canSeeSky(this.mob.blockPosition())) {
               return this.setWantedPos();
            } else if (this.interval > 0) {
               --this.interval;
               return false;
            } else {
               this.interval = 100;
               BlockPos pos = this.mob.blockPosition();
               return Fox.this.level().isBrightOutside() && Fox.this.level().canSeeSky(pos) && !((ServerLevel)Fox.this.level()).isVillage(pos) && this.setWantedPos();
            }
         } else {
            return false;
         }
      }

      public void start() {
         Fox.this.clearStates();
         super.start();
      }
   }

   public class FoxAlertableEntitiesSelector implements TargetingConditions.Selector {
      public FoxAlertableEntitiesSelector() {
         Objects.requireNonNull(Fox.this);
         super();
      }

      public boolean test(final LivingEntity target, final ServerLevel level) {
         if (target instanceof Fox) {
            return false;
         } else if (!(target instanceof Chicken) && !(target instanceof Rabbit) && !(target instanceof Monster)) {
            if (target instanceof TamableAnimal) {
               TamableAnimal tamableAnimal = (TamableAnimal)target;
               return !tamableAnimal.isTame();
            } else {
               if (target instanceof Player) {
                  Player player = (Player)target;
                  if (player.isSpectator() || player.isCreative()) {
                     return false;
                  }
               }

               if (Fox.this.trusts(target)) {
                  return false;
               } else {
                  return !target.isSleeping() && !target.isDiscrete();
               }
            }
         } else {
            return true;
         }
      }
   }

   private abstract class FoxBehaviorGoal extends Goal {
      private final TargetingConditions alertableTargeting;

      private FoxBehaviorGoal() {
         Objects.requireNonNull(Fox.this);
         super();
         this.alertableTargeting = TargetingConditions.forCombat().range(12.0).ignoreLineOfSight().selector(Fox.this.new FoxAlertableEntitiesSelector());
      }

      protected boolean hasShelter() {
         BlockPos foxPos = BlockPos.containing(Fox.this.getX(), Fox.this.getBoundingBox().maxY, Fox.this.getZ());
         return !Fox.this.level().canSeeSky(foxPos) && Fox.this.getWalkTargetValue(foxPos) >= 0.0F;
      }

      protected boolean alertable() {
         return !getServerLevel(Fox.this.level()).getNearbyEntities(LivingEntity.class, this.alertableTargeting, Fox.this, Fox.this.getBoundingBox().inflate(12.0, 6.0, 12.0)).isEmpty();
      }
   }

   private class SleepGoal extends FoxBehaviorGoal {
      private static final int WAIT_TIME_BEFORE_SLEEP = reducedTickDelay(140);
      private int countdown;

      public SleepGoal() {
         Objects.requireNonNull(Fox.this);
         super();
         this.countdown = Fox.this.random.nextInt(WAIT_TIME_BEFORE_SLEEP);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
      }

      public boolean canUse() {
         if (Fox.this.xxa == 0.0F && Fox.this.yya == 0.0F && Fox.this.zza == 0.0F) {
            return this.canSleep() || Fox.this.isSleeping();
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return this.canSleep();
      }

      private boolean canSleep() {
         if (this.countdown > 0) {
            --this.countdown;
            return false;
         } else {
            return Fox.this.level().isBrightOutside() && this.hasShelter() && !this.alertable() && !Fox.this.isInPowderSnow;
         }
      }

      public void stop() {
         this.countdown = Fox.this.random.nextInt(WAIT_TIME_BEFORE_SLEEP);
         Fox.this.clearStates();
      }

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

   private class PerchAndSearchGoal extends FoxBehaviorGoal {
      private double relX;
      private double relZ;
      private int lookTime;
      private int looksRemaining;

      public PerchAndSearchGoal() {
         Objects.requireNonNull(Fox.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return Fox.this.getLastHurtByMob() == null && Fox.this.getRandom().nextFloat() < 0.02F && !Fox.this.isSleeping() && Fox.this.getTarget() == null && Fox.this.getNavigation().isDone() && !this.alertable() && !Fox.this.isPouncing() && !Fox.this.isCrouching();
      }

      public boolean canContinueToUse() {
         return this.looksRemaining > 0;
      }

      public void start() {
         this.resetLook();
         this.looksRemaining = 2 + Fox.this.getRandom().nextInt(3);
         Fox.this.setSitting(true);
         Fox.this.getNavigation().stop();
      }

      public void stop() {
         Fox.this.setSitting(false);
      }

      public void tick() {
         --this.lookTime;
         if (this.lookTime <= 0) {
            --this.looksRemaining;
            this.resetLook();
         }

         Fox.this.getLookControl().setLookAt(Fox.this.getX() + this.relX, Fox.this.getEyeY(), Fox.this.getZ() + this.relZ, (float)Fox.this.getMaxHeadYRot(), (float)Fox.this.getMaxHeadXRot());
      }

      private void resetLook() {
         double rnd = 6.283185307179586 * Fox.this.getRandom().nextDouble();
         this.relX = Math.cos(rnd);
         this.relZ = Math.sin(rnd);
         this.lookTime = this.adjustedTickDelay(80 + Fox.this.getRandom().nextInt(20));
      }
   }

   public class FoxEatBerriesGoal extends MoveToBlockGoal {
      private static final int WAIT_TICKS = 40;
      protected int ticksWaited;

      public FoxEatBerriesGoal(final double speedModifier, final int searchRange, final int verticalSearchRange) {
         Objects.requireNonNull(Fox.this);
         super(Fox.this, speedModifier, searchRange, verticalSearchRange);
      }

      public double acceptedDistance() {
         return 2.0;
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 100 == 0;
      }

      protected boolean isValidTarget(final LevelReader level, final BlockPos pos) {
         BlockState blockState = level.getBlockState(pos);
         return blockState.is(Blocks.SWEET_BERRY_BUSH) && (Integer)blockState.getValue(SweetBerryBushBlock.AGE) >= 2 || CaveVines.hasGlowBerries(blockState);
      }

      public void tick() {
         if (this.isReachedTarget()) {
            if (this.ticksWaited >= 40) {
               this.onReachedTarget();
            } else {
               ++this.ticksWaited;
            }
         } else if (!this.isReachedTarget() && Fox.this.random.nextFloat() < 0.05F) {
            Fox.this.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
         }

         super.tick();
      }

      protected void onReachedTarget() {
         if ((Boolean)getServerLevel(Fox.this.level()).getGameRules().get(GameRules.MOB_GRIEFING)) {
            BlockState state = Fox.this.level().getBlockState(this.blockPos);
            if (state.is(Blocks.SWEET_BERRY_BUSH)) {
               this.pickSweetBerries(state);
            } else if (CaveVines.hasGlowBerries(state)) {
               this.pickGlowBerry(state);
            }

         }
      }

      private void pickGlowBerry(final BlockState state) {
         CaveVines.use(Fox.this, state, Fox.this.level(), this.blockPos);
      }

      private void pickSweetBerries(final BlockState state) {
         int age = (Integer)state.getValue(SweetBerryBushBlock.AGE);
         state.setValue(SweetBerryBushBlock.AGE, 1);
         int count = 1 + Fox.this.level().getRandom().nextInt(2) + (age == 3 ? 1 : 0);
         ItemStack heldItem = Fox.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (heldItem.isEmpty()) {
            Fox.this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.SWEET_BERRIES));
            --count;
         }

         if (count > 0) {
            Block.popResource(Fox.this.level(), this.blockPos, new ItemStack(Items.SWEET_BERRIES, count));
         }

         Fox.this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1.0F, 1.0F);
         Fox.this.level().setBlock(this.blockPos, (BlockState)state.setValue(SweetBerryBushBlock.AGE, 1), 2);
         Fox.this.level().gameEvent(GameEvent.BLOCK_CHANGE, this.blockPos, GameEvent.Context.of((Entity)Fox.this));
      }

      public boolean canUse() {
         return !Fox.this.isSleeping() && super.canUse();
      }

      public void start() {
         this.ticksWaited = 0;
         Fox.this.setSitting(false);
         super.start();
      }
   }

   public static class FoxGroupData extends AgeableMob.AgeableMobGroupData {
      public final Variant variant;

      public FoxGroupData(final Variant variant) {
         super(false);
         this.variant = variant;
      }
   }

   private class FaceplantGoal extends Goal {
      int countdown;

      public FaceplantGoal() {
         Objects.requireNonNull(Fox.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return Fox.this.isFaceplanted();
      }

      public boolean canContinueToUse() {
         return this.canUse() && this.countdown > 0;
      }

      public void start() {
         this.countdown = this.adjustedTickDelay(40);
      }

      public void stop() {
         Fox.this.setFaceplanted(false);
      }

      public void tick() {
         --this.countdown;
      }
   }

   private class FoxPanicGoal extends PanicGoal {
      public FoxPanicGoal(final double speedModifier) {
         Objects.requireNonNull(Fox.this);
         super(Fox.this, speedModifier);
      }

      public boolean shouldPanic() {
         return !Fox.this.isDefending() && super.shouldPanic();
      }
   }

   private class FoxStrollThroughVillageGoal extends StrollThroughVillageGoal {
      public FoxStrollThroughVillageGoal(final int searchRadius, final int interval) {
         Objects.requireNonNull(Fox.this);
         super(Fox.this, interval);
      }

      public void start() {
         Fox.this.clearStates();
         super.start();
      }

      public boolean canUse() {
         return super.canUse() && this.canFoxMove();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.canFoxMove();
      }

      private boolean canFoxMove() {
         return !Fox.this.isSleeping() && !Fox.this.isSitting() && !Fox.this.isDefending() && Fox.this.getTarget() == null;
      }
   }

   private class FoxFloatGoal extends FloatGoal {
      public FoxFloatGoal() {
         Objects.requireNonNull(Fox.this);
         super(Fox.this);
      }

      public void start() {
         super.start();
         Fox.this.clearStates();
      }

      public boolean canUse() {
         return Fox.this.isInWater() && Fox.this.getFluidHeight(FluidTags.WATER) > 0.25 || Fox.this.isInLava();
      }
   }

   public class FoxPounceGoal extends JumpGoal {
      public FoxPounceGoal() {
         Objects.requireNonNull(Fox.this);
         super();
      }

      public boolean canUse() {
         if (!Fox.this.isFullyCrouched()) {
            return false;
         } else {
            LivingEntity target = Fox.this.getTarget();
            if (target != null && target.isAlive()) {
               if (target.getMotionDirection() != target.getDirection()) {
                  return false;
               } else {
                  boolean hasClearPath = Fox.isPathClear(Fox.this, target);
                  if (!hasClearPath) {
                     Fox.this.getNavigation().createPath(target, 0);
                     Fox.this.setIsCrouching(false);
                     Fox.this.setIsInterested(false);
                  }

                  return hasClearPath;
               }
            } else {
               return false;
            }
         }
      }

      public boolean canContinueToUse() {
         LivingEntity target = Fox.this.getTarget();
         if (target != null && target.isAlive()) {
            double yd = Fox.this.getDeltaMovement().y;
            return (!(yd * yd < 0.05000000074505806) || !(Math.abs(Fox.this.getXRot()) < 15.0F) || !Fox.this.onGround()) && !Fox.this.isFaceplanted();
         } else {
            return false;
         }
      }

      public boolean isInterruptable() {
         return false;
      }

      public void start() {
         Fox.this.setJumping(true);
         Fox.this.setIsPouncing(true);
         Fox.this.setIsInterested(false);
         LivingEntity target = Fox.this.getTarget();
         if (target != null) {
            Fox.this.getLookControl().setLookAt(target, 60.0F, 30.0F);
            Vec3 uv = (new Vec3(target.getX() - Fox.this.getX(), target.getY() - Fox.this.getY(), target.getZ() - Fox.this.getZ())).normalize();
            Fox.this.setDeltaMovement(Fox.this.getDeltaMovement().add(uv.x * 0.8, 0.9, uv.z * 0.8));
         }

         Fox.this.getNavigation().stop();
      }

      public void stop() {
         Fox.this.setIsCrouching(false);
         Fox.this.crouchAmount = 0.0F;
         Fox.this.crouchAmountO = 0.0F;
         Fox.this.setIsInterested(false);
         Fox.this.setIsPouncing(false);
      }

      public void tick() {
         LivingEntity target = Fox.this.getTarget();
         if (target != null) {
            Fox.this.getLookControl().setLookAt(target, 60.0F, 30.0F);
         }

         if (!Fox.this.isFaceplanted()) {
            Vec3 movement = Fox.this.getDeltaMovement();
            if (movement.y * movement.y < 0.029999999329447746 && Fox.this.getXRot() != 0.0F) {
               Fox.this.setXRot(Mth.rotLerp(0.2F, Fox.this.getXRot(), 0.0F));
            } else {
               double direction = movement.horizontalDistance();
               float upwardsBias = Fox.this.jumping && movement.y > 0.0 ? 6.5F : 1.0F;
               double biasedY = movement.y * (double)upwardsBias;
               double len = Math.sqrt(direction * direction + biasedY * biasedY);
               if (len > 9.999999747378752E-6) {
                  double rotation = Math.signum(-biasedY) * Math.acos(direction / len) * 57.2957763671875;
                  Fox.this.setXRot((float)rotation);
               }
            }
         }

         if (target != null && Fox.this.distanceTo(target) <= 2.0F) {
            Fox.this.doHurtTarget(getServerLevel(Fox.this.level()), target);
         } else if (Fox.this.getXRot() > 0.0F && Fox.this.onGround() && (float)Fox.this.getDeltaMovement().y != 0.0F && Fox.this.level().getBlockState(Fox.this.blockPosition()).is(Blocks.SNOW)) {
            Fox.this.setXRot(60.0F);
            Fox.this.setTarget((LivingEntity)null);
            Fox.this.setFaceplanted(true);
         }

      }
   }

   public class FoxLookControl extends LookControl {
      public FoxLookControl() {
         Objects.requireNonNull(Fox.this);
         super(Fox.this);
      }

      public void tick() {
         if (!Fox.this.isSleeping()) {
            super.tick();
         }

      }

      protected boolean resetXRotOnTick() {
         return !Fox.this.isPouncing() && !Fox.this.isCrouching() && !Fox.this.isInterested() && !Fox.this.isFaceplanted();
      }
   }

   private static class FoxFollowParentGoal extends FollowParentGoal {
      private final Fox fox;

      public FoxFollowParentGoal(final Fox fox, final double speedModifier) {
         super(fox, speedModifier);
         this.fox = fox;
      }

      public boolean canUse() {
         return !this.fox.isDefending() && super.canUse();
      }

      public boolean canContinueToUse() {
         return !this.fox.isDefending() && super.canContinueToUse();
      }

      public void start() {
         this.fox.clearStates();
         super.start();
      }
   }

   private class FoxLookAtPlayerGoal extends LookAtPlayerGoal {
      public FoxLookAtPlayerGoal(final Mob mob, final Class<? extends LivingEntity> lookAtType, final float lookDistance) {
         Objects.requireNonNull(Fox.this);
         super(mob, lookAtType, lookDistance);
      }

      public boolean canUse() {
         return super.canUse() && !Fox.this.isFaceplanted() && !Fox.this.isInterested();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && !Fox.this.isFaceplanted() && !Fox.this.isInterested();
      }
   }
}
