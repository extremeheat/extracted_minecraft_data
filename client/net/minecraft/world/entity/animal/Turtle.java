package net.minecraft.world.entity.animal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
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
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Turtle extends Animal {
   private static final EntityDataAccessor<BlockPos> HOME_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Boolean> HAS_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> LAYING_EGG = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<BlockPos> TRAVEL_POS = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Boolean> GOING_HOME = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> TRAVELLING = SynchedEntityData.defineId(Turtle.class, EntityDataSerializers.BOOLEAN);
   public static final Ingredient FOOD_ITEMS = Ingredient.of(Blocks.SEAGRASS.asItem());
   int layEggCounter;
   public static final Predicate<LivingEntity> BABY_ON_LAND_SELECTOR = var0 -> var0.isBaby() && !var0.isInWater();

   public Turtle(EntityType<? extends Turtle> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
      this.setPathfindingMalus(BlockPathTypes.DOOR_IRON_CLOSED, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DOOR_WOOD_CLOSED, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DOOR_OPEN, -1.0F);
      this.moveControl = new Turtle.TurtleMoveControl(this);
      this.setMaxUpStep(1.0F);
   }

   public void setHomePos(BlockPos var1) {
      this.entityData.set(HOME_POS, var1);
   }

   BlockPos getHomePos() {
      return this.entityData.get(HOME_POS);
   }

   void setTravelPos(BlockPos var1) {
      this.entityData.set(TRAVEL_POS, var1);
   }

   BlockPos getTravelPos() {
      return this.entityData.get(TRAVEL_POS);
   }

   public boolean hasEgg() {
      return this.entityData.get(HAS_EGG);
   }

   void setHasEgg(boolean var1) {
      this.entityData.set(HAS_EGG, var1);
   }

   public boolean isLayingEgg() {
      return this.entityData.get(LAYING_EGG);
   }

   void setLayingEgg(boolean var1) {
      this.layEggCounter = var1 ? 1 : 0;
      this.entityData.set(LAYING_EGG, var1);
   }

   boolean isGoingHome() {
      return this.entityData.get(GOING_HOME);
   }

   void setGoingHome(boolean var1) {
      this.entityData.set(GOING_HOME, var1);
   }

   boolean isTravelling() {
      return this.entityData.get(TRAVELLING);
   }

   void setTravelling(boolean var1) {
      this.entityData.set(TRAVELLING, var1);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(HOME_POS, BlockPos.ZERO);
      this.entityData.define(HAS_EGG, false);
      this.entityData.define(TRAVEL_POS, BlockPos.ZERO);
      this.entityData.define(GOING_HOME, false);
      this.entityData.define(TRAVELLING, false);
      this.entityData.define(LAYING_EGG, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("HomePosX", this.getHomePos().getX());
      var1.putInt("HomePosY", this.getHomePos().getY());
      var1.putInt("HomePosZ", this.getHomePos().getZ());
      var1.putBoolean("HasEgg", this.hasEgg());
      var1.putInt("TravelPosX", this.getTravelPos().getX());
      var1.putInt("TravelPosY", this.getTravelPos().getY());
      var1.putInt("TravelPosZ", this.getTravelPos().getZ());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      int var2 = var1.getInt("HomePosX");
      int var3 = var1.getInt("HomePosY");
      int var4 = var1.getInt("HomePosZ");
      this.setHomePos(new BlockPos(var2, var3, var4));
      super.readAdditionalSaveData(var1);
      this.setHasEgg(var1.getBoolean("HasEgg"));
      int var5 = var1.getInt("TravelPosX");
      int var6 = var1.getInt("TravelPosY");
      int var7 = var1.getInt("TravelPosZ");
      this.setTravelPos(new BlockPos(var5, var6, var7));
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      this.setHomePos(this.blockPosition());
      this.setTravelPos(BlockPos.ZERO);
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   public static boolean checkTurtleSpawnRules(EntityType<Turtle> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var3.getY() < var1.getSeaLevel() + 4 && TurtleEggBlock.onSand(var1, var3) && isBrightEnoughToSpawn(var1, var3);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Turtle.TurtlePanicGoal(this, 1.2));
      this.goalSelector.addGoal(1, new Turtle.TurtleBreedGoal(this, 1.0));
      this.goalSelector.addGoal(1, new Turtle.TurtleLayEggGoal(this, 1.0));
      this.goalSelector.addGoal(2, new TemptGoal(this, 1.1, FOOD_ITEMS, false));
      this.goalSelector.addGoal(3, new Turtle.TurtleGoToWaterGoal(this, 1.0));
      this.goalSelector.addGoal(4, new Turtle.TurtleGoHomeGoal(this, 1.0));
      this.goalSelector.addGoal(7, new Turtle.TurtleTravelGoal(this, 1.0));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(9, new Turtle.TurtleRandomStrollGoal(this, 1.0, 100));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Override
   public boolean isPushedByFluid() {
      return false;
   }

   @Override
   public boolean canBreatheUnderwater() {
      return true;
   }

   @Override
   public MobType getMobType() {
      return MobType.WATER;
   }

   @Override
   public int getAmbientSoundInterval() {
      return 200;
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround && !this.isBaby() ? SoundEvents.TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   @Override
   protected void playSwimSound(float var1) {
      super.playSwimSound(var1 * 1.5F);
   }

   @Override
   protected SoundEvent getSwimSound() {
      return SoundEvents.TURTLE_SWIM;
   }

   @Nullable
   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isBaby() ? SoundEvents.TURTLE_HURT_BABY : SoundEvents.TURTLE_HURT;
   }

   @Nullable
   @Override
   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.TURTLE_DEATH_BABY : SoundEvents.TURTLE_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      SoundEvent var3 = this.isBaby() ? SoundEvents.TURTLE_SHAMBLE_BABY : SoundEvents.TURTLE_SHAMBLE;
      this.playSound(var3, 0.15F, 1.0F);
   }

   @Override
   public boolean canFallInLove() {
      return super.canFallInLove() && !this.hasEgg();
   }

   @Override
   protected float nextStep() {
      return this.moveDist + 0.15F;
   }

   @Override
   public float getScale() {
      return this.isBaby() ? 0.3F : 1.0F;
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new Turtle.TurtlePathNavigation(this, var1);
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.TURTLE.create(var1);
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return var1.is(Blocks.SEAGRASS.asItem());
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      if (!this.isGoingHome() && var2.getFluidState(var1).is(FluidTags.WATER)) {
         return 10.0F;
      } else {
         return TurtleEggBlock.onSand(var2, var1) ? 10.0F : var2.getPathfindingCostFromLightLevels(var1);
      }
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
         BlockPos var1 = this.blockPosition();
         if (TurtleEggBlock.onSand(this.level, var1)) {
            this.level.levelEvent(2001, var1, Block.getId(this.level.getBlockState(var1.below())));
         }
      }
   }

   @Override
   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         this.spawnAtLocation(Items.SCUTE, 1);
      }
   }

   @Override
   public void travel(Vec3 var1) {
      if (this.isControlledByLocalInstance() && this.isInWater()) {
         this.moveRelative(0.1F, var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (this.getTarget() == null && (!this.isGoingHome() || !this.getHomePos().closerToCenterThan(this.position(), 20.0))) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(var1);
      }
   }

   @Override
   public boolean canBeLeashed(Player var1) {
      return false;
   }

   @Override
   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      this.hurt(this.damageSources().lightningBolt(), 3.4028235E38F);
   }

   static class TurtleBreedGoal extends BreedGoal {
      private final Turtle turtle;

      TurtleBreedGoal(Turtle var1, double var2) {
         super(var1, var2);
         this.turtle = var1;
      }

      @Override
      public boolean canUse() {
         return super.canUse() && !this.turtle.hasEgg();
      }

      @Override
      protected void breed() {
         ServerPlayer var1 = this.animal.getLoveCause();
         if (var1 == null && this.partner.getLoveCause() != null) {
            var1 = this.partner.getLoveCause();
         }

         if (var1 != null) {
            var1.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(var1, this.animal, this.partner, null);
         }

         this.turtle.setHasEgg(true);
         this.animal.setAge(6000);
         this.partner.setAge(6000);
         this.animal.resetLove();
         this.partner.resetLove();
         RandomSource var2 = this.animal.getRandom();
         if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), var2.nextInt(7) + 1));
         }
      }
   }

   static class TurtleGoHomeGoal extends Goal {
      private final Turtle turtle;
      private final double speedModifier;
      private boolean stuck;
      private int closeToHomeTryTicks;
      private static final int GIVE_UP_TICKS = 600;

      TurtleGoHomeGoal(Turtle var1, double var2) {
         super();
         this.turtle = var1;
         this.speedModifier = var2;
      }

      @Override
      public boolean canUse() {
         if (this.turtle.isBaby()) {
            return false;
         } else if (this.turtle.hasEgg()) {
            return true;
         } else if (this.turtle.getRandom().nextInt(reducedTickDelay(700)) != 0) {
            return false;
         } else {
            return !this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 64.0);
         }
      }

      @Override
      public void start() {
         this.turtle.setGoingHome(true);
         this.stuck = false;
         this.closeToHomeTryTicks = 0;
      }

      @Override
      public void stop() {
         this.turtle.setGoingHome(false);
      }

      @Override
      public boolean canContinueToUse() {
         return !this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 7.0)
            && !this.stuck
            && this.closeToHomeTryTicks <= this.adjustedTickDelay(600);
      }

      @Override
      public void tick() {
         BlockPos var1 = this.turtle.getHomePos();
         boolean var2 = var1.closerToCenterThan(this.turtle.position(), 16.0);
         if (var2) {
            ++this.closeToHomeTryTicks;
         }

         if (this.turtle.getNavigation().isDone()) {
            Vec3 var3 = Vec3.atBottomCenterOf(var1);
            Vec3 var4 = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, var3, 0.3141592741012573);
            if (var4 == null) {
               var4 = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, var3, 1.5707963705062866);
            }

            if (var4 != null && !var2 && !this.turtle.level.getBlockState(BlockPos.containing(var4)).is(Blocks.WATER)) {
               var4 = DefaultRandomPos.getPosTowards(this.turtle, 16, 5, var3, 1.5707963705062866);
            }

            if (var4 == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(var4.x, var4.y, var4.z, this.speedModifier);
         }
      }
   }

   static class TurtleGoToWaterGoal extends MoveToBlockGoal {
      private static final int GIVE_UP_TICKS = 1200;
      private final Turtle turtle;

      TurtleGoToWaterGoal(Turtle var1, double var2) {
         super(var1, var1.isBaby() ? 2.0 : var2, 24);
         this.turtle = var1;
         this.verticalSearchStart = -1;
      }

      @Override
      public boolean canContinueToUse() {
         return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level, this.blockPos);
      }

      @Override
      public boolean canUse() {
         if (this.turtle.isBaby() && !this.turtle.isInWater()) {
            return super.canUse();
         } else {
            return !this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.canUse() : false;
         }
      }

      @Override
      public boolean shouldRecalculatePath() {
         return this.tryTicks % 160 == 0;
      }

      @Override
      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         return var1.getBlockState(var2).is(Blocks.WATER);
      }
   }

   static class TurtleLayEggGoal extends MoveToBlockGoal {
      private final Turtle turtle;

      TurtleLayEggGoal(Turtle var1, double var2) {
         super(var1, var2, 16);
         this.turtle = var1;
      }

      @Override
      public boolean canUse() {
         return this.turtle.hasEgg() && this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 9.0) ? super.canUse() : false;
      }

      @Override
      public boolean canContinueToUse() {
         return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 9.0);
      }

      @Override
      public void tick() {
         super.tick();
         BlockPos var1 = this.turtle.blockPosition();
         if (!this.turtle.isInWater() && this.isReachedTarget()) {
            if (this.turtle.layEggCounter < 1) {
               this.turtle.setLayingEgg(true);
            } else if (this.turtle.layEggCounter > this.adjustedTickDelay(200)) {
               Level var2 = this.turtle.level;
               var2.playSound(null, var1, SoundEvents.TURTLE_LAY_EGG, SoundSource.BLOCKS, 0.3F, 0.9F + var2.random.nextFloat() * 0.2F);
               BlockPos var3 = this.blockPos.above();
               BlockState var4 = Blocks.TURTLE_EGG.defaultBlockState().setValue(TurtleEggBlock.EGGS, Integer.valueOf(this.turtle.random.nextInt(4) + 1));
               var2.setBlock(var3, var4, 3);
               var2.gameEvent(GameEvent.BLOCK_PLACE, var3, GameEvent.Context.of(this.turtle, var4));
               this.turtle.setHasEgg(false);
               this.turtle.setLayingEgg(false);
               this.turtle.setInLoveTime(600);
            }

            if (this.turtle.isLayingEgg()) {
               ++this.turtle.layEggCounter;
            }
         }
      }

      @Override
      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         return !var1.isEmptyBlock(var2.above()) ? false : TurtleEggBlock.isSand(var1, var2);
      }
   }

   static class TurtleMoveControl extends MoveControl {
      private final Turtle turtle;

      TurtleMoveControl(Turtle var1) {
         super(var1);
         this.turtle = var1;
      }

      private void updateSpeed() {
         if (this.turtle.isInWater()) {
            this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, 0.005, 0.0));
            if (!this.turtle.getHomePos().closerToCenterThan(this.turtle.position(), 16.0)) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.08F));
            }

            if (this.turtle.isBaby()) {
               this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 3.0F, 0.06F));
            }
         } else if (this.turtle.onGround) {
            this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.06F));
         }
      }

      @Override
      public void tick() {
         this.updateSpeed();
         if (this.operation == MoveControl.Operation.MOVE_TO && !this.turtle.getNavigation().isDone()) {
            double var1 = this.wantedX - this.turtle.getX();
            double var3 = this.wantedY - this.turtle.getY();
            double var5 = this.wantedZ - this.turtle.getZ();
            double var7 = Math.sqrt(var1 * var1 + var3 * var3 + var5 * var5);
            if (var7 < 9.999999747378752E-6) {
               this.mob.setSpeed(0.0F);
            } else {
               var3 /= var7;
               float var9 = (float)(Mth.atan2(var5, var1) * 57.2957763671875) - 90.0F;
               this.turtle.setYRot(this.rotlerp(this.turtle.getYRot(), var9, 90.0F));
               this.turtle.yBodyRot = this.turtle.getYRot();
               float var10 = (float)(this.speedModifier * this.turtle.getAttributeValue(Attributes.MOVEMENT_SPEED));
               this.turtle.setSpeed(Mth.lerp(0.125F, this.turtle.getSpeed(), var10));
               this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0, (double)this.turtle.getSpeed() * var3 * 0.1, 0.0));
            }
         } else {
            this.turtle.setSpeed(0.0F);
         }
      }
   }

   static class TurtlePanicGoal extends PanicGoal {
      TurtlePanicGoal(Turtle var1, double var2) {
         super(var1, var2);
      }

      @Override
      public boolean canUse() {
         if (!this.shouldPanic()) {
            return false;
         } else {
            BlockPos var1 = this.lookForWater(this.mob.level, this.mob, 7);
            if (var1 != null) {
               this.posX = (double)var1.getX();
               this.posY = (double)var1.getY();
               this.posZ = (double)var1.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }
   }

   static class TurtlePathNavigation extends AmphibiousPathNavigation {
      TurtlePathNavigation(Turtle var1, Level var2) {
         super(var1, var2);
      }

      @Override
      public boolean isStableDestination(BlockPos var1) {
         Mob var3 = this.mob;
         if (var3 instanceof Turtle var2 && var2.isTravelling()) {
            return this.level.getBlockState(var1).is(Blocks.WATER);
         }

         return !this.level.getBlockState(var1.below()).isAir();
      }
   }

   static class TurtleRandomStrollGoal extends RandomStrollGoal {
      private final Turtle turtle;

      TurtleRandomStrollGoal(Turtle var1, double var2, int var4) {
         super(var1, var2, var4);
         this.turtle = var1;
      }

      @Override
      public boolean canUse() {
         return !this.mob.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() ? super.canUse() : false;
      }
   }

   static class TurtleTravelGoal extends Goal {
      private final Turtle turtle;
      private final double speedModifier;
      private boolean stuck;

      TurtleTravelGoal(Turtle var1, double var2) {
         super();
         this.turtle = var1;
         this.speedModifier = var2;
      }

      @Override
      public boolean canUse() {
         return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
      }

      @Override
      public void start() {
         boolean var1 = true;
         boolean var2 = true;
         RandomSource var3 = this.turtle.random;
         int var4 = var3.nextInt(1025) - 512;
         int var5 = var3.nextInt(9) - 4;
         int var6 = var3.nextInt(1025) - 512;
         if ((double)var5 + this.turtle.getY() > (double)(this.turtle.level.getSeaLevel() - 1)) {
            var5 = 0;
         }

         BlockPos var7 = BlockPos.containing((double)var4 + this.turtle.getX(), (double)var5 + this.turtle.getY(), (double)var6 + this.turtle.getZ());
         this.turtle.setTravelPos(var7);
         this.turtle.setTravelling(true);
         this.stuck = false;
      }

      @Override
      public void tick() {
         if (this.turtle.getNavigation().isDone()) {
            Vec3 var1 = Vec3.atBottomCenterOf(this.turtle.getTravelPos());
            Vec3 var2 = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, var1, 0.3141592741012573);
            if (var2 == null) {
               var2 = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, var1, 1.5707963705062866);
            }

            if (var2 != null) {
               int var3 = Mth.floor(var2.x);
               int var4 = Mth.floor(var2.z);
               boolean var5 = true;
               if (!this.turtle.level.hasChunksAt(var3 - 34, var4 - 34, var3 + 34, var4 + 34)) {
                  var2 = null;
               }
            }

            if (var2 == null) {
               this.stuck = true;
               return;
            }

            this.turtle.getNavigation().moveTo(var2.x, var2.y, var2.z, this.speedModifier);
         }
      }

      @Override
      public boolean canContinueToUse() {
         return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
      }

      @Override
      public void stop() {
         this.turtle.setTravelling(false);
         super.stop();
      }
   }
}
