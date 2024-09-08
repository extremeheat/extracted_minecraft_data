package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.TamableAnimal;
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
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.slf4j.Logger;

public abstract class Player extends LivingEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final HumanoidArm DEFAULT_MAIN_HAND = HumanoidArm.RIGHT;
   public static final int DEFAULT_MODEL_CUSTOMIZATION = 0;
   public static final int MAX_HEALTH = 20;
   public static final int SLEEP_DURATION = 100;
   public static final int WAKE_UP_DURATION = 10;
   public static final int ENDER_SLOT_OFFSET = 200;
   public static final int HELD_ITEM_SLOT = 499;
   public static final int CRAFTING_SLOT_OFFSET = 500;
   public static final float DEFAULT_BLOCK_INTERACTION_RANGE = 4.5F;
   public static final float DEFAULT_ENTITY_INTERACTION_RANGE = 3.0F;
   public static final float CROUCH_BB_HEIGHT = 1.5F;
   public static final float SWIMMING_BB_WIDTH = 0.6F;
   public static final float SWIMMING_BB_HEIGHT = 0.6F;
   public static final float DEFAULT_EYE_HEIGHT = 1.62F;
   private static final int CURRENT_IMPULSE_CONTEXT_RESET_GRACE_TIME_TICKS = 40;
   public static final Vec3 DEFAULT_VEHICLE_ATTACHMENT = new Vec3(0.0, 0.6, 0.0);
   public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F)
      .withEyeHeight(1.62F)
      .withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT));
   private static final Map<Pose, EntityDimensions> POSES = ImmutableMap.builder()
      .put(Pose.STANDING, STANDING_DIMENSIONS)
      .put(Pose.SLEEPING, SLEEPING_DIMENSIONS)
      .put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
      .put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
      .put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
      .put(
         Pose.CROUCHING,
         EntityDimensions.scalable(0.6F, 1.5F)
            .withEyeHeight(1.27F)
            .withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT))
      )
      .put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F).withEyeHeight(1.62F))
      .build();
   private static final EntityDataAccessor<Float> DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Integer> DATA_SCORE_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor<Byte> DATA_PLAYER_MAIN_HAND = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_LEFT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
   protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_RIGHT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
   private long timeEntitySatOnShoulder;
   final Inventory inventory = new Inventory(this);
   protected PlayerEnderChestContainer enderChestInventory = new PlayerEnderChestContainer();
   public final InventoryMenu inventoryMenu;
   public AbstractContainerMenu containerMenu;
   protected FoodData foodData = new FoodData();
   protected int jumpTriggerTime;
   public float oBob;
   public float bob;
   public int takeXpDelay;
   public double xCloakO;
   public double yCloakO;
   public double zCloakO;
   public double xCloak;
   public double yCloak;
   public double zCloak;
   private int sleepCounter;
   protected boolean wasUnderwater;
   private final Abilities abilities = new Abilities();
   public int experienceLevel;
   public int totalExperience;
   public float experienceProgress;
   protected int enchantmentSeed;
   protected final float defaultFlySpeed = 0.02F;
   private int lastLevelUpTime;
   private final GameProfile gameProfile;
   private boolean reducedDebugInfo;
   private ItemStack lastItemInMainHand = ItemStack.EMPTY;
   private final ItemCooldowns cooldowns = this.createItemCooldowns();
   private Optional<GlobalPos> lastDeathLocation = Optional.empty();
   @Nullable
   public FishingHook fishing;
   protected float hurtDir;
   @Nullable
   public Vec3 currentImpulseImpactPos;
   @Nullable
   public Entity currentExplosionCause;
   private boolean ignoreFallDamageFromCurrentImpulse;
   private int currentImpulseContextResetGraceTime;

   public Player(Level var1, BlockPos var2, float var3, GameProfile var4) {
      super(EntityType.PLAYER, var1);
      this.setUUID(var4.getId());
      this.gameProfile = var4;
      this.inventoryMenu = new InventoryMenu(this.inventory, !var1.isClientSide, this);
      this.containerMenu = this.inventoryMenu;
      this.moveTo((double)var2.getX() + 0.5, (double)(var2.getY() + 1), (double)var2.getZ() + 0.5, var3, 0.0F);
      this.rotOffs = 180.0F;
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
      return LivingEntity.createLivingAttributes()
         .add(Attributes.ATTACK_DAMAGE, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.10000000149011612)
         .add(Attributes.ATTACK_SPEED)
         .add(Attributes.LUCK)
         .add(Attributes.BLOCK_INTERACTION_RANGE, 4.5)
         .add(Attributes.ENTITY_INTERACTION_RANGE, 3.0)
         .add(Attributes.BLOCK_BREAK_SPEED)
         .add(Attributes.SUBMERGED_MINING_SPEED)
         .add(Attributes.SNEAKING_SPEED)
         .add(Attributes.MINING_EFFICIENCY)
         .add(Attributes.SWEEPING_DAMAGE_RATIO);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_PLAYER_ABSORPTION_ID, 0.0F);
      var1.define(DATA_SCORE_ID, 0);
      var1.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
      var1.define(DATA_PLAYER_MAIN_HAND, (byte)DEFAULT_MAIN_HAND.getId());
      var1.define(DATA_SHOULDER_LEFT, new CompoundTag());
      var1.define(DATA_SHOULDER_RIGHT, new CompoundTag());
   }

   @Override
   public void tick() {
      this.noPhysics = this.isSpectator();
      if (this.isSpectator()) {
         this.setOnGround(false);
      }

      if (this.takeXpDelay > 0) {
         this.takeXpDelay--;
      }

      if (this.isSleeping()) {
         this.sleepCounter++;
         if (this.sleepCounter > 100) {
            this.sleepCounter = 100;
         }

         if (!this.level().isClientSide && this.level().isDay()) {
            this.stopSleepInBed(false, true);
         }
      } else if (this.sleepCounter > 0) {
         this.sleepCounter++;
         if (this.sleepCounter >= 110) {
            this.sleepCounter = 0;
         }
      }

      this.updateIsUnderwater();
      super.tick();
      if (!this.level().isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      this.moveCloak();
      if (!this.level().isClientSide) {
         this.foodData.tick(this);
         this.awardStat(Stats.PLAY_TIME);
         this.awardStat(Stats.TOTAL_WORLD_TIME);
         if (this.isAlive()) {
            this.awardStat(Stats.TIME_SINCE_DEATH);
         }

         if (this.isDiscrete()) {
            this.awardStat(Stats.CROUCH_TIME);
         }

         if (!this.isSleeping()) {
            this.awardStat(Stats.TIME_SINCE_REST);
         }
      }

      int var1 = 29999999;
      double var2 = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
      double var4 = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
      if (var2 != this.getX() || var4 != this.getZ()) {
         this.setPos(var2, this.getY(), var4);
      }

      this.attackStrengthTicker++;
      ItemStack var6 = this.getMainHandItem();
      if (!ItemStack.matches(this.lastItemInMainHand, var6)) {
         if (!ItemStack.isSameItem(this.lastItemInMainHand, var6)) {
            this.resetAttackStrengthTicker();
         }

         this.lastItemInMainHand = var6.copy();
      }

      this.turtleHelmetTick();
      this.cooldowns.tick();
      this.updatePlayerPose();
      if (this.currentImpulseContextResetGraceTime > 0) {
         this.currentImpulseContextResetGraceTime--;
      }
   }

   @Override
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

   @Override
   public void onAboveBubbleCol(boolean var1) {
      if (!this.getAbilities().flying) {
         super.onAboveBubbleCol(var1);
      }
   }

   @Override
   public void onInsideBubbleColumn(boolean var1) {
      if (!this.getAbilities().flying) {
         super.onInsideBubbleColumn(var1);
      }
   }

   private void turtleHelmetTick() {
      ItemStack var1 = this.getItemBySlot(EquipmentSlot.HEAD);
      if (var1.is(Items.TURTLE_HELMET) && !this.isEyeInFluid(FluidTags.WATER)) {
         this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
      }
   }

   protected ItemCooldowns createItemCooldowns() {
      return new ItemCooldowns();
   }

   private void moveCloak() {
      this.xCloakO = this.xCloak;
      this.yCloakO = this.yCloak;
      this.zCloakO = this.zCloak;
      double var1 = this.getX() - this.xCloak;
      double var3 = this.getY() - this.yCloak;
      double var5 = this.getZ() - this.zCloak;
      double var7 = 10.0;
      if (var1 > 10.0) {
         this.xCloak = this.getX();
         this.xCloakO = this.xCloak;
      }

      if (var5 > 10.0) {
         this.zCloak = this.getZ();
         this.zCloakO = this.zCloak;
      }

      if (var3 > 10.0) {
         this.yCloak = this.getY();
         this.yCloakO = this.yCloak;
      }

      if (var1 < -10.0) {
         this.xCloak = this.getX();
         this.xCloakO = this.xCloak;
      }

      if (var5 < -10.0) {
         this.zCloak = this.getZ();
         this.zCloakO = this.zCloak;
      }

      if (var3 < -10.0) {
         this.yCloak = this.getY();
         this.yCloakO = this.yCloak;
      }

      this.xCloak += var1 * 0.25;
      this.zCloak += var5 * 0.25;
      this.yCloak += var3 * 0.25;
   }

   protected void updatePlayerPose() {
      if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.SWIMMING)) {
         Pose var1;
         if (this.isFallFlying()) {
            var1 = Pose.FALL_FLYING;
         } else if (this.isSleeping()) {
            var1 = Pose.SLEEPING;
         } else if (this.isSwimming()) {
            var1 = Pose.SWIMMING;
         } else if (this.isAutoSpinAttack()) {
            var1 = Pose.SPIN_ATTACK;
         } else if (this.isShiftKeyDown() && !this.abilities.flying) {
            var1 = Pose.CROUCHING;
         } else {
            var1 = Pose.STANDING;
         }

         Pose var2;
         if (this.isSpectator() || this.isPassenger() || this.canPlayerFitWithinBlocksAndEntitiesWhen(var1)) {
            var2 = var1;
         } else if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)) {
            var2 = Pose.CROUCHING;
         } else {
            var2 = Pose.SWIMMING;
         }

         this.setPose(var2);
      }
   }

   protected boolean canPlayerFitWithinBlocksAndEntitiesWhen(Pose var1) {
      return this.level().noCollision(this, this.getDimensions(var1).makeBoundingBox(this.position()).deflate(1.0E-7));
   }

   @Override
   protected SoundEvent getSwimSound() {
      return SoundEvents.PLAYER_SWIM;
   }

   @Override
   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.PLAYER_SPLASH;
   }

   @Override
   protected SoundEvent getSwimHighSpeedSplashSound() {
      return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
   }

   @Override
   public int getDimensionChangingDelay() {
      return 10;
   }

   @Override
   public void playSound(SoundEvent var1, float var2, float var3) {
      this.level().playSound(this, this.getX(), this.getY(), this.getZ(), var1, this.getSoundSource(), var2, var3);
   }

   public void playNotifySound(SoundEvent var1, SoundSource var2, float var3, float var4) {
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.PLAYERS;
   }

   @Override
   protected int getFireImmuneTicks() {
      return 20;
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 9) {
         this.completeUsingItem();
      } else if (var1 == 23) {
         this.reducedDebugInfo = false;
      } else if (var1 == 22) {
         this.reducedDebugInfo = true;
      } else {
         super.handleEntityEvent(var1);
      }
   }

   protected void closeContainer() {
      this.containerMenu = this.inventoryMenu;
   }

   protected void doCloseContainer() {
   }

   @Override
   public void rideTick() {
      if (!this.level().isClientSide && this.wantsToStopRiding() && this.isPassenger()) {
         this.stopRiding();
         this.setShiftKeyDown(false);
      } else {
         super.rideTick();
         this.oBob = this.bob;
         this.bob = 0.0F;
      }
   }

   @Override
   protected void serverAiStep() {
      super.serverAiStep();
      this.updateSwingTime();
      this.yHeadRot = this.getYRot();
   }

   @Override
   public void aiStep() {
      if (this.jumpTriggerTime > 0) {
         this.jumpTriggerTime--;
      }

      if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
         if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
            this.heal(1.0F);
         }

         if (this.foodData.getSaturationLevel() < 20.0F && this.tickCount % 20 == 0) {
            this.foodData.setSaturation(this.foodData.getSaturationLevel() + 1.0F);
         }

         if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
            this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
         }
      }

      this.inventory.tick();
      this.oBob = this.bob;
      if (this.abilities.flying && !this.isPassenger()) {
         this.resetFallDistance();
      }

      super.aiStep();
      this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
      float var1;
      if (this.onGround() && !this.isDeadOrDying() && !this.isSwimming()) {
         var1 = Math.min(0.1F, (float)this.getDeltaMovement().horizontalDistance());
      } else {
         var1 = 0.0F;
      }

      this.bob = this.bob + (var1 - this.bob) * 0.4F;
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         AABB var2;
         if (this.isPassenger() && !this.getVehicle().isRemoved()) {
            var2 = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0, 0.0, 1.0);
         } else {
            var2 = this.getBoundingBox().inflate(1.0, 0.5, 1.0);
         }

         List var3 = this.level().getEntities(this, var2);
         ArrayList var4 = Lists.newArrayList();

         for (Entity var6 : var3) {
            if (var6.getType() == EntityType.EXPERIENCE_ORB) {
               var4.add(var6);
            } else if (!var6.isRemoved()) {
               this.touch(var6);
            }
         }

         if (!var4.isEmpty()) {
            this.touch(Util.getRandom(var4, this.random));
         }
      }

      this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
      this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
      if (!this.level().isClientSide && (this.fallDistance > 0.5F || this.isInWater()) || this.abilities.flying || this.isSleeping() || this.isInPowderSnow) {
         this.removeEntitiesOnShoulder();
      }
   }

   private void playShoulderEntityAmbientSound(@Nullable CompoundTag var1) {
      if (var1 != null && (!var1.contains("Silent") || !var1.getBoolean("Silent")) && this.level().random.nextInt(200) == 0) {
         String var2 = var1.getString("id");
         EntityType.byString(var2)
            .filter(var0 -> var0 == EntityType.PARROT)
            .ifPresent(
               var1x -> {
                  if (!Parrot.imitateNearbyMobs(this.level(), this)) {
                     this.level()
                        .playSound(
                           null,
                           this.getX(),
                           this.getY(),
                           this.getZ(),
                           Parrot.getAmbient(this.level(), this.level().random),
                           this.getSoundSource(),
                           1.0F,
                           Parrot.getPitch(this.level().random)
                        );
                  }
               }
            );
      }
   }

   private void touch(Entity var1) {
      var1.playerTouch(this);
   }

   public int getScore() {
      return this.entityData.get(DATA_SCORE_ID);
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
      if (!this.level().isClientSide) {
         this.removeEntitiesOnShoulder();
         this.setLivingEntityFlag(4, true);
      }
   }

   @Nonnull
   @Override
   public ItemStack getWeaponItem() {
      return this.isAutoSpinAttack() && this.autoSpinAttackItemStack != null ? this.autoSpinAttackItemStack : super.getWeaponItem();
   }

   @Override
   public void die(DamageSource var1) {
      super.die(var1);
      this.reapplyPosition();
      if (!this.isSpectator() && this.level() instanceof ServerLevel var2) {
         this.dropAllDeathLoot(var2, var1);
      }

      if (var1 != null) {
         this.setDeltaMovement(
            (double)(-Mth.cos((this.getHurtDir() + this.getYRot()) * 0.017453292F) * 0.1F),
            0.10000000149011612,
            (double)(-Mth.sin((this.getHurtDir() + this.getYRot()) * 0.017453292F) * 0.1F)
         );
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

   @Override
   protected void dropEquipment() {
      super.dropEquipment();
      if (!this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
         this.destroyVanishingCursedItems();
         this.inventory.dropAll();
      }
   }

   protected void destroyVanishingCursedItems() {
      for (int var1 = 0; var1 < this.inventory.getContainerSize(); var1++) {
         ItemStack var2 = this.inventory.getItem(var1);
         if (!var2.isEmpty() && EnchantmentHelper.has(var2, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP)) {
            this.inventory.removeItemNoUpdate(var1);
         }
      }
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return var1.type().effects().sound();
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.PLAYER_DEATH;
   }

   public void handleCreativeModeItemDrop(ItemStack var1) {
   }

   @Nullable
   public ItemEntity drop(ItemStack var1, boolean var2) {
      return this.drop(var1, false, var2);
   }

   @Nullable
   public ItemEntity drop(ItemStack var1, boolean var2, boolean var3) {
      if (!var1.isEmpty() && this.level().isClientSide) {
         this.swing(InteractionHand.MAIN_HAND);
      }

      return null;
   }

   public float getDestroySpeed(BlockState var1) {
      float var2 = this.inventory.getDestroySpeed(var1);
      if (var2 > 1.0F) {
         var2 += (float)this.getAttributeValue(Attributes.MINING_EFFICIENCY);
      }

      if (MobEffectUtil.hasDigSpeed(this)) {
         var2 *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
      }

      if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
         float var3 = switch (this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
            case 0 -> 0.3F;
            case 1 -> 0.09F;
            case 2 -> 0.0027F;
            default -> 8.1E-4F;
         };
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
      return !var1.requiresCorrectToolForDrops() || this.inventory.getSelected().isCorrectToolForDrops(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setUUID(this.gameProfile.getId());
      ListTag var2 = var1.getList("Inventory", 10);
      this.inventory.load(var2);
      this.inventory.selected = var1.getInt("SelectedItemSlot");
      this.sleepCounter = var1.getShort("SleepTimer");
      this.experienceProgress = var1.getFloat("XpP");
      this.experienceLevel = var1.getInt("XpLevel");
      this.totalExperience = var1.getInt("XpTotal");
      this.enchantmentSeed = var1.getInt("XpSeed");
      if (this.enchantmentSeed == 0) {
         this.enchantmentSeed = this.random.nextInt();
      }

      this.setScore(var1.getInt("Score"));
      this.foodData.readAdditionalSaveData(var1);
      this.abilities.loadSaveData(var1);
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)this.abilities.getWalkingSpeed());
      if (var1.contains("EnderItems", 9)) {
         this.enderChestInventory.fromTag(var1.getList("EnderItems", 10), this.registryAccess());
      }

      if (var1.contains("ShoulderEntityLeft", 10)) {
         this.setShoulderEntityLeft(var1.getCompound("ShoulderEntityLeft"));
      }

      if (var1.contains("ShoulderEntityRight", 10)) {
         this.setShoulderEntityRight(var1.getCompound("ShoulderEntityRight"));
      }

      if (var1.contains("LastDeathLocation", 10)) {
         this.setLastDeathLocation(GlobalPos.CODEC.parse(NbtOps.INSTANCE, var1.get("LastDeathLocation")).resultOrPartial(LOGGER::error));
      }

      if (var1.contains("current_explosion_impact_pos", 9)) {
         Vec3.CODEC
            .parse(NbtOps.INSTANCE, var1.get("current_explosion_impact_pos"))
            .resultOrPartial(LOGGER::error)
            .ifPresent(var1x -> this.currentImpulseImpactPos = var1x);
      }

      this.ignoreFallDamageFromCurrentImpulse = var1.getBoolean("ignore_fall_damage_from_current_explosion");
      this.currentImpulseContextResetGraceTime = var1.getInt("current_impulse_context_reset_grace_time");
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      NbtUtils.addCurrentDataVersion(var1);
      var1.put("Inventory", this.inventory.save(new ListTag()));
      var1.putInt("SelectedItemSlot", this.inventory.selected);
      var1.putShort("SleepTimer", (short)this.sleepCounter);
      var1.putFloat("XpP", this.experienceProgress);
      var1.putInt("XpLevel", this.experienceLevel);
      var1.putInt("XpTotal", this.totalExperience);
      var1.putInt("XpSeed", this.enchantmentSeed);
      var1.putInt("Score", this.getScore());
      this.foodData.addAdditionalSaveData(var1);
      this.abilities.addSaveData(var1);
      var1.put("EnderItems", this.enderChestInventory.createTag(this.registryAccess()));
      if (!this.getShoulderEntityLeft().isEmpty()) {
         var1.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
      }

      if (!this.getShoulderEntityRight().isEmpty()) {
         var1.put("ShoulderEntityRight", this.getShoulderEntityRight());
      }

      this.getLastDeathLocation()
         .flatMap(var0 -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, var0).resultOrPartial(LOGGER::error))
         .ifPresent(var1x -> var1.put("LastDeathLocation", var1x));
      if (this.currentImpulseImpactPos != null) {
         var1.put("current_explosion_impact_pos", (Tag)Vec3.CODEC.encodeStart(NbtOps.INSTANCE, this.currentImpulseImpactPos).getOrThrow());
      }

      var1.putBoolean("ignore_fall_damage_from_current_explosion", this.ignoreFallDamageFromCurrentImpulse);
      var1.putInt("current_impulse_context_reset_grace_time", this.currentImpulseContextResetGraceTime);
   }

   @Override
   public boolean isInvulnerableTo(DamageSource var1) {
      if (super.isInvulnerableTo(var1)) {
         return true;
      } else if (var1.is(DamageTypeTags.IS_DROWNING)) {
         return !this.level().getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
      } else if (var1.is(DamageTypeTags.IS_FALL)) {
         return !this.level().getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
      } else if (var1.is(DamageTypeTags.IS_FIRE)) {
         return !this.level().getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
      } else {
         return var1.is(DamageTypeTags.IS_FREEZING) ? !this.level().getGameRules().getBoolean(GameRules.RULE_FREEZE_DAMAGE) : false;
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (this.abilities.invulnerable && !var1.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return false;
      } else {
         this.noActionTime = 0;
         if (this.isDeadOrDying()) {
            return false;
         } else {
            if (!this.level().isClientSide) {
               this.removeEntitiesOnShoulder();
            }

            if (var1.scalesWithDifficulty()) {
               if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
                  var2 = 0.0F;
               }

               if (this.level().getDifficulty() == Difficulty.EASY) {
                  var2 = Math.min(var2 / 2.0F + 1.0F, var2);
               }

               if (this.level().getDifficulty() == Difficulty.HARD) {
                  var2 = var2 * 3.0F / 2.0F;
               }
            }

            return var2 == 0.0F ? false : super.hurt(var1, var2);
         }
      }
   }

   @Override
   protected void blockUsingShield(LivingEntity var1) {
      super.blockUsingShield(var1);
      ItemStack var2 = this.getItemBlockingWith();
      if (var1.canDisableShield() && var2 != null) {
         this.disableShield(var2);
      }
   }

   @Override
   public boolean canBeSeenAsEnemy() {
      return !this.getAbilities().invulnerable && super.canBeSeenAsEnemy();
   }

   public boolean canHarmPlayer(Player var1) {
      PlayerTeam var2 = this.getTeam();
      PlayerTeam var3 = var1.getTeam();
      if (var2 == null) {
         return true;
      } else {
         return !var2.isAlliedTo(var3) ? true : var2.isAllowFriendlyFire();
      }
   }

   @Override
   protected void hurtArmor(DamageSource var1, float var2) {
      this.doHurtEquipment(var1, var2, new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD});
   }

   @Override
   protected void hurtHelmet(DamageSource var1, float var2) {
      this.doHurtEquipment(var1, var2, new EquipmentSlot[]{EquipmentSlot.HEAD});
   }

   @Override
   protected void hurtCurrentlyUsedShield(float var1) {
      if (this.useItem.is(Items.SHIELD)) {
         if (!this.level().isClientSide) {
            this.awardStat(Stats.ITEM_USED.get(this.useItem.getItem()));
         }

         if (var1 >= 3.0F) {
            int var2 = 1 + Mth.floor(var1);
            InteractionHand var3 = this.getUsedItemHand();
            this.useItem.hurtAndBreak(var2, this, getSlotForHand(var3));
            if (this.useItem.isEmpty()) {
               if (var3 == InteractionHand.MAIN_HAND) {
                  this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
               } else {
                  this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
               }

               this.useItem = ItemStack.EMPTY;
               this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
            }
         }
      }
   }

   @Override
   protected void actuallyHurt(DamageSource var1, float var2) {
      if (!this.isInvulnerableTo(var1)) {
         var2 = this.getDamageAfterArmorAbsorb(var1, var2);
         var2 = this.getDamageAfterMagicAbsorb(var1, var2);
         float var7 = Math.max(var2 - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (var2 - var7));
         float var4 = var2 - var7;
         if (var4 > 0.0F && var4 < 3.4028235E37F) {
            this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(var4 * 10.0F));
         }

         if (var7 != 0.0F) {
            this.causeFoodExhaustion(var1.getFoodExhaustion());
            this.getCombatTracker().recordDamage(var1, var7);
            this.setHealth(this.getHealth() - var7);
            if (var7 < 3.4028235E37F) {
               this.awardStat(Stats.DAMAGE_TAKEN, Math.round(var7 * 10.0F));
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

   public void openMinecartCommandBlock(BaseCommandBlock var1) {
   }

   public void openCommandBlock(CommandBlockEntity var1) {
   }

   public void openStructureBlock(StructureBlockEntity var1) {
   }

   public void openJigsawBlock(JigsawBlockEntity var1) {
   }

   public void openHorseInventory(AbstractHorse var1, Container var2) {
   }

   public OptionalInt openMenu(@Nullable MenuProvider var1) {
      return OptionalInt.empty();
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
            if (this.abilities.instabuild && var3 == this.getItemInHand(var2) && var3.getCount() < var4.getCount()) {
               var3.setCount(var4.getCount());
            }

            return var5;
         } else {
            if (!var3.isEmpty() && var1 instanceof LivingEntity) {
               if (this.abilities.instabuild) {
                  var3 = var4;
               }

               InteractionResult var6 = var3.interactLivingEntity(this, (LivingEntity)var1, var2);
               if (var6.consumesAction()) {
                  this.level().gameEvent(GameEvent.ENTITY_INTERACT, var1.position(), GameEvent.Context.of(this));
                  if (var3.isEmpty() && !this.abilities.instabuild) {
                     this.setItemInHand(var2, ItemStack.EMPTY);
                  }

                  return var6;
               }
            }

            return InteractionResult.PASS;
         }
      }
   }

   @Override
   public void removeVehicle() {
      super.removeVehicle();
      this.boardingCooldown = 0;
   }

   @Override
   protected boolean isImmobile() {
      return super.isImmobile() || this.isSleeping();
   }

   @Override
   public boolean isAffectedByFluids() {
      return !this.abilities.flying;
   }

   @Override
   protected Vec3 maybeBackOffFromEdge(Vec3 var1, MoverType var2) {
      float var3 = this.maxUpStep();
      if (!this.abilities.flying
         && !(var1.y > 0.0)
         && (var2 == MoverType.SELF || var2 == MoverType.PLAYER)
         && this.isStayingOnGroundSurface()
         && this.isAboveGround(var3)) {
         double var4 = var1.x;
         double var6 = var1.z;
         double var8 = 0.05;
         double var10 = Math.signum(var4) * 0.05;

         double var12;
         for (var12 = Math.signum(var6) * 0.05; var4 != 0.0 && this.canFallAtLeast(var4, 0.0, var3); var4 -= var10) {
            if (Math.abs(var4) <= 0.05) {
               var4 = 0.0;
               break;
            }
         }

         while (var6 != 0.0 && this.canFallAtLeast(0.0, var6, var3)) {
            if (Math.abs(var6) <= 0.05) {
               var6 = 0.0;
               break;
            }

            var6 -= var12;
         }

         while (var4 != 0.0 && var6 != 0.0 && this.canFallAtLeast(var4, var6, var3)) {
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
      return this.onGround() || this.fallDistance < var1 && !this.canFallAtLeast(0.0, 0.0, var1 - this.fallDistance);
   }

   private boolean canFallAtLeast(double var1, double var3, float var5) {
      AABB var6 = this.getBoundingBox();
      return this.level()
         .noCollision(
            this, new AABB(var6.minX + var1, var6.minY - (double)var5 - 9.999999747378752E-6, var6.minZ + var3, var6.maxX + var1, var6.minY, var6.maxZ + var3)
         );
   }

   public void attack(Entity var1) {
      if (var1.isAttackable()) {
         if (!var1.skipAttackInteraction(this)) {
            float var2 = this.isAutoSpinAttack() ? this.autoSpinAttackDmg : (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
            ItemStack var3 = this.getWeaponItem();
            DamageSource var4;
            if (var3.getItem() == Items.MACE && MaceItem.canSmashAttack(this)) {
               var4 = this.damageSources().mace(this);
            } else {
               var4 = this.damageSources().playerAttack(this);
            }

            float var5 = this.getEnchantedDamage(var1, var2, var4) - var2;
            float var6 = this.getAttackStrengthScale(0.5F);
            var2 *= 0.2F + var6 * var6 * 0.8F;
            var5 *= var6;
            this.resetAttackStrengthTicker();
            if (var1.getType().is(EntityTypeTags.REDIRECTABLE_PROJECTILE)
               && var1 instanceof Projectile var7
               && var7.deflect(ProjectileDeflection.AIM_DEFLECT, this, this, true)) {
               this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource());
               return;
            }

            if (var2 > 0.0F || var5 > 0.0F) {
               boolean var26 = var6 > 0.9F;
               boolean var8;
               if (this.isSprinting() && var26) {
                  this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                  var8 = true;
               } else {
                  var8 = false;
               }

               var2 += var3.getItem().getAttackDamageBonus(var1, var2, var4);
               boolean var9 = var26
                  && this.fallDistance > 0.0F
                  && !this.onGround()
                  && !this.onClimbable()
                  && !this.isInWater()
                  && !this.hasEffect(MobEffects.BLINDNESS)
                  && !this.isPassenger()
                  && var1 instanceof LivingEntity
                  && !this.isSprinting();
               if (var9) {
                  var2 *= 1.5F;
               }

               float var10 = var2 + var5;
               boolean var11 = false;
               if (var26 && !var9 && !var8 && this.onGround()) {
                  double var12 = this.getKnownMovement().horizontalDistanceSqr();
                  double var14 = (double)this.getSpeed() * 2.5;
                  if (var12 < Mth.square(var14) && this.getItemInHand(InteractionHand.MAIN_HAND).is(ItemTags.SWORDS)) {
                     var11 = true;
                  }
               }

               float var27 = 0.0F;
               if (var1 instanceof LivingEntity var13) {
                  var27 = var13.getHealth();
               }

               Vec3 var28 = var1.getDeltaMovement();
               boolean var29 = var1.hurt(var4, var10);
               if (var29) {
                  float var15 = this.getKnockback(var1, var4) + (var8 ? 1.0F : 0.0F);
                  if (var15 > 0.0F) {
                     if (var1 instanceof LivingEntity var16) {
                        var16.knockback(
                           (double)(var15 * 0.5F), (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F))
                        );
                     } else {
                        var1.push(
                           (double)(-Mth.sin(this.getYRot() * 0.017453292F) * var15 * 0.5F),
                           0.1,
                           (double)(Mth.cos(this.getYRot() * 0.017453292F) * var15 * 0.5F)
                        );
                     }

                     this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                     this.setSprinting(false);
                  }

                  if (var11) {
                     float var30 = 1.0F + (float)this.getAttributeValue(Attributes.SWEEPING_DAMAGE_RATIO) * var2;

                     for (LivingEntity var19 : this.level().getEntitiesOfClass(LivingEntity.class, var1.getBoundingBox().inflate(1.0, 0.25, 1.0))) {
                        if (var19 != this
                           && var19 != var1
                           && !this.isAlliedTo(var19)
                           && (!(var19 instanceof ArmorStand) || !((ArmorStand)var19).isMarker())
                           && this.distanceToSqr(var19) < 9.0) {
                           float var20 = this.getEnchantedDamage(var19, var30, var4) * var6;
                           var19.knockback(
                              0.4000000059604645, (double)Mth.sin(this.getYRot() * 0.017453292F), (double)(-Mth.cos(this.getYRot() * 0.017453292F))
                           );
                           var19.hurt(var4, var20);
                           if (this.level() instanceof ServerLevel var21) {
                              EnchantmentHelper.doPostAttackEffects(var21, var19, var4);
                           }
                        }
                     }

                     this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                     this.sweepAttack();
                  }

                  if (var1 instanceof ServerPlayer && var1.hurtMarked) {
                     ((ServerPlayer)var1).connection.send(new ClientboundSetEntityMotionPacket(var1));
                     var1.hurtMarked = false;
                     var1.setDeltaMovement(var28);
                  }

                  if (var9) {
                     this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                     this.crit(var1);
                  }

                  if (!var9 && !var11) {
                     if (var26) {
                        this.level()
                           .playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                     } else {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                     }
                  }

                  if (var5 > 0.0F) {
                     this.magicCrit(var1);
                  }

                  this.setLastHurtMob(var1);
                  Object var31 = var1;
                  if (var1 instanceof EnderDragonPart) {
                     var31 = ((EnderDragonPart)var1).parentMob;
                  }

                  boolean var32 = false;
                  if (this.level() instanceof ServerLevel var33) {
                     if (var31 instanceof LivingEntity var36) {
                        var32 = var3.hurtEnemy(var36, this);
                     }

                     EnchantmentHelper.doPostAttackEffects(var33, var1, var4);
                  }

                  if (!this.level().isClientSide && !var3.isEmpty() && var31 instanceof LivingEntity) {
                     if (var32) {
                        var3.postHurtEnemy((LivingEntity)var31, this);
                     }

                     if (var3.isEmpty()) {
                        if (var3 == this.getMainHandItem()) {
                           this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        } else {
                           this.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                        }
                     }
                  }

                  if (var1 instanceof LivingEntity) {
                     float var34 = var27 - ((LivingEntity)var1).getHealth();
                     this.awardStat(Stats.DAMAGE_DEALT, Math.round(var34 * 10.0F));
                     if (this.level() instanceof ServerLevel && var34 > 2.0F) {
                        int var37 = (int)((double)var34 * 0.5);
                        ((ServerLevel)this.level())
                           .sendParticles(ParticleTypes.DAMAGE_INDICATOR, var1.getX(), var1.getY(0.5), var1.getZ(), var37, 0.1, 0.0, 0.1, 0.2);
                     }
                  }

                  this.causeFoodExhaustion(0.1F);
               } else {
                  this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
               }
            }
         }
      }
   }

   protected float getEnchantedDamage(Entity var1, float var2, DamageSource var3) {
      return var2;
   }

   @Override
   protected void doAutoAttackOnTouch(LivingEntity var1) {
      this.attack(var1);
   }

   public void disableShield(ItemStack var1) {
      this.getCooldowns().addCooldown(var1, 100);
      this.stopUsingItem();
      this.level().broadcastEntityEvent(this, (byte)30);
   }

   public void crit(Entity var1) {
   }

   public void magicCrit(Entity var1) {
   }

   public void sweepAttack() {
      double var1 = (double)(-Mth.sin(this.getYRot() * 0.017453292F));
      double var3 = (double)Mth.cos(this.getYRot() * 0.017453292F);
      if (this.level() instanceof ServerLevel) {
         ((ServerLevel)this.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + var1, this.getY(0.5), this.getZ() + var3, 0, var1, 0.0, var3, 0.0);
      }
   }

   public void respawn() {
   }

   @Override
   public void remove(Entity.RemovalReason var1) {
      super.remove(var1);
      this.inventoryMenu.removed(this);
      if (this.containerMenu != null && this.hasContainerOpen()) {
         this.doCloseContainer();
      }
   }

   public boolean isLocalPlayer() {
      return false;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   public Abilities getAbilities() {
      return this.abilities;
   }

   @Override
   public boolean hasInfiniteMaterials() {
      return this.abilities.instabuild;
   }

   public void updateTutorialInventoryAction(ItemStack var1, ItemStack var2, ClickAction var3) {
   }

   public boolean hasContainerOpen() {
      return this.containerMenu != this.inventoryMenu;
   }

   public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos var1) {
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

   @Override
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
      this.awardStat(var1, 1);
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

   public void awardRecipesByKey(List<ResourceLocation> var1) {
   }

   public int resetRecipes(Collection<RecipeHolder<?>> var1) {
      return 0;
   }

   @Override
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

   @Override
   protected boolean canGlide() {
      return !this.abilities.flying && super.canGlide();
   }

   @Override
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

   @Override
   public float getSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
   }

   @Override
   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      if (this.abilities.mayfly) {
         return false;
      } else {
         if (var1 >= 2.0F) {
            this.awardStat(Stats.FALL_ONE_CM, (int)Math.round((double)var1 * 100.0));
         }

         boolean var5 = this.currentImpulseImpactPos != null && this.ignoreFallDamageFromCurrentImpulse;
         float var4;
         if (var5) {
            var4 = Math.min(var1, (float)(this.currentImpulseImpactPos.y - this.getY()));
            boolean var6 = var4 <= 0.0F;
            if (var6) {
               this.resetCurrentImpulseContext();
            } else {
               this.tryResetCurrentImpulseContext();
            }
         } else {
            var4 = var1;
         }

         if (var4 > 0.0F && super.causeFallDamage(var4, var2, var3)) {
            this.resetCurrentImpulseContext();
            return true;
         } else {
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

   public void stopFallFlying() {
      this.setSharedFlag(7, true);
      this.setSharedFlag(7, false);
   }

   @Override
   protected void doWaterSplashEffect() {
      if (!this.isSpectator()) {
         super.doWaterSplashEffect();
      }
   }

   @Override
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

   @Override
   public LivingEntity.Fallsounds getFallSounds() {
      return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
   }

   @Override
   public boolean killedEntity(ServerLevel var1, LivingEntity var2) {
      this.awardStat(Stats.ENTITY_KILLED.get(var2.getType()));
      return true;
   }

   @Override
   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
      if (!this.abilities.flying) {
         super.makeStuckInBlock(var1, var2);
      }

      this.tryResetCurrentImpulseContext();
   }

   public void giveExperiencePoints(int var1) {
      this.increaseScore(var1);
      this.experienceProgress = this.experienceProgress + (float)var1 / (float)this.getXpNeededForNextLevel();
      this.totalExperience = Mth.clamp(this.totalExperience + var1, 0, 2147483647);

      while (this.experienceProgress < 0.0F) {
         float var2 = this.experienceProgress * (float)this.getXpNeededForNextLevel();
         if (this.experienceLevel > 0) {
            this.giveExperienceLevels(-1);
            this.experienceProgress = 1.0F + var2 / (float)this.getXpNeededForNextLevel();
         } else {
            this.giveExperienceLevels(-1);
            this.experienceProgress = 0.0F;
         }
      }

      while (this.experienceProgress >= 1.0F) {
         this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getXpNeededForNextLevel();
         this.giveExperienceLevels(1);
         this.experienceProgress = this.experienceProgress / (float)this.getXpNeededForNextLevel();
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
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), var2 * 0.75F, 1.0F);
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
         if (!this.level().isClientSide) {
            this.foodData.addExhaustion(var1);
         }
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

   @Override
   protected int getBaseExperienceReward() {
      return !this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator() ? Math.min(this.experienceLevel * 7, 100) : 0;
   }

   @Override
   protected boolean isAlwaysExperienceDropper() {
      return true;
   }

   @Override
   public boolean shouldShowName() {
      return true;
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return this.abilities.flying || this.onGround() && this.isDiscrete() ? Entity.MovementEmission.NONE : Entity.MovementEmission.ALL;
   }

   public void onUpdateAbilities() {
   }

   @Override
   public Component getName() {
      return Component.literal(this.gameProfile.getName());
   }

   public PlayerEnderChestContainer getEnderChestInventory() {
      return this.enderChestInventory;
   }

   @Override
   public ItemStack getItemBySlot(EquipmentSlot var1) {
      if (var1 == EquipmentSlot.MAINHAND) {
         return this.inventory.getSelected();
      } else if (var1 == EquipmentSlot.OFFHAND) {
         return (ItemStack)this.inventory.offhand.getFirst();
      } else {
         return var1.getType() == EquipmentSlot.Type.HUMANOID_ARMOR ? this.inventory.armor.get(var1.getIndex()) : ItemStack.EMPTY;
      }
   }

   @Override
   protected boolean doesEmitEquipEvent(EquipmentSlot var1) {
      return var1.getType() == EquipmentSlot.Type.HUMANOID_ARMOR;
   }

   @Override
   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
      this.verifyEquippedItem(var2);
      if (var1 == EquipmentSlot.MAINHAND) {
         this.onEquipItem(var1, this.inventory.items.set(this.inventory.selected, var2), var2);
      } else if (var1 == EquipmentSlot.OFFHAND) {
         this.onEquipItem(var1, this.inventory.offhand.set(0, var2), var2);
      } else if (var1.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
         this.onEquipItem(var1, this.inventory.armor.set(var1.getIndex(), var2), var2);
      }
   }

   public boolean addItem(ItemStack var1) {
      return this.inventory.add(var1);
   }

   @Override
   public Iterable<ItemStack> getHandSlots() {
      return Lists.newArrayList(new ItemStack[]{this.getMainHandItem(), this.getOffhandItem()});
   }

   @Override
   public Iterable<ItemStack> getArmorSlots() {
      return this.inventory.armor;
   }

   @Override
   public boolean canUseSlot(EquipmentSlot var1) {
      return var1 != EquipmentSlot.BODY;
   }

   public boolean setEntityOnShoulder(CompoundTag var1) {
      if (this.isPassenger() || !this.onGround() || this.isInWater() || this.isInPowderSnow) {
         return false;
      } else if (this.getShoulderEntityLeft().isEmpty()) {
         this.setShoulderEntityLeft(var1);
         this.timeEntitySatOnShoulder = this.level().getGameTime();
         return true;
      } else if (this.getShoulderEntityRight().isEmpty()) {
         this.setShoulderEntityRight(var1);
         this.timeEntitySatOnShoulder = this.level().getGameTime();
         return true;
      } else {
         return false;
      }
   }

   protected void removeEntitiesOnShoulder() {
      if (this.timeEntitySatOnShoulder + 20L < this.level().getGameTime()) {
         this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
         this.setShoulderEntityLeft(new CompoundTag());
         this.respawnEntityOnShoulder(this.getShoulderEntityRight());
         this.setShoulderEntityRight(new CompoundTag());
      }
   }

   private void respawnEntityOnShoulder(CompoundTag var1) {
      if (!this.level().isClientSide && !var1.isEmpty()) {
         EntityType.create(var1, this.level(), EntitySpawnReason.LOAD).ifPresent(var1x -> {
            if (var1x instanceof TamableAnimal) {
               ((TamableAnimal)var1x).setOwnerUUID(this.uuid);
            }

            var1x.setPos(this.getX(), this.getY() + 0.699999988079071, this.getZ());
            ((ServerLevel)this.level()).addWithUUID(var1x);
         });
      }
   }

   @Override
   public abstract boolean isSpectator();

   @Override
   public boolean canBeHitByProjectile() {
      return !this.isSpectator() && super.canBeHitByProjectile();
   }

   @Override
   public boolean isSwimming() {
      return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
   }

   public abstract boolean isCreative();

   @Override
   public boolean isPushedByFluid() {
      return !this.abilities.flying;
   }

   public Scoreboard getScoreboard() {
      return this.level().getScoreboard();
   }

   @Override
   public Component getDisplayName() {
      MutableComponent var1 = PlayerTeam.formatNameForTeam(this.getTeam(), this.getName());
      return this.decorateDisplayNameComponent(var1);
   }

   private MutableComponent decorateDisplayNameComponent(MutableComponent var1) {
      String var2 = this.getGameProfile().getName();
      return var1.withStyle(
         var2x -> var2x.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + var2 + " "))
               .withHoverEvent(this.createHoverEvent())
               .withInsertion(var2)
      );
   }

   @Override
   public String getScoreboardName() {
      return this.getGameProfile().getName();
   }

   @Override
   protected void internalSetAbsorptionAmount(float var1) {
      this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, var1);
   }

   @Override
   public float getAbsorptionAmount() {
      return this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID);
   }

   public boolean isModelPartShown(PlayerModelPart var1) {
      return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & var1.getMask()) == var1.getMask();
   }

   @Override
   public SlotAccess getSlot(int var1) {
      if (var1 == 499) {
         return new SlotAccess() {
            @Override
            public ItemStack get() {
               return Player.this.containerMenu.getCarried();
            }

            @Override
            public boolean set(ItemStack var1) {
               Player.this.containerMenu.setCarried(var1);
               return true;
            }
         };
      } else {
         final int var2 = var1 - 500;
         if (var2 >= 0 && var2 < 4) {
            return new SlotAccess() {
               @Override
               public ItemStack get() {
                  return Player.this.inventoryMenu.getCraftSlots().getItem(var2);
               }

               @Override
               public boolean set(ItemStack var1) {
                  Player.this.inventoryMenu.getCraftSlots().setItem(var2, var1);
                  Player.this.inventoryMenu.slotsChanged(Player.this.inventory);
                  return true;
               }
            };
         } else if (var1 >= 0 && var1 < this.inventory.items.size()) {
            return SlotAccess.forContainer(this.inventory, var1);
         } else {
            int var3 = var1 - 200;
            return var3 >= 0 && var3 < this.enderChestInventory.getContainerSize()
               ? SlotAccess.forContainer(this.enderChestInventory, var3)
               : super.getSlot(var1);
         }
      }
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public void setReducedDebugInfo(boolean var1) {
      this.reducedDebugInfo = var1;
   }

   @Override
   public void setRemainingFireTicks(int var1) {
      super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min(var1, 1) : var1);
   }

   @Override
   public HumanoidArm getMainArm() {
      return this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
   }

   public void setMainArm(HumanoidArm var1) {
      this.entityData.set(DATA_PLAYER_MAIN_HAND, (byte)(var1 == HumanoidArm.LEFT ? 0 : 1));
   }

   public CompoundTag getShoulderEntityLeft() {
      return this.entityData.get(DATA_SHOULDER_LEFT);
   }

   protected void setShoulderEntityLeft(CompoundTag var1) {
      this.entityData.set(DATA_SHOULDER_LEFT, var1);
   }

   public CompoundTag getShoulderEntityRight() {
      return this.entityData.get(DATA_SHOULDER_RIGHT);
   }

   protected void setShoulderEntityRight(CompoundTag var1) {
      this.entityData.set(DATA_SHOULDER_RIGHT, var1);
   }

   public float getCurrentItemAttackStrengthDelay() {
      return (float)(1.0 / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0);
   }

   public float getAttackStrengthScale(float var1) {
      return Mth.clamp(((float)this.attackStrengthTicker + var1) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
   }

   public void resetAttackStrengthTicker() {
      this.attackStrengthTicker = 0;
   }

   public ItemCooldowns getCooldowns() {
      return this.cooldowns;
   }

   @Override
   protected float getBlockSpeedFactor() {
      return !this.abilities.flying && !this.isFallFlying() ? super.getBlockSpeedFactor() : 1.0F;
   }

   public float getLuck() {
      return (float)this.getAttributeValue(Attributes.LUCK);
   }

   public boolean canUseGameMasterBlocks() {
      return this.abilities.instabuild && this.getPermissionLevel() >= 2;
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      return POSES.getOrDefault(var1, STANDING_DIMENSIONS);
   }

   @Override
   public ImmutableList<Pose> getDismountPoses() {
      return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
   }

   @Override
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

            for (int var4 = 0; var4 < this.inventory.getContainerSize(); var4++) {
               ItemStack var5 = this.inventory.getItem(var4);
               if (var2.test(var5)) {
                  return var5;
               }
            }

            return this.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
         }
      }
   }

   @Override
   public Vec3 getRopeHoldPosition(float var1) {
      double var2 = 0.22 * (this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0);
      float var4 = Mth.lerp(var1 * 0.5F, this.getXRot(), this.xRotO) * 0.017453292F;
      float var5 = Mth.lerp(var1, this.yBodyRotO, this.yBodyRot) * 0.017453292F;
      if (this.isFallFlying() || this.isAutoSpinAttack()) {
         Vec3 var17 = this.getViewVector(var1);
         Vec3 var7 = this.getDeltaMovement();
         double var18 = var7.horizontalDistanceSqr();
         double var10 = var17.horizontalDistanceSqr();
         float var12;
         if (var18 > 0.0 && var10 > 0.0) {
            double var13 = (var7.x * var17.x + var7.z * var17.z) / Math.sqrt(var18 * var10);
            double var15 = var7.x * var17.z - var7.z * var17.x;
            var12 = (float)(Math.signum(var15) * Math.acos(var13));
         } else {
            var12 = 0.0F;
         }

         return this.getPosition(var1).add(new Vec3(var2, -0.11, 0.85).zRot(-var12).xRot(-var4).yRot(-var5));
      } else if (this.isVisuallySwimming()) {
         return this.getPosition(var1).add(new Vec3(var2, 0.2, -0.15).xRot(-var4).yRot(-var5));
      } else {
         double var6 = this.getBoundingBox().getYsize() - 1.0;
         double var8 = this.isCrouching() ? -0.2 : 0.07;
         return this.getPosition(var1).add(new Vec3(var2, var6, var8).yRot(-var5));
      }
   }

   @Override
   public boolean isAlwaysTicking() {
      return true;
   }

   public boolean isScoping() {
      return this.isUsingItem() && this.getUseItem().is(Items.SPYGLASS);
   }

   @Override
   public boolean shouldBeSaved() {
      return false;
   }

   public Optional<GlobalPos> getLastDeathLocation() {
      return this.lastDeathLocation;
   }

   public void setLastDeathLocation(Optional<GlobalPos> var1) {
      this.lastDeathLocation = var1;
   }

   @Override
   public float getHurtDir() {
      return this.hurtDir;
   }

   @Override
   public void animateHurt(float var1) {
      super.animateHurt(var1);
      this.hurtDir = var1;
   }

   @Override
   public boolean canSprint() {
      return true;
   }

   @Override
   protected float getFlyingSpeed() {
      if (this.abilities.flying && !this.isPassenger()) {
         return this.isSprinting() ? this.abilities.getFlyingSpeed() * 2.0F : this.abilities.getFlyingSpeed();
      } else {
         return this.isSprinting() ? 0.025999999F : 0.02F;
      }
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
      return new AABB(var1).distanceToSqr(this.getEyePosition()) < var4 * var4;
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

   public static enum BedSleepingProblem {
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW(Component.translatable("block.minecraft.bed.no_sleep")),
      TOO_FAR_AWAY(Component.translatable("block.minecraft.bed.too_far_away")),
      OBSTRUCTED(Component.translatable("block.minecraft.bed.obstructed")),
      OTHER_PROBLEM,
      NOT_SAFE(Component.translatable("block.minecraft.bed.not_safe"));

      @Nullable
      private final Component message;

      private BedSleepingProblem() {
         this.message = null;
      }

      private BedSleepingProblem(final Component nullxx) {
         this.message = nullxx;
      }

      @Nullable
      public Component getMessage() {
         return this.message;
      }
   }
}
