package net.minecraft.world.entity.animal.turtle;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Turtle extends Animal {
   private static final EntityDataAccessor<Boolean> HAS_EGG;
   private static final EntityDataAccessor<Boolean> LAYING_EGG;
   private static final float BABY_SCALE = 0.3F;
   private static final EntityDimensions BABY_DIMENSIONS;
   private static final boolean DEFAULT_HAS_EGG = false;
   private int layEggCounter;
   public static final TargetingConditions.Selector BABY_ON_LAND_SELECTOR;
   private BlockPos homePos;
   private @Nullable BlockPos travelPos;
   private boolean goingHome;

   public Turtle(final EntityType<? extends Turtle> type, final Level level) {
      super(type, level);
      this.homePos = BlockPos.ZERO;
      this.setPathfindingMalus(PathType.WATER, 0.0F);
      this.setPathfindingMalus(PathType.DOOR_IRON_CLOSED, -1.0F);
      this.setPathfindingMalus(PathType.DOOR_WOOD_CLOSED, -1.0F);
      this.setPathfindingMalus(PathType.DOOR_OPEN, -1.0F);
      this.moveControl = new TurtleMoveControl(this);
   }

   public void setHomePos(final BlockPos pos) {
      this.homePos = pos;
   }

   public BlockPos getHomePos() {
      return this.homePos;
   }

   public boolean hasEgg() {
      return (Boolean)this.entityData.get(HAS_EGG);
   }

   private void setHasEgg(final boolean onOff) {
      this.entityData.set(HAS_EGG, onOff);
   }

   public boolean isLayingEgg() {
      return (Boolean)this.entityData.get(LAYING_EGG);
   }

   private void setLayingEgg(final boolean on) {
      this.layEggCounter = on ? 1 : 0;
      this.entityData.set(LAYING_EGG, on);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(HAS_EGG, false);
      entityData.define(LAYING_EGG, false);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("home_pos", BlockPos.CODEC, this.homePos);
      output.putBoolean("has_egg", this.hasEgg());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      this.setHomePos((BlockPos)input.read("home_pos", BlockPos.CODEC).orElse(this.blockPosition()));
      super.readAdditionalSaveData(input);
      this.setHasEgg(input.getBooleanOr("has_egg", false));
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      this.setHomePos(this.blockPosition());
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   public static boolean checkTurtleSpawnRules(final EntityType<Turtle> type, final LevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      return pos.getY() < level.getSeaLevel() + 4 && TurtleEggBlock.onSand(level, pos) && isBrightEnoughToSpawn(level, pos);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new TurtlePanicGoal(this, 1.2));
      this.goalSelector.addGoal(1, new TurtleBreedGoal(this, 1.0));
      this.goalSelector.addGoal(1, new TurtleLayEggGoal(this, 1.0));
      this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, (i) -> i.is(ItemTags.TURTLE_FOOD), false));
      this.goalSelector.addGoal(3, new TurtleGoToWaterGoal(this, 1.0));
      this.goalSelector.addGoal(4, new TurtleGoHomeGoal(this, 1.0));
      this.goalSelector.addGoal(7, new TurtleTravelGoal(this, 1.0));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(9, new TurtleRandomStrollGoal(this, 1.0, 100));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.STEP_HEIGHT, 1.0);
   }

   public boolean isPushedByFluid() {
      return false;
   }

   public int getAmbientSoundInterval() {
      return 200;
   }

   protected @Nullable SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround() && !this.isBaby() ? SoundEvents.TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   protected void playSwimSound(final float volume) {
      super.playSwimSound(volume * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.TURTLE_SWIM;
   }

   protected @Nullable SoundEvent getHurtSound(final DamageSource source) {
      return this.isBaby() ? SoundEvents.TURTLE_HURT_BABY : SoundEvents.TURTLE_HURT;
   }

   protected @Nullable SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.TURTLE_DEATH_BABY : SoundEvents.TURTLE_DEATH;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      SoundEvent sound = this.isBaby() ? SoundEvents.TURTLE_SHAMBLE_BABY : SoundEvents.TURTLE_SHAMBLE;
      this.playSound(sound, 0.15F, 1.0F);
   }

   public boolean canFallInLove() {
      return super.canFallInLove() && !this.hasEgg();
   }

   protected float nextStep() {
      return this.moveDist + 0.15F;
   }

   public float getAgeScale() {
      return this.isBaby() ? 0.3F : 1.0F;
   }

   protected PathNavigation createNavigation(final Level level) {
      return new TurtlePathNavigation(this, level);
   }

   public @Nullable AgeableMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      return EntityTypes.TURTLE.create(level, EntitySpawnReason.BREEDING);
   }

   public boolean isFood(final ItemStack itemStack) {
      return itemStack.is(ItemTags.TURTLE_FOOD);
   }

   public float getWalkTargetValue(final BlockPos pos, final LevelReader level) {
      if (!this.goingHome && level.getFluidState(pos).is(FluidTags.WATER)) {
         return 10.0F;
      } else {
         return TurtleEggBlock.onSand(level, pos) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
      }
   }

   public void aiStep() {
      super.aiStep();
      if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
         BlockPos pos = this.blockPosition();
         if (TurtleEggBlock.onSand(this.level(), pos)) {
            this.level().levelEvent(2001, pos, Block.getId(this.level().getBlockState(pos.below())));
            this.gameEvent(GameEvent.ENTITY_ACTION);
         }
      }

   }

   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (!this.isBaby()) {
         Level var2 = this.level();
         if (var2 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var2;
            if ((Boolean)level.getGameRules().get(GameRules.MOB_DROPS)) {
               this.dropFromGiftLootTable(level, BuiltInLootTables.TURTLE_GROW, this::spawnAtLocation);
            }
         }
      }

   }

   protected void travelInWater(final Vec3 input, final double baseGravity, final boolean isFalling, final double oldY) {
      this.moveRelative(0.1F, input);
      this.move(MoverType.SELF, this.getDeltaMovement());
      this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
      if (this.getTarget() == null && (!this.goingHome || !this.homePos.closerToCenterThan(this.position(), 20.0))) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
      }

   }

   public boolean canBeLeashed() {
      return false;
   }

   public void thunderHit(final ServerLevel level, final LightningBolt lightningBolt) {
      this.hurtServer(level, this.damageSources().lightningBolt(), 3.4028235E38F);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   static {
      HAS_EGG = SynchedEntityData.<Boolean>defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
      LAYING_EGG = SynchedEntityData.<Boolean>defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
      BABY_DIMENSIONS = EntityTypes.TURTLE.getDimensions().withAttachments(EntityAttachments.builder().attach(EntityAttachment.PASSENGER, 0.0F, EntityTypes.TURTLE.getHeight(), -0.25F)).scale(0.3F);
      BABY_ON_LAND_SELECTOR = (target, level) -> target.isBaby() && !target.isInWater();
   }

   private static class TurtlePanicGoal extends PanicGoal {
      TurtlePanicGoal(final Turtle turtle, final double speedModifier) {
         super(turtle, speedModifier);
      }

      public boolean canUse() {
         if (!this.shouldPanic()) {
            return false;
         } else {
            BlockPos blockPos = this.lookForWater(this.mob.level(), this.mob, 7);
            if (blockPos != null) {
               this.posX = (double)blockPos.getX();
               this.posY = (double)blockPos.getY();
               this.posZ = (double)blockPos.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }
   }

   private static class TurtleTravelGoal extends Goal {
      private final Turtle turtle;
      private final double speedModifier;
      private boolean stuck;

      TurtleTravelGoal(final Turtle turtle, final double speedModifier) {
         super();
         this.turtle = turtle;
         this.speedModifier = speedModifier;
      }

      public boolean canUse() {
         return !this.turtle.goingHome && !this.turtle.hasEgg() && this.turtle.isInWater();
      }

      public void start() {
         int xzDist = 512;
         int yDist = 4;
         RandomSource random = this.turtle.random;
         int xt = random.nextInt(1025) - 512;
         int yt = random.nextInt(9) - 4;
         int zt = random.nextInt(1025) - 512;
         if ((double)yt + this.turtle.getY() > (double)(this.turtle.level().getSeaLevel() - 1)) {
            yt = 0;
         }

         this.turtle.travelPos = BlockPos.containing((double)xt + this.turtle.getX(), (double)yt + this.turtle.getY(), (double)zt + this.turtle.getZ());
         this.stuck = false;
      }

      public void tick() {
         if (this.turtle.travelPos == null) {
            this.stuck = true;
         } else {
            if (this.turtle.getNavigation().isDone()) {
               Vec3 targetPos = Vec3.atBottomCenterOf(this.turtle.travelPos);
               Vec3 nextPos = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, targetPos, 0.3141592741012573);
               if (nextPos == null) {
                  nextPos = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, targetPos, 1.5707963705062866);
               }

               if (nextPos != null) {
                  int xc = Mth.floor(nextPos.x);
                  int zc = Mth.floor(nextPos.z);
                  int r = 34;
                  if (!this.turtle.level().hasChunksAt(xc - 34, zc - 34, xc + 34, zc + 34)) {
                     nextPos = null;
                  }
               }

               if (nextPos == null) {
                  this.stuck = true;
                  return;
               }

               this.turtle.getNavigation().moveTo(nextPos.x, nextPos.y, nextPos.z, this.speedModifier);
            }

         }
      }

      public boolean canContinueToUse() {
         return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.goingHome && !this.turtle.isInLove() && !this.turtle.hasEgg();
      }

      public void stop() {
         this.turtle.travelPos = null;
         super.stop();
      }
   }

   private static class TurtleGoHomeGoal extends Goal {
      private final Turtle turtle;
      private final double speedModifier;
      private boolean stuck;
      private int closeToHomeTryTicks;
      private static final int GIVE_UP_TICKS = 600;

      TurtleGoHomeGoal(final Turtle turtle, final double speedModifier) {
         super();
         this.turtle = turtle;
         this.speedModifier = speedModifier;
      }

      public boolean canUse() {
         if (this.turtle.isBaby()) {
            return false;
         } else if (this.turtle.hasEgg()) {
            return true;
         } else if (this.turtle.getRandom().nextInt(reducedTickDelay(700)) != 0) {
            return false;
         } else {
            return !this.turtle.homePos.closerToCenterThan(this.turtle.position(), 64.0);
         }
      }

      public void start() {
         this.turtle.goingHome = true;
         this.stuck = false;
         this.closeToHomeTryTicks = 0;
      }

      public void stop() {
         this.turtle.goingHome = false;
      }

      public boolean canContinueToUse() {
         return !this.turtle.homePos.closerToCenterThan(this.turtle.position(), 7.0) && !this.stuck && this.closeToHomeTryTicks <= this.adjustedTickDelay(600);
      }

      public void tick() {
         BlockPos homePos = this.turtle.homePos;
         boolean closeToHome = homePos.closerToCenterThan(this.turtle.position(), 16.0);
         if (closeToHome) {
            ++this.closeToHomeTryTicks;
         }

         if (this.turtle.getNavigation().isDone()) {
            Vec3 homePosVec = Vec3.atBottomCenterOf(homePos);
            Vec3 nextPos = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, homePosVec, 0.3141592741012573);
            if (nextPos == null) {
               nextPos = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, homePosVec, 1.5707963705062866);
            }

            if (nextPos != null && !closeToHome && !this.turtle.level().getBlockState(BlockPos.containing(nextPos)).is(Blocks.WATER)) {
               nextPos = DefaultRandomPos.getPosTowards(this.turtle, 16, 5, homePosVec, 1.5707963705062866);
            }

            if (nextPos == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(nextPos.x, nextPos.y, nextPos.z, this.speedModifier);
         }

      }
   }

   private static class TurtleBreedGoal extends BreedGoal {
      private final Turtle turtle;

      TurtleBreedGoal(final Turtle turtle, final double speedModifier) {
         super(turtle, speedModifier);
         this.turtle = turtle;
      }

      public boolean canUse() {
         return super.canUse() && !this.turtle.hasEgg();
      }

      protected void breed() {
         ServerPlayer loveCause = this.animal.getLoveCause();
         if (loveCause == null && this.partner.getLoveCause() != null) {
            loveCause = this.partner.getLoveCause();
         }

         if (loveCause != null) {
            loveCause.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(loveCause, this.animal, this.partner, (AgeableMob)null);
         }

         this.turtle.setHasEgg(true);
         this.animal.setAge(6000);
         this.partner.setAge(6000);
         this.animal.resetLove();
         this.partner.resetLove();
         RandomSource random = this.animal.getRandom();
         if ((Boolean)getServerLevel(this.level).getGameRules().get(GameRules.MOB_DROPS)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
         }

      }
   }

   private static class TurtleLayEggGoal extends MoveToBlockGoal {
      private final Turtle turtle;

      TurtleLayEggGoal(final Turtle turtle, final double speedModifier) {
         super(turtle, speedModifier, 16);
         this.turtle = turtle;
      }

      public boolean canUse() {
         return this.turtle.hasEgg() && this.turtle.homePos.closerToCenterThan(this.turtle.position(), 9.0) ? super.canUse() : false;
      }

      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.homePos.closerToCenterThan(this.turtle.position(), 9.0);
      }

      public void tick() {
         super.tick();
         BlockPos turtlePos = this.turtle.blockPosition();
         if (!this.turtle.isInWater() && this.isReachedTarget()) {
            if (this.turtle.layEggCounter < 1) {
               this.turtle.setLayingEgg(true);
            } else if (this.turtle.layEggCounter > this.adjustedTickDelay(200)) {
               Level level = this.turtle.level();
               level.playSound((Entity)null, (BlockPos)turtlePos, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + level.getRandom().nextFloat() * 0.2F);
               BlockPos eggPos = this.blockPos.above();
               BlockState eggState = (BlockState)Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, this.turtle.random.nextInt(4) + 1);
               level.setBlock(eggPos, eggState, 3);
               level.gameEvent(GameEvent.BLOCK_PLACE, eggPos, GameEvent.Context.of(this.turtle, eggState));
               this.turtle.setHasEgg(false);
               this.turtle.setLayingEgg(false);
               this.turtle.setInLoveTime(600);
            }

            if (this.turtle.isLayingEgg()) {
               ++this.turtle.layEggCounter;
            }
         }

      }

      protected boolean isValidTarget(final LevelReader level, final BlockPos pos) {
         return !level.isEmptyBlock(pos.above()) ? false : TurtleEggBlock.isSand(level, pos);
      }
   }

   private static class TurtleRandomStrollGoal extends RandomStrollGoal {
      private final Turtle turtle;

      private TurtleRandomStrollGoal(final Turtle turtle, final double speedModifier, final int interval) {
         super(turtle, speedModifier, interval);
         this.turtle = turtle;
      }

      public boolean canUse() {
         return !this.mob.isInWater() && !this.turtle.goingHome && !this.turtle.hasEgg() ? super.canUse() : false;
      }
   }

   private static class TurtleGoToWaterGoal extends MoveToBlockGoal {
      private static final int GIVE_UP_TICKS = 1200;
      private final Turtle turtle;

      private TurtleGoToWaterGoal(final Turtle turtle, final double speedModifier) {
         super(turtle, turtle.isBaby() ? 2.0 : speedModifier, 24);
         this.turtle = turtle;
         this.verticalSearchStart = -1;
      }

      public boolean canContinueToUse() {
         return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level(), this.blockPos);
      }

      public boolean canUse() {
         if (this.turtle.isBaby() && !this.turtle.isInWater()) {
            return super.canUse();
         } else {
            return !this.turtle.goingHome && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.canUse() : false;
         }
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 160 == 0;
      }

      protected boolean isValidTarget(final LevelReader level, final BlockPos pos) {
         return level.getBlockState(pos).is(Blocks.WATER);
      }
   }

   private static class TurtleMoveControl<T extends Turtle> extends MoveControl<T> {
      TurtleMoveControl(final T turtle) {
         super(turtle);
      }

      private void updateSpeed() {
         if (((Turtle)this.mob).isInWater()) {
            ((Turtle)this.mob).setDeltaMovement(((Turtle)this.mob).getDeltaMovement().add(0.0, 0.005, 0.0));
            if (!((Turtle)this.mob).getHomePos().closerToCenterThan(((Turtle)this.mob).position(), 16.0)) {
               ((Turtle)this.mob).setSpeed(Math.max(((Turtle)this.mob).getSpeed() / 2.0F, 0.08F));
            }

            if (((Turtle)this.mob).isBaby()) {
               ((Turtle)this.mob).setSpeed(Math.max(((Turtle)this.mob).getSpeed() / 3.0F, 0.06F));
            }
         } else if (((Turtle)this.mob).onGround()) {
            ((Turtle)this.mob).setSpeed(Math.max(((Turtle)this.mob).getSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.updateSpeed();
         if (this.operation == MoveControl.Operation.MOVE_TO && !((Turtle)this.mob).getNavigation().isDone()) {
            double xd = this.wantedX - ((Turtle)this.mob).getX();
            double yd = this.wantedY - ((Turtle)this.mob).getY();
            double zd = this.wantedZ - ((Turtle)this.mob).getZ();
            double dd = Math.sqrt(xd * xd + yd * yd + zd * zd);
            if (dd < 9.999999747378752E-6) {
               ((Turtle)this.mob).setSpeed(0.0F);
            } else {
               yd /= dd;
               float yRotD = (float)(Mth.atan2(zd, xd) * 57.2957763671875) - 90.0F;
               ((Turtle)this.mob).setYRot(this.rotlerp(((Turtle)this.mob).getYRot(), yRotD, 90.0F));
               (this.mob).yBodyRot = ((Turtle)this.mob).getYRot();
               float targetSpeed = (float)(this.speedModifier * ((Turtle)this.mob).getAttributeValue(Attributes.MOVEMENT_SPEED));
               ((Turtle)this.mob).setSpeed(Mth.lerp(0.125F, ((Turtle)this.mob).getSpeed(), targetSpeed));
               ((Turtle)this.mob).setDeltaMovement(((Turtle)this.mob).getDeltaMovement().add(0.0, (double)((Turtle)this.mob).getSpeed() * yd * 0.1, 0.0));
            }
         } else {
            ((Turtle)this.mob).setSpeed(0.0F);
         }
      }
   }

   private static class TurtlePathNavigation extends AmphibiousPathNavigation {
      TurtlePathNavigation(final Turtle mob, final Level level) {
         super(mob, level);
      }

      public boolean isStableDestination(final BlockPos pos) {
         Mob var3 = this.mob;
         if (var3 instanceof Turtle turtle) {
            if (turtle.travelPos != null) {
               return this.level.getBlockState(pos).is(Blocks.WATER);
            }
         }

         return !this.level.getBlockState(pos.below()).isAir();
      }
   }
}
