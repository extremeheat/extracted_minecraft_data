package net.minecraft.world.entity.player;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.NameAndId;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public abstract class Player extends Avatar implements ContainerUser {
   public static final int MAX_HEALTH = 20;
   public static final int SLEEP_DURATION = 100;
   public static final int WAKE_UP_DURATION = 10;
   public static final int ENDER_SLOT_OFFSET = 200;
   public static final int HELD_ITEM_SLOT = 499;
   public static final int CRAFTING_SLOT_OFFSET = 500;
   public static final float DEFAULT_BLOCK_INTERACTION_RANGE = 4.5F;
   public static final float DEFAULT_ENTITY_INTERACTION_RANGE = 3.0F;
   private static final int CURRENT_IMPULSE_CONTEXT_RESET_GRACE_TIME_TICKS = 40;
   private static final EntityDataAccessor<Float> DATA_PLAYER_ABSORPTION_ID;
   private static final EntityDataAccessor<Integer> DATA_SCORE_ID;
   private static final EntityDataAccessor<OptionalInt> DATA_SHOULDER_PARROT_LEFT;
   private static final EntityDataAccessor<OptionalInt> DATA_SHOULDER_PARROT_RIGHT;
   public static final int CLIENT_LOADED_TIMEOUT_TIME = 60;
   private static final short DEFAULT_SLEEP_TIMER = 0;
   private static final float DEFAULT_EXPERIENCE_PROGRESS = 0.0F;
   private static final int DEFAULT_EXPERIENCE_LEVEL = 0;
   private static final int DEFAULT_TOTAL_EXPERIENCE = 0;
   private static final int NO_ENCHANTMENT_SEED = 0;
   private static final int DEFAULT_SELECTED_SLOT = 0;
   private static final int DEFAULT_SCORE = 0;
   private static final boolean DEFAULT_IGNORE_FALL_DAMAGE_FROM_CURRENT_IMPULSE = false;
   private static final int DEFAULT_CURRENT_IMPULSE_CONTEXT_RESET_GRACE_TIME = 0;
   final Inventory inventory;
   protected PlayerEnderChestContainer enderChestInventory = new PlayerEnderChestContainer();
   public final InventoryMenu inventoryMenu;
   public AbstractContainerMenu containerMenu;
   protected FoodData foodData = new FoodData();
   protected int jumpTriggerTime;
   private boolean clientLoaded = false;
   protected int clientLoadedTimeoutTimer = 60;
   public int takeXpDelay;
   private int sleepCounter = 0;
   protected boolean wasUnderwater;
   private final Abilities abilities = new Abilities();
   public int experienceLevel = 0;
   public int totalExperience = 0;
   public float experienceProgress = 0.0F;
   protected int enchantmentSeed = 0;
   protected final float defaultFlySpeed = 0.02F;
   private int lastLevelUpTime;
   private final GameProfile gameProfile;
   private boolean reducedDebugInfo;
   private ItemStack lastItemInMainHand;
   private final ItemCooldowns cooldowns;
   private Optional<GlobalPos> lastDeathLocation;
   @Nullable
   public FishingHook fishing;
   protected float hurtDir;
   @Nullable
   public Vec3 currentImpulseImpactPos;
   @Nullable
   public Entity currentExplosionCause;
   private boolean ignoreFallDamageFromCurrentImpulse;
   private int currentImpulseContextResetGraceTime;

   public Player(Level var1, GameProfile var2) {
      super(EntityType.PLAYER, var1);
      this.lastItemInMainHand = ItemStack.EMPTY;
      this.cooldowns = this.createItemCooldowns();
      this.lastDeathLocation = Optional.empty();
      this.ignoreFallDamageFromCurrentImpulse = false;
      this.currentImpulseContextResetGraceTime = 0;
      this.setUUID(var2.id());
      this.gameProfile = var2;
      this.inventory = new Inventory(this, this.equipment);
      this.inventoryMenu = new InventoryMenu(this.inventory, !var1.isClientSide(), this);
      this.containerMenu = this.inventoryMenu;
   }

   protected EntityEquipment createEquipment() {
      return new PlayerEquipment(this);
   }

   public boolean blockActionRestricted(Level var1, BlockPos var2, GameType var3) {
      if (!var3.isBlockPlacingRestricted()) {
         return false;
      } else if (var3 == GameType.SPECTATOR) {
         return true;
      } else if (this.mayBuild()) {
         return false;
      } else {
         ItemStack var4 = this.getMainHandItem();
         return var4.isEmpty() || !var4.canBreakBlockInAdventureMode(new BlockInWorld(var1, var2, false));
      }
   }

   public static AttributeSupplier.Builder createAttributes() {
      return LivingEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE, 1.0).add(Attributes.MOVEMENT_SPEED, 0.10000000149011612).add(Attributes.ATTACK_SPEED).add(Attributes.LUCK).add(Attributes.BLOCK_INTERACTION_RANGE, 4.5).add(Attributes.ENTITY_INTERACTION_RANGE, 3.0).add(Attributes.BLOCK_BREAK_SPEED).add(Attributes.SUBMERGED_MINING_SPEED).add(Attributes.SNEAKING_SPEED).add(Attributes.MINING_EFFICIENCY).add(Attributes.SWEEPING_DAMAGE_RATIO).add(Attributes.WAYPOINT_TRANSMIT_RANGE, 6.0E7).add(Attributes.WAYPOINT_RECEIVE_RANGE, 6.0E7);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_PLAYER_ABSORPTION_ID, 0.0F);
      var1.define(DATA_SCORE_ID, 0);
      var1.define(DATA_SHOULDER_PARROT_LEFT, OptionalInt.empty());
      var1.define(DATA_SHOULDER_PARROT_RIGHT, OptionalInt.empty());
   }

   public void tick() {
      this.noPhysics = this.isSpectator();
      if (this.isSpectator() || this.isPassenger()) {
         this.setOnGround(false);
      }

      if (this.takeXpDelay > 0) {
         --this.takeXpDelay;
      }

      if (this.isSleeping()) {
         ++this.sleepCounter;
         if (this.sleepCounter > 100) {
            this.sleepCounter = 100;
         }

         if (!this.level().isClientSide() && !((BedRule)this.level().environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, this.position())).canSleep(this.level())) {
            this.stopSleepInBed(false, true);
         }
      } else if (this.sleepCounter > 0) {
         ++this.sleepCounter;
         if (this.sleepCounter >= 110) {
            this.sleepCounter = 0;
         }
      }

      this.updateIsUnderwater();
      super.tick();
      int var1 = 29999999;
      double var2 = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
      double var4 = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
      if (var2 != this.getX() || var4 != this.getZ()) {
         this.setPos(var2, this.getY(), var4);
      }

      ++this.attackStrengthTicker;
      ++this.itemSwapTicker;
      ItemStack var6 = this.getMainHandItem();
      if (!ItemStack.matches(this.lastItemInMainHand, var6)) {
         if (!ItemStack.isSameItem(this.lastItemInMainHand, var6)) {
            this.resetAttackStrengthTicker();
         }

         this.lastItemInMainHand = var6.copy();
      }

      if (!this.isEyeInFluid(FluidTags.WATER) && this.isEquipped(Items.TURTLE_HELMET)) {
         this.turtleHelmetTick();
      }

      this.cooldowns.tick();
      this.updatePlayerPose();
      if (this.currentImpulseContextResetGraceTime > 0) {
         --this.currentImpulseContextResetGraceTime;
      }

   }

   protected float getMaxHeadRotationRelativeToBody() {
      return this.isBlocking() ? 15.0F : super.getMaxHeadRotationRelativeToBody();
   }

   public boolean isSecondaryUseActive() {
      return this.isShiftKeyDown();
   }

   protected boolean wantsToStopRiding() {
      return this.isShiftKeyDown();
   }

   protected boolean isStayingOnGroundSurface() {
      return this.isShiftKeyDown();
   }

   protected boolean updateIsUnderwater() {
      this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
      return this.wasUnderwater;
   }

   public void onAboveBubbleColumn(boolean var1, BlockPos var2) {
      if (!this.getAbilities().flying) {
         super.onAboveBubbleColumn(var1, var2);
      }

   }

   public void onInsideBubbleColumn(boolean var1) {
      if (!this.getAbilities().flying) {
         super.onInsideBubbleColumn(var1);
      }

   }

   private void turtleHelmetTick() {
      this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
   }

   private boolean isEquipped(Item var1) {
      for(EquipmentSlot var3 : EquipmentSlot.VALUES) {
         ItemStack var4 = this.getItemBySlot(var3);
         Equippable var5 = (Equippable)var4.get(DataComponents.EQUIPPABLE);
         if (var4.is(var1) && var5 != null && var5.slot() == var3) {
            return true;
         }
      }

      return false;
   }

   protected ItemCooldowns createItemCooldowns() {
      return new ItemCooldowns();
   }

   protected void updatePlayerPose() {
      if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.SWIMMING)) {
         Pose var1 = this.getDesiredPose();
         Pose var2;
         if (!this.isSpectator() && !this.isPassenger() && !this.canPlayerFitWithinBlocksAndEntitiesWhen(var1)) {
            if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)) {
               var2 = Pose.CROUCHING;
            } else {
               var2 = Pose.SWIMMING;
            }
         } else {
            var2 = var1;
         }

         this.setPose(var2);
      }
   }

   private Pose getDesiredPose() {
      if (this.isSleeping()) {
         return Pose.SLEEPING;
      } else if (this.isSwimming()) {
         return Pose.SWIMMING;
      } else if (this.isFallFlying()) {
         return Pose.FALL_FLYING;
      } else if (this.isAutoSpinAttack()) {
         return Pose.SPIN_ATTACK;
      } else {
         return this.isShiftKeyDown() && !this.abilities.flying ? Pose.CROUCHING : Pose.STANDING;
      }
   }

   protected boolean canPlayerFitWithinBlocksAndEntitiesWhen(Pose var1) {
      return this.level().noCollision(this, this.getDimensions(var1).makeBoundingBox(this.position()).deflate(1.0E-7));
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.PLAYER_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.PLAYER_SPLASH;
   }

   protected SoundEvent getSwimHighSpeedSplashSound() {
      return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
   }

   public int getDimensionChangingDelay() {
      return 10;
   }

   public void playSound(SoundEvent var1, float var2, float var3) {
      this.level().playSound(this, this.getX(), this.getY(), this.getZ(), var1, this.getSoundSource(), var2, var3);
   }

   public void playNotifySound(SoundEvent var1, SoundSource var2, float var3, float var4) {
   }

   public SoundSource getSoundSource() {
      return SoundSource.PLAYERS;
   }

   protected int getFireImmuneTicks() {
      return 20;
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 9) {
         this.completeUsingItem();
      } else if (var1 == 23) {
         this.setReducedDebugInfo(false);
      } else if (var1 == 22) {
         this.setReducedDebugInfo(true);
      } else {
         super.handleEntityEvent(var1);
      }

   }

   protected void closeContainer() {
      this.containerMenu = this.inventoryMenu;
   }

   protected void doCloseContainer() {
   }

   public void rideTick() {
      if (!this.level().isClientSide() && this.wantsToStopRiding() && this.isPassenger()) {
         this.stopRiding();
         this.setShiftKeyDown(false);
      } else {
         super.rideTick();
      }
   }

   public void aiStep() {
      if (this.jumpTriggerTime > 0) {
         --this.jumpTriggerTime;
      }

      this.tickRegeneration();
      this.inventory.tick();
      if (this.abilities.flying && !this.isPassenger()) {
         this.resetFallDistance();
      }

      super.aiStep();
      this.updateSwingTime();
      this.yHeadRot = this.getYRot();
      this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         AABB var1;
         if (this.isPassenger() && !this.getVehicle().isRemoved()) {
            var1 = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0, 0.0, 1.0);
         } else {
            var1 = this.getBoundingBox().inflate(1.0, 0.5, 1.0);
         }

         List var2 = this.level().getEntities(this, var1);
         ArrayList var3 = Lists.newArrayList();

         for(Entity var5 : var2) {
            if (var5.getType() == EntityType.EXPERIENCE_ORB) {
               var3.add(var5);
            } else if (!var5.isRemoved()) {
               this.touch(var5);
            }
         }

         if (!var3.isEmpty()) {
            this.touch((Entity)Util.getRandom(var3, this.random));
         }
      }

      this.handleShoulderEntities();
   }

   protected void tickRegeneration() {
   }

   public void handleShoulderEntities() {
   }

   protected void removeEntitiesOnShoulder() {
   }

   private void touch(Entity var1) {
      var1.playerTouch(this);
   }

   public int getScore() {
      return (Integer)this.entityData.get(DATA_SCORE_ID);
   }

   public void setScore(int var1) {
      this.entityData.set(DATA_SCORE_ID, var1);
   }

   public void increaseScore(int var1) {
      int var2 = this.getScore();
      this.entityData.set(DATA_SCORE_ID, var2 + var1);
   }

   public void startAutoSpinAttack(int var1, float var2, ItemStack var3) {
      this.autoSpinAttackTicks = var1;
      this.autoSpinAttackDmg = var2;
      this.autoSpinAttackItemStack = var3;
      if (!this.level().isClientSide()) {
         this.removeEntitiesOnShoulder();
         this.setLivingEntityFlag(4, true);
      }

   }

   @Nonnull
   public ItemStack getWeaponItem() {
      return this.isAutoSpinAttack() && this.autoSpinAttackItemStack != null ? this.autoSpinAttackItemStack : super.getWeaponItem();
   }

   public void die(DamageSource var1) {
      super.die(var1);
      this.reapplyPosition();
      if (!this.isSpectator()) {
         Level var3 = this.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            this.dropAllDeathLoot(var2, var1);
         }
      }

      if (var1 != null) {
         this.setDeltaMovement((double)(-Mth.cos((this.getHurtDir() + this.getYRot()) * 0.017453292F) * 0.1F), 0.10000000149011612, (double)(-Mth.sin((this.getHurtDir() + this.getYRot()) * 0.017453292F) * 0.1F));
      } else {
         this.setDeltaMovement(0.0, 0.1, 0.0);
      }

      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setSharedFlagOnFire(false);
      this.setLastDeathLocation(Optional.of(GlobalPos.of(this.level().dimension(), this.blockPosition())));
   }

   protected void dropEquipment(ServerLevel var1) {
      super.dropEquipment(var1);
      if (!var1.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
         this.destroyVanishingCursedItems();
         this.inventory.dropAll();
      }

   }

   protected void destroyVanishingCursedItems() {
      for(int var1 = 0; var1 < this.inventory.getContainerSize(); ++var1) {
         ItemStack var2 = this.inventory.getItem(var1);
         if (!var2.isEmpty() && EnchantmentHelper.has(var2, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            this.inventory.removeItemNoUpdate(var1);
         }
      }

   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return var1.type().effects().sound();
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PLAYER_DEATH;
   }

   public void handleCreativeModeItemDrop(ItemStack var1) {
   }

   @Nullable
   public ItemEntity drop(ItemStack var1, boolean var2) {
      return this.drop(var1, false, var2);
   }

   public float getDestroySpeed(BlockState var1) {
      float var2 = this.inventory.getSelectedItem().getDestroySpeed(var1);
      if (var2 > 1.0F) {
         var2 += (float)this.getAttributeValue(Attributes.MINING_EFFICIENCY);
      }

      if (MobEffectUtil.hasDigSpeed(this)) {
         var2 *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
      }

      if (this.hasEffect(MobEffects.MINING_FATIGUE)) {
         float var10000;
         switch (this.getEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
            case 0 -> var10000 = 0.3F;
            case 1 -> var10000 = 0.09F;
            case 2 -> var10000 = 0.0027F;
            default -> var10000 = 8.1E-4F;
         }

         float var3 = var10000;
         var2 *= var3;
      }

      var2 *= (float)this.getAttributeValue(Attributes.BLOCK_BREAK_SPEED);
      if (this.isEyeInFluid(FluidTags.WATER)) {
         var2 *= (float)this.getAttribute(Attributes.SUBMERGED_MINING_SPEED).getValue();
      }

      if (!this.onGround()) {
         var2 /= 5.0F;
      }

      return var2;
   }

   public boolean hasCorrectToolForDrops(BlockState var1) {
      return !var1.requiresCorrectToolForDrops() || this.inventory.getSelectedItem().isCorrectToolForDrops(var1);
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setUUID(this.gameProfile.id());
      this.inventory.load(var1.listOrEmpty("Inventory", ItemStackWithSlot.CODEC));
      this.inventory.setSelectedSlot(var1.getIntOr("SelectedItemSlot", 0));
      this.sleepCounter = var1.getShortOr("SleepTimer", (short)0);
      this.experienceProgress = var1.getFloatOr("XpP", 0.0F);
      this.experienceLevel = var1.getIntOr("XpLevel", 0);
      this.totalExperience = var1.getIntOr("XpTotal", 0);
      this.enchantmentSeed = var1.getIntOr("XpSeed", 0);
      if (this.enchantmentSeed == 0) {
         this.enchantmentSeed = this.random.nextInt();
      }

      this.setScore(var1.getIntOr("Score", 0));
      this.foodData.readAdditionalSaveData(var1);
      Optional var10000 = var1.read("abilities", Abilities.Packed.CODEC);
      Abilities var10001 = this.abilities;
      Objects.requireNonNull(var10001);
      var10000.ifPresent(var10001::apply);
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)this.abilities.getWalkingSpeed());
      this.enderChestInventory.fromSlots(var1.listOrEmpty("EnderItems", ItemStackWithSlot.CODEC));
      this.setLastDeathLocation(var1.read("LastDeathLocation", GlobalPos.CODEC));
      this.currentImpulseImpactPos = (Vec3)var1.read("current_explosion_impact_pos", Vec3.CODEC).orElse((Object)null);
      this.ignoreFallDamageFromCurrentImpulse = var1.getBooleanOr("ignore_fall_damage_from_current_explosion", false);
      this.currentImpulseContextResetGraceTime = var1.getIntOr("current_impulse_context_reset_grace_time", 0);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      NbtUtils.addCurrentDataVersion(var1);
      this.inventory.save(var1.list("Inventory", ItemStackWithSlot.CODEC));
      var1.putInt("SelectedItemSlot", this.inventory.getSelectedSlot());
      var1.putShort("SleepTimer", (short)this.sleepCounter);
      var1.putFloat("XpP", this.experienceProgress);
      var1.putInt("XpLevel", this.experienceLevel);
      var1.putInt("XpTotal", this.totalExperience);
      var1.putInt("XpSeed", this.enchantmentSeed);
      var1.putInt("Score", this.getScore());
      this.foodData.addAdditionalSaveData(var1);
      var1.store("abilities", Abilities.Packed.CODEC, this.abilities.pack());
      this.enderChestInventory.storeAsSlots(var1.list("EnderItems", ItemStackWithSlot.CODEC));
      this.lastDeathLocation.ifPresent((var1x) -> var1.store("LastDeathLocation", GlobalPos.CODEC, var1x));
      var1.storeNullable("current_explosion_impact_pos", Vec3.CODEC, this.currentImpulseImpactPos);
      var1.putBoolean("ignore_fall_damage_from_current_explosion", this.ignoreFallDamageFromCurrentImpulse);
      var1.putInt("current_impulse_context_reset_grace_time", this.currentImpulseContextResetGraceTime);
   }

   public boolean isInvulnerableTo(ServerLevel var1, DamageSource var2) {
      if (super.isInvulnerableTo(var1, var2)) {
         return true;
      } else if (var2.is(DamageTypeTags.IS_DROWNING)) {
         return !var1.getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
      } else if (var2.is(DamageTypeTags.IS_FALL)) {
         return !var1.getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
      } else if (var2.is(DamageTypeTags.IS_FIRE)) {
         return !var1.getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
      } else if (var2.is(DamageTypeTags.IS_FREEZING)) {
         return !var1.getGameRules().getBoolean(GameRules.RULE_FREEZE_DAMAGE);
      } else {
         return false;
      }
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isInvulnerableTo(var1, var2)) {
         return false;
      } else if (this.abilities.invulnerable && !var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return false;
      } else {
         this.noActionTime = 0;
         if (this.isDeadOrDying()) {
            return false;
         } else {
            this.removeEntitiesOnShoulder();
            if (var2.scalesWithDifficulty()) {
               if (var1.getDifficulty() == Difficulty.PEACEFUL) {
                  var3 = 0.0F;
               }

               if (var1.getDifficulty() == Difficulty.EASY) {
                  var3 = Math.min(var3 / 2.0F + 1.0F, var3);
               }

               if (var1.getDifficulty() == Difficulty.HARD) {
                  var3 = var3 * 3.0F / 2.0F;
               }
            }

            return var3 == 0.0F ? false : super.hurtServer(var1, var2, var3);
         }
      }
   }

   protected void blockUsingItem(ServerLevel var1, LivingEntity var2) {
      super.blockUsingItem(var1, var2);
      ItemStack var3 = this.getItemBlockingWith();
      BlocksAttacks var4 = var3 != null ? (BlocksAttacks)var3.get(DataComponents.BLOCKS_ATTACKS) : null;
      float var5 = var2.getSecondsToDisableBlocking();
      if (var5 > 0.0F && var4 != null) {
         var4.disable(var1, this, var5, var3);
      }

   }

   public boolean canBeSeenAsEnemy() {
      return !this.getAbilities().invulnerable && super.canBeSeenAsEnemy();
   }

   public boolean canHarmPlayer(Player var1) {
      PlayerTeam var2 = this.getTeam();
      PlayerTeam var3 = var1.getTeam();
      if (var2 == null) {
         return true;
      } else {
         return !((Team)var2).isAlliedTo(var3) ? true : ((Team)var2).isAllowFriendlyFire();
      }
   }

   protected void hurtArmor(DamageSource var1, float var2) {
      this.doHurtEquipment(var1, var2, new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD});
   }

   protected void hurtHelmet(DamageSource var1, float var2) {
      this.doHurtEquipment(var1, var2, new EquipmentSlot[]{EquipmentSlot.HEAD});
   }

   protected void actuallyHurt(ServerLevel var1, DamageSource var2, float var3) {
      if (!this.isInvulnerableTo(var1, var2)) {
         var3 = this.getDamageAfterArmorAbsorb(var2, var3);
         var3 = this.getDamageAfterMagicAbsorb(var2, var3);
         float var4 = var3;
         var3 = Math.max(var3 - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (var4 - var3));
         float var5 = var4 - var3;
         if (var5 > 0.0F && var5 < 3.4028235E37F) {
            this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(var5 * 10.0F));
         }

         if (var3 != 0.0F) {
            this.causeFoodExhaustion(var2.getFoodExhaustion());
            this.getCombatTracker().recordDamage(var2, var3);
            this.setHealth(this.getHealth() - var3);
            if (var3 < 3.4028235E37F) {
               this.awardStat(Stats.DAMAGE_TAKEN, Math.round(var3 * 10.0F));
            }

            this.gameEvent(GameEvent.ENTITY_DAMAGE);
         }
      }
   }

   public boolean isTextFilteringEnabled() {
      return false;
   }

   public void openTextEdit(SignBlockEntity var1, boolean var2) {
   }

   public void openMinecartCommandBlock(MinecartCommandBlock var1) {
   }

   public void openCommandBlock(CommandBlockEntity var1) {
   }

   public void openStructureBlock(StructureBlockEntity var1) {
   }

   public void openTestBlock(TestBlockEntity var1) {
   }

   public void openTestInstanceBlock(TestInstanceBlockEntity var1) {
   }

   public void openJigsawBlock(JigsawBlockEntity var1) {
   }

   public void openHorseInventory(AbstractHorse var1, Container var2) {
   }

   public OptionalInt openMenu(@Nullable MenuProvider var1) {
      return OptionalInt.empty();
   }

   public void openDialog(Holder<Dialog> var1) {
   }

   public void sendMerchantOffers(int var1, MerchantOffers var2, int var3, int var4, boolean var5, boolean var6) {
   }

   public void openItemGui(ItemStack var1, InteractionHand var2) {
   }

   public InteractionResult interactOn(Entity var1, InteractionHand var2) {
      if (this.isSpectator()) {
         if (var1 instanceof MenuProvider) {
            this.openMenu((MenuProvider)var1);
         }

         return InteractionResult.PASS;
      } else {
         ItemStack var3 = this.getItemInHand(var2);
         ItemStack var4 = var3.copy();
         InteractionResult var5 = var1.interact(this, var2);
         if (var5.consumesAction()) {
            if (this.hasInfiniteMaterials() && var3 == this.getItemInHand(var2) && var3.getCount() < var4.getCount()) {
               var3.setCount(var4.getCount());
            }

            return var5;
         } else {
            if (!var3.isEmpty() && var1 instanceof LivingEntity) {
               if (this.hasInfiniteMaterials()) {
                  var3 = var4;
               }

               InteractionResult var6 = var3.interactLivingEntity(this, (LivingEntity)var1, var2);
               if (var6.consumesAction()) {
                  this.level().gameEvent(GameEvent.ENTITY_INTERACT, var1.position(), GameEvent.Context.of((Entity)this));
                  if (var3.isEmpty() && !this.hasInfiniteMaterials()) {
                     this.setItemInHand(var2, ItemStack.EMPTY);
                  }

                  return var6;
               }
            }

            return InteractionResult.PASS;
         }
      }
   }

   public void removeVehicle() {
      super.removeVehicle();
      this.boardingCooldown = 0;
   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.isSleeping();
   }

   public boolean isAffectedByFluids() {
      return !this.abilities.flying;
   }

   protected Vec3 maybeBackOffFromEdge(Vec3 var1, MoverType var2) {
      float var3 = this.maxUpStep();
      if (!this.abilities.flying && !(var1.y > 0.0) && (var2 == MoverType.SELF || var2 == MoverType.PLAYER) && this.isStayingOnGroundSurface() && this.isAboveGround(var3)) {
         double var4 = var1.x;
         double var6 = var1.z;
         double var8 = 0.05;
         double var10 = Math.signum(var4) * 0.05;

         double var12;
         for(var12 = Math.signum(var6) * 0.05; var4 != 0.0 && this.canFallAtLeast(var4, 0.0, (double)var3); var4 -= var10) {
            if (Math.abs(var4) <= 0.05) {
               var4 = 0.0;
               break;
            }
         }

         while(var6 != 0.0 && this.canFallAtLeast(0.0, var6, (double)var3)) {
            if (Math.abs(var6) <= 0.05) {
               var6 = 0.0;
               break;
            }

            var6 -= var12;
         }

         while(var4 != 0.0 && var6 != 0.0 && this.canFallAtLeast(var4, var6, (double)var3)) {
            if (Math.abs(var4) <= 0.05) {
               var4 = 0.0;
            } else {
               var4 -= var10;
            }

            if (Math.abs(var6) <= 0.05) {
               var6 = 0.0;
            } else {
               var6 -= var12;
            }
         }

         return new Vec3(var4, var1.y, var6);
      } else {
         return var1;
      }
   }

   private boolean isAboveGround(float var1) {
      return this.onGround() || this.fallDistance < (double)var1 && !this.canFallAtLeast(0.0, 0.0, (double)var1 - this.fallDistance);
   }

   private boolean canFallAtLeast(double var1, double var3, double var5) {
      AABB var7 = this.getBoundingBox();
      return this.level().noCollision(this, new AABB(var7.minX + 1.0E-7 + var1, var7.minY - var5 - 1.0E-7, var7.minZ + 1.0E-7 + var3, var7.maxX - 1.0E-7 + var1, var7.minY, var7.maxZ - 1.0E-7 + var3));
   }

   public void attack(Entity var1) {
      if (!this.cannotAttack(var1)) {
         float var2 = this.isAutoSpinAttack() ? this.autoSpinAttackDmg : (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
         ItemStack var3 = this.getWeaponItem();
         DamageSource var4 = this.createAttackSource(var3);
         float var5 = this.getEnchantedDamage(var1, var2, var4) - var2;
         float var6 = this.getAttackStrengthScale(0.5F);
         var2 *= 0.2F + var6 * var6 * 0.8F;
         var5 *= var6;
         this.onAttack();
         if (!this.deflectProjectile(var1)) {
            if (var2 > 0.0F || var5 > 0.0F) {
               boolean var7 = var6 > 0.9F;
               boolean var8;
               if (this.isSprinting() && var7) {
                  this.makeSound(SoundEvents.PLAYER_ATTACK_KNOCKBACK);
                  var8 = true;
               } else {
                  var8 = false;
               }

               var2 += var3.getItem().getAttackDamageBonus(var1, var2, var4);
               boolean var9 = var7 && this.canCriticalAttack(var1);
               if (var9) {
                  var2 *= 1.5F;
               }

               float var10 = var2 + var5;
               boolean var11 = this.isSweepAttack(var7, var9, var8);
               float var12 = 0.0F;
               if (var1 instanceof LivingEntity) {
                  LivingEntity var13 = (LivingEntity)var1;
                  var12 = var13.getHealth();
               }

               Vec3 var18 = var1.getDeltaMovement();
               boolean var14 = var1.hurtOrSimulate(var4, var10);
               if (var14) {
                  this.causeExtraKnockback(var1, this.getKnockback(var1, var4) + (var8 ? 0.5F : 0.0F), var18);
                  if (var11) {
                     this.doSweepAttack(var1, var2, var4, var6);
                  }

                  this.attackVisualEffects(var1, var9, var11, var7, var5);
                  this.setLastHurtMob(var1);
                  this.itemAttackInteraction(var1, var3, var4, true);
                  this.damageStatsAndHearts(var1, var12);
                  this.causeFoodExhaustion(0.1F);
               } else {
                  this.makeSound(SoundEvents.PLAYER_ATTACK_NODAMAGE);
               }
            }

            this.lungeForwardMaybe();
         }
      }
   }

   private DamageSource createAttackSource(ItemStack var1) {
      return var1.getDamageSource(this, () -> this.damageSources().playerAttack(this));
   }

   private boolean cannotAttack(Entity var1) {
      return !var1.isAttackable() ? true : var1.skipAttackInteraction(this);
   }

   private boolean deflectProjectile(Entity var1) {
      if (var1.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE) && var1 instanceof Projectile var2) {
         if (var2.deflect(ProjectileDeflection.AIM_DEFLECT, this, EntityReference.of(this), true)) {
            this.level().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource());
            return true;
         }
      }

      return false;
   }

   private boolean canCriticalAttack(Entity var1) {
      return this.fallDistance > 0.0 && !this.onGround() && !this.onClimbable() && !this.isInWater() && !this.isMobilityRestricted() && !this.isPassenger() && var1 instanceof LivingEntity && !this.isSprinting();
   }

   private boolean isSweepAttack(boolean var1, boolean var2, boolean var3) {
      if (var1 && !var2 && !var3 && this.onGround()) {
         double var4 = this.getKnownMovement().horizontalDistanceSqr();
         double var6 = (double)this.getSpeed() * 2.5;
         if (var4 < Mth.square(var6)) {
            return this.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.SWORDS);
         }
      }

      return false;
   }

   private void attackVisualEffects(Entity var1, boolean var2, boolean var3, boolean var4, float var5) {
      if (var2) {
         this.makeSound(SoundEvents.PLAYER_ATTACK_CRIT);
         this.crit(var1);
      }

      if (!var2 && !var3) {
         this.makeSound(var4 ? SoundEvents.PLAYER_ATTACK_STRONG : SoundEvents.PLAYER_ATTACK_WEAK);
      }

      if (var5 > 0.0F) {
         this.magicCrit(var1);
      }

   }

   private void damageStatsAndHearts(Entity var1, float var2) {
      if (var1 instanceof LivingEntity) {
         float var3 = var2 - ((LivingEntity)var1).getHealth();
         this.awardStat(Stats.DAMAGE_DEALT, Math.round(var3 * 10.0F));
         if (this.level() instanceof ServerLevel && var3 > 2.0F) {
            int var4 = (int)((double)var3 * 0.5);
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, var1.getX(), var1.getY(0.5), var1.getZ(), var4, 0.1, 0.0, 0.1, 0.2);
         }
      }

   }

   private void itemAttackInteraction(Entity var1, ItemStack var2, DamageSource var3, boolean var4) {
      Object var5 = var1;
      if (var1 instanceof EnderDragonPart) {
         var5 = ((EnderDragonPart)var1).parentMob;
      }

      boolean var6 = false;
      Level var8 = this.level();
      if (var8 instanceof ServerLevel var7) {
         if (var5 instanceof LivingEntity var9) {
            var6 = var2.hurtEnemy(var9, this);
         }

         if (var4) {
            EnchantmentHelper.doPostAttackEffectsWithItemSource(var7, var1, var3, var2);
         }
      }

      if (!this.level().isClientSide() && !var2.isEmpty() && var5 instanceof LivingEntity) {
         if (var6) {
            var2.postHurtEnemy((LivingEntity)var5, this);
         }

         if (var2.isEmpty()) {
            if (var2 == this.getMainHandItem()) {
               this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            } else {
               this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
         }
      }

   }

   public void causeExtraKnockback(Entity var1, float var2, Vec3 var3) {
      if (var2 > 0.0F) {
         if (var1 instanceof LivingEntity) {
            LivingEntity var4 = (LivingEntity)var1;
            var4.knockback((double)var2, (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
         } else {
            var1.push((double)(-Mth.sin(this.getYRot() * 0.017453292F) * var2), 0.1, (double)(Mth.cos(this.getYRot() * 0.017453292F) * var2));
         }

         this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
         this.setSprinting(false);
      }

      if (var1 instanceof ServerPlayer && var1.hurtMarked) {
         ((ServerPlayer)var1).connection.send(new ClientboundSetEntityMotionPacket(var1));
         var1.hurtMarked = false;
         var1.setDeltaMovement(var3);
      }

   }

   public float getVoicePitch() {
      return 1.0F;
   }

   private void doSweepAttack(Entity var1, float var2, DamageSource var3, float var4) {
      this.makeSound(SoundEvents.PLAYER_ATTACK_SWEEP);
      Level var6 = this.level();
      if (var6 instanceof ServerLevel var5) {
         float var12 = 1.0F + (float)this.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * var2;

         for(LivingEntity var9 : this.level().getEntitiesOfClass(LivingEntity.class, var1.getBoundingBox().inflate(1.0, 0.25, 1.0))) {
            if (var9 != this && var9 != var1 && !this.isAlliedTo(var9)) {
               if (var9 instanceof ArmorStand) {
                  ArmorStand var10 = (ArmorStand)var9;
                  if (var10.isMarker()) {
                     continue;
                  }
               }

               if (this.distanceToSqr(var9) < 9.0) {
                  float var14 = this.getEnchantedDamage(var9, var12, var3) * var4;
                  if (var9.hurtServer(var5, var3, var14)) {
                     var9.knockback(0.4000000059604645, (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F)));
                     EnchantmentHelper.doPostAttackEffects(var5, var9, var3);
                  }
               }
            }
         }

         double var13 = (double)(-Mth.sin(this.getYRot() * 0.017453292F));
         double var15 = (double)Mth.cos(this.getYRot() * 0.017453292F);
         var5.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + var13, this.getY(0.5), this.getZ() + var15, 0, var13, 0.0, var15, 0.0);
      }
   }

   protected float getEnchantedDamage(Entity var1, float var2, DamageSource var3) {
      return var2;
   }

   protected void doAutoAttackOnTouch(LivingEntity var1) {
      this.attack(var1);
   }

   public void crit(Entity var1) {
   }

   public boolean stabAttack(EquipmentSlot var1, Entity var2, float var3, boolean var4, boolean var5, boolean var6) {
      if (this.cannotAttack(var2)) {
         return false;
      } else {
         ItemStack var7 = this.getItemBySlot(var1);
         DamageSource var8 = this.createAttackSource(var7);
         float var9 = this.getEnchantedDamage(var2, var3, var8) - var3;
         if (var5 && this.deflectProjectile(var2)) {
            return true;
         } else {
            float var10 = var4 ? var3 + var9 : 0.0F;
            float var11 = 0.0F;
            if (var2 instanceof LivingEntity) {
               LivingEntity var12 = (LivingEntity)var2;
               var11 = var12.getHealth();
            }

            Vec3 var15 = var2.getDeltaMovement();
            boolean var13 = var4 && var2.hurtOrSimulate(var8, var10);
            if (var5) {
               this.causeExtraKnockback(var2, 0.4F + this.getKnockback(var2, var8), var15);
            }

            boolean var14 = false;
            if (var6 && var2.isPassenger()) {
               var14 = true;
               var2.stopRiding();
            }

            if (!var13 && !var5 && !var14) {
               return false;
            } else {
               this.attackVisualEffects(var2, false, false, true, var9);
               this.setLastHurtMob(var2);
               this.itemAttackInteraction(var2, var7, var8, var13);
               this.damageStatsAndHearts(var2, var11);
               this.causeFoodExhaustion(0.1F);
               return true;
            }
         }
      }
   }

   public void magicCrit(Entity var1) {
   }

   public void remove(Entity.RemovalReason var1) {
      super.remove(var1);
      this.inventoryMenu.removed(this);
      if (this.hasContainerOpen()) {
         this.doCloseContainer();
      }

   }

   public boolean isClientAuthoritative() {
      return true;
   }

   protected boolean isLocalClientAuthoritative() {
      return this.isLocalPlayer();
   }

   public boolean isLocalPlayer() {
      return false;
   }

   public boolean canSimulateMovement() {
      return !this.level().isClientSide() || this.isLocalPlayer();
   }

   public boolean isEffectiveAi() {
      return !this.level().isClientSide() || this.isLocalPlayer();
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public NameAndId nameAndId() {
      return new NameAndId(this.gameProfile);
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public Abilities getAbilities() {
      return this.abilities;
   }

   public boolean hasInfiniteMaterials() {
      return this.abilities.instabuild;
   }

   public boolean preventsBlockDrops() {
      return this.abilities.instabuild;
   }

   public void updateTutorialInventoryAction(ItemStack var1, ItemStack var2, ClickAction var3) {
   }

   public boolean hasContainerOpen() {
      return this.containerMenu != this.inventoryMenu;
   }

   public boolean canDropItems() {
      return true;
   }

   public Either<BedSleepingProblem, Unit> startSleepInBed(BlockPos var1) {
      this.startSleeping(var1);
      this.sleepCounter = 0;
      return Either.right(Unit.INSTANCE);
   }

   public void stopSleepInBed(boolean var1, boolean var2) {
      super.stopSleeping();
      if (this.level() instanceof ServerLevel && var2) {
         ((ServerLevel)this.level()).updateSleepingPlayerList();
      }

      this.sleepCounter = var1 ? 0 : 100;
   }

   public void stopSleeping() {
      this.stopSleepInBed(true, true);
   }

   public boolean isSleepingLongEnough() {
      return this.isSleeping() && this.sleepCounter >= 100;
   }

   public int getSleepTimer() {
      return this.sleepCounter;
   }

   public void displayClientMessage(Component var1, boolean var2) {
   }

   public void awardStat(ResourceLocation var1) {
      this.awardStat(Stats.CUSTOM.get(var1));
   }

   public void awardStat(ResourceLocation var1, int var2) {
      this.awardStat(Stats.CUSTOM.get(var1), var2);
   }

   public void awardStat(Stat<?> var1) {
      this.awardStat((Stat)var1, 1);
   }

   public void awardStat(Stat<?> var1, int var2) {
   }

   public void resetStat(Stat<?> var1) {
   }

   public int awardRecipes(Collection<RecipeHolder<?>> var1) {
      return 0;
   }

   public void triggerRecipeCrafted(RecipeHolder<?> var1, List<ItemStack> var2) {
   }

   public void awardRecipesByKey(List<ResourceKey<Recipe<?>>> var1) {
   }

   public int resetRecipes(Collection<RecipeHolder<?>> var1) {
      return 0;
   }

   public void travel(Vec3 var1) {
      if (this.isPassenger()) {
         super.travel(var1);
      } else {
         if (this.isSwimming()) {
            double var2 = this.getLookAngle().y;
            double var4 = var2 < -0.2 ? 0.085 : 0.06;
            if (var2 <= 0.0 || this.jumping || !this.level().getFluidState(BlockPos.containing(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).isEmpty()) {
               Vec3 var6 = this.getDeltaMovement();
               this.setDeltaMovement(var6.add(0.0, (var2 - var6.y) * var4, 0.0));
            }
         }

         if (this.getAbilities().flying) {
            double var7 = this.getDeltaMovement().y;
            super.travel(var1);
            this.setDeltaMovement(this.getDeltaMovement().with(Direction.Axis.Y, var7 * 0.6));
         } else {
            super.travel(var1);
         }

      }
   }

   protected boolean canGlide() {
      return !this.abilities.flying && super.canGlide();
   }

   public void updateSwimming() {
      if (this.abilities.flying) {
         this.setSwimming(false);
      } else {
         super.updateSwimming();
      }

   }

   protected boolean freeAt(BlockPos var1) {
      return !this.level().getBlockState(var1).isSuffocating(this.level(), var1);
   }

   public float getSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   public boolean causeFallDamage(double var1, float var3, DamageSource var4) {
      if (this.abilities.mayfly) {
         return false;
      } else {
         if (var1 >= 2.0) {
            this.awardStat(Stats.FALL_ONE_CM, (int)Math.round(var1 * 100.0));
         }

         boolean var7 = this.currentImpulseImpactPos != null && this.ignoreFallDamageFromCurrentImpulse;
         double var5;
         if (var7) {
            var5 = Math.min(var1, this.currentImpulseImpactPos.y - this.getY());
            boolean var8 = var5 <= 0.0;
            if (var8) {
               this.resetCurrentImpulseContext();
            } else {
               this.tryResetCurrentImpulseContext();
            }
         } else {
            var5 = var1;
         }

         if (var5 > 0.0 && super.causeFallDamage(var5, var3, var4)) {
            this.resetCurrentImpulseContext();
            return true;
         } else {
            this.propagateFallToPassengers(var1, var3, var4);
            return false;
         }
      }
   }

   public boolean tryToStartFallFlying() {
      if (!this.isFallFlying() && this.canGlide() && !this.isInWater()) {
         this.startFallFlying();
         return true;
      } else {
         return false;
      }
   }

   public void startFallFlying() {
      this.setSharedFlag(7, true);
   }

   protected void doWaterSplashEffect() {
      if (!this.isSpectator()) {
         super.doWaterSplashEffect();
      }

   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      if (this.isInWater()) {
         this.waterSwimSound();
         this.playMuffledStepSound(var2);
      } else {
         BlockPos var3 = this.getPrimaryStepSoundBlockPos(var1);
         if (!var1.equals(var3)) {
            BlockState var4 = this.level().getBlockState(var3);
            if (var4.is(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)) {
               this.playCombinationStepSounds(var4, var2);
            } else {
               super.playStepSound(var3, var4);
            }
         } else {
            super.playStepSound(var1, var2);
         }
      }

   }

   public LivingEntity.Fallsounds getFallSounds() {
      return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
   }

   public boolean killedEntity(ServerLevel var1, LivingEntity var2, DamageSource var3) {
      this.awardStat(Stats.ENTITY_KILLED.get(var2.getType()));
      return true;
   }

   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
      if (!this.abilities.flying) {
         super.makeStuckInBlock(var1, var2);
      }

      this.tryResetCurrentImpulseContext();
   }

   public void giveExperiencePoints(int var1) {
      this.increaseScore(var1);
      this.experienceProgress += (float)var1 / (float)this.getXpNeededForNextLevel();
      this.totalExperience = Mth.clamp(this.totalExperience + var1, 0, 2147483647);

      while(this.experienceProgress < 0.0F) {
         float var2 = this.experienceProgress * (float)this.getXpNeededForNextLevel();
         if (this.experienceLevel > 0) {
            this.giveExperienceLevels(-1);
            this.experienceProgress = 1.0F + var2 / (float)this.getXpNeededForNextLevel();
         } else {
            this.giveExperienceLevels(-1);
            this.experienceProgress = 0.0F;
         }
      }

      while(this.experienceProgress >= 1.0F) {
         this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getXpNeededForNextLevel();
         this.giveExperienceLevels(1);
         this.experienceProgress /= (float)this.getXpNeededForNextLevel();
      }

   }

   public int getEnchantmentSeed() {
      return this.enchantmentSeed;
   }

   public void onEnchantmentPerformed(ItemStack var1, int var2) {
      this.experienceLevel -= var2;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experienceProgress = 0.0F;
         this.totalExperience = 0;
      }

      this.enchantmentSeed = this.random.nextInt();
   }

   public void giveExperienceLevels(int var1) {
      this.experienceLevel = IntMath.saturatedAdd(this.experienceLevel, var1);
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experienceProgress = 0.0F;
         this.totalExperience = 0;
      }

      if (var1 > 0 && this.experienceLevel % 5 == 0 && (float)this.lastLevelUpTime < (float)this.tickCount - 100.0F) {
         float var2 = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
         this.level().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), var2 * 0.75F, 1.0F);
         this.lastLevelUpTime = this.tickCount;
      }

   }

   public int getXpNeededForNextLevel() {
      if (this.experienceLevel >= 30) {
         return 112 + (this.experienceLevel - 30) * 9;
      } else {
         return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
      }
   }

   public void causeFoodExhaustion(float var1) {
      if (!this.abilities.invulnerable) {
         if (!this.level().isClientSide()) {
            this.foodData.addExhaustion(var1);
         }

      }
   }

   public void lungeForwardMaybe() {
      if (this.foodData.hasEnoughFood()) {
         super.lungeForwardMaybe();
      }

   }

   public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
      return Optional.empty();
   }

   public FoodData getFoodData() {
      return this.foodData;
   }

   public boolean canEat(boolean var1) {
      return this.abilities.invulnerable || var1 || this.foodData.needsFood();
   }

   public boolean isHurt() {
      return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
   }

   public boolean mayBuild() {
      return this.abilities.mayBuild;
   }

   public boolean mayUseItemAt(BlockPos var1, Direction var2, ItemStack var3) {
      if (this.abilities.mayBuild) {
         return true;
      } else {
         BlockPos var4 = var1.relative(var2.getOpposite());
         BlockInWorld var5 = new BlockInWorld(this.level(), var4, false);
         return var3.canPlaceOnBlockInAdventureMode(var5);
      }
   }

   protected int getBaseExperienceReward(ServerLevel var1) {
      return !var1.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator() ? Math.min(this.experienceLevel * 7, 100) : 0;
   }

   protected boolean isAlwaysExperienceDropper() {
      return true;
   }

   public boolean shouldShowName() {
      return true;
   }

   protected Entity.MovementEmission getMovementEmission() {
      return this.abilities.flying || this.onGround() && this.isDiscrete() ? Entity.MovementEmission.NONE : Entity.MovementEmission.ALL;
   }

   public void onUpdateAbilities() {
   }

   public Component getName() {
      return Component.literal(this.gameProfile.name());
   }

   public String getPlainTextName() {
      return this.gameProfile.name();
   }

   public PlayerEnderChestContainer getEnderChestInventory() {
      return this.enderChestInventory;
   }

   protected boolean doesEmitEquipEvent(EquipmentSlot var1) {
      return var1.getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
   }

   public boolean addItem(ItemStack var1) {
      return this.inventory.add(var1);
   }

   @Nullable
   public abstract GameType gameMode();

   public boolean isSpectator() {
      return this.gameMode() == GameType.SPECTATOR;
   }

   public boolean canBeHitByProjectile() {
      return !this.isSpectator() && super.canBeHitByProjectile();
   }

   public boolean isSwimming() {
      return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
   }

   public boolean isCreative() {
      return this.gameMode() == GameType.CREATIVE;
   }

   public boolean isPushedByFluid() {
      return !this.abilities.flying;
   }

   public Component getDisplayName() {
      MutableComponent var1 = PlayerTeam.formatNameForTeam(this.getTeam(), this.getName());
      return this.decorateDisplayNameComponent(var1);
   }

   private MutableComponent decorateDisplayNameComponent(MutableComponent var1) {
      String var2 = this.getGameProfile().name();
      return var1.withStyle((UnaryOperator)((var2x) -> var2x.withClickEvent(new ClickEvent.SuggestCommand("/tell " + var2 + " ")).withHoverEvent(this.createHoverEvent()).withInsertion(var2)));
   }

   public String getScoreboardName() {
      return this.getGameProfile().name();
   }

   protected void internalSetAbsorptionAmount(float var1) {
      this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, var1);
   }

   public float getAbsorptionAmount() {
      return (Float)this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID);
   }

   public SlotAccess getSlot(int var1) {
      if (var1 == 499) {
         return new SlotAccess() {
            public ItemStack get() {
               return Player.this.containerMenu.getCarried();
            }

            public boolean set(ItemStack var1) {
               Player.this.containerMenu.setCarried(var1);
               return true;
            }
         };
      } else {
         final int var2 = var1 - 500;
         if (var2 >= 0 && var2 < 4) {
            return new SlotAccess() {
               public ItemStack get() {
                  return Player.this.inventoryMenu.getCraftSlots().getItem(var2);
               }

               public boolean set(ItemStack var1) {
                  Player.this.inventoryMenu.getCraftSlots().setItem(var2, var1);
                  Player.this.inventoryMenu.slotsChanged(Player.this.inventory);
                  return true;
               }
            };
         } else if (var1 >= 0 && var1 < this.inventory.getNonEquipmentItems().size()) {
            return SlotAccess.forContainer(this.inventory, var1);
         } else {
            int var3 = var1 - 200;
            return var3 >= 0 && var3 < this.enderChestInventory.getContainerSize() ? SlotAccess.forContainer(this.enderChestInventory, var3) : super.getSlot(var1);
         }
      }
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public void setReducedDebugInfo(boolean var1) {
      this.reducedDebugInfo = var1;
   }

   public void setRemainingFireTicks(int var1) {
      super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min(var1, 1) : var1);
   }

   protected static Optional<Parrot.Variant> extractParrotVariant(CompoundTag var0) {
      if (!var0.isEmpty()) {
         EntityType var1 = (EntityType)var0.read("id", EntityType.CODEC).orElse((Object)null);
         if (var1 == EntityType.PARROT) {
            return var0.read("Variant", Parrot.Variant.LEGACY_CODEC);
         }
      }

      return Optional.empty();
   }

   protected static OptionalInt convertParrotVariant(Optional<Parrot.Variant> var0) {
      return (OptionalInt)var0.map((var0x) -> OptionalInt.of(var0x.getId())).orElse(OptionalInt.empty());
   }

   private static Optional<Parrot.Variant> convertParrotVariant(OptionalInt var0) {
      return var0.isPresent() ? Optional.of(Parrot.Variant.byId(var0.getAsInt())) : Optional.empty();
   }

   public void setShoulderParrotLeft(Optional<Parrot.Variant> var1) {
      this.entityData.set(DATA_SHOULDER_PARROT_LEFT, convertParrotVariant(var1));
   }

   public Optional<Parrot.Variant> getShoulderParrotLeft() {
      return convertParrotVariant((OptionalInt)this.entityData.get(DATA_SHOULDER_PARROT_LEFT));
   }

   public void setShoulderParrotRight(Optional<Parrot.Variant> var1) {
      this.entityData.set(DATA_SHOULDER_PARROT_RIGHT, convertParrotVariant(var1));
   }

   public Optional<Parrot.Variant> getShoulderParrotRight() {
      return convertParrotVariant((OptionalInt)this.entityData.get(DATA_SHOULDER_PARROT_RIGHT));
   }

   public float getCurrentItemAttackStrengthDelay() {
      return (float)(1.0 / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0);
   }

   public boolean cannotAttackWithItem(ItemStack var1, int var2) {
      float var3 = (Float)var1.getOrDefault(DataComponents.MINIMUM_ATTACK_CHARGE, 0.0F);
      float var4 = (float)(this.attackStrengthTicker + var2) / this.getCurrentItemAttackStrengthDelay();
      return var3 > 0.0F && var4 < var3;
   }

   public float getAttackStrengthScale(float var1) {
      return Mth.clamp(((float)this.attackStrengthTicker + var1) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
   }

   public float getItemSwapScale(float var1) {
      return Mth.clamp(((float)this.itemSwapTicker + var1) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
   }

   public void resetAttackStrengthTicker() {
      this.attackStrengthTicker = 0;
      this.itemSwapTicker = 0;
   }

   public void onAttack() {
      this.resetOnlyAttackStrengthTicker();
      super.onAttack();
   }

   public void resetOnlyAttackStrengthTicker() {
      this.attackStrengthTicker = 0;
   }

   public ItemCooldowns getCooldowns() {
      return this.cooldowns;
   }

   protected float getBlockSpeedFactor() {
      return !this.abilities.flying && !this.isFallFlying() ? super.getBlockSpeedFactor() : 1.0F;
   }

   public float getLuck() {
      return (float)this.getAttributeValue(Attributes.LUCK);
   }

   public boolean canUseGameMasterBlocks() {
      return this.abilities.instabuild && this.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
   }

   public PermissionSet permissions() {
      return PermissionSet.NO_PERMISSIONS;
   }

   public ImmutableList<Pose> getDismountPoses() {
      return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
   }

   public ItemStack getProjectile(ItemStack var1) {
      if (!(var1.getItem() instanceof ProjectileWeaponItem)) {
         return ItemStack.EMPTY;
      } else {
         Predicate var2 = ((ProjectileWeaponItem)var1.getItem()).getSupportedHeldProjectiles();
         ItemStack var3 = ProjectileWeaponItem.getHeldProjectile(this, var2);
         if (!var3.isEmpty()) {
            return var3;
         } else {
            var2 = ((ProjectileWeaponItem)var1.getItem()).getAllSupportedProjectiles();

            for(int var4 = 0; var4 < this.inventory.getContainerSize(); ++var4) {
               ItemStack var5 = this.inventory.getItem(var4);
               if (var2.test(var5)) {
                  return var5;
               }
            }

            return this.hasInfiniteMaterials() ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
         }
      }
   }

   public Vec3 getRopeHoldPosition(float var1) {
      double var2 = 0.22 * (this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0);
      float var4 = Mth.lerp(var1 * 0.5F, this.getXRot(), this.xRotO) * 0.017453292F;
      float var5 = Mth.lerp(var1, this.yBodyRotO, this.yBodyRot) * 0.017453292F;
      if (!this.isFallFlying() && !this.isAutoSpinAttack()) {
         if (this.isVisuallySwimming()) {
            return this.getPosition(var1).add((new Vec3(var2, 0.2, -0.15)).xRot(-var4).yRot(-var5));
         } else {
            double var17 = this.getBoundingBox().getYsize() - 1.0;
            double var18 = this.isCrouching() ? -0.2 : 0.07;
            return this.getPosition(var1).add((new Vec3(var2, var17, var18)).yRot(-var5));
         }
      } else {
         Vec3 var6 = this.getViewVector(var1);
         Vec3 var7 = this.getDeltaMovement();
         double var8 = var7.horizontalDistanceSqr();
         double var10 = var6.horizontalDistanceSqr();
         float var12;
         if (var8 > 0.0 && var10 > 0.0) {
            double var13 = (var7.x * var6.x + var7.z * var6.z) / Math.sqrt(var8 * var10);
            double var15 = var7.x * var6.z - var7.z * var6.x;
            var12 = (float)(Math.signum(var15) * Math.acos(var13));
         } else {
            var12 = 0.0F;
         }

         return this.getPosition(var1).add((new Vec3(var2, -0.11, 0.85)).zRot(-var12).xRot(-var4).yRot(-var5));
      }
   }

   public boolean isAlwaysTicking() {
      return true;
   }

   public boolean isScoping() {
      return this.isUsingItem() && this.getUseItem().is(Items.SPYGLASS);
   }

   public boolean shouldBeSaved() {
      return false;
   }

   public Optional<GlobalPos> getLastDeathLocation() {
      return this.lastDeathLocation;
   }

   public void setLastDeathLocation(Optional<GlobalPos> var1) {
      this.lastDeathLocation = var1;
   }

   public float getHurtDir() {
      return this.hurtDir;
   }

   public void animateHurt(float var1) {
      super.animateHurt(var1);
      this.hurtDir = var1;
   }

   public boolean isMobilityRestricted() {
      return this.hasEffect(MobEffects.BLINDNESS);
   }

   public boolean canSprint() {
      return true;
   }

   protected float getFlyingSpeed() {
      if (this.abilities.flying && !this.isPassenger()) {
         return this.isSprinting() ? this.abilities.getFlyingSpeed() * 2.0F : this.abilities.getFlyingSpeed();
      } else {
         return this.isSprinting() ? 0.025999999F : 0.02F;
      }
   }

   public boolean hasClientLoaded() {
      return this.clientLoaded || this.clientLoadedTimeoutTimer <= 0;
   }

   public void tickClientLoadTimeout() {
      if (!this.clientLoaded) {
         --this.clientLoadedTimeoutTimer;
      }

   }

   public void setClientLoaded(boolean var1) {
      this.clientLoaded = var1;
      if (!this.clientLoaded) {
         this.clientLoadedTimeoutTimer = 60;
      }

   }

   public boolean hasContainerOpen(ContainerOpenersCounter var1, BlockPos var2) {
      return var1.isOwnContainer(this);
   }

   public double getContainerInteractionRange() {
      return this.blockInteractionRange();
   }

   public double blockInteractionRange() {
      return this.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
   }

   public double entityInteractionRange() {
      return this.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
   }

   public boolean canInteractWithEntity(Entity var1, double var2) {
      return var1.isRemoved() ? false : this.canInteractWithEntity(var1.getBoundingBox(), var2);
   }

   public boolean canInteractWithEntity(AABB var1, double var2) {
      double var4 = this.entityInteractionRange() + var2;
      return var1.distanceToSqr(this.getEyePosition()) < var4 * var4;
   }

   public boolean canInteractWithBlock(BlockPos var1, double var2) {
      double var4 = this.blockInteractionRange() + var2;
      return (new AABB(var1)).distanceToSqr(this.getEyePosition()) < var4 * var4;
   }

   public void setIgnoreFallDamageFromCurrentImpulse(boolean var1) {
      this.ignoreFallDamageFromCurrentImpulse = var1;
      if (var1) {
         this.currentImpulseContextResetGraceTime = 40;
      } else {
         this.currentImpulseContextResetGraceTime = 0;
      }

   }

   public boolean isIgnoringFallDamageFromCurrentImpulse() {
      return this.ignoreFallDamageFromCurrentImpulse;
   }

   public void tryResetCurrentImpulseContext() {
      if (this.currentImpulseContextResetGraceTime == 0) {
         this.resetCurrentImpulseContext();
      }

   }

   public void resetCurrentImpulseContext() {
      this.currentImpulseContextResetGraceTime = 0;
      this.currentExplosionCause = null;
      this.currentImpulseImpactPos = null;
      this.ignoreFallDamageFromCurrentImpulse = false;
   }

   public boolean shouldRotateWithMinecart() {
      return false;
   }

   public boolean onClimbable() {
      return this.abilities.flying ? false : super.onClimbable();
   }

   public String debugInfo() {
      return MoreObjects.toStringHelper(this).add("name", this.getPlainTextName()).add("id", this.getId()).add("pos", this.position()).add("mode", this.gameMode()).add("permission", this.permissions()).toString();
   }

   static {
      DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.<Float>defineId(Player.class, EntityDataSerializers.FLOAT);
      DATA_SCORE_ID = SynchedEntityData.<Integer>defineId(Player.class, EntityDataSerializers.INT);
      DATA_SHOULDER_PARROT_LEFT = SynchedEntityData.<OptionalInt>defineId(Player.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
      DATA_SHOULDER_PARROT_RIGHT = SynchedEntityData.<OptionalInt>defineId(Player.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
   }

   public static record BedSleepingProblem(@Nullable Component message) {
      public static final BedSleepingProblem TOO_FAR_AWAY = new BedSleepingProblem(Component.translatable("block.minecraft.bed.too_far_away"));
      public static final BedSleepingProblem OBSTRUCTED = new BedSleepingProblem(Component.translatable("block.minecraft.bed.obstructed"));
      public static final BedSleepingProblem OTHER_PROBLEM = new BedSleepingProblem((Component)null);
      public static final BedSleepingProblem NOT_SAFE = new BedSleepingProblem(Component.translatable("block.minecraft.bed.not_safe"));

      public BedSleepingProblem(@Nullable Component var1) {
         super();
         this.message = var1;
      }
   }
}
