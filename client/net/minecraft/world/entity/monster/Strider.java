package net.minecraft.world.entity.monster;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.UUID;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class Strider extends Animal implements ItemSteerable, Saddleable {
   private static final UUID SUFFOCATING_MODIFIER_UUID = UUID.fromString("9e362924-01de-4ddd-a2b2-d0f7a405a174");
   private static final AttributeModifier SUFFOCATING_MODIFIER;
   private static final float SUFFOCATE_STEERING_MODIFIER = 0.35F;
   private static final float STEERING_MODIFIER = 0.55F;
   private static final EntityDataAccessor<Integer> DATA_BOOST_TIME;
   private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING;
   private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID;
   private final ItemBasedSteering steering;
   @Nullable
   private TemptGoal temptGoal;

   public Strider(EntityType<? extends Strider> var1, Level var2) {
      super(var1, var2);
      this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
      this.blocksBuilding = true;
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.LAVA, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
   }

   public static boolean checkStriderSpawnRules(EntityType<Strider> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      BlockPos.MutableBlockPos var5 = var3.mutable();

      do {
         var5.move(Direction.UP);
      } while(var1.getFluidState(var5).is(FluidTags.LAVA));

      return var1.getBlockState(var5).isAir();
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_BOOST_TIME.equals(var1) && this.level().isClientSide) {
         this.steering.onSynced();
      }

      super.onSyncedDataUpdated(var1);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_BOOST_TIME, 0);
      var1.define(DATA_SUFFOCATING, false);
      var1.define(DATA_SADDLE_ID, false);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.steering.addAdditionalSaveData(var1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.steering.readAdditionalSaveData(var1);
   }

   public boolean isSaddled() {
      return this.steering.hasSaddle();
   }

   public boolean isSaddleable() {
      return this.isAlive() && !this.isBaby();
   }

   public void equipSaddle(@Nullable SoundSource var1) {
      this.steering.setSaddle(true);
      if (var1 != null) {
         this.level().playSound((Player)null, (Entity)this, SoundEvents.STRIDER_SADDLE, var1, 0.5F, 1.0F);
      }

   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.65));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.temptGoal = new TemptGoal(this, 1.4, (var0) -> {
         return var0.is(ItemTags.STRIDER_TEMPT_ITEMS);
      }, false);
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(4, new StriderGoToLavaGoal(this, 1.0));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0, 60));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Strider.class, 8.0F));
   }

   public void setSuffocating(boolean var1) {
      this.entityData.set(DATA_SUFFOCATING, var1);
      AttributeInstance var2 = this.getAttribute(Attributes.MOVEMENT_SPEED);
      if (var2 != null) {
         if (var1) {
            var2.addOrUpdateTransientModifier(SUFFOCATING_MODIFIER);
         } else {
            var2.removeModifier(SUFFOCATING_MODIFIER);
         }
      }

   }

   public boolean isSuffocating() {
      return (Boolean)this.entityData.get(DATA_SUFFOCATING);
   }

   public boolean canStandOnFluid(FluidState var1) {
      return var1.is(FluidTags.LAVA);
   }

   protected Vec3 getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      float var4 = Math.min(0.25F, this.walkAnimation.speed());
      float var5 = this.walkAnimation.position();
      float var6 = 0.12F * Mth.cos(var5 * 1.5F) * 2.0F * var4;
      return super.getPassengerAttachmentPoint(var1, var2, var3).add(0.0, (double)(var6 * var3), 0.0);
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   @Nullable
   public LivingEntity getControllingPassenger() {
      if (this.isSaddled()) {
         Entity var2 = this.getFirstPassenger();
         if (var2 instanceof Player) {
            Player var1 = (Player)var2;
            if (var1.isHolding(Items.WARPED_FUNGUS_ON_A_STICK)) {
               return var1;
            }
         }
      }

      return super.getControllingPassenger();
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      Vec3[] var2 = new Vec3[]{getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot()), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot() - 22.5F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot() + 22.5F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot() - 45.0F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.getYRot() + 45.0F)};
      LinkedHashSet var3 = Sets.newLinkedHashSet();
      double var4 = this.getBoundingBox().maxY;
      double var6 = this.getBoundingBox().minY - 0.5;
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();
      Vec3[] var9 = var2;
      int var10 = var2.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         Vec3 var12 = var9[var11];
         var8.set(this.getX() + var12.x, var4, this.getZ() + var12.z);

         for(double var13 = var4; var13 > var6; --var13) {
            var3.add(var8.immutable());
            var8.move(Direction.DOWN);
         }
      }

      Iterator var17 = var3.iterator();

      while(true) {
         BlockPos var18;
         double var19;
         do {
            do {
               if (!var17.hasNext()) {
                  return new Vec3(this.getX(), this.getBoundingBox().maxY, this.getZ());
               }

               var18 = (BlockPos)var17.next();
            } while(this.level().getFluidState(var18).is(FluidTags.LAVA));

            var19 = this.level().getBlockFloorHeight(var18);
         } while(!DismountHelper.isBlockFloorValid(var19));

         Vec3 var20 = Vec3.upFromBottomCenterOf(var18, var19);
         UnmodifiableIterator var14 = var1.getDismountPoses().iterator();

         while(var14.hasNext()) {
            Pose var15 = (Pose)var14.next();
            AABB var16 = var1.getLocalBoundsForPose(var15);
            if (DismountHelper.canDismountTo(this.level(), var1, var16.move(var20))) {
               var1.setPose(var15);
               return var20;
            }
         }
      }
   }

   protected void tickRidden(Player var1, Vec3 var2) {
      this.setRot(var1.getYRot(), var1.getXRot() * 0.5F);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
      this.steering.tickBoost();
      super.tickRidden(var1, var2);
   }

   protected Vec3 getRiddenInput(Player var1, Vec3 var2) {
      return new Vec3(0.0, 0.0, 1.0);
   }

   protected float getRiddenSpeed(Player var1) {
      return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (double)(this.isSuffocating() ? 0.35F : 0.55F) * (double)this.steering.boostFactor());
   }

   protected float nextStep() {
      return this.moveDist + 0.6F;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(this.isInLava() ? SoundEvents.STRIDER_STEP_LAVA : SoundEvents.STRIDER_STEP, 1.0F, 1.0F);
   }

   public boolean boost() {
      return this.steering.boost(this.getRandom());
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
      this.checkInsideBlocks();
      if (this.isInLava()) {
         this.resetFallDistance();
      } else {
         super.checkFallDamage(var1, var3, var4, var5);
      }
   }

   public void tick() {
      if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
         this.makeSound(SoundEvents.STRIDER_HAPPY);
      } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
         this.makeSound(SoundEvents.STRIDER_RETREAT);
      }

      if (!this.isNoAi()) {
         boolean var10000;
         boolean var3;
         label36: {
            BlockState var1 = this.level().getBlockState(this.blockPosition());
            BlockState var2 = this.getBlockStateOnLegacy();
            var3 = var1.is(BlockTags.STRIDER_WARM_BLOCKS) || var2.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0;
            Entity var6 = this.getVehicle();
            if (var6 instanceof Strider) {
               Strider var5 = (Strider)var6;
               if (var5.isSuffocating()) {
                  var10000 = true;
                  break label36;
               }
            }

            var10000 = false;
         }

         boolean var4 = var10000;
         this.setSuffocating(!var3 || var4);
      }

      super.tick();
      this.floatStrider();
      this.checkInsideBlocks();
   }

   private boolean isBeingTempted() {
      return this.temptGoal != null && this.temptGoal.isRunning();
   }

   protected boolean shouldPassengersInheritMalus() {
      return true;
   }

   private void floatStrider() {
      if (this.isInLava()) {
         CollisionContext var1 = CollisionContext.of(this);
         if (var1.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
            this.setOnGround(true);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5).add(0.0, 0.05, 0.0));
         }
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.17499999701976776).add(Attributes.FOLLOW_RANGE, 16.0);
   }

   protected SoundEvent getAmbientSound() {
      return !this.isPanicking() && !this.isBeingTempted() ? SoundEvents.STRIDER_AMBIENT : null;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.STRIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.STRIDER_DEATH;
   }

   protected boolean canAddPassenger(Entity var1) {
      return !this.isVehicle() && !this.isEyeInFluid(FluidTags.LAVA);
   }

   public boolean isSensitiveToWater() {
      return true;
   }

   public boolean isOnFire() {
      return false;
   }

   protected PathNavigation createNavigation(Level var1) {
      return new StriderPathNavigation(this, var1);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      if (var2.getBlockState(var1).getFluidState().is(FluidTags.LAVA)) {
         return 10.0F;
      } else {
         return this.isInLava() ? -1.0F / 0.0F : 0.0F;
      }
   }

   @Nullable
   public Strider getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (Strider)EntityType.STRIDER.create(var1);
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.STRIDER_FOOD);
   }

   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isSaddled()) {
         this.spawnAtLocation(Items.SADDLE);
      }

   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      boolean var3 = this.isFood(var1.getItemInHand(var2));
      if (!var3 && this.isSaddled() && !this.isVehicle() && !var1.isSecondaryUseActive()) {
         if (!this.level().isClientSide) {
            var1.startRiding(this);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         InteractionResult var4 = super.mobInteract(var1, var2);
         if (!var4.consumesAction()) {
            ItemStack var5 = var1.getItemInHand(var2);
            return var5.is(Items.SADDLE) ? var5.interactLivingEntity(var1, this, var2) : InteractionResult.PASS;
         } else {
            if (var3 && !this.isSilent()) {
               this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.STRIDER_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            return var4;
         }
      }
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      if (this.isBaby()) {
         return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
      } else {
         RandomSource var5 = var1.getRandom();
         if (var5.nextInt(30) == 0) {
            Mob var6 = (Mob)EntityType.ZOMBIFIED_PIGLIN.create(var1.getLevel());
            if (var6 != null) {
               var4 = this.spawnJockey(var1, var2, var6, new Zombie.ZombieGroupData(Zombie.getSpawnAsBabyOdds(var5), false));
               var6.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
               this.equipSaddle((SoundSource)null);
            }
         } else if (var5.nextInt(10) == 0) {
            AgeableMob var7 = (AgeableMob)EntityType.STRIDER.create(var1.getLevel());
            if (var7 != null) {
               var7.setAge(-24000);
               var4 = this.spawnJockey(var1, var2, var7, (SpawnGroupData)null);
            }
         } else {
            var4 = new AgeableMob.AgeableMobGroupData(0.5F);
         }

         return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
      }
   }

   private SpawnGroupData spawnJockey(ServerLevelAccessor var1, DifficultyInstance var2, Mob var3, @Nullable SpawnGroupData var4) {
      var3.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
      var3.finalizeSpawn(var1, var2, MobSpawnType.JOCKEY, var4);
      var3.startRiding(this, true);
      return new AgeableMob.AgeableMobGroupData(0.0F);
   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      SUFFOCATING_MODIFIER = new AttributeModifier(SUFFOCATING_MODIFIER_UUID, "Strider suffocating modifier", -0.3400000035762787, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
      DATA_BOOST_TIME = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.INT);
      DATA_SUFFOCATING = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
      DATA_SADDLE_ID = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
   }

   private static class StriderGoToLavaGoal extends MoveToBlockGoal {
      private final Strider strider;

      StriderGoToLavaGoal(Strider var1, double var2) {
         super(var1, var2, 8, 2);
         this.strider = var1;
      }

      public BlockPos getMoveToTarget() {
         return this.blockPos;
      }

      public boolean canContinueToUse() {
         return !this.strider.isInLava() && this.isValidTarget(this.strider.level(), this.blockPos);
      }

      public boolean canUse() {
         return !this.strider.isInLava() && super.canUse();
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 20 == 0;
      }

      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         return var1.getBlockState(var2).is(Blocks.LAVA) && var1.getBlockState(var2.above()).isPathfindable(PathComputationType.LAND);
      }
   }

   static class StriderPathNavigation extends GroundPathNavigation {
      StriderPathNavigation(Strider var1, Level var2) {
         super(var1, var2);
      }

      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = new WalkNodeEvaluator();
         this.nodeEvaluator.setCanPassDoors(true);
         return new PathFinder(this.nodeEvaluator, var1);
      }

      protected boolean hasValidPathType(PathType var1) {
         return var1 != PathType.LAVA && var1 != PathType.DAMAGE_FIRE && var1 != PathType.DANGER_FIRE ? super.hasValidPathType(var1) : true;
      }

      public boolean isStableDestination(BlockPos var1) {
         return this.level.getBlockState(var1).is(Blocks.LAVA) || super.isStableDestination(var1);
      }
   }
}
