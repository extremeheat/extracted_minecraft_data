package net.minecraft.world.entity.animal.pig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Pig extends Animal implements ItemSteerable {
   private static final EntityDataAccessor<Integer> DATA_BOOST_TIME;
   private static final EntityDataAccessor<Holder<PigVariant>> DATA_VARIANT_ID;
   private final ItemBasedSteering steering;

   public Pig(final EntityType<? extends Pig> type, final Level level) {
      super(type, level);
      this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, (i) -> i.is(Items.CARROT_ON_A_STICK), false));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, (i) -> i.is(ItemTags.PIG_FOOD), false));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   public @Nullable LivingEntity getControllingPassenger() {
      if (this.isSaddled()) {
         Entity var2 = this.getFirstPassenger();
         if (var2 instanceof Player) {
            Player player = (Player)var2;
            if (player.isHolding(Items.CARROT_ON_A_STICK)) {
               return player;
            }
         }
      }

      return super.getControllingPassenger();
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (DATA_BOOST_TIME.equals(accessor) && this.level().isClientSide()) {
         this.steering.onSynced();
      }

      super.onSyncedDataUpdated(accessor);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_BOOST_TIME, 0);
      entityData.define(DATA_VARIANT_ID, VariantUtils.getDefaultOrAny(this.registryAccess(), PigVariants.DEFAULT));
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      VariantUtils.writeVariant(output, this.getVariant());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      VariantUtils.readVariant(input, Registries.PIG_VARIANT).ifPresent(this::setVariant);
   }

   protected SoundEvent getAmbientSound() {
      return this.isBaby() ? SoundEvents.PIG_AMBIENT_BABY : SoundEvents.PIG_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return this.isBaby() ? SoundEvents.PIG_HURT_BABY : SoundEvents.PIG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.PIG_DEATH_BABY : SoundEvents.PIG_DEATH;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      this.playSound(this.isBaby() ? SoundEvents.PIG_STEP_BABY : SoundEvents.PIG_STEP, 0.15F, 1.0F);
   }

   public InteractionResult mobInteract(final Player player, final InteractionHand hand) {
      boolean hasFood = this.isFood(player.getItemInHand(hand));
      if (!hasFood && this.isSaddled() && !this.isVehicle() && !player.isSecondaryUseActive()) {
         if (!this.level().isClientSide()) {
            player.startRiding(this);
         }

         return InteractionResult.SUCCESS;
      } else {
         InteractionResult interactionResult = super.mobInteract(player, hand);
         if (!interactionResult.consumesAction()) {
            ItemStack itemStack = player.getItemInHand(hand);
            return (InteractionResult)(this.isEquippableInSlot(itemStack, EquipmentSlot.SADDLE) ? itemStack.interactLivingEntity(player, this, hand) : InteractionResult.PASS);
         } else {
            return interactionResult;
         }
      }
   }

   public boolean canUseSlot(final EquipmentSlot slot) {
      if (slot != EquipmentSlot.SADDLE) {
         return super.canUseSlot(slot);
      } else {
         return this.isAlive() && !this.isBaby();
      }
   }

   protected boolean canDispenserEquipIntoSlot(final EquipmentSlot slot) {
      return slot == EquipmentSlot.SADDLE || super.canDispenserEquipIntoSlot(slot);
   }

   protected Holder<SoundEvent> getEquipSound(final EquipmentSlot slot, final ItemStack stack, final Equippable equippable) {
      return (Holder<SoundEvent>)(slot == EquipmentSlot.SADDLE ? SoundEvents.PIG_SADDLE : super.getEquipSound(slot, stack, equippable));
   }

   public void thunderHit(final ServerLevel level, final LightningBolt lightningBolt) {
      if (level.getDifficulty() != Difficulty.PEACEFUL) {
         ZombifiedPiglin zombifiedPiglin = (ZombifiedPiglin)this.convertTo(EntityType.ZOMBIFIED_PIGLIN, ConversionParams.single(this, false, true), (zp) -> {
            zp.populateDefaultEquipmentSlots(this.getRandom(), level.getCurrentDifficultyAt(this.blockPosition()));
            zp.setPersistenceRequired();
         });
         if (zombifiedPiglin == null) {
            super.thunderHit(level, lightningBolt);
         }
      } else {
         super.thunderHit(level, lightningBolt);
      }

   }

   protected void tickRidden(final Player controller, final Vec3 riddenInput) {
      super.tickRidden(controller, riddenInput);
      this.setRot(controller.getYRot(), controller.getXRot() * 0.5F);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
      this.steering.tickBoost();
   }

   protected Vec3 getRiddenInput(final Player controller, final Vec3 selfInput) {
      return new Vec3(0.0, 0.0, 1.0);
   }

   protected float getRiddenSpeed(final Player controller) {
      return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225 * (double)this.steering.boostFactor());
   }

   public boolean boost() {
      return this.steering.boost(this.getRandom());
   }

   public @Nullable Pig getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      Pig baby = EntityType.PIG.create(level, EntitySpawnReason.BREEDING);
      if (baby != null && partner instanceof Pig partnerPig) {
         baby.setVariant(this.random.nextBoolean() ? this.getVariant() : partnerPig.getVariant());
      }

      return baby;
   }

   public boolean isFood(final ItemStack itemStack) {
      return itemStack.is(ItemTags.PIG_FOOD);
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   private void setVariant(final Holder<PigVariant> variant) {
      this.entityData.set(DATA_VARIANT_ID, variant);
   }

   public Holder<PigVariant> getVariant() {
      return (Holder)this.entityData.get(DATA_VARIANT_ID);
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.PIG_VARIANT ? castComponentValue(type, this.getVariant()) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.PIG_VARIANT);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.PIG_VARIANT) {
         this.setVariant((Holder)castComponentValue(DataComponents.PIG_VARIANT, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   public SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      VariantUtils.selectVariantToSpawn(SpawnContext.create(level, this.blockPosition()), Registries.PIG_VARIANT).ifPresent(this::setVariant);
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   static {
      DATA_BOOST_TIME = SynchedEntityData.<Integer>defineId(Pig.class, EntityDataSerializers.INT);
      DATA_VARIANT_ID = SynchedEntityData.<Holder<PigVariant>>defineId(Pig.class, EntityDataSerializers.PIG_VARIANT);
   }
}
