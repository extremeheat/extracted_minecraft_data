package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
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
import net.minecraft.tags.Tag;
import net.minecraft.util.IntRange;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
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
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Bee extends Animal implements NeutralMob, FlyingAnimal {
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
   private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME;
   private static final IntRange PERSISTENT_ANGER_TIME;
   private UUID persistentAngerTarget;
   private float rollAmount;
   private float rollAmountO;
   private int timeSinceSting;
   private int ticksWithoutNectarSinceExitingHive;
   private int stayOutOfHiveCountdown;
   private int numCropsGrownSincePollination;
   private int remainingCooldownBeforeLocatingNewHive;
   private int remainingCooldownBeforeLocatingNewFlower;
   @Nullable
   private BlockPos savedFlowerPos;
   @Nullable
   private BlockPos hivePos;
   private Bee.BeePollinateGoal beePollinateGoal;
   private Bee.BeeGoToHiveGoal goToHiveGoal;
   private Bee.BeeGoToKnownFlowerGoal goToKnownFlowerGoal;
   private int underWaterTicks;

   public Bee(EntityType<? extends Bee> var1, Level var2) {
      super(var1, var2);
      this.remainingCooldownBeforeLocatingNewFlower = Mth.nextInt(this.random, 20, 60);
      this.moveControl = new FlyingMoveControl(this, 20, true);
      this.lookControl = new Bee.BeeLookControl(this);
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
      this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBlockState(var1).isAir() ? 10.0F : 0.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Bee.BeeAttackGoal(this, 1.399999976158142D, true));
      this.goalSelector.addGoal(1, new Bee.BeeEnterHiveGoal());
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of((Tag)ItemTags.FLOWERS), false));
      this.beePollinateGoal = new Bee.BeePollinateGoal();
      this.goalSelector.addGoal(4, this.beePollinateGoal);
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new Bee.BeeLocateHiveGoal());
      this.goToHiveGoal = new Bee.BeeGoToHiveGoal();
      this.goalSelector.addGoal(5, this.goToHiveGoal);
      this.goToKnownFlowerGoal = new Bee.BeeGoToKnownFlowerGoal();
      this.goalSelector.addGoal(6, this.goToKnownFlowerGoal);
      this.goalSelector.addGoal(7, new Bee.BeeGrowCropGoal());
      this.goalSelector.addGoal(8, new Bee.BeeWanderGoal());
      this.goalSelector.addGoal(9, new FloatGoal(this));
      this.targetSelector.addGoal(1, (new Bee.BeeHurtByOtherGoal(this)).setAlertOthers(new Class[0]));
      this.targetSelector.addGoal(2, new Bee.BeeBecomeAngryTargetGoal(this));
      this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal(this, true));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.hasHive()) {
         var1.put("HivePos", NbtUtils.writeBlockPos(this.getHivePos()));
      }

      if (this.hasSavedFlowerPos()) {
         var1.put("FlowerPos", NbtUtils.writeBlockPos(this.getSavedFlowerPos()));
      }

      var1.putBoolean("HasNectar", this.hasNectar());
      var1.putBoolean("HasStung", this.hasStung());
      var1.putInt("TicksSincePollination", this.ticksWithoutNectarSinceExitingHive);
      var1.putInt("CannotEnterHiveTicks", this.stayOutOfHiveCountdown);
      var1.putInt("CropsGrownSincePollination", this.numCropsGrownSincePollination);
      this.addPersistentAngerSaveData(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.hivePos = null;
      if (var1.contains("HivePos")) {
         this.hivePos = NbtUtils.readBlockPos(var1.getCompound("HivePos"));
      }

      this.savedFlowerPos = null;
      if (var1.contains("FlowerPos")) {
         this.savedFlowerPos = NbtUtils.readBlockPos(var1.getCompound("FlowerPos"));
      }

      super.readAdditionalSaveData(var1);
      this.setHasNectar(var1.getBoolean("HasNectar"));
      this.setHasStung(var1.getBoolean("HasStung"));
      this.ticksWithoutNectarSinceExitingHive = var1.getInt("TicksSincePollination");
      this.stayOutOfHiveCountdown = var1.getInt("CannotEnterHiveTicks");
      this.numCropsGrownSincePollination = var1.getInt("CropsGrownSincePollination");
      this.readPersistentAngerSaveData(this.level, var1);
   }

   public boolean doHurtTarget(Entity var1) {
      boolean var2 = var1.hurt(DamageSource.sting(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (var2) {
         this.doEnchantDamageEffects(this, var1);
         if (var1 instanceof LivingEntity) {
            ((LivingEntity)var1).setStingerCount(((LivingEntity)var1).getStingerCount() + 1);
            byte var3 = 0;
            if (this.level.getDifficulty() == Difficulty.NORMAL) {
               var3 = 10;
            } else if (this.level.getDifficulty() == Difficulty.HARD) {
               var3 = 18;
            }

            if (var3 > 0) {
               ((LivingEntity)var1).addEffect(new MobEffectInstance(MobEffects.POISON, var3 * 20, 0));
            }
         }

         this.setHasStung(true);
         this.stopBeingAngry();
         this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
      }

      return var2;
   }

   public void tick() {
      super.tick();
      if (this.hasNectar() && this.getCropsGrownSincePollination() < 10 && this.random.nextFloat() < 0.05F) {
         for(int var1 = 0; var1 < this.random.nextInt(2) + 1; ++var1) {
            this.spawnFluidParticle(this.level, this.getX() - 0.30000001192092896D, this.getX() + 0.30000001192092896D, this.getZ() - 0.30000001192092896D, this.getZ() + 0.30000001192092896D, this.getY(0.5D), ParticleTypes.FALLING_NECTAR);
         }
      }

      this.updateRollAmount();
   }

   private void spawnFluidParticle(Level var1, double var2, double var4, double var6, double var8, double var10, ParticleOptions var12) {
      var1.addParticle(var12, Mth.lerp(var1.random.nextDouble(), var2, var4), var10, Mth.lerp(var1.random.nextDouble(), var6, var8), 0.0D, 0.0D, 0.0D);
   }

   private void pathfindRandomlyTowards(BlockPos var1) {
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

      Vec3 var9 = AirRandomPos.getPosTowards(this, var6, var7, var3, var2, 0.3141592741012573D);
      if (var9 != null) {
         this.navigation.setMaxVisitedNodesMultiplier(0.5F);
         this.navigation.moveTo(var9.x, var9.y, var9.z, 1.0D);
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

   private boolean isTiredOfLookingForNectar() {
      return this.ticksWithoutNectarSinceExitingHive > 3600;
   }

   private boolean wantsToEnterHive() {
      if (this.stayOutOfHiveCountdown <= 0 && !this.beePollinateGoal.isPollinating() && !this.hasStung() && this.getTarget() == null) {
         boolean var1 = this.isTiredOfLookingForNectar() || this.level.isRaining() || this.level.isNight() || this.hasNectar();
         return var1 && !this.isHiveNearFire();
      } else {
         return false;
      }
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

   protected void customServerAiStep() {
      boolean var1 = this.hasStung();
      if (this.isInWaterOrBubble()) {
         ++this.underWaterTicks;
      } else {
         this.underWaterTicks = 0;
      }

      if (this.underWaterTicks > 20) {
         this.hurt(DamageSource.DROWN, 1.0F);
      }

      if (var1) {
         ++this.timeSinceSting;
         if (this.timeSinceSting % 5 == 0 && this.random.nextInt(Mth.clamp(1200 - this.timeSinceSting, 1, 1200)) == 0) {
            this.hurt(DamageSource.GENERIC, this.getHealth());
         }
      }

      if (!this.hasNectar()) {
         ++this.ticksWithoutNectarSinceExitingHive;
      }

      if (!this.level.isClientSide) {
         this.updatePersistentAnger((ServerLevel)this.level, false);
      }

   }

   public void resetTicksWithoutNectarSinceExitingHive() {
      this.ticksWithoutNectarSinceExitingHive = 0;
   }

   private boolean isHiveNearFire() {
      if (this.hivePos == null) {
         return false;
      } else {
         BlockEntity var1 = this.level.getBlockEntity(this.hivePos);
         return var1 instanceof BeehiveBlockEntity && ((BeehiveBlockEntity)var1).isFireNearby();
      }
   }

   public int getRemainingPersistentAngerTime() {
      return (Integer)this.entityData.get(DATA_REMAINING_ANGER_TIME);
   }

   public void setRemainingPersistentAngerTime(int var1) {
      this.entityData.set(DATA_REMAINING_ANGER_TIME, var1);
   }

   public UUID getPersistentAngerTarget() {
      return this.persistentAngerTarget;
   }

   public void setPersistentAngerTarget(@Nullable UUID var1) {
      this.persistentAngerTarget = var1;
   }

   public void startPersistentAngerTimer() {
      this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.randomValue(this.random));
   }

   private boolean doesHiveHaveSpace(BlockPos var1) {
      BlockEntity var2 = this.level.getBlockEntity(var1);
      if (var2 instanceof BeehiveBlockEntity) {
         return !((BeehiveBlockEntity)var2).isFull();
      } else {
         return false;
      }
   }

   public boolean hasHive() {
      return this.hivePos != null;
   }

   @Nullable
   public BlockPos getHivePos() {
      return this.hivePos;
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendBeeInfo(this);
   }

   private int getCropsGrownSincePollination() {
      return this.numCropsGrownSincePollination;
   }

   private void resetNumCropsGrownSincePollination() {
      this.numCropsGrownSincePollination = 0;
   }

   private void incrementNumCropsGrownSincePollination() {
      ++this.numCropsGrownSincePollination;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide) {
         if (this.stayOutOfHiveCountdown > 0) {
            --this.stayOutOfHiveCountdown;
         }

         if (this.remainingCooldownBeforeLocatingNewHive > 0) {
            --this.remainingCooldownBeforeLocatingNewHive;
         }

         if (this.remainingCooldownBeforeLocatingNewFlower > 0) {
            --this.remainingCooldownBeforeLocatingNewFlower;
         }

         boolean var1 = this.isAngry() && !this.hasStung() && this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0D;
         this.setRolling(var1);
         if (this.tickCount % 20 == 0 && !this.isHiveValid()) {
            this.hivePos = null;
         }
      }

   }

   private boolean isHiveValid() {
      if (!this.hasHive()) {
         return false;
      } else {
         BlockEntity var1 = this.level.getBlockEntity(this.hivePos);
         return var1 != null && var1.getType() == BlockEntityType.BEEHIVE;
      }
   }

   public boolean hasNectar() {
      return this.getFlag(8);
   }

   private void setHasNectar(boolean var1) {
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

   private boolean isTooFarAway(BlockPos var1) {
      return !this.closerThan(var1, 32);
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
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.FLYING_SPEED, 0.6000000238418579D).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896D).add(Attributes.ATTACK_DAMAGE, 2.0D).add(Attributes.FOLLOW_RANGE, 48.0D);
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
      var2.setCanPassDoors(true);
      return var2;
   }

   public boolean isFood(ItemStack var1) {
      return var1.is((Tag)ItemTags.FLOWERS);
   }

   private boolean isFlowerValid(BlockPos var1) {
      return this.level.isLoaded(var1) && this.level.getBlockState(var1).is(BlockTags.FLOWERS);
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

   public Bee getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (Bee)EntityType.BEE.create(var1);
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return this.isBaby() ? var2.height * 0.5F : var2.height * 0.5F;
   }

   public boolean causeFallDamage(float var1, float var2) {
      return false;
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   protected boolean makeFlySound() {
      return true;
   }

   public void dropOffNectar() {
      this.setHasNectar(false);
      this.resetNumCropsGrownSincePollination();
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         Entity var3 = var1.getEntity();
         if (!this.level.isClientSide) {
            this.beePollinateGoal.stopPollinating();
         }

         return super.hurt(var1, var2);
      }
   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }

   protected void jumpInLiquid(Tag<Fluid> var1) {
      this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.01D, 0.0D));
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.2F));
   }

   private boolean closerThan(BlockPos var1, int var2) {
      return var1.closerThan(this.blockPosition(), (double)var2);
   }

   // $FF: synthetic method
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.defineId(Bee.class, EntityDataSerializers.BYTE);
      DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Bee.class, EntityDataSerializers.INT);
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   }

   class BeeEnterHiveGoal extends Bee.BaseBeeGoal {
      private BeeEnterHiveGoal() {
         super(null);
      }

      public boolean canBeeUse() {
         if (Bee.this.hasHive() && Bee.this.wantsToEnterHive() && Bee.this.hivePos.closerThan(Bee.this.position(), 2.0D)) {
            BlockEntity var1 = Bee.this.level.getBlockEntity(Bee.this.hivePos);
            if (var1 instanceof BeehiveBlockEntity) {
               BeehiveBlockEntity var2 = (BeehiveBlockEntity)var1;
               if (!var2.isFull()) {
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
         BlockEntity var1 = Bee.this.level.getBlockEntity(Bee.this.hivePos);
         if (var1 instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity var2 = (BeehiveBlockEntity)var1;
            var2.addOccupant(Bee.this, Bee.this.hasNectar());
         }

      }

      // $FF: synthetic method
      BeeEnterHiveGoal(Object var2) {
         this();
      }
   }

   class BeeAttackGoal extends MeleeAttackGoal {
      BeeAttackGoal(PathfinderMob var2, double var3, boolean var5) {
         super(var2, var3, var5);
      }

      public boolean canUse() {
         return super.canUse() && Bee.this.isAngry() && !Bee.this.hasStung();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && Bee.this.isAngry() && !Bee.this.hasStung();
      }
   }

   class BeeGrowCropGoal extends Bee.BaseBeeGoal {
      private BeeGrowCropGoal() {
         super(null);
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
         if (Bee.this.random.nextInt(30) == 0) {
            for(int var1 = 1; var1 <= 2; ++var1) {
               BlockPos var2 = Bee.this.blockPosition().below(var1);
               BlockState var3 = Bee.this.level.getBlockState(var2);
               Block var4 = var3.getBlock();
               boolean var5 = false;
               IntegerProperty var6 = null;
               if (var3.is(BlockTags.BEE_GROWABLES)) {
                  if (var4 instanceof CropBlock) {
                     CropBlock var7 = (CropBlock)var4;
                     if (!var7.isMaxAge(var3)) {
                        var5 = true;
                        var6 = var7.getAgeProperty();
                     }
                  } else {
                     int var8;
                     if (var4 instanceof StemBlock) {
                        var8 = (Integer)var3.getValue(StemBlock.AGE);
                        if (var8 < 7) {
                           var5 = true;
                           var6 = StemBlock.AGE;
                        }
                     } else if (var3.is(Blocks.SWEET_BERRY_BUSH)) {
                        var8 = (Integer)var3.getValue(SweetBerryBushBlock.AGE);
                        if (var8 < 3) {
                           var5 = true;
                           var6 = SweetBerryBushBlock.AGE;
                        }
                     }
                  }

                  if (var5) {
                     Bee.this.level.levelEvent(2005, var2, 0);
                     Bee.this.level.setBlockAndUpdate(var2, (BlockState)var3.setValue(var6, (Integer)var3.getValue(var6) + 1));
                     Bee.this.incrementNumCropsGrownSincePollination();
                  }
               }
            }

         }
      }

      // $FF: synthetic method
      BeeGrowCropGoal(Object var2) {
         this();
      }
   }

   class BeeLocateHiveGoal extends Bee.BaseBeeGoal {
      private BeeLocateHiveGoal() {
         super(null);
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
         PoiManager var2 = ((ServerLevel)Bee.this.level).getPoiManager();
         Stream var3 = var2.getInRange((var0) -> {
            return var0 == PoiType.BEEHIVE || var0 == PoiType.BEE_NEST;
         }, var1, 20, PoiManager.Occupancy.ANY);
         return (List)var3.map(PoiRecord::getPos).filter((var1x) -> {
            return Bee.this.doesHiveHaveSpace(var1x);
         }).sorted(Comparator.comparingDouble((var1x) -> {
            return var1x.distSqr(var1);
         })).collect(Collectors.toList());
      }

      // $FF: synthetic method
      BeeLocateHiveGoal(Object var2) {
         this();
      }
   }

   class BeePollinateGoal extends Bee.BaseBeeGoal {
      private final Predicate<BlockState> VALID_POLLINATION_BLOCKS = (var0) -> {
         if (var0.is(BlockTags.TALL_FLOWERS)) {
            if (var0.is(Blocks.SUNFLOWER)) {
               return var0.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER;
            } else {
               return true;
            }
         } else {
            return var0.is(BlockTags.SMALL_FLOWERS);
         }
      };
      private int successfulPollinatingTicks;
      private int lastSoundPlayedTick;
      private boolean pollinating;
      private Vec3 hoverPos;
      private int pollinatingTicks;

      BeePollinateGoal() {
         super(null);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canBeeUse() {
         if (Bee.this.remainingCooldownBeforeLocatingNewFlower > 0) {
            return false;
         } else if (Bee.this.hasNectar()) {
            return false;
         } else if (Bee.this.level.isRaining()) {
            return false;
         } else {
            Optional var1 = this.findNearbyFlower();
            if (var1.isPresent()) {
               Bee.this.savedFlowerPos = (BlockPos)var1.get();
               Bee.this.navigation.moveTo((double)Bee.this.savedFlowerPos.getX() + 0.5D, (double)Bee.this.savedFlowerPos.getY() + 0.5D, (double)Bee.this.savedFlowerPos.getZ() + 0.5D, 1.2000000476837158D);
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
         } else if (Bee.this.level.isRaining()) {
            return false;
         } else if (this.hasPollinatedLongEnough()) {
            return Bee.this.random.nextFloat() < 0.2F;
         } else if (Bee.this.tickCount % 20 == 0 && !Bee.this.isFlowerValid(Bee.this.savedFlowerPos)) {
            Bee.this.savedFlowerPos = null;
            return false;
         } else {
            return true;
         }
      }

      private boolean hasPollinatedLongEnough() {
         return this.successfulPollinatingTicks > 400;
      }

      private boolean isPollinating() {
         return this.pollinating;
      }

      private void stopPollinating() {
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

      public void tick() {
         ++this.pollinatingTicks;
         if (this.pollinatingTicks > 600) {
            Bee.this.savedFlowerPos = null;
         } else {
            Vec3 var1 = Vec3.atBottomCenterOf(Bee.this.savedFlowerPos).add(0.0D, 0.6000000238418579D, 0.0D);
            if (var1.distanceTo(Bee.this.position()) > 1.0D) {
               this.hoverPos = var1;
               this.setWantedPos();
            } else {
               if (this.hoverPos == null) {
                  this.hoverPos = var1;
               }

               boolean var2 = Bee.this.position().distanceTo(this.hoverPos) <= 0.1D;
               boolean var3 = true;
               if (!var2 && this.pollinatingTicks > 600) {
                  Bee.this.savedFlowerPos = null;
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

      private void setWantedPos() {
         Bee.this.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), 0.3499999940395355D);
      }

      private float getOffset() {
         return (Bee.this.random.nextFloat() * 2.0F - 1.0F) * 0.33333334F;
      }

      private Optional<BlockPos> findNearbyFlower() {
         return this.findNearestBlock(this.VALID_POLLINATION_BLOCKS, 5.0D);
      }

      private Optional<BlockPos> findNearestBlock(Predicate<BlockState> var1, double var2) {
         BlockPos var4 = Bee.this.blockPosition();
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(int var6 = 0; (double)var6 <= var2; var6 = var6 > 0 ? -var6 : 1 - var6) {
            for(int var7 = 0; (double)var7 < var2; ++var7) {
               for(int var8 = 0; var8 <= var7; var8 = var8 > 0 ? -var8 : 1 - var8) {
                  for(int var9 = var8 < var7 && var8 > -var7 ? var7 : 0; var9 <= var7; var9 = var9 > 0 ? -var9 : 1 - var9) {
                     var5.setWithOffset(var4, var8, var6 - 1, var9);
                     if (var4.closerThan(var5, var2) && var1.test(Bee.this.level.getBlockState(var5))) {
                        return Optional.of(var5);
                     }
                  }
               }
            }
         }

         return Optional.empty();
      }
   }

   class BeeLookControl extends LookControl {
      BeeLookControl(Mob var2) {
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

   public class BeeGoToKnownFlowerGoal extends Bee.BaseBeeGoal {
      private int travellingTicks;

      BeeGoToKnownFlowerGoal() {
         super(null);
         this.travellingTicks = Bee.this.level.random.nextInt(10);
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canBeeUse() {
         return Bee.this.savedFlowerPos != null && !Bee.this.hasRestriction() && this.wantsToGoToKnownFlower() && Bee.this.isFlowerValid(Bee.this.savedFlowerPos) && !Bee.this.closerThan(Bee.this.savedFlowerPos, 2);
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
            if (this.travellingTicks > 600) {
               Bee.this.savedFlowerPos = null;
            } else if (!Bee.this.navigation.isInProgress()) {
               if (Bee.this.isTooFarAway(Bee.this.savedFlowerPos)) {
                  Bee.this.savedFlowerPos = null;
               } else {
                  Bee.this.pathfindRandomlyTowards(Bee.this.savedFlowerPos);
               }
            }
         }
      }

      private boolean wantsToGoToKnownFlower() {
         return Bee.this.ticksWithoutNectarSinceExitingHive > 2400;
      }
   }

   public class BeeGoToHiveGoal extends Bee.BaseBeeGoal {
      private int travellingTicks;
      private final List<BlockPos> blacklistedTargets;
      @Nullable
      private Path lastPath;
      private int ticksStuck;

      BeeGoToHiveGoal() {
         super(null);
         this.travellingTicks = Bee.this.level.random.nextInt(10);
         this.blacklistedTargets = Lists.newArrayList();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canBeeUse() {
         return Bee.this.hivePos != null && !Bee.this.hasRestriction() && Bee.this.wantsToEnterHive() && !this.hasReachedTarget(Bee.this.hivePos) && Bee.this.level.getBlockState(Bee.this.hivePos).is(BlockTags.BEEHIVES);
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
            if (this.travellingTicks > 600) {
               this.dropAndBlacklistHive();
            } else if (!Bee.this.navigation.isInProgress()) {
               if (!Bee.this.closerThan(Bee.this.hivePos, 16)) {
                  if (Bee.this.isTooFarAway(Bee.this.hivePos)) {
                     this.dropHive();
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
                        this.dropHive();
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
         Bee.this.navigation.setMaxVisitedNodesMultiplier(10.0F);
         Bee.this.navigation.moveTo((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), 1.0D);
         return Bee.this.navigation.getPath() != null && Bee.this.navigation.getPath().canReach();
      }

      private boolean isTargetBlacklisted(BlockPos var1) {
         return this.blacklistedTargets.contains(var1);
      }

      private void blacklistTarget(BlockPos var1) {
         this.blacklistedTargets.add(var1);

         while(this.blacklistedTargets.size() > 3) {
            this.blacklistedTargets.remove(0);
         }

      }

      private void clearBlacklist() {
         this.blacklistedTargets.clear();
      }

      private void dropAndBlacklistHive() {
         if (Bee.this.hivePos != null) {
            this.blacklistTarget(Bee.this.hivePos);
         }

         this.dropHive();
      }

      private void dropHive() {
         Bee.this.hivePos = null;
         Bee.this.remainingCooldownBeforeLocatingNewHive = 200;
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

   class BeeWanderGoal extends Goal {
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
            Bee.this.navigation.moveTo(Bee.this.navigation.createPath((BlockPos)(new BlockPos(var1)), 1), 1.0D);
         }

      }

      @Nullable
      private Vec3 findPos() {
         Vec3 var1;
         if (Bee.this.isHiveValid() && !Bee.this.closerThan(Bee.this.hivePos, 22)) {
            Vec3 var2 = Vec3.atCenterOf(Bee.this.hivePos);
            var1 = var2.subtract(Bee.this.position()).normalize();
         } else {
            var1 = Bee.this.getViewVector(0.0F);
         }

         boolean var4 = true;
         Vec3 var3 = HoverRandomPos.getPos(Bee.this, 8, 7, var1.x, var1.z, 1.5707964F, 3, 1);
         return var3 != null ? var3 : AirAndWaterRandomPos.getPos(Bee.this, 8, 4, -2, var1.x, var1.z, 1.5707963705062866D);
      }
   }

   abstract class BaseBeeGoal extends Goal {
      private BaseBeeGoal() {
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

      // $FF: synthetic method
      BaseBeeGoal(Object var2) {
         this();
      }
   }

   static class BeeBecomeAngryTargetGoal extends NearestAttackableTargetGoal<Player> {
      BeeBecomeAngryTargetGoal(Bee var1) {
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

   class BeeHurtByOtherGoal extends HurtByTargetGoal {
      BeeHurtByOtherGoal(Bee var2) {
         super(var2);
      }

      public boolean canContinueToUse() {
         return Bee.this.isAngry() && super.canContinueToUse();
      }

      protected void alertOther(Mob var1, LivingEntity var2) {
         if (var1 instanceof Bee && this.mob.canSee(var2)) {
            var1.setTarget(var2);
         }

      }
   }
}
