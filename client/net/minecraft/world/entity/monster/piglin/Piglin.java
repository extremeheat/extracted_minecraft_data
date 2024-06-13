package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Piglin extends AbstractPiglin implements CrossbowAttackMob, InventoryCarrier {
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(Piglin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Piglin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_IS_DANCING = SynchedEntityData.defineId(Piglin.class, EntityDataSerializers.BOOLEAN);
   private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("766bfa64-11f3-11ea-8d71-362b9e155667");
   private static final AttributeModifier SPEED_MODIFIER_BABY = new AttributeModifier(
      SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.20000000298023224, AttributeModifier.Operation.ADD_MULTIPLIED_BASE
   );
   private static final int MAX_HEALTH = 16;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.35F;
   private static final int ATTACK_DAMAGE = 5;
   private static final float CHANCE_OF_WEARING_EACH_ARMOUR_ITEM = 0.1F;
   private static final int MAX_PASSENGERS_ON_ONE_HOGLIN = 3;
   private static final float PROBABILITY_OF_SPAWNING_AS_BABY = 0.2F;
   private static final EntityDimensions BABY_DIMENSIONS = EntityType.PIGLIN.getDimensions().scale(0.5F).withEyeHeight(0.97F);
   private static final double PROBABILITY_OF_SPAWNING_WITH_CROSSBOW_INSTEAD_OF_SWORD = 0.5;
   private final SimpleContainer inventory = new SimpleContainer(8);
   private boolean cannotHunt;
   protected static final ImmutableList<SensorType<? extends Sensor<? super Piglin>>> SENSOR_TYPES = ImmutableList.of(
      SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.NEAREST_ITEMS, SensorType.HURT_BY, SensorType.PIGLIN_SPECIFIC_SENSOR
   );
   protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
      MemoryModuleType.LOOK_TARGET,
      MemoryModuleType.DOORS_TO_CLOSE,
      MemoryModuleType.NEAREST_LIVING_ENTITIES,
      MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
      MemoryModuleType.NEAREST_VISIBLE_PLAYER,
      MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
      MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
      MemoryModuleType.NEARBY_ADULT_PIGLINS,
      MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
      MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
      MemoryModuleType.HURT_BY,
      MemoryModuleType.HURT_BY_ENTITY,
      new MemoryModuleType[]{
         MemoryModuleType.WALK_TARGET,
         MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
         MemoryModuleType.ATTACK_TARGET,
         MemoryModuleType.ATTACK_COOLING_DOWN,
         MemoryModuleType.INTERACTION_TARGET,
         MemoryModuleType.PATH,
         MemoryModuleType.ANGRY_AT,
         MemoryModuleType.UNIVERSAL_ANGER,
         MemoryModuleType.AVOID_TARGET,
         MemoryModuleType.ADMIRING_ITEM,
         MemoryModuleType.TIME_TRYING_TO_REACH_ADMIRE_ITEM,
         MemoryModuleType.ADMIRING_DISABLED,
         MemoryModuleType.DISABLE_WALK_TO_ADMIRE_ITEM,
         MemoryModuleType.CELEBRATE_LOCATION,
         MemoryModuleType.DANCING,
         MemoryModuleType.HUNTED_RECENTLY,
         MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
         MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
         MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED,
         MemoryModuleType.RIDE_TARGET,
         MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
         MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
         MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN,
         MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
         MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM,
         MemoryModuleType.ATE_RECENTLY,
         MemoryModuleType.NEAREST_REPELLENT
      }
   );

   public Piglin(EntityType<? extends AbstractPiglin> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.isBaby()) {
         var1.putBoolean("IsBaby", true);
      }

      if (this.cannotHunt) {
         var1.putBoolean("CannotHunt", true);
      }

      this.writeInventoryToTag(var1, this.registryAccess());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setBaby(var1.getBoolean("IsBaby"));
      this.setCannotHunt(var1.getBoolean("CannotHunt"));
      this.readInventoryFromTag(var1, this.registryAccess());
   }

   @VisibleForDebug
   @Override
   public SimpleContainer getInventory() {
      return this.inventory;
   }

   @Override
   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      if (var1.getEntity() instanceof Creeper var5 && var5.canDropMobsSkull()) {
         ItemStack var6 = new ItemStack(Items.PIGLIN_HEAD);
         var5.increaseDroppedSkulls();
         this.spawnAtLocation(var6);
      }

      this.inventory.removeAllItems().forEach(this::spawnAtLocation);
   }

   protected ItemStack addToInventory(ItemStack var1) {
      return this.inventory.addItem(var1);
   }

   protected boolean canAddToInventory(ItemStack var1) {
      return this.inventory.canAddItem(var1);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_BABY_ID, false);
      var1.define(DATA_IS_CHARGING_CROSSBOW, false);
      var1.define(DATA_IS_DANCING, false);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      super.onSyncedDataUpdated(var1);
      if (DATA_BABY_ID.equals(var1)) {
         this.refreshDimensions();
      }
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 16.0)
         .add(Attributes.MOVEMENT_SPEED, 0.3499999940395355)
         .add(Attributes.ATTACK_DAMAGE, 5.0);
   }

   public static boolean checkPiglinSpawnRules(EntityType<Piglin> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return !var1.getBlockState(var3.below()).is(Blocks.NETHER_WART_BLOCK);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      if (var3 != MobSpawnType.STRUCTURE) {
         if (var5.nextFloat() < 0.2F) {
            this.setBaby(true);
         } else if (this.isAdult()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, this.createSpawnWeapon());
         }
      }

      PiglinAi.initMemories(this, var1.getRandom());
      this.populateDefaultEquipmentSlots(var5, var2);
      this.populateDefaultEquipmentEnchantments(var5, var2);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return false;
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return !this.isPersistenceRequired();
   }

   @Override
   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      if (this.isAdult()) {
         this.maybeWearArmor(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET), var1);
         this.maybeWearArmor(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE), var1);
         this.maybeWearArmor(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS), var1);
         this.maybeWearArmor(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS), var1);
      }
   }

   private void maybeWearArmor(EquipmentSlot var1, ItemStack var2, RandomSource var3) {
      if (var3.nextFloat() < 0.1F) {
         this.setItemSlot(var1, var2);
      }
   }

   @Override
   protected Brain.Provider<Piglin> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return PiglinAi.makeBrain(this, this.brainProvider().makeBrain(var1));
   }

   @Override
   public Brain<Piglin> getBrain() {
      return (Brain<Piglin>)super.getBrain();
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      InteractionResult var3 = super.mobInteract(var1, var2);
      if (var3.consumesAction()) {
         return var3;
      } else if (!this.level().isClientSide) {
         return PiglinAi.mobInteract(this, var1, var2);
      } else {
         boolean var4 = PiglinAi.canAdmire(this, var1.getItemInHand(var2)) && this.getArmPose() != PiglinArmPose.ADMIRING_ITEM;
         return var4 ? InteractionResult.SUCCESS : InteractionResult.PASS;
      }
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(var1);
   }

   @Override
   public void setBaby(boolean var1) {
      this.getEntityData().set(DATA_BABY_ID, var1);
      if (!this.level().isClientSide) {
         AttributeInstance var2 = this.getAttribute(Attributes.MOVEMENT_SPEED);
         var2.removeModifier(SPEED_MODIFIER_BABY.id());
         if (var1) {
            var2.addTransientModifier(SPEED_MODIFIER_BABY);
         }
      }
   }

   @Override
   public boolean isBaby() {
      return this.getEntityData().get(DATA_BABY_ID);
   }

   private void setCannotHunt(boolean var1) {
      this.cannotHunt = var1;
   }

   @Override
   protected boolean canHunt() {
      return !this.cannotHunt;
   }

   @Override
   protected void customServerAiStep() {
      this.level().getProfiler().push("piglinBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      PiglinAi.updateActivity(this);
      super.customServerAiStep();
   }

   @Override
   public int getExperienceReward() {
      return this.xpReward;
   }

   @Override
   protected void finishConversion(ServerLevel var1) {
      PiglinAi.cancelAdmiring(this);
      this.inventory.removeAllItems().forEach(this::spawnAtLocation);
      super.finishConversion(var1);
   }

   private ItemStack createSpawnWeapon() {
      return (double)this.random.nextFloat() < 0.5 ? new ItemStack(Items.CROSSBOW) : new ItemStack(Items.GOLDEN_SWORD);
   }

   private boolean isChargingCrossbow() {
      return this.entityData.get(DATA_IS_CHARGING_CROSSBOW);
   }

   @Override
   public void setChargingCrossbow(boolean var1) {
      this.entityData.set(DATA_IS_CHARGING_CROSSBOW, var1);
   }

   @Override
   public void onCrossbowAttackPerformed() {
      this.noActionTime = 0;
   }

   @Override
   public PiglinArmPose getArmPose() {
      if (this.isDancing()) {
         return PiglinArmPose.DANCING;
      } else if (PiglinAi.isLovedItem(this.getOffhandItem())) {
         return PiglinArmPose.ADMIRING_ITEM;
      } else if (this.isAggressive() && this.isHoldingMeleeWeapon()) {
         return PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON;
      } else if (this.isChargingCrossbow()) {
         return PiglinArmPose.CROSSBOW_CHARGE;
      } else {
         return this.isAggressive() && this.isHolding(Items.CROSSBOW) ? PiglinArmPose.CROSSBOW_HOLD : PiglinArmPose.DEFAULT;
      }
   }

   public boolean isDancing() {
      return this.entityData.get(DATA_IS_DANCING);
   }

   public void setDancing(boolean var1) {
      this.entityData.set(DATA_IS_DANCING, var1);
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      boolean var3 = super.hurt(var1, var2);
      if (this.level().isClientSide) {
         return false;
      } else {
         if (var3 && var1.getEntity() instanceof LivingEntity) {
            PiglinAi.wasHurtBy(this, (LivingEntity)var1.getEntity());
         }

         return var3;
      }
   }

   @Override
   public void performRangedAttack(LivingEntity var1, float var2) {
      this.performCrossbowAttack(this, 1.6F);
   }

   @Override
   public boolean canFireProjectileWeapon(ProjectileWeaponItem var1) {
      return var1 == Items.CROSSBOW;
   }

   protected void holdInMainHand(ItemStack var1) {
      this.setItemSlotAndDropWhenKilled(EquipmentSlot.MAINHAND, var1);
   }

   protected void holdInOffHand(ItemStack var1) {
      if (var1.is(PiglinAi.BARTERING_ITEM)) {
         this.setItemSlot(EquipmentSlot.OFFHAND, var1);
         this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
      } else {
         this.setItemSlotAndDropWhenKilled(EquipmentSlot.OFFHAND, var1);
      }
   }

   @Override
   public boolean wantsToPickUp(ItemStack var1) {
      return this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) && this.canPickUpLoot() && PiglinAi.wantsToPickup(this, var1);
   }

   protected boolean canReplaceCurrentItem(ItemStack var1) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(var1);
      ItemStack var3 = this.getItemBySlot(var2);
      return this.canReplaceCurrentItem(var1, var3);
   }

   @Override
   protected boolean canReplaceCurrentItem(ItemStack var1, ItemStack var2) {
      if (EnchantmentHelper.hasBindingCurse(var2)) {
         return false;
      } else {
         boolean var3 = PiglinAi.isLovedItem(var1) || var1.is(Items.CROSSBOW);
         boolean var4 = PiglinAi.isLovedItem(var2) || var2.is(Items.CROSSBOW);
         if (var3 && !var4) {
            return true;
         } else if (!var3 && var4) {
            return false;
         } else {
            return this.isAdult() && !var1.is(Items.CROSSBOW) && var2.is(Items.CROSSBOW) ? false : super.canReplaceCurrentItem(var1, var2);
         }
      }
   }

   @Override
   protected void pickUpItem(ItemEntity var1) {
      this.onItemPickup(var1);
      PiglinAi.pickUpItem(this, var1);
   }

   @Override
   public boolean startRiding(Entity var1, boolean var2) {
      if (this.isBaby() && var1.getType() == EntityType.HOGLIN) {
         var1 = this.getTopPassenger(var1, 3);
      }

      return super.startRiding(var1, var2);
   }

   private Entity getTopPassenger(Entity var1, int var2) {
      List var3 = var1.getPassengers();
      return var2 != 1 && !var3.isEmpty() ? this.getTopPassenger((Entity)var3.get(0), var2 - 1) : var1;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.level().isClientSide ? null : PiglinAi.getSoundForCurrentActivity(this).orElse(null);
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PIGLIN_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.PIGLIN_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.PIGLIN_STEP, 0.15F, 1.0F);
   }

   @Override
   protected void playConvertedSound() {
      this.makeSound(SoundEvents.PIGLIN_CONVERTED_TO_ZOMBIFIED);
   }
}
