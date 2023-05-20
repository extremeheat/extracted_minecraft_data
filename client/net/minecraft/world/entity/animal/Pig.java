package net.minecraft.world.entity.animal;

import com.google.common.collect.UnmodifiableIterator;
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
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ItemBasedSteering;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Pig extends Animal implements ItemSteerable, Saddleable {
   private static final EntityDataAccessor<Boolean> DATA_SADDLE_ID = SynchedEntityData.defineId(Pig.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_BOOST_TIME = SynchedEntityData.defineId(Pig.class, EntityDataSerializers.INT);
   private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
   private final ItemBasedSteering steering = new ItemBasedSteering(this.entityData, DATA_BOOST_TIME, DATA_SADDLE_ID);

   public Pig(EntityType<? extends Pig> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
      this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, Ingredient.of(Items.CARROT_ON_A_STICK), false));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, FOOD_ITEMS, false));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.25);
   }

   @Nullable
   @Override
   public LivingEntity getControllingPassenger() {
      if (this.isSaddled()) {
         Entity var2 = this.getFirstPassenger();
         if (var2 instanceof Player var1
            && (((Player)var1).getMainHandItem().is(Items.CARROT_ON_A_STICK) || ((Player)var1).getOffhandItem().is(Items.CARROT_ON_A_STICK))) {
            return (LivingEntity)var1;
         }
      }

      return null;
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
      this.entityData.define(DATA_SADDLE_ID, false);
      this.entityData.define(DATA_BOOST_TIME, 0);
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
   protected SoundEvent getAmbientSound() {
      return SoundEvents.PIG_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PIG_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.PIG_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.PIG_STEP, 0.15F, 1.0F);
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
            return var4;
         }
      }
   }

   @Override
   public boolean isSaddleable() {
      return this.isAlive() && !this.isBaby();
   }

   @Override
   protected void dropEquipment() {
      super.dropEquipment();
      if (this.isSaddled()) {
         this.spawnAtLocation(Items.SADDLE);
      }
   }

   @Override
   public boolean isSaddled() {
      return this.steering.hasSaddle();
   }

   @Override
   public void equipSaddle(@Nullable SoundSource var1) {
      this.steering.setSaddle(true);
      if (var1 != null) {
         this.level.playSound(null, this, SoundEvents.PIG_SADDLE, var1, 0.5F, 1.0F);
      }
   }

   @Override
   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      Direction var2 = this.getMotionDirection();
      if (var2.getAxis() == Direction.Axis.Y) {
         return super.getDismountLocationForPassenger(var1);
      } else {
         int[][] var3 = DismountHelper.offsetsForDirection(var2);
         BlockPos var4 = this.blockPosition();
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
         UnmodifiableIterator var6 = var1.getDismountPoses().iterator();

         while(var6.hasNext()) {
            Pose var7 = (Pose)var6.next();
            AABB var8 = var1.getLocalBoundsForPose(var7);

            for(int[] var12 : var3) {
               var5.set(var4.getX() + var12[0], var4.getY(), var4.getZ() + var12[1]);
               double var13 = this.level.getBlockFloorHeight(var5);
               if (DismountHelper.isBlockFloorValid(var13)) {
                  Vec3 var15 = Vec3.upFromBottomCenterOf(var5, var13);
                  if (DismountHelper.canDismountTo(this.level, var1, var8.move(var15))) {
                     var1.setPose(var7);
                     return var15;
                  }
               }
            }
         }

         return super.getDismountLocationForPassenger(var1);
      }
   }

   @Override
   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      if (var1.getDifficulty() != Difficulty.PEACEFUL) {
         ZombifiedPiglin var3 = EntityType.ZOMBIFIED_PIGLIN.create(var1);
         if (var3 != null) {
            var3.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
            var3.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            var3.setNoAi(this.isNoAi());
            var3.setBaby(this.isBaby());
            if (this.hasCustomName()) {
               var3.setCustomName(this.getCustomName());
               var3.setCustomNameVisible(this.isCustomNameVisible());
            }

            var3.setPersistenceRequired();
            var1.addFreshEntity(var3);
            this.discard();
         } else {
            super.thunderHit(var1, var2);
         }
      } else {
         super.thunderHit(var1, var2);
      }
   }

   @Override
   protected void tickRidden(LivingEntity var1, Vec3 var2) {
      super.tickRidden(var1, var2);
      this.setRot(var1.getYRot(), var1.getXRot() * 0.5F);
      this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
      this.steering.tickBoost();
   }

   @Override
   protected Vec3 getRiddenInput(LivingEntity var1, Vec3 var2) {
      return new Vec3(0.0, 0.0, 1.0);
   }

   @Override
   protected float getRiddenSpeed(LivingEntity var1) {
      return (float)(this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225 * (double)this.steering.boostFactor());
   }

   @Override
   public boolean boost() {
      return this.steering.boost(this.getRandom());
   }

   @Nullable
   public Pig getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.PIG.create(var1);
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return FOOD_ITEMS.test(var1);
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }
}
