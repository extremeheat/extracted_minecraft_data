package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Drowned extends Zombie implements RangedAttackMob {
   private boolean searchingForLand;
   protected final WaterBoundPathNavigation waterNavigation;
   protected final GroundPathNavigation groundNavigation;

   public Drowned(EntityType<? extends Drowned> var1, Level var2) {
      super(var1, var2);
      this.maxUpStep = 1.0F;
      this.moveControl = new Drowned.DrownedMoveControl(this);
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.waterNavigation = new WaterBoundPathNavigation(this, var2);
      this.groundNavigation = new GroundPathNavigation(this, var2);
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(1, new Drowned.DrownedGoToWaterGoal(this, 1.0D));
      this.goalSelector.addGoal(2, new Drowned.DrownedTridentAttackGoal(this, 1.0D, 40, 10.0F));
      this.goalSelector.addGoal(2, new Drowned.DrownedAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(5, new Drowned.DrownedGoToBeachGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new Drowned.DrownedSwimUpGoal(this, 1.0D, this.level.getSeaLevel()));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Drowned.class})).setAlertOthers(ZombifiedPiglin.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, this::okTarget));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      var4 = super.finalizeSpawn(var1, var2, var3, var4, var5);
      if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
         this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
         this.handDropChances[EquipmentSlot.OFFHAND.getIndex()] = 2.0F;
      }

      return var4;
   }

   public static boolean checkDrownedSpawnRules(EntityType<Drowned> var0, ServerLevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      Optional var5 = var1.getBiomeName(var3);
      boolean var6 = var1.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(var1, var3, var4) && (var2 == MobSpawnType.SPAWNER || var1.getFluidState(var3).is(FluidTags.WATER));
      if (!Objects.equals(var5, Optional.of(Biomes.RIVER)) && !Objects.equals(var5, Optional.of(Biomes.FROZEN_RIVER))) {
         return var4.nextInt(40) == 0 && isDeepEnoughToSpawn(var1, var3) && var6;
      } else {
         return var4.nextInt(15) == 0 && var6;
      }
   }

   private static boolean isDeepEnoughToSpawn(LevelAccessor var0, BlockPos var1) {
      return var1.getY() < var0.getSeaLevel() - 5;
   }

   protected boolean supportsBreakDoorGoal() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.DROWNED_AMBIENT_WATER : SoundEvents.DROWNED_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isInWater() ? SoundEvents.DROWNED_HURT_WATER : SoundEvents.DROWNED_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isInWater() ? SoundEvents.DROWNED_DEATH_WATER : SoundEvents.DROWNED_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.DROWNED_STEP;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.DROWNED_SWIM;
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance var1) {
      if ((double)this.random.nextFloat() > 0.9D) {
         int var2 = this.random.nextInt(16);
         if (var2 < 10) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
         } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
         }
      }

   }

   protected boolean canReplaceCurrentItem(ItemStack var1, ItemStack var2) {
      if (var2.is(Items.NAUTILUS_SHELL)) {
         return false;
      } else if (var2.is(Items.TRIDENT)) {
         if (var1.is(Items.TRIDENT)) {
            return var1.getDamageValue() < var2.getDamageValue();
         } else {
            return false;
         }
      } else {
         return var1.is(Items.TRIDENT) ? true : super.canReplaceCurrentItem(var1, var2);
      }
   }

   protected boolean convertsInWater() {
      return false;
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   public boolean okTarget(@Nullable LivingEntity var1) {
      if (var1 != null) {
         return !this.level.isDay() || var1.isInWater();
      } else {
         return false;
      }
   }

   public boolean isPushedByFluid() {
      return !this.isSwimming();
   }

   private boolean wantsToSwim() {
      if (this.searchingForLand) {
         return true;
      } else {
         LivingEntity var1 = this.getTarget();
         return var1 != null && var1.isInWater();
      }
   }

   public void travel(Vec3 var1) {
      if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
         this.moveRelative(0.01F, var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
      } else {
         super.travel(var1);
      }

   }

   public void updateSwimming() {
      if (!this.level.isClientSide) {
         if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.navigation = this.waterNavigation;
            this.setSwimming(true);
         } else {
            this.navigation = this.groundNavigation;
            this.setSwimming(false);
         }
      }

   }

   protected boolean closeToNextPos() {
      Path var1 = this.getNavigation().getPath();
      if (var1 != null) {
         BlockPos var2 = var1.getTarget();
         if (var2 != null) {
            double var3 = this.distanceToSqr((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
            if (var3 < 4.0D) {
               return true;
            }
         }
      }

      return false;
   }

   public void performRangedAttack(LivingEntity var1, float var2) {
      ThrownTrident var3 = new ThrownTrident(this.level, this, new ItemStack(Items.TRIDENT));
      double var4 = var1.getX() - this.getX();
      double var6 = var1.getY(0.3333333333333333D) - var3.getY();
      double var8 = var1.getZ() - this.getZ();
      double var10 = (double)Mth.sqrt(var4 * var4 + var8 * var8);
      var3.shoot(var4, var6 + var10 * 0.20000000298023224D, var8, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(var3);
   }

   public void setSearchingForLand(boolean var1) {
      this.searchingForLand = var1;
   }

   static class DrownedMoveControl extends MoveControl {
      private final Drowned drowned;

      public DrownedMoveControl(Drowned var1) {
         super(var1);
         this.drowned = var1;
      }

      public void tick() {
         LivingEntity var1 = this.drowned.getTarget();
         if (this.drowned.wantsToSwim() && this.drowned.isInWater()) {
            if (var1 != null && var1.getY() > this.drowned.getY() || this.drowned.searchingForLand) {
               this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
            }

            if (this.operation != MoveControl.Operation.MOVE_TO || this.drowned.getNavigation().isDone()) {
               this.drowned.setSpeed(0.0F);
               return;
            }

            double var2 = this.wantedX - this.drowned.getX();
            double var4 = this.wantedY - this.drowned.getY();
            double var6 = this.wantedZ - this.drowned.getZ();
            double var8 = (double)Mth.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
            var4 /= var8;
            float var10 = (float)(Mth.atan2(var6, var2) * 57.2957763671875D) - 90.0F;
            this.drowned.yRot = this.rotlerp(this.drowned.yRot, var10, 90.0F);
            this.drowned.yBodyRot = this.drowned.yRot;
            float var11 = (float)(this.speedModifier * this.drowned.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float var12 = Mth.lerp(0.125F, this.drowned.getSpeed(), var11);
            this.drowned.setSpeed(var12);
            this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add((double)var12 * var2 * 0.005D, (double)var12 * var4 * 0.1D, (double)var12 * var6 * 0.005D));
         } else {
            if (!this.drowned.onGround) {
               this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
            }

            super.tick();
         }

      }
   }

   static class DrownedAttackGoal extends ZombieAttackGoal {
      private final Drowned drowned;

      public DrownedAttackGoal(Drowned var1, double var2, boolean var4) {
         super(var1, var2, var4);
         this.drowned = var1;
      }

      public boolean canUse() {
         return super.canUse() && this.drowned.okTarget(this.drowned.getTarget());
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.drowned.okTarget(this.drowned.getTarget());
      }
   }

   static class DrownedGoToWaterGoal extends Goal {
      private final PathfinderMob mob;
      private double wantedX;
      private double wantedY;
      private double wantedZ;
      private final double speedModifier;
      private final Level level;

      public DrownedGoToWaterGoal(PathfinderMob var1, double var2) {
         super();
         this.mob = var1;
         this.speedModifier = var2;
         this.level = var1.level;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (!this.level.isDay()) {
            return false;
         } else if (this.mob.isInWater()) {
            return false;
         } else {
            Vec3 var1 = this.getWaterPos();
            if (var1 == null) {
               return false;
            } else {
               this.wantedX = var1.x;
               this.wantedY = var1.y;
               this.wantedZ = var1.z;
               return true;
            }
         }
      }

      public boolean canContinueToUse() {
         return !this.mob.getNavigation().isDone();
      }

      public void start() {
         this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
      }

      @Nullable
      private Vec3 getWaterPos() {
         Random var1 = this.mob.getRandom();
         BlockPos var2 = this.mob.blockPosition();

         for(int var3 = 0; var3 < 10; ++var3) {
            BlockPos var4 = var2.offset(var1.nextInt(20) - 10, 2 - var1.nextInt(8), var1.nextInt(20) - 10);
            if (this.level.getBlockState(var4).is(Blocks.WATER)) {
               return Vec3.atBottomCenterOf(var4);
            }
         }

         return null;
      }
   }

   static class DrownedGoToBeachGoal extends MoveToBlockGoal {
      private final Drowned drowned;

      public DrownedGoToBeachGoal(Drowned var1, double var2) {
         super(var1, var2, 8, 2);
         this.drowned = var1;
      }

      public boolean canUse() {
         return super.canUse() && !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() >= (double)(this.drowned.level.getSeaLevel() - 3);
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse();
      }

      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         BlockPos var3 = var2.above();
         return var1.isEmptyBlock(var3) && var1.isEmptyBlock(var3.above()) ? var1.getBlockState(var2).entityCanStandOn(var1, var2, this.drowned) : false;
      }

      public void start() {
         this.drowned.setSearchingForLand(false);
         this.drowned.navigation = this.drowned.groundNavigation;
         super.start();
      }

      public void stop() {
         super.stop();
      }
   }

   static class DrownedSwimUpGoal extends Goal {
      private final Drowned drowned;
      private final double speedModifier;
      private final int seaLevel;
      private boolean stuck;

      public DrownedSwimUpGoal(Drowned var1, double var2, int var4) {
         super();
         this.drowned = var1;
         this.speedModifier = var2;
         this.seaLevel = var4;
      }

      public boolean canUse() {
         return !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() < (double)(this.seaLevel - 2);
      }

      public boolean canContinueToUse() {
         return this.canUse() && !this.stuck;
      }

      public void tick() {
         if (this.drowned.getY() < (double)(this.seaLevel - 1) && (this.drowned.getNavigation().isDone() || this.drowned.closeToNextPos())) {
            Vec3 var1 = DefaultRandomPos.getPosTowards(this.drowned, 4, 8, new Vec3(this.drowned.getX(), (double)(this.seaLevel - 1), this.drowned.getZ()), 1.5707963705062866D);
            if (var1 == null) {
               this.stuck = true;
               return;
            }

            this.drowned.getNavigation().moveTo(var1.x, var1.y, var1.z, this.speedModifier);
         }

      }

      public void start() {
         this.drowned.setSearchingForLand(true);
         this.stuck = false;
      }

      public void stop() {
         this.drowned.setSearchingForLand(false);
      }
   }

   static class DrownedTridentAttackGoal extends RangedAttackGoal {
      private final Drowned drowned;

      public DrownedTridentAttackGoal(RangedAttackMob var1, double var2, int var4, float var5) {
         super(var1, var2, var4, var5);
         this.drowned = (Drowned)var1;
      }

      public boolean canUse() {
         return super.canUse() && this.drowned.getMainHandItem().is(Items.TRIDENT);
      }

      public void start() {
         super.start();
         this.drowned.setAggressive(true);
         this.drowned.startUsingItem(InteractionHand.MAIN_HAND);
      }

      public void stop() {
         super.stop();
         this.drowned.stopUsingItem();
         this.drowned.setAggressive(false);
      }
   }
}
