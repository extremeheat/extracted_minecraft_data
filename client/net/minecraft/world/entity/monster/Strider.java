package net.minecraft.world.entity.monster;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

public class Strider extends Animal implements ItemSteerable, Saddleable {
   private static final Ingredient FOOD_ITEMS;
   private static final Ingredient TEMPT_ITEMS;
   private static final EntityDataAccessor<Integer> DATA_BOOST_TIME;
   private static final EntityDataAccessor<Boolean> DATA_SUFFOCATING;
   private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID;
   private final ItemBasedSteering steering;
   private TemptGoal temptGoal;
   private PanicGoal panicGoal;

   public Strider(EntityType<? extends Strider> var1, Level var2) {
      super(var1, var2);
      this.steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);
      this.blocksBuilding = true;
      this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
   }

   public static boolean checkStriderSpawnRules(EntityType<Strider> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      BlockPos.MutableBlockPos var5 = var3.mutable();

      do {
         var5.move(Direction.UP);
      } while(var1.getFluidState(var5).is(FluidTags.LAVA));

      return var1.getBlockState(var5).isAir();
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_BOOST_TIME.equals(var1) && this.level.isClientSide) {
         this.steering.onSynced();
      }

      super.onSyncedDataUpdated(var1);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BOOST_TIME, 0);
      this.entityData.define(DATA_SUFFOCATING, false);
      this.entityData.define(DATA_SADDLE_ID, false);
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
         this.level.playSound((Player)null, (Entity)this, SoundEvents.STRIDER_SADDLE, var1, 0.5F, 1.0F);
      }

   }

   protected void registerGoals() {
      this.panicGoal = new PanicGoal(this, 1.65D);
      this.goalSelector.addGoal(1, this.panicGoal);
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.temptGoal = new TemptGoal(this, 1.4D, false, TEMPT_ITEMS);
      this.goalSelector.addGoal(3, this.temptGoal);
      this.goalSelector.addGoal(4, new Strider.StriderGoToLavaGoal(this, 1.5D));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D, 60));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Strider.class, 8.0F));
   }

   public void setSuffocating(boolean var1) {
      this.entityData.set(DATA_SUFFOCATING, var1);
   }

   public boolean isSuffocating() {
      return this.getVehicle() instanceof Strider ? ((Strider)this.getVehicle()).isSuffocating() : (Boolean)this.entityData.get(DATA_SUFFOCATING);
   }

   public boolean canStandOnFluid(Fluid var1) {
      return var1.is(FluidTags.LAVA);
   }

   public double getPassengersRidingOffset() {
      float var1 = Math.min(0.25F, this.animationSpeed);
      float var2 = this.animationPosition;
      return (double)this.getBbHeight() - 0.19D + (double)(0.12F * Mth.cos(var2 * 1.5F) * 2.0F * var1);
   }

   public boolean canBeControlledByRider() {
      Entity var1 = this.getControllingPassenger();
      if (!(var1 instanceof Player)) {
         return false;
      } else {
         Player var2 = (Player)var1;
         return var2.getMainHandItem().is(Items.WARPED_FUNGUS_ON_A_STICK) || var2.getOffhandItem().is(Items.WARPED_FUNGUS_ON_A_STICK);
      }
   }

   public boolean checkSpawnObstruction(LevelReader var1) {
      return var1.isUnobstructed(this);
   }

   @Nullable
   public Entity getControllingPassenger() {
      return this.getFirstPassenger();
   }

   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      Vec3[] var2 = new Vec3[]{getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.yRot), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.yRot - 22.5F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.yRot + 22.5F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.yRot - 45.0F), getCollisionHorizontalEscapeVector((double)this.getBbWidth(), (double)var1.getBbWidth(), var1.yRot + 45.0F)};
      LinkedHashSet var3 = Sets.newLinkedHashSet();
      double var4 = this.getBoundingBox().maxY;
      double var6 = this.getBoundingBox().minY - 0.5D;
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
            } while(this.level.getFluidState(var18).is(FluidTags.LAVA));

            var19 = this.level.getBlockFloorHeight(var18);
         } while(!DismountHelper.isBlockFloorValid(var19));

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

   public void travel(Vec3 var1) {
      this.setSpeed(this.getMoveSpeed());
      this.travel(this, this.steering, var1);
   }

   public float getMoveSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.66F : 1.0F);
   }

   public float getSteeringSpeed() {
      return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * (this.isSuffocating() ? 0.23F : 0.55F);
   }

   public void travelWithInput(Vec3 var1) {
      super.travel(var1);
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
         this.fallDistance = 0.0F;
      } else {
         super.checkFallDamage(var1, var3, var4, var5);
      }
   }

   public void tick() {
      if (this.isBeingTempted() && this.random.nextInt(140) == 0) {
         this.playSound(SoundEvents.STRIDER_HAPPY, 1.0F, this.getVoicePitch());
      } else if (this.isPanicking() && this.random.nextInt(60) == 0) {
         this.playSound(SoundEvents.STRIDER_RETREAT, 1.0F, this.getVoicePitch());
      }

      BlockState var1 = this.level.getBlockState(this.blockPosition());
      BlockState var2 = this.getBlockStateOn();
      boolean var3 = var1.is(BlockTags.STRIDER_WARM_BLOCKS) || var2.is(BlockTags.STRIDER_WARM_BLOCKS) || this.getFluidHeight(FluidTags.LAVA) > 0.0D;
      this.setSuffocating(!var3);
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

   protected boolean shouldPassengersInheritMalus() {
      return true;
   }

   private void floatStrider() {
      if (this.isInLava()) {
         CollisionContext var1 = CollisionContext.of(this);
         if (var1.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true) && !this.level.getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
            this.onGround = true;
         } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5D).add(0.0D, 0.05D, 0.0D));
         }
      }

   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.17499999701976776D).add(Attributes.FOLLOW_RANGE, 16.0D);
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
      return new Strider.StriderPathNavigation(this, var1);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      if (var2.getBlockState(var1).getFluidState().is(FluidTags.LAVA)) {
         return 10.0F;
      } else {
         return this.isInLava() ? -1.0F / 0.0 : 0.0F;
      }
   }

   public Strider getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (Strider)EntityType.STRIDER.create(var1);
   }

   public boolean isFood(ItemStack var1) {
      return FOOD_ITEMS.test(var1);
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
               this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.STRIDER_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            return var4;
         }
      }
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      if (this.isBaby()) {
         return super.finalizeSpawn(var1, var2, var3, var4, var5);
      } else {
         Object var7;
         if (this.random.nextInt(30) == 0) {
            Mob var6 = (Mob)EntityType.ZOMBIFIED_PIGLIN.create(var1.getLevel());
            var7 = this.spawnJockey(var1, var2, var6, new Zombie.ZombieGroupData(Zombie.getSpawnAsBabyOdds(this.random), false));
            var6.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK));
            this.equipSaddle((SoundSource)null);
         } else if (this.random.nextInt(10) == 0) {
            AgeableMob var8 = (AgeableMob)EntityType.STRIDER.create(var1.getLevel());
            var8.setAge(-24000);
            var7 = this.spawnJockey(var1, var2, var8, (SpawnGroupData)null);
         } else {
            var7 = new AgeableMob.AgeableMobGroupData(0.5F);
         }

         return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var7, var5);
      }
   }

   private SpawnGroupData spawnJockey(ServerLevelAccessor var1, DifficultyInstance var2, Mob var3, @Nullable SpawnGroupData var4) {
      var3.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
      var3.finalizeSpawn(var1, var2, MobSpawnType.JOCKEY, var4, (CompoundTag)null);
      var3.startRiding(this, true);
      return new AgeableMob.AgeableMobGroupData(0.0F);
   }

   // $FF: synthetic method
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      FOOD_ITEMS = Ingredient.of(Items.WARPED_FUNGUS);
      TEMPT_ITEMS = Ingredient.of(Items.WARPED_FUNGUS, Items.WARPED_FUNGUS_ON_A_STICK);
      DATA_BOOST_TIME = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.INT);
      DATA_SUFFOCATING = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
      DATA_SADDLE_ID = SynchedEntityData.defineId(Strider.class, EntityDataSerializers.BOOLEAN);
   }

   static class StriderGoToLavaGoal extends MoveToBlockGoal {
      private final Strider strider;

      private StriderGoToLavaGoal(Strider var1, double var2) {
         super(var1, var2, 8, 2);
         this.strider = var1;
      }

      public BlockPos getMoveToTarget() {
         return this.blockPos;
      }

      public boolean canContinueToUse() {
         return !this.strider.isInLava() && this.isValidTarget(this.strider.level, this.blockPos);
      }

      public boolean canUse() {
         return !this.strider.isInLava() && super.canUse();
      }

      public boolean shouldRecalculatePath() {
         return this.tryTicks % 20 == 0;
      }

      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         return var1.getBlockState(var2).is(Blocks.LAVA) && var1.getBlockState(var2.above()).isPathfindable(var1, var2, PathComputationType.LAND);
      }

      // $FF: synthetic method
      StriderGoToLavaGoal(Strider var1, double var2, Object var4) {
         this(var1, var2);
      }
   }

   static class StriderPathNavigation extends GroundPathNavigation {
      StriderPathNavigation(Strider var1, Level var2) {
         super(var1, var2);
      }

      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = new WalkNodeEvaluator();
         return new PathFinder(this.nodeEvaluator, var1);
      }

      protected boolean hasValidPathType(BlockPathTypes var1) {
         return var1 != BlockPathTypes.LAVA && var1 != BlockPathTypes.DAMAGE_FIRE && var1 != BlockPathTypes.DANGER_FIRE ? super.hasValidPathType(var1) : true;
      }

      public boolean isStableDestination(BlockPos var1) {
         return this.level.getBlockState(var1).is(Blocks.LAVA) || super.isStableDestination(var1);
      }
   }
}
