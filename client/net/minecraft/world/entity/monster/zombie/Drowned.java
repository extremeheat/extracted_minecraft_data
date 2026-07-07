package net.minecraft.world.entity.monster.zombie;

import com.google.common.annotations.VisibleForTesting;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Drowned extends Zombie implements RangedAttackMob {
   public static final float NAUTILUS_SHELL_CHANCE = 0.03F;
   private static final float ZOMBIE_NAUTILUS_JOCKEY_CHANCE = 0.5F;
   private static final EntityDimensions BABY_DIMENSIONS;
   private boolean searchingForLand;
   private float rangedAttackUncertainty = -1.0F;

   public Drowned(final EntityType<? extends Drowned> type, final Level level) {
      super(type, level);
      this.moveControl = new DrownedMoveControl(this);
      this.setPathfindingMalus(PathType.WATER, 0.0F);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Zombie.createAttributes().add(Attributes.STEP_HEIGHT, 1.0);
   }

   protected PathNavigation createNavigation(final Level level) {
      return new AmphibiousPathNavigation(this, level);
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(1, new DrownedGoToWaterGoal(this, 1.0));
      this.goalSelector.addGoal(2, new DrownedTridentAttackGoal(this, 1.0, 40, 10.0F));
      this.goalSelector.addGoal(2, new DrownedAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(5, new DrownedGoToBeachGoal(this, 1.0));
      this.goalSelector.addGoal(6, new DrownedSwimUpGoal(this, 1.0, this.level().getSeaLevel()));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Drowned.class})).setAlertOthers(ZombifiedPiglin.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, 10, true, false, (target, level) -> this.okTarget(target)));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Axolotl.class, true, false));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
      groupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
      if (this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty() && level.getRandom().nextFloat() < 0.03F) {
         this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
         this.setGuaranteedDrop(EquipmentSlot.OFFHAND);
      }

      if ((spawnReason == EntitySpawnReason.NATURAL || spawnReason == EntitySpawnReason.STRUCTURE) && this.getMainHandItem().is(Items.TRIDENT) && level.getRandom().nextFloat() < 0.5F && !this.isBaby() && !level.getBiome(this.blockPosition()).is(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS)) {
         ZombieNautilus zombieNautilus = (ZombieNautilus)EntityTypes.ZOMBIE_NAUTILUS.create(this.level(), EntitySpawnReason.JOCKEY);
         if (zombieNautilus != null) {
            if (spawnReason == EntitySpawnReason.STRUCTURE) {
               zombieNautilus.setPersistenceRequired();
            }

            zombieNautilus.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            zombieNautilus.finalizeSpawn(level, difficulty, spawnReason, (SpawnGroupData)null);
            this.startRiding(zombieNautilus, false, false);
            level.addFreshEntity(zombieNautilus);
         }
      }

      return groupData;
   }

   public static boolean checkDrownedSpawnRules(final EntityType<Drowned> type, final ServerLevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      if (!level.getFluidState(pos.below()).is(FluidTags.WATER) && !EntitySpawnReason.isSpawner(spawnReason)) {
         return false;
      } else {
         Holder<Biome> biome = level.getBiome(pos);
         boolean canMonsterSpawn = level.getDifficulty() != Difficulty.PEACEFUL && (EntitySpawnReason.ignoresLightRequirements(spawnReason) || isDarkEnoughToSpawn(level, pos, random)) && (EntitySpawnReason.isSpawner(spawnReason) || level.getFluidState(pos).is(FluidTags.WATER));
         if (!canMonsterSpawn || !EntitySpawnReason.isSpawner(spawnReason) && spawnReason != EntitySpawnReason.REINFORCEMENT) {
            if (biome.is(BiomeTags.MORE_FREQUENT_DROWNED_SPAWNS)) {
               return random.nextInt(15) == 0 && canMonsterSpawn;
            } else {
               return random.nextInt(40) == 0 && isDeepEnoughToSpawn(level, pos) && canMonsterSpawn;
            }
         } else {
            return true;
         }
      }
   }

   private static boolean isDeepEnoughToSpawn(final LevelAccessor level, final BlockPos pos) {
      return pos.getY() < level.getSeaLevel() - 5;
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.DROWNED_AMBIENT_WATER : SoundEvents.DROWNED_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
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

   protected boolean canSpawnInLiquids() {
      return true;
   }

   protected void populateDefaultEquipmentSlots(final RandomSource random, final DifficultyInstance difficulty) {
      if ((double)random.nextFloat() > 0.9) {
         int rand = random.nextInt(16);
         if (rand < 10) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
         } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
         }
      }

   }

   protected boolean canReplaceCurrentItem(final ItemStack newItemStack, final ItemStack currentItemStack, final EquipmentSlot slot) {
      return currentItemStack.is(Items.NAUTILUS_SHELL) ? false : super.canReplaceCurrentItem(newItemStack, currentItemStack, slot);
   }

   protected boolean convertsInWater() {
      return false;
   }

   public boolean checkSpawnObstruction(final LevelReader level) {
      return level.isUnobstructed(this);
   }

   public boolean okTarget(final @Nullable LivingEntity target) {
      if (target != null) {
         return !this.level().isBrightOutside() || target.isInWater();
      } else {
         return false;
      }
   }

   public boolean isPushedByFluid() {
      return !this.isSwimming();
   }

   public boolean wantsToSwim() {
      if (this.searchingForLand) {
         return true;
      } else {
         LivingEntity target = this.getTarget();
         return target != null && target.isInWater();
      }
   }

   protected void travelInWater(final Vec3 input, final double baseGravity, final boolean isFalling, final double oldY) {
      if (this.isUnderWater() && this.wantsToSwim()) {
         this.moveRelative(0.01F, input);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
      } else {
         super.travelInWater(input, baseGravity, isFalling, oldY);
      }

   }

   public void updateSwimming() {
      if (!this.level().isClientSide()) {
         this.setSwimming(this.isEffectiveAi() && this.isUnderWater() && this.wantsToSwim());
      }

   }

   public boolean isVisuallySwimming() {
      return this.isSwimming() && !this.isPassenger();
   }

   protected boolean closeToNextPos() {
      Path path = this.getNavigation().getPath();
      if (path != null) {
         BlockPos pos = path.getTarget();
         if (pos != null) {
            double sqrDistToNextPos = this.distanceToSqr((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
            if (sqrDistToNextPos < 4.0) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean shouldDoRangedAttack() {
      if (!this.getMainHandItem().is(Items.TRIDENT)) {
         return false;
      } else if (this.hasTargetInRangedDistance()) {
         return true;
      } else {
         LivingEntity target = this.getTarget();
         if (target != null && target.isAlive()) {
            if (this.isWithinMeleeAttackRange(target)) {
               return false;
            } else {
               Path path = this.navigation.getPath() != null ? this.navigation.getPath() : this.navigation.createPath(target, 0);
               return path != null && path.getTarget().equals(target.blockPosition()) && !path.canReach();
            }
         } else {
            return false;
         }
      }
   }

   public void performRangedAttack(final LivingEntity target, final float power) {
      ItemStack mainHandItem = this.getMainHandItem();
      ItemStack tridentItemStack = mainHandItem.is(Items.TRIDENT) ? mainHandItem : new ItemStack(Items.TRIDENT);
      ThrownTrident trident = new ThrownTrident(this.level(), this, tridentItemStack);
      double xd = target.getX() - this.getX();
      double yd = target.getY(0.3333333333333333) - trident.getY();
      double zd = target.getZ() - this.getZ();
      double distanceToTarget = Math.sqrt(xd * xd + zd * zd);
      Level var15 = this.level();
      if (var15 instanceof ServerLevel serverLevel) {
         Projectile.spawnProjectileUsingShoot(trident, serverLevel, tridentItemStack, xd, yd + distanceToTarget * 0.20000000298023224, zd, 1.6F, this.rangedAttackUncertainty(serverLevel));
      }

      this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
   }

   public float rangedAttackUncertainty(final Level level) {
      return this.rangedAttackUncertainty < 0.0F ? RangedAttackMob.super.rangedAttackUncertainty(level) : this.rangedAttackUncertainty;
   }

   @VisibleForTesting
   public void setRangedAttackUncertainty(final float rangedAttackUncertainty) {
      this.rangedAttackUncertainty = rangedAttackUncertainty;
   }

   public TagKey<Item> getPreferredWeaponType() {
      return ItemTags.DROWNED_PREFERRED_WEAPONS;
   }

   public void setSearchingForLand(final boolean searchingForLand) {
      this.searchingForLand = searchingForLand;
   }

   public boolean isSearchingForLand() {
      return this.searchingForLand;
   }

   public void rideTick() {
      super.rideTick();
      Entity var2 = this.getControlledVehicle();
      if (var2 instanceof PathfinderMob entity) {
         this.yBodyRot = entity.yBodyRot;
      }

   }

   public boolean wantsToPickUp(final ServerLevel level, final ItemStack itemStack) {
      return itemStack.is(ItemTags.SPEARS) ? false : super.wantsToPickUp(level, itemStack);
   }

   static {
      BABY_DIMENSIONS = EntityDimensions.scalable(0.49F, 0.98F).withEyeHeight(0.775F).withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, 0.0F, 0.1875F, 0.0F));
   }

   private static class DrownedTridentAttackGoal extends RangedAttackGoal {
      private final Drowned drowned;

      public DrownedTridentAttackGoal(final RangedAttackMob mob, final double speedModifier, final int attackInterval, final float attackRadius) {
         super(mob, speedModifier, attackInterval, attackRadius);
         this.drowned = (Drowned)mob;
      }

      public boolean canUse() {
         return super.canUse() && this.drowned.shouldDoRangedAttack();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.drowned.shouldDoRangedAttack();
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

   private static class DrownedSwimUpGoal extends Goal {
      private final Drowned drowned;
      private final double speedModifier;
      private final int seaLevel;
      private boolean stuck;

      public DrownedSwimUpGoal(final Drowned drowned, final double speedModifier, final int seaLevel) {
         super();
         this.drowned = drowned;
         this.speedModifier = speedModifier;
         this.seaLevel = seaLevel;
      }

      public boolean canUse() {
         return !this.drowned.level().isBrightOutside() && this.drowned.isInWater() && this.drowned.getY() < (double)(this.seaLevel - 2);
      }

      public boolean canContinueToUse() {
         return this.canUse() && !this.stuck;
      }

      public void tick() {
         if (this.drowned.getY() < (double)(this.seaLevel - 1) && (this.drowned.getNavigation().isDone() || this.drowned.closeToNextPos())) {
            Vec3 nextPos = DefaultRandomPos.getPosTowards(this.drowned, 4, 8, new Vec3(this.drowned.getX(), (double)(this.seaLevel - 1), this.drowned.getZ()), 1.5707963705062866);
            if (nextPos == null) {
               this.stuck = true;
               return;
            }

            this.drowned.getNavigation().moveTo(nextPos.x, nextPos.y, nextPos.z, this.speedModifier);
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

   private static class DrownedGoToBeachGoal extends MoveToBlockGoal {
      private final Drowned drowned;

      public DrownedGoToBeachGoal(final Drowned drowned, final double speedModifier) {
         super(drowned, speedModifier, 8, 2);
         this.drowned = drowned;
      }

      public boolean canUse() {
         return super.canUse() && !this.drowned.level().isBrightOutside() && this.drowned.isInWater() && this.drowned.getY() >= (double)(this.drowned.level().getSeaLevel() - 3);
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse();
      }

      protected boolean isValidTarget(final LevelReader level, final BlockPos pos) {
         BlockPos above = pos.above();
         return level.isEmptyBlock(above) && level.isEmptyBlock(above.above()) ? level.getBlockState(pos).entityCanStandOn(level, pos, this.drowned) : false;
      }

      public void start() {
         this.drowned.setSearchingForLand(false);
         super.start();
      }

      public void stop() {
         super.stop();
      }
   }

   private static class DrownedGoToWaterGoal extends Goal {
      private final PathfinderMob mob;
      private double wantedX;
      private double wantedY;
      private double wantedZ;
      private final double speedModifier;
      private final Level level;

      public DrownedGoToWaterGoal(final PathfinderMob mob, final double speedModifier) {
         super();
         this.mob = mob;
         this.speedModifier = speedModifier;
         this.level = mob.level();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (!this.level.isBrightOutside()) {
            return false;
         } else if (this.mob.isInWater()) {
            return false;
         } else {
            Vec3 pos = this.getWaterPos();
            if (pos == null) {
               return false;
            } else {
               this.wantedX = pos.x;
               this.wantedY = pos.y;
               this.wantedZ = pos.z;
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

      private @Nullable Vec3 getWaterPos() {
         RandomSource random = this.mob.getRandom();
         BlockPos pos = this.mob.blockPosition();

         for(int i = 0; i < 10; ++i) {
            BlockPos randomPos = pos.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);
            if (this.level.getBlockState(randomPos).is(Blocks.WATER)) {
               return Vec3.atBottomCenterOf(randomPos);
            }
         }

         return null;
      }
   }

   private static class DrownedAttackGoal extends ZombieAttackGoal {
      private final Drowned drowned;

      public DrownedAttackGoal(final Drowned drowned, final double speedModifier, final boolean trackTarget) {
         super(drowned, speedModifier, trackTarget);
         this.drowned = drowned;
      }

      public boolean canUse() {
         return super.canUse() && this.drowned.okTarget(this.drowned.getTarget()) && !this.drowned.shouldDoRangedAttack();
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.drowned.okTarget(this.drowned.getTarget()) && !this.drowned.shouldDoRangedAttack();
      }

      public void start() {
         super.start();
         if (this.drowned.getMainHandItem().has(DataComponents.WEAPON)) {
            this.drowned.startUsingItem(InteractionHand.MAIN_HAND);
         }

      }

      public void stop() {
         super.stop();
         if (this.drowned.getMainHandItem().has(DataComponents.WEAPON)) {
            this.drowned.stopUsingItem();
         }

      }
   }

   private static class DrownedMoveControl<T extends Drowned> extends MoveControl<T> {
      public DrownedMoveControl(final T drowned) {
         super(drowned);
      }

      public void tick() {
         LivingEntity target = ((Drowned)this.mob).getTarget();
         if (((Drowned)this.mob).wantsToSwim() && ((Drowned)this.mob).isInWater()) {
            if (target != null && target.getY() > ((Drowned)this.mob).getY() || ((Drowned)this.mob).isSearchingForLand()) {
               ((Drowned)this.mob).setDeltaMovement(((Drowned)this.mob).getDeltaMovement().add(0.0, 0.002, 0.0));
            }

            if (this.operation != MoveControl.Operation.MOVE_TO || ((Drowned)this.mob).getNavigation().isDone()) {
               ((Drowned)this.mob).setSpeed(0.0F);
               return;
            }

            double xd = this.wantedX - ((Drowned)this.mob).getX();
            double yd = this.wantedY - ((Drowned)this.mob).getY();
            double zd = this.wantedZ - ((Drowned)this.mob).getZ();
            double dd = Math.sqrt(xd * xd + yd * yd + zd * zd);
            yd /= dd;
            float yRotD = (float)(Mth.atan2(zd, xd) * 57.2957763671875) - 90.0F;
            ((Drowned)this.mob).setYRot(this.rotlerp(((Drowned)this.mob).getYRot(), yRotD, 90.0F));
            (this.mob).yBodyRot = ((Drowned)this.mob).getYRot();
            float targetSpeed = (float)(this.speedModifier * ((Drowned)this.mob).getAttributeValue(Attributes.MOVEMENT_SPEED));
            float newSpeed = Mth.lerp(0.125F, ((Drowned)this.mob).getSpeed(), targetSpeed);
            ((Drowned)this.mob).setSpeed(newSpeed);
            ((Drowned)this.mob).setDeltaMovement(((Drowned)this.mob).getDeltaMovement().add((double)newSpeed * xd * 0.005, (double)newSpeed * yd * 0.1, (double)newSpeed * zd * 0.005));
         } else {
            if (!((Drowned)this.mob).onGround()) {
               ((Drowned)this.mob).setDeltaMovement(((Drowned)this.mob).getDeltaMovement().add(0.0, -0.008, 0.0));
            }

            super.tick();
         }

      }
   }
}
