package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Bee extends Animal implements NeutralMob, FlyingAnimal {
   public static final float FLAP_DEGREES_PER_TICK = 120.32113F;
   public static final int TICKS_PER_FLAP = Mth.ceil(1.4959966F);
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
   private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME;
   private static final int FLAG_ROLL = 2;
   private static final int FLAG_HAS_STUNG = 4;
   private static final int FLAG_HAS_NECTAR = 8;
   private static final int STING_DEATH_COUNTDOWN = 1200;
   private static final int TICKS_BEFORE_GOING_TO_KNOWN_FLOWER = 600;
   private static final int TICKS_WITHOUT_NECTAR_BEFORE_GOING_HOME = 3600;
   private static final int MIN_ATTACK_DIST = 4;
   private static final int MAX_CROPS_GROWABLE = 10;
   private static final int POISON_SECONDS_NORMAL = 10;
   private static final int POISON_SECONDS_HARD = 18;
   private static final int TOO_FAR_DISTANCE = 48;
   private static final int HIVE_CLOSE_ENOUGH_DISTANCE = 2;
   private static final int RESTRICTED_WANDER_DISTANCE_REDUCTION = 24;
   private static final int DEFAULT_WANDER_DISTANCE_REDUCTION = 16;
   private static final int PATHFIND_TO_HIVE_WHEN_CLOSER_THAN = 16;
   private static final int HIVE_SEARCH_DISTANCE = 20;
   public static final String TAG_CROPS_GROWN_SINCE_POLLINATION = "CropsGrownSincePollination";
   public static final String TAG_CANNOT_ENTER_HIVE_TICKS = "CannotEnterHiveTicks";
   public static final String TAG_TICKS_SINCE_POLLINATION = "TicksSincePollination";
   public static final String TAG_HAS_STUNG = "HasStung";
   public static final String TAG_HAS_NECTAR = "HasNectar";
   public static final String TAG_FLOWER_POS = "flower_pos";
   public static final String TAG_HIVE_POS = "hive_pos";
   private static final UniformInt PERSISTENT_ANGER_TIME;
   @Nullable
   private UUID persistentAngerTarget;
   private float rollAmount;
   private float rollAmountO;
   private int timeSinceSting;
   int ticksWithoutNectarSinceExitingHive;
   private int stayOutOfHiveCountdown;
   private int numCropsGrownSincePollination;
   private static final int COOLDOWN_BEFORE_LOCATING_NEW_HIVE = 200;
   int remainingCooldownBeforeLocatingNewHive;
   private static final int COOLDOWN_BEFORE_LOCATING_NEW_FLOWER = 200;
   private static final int MIN_FIND_FLOWER_RETRY_COOLDOWN = 20;
   private static final int MAX_FIND_FLOWER_RETRY_COOLDOWN = 60;
   int remainingCooldownBeforeLocatingNewFlower;
   @Nullable
   BlockPos savedFlowerPos;
   @Nullable
   BlockPos hivePos;
   BeePollinateGoal beePollinateGoal;
   BeeGoToHiveGoal goToHiveGoal;
   private BeeGoToKnownFlowerGoal goToKnownFlowerGoal;
   private int underWaterTicks;

   public Bee(EntityType<? extends Bee> var1, Level var2) {
      super(var1, var2);
      this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(this.random, 20, 60);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.lookControl = new BeeLookControl(this);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
      this.setPathfindingMalus(PathType.FENCE, -1.0F);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_FLAGS_ID, (byte)0);
      var1.define(DATA_REMAINING_ANGER_TIME, 0);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBlockState(var1).isAir() ? 10.0F : 0.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BeeAttackGoal(this, 1.399999976158142, true));
      this.goalSelector.addGoal(1, new BeeEnterHiveGoal());
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, (var0) -> {
         return var0.is(ItemTags.BEE_FOOD);
      }, false));
      this.goalSelector.addGoal(3, new ValidateHiveGoal());
      this.goalSelector.addGoal(3, new ValidateFlowerGoal());
      this.beePollinateGoal = new BeePollinateGoal();
      this.goalSelector.addGoal(4, this.beePollinateGoal);
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25));
      this.goalSelector.addGoal(5, new BeeLocateHiveGoal());
      this.goToHiveGoal = new BeeGoToHiveGoal();
      this.goalSelector.addGoal(5, this.goToHiveGoal);
      this.goToKnownFlowerGoal = new BeeGoToKnownFlowerGoal();
      this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);
      this.goalSelector.addGoal(7, new BeeGrowCropGoal());
      this.goalSelector.addGoal(8, new BeeWanderGoal());
      this.goalSelector.addGoal(9, new FloatGoal(this));
      this.targetSelector.addGoal(1, (new BeeHurtByOtherGoal(this)).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new BeeBecomeAngryTargetGoal(this));
      this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal(this, true));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.hasHive()) {
         var1.put("hive_pos", NbtUtils.writeBlockPos(this.getHivePos()));
      }

      if (this.hasSavedFlowerPos()) {
         var1.put("flower_pos", NbtUtils.writeBlockPos(this.getSavedFlowerPos()));
      }

      var1.putBoolean("HasNectar", this.hasNectar());
      var1.putBoolean("HasStung", this.hasStung());
      var1.putInt("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
      var1.putInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
      var1.putInt("CropsGrownSincePollination", this.numCropsGrownSincePollination);
      this.addPersistentAngerSaveData(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setHasNectar(var1.getBoolean("HasNectar"));
      this.setHasStung(var1.getBoolean("HasStung"));
      this.ticksWithoutNectarSinceExitingHive = var1.getInt("TicksSincePollination");
      this.stayOutOfHiveCountdown = var1.getInt("CannotEnterHiveTicks");
      this.numCropsGrownSincePollination = var1.getInt("CropsGrownSincePollination");
      this.hivePos = (BlockPos)NbtUtils.readBlockPos(var1, "hive_pos").orElse((Object)null);
      this.savedFlowerPos = (BlockPos)NbtUtils.readBlockPos(var1, "flower_pos").orElse((Object)null);
      this.readPersistentAngerSaveData(this.level(), var1);
   }

   public boolean doHurtTarget(ServerLevel var1, Entity var2) {
      DamageSource var3 = this.damageSources().sting(this);
      boolean var4 = var2.hurtServer(var1, var3, (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (var4) {
         EnchantmentHelper.doPostAttackEffects(var1, var2, var3);
         if (var2 instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)var2;
            var5.setStingerCount(var5.getStingerCount() + 1);
            byte var6 = 0;
            if (this.level().getDifficulty() == Difficulty.NORMAL) {
               var6 = 10;
            } else if (this.level().getDifficulty() == Difficulty.HARD) {
               var6 = 18;
            }

            if (var6 > 0) {
               var5.addEffect(new MobEffectInstance(MobEffects.POISON, var6 * 20, 0), this);
            }
         }

         this.setHasStung(true);
         this.stopBeingAngry();
         this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
      }

      return var4;
   }

   public void tick() {
      super.tick();
      if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05F) {
         for(int var1 = 0; var1 < this.random.nextInt(2) + 1; ++var1) {
            this.spawnFluidParticle(this.level(), this.getX() - 0.30000001192092896, this.getX() + 0.30000001192092896, this.getZ() - 0.30000001192092896, this.getZ() + 0.30000001192092896, this.getY(0.5), ParticleTypes.FALLING_NECTAR);
         }
      }

      this.updateRollAmount();
   }

   private void spawnFluidParticle(Level var1, double var2, double var4, double var6, double var8, double var10, ParticleOptions var12) {
      var1.addParticle(var12, Mth.lerp(var1.random.nextDouble(), var2, var4), var10, Mth.lerp(var1.random.nextDouble(), var6, var8), 0.0, 0.0, 0.0);
   }

   void pathfindRandomlyTowards(BlockPos var1) {
      Vec3 var2 = Vec3.atBottomCenterOf(var1);
      byte var3 = 0;
      BlockPos var4 = this.blockPosition();
      int var5 = (int)var2.y - var4.getY();
      if (var5 > 2) {
         var3 = 4;
      } else if (var5 < -2) {
         var3 = -4;
      }

      int var6 = 6;
      int var7 = 8;
      int var8 = var4.distManhattan(var1);
      if (var8 < 15) {
         var6 = var8 / 2;
         var7 = var8 / 2;
      }

      Vec3 var9 = AirRandomPos.getPosTowards(this, var6, var7, var3, var2, 0.3141592741012573);
      if (var9 != null) {
         this.navigation.setMaxVisitedNodesMultiplier(0.5F);
         this.navigation.moveTo(var9.x, var9.y, var9.z, 1.0);
      }
   }

   @Nullable
   public BlockPos getSavedFlowerPos() {
      return this.savedFlowerPos;
   }

   public boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   public void setSavedFlowerPos(BlockPos var1) {
      this.savedFlowerPos = var1;
   }

   @VisibleForDebug
   public int getTravellingTicks() {
      return Math.max(this.goToHiveGoal.travellingTicks, this.goToKnownFlowerGoal.travellingTicks);
   }

   @VisibleForDebug
   public List<BlockPos> getBlacklistedHives() {
      return this.goToHiveGoal.blacklistedTargets;
   }

   private boolean isTiredOfLookingForNectar() {
      return this.ticksWithoutNectarSinceExitingHive > 3600;
   }

   void dropHive() {
      this.hivePos = null;
      this.remainingCooldownBeforeLocatingNewHive = 200;
   }

   void dropFlower() {
      this.savedFlowerPos = null;
      this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(this.random, 20, 60);
   }

   boolean wantsToEnterHive() {
      if (this.stayOutOfHiveCountdown <= 0 && !this.beePollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
         boolean var1 = this.isTiredOfLookingForNectar() || isNightOrRaining(this.level()) || this.hasNectar();
         return var1 && !this.isHiveNearFire();
      } else {
         return false;
      }
   }

   public static boolean isNightOrRaining(Level var0) {
      return var0.dimensionType().hasSkyLight() && (var0.isNight() || var0.isRaining());
   }

   public void setStayOutOfHiveCountdown(int var1) {
      this.stayOutOfHiveCountdown = var1;
   }

   public float getRollAmount(float var1) {
      return Mth.lerp(var1, this.rollAmountO, this.rollAmount);
   }

   private void updateRollAmount() {
      this.rollAmountO = this.rollAmount;
      if (this.isRolling()) {
         this.rollAmount = Math.min(1.0F, this.rollAmount + 0.2F);
      } else {
         this.rollAmount = Math.max(0.0F, this.rollAmount - 0.24F);
      }

   }

   protected void customServerAiStep(ServerLevel var1) {
      boolean var2 = this.hasStung();
      if (this.isInWaterOrBubble()) {
         ++this.underWaterTicks;
      } else {
         this.underWaterTicks = 0;
      }

      if (this.underWaterTicks > 20) {
         this.hurtServer(var1, this.damageSources().drown(), 1.0F);
      }

      if (var2) {
         ++this.timeSinceSting;
         if (this.timeSinceSting % 5 == 0 && this.random.nextInt(Mth.clamp(1200 - this.timeSinceSting, 1, 1200)) == 0) {
            this.hurtServer(var1, this.damageSources().generic(), this.getHealth());
         }
      }

      if (!this.hasNectar()) {
         ++this.ticksWithoutNectarSinceExitingHive;
      }

      this.updatePersistentAnger(var1, false);
   }

   public void resetTicksWithoutNectarSinceExitingHive() {
      this.ticksWithoutNectarSinceExitingHive = 0;
   }

   private boolean isHiveNearFire() {
      BeehiveBlockEntity var1 = this.getBeehiveBlockEntity();
      return var1 != null && var1.isFireNearby();
   }

   public int getRemainingPersistentAngerTime() {
      return (Integer)this.entityData.get(DATA_REMAINING_ANGER_TIME);
   }

   public void setRemainingPersistentAngerTime(int var1) {
      this.entityData.set(DATA_REMAINING_ANGER_TIME, var1);
   }

   @Nullable
   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
   }

   private boolean doesHiveHaveSpace(BlockPos var1) {
      BlockEntity var2 = this.level().getBlockEntity(var1);
      if (var2 instanceof BeehiveBlockEntity) {
         return !((BeehiveBlockEntity)var2).isFull();
      } else {
         return false;
      }
   }

   @VisibleForDebug
   public boolean hasHive() {
      return this.hivePos != null;
   }

   @Nullable
   @VisibleForDebug
   public BlockPos getHivePos() {
      return this.hivePos;
   }

   @VisibleForDebug
   public GoalSelector getGoalSelector() {
      return this.goalSelector;
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendBeeInfo(this);
   }

   int getCropsGrownSincePollination() {
      return this.numCropsGrownSincePollination;
   }

   private void resetNumCropsGrownSincePollination() {
      this.numCropsGrownSincePollination = 0;
   }

   void incrementNumCropsGrownSincePollination() {
      ++this.numCropsGrownSincePollination;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level().isClientSide) {
         if (this.stayOutOfHiveCountdown > 0) {
            --this.stayOutOfHiveCountdown;
         }

         if (this.remainingCooldownBeforeLocatingNewHive > 0) {
            --this.remainingCooldownBeforeLocatingNewHive;
         }

         if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
            --this.remainingCooldownBeforeLocatingNewFlower;
         }

         boolean var1 = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0;
         this.setRolling(var1);
         if (this.tickCount % 20 == 0 && !this.isHiveValid()) {
            this.hivePos = null;
         }
      }

   }

   @Nullable
   BeehiveBlockEntity getBeehiveBlockEntity() {
      if (this.hivePos == null) {
         return null;
      } else {
         return this.isTooFarAway(this.hivePos) ? null : (BeehiveBlockEntity)this.level().getBlockEntity(this.hivePos, BlockEntityType.BEEHIVE).orElse((Object)null);
      }
   }

   boolean isHiveValid() {
      return this.getBeehiveBlockEntity() != null;
   }

   public boolean hasNectar() {
      return this.getFlag(8);
   }

   void setHasNectar(boolean var1) {
      if (var1) {
         this.resetTicksWithoutNectarSinceExitingHive();
      }

      this.setFlag(8, var1);
   }

   public boolean hasStung() {
      return this.getFlag(4);
   }

   private void setHasStung(boolean var1) {
      this.setFlag(4, var1);
   }

   private boolean isRolling() {
      return this.getFlag(2);
   }

   private void setRolling(boolean var1) {
      this.setFlag(2, var1);
   }

   boolean isTooFarAway(BlockPos var1) {
      return !this.closerThan(var1, 48);
   }

   private void setFlag(int var1, boolean var2) {
      if (var2) {
         this.entityData.set(DATA_FLAGS_ID, (byte)((Byte)this.entityData.get(DATA_FLAGS_ID) | var1));
      } else {
         this.entityData.set(DATA_FLAGS_ID, (byte)((Byte)this.entityData.get(DATA_FLAGS_ID) & ~var1));
      }

   }

   private boolean getFlag(int var1) {
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & var1) != 0;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FLYING_SPEED, 0.6000000238418579).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   protected PathNavigation createNavigation(Level var1) {
      FlyingPathNavigation var2 = new FlyingPathNavigation(this, var1) {
         public boolean isStableDestination(BlockPos var1) {
            return !this.level.getBlockState(var1.below()).isAir();
         }

         public void tick() {
            if (!Bee.this.beePollinateGoal.isPollinating()) {
               super.tick();
            }
         }
      };
      var2.setCanOpenDoors(false);
      var2.setCanFloat(false);
      var2.setRequiredPathLength(48.0F);
      return var2;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (this.isFood(var3)) {
         Item var6 = var3.getItem();
         if (var6 instanceof BlockItem) {
            BlockItem var4 = (BlockItem)var6;
            Block var7 = var4.getBlock();
            if (var7 instanceof FlowerBlock) {
               FlowerBlock var5 = (FlowerBlock)var7;
               MobEffectInstance var8 = var5.getBeeInteractionEffect();
               if (var8 != null) {
                  this.usePlayerItem(var1, var2, var3);
                  if (!this.level().isClientSide) {
                     this.addEffect(var8);
                  }

                  return InteractionResult.SUCCESS;
               }
            }
         }
      }

      return super.mobInteract(var1, var2);
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.BEE_FOOD);
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.BEE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BEE_DEATH;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   @Nullable
   public Bee getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (Bee)EntityType.BEE.create(var1, EntitySpawnReason.BREEDING);
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   public boolean isFlapping() {
      return this.isFlying() && this.tickCount % TICKS_PER_FLAP == 0;
   }

   public boolean isFlying() {
      return !this.onGround();
   }

   public void dropOffNectar() {
      this.setHasNectar(false);
      this.resetNumCropsGrownSincePollination();
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isInvulnerableTo(var1, var2)) {
         return false;
      } else {
         this.beePollinateGoal.stopPollinating();
         return super.hurtServer(var1, var2, var3);
      }
   }

   protected void jumpInLiquid(TagKey<Fluid> var1) {
      this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.01, 0.0));
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.2F));
   }

   boolean closerThan(BlockPos var1, int var2) {
      return var1.closerThan(this.blockPosition(), (double)var2);
   }

   public void setHivePos(BlockPos var1) {
      this.hivePos = var1;
   }

   static boolean attractsBees(BlockState var0) {
      if (var0.is(BlockTags.BEE_ATTRACTIVE)) {
         if ((Boolean)var0.getValueOrElse(BlockStateProperties.WATERLOGGED, false)) {
            return false;
         } else if (var0.is(Blocks.SUNFLOWER)) {
            return var0.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.defineId(Bee.class, EntityDataSerializers.BYTE);
      DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Bee.class, EntityDataSerializers.INT);
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   }

   private class BeeLookControl extends LookControl {
      BeeLookControl(final Mob var2) {
         super(var2);
      }

      public void tick() {
         if (!Bee.this.isAngry()) {
            super.tick();
         }
      }

      protected boolean resetXRotOnTick() {
         return !Bee.this.beePollinateGoal.isPollinating();
      }
   }

   private class BeeAttackGoal extends MeleeAttackGoal {
      BeeAttackGoal(final PathfinderMob var2, final double var3, final boolean var5) {
         super(var2, var3, var5);
      }

      public boolean canUse() {
         return super.canUse() && Bee.this.isAngry() && !Bee.this.hasStung();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && Bee.this.isAngry() && !Bee.this.hasStung();
      }
   }

   class BeeEnterHiveGoal extends BaseBeeGoal {
      BeeEnterHiveGoal() {
         super();
      }

      public boolean canBeeUse() {
         if (Bee.this.hivePos != null && Bee.this.wantsToEnterHive() && Bee.this.hivePos.closerToCenterThan(Bee.this.position(), 2.0)) {
            BeehiveBlockEntity var1 = Bee.this.getBeehiveBlockEntity();
            if (var1 != null) {
               if (!var1.isFull()) {
                  return true;
               }

               Bee.this.hivePos = null;
            }
         }

         return false;
      }

      public boolean canBeeContinueToUse() {
         return false;
      }

      public void start() {
         BeehiveBlockEntity var1 = Bee.this.getBeehiveBlockEntity();
         if (var1 != null) {
            var1.addOccupant(Bee.this);
         }

      }
   }

   class ValidateHiveGoal extends BaseBeeGoal {
      private final int VALIDATE_HIVE_COOLDOWN;
      private long lastValidateTick;

      ValidateHiveGoal() {
         super();
         this.VALIDATE_HIVE_COOLDOWN = Mth.nextInt(Bee.this.random, 20, 40);
         this.lastValidateTick = -1L;
      }

      public void start() {
         if (Bee.this.hivePos != null && Bee.this.level().isLoaded(Bee.this.hivePos) && !Bee.this.isHiveValid()) {
            Bee.this.dropHive();
         }

         this.lastValidateTick = Bee.this.level().getGameTime();
      }

      public boolean canBeeUse() {
         return Bee.this.level().getGameTime() > this.lastValidateTick + (long)this.VALIDATE_HIVE_COOLDOWN;
      }

      public boolean canBeeContinueToUse() {
         return false;
      }
   }

   private class ValidateFlowerGoal extends BaseBeeGoal {
      private final int validateFlowerCooldown;
      private long lastValidateTick;

      ValidateFlowerGoal() {
         super();
         this.validateFlowerCooldown = Mth.nextInt(Bee.this.random, 20, 40);
         this.lastValidateTick = -1L;
      }

      public void start() {
         if (Bee.this.savedFlowerPos != null && Bee.this.level().isLoaded(Bee.this.savedFlowerPos) && !this.isFlower(Bee.this.savedFlowerPos)) {
            Bee.this.dropFlower();
         }

         this.lastValidateTick = Bee.this.level().getGameTime();
      }

      public boolean canBeeUse() {
         return Bee.this.level().getGameTime() > this.lastValidateTick + (long)this.validateFlowerCooldown;
      }

      public boolean canBeeContinueToUse() {
         return false;
      }

      private boolean isFlower(BlockPos var1) {
         return Bee.attractsBees(Bee.this.level().getBlockState(var1));
      }
   }

   private class BeePollinateGoal extends BaseBeeGoal {
      private static final int MIN_POLLINATION_TICKS = 400;
      private static final double ARRIVAL_THRESHOLD = 0.1;
      private static final int POSITION_CHANGE_CHANCE = 25;
      private static final float SPEED_MODIFIER = 0.35F;
      private static final float HOVER_HEIGHT_WITHIN_FLOWER = 0.6F;
      private static final float HOVER_POS_OFFSET = 0.33333334F;
      private static final int FLOWER_SEARCH_RADIUS = 5;
      private int successfulPollinatingTicks;
      private int lastSoundPlayedTick;
      private boolean pollinating;
      @Nullable
      private Vec3 hoverPos;
      private int pollinatingTicks;
      private static final int MAX_POLLINATING_TICKS = 600;
      private Long2LongOpenHashMap unreachableFlowerCache = new Long2LongOpenHashMap();

      BeePollinateGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canBeeUse() {
         if (Bee.this.remainingCooldownBeforeLocatingNewFlower > 0) {
            return false;
         } else if (Bee.this.hasNectar()) {
            return false;
         } else if (Bee.this.level().isRaining()) {
            return false;
         } else {
            Optional var1 = this.findNearbyFlower();
            if (var1.isPresent()) {
               Bee.this.savedFlowerPos = (BlockPos)var1.get();
               Bee.this.navigation.moveTo((double)Bee.this.savedFlowerPos.getX() + 0.5, (double)Bee.this.savedFlowerPos.getY() + 0.5, (double)Bee.this.savedFlowerPos.getZ() + 0.5, 1.2000000476837158);
               return true;
            } else {
               Bee.this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(Bee.this.random, 20, 60);
               return false;
            }
         }
      }

      public boolean canBeeContinueToUse() {
         if (!this.pollinating) {
            return false;
         } else if (!Bee.this.hasSavedFlowerPos()) {
            return false;
         } else if (Bee.this.level().isRaining()) {
            return false;
         } else if (this.hasPollinatedLongEnough()) {
            return Bee.this.random.nextFloat() < 0.2F;
         } else {
            return true;
         }
      }

      private boolean hasPollinatedLongEnough() {
         return this.successfulPollinatingTicks > 400;
      }

      boolean isPollinating() {
         return this.pollinating;
      }

      void stopPollinating() {
         this.pollinating = false;
      }

      public void start() {
         this.successfulPollinatingTicks = 0;
         this.pollinatingTicks = 0;
         this.lastSoundPlayedTick = 0;
         this.pollinating = true;
         Bee.this.resetTicksWithoutNectarSinceExitingHive();
      }

      public void stop() {
         if (this.hasPollinatedLongEnough()) {
            Bee.this.setHasNectar(true);
         }

         this.pollinating = false;
         Bee.this.navigation.stop();
         Bee.this.remainingCooldownBeforeLocatingNewFlower = 200;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (Bee.this.hasSavedFlowerPos()) {
            ++this.pollinatingTicks;
            if (this.pollinatingTicks > 600) {
               Bee.this.dropFlower();
               this.pollinating = false;
               Bee.this.remainingCooldownBeforeLocatingNewFlower = 200;
            } else {
               Vec3 var1 = Vec3.atBottomCenterOf(Bee.this.savedFlowerPos).add(0.0, 0.6000000238418579, 0.0);
               if (var1.distanceTo(Bee.this.position()) > 1.0) {
                  this.hoverPos = var1;
                  this.setWantedPos();
               } else {
                  if (this.hoverPos == null) {
                     this.hoverPos = var1;
                  }

                  boolean var2 = Bee.this.position().distanceTo(this.hoverPos) <= 0.1;
                  boolean var3 = true;
                  if (!var2 && this.pollinatingTicks > 600) {
                     Bee.this.dropFlower();
                  } else {
                     if (var2) {
                        boolean var4 = Bee.this.random.nextInt(25) == 0;
                        if (var4) {
                           this.hoverPos = new Vec3(var1.x() + (double)this.getOffset(), var1.y(), var1.z() + (double)this.getOffset());
                           Bee.this.navigation.stop();
                        } else {
                           var3 = false;
                        }

                        Bee.this.getLookControl().setLookAt(var1.x(), var1.y(), var1.z());
                     }

                     if (var3) {
                        this.setWantedPos();
                     }

                     ++this.successfulPollinatingTicks;
                     if (Bee.this.random.nextFloat() < 0.05F && this.successfulPollinatingTicks > this.lastSoundPlayedTick + 60) {
                        this.lastSoundPlayedTick = this.successfulPollinatingTicks;
                        Bee.this.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
                     }

                  }
               }
            }
         }
      }

      private void setWantedPos() {
         Bee.this.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), 0.3499999940395355);
      }

      private float getOffset() {
         return (Bee.this.random.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
      }

      private Optional<BlockPos> findNearbyFlower() {
         Iterable var1 = BlockPos.withinManhattan(Bee.this.blockPosition(), 5, 5, 5);
         Long2LongOpenHashMap var2 = new Long2LongOpenHashMap();
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            BlockPos var4 = (BlockPos)var3.next();
            long var5 = this.unreachableFlowerCache.getOrDefault(var4.asLong(), -9223372036854775808L);
            if (Bee.this.level().getGameTime() < var5) {
               var2.put(var4.asLong(), var5);
            } else if (Bee.attractsBees(Bee.this.level().getBlockState(var4))) {
               Path var7 = Bee.this.navigation.createPath((BlockPos)var4, 1);
               if (var7 != null && var7.canReach()) {
                  return Optional.of(var4);
               }

               var2.put(var4.asLong(), Bee.this.level().getGameTime() + 600L);
            }
         }

         this.unreachableFlowerCache = var2;
         return Optional.empty();
      }
   }

   class BeeLocateHiveGoal extends BaseBeeGoal {
      BeeLocateHiveGoal() {
         super();
      }

      public boolean canBeeUse() {
         return Bee.this.remainingCooldownBeforeLocatingNewHive == 0 && !Bee.this.hasHive() && Bee.this.wantsToEnterHive();
      }

      public boolean canBeeContinueToUse() {
         return false;
      }

      public void start() {
         Bee.this.remainingCooldownBeforeLocatingNewHive = 200;
         List var1 = this.findNearbyHivesWithSpace();
         if (!var1.isEmpty()) {
            Iterator var2 = var1.iterator();

            BlockPos var3;
            do {
               if (!var2.hasNext()) {
                  Bee.this.goToHiveGoal.clearBlacklist();
                  Bee.this.hivePos = (BlockPos)var1.get(0);
                  return;
               }

               var3 = (BlockPos)var2.next();
            } while(Bee.this.goToHiveGoal.isTargetBlacklisted(var3));

            Bee.this.hivePos = var3;
         }
      }

      private List<BlockPos> findNearbyHivesWithSpace() {
         BlockPos var1 = Bee.this.blockPosition();
         PoiManager var2 = ((ServerLevel)Bee.this.level()).getPoiManager();
         Stream var3 = var2.getInRange((var0) -> {
            return var0.is(PoiTypeTags.BEE_HOME);
         }, var1, 20, PoiManager.Occupancy.ANY);
         return (List)var3.map(PoiRecord::getPos).filter(Bee.this::doesHiveHaveSpace).sorted(Comparator.comparingDouble((var1x) -> {
            return var1x.distSqr(var1);
         })).collect(Collectors.toList());
      }
   }

   @VisibleForDebug
   public class BeeGoToHiveGoal extends BaseBeeGoal {
      public static final int MAX_TRAVELLING_TICKS = 2400;
      int travellingTicks;
      private static final int MAX_BLACKLISTED_TARGETS = 3;
      final List<BlockPos> blacklistedTargets;
      @Nullable
      private Path lastPath;
      private static final int TICKS_BEFORE_HIVE_DROP = 60;
      private int ticksStuck;

      BeeGoToHiveGoal() {
         super();
         this.travellingTicks = Bee.this.level().random.nextInt(10);
         this.blacklistedTargets = Lists.newArrayList();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canBeeUse() {
         return Bee.this.hivePos != null && !Bee.this.isTooFarAway(Bee.this.hivePos) && !Bee.this.hasRestriction() && Bee.this.wantsToEnterHive() && !this.hasReachedTarget(Bee.this.hivePos) && Bee.this.level().getBlockState(Bee.this.hivePos).is(BlockTags.BEEHIVES);
      }

      public boolean canBeeContinueToUse() {
         return this.canBeeUse();
      }

      public void start() {
         this.travellingTicks = 0;
         this.ticksStuck = 0;
         super.start();
      }

      public void stop() {
         this.travellingTicks = 0;
         this.ticksStuck = 0;
         Bee.this.navigation.stop();
         Bee.this.navigation.resetMaxVisitedNodesMultiplier();
      }

      public void tick() {
         if (Bee.this.hivePos != null) {
            ++this.travellingTicks;
            if (this.travellingTicks > this.adjustedTickDelay(2400)) {
               this.dropAndBlacklistHive();
            } else if (!Bee.this.navigation.isInProgress()) {
               if (!Bee.this.closerThan(Bee.this.hivePos, 16)) {
                  if (Bee.this.isTooFarAway(Bee.this.hivePos)) {
                     Bee.this.dropHive();
                  } else {
                     Bee.this.pathfindRandomlyTowards(Bee.this.hivePos);
                  }
               } else {
                  boolean var1 = this.pathfindDirectlyTowards(Bee.this.hivePos);
                  if (!var1) {
                     this.dropAndBlacklistHive();
                  } else if (this.lastPath != null && Bee.this.navigation.getPath().sameAs(this.lastPath)) {
                     ++this.ticksStuck;
                     if (this.ticksStuck > 60) {
                        Bee.this.dropHive();
                        this.ticksStuck = 0;
                     }
                  } else {
                     this.lastPath = Bee.this.navigation.getPath();
                  }

               }
            }
         }
      }

      private boolean pathfindDirectlyTowards(BlockPos var1) {
         int var2 = Bee.this.closerThan(var1, 3) ? 1 : 2;
         Bee.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
         Bee.this.navigation.moveTo((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), var2, 1.0);
         return Bee.this.navigation.getPath() != null && Bee.this.navigation.getPath().canReach();
      }

      boolean isTargetBlacklisted(BlockPos var1) {
         return this.blacklistedTargets.contains(var1);
      }

      private void blacklistTarget(BlockPos var1) {
         this.blacklistedTargets.add(var1);

         while(this.blacklistedTargets.size() > 3) {
            this.blacklistedTargets.remove(0);
         }

      }

      void clearBlacklist() {
         this.blacklistedTargets.clear();
      }

      private void dropAndBlacklistHive() {
         if (Bee.this.hivePos != null) {
            this.blacklistTarget(Bee.this.hivePos);
         }

         Bee.this.dropHive();
      }

      private boolean hasReachedTarget(BlockPos var1) {
         if (Bee.this.closerThan(var1, 2)) {
            return true;
         } else {
            Path var2 = Bee.this.navigation.getPath();
            return var2 != null && var2.getTarget().equals(var1) && var2.canReach() && var2.isDone();
         }
      }
   }

   public class BeeGoToKnownFlowerGoal extends BaseBeeGoal {
      private static final int MAX_TRAVELLING_TICKS = 2400;
      int travellingTicks;

      BeeGoToKnownFlowerGoal() {
         super();
         this.travellingTicks = Bee.this.level().random.nextInt(10);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canBeeUse() {
         return Bee.this.savedFlowerPos != null && !Bee.this.hasRestriction() && this.wantsToGoToKnownFlower() && !Bee.this.closerThan(Bee.this.savedFlowerPos, 2);
      }

      public boolean canBeeContinueToUse() {
         return this.canBeeUse();
      }

      public void start() {
         this.travellingTicks = 0;
         super.start();
      }

      public void stop() {
         this.travellingTicks = 0;
         Bee.this.navigation.stop();
         Bee.this.navigation.resetMaxVisitedNodesMultiplier();
      }

      public void tick() {
         if (Bee.this.savedFlowerPos != null) {
            ++this.travellingTicks;
            if (this.travellingTicks > this.adjustedTickDelay(2400)) {
               Bee.this.dropFlower();
            } else if (!Bee.this.navigation.isInProgress()) {
               if (Bee.this.isTooFarAway(Bee.this.savedFlowerPos)) {
                  Bee.this.dropFlower();
               } else {
                  Bee.this.pathfindRandomlyTowards(Bee.this.savedFlowerPos);
               }
            }
         }
      }

      private boolean wantsToGoToKnownFlower() {
         return Bee.this.ticksWithoutNectarSinceExitingHive > 600;
      }
   }

   class BeeGrowCropGoal extends BaseBeeGoal {
      static final int GROW_CHANCE = 30;

      BeeGrowCropGoal() {
         super();
      }

      public boolean canBeeUse() {
         if (Bee.this.getCropsGrownSincePollination() >= 10) {
            return false;
         } else if (Bee.this.random.nextFloat() < 0.3F) {
            return false;
         } else {
            return Bee.this.hasNectar() && Bee.this.isHiveValid();
         }
      }

      public boolean canBeeContinueToUse() {
         return this.canBeeUse();
      }

      public void tick() {
         if (Bee.this.random.nextInt(this.adjustedTickDelay(30)) == 0) {
            for(int var1 = 1; var1 <= 2; ++var1) {
               BlockPos var2 = Bee.this.blockPosition().below(var1);
               BlockState var3 = Bee.this.level().getBlockState(var2);
               Block var4 = var3.getBlock();
               BlockState var5 = null;
               if (var3.is(BlockTags.BEE_GROWABLES)) {
                  if (var4 instanceof CropBlock) {
                     CropBlock var6 = (CropBlock)var4;
                     if (!var6.isMaxAge(var3)) {
                        var5 = var6.getStateForAge(var6.getAge(var3) + 1);
                     }
                  } else {
                     int var7;
                     if (var4 instanceof StemBlock) {
                        var7 = (Integer)var3.getValue(StemBlock.AGE);
                        if (var7 < 7) {
                           var5 = (BlockState)var3.setValue(StemBlock.AGE, var7 + 1);
                        }
                     } else if (var3.is(Blocks.SWEET_BERRY_BUSH)) {
                        var7 = (Integer)var3.getValue(SweetBerryBushBlock.AGE);
                        if (var7 < 3) {
                           var5 = (BlockState)var3.setValue(SweetBerryBushBlock.AGE, var7 + 1);
                        }
                     } else if (var3.is(Blocks.CAVE_VINES) || var3.is(Blocks.CAVE_VINES_PLANT)) {
                        BonemealableBlock var8 = (BonemealableBlock)var3.getBlock();
                        if (var8.isValidBonemealTarget(Bee.this.level(), var2, var3)) {
                           var8.performBonemeal((ServerLevel)Bee.this.level(), Bee.this.random, var2, var3);
                           var5 = Bee.this.level().getBlockState(var2);
                        }
                     }
                  }

                  if (var5 != null) {
                     Bee.this.level().levelEvent(2011, var2, 15);
                     Bee.this.level().setBlockAndUpdate(var2, var5);
                     Bee.this.incrementNumCropsGrownSincePollination();
                  }
               }
            }

         }
      }
   }

   private class BeeWanderGoal extends Goal {
      BeeWanderGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return Bee.this.navigation.isDone() && Bee.this.random.nextInt(10) == 0;
      }

      public boolean canContinueToUse() {
         return Bee.this.navigation.isInProgress();
      }

      public void start() {
         Vec3 var1 = this.findPos();
         if (var1 != null) {
            Bee.this.navigation.moveTo(Bee.this.navigation.createPath((BlockPos)BlockPos.containing(var1), 1), 1.0);
         }

      }

      @Nullable
      private Vec3 findPos() {
         Vec3 var1;
         if (Bee.this.isHiveValid() && !Bee.this.closerThan(Bee.this.hivePos, this.getWanderThreshold())) {
            Vec3 var2 = Vec3.atCenterOf(Bee.this.hivePos);
            var1 = var2.subtract(Bee.this.position()).normalize();
         } else {
            var1 = Bee.this.getViewVector(0.0F);
         }

         boolean var4 = true;
         Vec3 var3 = HoverRandomPos.getPos(Bee.this, 8, 7, var1.x, var1.z, 1.5707964F, 3, 1);
         return var3 != null ? var3 : AirAndWaterRandomPos.getPos(Bee.this, 8, 4, -2, var1.x, var1.z, 1.5707963705062866);
      }

      private int getWanderThreshold() {
         int var1 = !Bee.this.hasHive() && !Bee.this.hasSavedFlowerPos() ? 16 : 24;
         return 48 - var1;
      }
   }

   class BeeHurtByOtherGoal extends HurtByTargetGoal {
      BeeHurtByOtherGoal(final Bee var2) {
         super(var2);
      }

      public boolean canContinueToUse() {
         return Bee.this.isAngry() && super.canContinueToUse();
      }

      protected void alertOther(Mob var1, LivingEntity var2) {
         if (var1 instanceof Bee && this.mob.hasLineOfSight(var2)) {
            var1.setTarget(var2);
         }

      }
   }

   static class BeeBecomeAngryTargetGoal extends NearestAttackableTargetGoal<Player> {
      BeeBecomeAngryTargetGoal(Bee var1) {
         Objects.requireNonNull(var1);
         super(var1, Player.class, 10, true, false, var1::isAngryAt);
      }

      public boolean canUse() {
         return this.beeCanTarget() && super.canUse();
      }

      public boolean canContinueToUse() {
         boolean var1 = this.beeCanTarget();
         if (var1 && this.mob.getTarget() != null) {
            return super.canContinueToUse();
         } else {
            this.targetMob = null;
            return false;
         }
      }

      private boolean beeCanTarget() {
         Bee var1 = (Bee)this.mob;
         return var1.isAngry() && !var1.hasStung();
      }
   }

   private abstract class BaseBeeGoal extends Goal {
      BaseBeeGoal() {
         super();
      }

      public abstract boolean canBeeUse();

      public abstract boolean canBeeContinueToUse();

      public boolean canUse() {
         return this.canBeeUse() && !Bee.this.isAngry();
      }

      public boolean canContinueToUse() {
         return this.canBeeContinueToUse() && !Bee.this.isAngry();
      }
   }
}
