package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public abstract class Player extends LivingEntity {
   public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F);
   private static final Map<Pose, EntityDimensions> POSES;
   private static final EntityDataAccessor<Float> DATA_PLAYER_ABSORPTION_ID;
   private static final EntityDataAccessor<Integer> DATA_SCORE_ID;
   protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION;
   protected static final EntityDataAccessor<Byte> DATA_PLAYER_MAIN_HAND;
   protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_LEFT;
   protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_RIGHT;
   private long timeEntitySatOnShoulder;
   public final Inventory inventory = new Inventory(this);
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
   private BlockPos respawnPosition;
   private boolean respawnForced;
   public final Abilities abilities = new Abilities();
   public int experienceLevel;
   public int totalExperience;
   public float experienceProgress;
   protected int enchantmentSeed;
   protected final float defaultFlySpeed = 0.02F;
   private int lastLevelUpTime;
   private final GameProfile gameProfile;
   private boolean reducedDebugInfo;
   private ItemStack lastItemInMainHand;
   private final ItemCooldowns cooldowns;
   @Nullable
   public FishingHook fishing;

   public Player(Level var1, GameProfile var2) {
      super(EntityType.PLAYER, var1);
      this.lastItemInMainHand = ItemStack.EMPTY;
      this.cooldowns = this.createItemCooldowns();
      this.setUUID(createPlayerUUID(var2));
      this.gameProfile = var2;
      this.inventoryMenu = new InventoryMenu(this.inventory, !var1.isClientSide, this);
      this.containerMenu = this.inventoryMenu;
      BlockPos var3 = var1.getSharedSpawnPos();
      this.moveTo((double)var3.getX() + 0.5D, (double)(var3.getY() + 1), (double)var3.getZ() + 0.5D, 0.0F, 0.0F);
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
         return var4.isEmpty() || !var4.hasAdventureModeBreakTagForBlock(var1.getTagManager(), new BlockInWorld(var1, var2, false));
      }
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.10000000149011612D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.LUCK);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_PLAYER_ABSORPTION_ID, 0.0F);
      this.entityData.define(DATA_SCORE_ID, 0);
      this.entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
      this.entityData.define(DATA_PLAYER_MAIN_HAND, (byte)1);
      this.entityData.define(DATA_SHOULDER_LEFT, new CompoundTag());
      this.entityData.define(DATA_SHOULDER_RIGHT, new CompoundTag());
   }

   public void tick() {
      this.noPhysics = this.isSpectator();
      if (this.isSpectator()) {
         this.onGround = false;
      }

      if (this.takeXpDelay > 0) {
         --this.takeXpDelay;
      }

      if (this.isSleeping()) {
         ++this.sleepCounter;
         if (this.sleepCounter > 100) {
            this.sleepCounter = 100;
         }

         if (!this.level.isClientSide && this.level.isDay()) {
            this.stopSleepInBed(false, true, true);
         }
      } else if (this.sleepCounter > 0) {
         ++this.sleepCounter;
         if (this.sleepCounter >= 110) {
            this.sleepCounter = 0;
         }
      }

      this.updateIsUnderwater();
      super.tick();
      if (!this.level.isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      if (this.isOnFire() && this.abilities.invulnerable) {
         this.clearFire();
      }

      this.moveCloak();
      if (!this.level.isClientSide) {
         this.foodData.tick(this);
         this.awardStat(Stats.PLAY_ONE_MINUTE);
         if (this.isAlive()) {
            this.awardStat(Stats.TIME_SINCE_DEATH);
         }

         if (this.isSneaking()) {
            this.awardStat(Stats.SNEAK_TIME);
         }

         if (!this.isSleeping()) {
            this.awardStat(Stats.TIME_SINCE_REST);
         }
      }

      int var1 = 29999999;
      double var2 = Mth.clamp(this.x, -2.9999999E7D, 2.9999999E7D);
      double var4 = Mth.clamp(this.z, -2.9999999E7D, 2.9999999E7D);
      if (var2 != this.x || var4 != this.z) {
         this.setPos(var2, this.y, var4);
      }

      ++this.attackStrengthTicker;
      ItemStack var6 = this.getMainHandItem();
      if (!ItemStack.matches(this.lastItemInMainHand, var6)) {
         if (!ItemStack.isSameIgnoreDurability(this.lastItemInMainHand, var6)) {
            this.resetAttackStrengthTicker();
         }

         this.lastItemInMainHand = var6.isEmpty() ? ItemStack.EMPTY : var6.copy();
      }

      this.turtleHelmetTick();
      this.cooldowns.tick();
      this.updatePlayerPose();
   }

   protected boolean updateIsUnderwater() {
      this.wasUnderwater = this.isUnderLiquid(FluidTags.WATER, true);
      return this.wasUnderwater;
   }

   private void turtleHelmetTick() {
      ItemStack var1 = this.getItemBySlot(EquipmentSlot.HEAD);
      if (var1.getItem() == Items.TURTLE_HELMET && !this.isUnderLiquid(FluidTags.WATER)) {
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
      double var1 = this.x - this.xCloak;
      double var3 = this.y - this.yCloak;
      double var5 = this.z - this.zCloak;
      double var7 = 10.0D;
      if (var1 > 10.0D) {
         this.xCloak = this.x;
         this.xCloakO = this.xCloak;
      }

      if (var5 > 10.0D) {
         this.zCloak = this.z;
         this.zCloakO = this.zCloak;
      }

      if (var3 > 10.0D) {
         this.yCloak = this.y;
         this.yCloakO = this.yCloak;
      }

      if (var1 < -10.0D) {
         this.xCloak = this.x;
         this.xCloakO = this.xCloak;
      }

      if (var5 < -10.0D) {
         this.zCloak = this.z;
         this.zCloakO = this.zCloak;
      }

      if (var3 < -10.0D) {
         this.yCloak = this.y;
         this.yCloakO = this.yCloak;
      }

      this.xCloak += var1 * 0.25D;
      this.zCloak += var5 * 0.25D;
      this.yCloak += var3 * 0.25D;
   }

   protected void updatePlayerPose() {
      if (this.canEnterPose(Pose.SWIMMING)) {
         Pose var1;
         if (this.isFallFlying()) {
            var1 = Pose.FALL_FLYING;
         } else if (this.isSleeping()) {
            var1 = Pose.SLEEPING;
         } else if (this.isSwimming()) {
            var1 = Pose.SWIMMING;
         } else if (this.isAutoSpinAttack()) {
            var1 = Pose.SPIN_ATTACK;
         } else if (this.isSneaking() && !this.abilities.flying) {
            var1 = Pose.SNEAKING;
         } else {
            var1 = Pose.STANDING;
         }

         Pose var2;
         if (!this.isSpectator() && !this.isPassenger() && !this.canEnterPose(var1)) {
            if (this.canEnterPose(Pose.SNEAKING)) {
               var2 = Pose.SNEAKING;
            } else {
               var2 = Pose.SWIMMING;
            }
         } else {
            var2 = var1;
         }

         this.setPose(var2);
      }
   }

   public int getPortalWaitTime() {
      return this.abilities.invulnerable ? 1 : 80;
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
      this.level.playSound(this, this.x, this.y, this.z, var1, this.getSoundSource(), var2, var3);
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
         this.reducedDebugInfo = false;
      } else if (var1 == 22) {
         this.reducedDebugInfo = true;
      } else if (var1 == 43) {
         this.addParticlesAroundSelf(ParticleTypes.CLOUD);
      } else {
         super.handleEntityEvent(var1);
      }

   }

   private void addParticlesAroundSelf(ParticleOptions var1) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.random.nextGaussian() * 0.02D;
         double var5 = this.random.nextGaussian() * 0.02D;
         double var7 = this.random.nextGaussian() * 0.02D;
         this.level.addParticle(var1, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 1.0D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var3, var5, var7);
      }

   }

   protected void closeContainer() {
      this.containerMenu = this.inventoryMenu;
   }

   public void rideTick() {
      if (!this.level.isClientSide && this.isSneaking() && this.isPassenger()) {
         this.stopRiding();
         this.setSneaking(false);
      } else {
         double var1 = this.x;
         double var3 = this.y;
         double var5 = this.z;
         float var7 = this.yRot;
         float var8 = this.xRot;
         super.rideTick();
         this.oBob = this.bob;
         this.bob = 0.0F;
         this.checkRidingStatistiscs(this.x - var1, this.y - var3, this.z - var5);
         if (this.getVehicle() instanceof Pig) {
            this.xRot = var8;
            this.yRot = var7;
            this.yBodyRot = ((Pig)this.getVehicle()).yBodyRot;
         }

      }
   }

   public void resetPos() {
      this.setPose(Pose.STANDING);
      super.resetPos();
      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   protected void serverAiStep() {
      super.serverAiStep();
      this.updateSwingTime();
      this.yHeadRot = this.yRot;
   }

   public void aiStep() {
      if (this.jumpTriggerTime > 0) {
         --this.jumpTriggerTime;
      }

      if (this.level.getDifficulty() == Difficulty.PEACEFUL && this.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
         if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
            this.heal(1.0F);
         }

         if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
            this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
         }
      }

      this.inventory.tick();
      this.oBob = this.bob;
      super.aiStep();
      AttributeInstance var1 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (!this.level.isClientSide) {
         var1.setBaseValue((double)this.abilities.getWalkingSpeed());
      }

      this.flyingSpeed = 0.02F;
      if (this.isSprinting()) {
         this.flyingSpeed = (float)((double)this.flyingSpeed + 0.005999999865889549D);
      }

      this.setSpeed((float)var1.getValue());
      float var2;
      if (this.onGround && this.getHealth() > 0.0F && !this.isSwimming()) {
         var2 = Math.min(0.1F, Mth.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement())));
      } else {
         var2 = 0.0F;
      }

      this.bob += (var2 - this.bob) * 0.4F;
      if (this.getHealth() > 0.0F && !this.isSpectator()) {
         AABB var3;
         if (this.isPassenger() && !this.getVehicle().removed) {
            var3 = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0D, 0.0D, 1.0D);
         } else {
            var3 = this.getBoundingBox().inflate(1.0D, 0.5D, 1.0D);
         }

         List var4 = this.level.getEntities(this, var3);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            Entity var6 = (Entity)var4.get(var5);
            if (!var6.removed) {
               this.touch(var6);
            }
         }
      }

      this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
      this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
      if (!this.level.isClientSide && (this.fallDistance > 0.5F || this.isInWater() || this.isPassenger()) || this.abilities.flying || this.isSleeping()) {
         this.removeEntitiesOnShoulder();
      }

   }

   private void playShoulderEntityAmbientSound(@Nullable CompoundTag var1) {
      if (var1 != null && !var1.contains("Silent") || !var1.getBoolean("Silent")) {
         String var2 = var1.getString("id");
         EntityType.byString(var2).filter((var0) -> {
            return var0 == EntityType.PARROT;
         }).ifPresent((var1x) -> {
            Parrot.playAmbientSound(this.level, this);
         });
      }

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

   public void die(DamageSource var1) {
      super.die(var1);
      this.setPos(this.x, this.y, this.z);
      if (!this.isSpectator()) {
         this.dropAllDeathLoot(var1);
      }

      if (var1 != null) {
         this.setDeltaMovement((double)(-Mth.cos((this.hurtDir + this.yRot) * 0.017453292F) * 0.1F), 0.10000000149011612D, (double)(-Mth.sin((this.hurtDir + this.yRot) * 0.017453292F) * 0.1F));
      } else {
         this.setDeltaMovement(0.0D, 0.1D, 0.0D);
      }

      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setSharedFlag(0, false);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
         this.destroyVanishingCursedItems();
         this.inventory.dropAll();
      }

   }

   protected void destroyVanishingCursedItems() {
      for(int var1 = 0; var1 < this.inventory.getContainerSize(); ++var1) {
         ItemStack var2 = this.inventory.getItem(var1);
         if (!var2.isEmpty() && EnchantmentHelper.hasVanishingCurse(var2)) {
            this.inventory.removeItemNoUpdate(var1);
         }
      }

   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      if (var1 == DamageSource.ON_FIRE) {
         return SoundEvents.PLAYER_HURT_ON_FIRE;
      } else if (var1 == DamageSource.DROWN) {
         return SoundEvents.PLAYER_HURT_DROWN;
      } else {
         return var1 == DamageSource.SWEET_BERRY_BUSH ? SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH : SoundEvents.PLAYER_HURT;
      }
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PLAYER_DEATH;
   }

   @Nullable
   public ItemEntity drop(boolean var1) {
      return this.drop(this.inventory.removeItem(this.inventory.selected, var1 && !this.inventory.getSelected().isEmpty() ? this.inventory.getSelected().getCount() : 1), false, true);
   }

   @Nullable
   public ItemEntity drop(ItemStack var1, boolean var2) {
      return this.drop(var1, false, var2);
   }

   @Nullable
   public ItemEntity drop(ItemStack var1, boolean var2, boolean var3) {
      if (var1.isEmpty()) {
         return null;
      } else {
         double var4 = this.y - 0.30000001192092896D + (double)this.getEyeHeight();
         ItemEntity var6 = new ItemEntity(this.level, this.x, var4, this.z, var1);
         var6.setPickUpDelay(40);
         if (var3) {
            var6.setThrower(this.getUUID());
         }

         float var7;
         float var8;
         if (var2) {
            var7 = this.random.nextFloat() * 0.5F;
            var8 = this.random.nextFloat() * 6.2831855F;
            this.setDeltaMovement((double)(-Mth.sin(var8) * var7), 0.20000000298023224D, (double)(Mth.cos(var8) * var7));
         } else {
            var7 = 0.3F;
            var8 = Mth.sin(this.xRot * 0.017453292F);
            float var9 = Mth.cos(this.xRot * 0.017453292F);
            float var10 = Mth.sin(this.yRot * 0.017453292F);
            float var11 = Mth.cos(this.yRot * 0.017453292F);
            float var12 = this.random.nextFloat() * 6.2831855F;
            float var13 = 0.02F * this.random.nextFloat();
            var6.setDeltaMovement((double)(-var10 * var9 * 0.3F) + Math.cos((double)var12) * (double)var13, (double)(-var8 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(var11 * var9 * 0.3F) + Math.sin((double)var12) * (double)var13);
         }

         return var6;
      }
   }

   public float getDestroySpeed(BlockState var1) {
      float var2 = this.inventory.getDestroySpeed(var1);
      if (var2 > 1.0F) {
         int var3 = EnchantmentHelper.getBlockEfficiency(this);
         ItemStack var4 = this.getMainHandItem();
         if (var3 > 0 && !var4.isEmpty()) {
            var2 += (float)(var3 * var3 + 1);
         }
      }

      if (MobEffectUtil.hasDigSpeed(this)) {
         var2 *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
      }

      if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
         float var5;
         switch(this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
         case 0:
            var5 = 0.3F;
            break;
         case 1:
            var5 = 0.09F;
            break;
         case 2:
            var5 = 0.0027F;
            break;
         case 3:
         default:
            var5 = 8.1E-4F;
         }

         var2 *= var5;
      }

      if (this.isUnderLiquid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
         var2 /= 5.0F;
      }

      if (!this.onGround) {
         var2 /= 5.0F;
      }

      return var2;
   }

   public boolean canDestroy(BlockState var1) {
      return var1.getMaterial().isAlwaysDestroyable() || this.inventory.canDestroy(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setUUID(createPlayerUUID(this.gameProfile));
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
      if (var1.contains("SpawnX", 99) && var1.contains("SpawnY", 99) && var1.contains("SpawnZ", 99)) {
         this.respawnPosition = new BlockPos(var1.getInt("SpawnX"), var1.getInt("SpawnY"), var1.getInt("SpawnZ"));
         this.respawnForced = var1.getBoolean("SpawnForced");
      }

      this.foodData.readAdditionalSaveData(var1);
      this.abilities.loadSaveData(var1);
      if (var1.contains("EnderItems", 9)) {
         this.enderChestInventory.fromTag(var1.getList("EnderItems", 10));
      }

      if (var1.contains("ShoulderEntityLeft", 10)) {
         this.setShoulderEntityLeft(var1.getCompound("ShoulderEntityLeft"));
      }

      if (var1.contains("ShoulderEntityRight", 10)) {
         this.setShoulderEntityRight(var1.getCompound("ShoulderEntityRight"));
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      var1.put("Inventory", this.inventory.save(new ListTag()));
      var1.putInt("SelectedItemSlot", this.inventory.selected);
      var1.putShort("SleepTimer", (short)this.sleepCounter);
      var1.putFloat("XpP", this.experienceProgress);
      var1.putInt("XpLevel", this.experienceLevel);
      var1.putInt("XpTotal", this.totalExperience);
      var1.putInt("XpSeed", this.enchantmentSeed);
      var1.putInt("Score", this.getScore());
      if (this.respawnPosition != null) {
         var1.putInt("SpawnX", this.respawnPosition.getX());
         var1.putInt("SpawnY", this.respawnPosition.getY());
         var1.putInt("SpawnZ", this.respawnPosition.getZ());
         var1.putBoolean("SpawnForced", this.respawnForced);
      }

      this.foodData.addAdditionalSaveData(var1);
      this.abilities.addSaveData(var1);
      var1.put("EnderItems", this.enderChestInventory.createTag());
      if (!this.getShoulderEntityLeft().isEmpty()) {
         var1.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
      }

      if (!this.getShoulderEntityRight().isEmpty()) {
         var1.put("ShoulderEntityRight", this.getShoulderEntityRight());
      }

   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else if (this.abilities.invulnerable && !var1.isBypassInvul()) {
         return false;
      } else {
         this.noActionTime = 0;
         if (this.getHealth() <= 0.0F) {
            return false;
         } else {
            this.removeEntitiesOnShoulder();
            if (var1.scalesWithDifficulty()) {
               if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                  var2 = 0.0F;
               }

               if (this.level.getDifficulty() == Difficulty.EASY) {
                  var2 = Math.min(var2 / 2.0F + 1.0F, var2);
               }

               if (this.level.getDifficulty() == Difficulty.HARD) {
                  var2 = var2 * 3.0F / 2.0F;
               }
            }

            return var2 == 0.0F ? false : super.hurt(var1, var2);
         }
      }
   }

   protected void blockUsingShield(LivingEntity var1) {
      super.blockUsingShield(var1);
      if (var1.getMainHandItem().getItem() instanceof AxeItem) {
         this.disableShield(true);
      }

   }

   public boolean canHarmPlayer(Player var1) {
      Team var2 = this.getTeam();
      Team var3 = var1.getTeam();
      if (var2 == null) {
         return true;
      } else {
         return !var2.isAlliedTo(var3) ? true : var2.isAllowFriendlyFire();
      }
   }

   protected void hurtArmor(float var1) {
      this.inventory.hurtArmor(var1);
   }

   protected void hurtCurrentlyUsedShield(float var1) {
      if (var1 >= 3.0F && this.useItem.getItem() == Items.SHIELD) {
         int var2 = 1 + Mth.floor(var1);
         InteractionHand var3 = this.getUsedItemHand();
         this.useItem.hurtAndBreak(var2, this, (var1x) -> {
            var1x.broadcastBreakEvent(var3);
         });
         if (this.useItem.isEmpty()) {
            if (var3 == InteractionHand.MAIN_HAND) {
               this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            } else {
               this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            }

            this.useItem = ItemStack.EMPTY;
            this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F);
         }
      }

   }

   protected void actuallyHurt(DamageSource var1, float var2) {
      if (!this.isInvulnerableTo(var1)) {
         var2 = this.getDamageAfterArmorAbsorb(var1, var2);
         var2 = this.getDamageAfterMagicAbsorb(var1, var2);
         float var3 = var2;
         var2 = Math.max(var2 - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (var3 - var2));
         float var4 = var3 - var2;
         if (var4 > 0.0F && var4 < 3.4028235E37F) {
            this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(var4 * 10.0F));
         }

         if (var2 != 0.0F) {
            this.causeFoodExhaustion(var1.getFoodExhaustion());
            float var5 = this.getHealth();
            this.setHealth(this.getHealth() - var2);
            this.getCombatTracker().recordDamage(var1, var5, var2);
            if (var2 < 3.4028235E37F) {
               this.awardStat(Stats.DAMAGE_TAKEN, Math.round(var2 * 10.0F));
            }

         }
      }
   }

   public void openTextEdit(SignBlockEntity var1) {
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
         ItemStack var4 = var3.isEmpty() ? ItemStack.EMPTY : var3.copy();
         if (var1.interact(this, var2)) {
            if (this.abilities.instabuild && var3 == this.getItemInHand(var2) && var3.getCount() < var4.getCount()) {
               var3.setCount(var4.getCount());
            }

            return InteractionResult.SUCCESS;
         } else {
            if (!var3.isEmpty() && var1 instanceof LivingEntity) {
               if (this.abilities.instabuild) {
                  var3 = var4;
               }

               if (var3.interactEnemy(this, (LivingEntity)var1, var2)) {
                  if (var3.isEmpty() && !this.abilities.instabuild) {
                     this.setItemInHand(var2, ItemStack.EMPTY);
                  }

                  return InteractionResult.SUCCESS;
               }
            }

            return InteractionResult.PASS;
         }
      }
   }

   public double getRidingHeight() {
      return -0.35D;
   }

   public void stopRiding() {
      super.stopRiding();
      this.boardingCooldown = 0;
   }

   protected boolean isImmobile() {
      return super.isImmobile() || this.isSleeping();
   }

   public void attack(Entity var1) {
      if (var1.isAttackable()) {
         if (!var1.skipAttackInteraction(this)) {
            float var2 = (float)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
            float var3;
            if (var1 instanceof LivingEntity) {
               var3 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)var1).getMobType());
            } else {
               var3 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);
            }

            float var4 = this.getAttackStrengthScale(0.5F);
            var2 *= 0.2F + var4 * var4 * 0.8F;
            var3 *= var4;
            this.resetAttackStrengthTicker();
            if (var2 > 0.0F || var3 > 0.0F) {
               boolean var5 = var4 > 0.9F;
               boolean var6 = false;
               byte var7 = 0;
               int var21 = var7 + EnchantmentHelper.getKnockbackBonus(this);
               if (this.isSprinting() && var5) {
                  this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                  ++var21;
                  var6 = true;
               }

               boolean var8 = var5 && this.fallDistance > 0.0F && !this.onGround && !this.onLadder() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && var1 instanceof LivingEntity;
               var8 = var8 && !this.isSprinting();
               if (var8) {
                  var2 *= 1.5F;
               }

               var2 += var3;
               boolean var9 = false;
               double var10 = (double)(this.walkDist - this.walkDistO);
               if (var5 && !var8 && !var6 && this.onGround && var10 < (double)this.getSpeed()) {
                  ItemStack var12 = this.getItemInHand(InteractionHand.MAIN_HAND);
                  if (var12.getItem() instanceof SwordItem) {
                     var9 = true;
                  }
               }

               float var22 = 0.0F;
               boolean var13 = false;
               int var14 = EnchantmentHelper.getFireAspect(this);
               if (var1 instanceof LivingEntity) {
                  var22 = ((LivingEntity)var1).getHealth();
                  if (var14 > 0 && !var1.isOnFire()) {
                     var13 = true;
                     var1.setSecondsOnFire(1);
                  }
               }

               Vec3 var15 = var1.getDeltaMovement();
               boolean var16 = var1.hurt(DamageSource.playerAttack(this), var2);
               if (var16) {
                  if (var21 > 0) {
                     if (var1 instanceof LivingEntity) {
                        ((LivingEntity)var1).knockback(this, (float)var21 * 0.5F, (double)Mth.sin(this.yRot * 0.017453292F), (double)(-Mth.cos(this.yRot * 0.017453292F)));
                     } else {
                        var1.push((double)(-Mth.sin(this.yRot * 0.017453292F) * (float)var21 * 0.5F), 0.1D, (double)(Mth.cos(this.yRot * 0.017453292F) * (float)var21 * 0.5F));
                     }

                     this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                     this.setSprinting(false);
                  }

                  if (var9) {
                     float var17 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * var2;
                     List var18 = this.level.getEntitiesOfClass(LivingEntity.class, var1.getBoundingBox().inflate(1.0D, 0.25D, 1.0D));
                     Iterator var19 = var18.iterator();

                     label166:
                     while(true) {
                        LivingEntity var20;
                        do {
                           do {
                              do {
                                 do {
                                    if (!var19.hasNext()) {
                                       this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                                       this.sweepAttack();
                                       break label166;
                                    }

                                    var20 = (LivingEntity)var19.next();
                                 } while(var20 == this);
                              } while(var20 == var1);
                           } while(this.isAlliedTo(var20));
                        } while(var20 instanceof ArmorStand && ((ArmorStand)var20).isMarker());

                        if (this.distanceToSqr(var20) < 9.0D) {
                           var20.knockback(this, 0.4F, (double)Mth.sin(this.yRot * 0.017453292F), (double)(-Mth.cos(this.yRot * 0.017453292F)));
                           var20.hurt(DamageSource.playerAttack(this), var17);
                        }
                     }
                  }

                  if (var1 instanceof ServerPlayer && var1.hurtMarked) {
                     ((ServerPlayer)var1).connection.send(new ClientboundSetEntityMotionPacket(var1));
                     var1.hurtMarked = false;
                     var1.setDeltaMovement(var15);
                  }

                  if (var8) {
                     this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                     this.crit(var1);
                  }

                  if (!var8 && !var9) {
                     if (var5) {
                        this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                     } else {
                        this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                     }
                  }

                  if (var3 > 0.0F) {
                     this.magicCrit(var1);
                  }

                  this.setLastHurtMob(var1);
                  if (var1 instanceof LivingEntity) {
                     EnchantmentHelper.doPostHurtEffects((LivingEntity)var1, this);
                  }

                  EnchantmentHelper.doPostDamageEffects(this, var1);
                  ItemStack var23 = this.getMainHandItem();
                  Object var24 = var1;
                  if (var1 instanceof EnderDragonPart) {
                     var24 = ((EnderDragonPart)var1).parentMob;
                  }

                  if (!this.level.isClientSide && !var23.isEmpty() && var24 instanceof LivingEntity) {
                     var23.hurtEnemy((LivingEntity)var24, this);
                     if (var23.isEmpty()) {
                        this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                     }
                  }

                  if (var1 instanceof LivingEntity) {
                     float var25 = var22 - ((LivingEntity)var1).getHealth();
                     this.awardStat(Stats.DAMAGE_DEALT, Math.round(var25 * 10.0F));
                     if (var14 > 0) {
                        var1.setSecondsOnFire(var14 * 4);
                     }

                     if (this.level instanceof ServerLevel && var25 > 2.0F) {
                        int var26 = (int)((double)var25 * 0.5D);
                        ((ServerLevel)this.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, var1.x, var1.y + (double)(var1.getBbHeight() * 0.5F), var1.z, var26, 0.1D, 0.0D, 0.1D, 0.2D);
                     }
                  }

                  this.causeFoodExhaustion(0.1F);
               } else {
                  this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                  if (var13) {
                     var1.clearFire();
                  }
               }
            }

         }
      }
   }

   protected void doAutoAttackOnTouch(LivingEntity var1) {
      this.attack(var1);
   }

   public void disableShield(boolean var1) {
      float var2 = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
      if (var1) {
         var2 += 0.75F;
      }

      if (this.random.nextFloat() < var2) {
         this.getCooldowns().addCooldown(Items.SHIELD, 100);
         this.stopUsingItem();
         this.level.broadcastEntityEvent(this, (byte)30);
      }

   }

   public void crit(Entity var1) {
   }

   public void magicCrit(Entity var1) {
   }

   public void sweepAttack() {
      double var1 = (double)(-Mth.sin(this.yRot * 0.017453292F));
      double var3 = (double)Mth.cos(this.yRot * 0.017453292F);
      if (this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).sendParticles(ParticleTypes.SWEEP_ATTACK, this.x + var1, this.y + (double)this.getBbHeight() * 0.5D, this.z + var3, 0, var1, 0.0D, var3, 0.0D);
      }

   }

   public void respawn() {
   }

   public void remove() {
      super.remove();
      this.inventoryMenu.removed(this);
      if (this.containerMenu != null) {
         this.containerMenu.removed(this);
      }

   }

   public boolean isLocalPlayer() {
      return false;
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }

   public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos var1) {
      Direction var2 = (Direction)this.level.getBlockState(var1).getValue(HorizontalDirectionalBlock.FACING);
      if (!this.level.isClientSide) {
         if (this.isSleeping() || !this.isAlive()) {
            return Either.left(Player.BedSleepingProblem.OTHER_PROBLEM);
         }

         if (!this.level.dimension.isNaturalDimension()) {
            return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_HERE);
         }

         if (this.level.isDay()) {
            return Either.left(Player.BedSleepingProblem.NOT_POSSIBLE_NOW);
         }

         if (!this.bedInRange(var1, var2)) {
            return Either.left(Player.BedSleepingProblem.TOO_FAR_AWAY);
         }

         if (this.bedBlocked(var1, var2)) {
            return Either.left(Player.BedSleepingProblem.OBSTRUCTED);
         }

         if (!this.isCreative()) {
            double var3 = 8.0D;
            double var5 = 5.0D;
            List var7 = this.level.getEntitiesOfClass(Monster.class, new AABB((double)var1.getX() - 8.0D, (double)var1.getY() - 5.0D, (double)var1.getZ() - 8.0D, (double)var1.getX() + 8.0D, (double)var1.getY() + 5.0D, (double)var1.getZ() + 8.0D), (var1x) -> {
               return var1x.isPreventingPlayerRest(this);
            });
            if (!var7.isEmpty()) {
               return Either.left(Player.BedSleepingProblem.NOT_SAFE);
            }
         }
      }

      this.startSleeping(var1);
      this.sleepCounter = 0;
      if (this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).updateSleepingPlayerList();
      }

      return Either.right(Unit.INSTANCE);
   }

   public void startSleeping(BlockPos var1) {
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      super.startSleeping(var1);
   }

   private boolean bedInRange(BlockPos var1, Direction var2) {
      if (Math.abs(this.x - (double)var1.getX()) <= 3.0D && Math.abs(this.y - (double)var1.getY()) <= 2.0D && Math.abs(this.z - (double)var1.getZ()) <= 3.0D) {
         return true;
      } else {
         BlockPos var3 = var1.relative(var2.getOpposite());
         return Math.abs(this.x - (double)var3.getX()) <= 3.0D && Math.abs(this.y - (double)var3.getY()) <= 2.0D && Math.abs(this.z - (double)var3.getZ()) <= 3.0D;
      }
   }

   private boolean bedBlocked(BlockPos var1, Direction var2) {
      BlockPos var3 = var1.above();
      return !this.freeAt(var3) || !this.freeAt(var3.relative(var2.getOpposite()));
   }

   public void stopSleepInBed(boolean var1, boolean var2, boolean var3) {
      Optional var4 = this.getSleepingPos();
      super.stopSleeping();
      if (this.level instanceof ServerLevel && var2) {
         ((ServerLevel)this.level).updateSleepingPlayerList();
      }

      this.sleepCounter = var1 ? 0 : 100;
      if (var3) {
         var4.ifPresent((var1x) -> {
            this.setRespawnPosition(var1x, false);
         });
      }

   }

   public void stopSleeping() {
      this.stopSleepInBed(true, true, false);
   }

   public static Optional<Vec3> checkBedValidRespawnPosition(LevelReader var0, BlockPos var1, boolean var2) {
      Block var3 = var0.getBlockState(var1).getBlock();
      if (!(var3 instanceof BedBlock)) {
         if (!var2) {
            return Optional.empty();
         } else {
            boolean var4 = var3.isPossibleToRespawnInThis();
            boolean var5 = var0.getBlockState(var1.above()).getBlock().isPossibleToRespawnInThis();
            return var4 && var5 ? Optional.of(new Vec3((double)var1.getX() + 0.5D, (double)var1.getY() + 0.1D, (double)var1.getZ() + 0.5D)) : Optional.empty();
         }
      } else {
         return BedBlock.findStandUpPosition(EntityType.PLAYER, var0, var1, 0);
      }
   }

   public boolean isSleepingLongEnough() {
      return this.isSleeping() && this.sleepCounter >= 100;
   }

   public int getSleepTimer() {
      return this.sleepCounter;
   }

   public void displayClientMessage(Component var1, boolean var2) {
   }

   public BlockPos getRespawnPosition() {
      return this.respawnPosition;
   }

   public boolean isRespawnForced() {
      return this.respawnForced;
   }

   public void setRespawnPosition(BlockPos var1, boolean var2) {
      if (var1 != null) {
         this.respawnPosition = var1;
         this.respawnForced = var2;
      } else {
         this.respawnPosition = null;
         this.respawnForced = false;
      }

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

   public int awardRecipes(Collection<Recipe<?>> var1) {
      return 0;
   }

   public void awardRecipesByKey(ResourceLocation[] var1) {
   }

   public int resetRecipes(Collection<Recipe<?>> var1) {
      return 0;
   }

   public void jumpFromGround() {
      super.jumpFromGround();
      this.awardStat(Stats.JUMP);
      if (this.isSprinting()) {
         this.causeFoodExhaustion(0.2F);
      } else {
         this.causeFoodExhaustion(0.05F);
      }

   }

   public void travel(Vec3 var1) {
      double var2 = this.x;
      double var4 = this.y;
      double var6 = this.z;
      double var8;
      if (this.isSwimming() && !this.isPassenger()) {
         var8 = this.getLookAngle().y;
         double var10 = var8 < -0.2D ? 0.085D : 0.06D;
         if (var8 <= 0.0D || this.jumping || !this.level.getBlockState(new BlockPos(this.x, this.y + 1.0D - 0.1D, this.z)).getFluidState().isEmpty()) {
            Vec3 var12 = this.getDeltaMovement();
            this.setDeltaMovement(var12.add(0.0D, (var8 - var12.y) * var10, 0.0D));
         }
      }

      if (this.abilities.flying && !this.isPassenger()) {
         var8 = this.getDeltaMovement().y;
         float var13 = this.flyingSpeed;
         this.flyingSpeed = this.abilities.getFlyingSpeed() * (float)(this.isSprinting() ? 2 : 1);
         super.travel(var1);
         Vec3 var11 = this.getDeltaMovement();
         this.setDeltaMovement(var11.x, var8 * 0.6D, var11.z);
         this.flyingSpeed = var13;
         this.fallDistance = 0.0F;
         this.setSharedFlag(7, false);
      } else {
         super.travel(var1);
      }

      this.checkMovementStatistics(this.x - var2, this.y - var4, this.z - var6);
   }

   public void updateSwimming() {
      if (this.abilities.flying) {
         this.setSwimming(false);
      } else {
         super.updateSwimming();
      }

   }

   protected boolean freeAt(BlockPos var1) {
      return !this.level.getBlockState(var1).isViewBlocking(this.level, var1);
   }

   public float getSpeed() {
      return (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
   }

   public void checkMovementStatistics(double var1, double var3, double var5) {
      if (!this.isPassenger()) {
         int var7;
         if (this.isSwimming()) {
            var7 = Math.round(Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               this.awardStat(Stats.SWIM_ONE_CM, var7);
               this.causeFoodExhaustion(0.01F * (float)var7 * 0.01F);
            }
         } else if (this.isUnderLiquid(FluidTags.WATER, true)) {
            var7 = Math.round(Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               this.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, var7);
               this.causeFoodExhaustion(0.01F * (float)var7 * 0.01F);
            }
         } else if (this.isInWater()) {
            var7 = Math.round(Mth.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               this.awardStat(Stats.WALK_ON_WATER_ONE_CM, var7);
               this.causeFoodExhaustion(0.01F * (float)var7 * 0.01F);
            }
         } else if (this.onLadder()) {
            if (var3 > 0.0D) {
               this.awardStat(Stats.CLIMB_ONE_CM, (int)Math.round(var3 * 100.0D));
            }
         } else if (this.onGround) {
            var7 = Math.round(Mth.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               if (this.isSprinting()) {
                  this.awardStat(Stats.SPRINT_ONE_CM, var7);
                  this.causeFoodExhaustion(0.1F * (float)var7 * 0.01F);
               } else if (this.isSneaking()) {
                  this.awardStat(Stats.CROUCH_ONE_CM, var7);
                  this.causeFoodExhaustion(0.0F * (float)var7 * 0.01F);
               } else {
                  this.awardStat(Stats.WALK_ONE_CM, var7);
                  this.causeFoodExhaustion(0.0F * (float)var7 * 0.01F);
               }
            }
         } else if (this.isFallFlying()) {
            var7 = Math.round(Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            this.awardStat(Stats.AVIATE_ONE_CM, var7);
         } else {
            var7 = Math.round(Mth.sqrt(var1 * var1 + var5 * var5) * 100.0F);
            if (var7 > 25) {
               this.awardStat(Stats.FLY_ONE_CM, var7);
            }
         }

      }
   }

   private void checkRidingStatistiscs(double var1, double var3, double var5) {
      if (this.isPassenger()) {
         int var7 = Math.round(Mth.sqrt(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
         if (var7 > 0) {
            if (this.getVehicle() instanceof AbstractMinecart) {
               this.awardStat(Stats.MINECART_ONE_CM, var7);
            } else if (this.getVehicle() instanceof Boat) {
               this.awardStat(Stats.BOAT_ONE_CM, var7);
            } else if (this.getVehicle() instanceof Pig) {
               this.awardStat(Stats.PIG_ONE_CM, var7);
            } else if (this.getVehicle() instanceof AbstractHorse) {
               this.awardStat(Stats.HORSE_ONE_CM, var7);
            }
         }
      }

   }

   public void causeFallDamage(float var1, float var2) {
      if (!this.abilities.mayfly) {
         if (var1 >= 2.0F) {
            this.awardStat(Stats.FALL_ONE_CM, (int)Math.round((double)var1 * 100.0D));
         }

         super.causeFallDamage(var1, var2);
      }
   }

   protected void doWaterSplashEffect() {
      if (!this.isSpectator()) {
         super.doWaterSplashEffect();
      }

   }

   protected SoundEvent getFallDamageSound(int var1) {
      return var1 > 4 ? SoundEvents.PLAYER_BIG_FALL : SoundEvents.PLAYER_SMALL_FALL;
   }

   public void killed(LivingEntity var1) {
      this.awardStat(Stats.ENTITY_KILLED.get(var1.getType()));
   }

   public void makeStuckInBlock(BlockState var1, Vec3 var2) {
      if (!this.abilities.flying) {
         super.makeStuckInBlock(var1, var2);
      }

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
      this.experienceLevel += var1;
      if (this.experienceLevel < 0) {
         this.experienceLevel = 0;
         this.experienceProgress = 0.0F;
         this.totalExperience = 0;
      }

      if (var1 > 0 && this.experienceLevel % 5 == 0 && (float)this.lastLevelUpTime < (float)this.tickCount - 100.0F) {
         float var2 = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
         this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), var2 * 0.75F, 1.0F);
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
         if (!this.level.isClientSide) {
            this.foodData.addExhaustion(var1);
         }

      }
   }

   public FoodData getFoodData() {
      return this.foodData;
   }

   public boolean canEat(boolean var1) {
      return !this.abilities.invulnerable && (var1 || this.foodData.needsFood());
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
         BlockInWorld var5 = new BlockInWorld(this.level, var4, false);
         return var3.hasAdventureModePlaceTagForBlock(this.level.getTagManager(), var5);
      }
   }

   protected int getExperienceReward(Player var1) {
      if (!this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator()) {
         int var2 = this.experienceLevel * 7;
         return var2 > 100 ? 100 : var2;
      } else {
         return 0;
      }
   }

   protected boolean isAlwaysExperienceDropper() {
      return true;
   }

   public boolean shouldShowName() {
      return true;
   }

   protected boolean makeStepSound() {
      return !this.abilities.flying;
   }

   public void onUpdateAbilities() {
   }

   public void setGameMode(GameType var1) {
   }

   public Component getName() {
      return new TextComponent(this.gameProfile.getName());
   }

   public PlayerEnderChestContainer getEnderChestInventory() {
      return this.enderChestInventory;
   }

   public ItemStack getItemBySlot(EquipmentSlot var1) {
      if (var1 == EquipmentSlot.MAINHAND) {
         return this.inventory.getSelected();
      } else if (var1 == EquipmentSlot.OFFHAND) {
         return (ItemStack)this.inventory.offhand.get(0);
      } else {
         return var1.getType() == EquipmentSlot.Type.ARMOR ? (ItemStack)this.inventory.armor.get(var1.getIndex()) : ItemStack.EMPTY;
      }
   }

   public void setItemSlot(EquipmentSlot var1, ItemStack var2) {
      if (var1 == EquipmentSlot.MAINHAND) {
         this.playEquipSound(var2);
         this.inventory.items.set(this.inventory.selected, var2);
      } else if (var1 == EquipmentSlot.OFFHAND) {
         this.playEquipSound(var2);
         this.inventory.offhand.set(0, var2);
      } else if (var1.getType() == EquipmentSlot.Type.ARMOR) {
         this.playEquipSound(var2);
         this.inventory.armor.set(var1.getIndex(), var2);
      }

   }

   public boolean addItem(ItemStack var1) {
      this.playEquipSound(var1);
      return this.inventory.add(var1);
   }

   public Iterable<ItemStack> getHandSlots() {
      return Lists.newArrayList(new ItemStack[]{this.getMainHandItem(), this.getOffhandItem()});
   }

   public Iterable<ItemStack> getArmorSlots() {
      return this.inventory.armor;
   }

   public boolean setEntityOnShoulder(CompoundTag var1) {
      if (!this.isPassenger() && this.onGround && !this.isInWater()) {
         if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft(var1);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
         } else if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight(var1);
            this.timeEntitySatOnShoulder = this.level.getGameTime();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void removeEntitiesOnShoulder() {
      if (this.timeEntitySatOnShoulder + 20L < this.level.getGameTime()) {
         this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
         this.setShoulderEntityLeft(new CompoundTag());
         this.respawnEntityOnShoulder(this.getShoulderEntityRight());
         this.setShoulderEntityRight(new CompoundTag());
      }

   }

   private void respawnEntityOnShoulder(CompoundTag var1) {
      if (!this.level.isClientSide && !var1.isEmpty()) {
         EntityType.create(var1, this.level).ifPresent((var1x) -> {
            if (var1x instanceof TamableAnimal) {
               ((TamableAnimal)var1x).setOwnerUUID(this.uuid);
            }

            var1x.setPos(this.x, this.y + 0.699999988079071D, this.z);
            ((ServerLevel)this.level).addWithUUID(var1x);
         });
      }

   }

   public boolean isInvisibleTo(Player var1) {
      if (!this.isInvisible()) {
         return false;
      } else if (var1.isSpectator()) {
         return false;
      } else {
         Team var2 = this.getTeam();
         return var2 == null || var1 == null || var1.getTeam() != var2 || !var2.canSeeFriendlyInvisibles();
      }
   }

   public abstract boolean isSpectator();

   public boolean isSwimming() {
      return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
   }

   public abstract boolean isCreative();

   public boolean isPushedByWater() {
      return !this.abilities.flying;
   }

   public Scoreboard getScoreboard() {
      return this.level.getScoreboard();
   }

   public Component getDisplayName() {
      Component var1 = PlayerTeam.formatNameForTeam(this.getTeam(), this.getName());
      return this.decorateDisplayNameComponent(var1);
   }

   public Component getDisplayNameWithUuid() {
      return (new TextComponent("")).append(this.getName()).append(" (").append(this.gameProfile.getId().toString()).append(")");
   }

   private Component decorateDisplayNameComponent(Component var1) {
      String var2 = this.getGameProfile().getName();
      return var1.withStyle((var2x) -> {
         var2x.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + var2 + " ")).setHoverEvent(this.createHoverEvent()).setInsertion(var2);
      });
   }

   public String getScoreboardName() {
      return this.getGameProfile().getName();
   }

   public float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      switch(var1) {
      case SWIMMING:
      case FALL_FLYING:
      case SPIN_ATTACK:
         return 0.4F;
      case SNEAKING:
         return 1.27F;
      default:
         return 1.62F;
      }
   }

   public void setAbsorptionAmount(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, var1);
   }

   public float getAbsorptionAmount() {
      return (Float)this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID);
   }

   public static UUID createPlayerUUID(GameProfile var0) {
      UUID var1 = var0.getId();
      if (var1 == null) {
         var1 = createPlayerUUID(var0.getName());
      }

      return var1;
   }

   public static UUID createPlayerUUID(String var0) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0).getBytes(StandardCharsets.UTF_8));
   }

   public boolean isModelPartShown(PlayerModelPart var1) {
      return ((Byte)this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & var1.getMask()) == var1.getMask();
   }

   public boolean setSlot(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.inventory.items.size()) {
         this.inventory.setItem(var1, var2);
         return true;
      } else {
         EquipmentSlot var3;
         if (var1 == 100 + EquipmentSlot.HEAD.getIndex()) {
            var3 = EquipmentSlot.HEAD;
         } else if (var1 == 100 + EquipmentSlot.CHEST.getIndex()) {
            var3 = EquipmentSlot.CHEST;
         } else if (var1 == 100 + EquipmentSlot.LEGS.getIndex()) {
            var3 = EquipmentSlot.LEGS;
         } else if (var1 == 100 + EquipmentSlot.FEET.getIndex()) {
            var3 = EquipmentSlot.FEET;
         } else {
            var3 = null;
         }

         if (var1 == 98) {
            this.setItemSlot(EquipmentSlot.MAINHAND, var2);
            return true;
         } else if (var1 == 99) {
            this.setItemSlot(EquipmentSlot.OFFHAND, var2);
            return true;
         } else if (var3 == null) {
            int var4 = var1 - 200;
            if (var4 >= 0 && var4 < this.enderChestInventory.getContainerSize()) {
               this.enderChestInventory.setItem(var4, var2);
               return true;
            } else {
               return false;
            }
         } else {
            if (!var2.isEmpty()) {
               if (!(var2.getItem() instanceof ArmorItem) && !(var2.getItem() instanceof ElytraItem)) {
                  if (var3 != EquipmentSlot.HEAD) {
                     return false;
                  }
               } else if (Mob.getEquipmentSlotForItem(var2) != var3) {
                  return false;
               }
            }

            this.inventory.setItem(var3.getIndex() + this.inventory.items.size(), var2);
            return true;
         }
      }
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public void setReducedDebugInfo(boolean var1) {
      this.reducedDebugInfo = var1;
   }

   public HumanoidArm getMainArm() {
      return (Byte)this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
   }

   public void setMainArm(HumanoidArm var1) {
      this.entityData.set(DATA_PLAYER_MAIN_HAND, (byte)(var1 == HumanoidArm.LEFT ? 0 : 1));
   }

   public CompoundTag getShoulderEntityLeft() {
      return (CompoundTag)this.entityData.get(DATA_SHOULDER_LEFT);
   }

   protected void setShoulderEntityLeft(CompoundTag var1) {
      this.entityData.set(DATA_SHOULDER_LEFT, var1);
   }

   public CompoundTag getShoulderEntityRight() {
      return (CompoundTag)this.entityData.get(DATA_SHOULDER_RIGHT);
   }

   protected void setShoulderEntityRight(CompoundTag var1) {
      this.entityData.set(DATA_SHOULDER_RIGHT, var1);
   }

   public float getCurrentItemAttackStrengthDelay() {
      return (float)(1.0D / this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getValue() * 20.0D);
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

   public float getLuck() {
      return (float)this.getAttribute(SharedMonsterAttributes.LUCK).getValue();
   }

   public boolean canUseGameMasterBlocks() {
      return this.abilities.instabuild && this.getPermissionLevel() >= 2;
   }

   public boolean canTakeItem(ItemStack var1) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(var1);
      return this.getItemBySlot(var2).isEmpty();
   }

   public EntityDimensions getDimensions(Pose var1) {
      return (EntityDimensions)POSES.getOrDefault(var1, STANDING_DIMENSIONS);
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

            return this.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
         }
      }
   }

   public ItemStack eat(Level var1, ItemStack var2) {
      this.getFoodData().eat(var2.getItem(), var2);
      this.awardStat(Stats.ITEM_USED.get(var2.getItem()));
      var1.playSound((Player)null, this.x, this.y, this.z, SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, var1.random.nextFloat() * 0.1F + 0.9F);
      if (this instanceof ServerPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)this, var2);
      }

      return super.eat(var1, var2);
   }

   static {
      POSES = ImmutableMap.builder().put(Pose.STANDING, STANDING_DIMENSIONS).put(Pose.SLEEPING, SLEEPING_DIMENSIONS).put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F)).put(Pose.SNEAKING, EntityDimensions.scalable(0.6F, 1.5F)).put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F)).build();
      DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
      DATA_SCORE_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
      DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
      DATA_PLAYER_MAIN_HAND = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
      DATA_SHOULDER_LEFT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
      DATA_SHOULDER_RIGHT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
   }

   public static enum BedSleepingProblem {
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW(new TranslatableComponent("block.minecraft.bed.no_sleep", new Object[0])),
      TOO_FAR_AWAY(new TranslatableComponent("block.minecraft.bed.too_far_away", new Object[0])),
      OBSTRUCTED(new TranslatableComponent("block.minecraft.bed.obstructed", new Object[0])),
      OTHER_PROBLEM,
      NOT_SAFE(new TranslatableComponent("block.minecraft.bed.not_safe", new Object[0]));

      @Nullable
      private final Component message;

      private BedSleepingProblem() {
         this.message = null;
      }

      private BedSleepingProblem(Component var3) {
         this.message = var3;
      }

      @Nullable
      public Component getMessage() {
         return this.message;
      }
   }
}
