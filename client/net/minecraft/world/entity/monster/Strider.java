package net.minecraft.world.entity.monster;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.LinkedHashSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class Strider extends Animal implements ItemSteerable, Saddleable {
   private static final float SUFFOCATE_STEERING_MODIFIER = 0.23F;
   private static final float SUFFOCATE_SPEED_MODIFIER = 0.66F;
   private static final float STEERING_MODIFIER = 0.55F;
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.WARPED_FUNGUS);
   private static final Ingredient TEMPT_ITEMS = Ingredient.of(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
   private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
   private final ItemBasedSteering steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
   @Nullable
   private TemptGoal temptGoal;
   @Nullable
   private PanicGoal panicGoal;

   public Strider(EntityType<? extends Strider> var1, Level var2) {
      super(var1, var2);
      this.blocksBuilding = true;
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
   }

   public static boolean checkStriderSpawnRules(EntityType<Strider> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      BlockPos.MutableBlockPos var5 = var3.mutable();

      do {
         var5.move(Direction.UP);
      } while(var1.getFluidState(var5).is(FluidTags.LAVA));

      return var1.getBlockState(var5).isAir();
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_BOOST_TIME.equals(var1) && this.level.isClientSide) {
         this.steering.onSynced();
      }

      super.onSyncedDataUpdated(var1);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BOOST_TIME, 0);
      this.entityData.define(DATA_SUFFOCATING, false);
      this.entityData.define(DATA_SADDLE_ID, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.steering.addAdditionalSaveData(var1);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.steering.readAdditionalSaveData(var1);
   }

   @Override
   public boolean isSaddled() {
      return this.steering.hasSaddle();
   }

   @Override
   public boolean isSaddleable() {
      return this.isAlive() && !this.isBaby();
   }

   @Override
   public void equipSaddle(@Nullable SoundSource var1) {
      this.steering.setSaddle(true);
      if (var1 != null) {
         this.level.playSound(null, this, SoundEvents.STRIDER_SADDLE, var1, 0.5F, 1.0F);
      }
   }

   @Override
   protected void registerGoals() {
      this.panicGoal = new PanicGoal(this, 1.65);
      this.goalSelector.addGoal(1, this.panicGoal);
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.temptGoal = new TemptGoal(this, 1.4, TEMPT_ITEMS, false);
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(4, new Strider.StriderGoToLavaGoal(this, 1.5));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Strider.class, 8.0F));
   }

   public void setSuffocating(boolean var1) {
      this.entityData.set(DATA_SUFFOCATING, var1);
   }

   public boolean isSuffocating() {
      return this.getVehicle() instanceof Strider ? ((Strider)this.getVehicle()).isSuffocating() : this.entityData.get(DATA_SUFFOCATING);
   }

   @Override
   public boolean canStandOnFluid(FluidState var1) {
      return var1.is(FluidTags.LAVA);
   }

   @Override
   public double getPassengersRidingOffset() {
      float var1 = Math.min(0.25F, this.animationSpeed);
      float var2 = this.animationPosition;
      return (double)this.getBbHeight() - 0.19 + (double)(0.12F * Mth.cos(var2 * 1.5F) * 2.0F * var1);
   }

   @Override
   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   @Nullable
   @Override
   public Entity getControllingPassenger() {
      Entity var1 = this.getFirstPassenger();
      return var1 != null && this.canBeControlledBy(var1) ? var1 : null;
   }

   private boolean canBeControlledBy(Entity var1) {
      if (!(var1 instanceof Player)) {
         return false;
      } else {
         Player var2 = (Player)var1;
         return var2.getMainHandItem().is(Items.WARPED_FUNGUS_ON_A_STICK) || var2.getOffhandItem().is(Items.WARPED_FUNGUS_ON_A_STICK);
      }
   }

   @Override
   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      Vec3[] var2 = new Vec3[]{
         getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot()),
         getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot() - 22.5F),
         getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot() + 22.5F),
         getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot() - 45.0F),
         getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot() + 45.0F)
      };
      LinkedHashSet var3 = Sets.newLinkedHashSet();
      double var4 = this.getBoundingBox().maxY;
      double var6 = this.getBoundingBox().minY - 0.5;
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(Vec3 var12 : var2) {
         var8.set(this.getX() + var12.x, var4, this.getZ() + var12.z);

         for(double var13 = var4; var13 > var6; --var13) {
            var3.add(var8.immutable());
            var8.move(Direction.DOWN);
         }
      }

      for(BlockPos var18 : var3) {
         if (!this.level.getFluidState(var18).is(FluidTags.LAVA)) {
            double var19 = this.level.getBlockFloorHeight(var18);
            if (DismountHelper.isBlockFloorValid(var19)) {
               Vec3 var20 = Vec3.upFromBottomCenterOf(var18, var19);
               UnmodifiableIterator var14 = var1.getDismountPoses().iterator();

               while(var14.hasNext()) {
                  Pose var15 = (Pose)var14.next();
                  AABB var16 = var1.getLocalBoundsForPose(var15);
                  if (DismountHelper.canDismountTo(this.level, var1, var16.move(var20))) {
                     var1.setPose(var15);
                     return var20;
                  }
               }
            }
         }
      }

      return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
   }

   @Override
   public void travel(Vec3 var1) {
      this.setSpeed(this.getMoveSpeed());
      this.travel(this, this.steering, var1);
   }

   public float getMoveSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.66F : 1.0F);
   }

   @Override
   public float getSteeringSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.23F : 0.55F);
   }

   @Override
   public void travelWithInput(Vec3 var1) {
      super.travel(var1);
   }

   @Override
   protected float nextStep() {
      return this.moveDist + 0.6F;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(this.isInLava() ? SoundEvents.STRIDER_STEP_LAVA : SoundEvents.STRIDER_STEP, 1.0F, 1.0F);
   }

   @Override
   public boolean boost() {
      return this.steering.boost(this.getRandom());
   }

   @Override
   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
      this.checkInsideBlocks();
      if (this.isInLava()) {
         this.resetFallDistance();
      } else {
         super.checkFallDamage(var1, var3, var4, var5);
      }
   }

   @Override
   public void tick() {
      if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
         this.playSound(SoundEvents.STRIDER_HAPPY, 1.0F, this.getVoicePitch());
      } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
         this.playSound(SoundEvents.STRIDER_RETREAT, 1.0F, this.getVoicePitch());
      }

      if (!this.isNoAi()) {
         BlockState var1 = this.level.getBlockState(this.blockPosition());
         BlockState var2 = this.getBlockStateOnLegacy();
         boolean var3 = var1.is(BlockTags.STRIDER_WARM_BLOCKS) || var2.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
         this.setSuffocating(!var3);
      }

      super.tick();
      this.floatStrider();
      this.checkInsideBlocks();
   }

   private boolean isPanicking() {
      return this.panicGoal != null && this.panicGoal.isRunning();
   }

   private boolean isBeingTempted() {
      return this.temptGoal != null && this.temptGoal.isRunning();
   }

   @Override
   protected boolean shouldPassengersInheritMalus() {
      return true;
   }

   private void floatStrider() {
      if (this.isInLava()) {
         CollisionContext var1 = CollisionContext.of(this);
         if (var1.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level.getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
            this.onGround = true;
         } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5).add(0.0, 0.05, 0.0));
         }
      }
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.17499999701976776).add(Attributes.FOLLOW_RANGE, 16.0);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return !this.isPanicking() && !this.isBeingTempted() ? SoundEvents.STRIDER_AMBIENT : null;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.STRIDER_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.STRIDER_DEATH;
   }

   @Override
   protected boolean canAddPassenger(Entity var1) {
      return !this.isVehicle() && !this.isEyeInFluid(FluidTags.LAVA);
   }

   @Override
   public boolean isSensitiveToWater() {
      return true;
   }

   @Override
   public boolean isOnFire() {
      return false;
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new Strider.StriderPathNavigation(this, var1);
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      if (var2.getBlockState(var1).getFluidState().is(FluidTags.LAVA)) {
         return 10.0F;
      } else {
         return this.isInLava() ? -1.0F / 0.0F : 0.0F;
      }
   }

   @Nullable
   public Strider getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.STRIDER.create(var1);
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return FOOD_ITEMS.test(var1);
   }

   @Override
   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isSaddled()) {
         this.spawnAtLocation(Items.SADDLE);
      }
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      boolean var3 = this.isFood(var1.getItemInHand(var2));
      if (!var3 && this.isSaddled() && !this.isVehicle() && !var1.isSecondaryUseActive()) {
         if (!this.level.isClientSide) {
            var1.startRiding(this);
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         InteractionResult var4 = super.mobInteract(var1, var2);
         if (!var4.consumesAction()) {
            ItemStack var5 = var1.getItemInHand(var2);
            return var5.is(Items.SADDLE) ? var5.interactLivingEntity(var1, this, var2) : InteractionResult.PASS;
         } else {
            if (var3 && !this.isSilent()) {
               this.level
                  .playSound(
                     null,
                     this.getX(),
                     this.getY(),
                     this.getZ(),
                     SoundEvents.STRIDER_EAT,
                     this.getSoundSource(),
                     1.0F,
                     1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
                  );
            }

            return var4;
         }
      }
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      if (this.isBaby()) {
         return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
      } else {
         RandomSource var6 = var1.getRandom();
         if (var6.nextInt(30) == 0) {
            Mob var7 = EntityType.ZOMBIFIED_PIGLIN.create(var1.getLevel());
            if (var7 != null) {
               var4 = this.spawnJockey(var1, var2, var7, new Zombie.ZombieGroupData(Zombie.getSpawnAsBabyOdds(var6), false));
               var7.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
               this.equipSaddle(null);
            }
         } else if (var6.nextInt(10) == 0) {
            AgeableMob var8 = EntityType.STRIDER.create(var1.getLevel());
            if (var8 != null) {
               var8.setAge(-24000);
               var4 = this.spawnJockey(var1, var2, var8, null);
            }
         } else {
            var4 = new AgeableMob.AgeableMobGroupData(0.5F);
         }

         return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
      }
   }

   private SpawnGroupData spawnJockey(ServerLevelAccessor var1, DifficultyInstance var2, Mob var3, @Nullable SpawnGroupData var4) {
      var3.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
      var3.finalizeSpawn(var1, var2, MobSpawnType.JOCKEY, var4, null);
      var3.startRiding(this, true);
      return new AgeableMob.AgeableMobGroupData(0.0F);
   }

   static class StriderGoToLavaGoal extends MoveToBlockGoal {
      private final Strider strider;

      StriderGoToLavaGoal(Strider var1, double var2) {
         super(var1, var2, 8, 2);
         this.strider = var1;
      }

      @Override
      public BlockPos getMoveToTarget() {
         return this.blockPos;
      }

      @Override
      public boolean canContinueToUse() {
         return !this.strider.isInLava() && this.isValidTarget(this.strider.level, this.blockPos);
      }

      @Override
      public boolean canUse() {
         return !this.strider.isInLava() && super.canUse();
      }

      @Override
      public boolean shouldRecalculatePath() {
         return this.tryTicks % 20 == 0;
      }

      @Override
      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         return var1.getBlockState(var2).is(Blocks.LAVA) && var1.getBlockState(var2.above()).isPathfindable(var1, var2, PathComputationType.LAND);
      }
   }

   static class StriderPathNavigation extends GroundPathNavigation {
      StriderPathNavigation(Strider var1, Level var2) {
         super(var1, var2);
      }

      @Override
      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = new WalkNodeEvaluator();
         this.nodeEvaluator.setCanPassDoors(true);
         return new PathFinder(this.nodeEvaluator, var1);
      }

      @Override
      protected boolean hasValidPathType(BlockPathTypes var1) {
         return var1 != BlockPathTypes.LAVA && var1 != BlockPathTypes.DAMAGE_FIRE && var1 != BlockPathTypes.DANGER_FIRE ? super.hasValidPathType(var1) : true;
      }

      @Override
      public boolean isStableDestination(BlockPos var1) {
         return this.level.getBlockState(var1).is(Blocks.LAVA) || super.isStableDestination(var1);
      }
   }
}
