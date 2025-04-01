package net.minecraft.world.entity.pets;

import java.util.Objects;
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
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class PetBee extends AbstractPet implements NeutralMob, FlyingAnimal {
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
   public static final boolean DEFAULT_HAS_NECTAR = false;
   private static final boolean DEFAULT_HAS_STUNG = false;
   private static final int DEFAULT_TICKS_SINCE_POLLINATION = 0;
   private static final int DEFAULT_CANNOT_ENTER_HIVE_TICKS = 0;
   private static final int DEFAULT_CROPS_GROWN_SINCE_POLLINATION = 0;
   private static final UniformInt PERSISTENT_ANGER_TIME;
   @Nullable
   private UUID persistentAngerTarget;
   private float rollAmount;
   private float rollAmountO;
   private int timeSinceSting;
   private int underWaterTicks;

   public PetBee(EntityType<? extends PetBee> var1, Level var2) {
      super(var1, var2);
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

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.addPersistentAngerSaveData(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
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

         this.stopBeingAngry();
         this.playSound(SoundEvents.BEE_STING, 1.0F, 1.0F);
      }

      return var4;
   }

   public void tick() {
      super.tick();
      this.updateRollAmount();
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

      Vec3 var9 = AirRandomPos.getPosTowards(this, var6, var7, var3, var2, 0.3141592741012573);
      if (var9 != null) {
         this.navigation.setMaxVisitedNodesMultiplier(0.5F);
         this.navigation.moveTo(var9.x, var9.y, var9.z, 1.0);
      }
   }

   public static boolean isNightOrRaining(Level var0) {
      return var0.dimensionType().hasSkyLight() && (var0.isDarkOutside() || var0.isRaining());
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
      if (this.isInWater()) {
         ++this.underWaterTicks;
      } else {
         this.underWaterTicks = 0;
      }

      if (this.underWaterTicks > 20) {
         this.hurtServer(var1, this.damageSources().drown(), 1.0F);
      }

      this.updatePersistentAnger(var1, false);
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
   public GoalSelector getGoalSelector() {
      return this.goalSelector;
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
   }

   private boolean isRolling() {
      return this.getFlag(2);
   }

   private void setRolling(boolean var1) {
      this.setFlag(2, var1);
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

   protected PathNavigation createNavigation(Level var1) {
      FlyingPathNavigation var2 = new FlyingPathNavigation(this, var1) {
         public boolean isStableDestination(BlockPos var1) {
            return !this.level.getBlockState(var1.below()).isAir();
         }

         public void tick() {
            super.tick();
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

   protected void playStepSound(BlockPos var1, BlockState var2) {
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   @Nullable
   public Bee getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.BEE.create(var1, EntitySpawnReason.BREEDING);
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   public boolean isFlapping() {
      return this.isFlying() && this.tickCount % TICKS_PER_FLAP == 0;
   }

   public boolean isFlying() {
      return !this.onGround();
   }

   protected void jumpInLiquid(TagKey<Fluid> var1) {
      this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.01, 0.0));
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.2F));
   }

   private boolean closerThan(BlockPos var1, int var2) {
      return var1.closerThan(this.blockPosition(), (double)var2);
   }

   public static boolean attractsBees(BlockState var0) {
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

   public boolean isAngryAt(LivingEntity var1, ServerLevel var2) {
      if (!this.canAttack(var1)) {
         return false;
      } else {
         return var1.getType() == EntityType.PLAYER ? true : var1.getUUID().equals(this.getPersistentAngerTarget());
      }
   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.<Byte>defineId(PetBee.class, EntityDataSerializers.BYTE);
      DATA_REMAINING_ANGER_TIME = SynchedEntityData.<Integer>defineId(PetBee.class, EntityDataSerializers.INT);
      PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
   }

   class BeeHurtByOtherGoal extends HurtByTargetGoal {
      BeeHurtByOtherGoal(final Bee var2) {
         super(var2);
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse();
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
         return !var1.hasStung();
      }
   }

   abstract class BaseBeeGoal extends Goal {
      private BaseBeeGoal() {
         super();
      }

      public abstract boolean canBeeUse();

      public abstract boolean canBeeContinueToUse();

      public boolean canUse() {
         return false;
      }

      public boolean canContinueToUse() {
         return false;
      }
   }

   class BeeLookControl extends LookControl {
      BeeLookControl(final Mob var2) {
         super(var2);
      }

      public void tick() {
      }
   }
}
