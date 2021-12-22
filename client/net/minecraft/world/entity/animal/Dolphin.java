package net.minecraft.world.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.DolphinJumpGoal;
import net.minecraft.world.entity.ai.goal.FollowBoatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class Dolphin extends WaterAnimal {
   private static final EntityDataAccessor<BlockPos> TREASURE_POS;
   private static final EntityDataAccessor<Boolean> GOT_FISH;
   private static final EntityDataAccessor<Integer> MOISTNESS_LEVEL;
   static final TargetingConditions SWIM_WITH_PLAYER_TARGETING;
   public static final int TOTAL_AIR_SUPPLY = 4800;
   private static final int TOTAL_MOISTNESS_LEVEL = 2400;
   public static final Predicate<ItemEntity> ALLOWED_ITEMS;

   public Dolphin(EntityType<? extends Dolphin> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
      this.lookControl = new SmoothSwimmingLookControl(this, 10);
      this.setCanPickUpLoot(true);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.setAirSupply(this.getMaxAirSupply());
      this.setXRot(0.0F);
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   protected void handleAirSupply(int var1) {
   }

   public void setTreasurePos(BlockPos var1) {
      this.entityData.set(TREASURE_POS, var1);
   }

   public BlockPos getTreasurePos() {
      return (BlockPos)this.entityData.get(TREASURE_POS);
   }

   public boolean gotFish() {
      return (Boolean)this.entityData.get(GOT_FISH);
   }

   public void setGotFish(boolean var1) {
      this.entityData.set(GOT_FISH, var1);
   }

   public int getMoistnessLevel() {
      return (Integer)this.entityData.get(MOISTNESS_LEVEL);
   }

   public void setMoisntessLevel(int var1) {
      this.entityData.set(MOISTNESS_LEVEL, var1);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(TREASURE_POS, BlockPos.ZERO);
      this.entityData.define(GOT_FISH, false);
      this.entityData.define(MOISTNESS_LEVEL, 2400);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("TreasurePosX", this.getTreasurePos().getX());
      var1.putInt("TreasurePosY", this.getTreasurePos().getY());
      var1.putInt("TreasurePosZ", this.getTreasurePos().getZ());
      var1.putBoolean("GotFish", this.gotFish());
      var1.putInt("Moistness", this.getMoistnessLevel());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      int var2 = var1.getInt("TreasurePosX");
      int var3 = var1.getInt("TreasurePosY");
      int var4 = var1.getInt("TreasurePosZ");
      this.setTreasurePos(new BlockPos(var2, var3, var4));
      super.readAdditionalSaveData(var1);
      this.setGotFish(var1.getBoolean("GotFish"));
      this.setMoisntessLevel(var1.getInt("Moistness"));
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreathAirGoal(this));
      this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(1, new Dolphin.DolphinSwimToTreasureGoal(this));
      this.goalSelector.addGoal(2, new Dolphin.DolphinSwimWithPlayerGoal(this, 4.0D));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
      this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2000000476837158D, true));
      this.goalSelector.addGoal(8, new Dolphin.PlayWithItemsGoal());
      this.goalSelector.addGoal(8, new FollowBoatGoal(this));
      this.goalSelector.addGoal(9, new AvoidEntityGoal(this, Guardian.class, 8.0F, 1.0D, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Guardian.class})).setAlertOthers());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 1.2000000476837158D).add(Attributes.ATTACK_DAMAGE, 3.0D);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new WaterBoundPathNavigation(this, var1);
   }

   public boolean doHurtTarget(Entity var1) {
      boolean var2 = var1.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (var2) {
         this.doEnchantDamageEffects(this, var1);
         this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
      }

      return var2;
   }

   public int getMaxAirSupply() {
      return 4800;
   }

   protected int increaseAirSupply(int var1) {
      return this.getMaxAirSupply();
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.3F;
   }

   public int getMaxHeadXRot() {
      return 1;
   }

   public int getMaxHeadYRot() {
      return 1;
   }

   protected boolean canRide(Entity var1) {
      return true;
   }

   public boolean canTakeItem(ItemStack var1) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(var1);
      if (!this.getItemBySlot(var2).isEmpty()) {
         return false;
      } else {
         return var2 == EquipmentSlot.MAINHAND && super.canTakeItem(var1);
      }
   }

   protected void pickUpItem(ItemEntity var1) {
      if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
         ItemStack var2 = var1.getItem();
         if (this.canHoldItem(var2)) {
            this.onItemPickup(var1);
            this.setItemSlot(EquipmentSlot.MAINHAND, var2);
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0F;
            this.take(var1, var2.getCount());
            var1.discard();
         }
      }

   }

   public void tick() {
      super.tick();
      if (this.isNoAi()) {
         this.setAirSupply(this.getMaxAirSupply());
      } else {
         if (this.isInWaterRainOrBubble()) {
            this.setMoisntessLevel(2400);
         } else {
            this.setMoisntessLevel(this.getMoistnessLevel() - 1);
            if (this.getMoistnessLevel() <= 0) {
               this.hurt(DamageSource.DRY_OUT, 1.0F);
            }

            if (this.onGround) {
               this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
               this.setYRot(this.random.nextFloat() * 360.0F);
               this.onGround = false;
               this.hasImpulse = true;
            }
         }

         if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
            Vec3 var1 = this.getViewVector(0.0F);
            float var2 = Mth.cos(this.getYRot() * 0.017453292F) * 0.3F;
            float var3 = Mth.sin(this.getYRot() * 0.017453292F) * 0.3F;
            float var4 = 1.2F - this.random.nextFloat() * 0.7F;

            for(int var5 = 0; var5 < 2; ++var5) {
               this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - var1.field_414 * (double)var4 + (double)var2, this.getY() - var1.field_415, this.getZ() - var1.field_416 * (double)var4 + (double)var3, 0.0D, 0.0D, 0.0D);
               this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - var1.field_414 * (double)var4 - (double)var2, this.getY() - var1.field_415, this.getZ() - var1.field_416 * (double)var4 - (double)var3, 0.0D, 0.0D, 0.0D);
            }
         }

      }
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 38) {
         this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
      } else {
         super.handleEntityEvent(var1);
      }

   }

   private void addParticlesAroundSelf(ParticleOptions var1) {
      for(int var2 = 0; var2 < 7; ++var2) {
         double var3 = this.random.nextGaussian() * 0.01D;
         double var5 = this.random.nextGaussian() * 0.01D;
         double var7 = this.random.nextGaussian() * 0.01D;
         this.level.addParticle(var1, this.getRandomX(1.0D), this.getRandomY() + 0.2D, this.getRandomZ(1.0D), var3, var5, var7);
      }

   }

   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!var3.isEmpty() && var3.method_86(ItemTags.FISHES)) {
         if (!this.level.isClientSide) {
            this.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 1.0F);
         }

         this.setGotFish(true);
         if (!var1.getAbilities().instabuild) {
            var3.shrink(1);
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.DOLPHIN_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.DOLPHIN_DEATH;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.DOLPHIN_AMBIENT_WATER : SoundEvents.DOLPHIN_AMBIENT;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.DOLPHIN_SPLASH;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.DOLPHIN_SWIM;
   }

   protected boolean closeToNextPos() {
      BlockPos var1 = this.getNavigation().getTargetPos();
      return var1 != null ? var1.closerThan(this.position(), 12.0D) : false;
   }

   public void travel(Vec3 var1) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
         }
      } else {
         super.travel(var1);
      }

   }

   public boolean canBeLeashed(Player var1) {
      return true;
   }

   static {
      TREASURE_POS = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BLOCK_POS);
      GOT_FISH = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BOOLEAN);
      MOISTNESS_LEVEL = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.INT);
      SWIM_WITH_PLAYER_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
      ALLOWED_ITEMS = (var0) -> {
         return !var0.hasPickUpDelay() && var0.isAlive() && var0.isInWater();
      };
   }

   private static class DolphinSwimToTreasureGoal extends Goal {
      private final Dolphin dolphin;
      private boolean stuck;

      DolphinSwimToTreasureGoal(Dolphin var1) {
         super();
         this.dolphin = var1;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean isInterruptable() {
         return false;
      }

      public boolean canUse() {
         return this.dolphin.gotFish() && this.dolphin.getAirSupply() >= 100;
      }

      public boolean canContinueToUse() {
         BlockPos var1 = this.dolphin.getTreasurePos();
         return !(new BlockPos((double)var1.getX(), this.dolphin.getY(), (double)var1.getZ())).closerThan(this.dolphin.position(), 4.0D) && !this.stuck && this.dolphin.getAirSupply() >= 100;
      }

      public void start() {
         if (this.dolphin.level instanceof ServerLevel) {
            ServerLevel var1 = (ServerLevel)this.dolphin.level;
            this.stuck = false;
            this.dolphin.getNavigation().stop();
            BlockPos var2 = this.dolphin.blockPosition();
            StructureFeature var3 = (double)var1.random.nextFloat() >= 0.5D ? StructureFeature.OCEAN_RUIN : StructureFeature.SHIPWRECK;
            BlockPos var4 = var1.findNearestMapFeature(var3, var2, 50, false);
            if (var4 == null) {
               StructureFeature var5 = var3.equals(StructureFeature.OCEAN_RUIN) ? StructureFeature.SHIPWRECK : StructureFeature.OCEAN_RUIN;
               BlockPos var6 = var1.findNearestMapFeature(var5, var2, 50, false);
               if (var6 == null) {
                  this.stuck = true;
                  return;
               }

               this.dolphin.setTreasurePos(var6);
            } else {
               this.dolphin.setTreasurePos(var4);
            }

            var1.broadcastEntityEvent(this.dolphin, (byte)38);
         }
      }

      public void stop() {
         BlockPos var1 = this.dolphin.getTreasurePos();
         if ((new BlockPos((double)var1.getX(), this.dolphin.getY(), (double)var1.getZ())).closerThan(this.dolphin.position(), 4.0D) || this.stuck) {
            this.dolphin.setGotFish(false);
         }

      }

      public void tick() {
         Level var1 = this.dolphin.level;
         if (this.dolphin.closeToNextPos() || this.dolphin.getNavigation().isDone()) {
            Vec3 var2 = Vec3.atCenterOf(this.dolphin.getTreasurePos());
            Vec3 var3 = DefaultRandomPos.getPosTowards(this.dolphin, 16, 1, var2, 0.39269909262657166D);
            if (var3 == null) {
               var3 = DefaultRandomPos.getPosTowards(this.dolphin, 8, 4, var2, 1.5707963705062866D);
            }

            if (var3 != null) {
               BlockPos var4 = new BlockPos(var3);
               if (!var1.getFluidState(var4).method_56(FluidTags.WATER) || !var1.getBlockState(var4).isPathfindable(var1, var4, PathComputationType.WATER)) {
                  var3 = DefaultRandomPos.getPosTowards(this.dolphin, 8, 5, var2, 1.5707963705062866D);
               }
            }

            if (var3 == null) {
               this.stuck = true;
               return;
            }

            this.dolphin.getLookControl().setLookAt(var3.field_414, var3.field_415, var3.field_416, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
            this.dolphin.getNavigation().moveTo(var3.field_414, var3.field_415, var3.field_416, 1.3D);
            if (var1.random.nextInt(this.adjustedTickDelay(80)) == 0) {
               var1.broadcastEntityEvent(this.dolphin, (byte)38);
            }
         }

      }
   }

   private static class DolphinSwimWithPlayerGoal extends Goal {
      private final Dolphin dolphin;
      private final double speedModifier;
      @Nullable
      private Player player;

      DolphinSwimWithPlayerGoal(Dolphin var1, double var2) {
         super();
         this.dolphin = var1;
         this.speedModifier = var2;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         this.player = this.dolphin.level.getNearestPlayer(Dolphin.SWIM_WITH_PLAYER_TARGETING, this.dolphin);
         if (this.player == null) {
            return false;
         } else {
            return this.player.isSwimming() && this.dolphin.getTarget() != this.player;
         }
      }

      public boolean canContinueToUse() {
         return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr(this.player) < 256.0D;
      }

      public void start() {
         this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
      }

      public void stop() {
         this.player = null;
         this.dolphin.getNavigation().stop();
      }

      public void tick() {
         this.dolphin.getLookControl().setLookAt(this.player, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
         if (this.dolphin.distanceToSqr(this.player) < 6.25D) {
            this.dolphin.getNavigation().stop();
         } else {
            this.dolphin.getNavigation().moveTo((Entity)this.player, this.speedModifier);
         }

         if (this.player.isSwimming() && this.player.level.random.nextInt(6) == 0) {
            this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
         }

      }
   }

   class PlayWithItemsGoal extends Goal {
      private int cooldown;

      PlayWithItemsGoal() {
         super();
      }

      public boolean canUse() {
         if (this.cooldown > Dolphin.this.tickCount) {
            return false;
         } else {
            List var1 = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
            return !var1.isEmpty() || !Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
         }
      }

      public void start() {
         List var1 = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
         if (!var1.isEmpty()) {
            Dolphin.this.getNavigation().moveTo((Entity)var1.get(0), 1.2000000476837158D);
            Dolphin.this.playSound(SoundEvents.DOLPHIN_PLAY, 1.0F, 1.0F);
         }

         this.cooldown = 0;
      }

      public void stop() {
         ItemStack var1 = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!var1.isEmpty()) {
            this.drop(var1);
            Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.cooldown = Dolphin.this.tickCount + Dolphin.this.random.nextInt(100);
         }

      }

      public void tick() {
         List var1 = Dolphin.this.level.getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Dolphin.ALLOWED_ITEMS);
         ItemStack var2 = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!var2.isEmpty()) {
            this.drop(var2);
            Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         } else if (!var1.isEmpty()) {
            Dolphin.this.getNavigation().moveTo((Entity)var1.get(0), 1.2000000476837158D);
         }

      }

      private void drop(ItemStack var1) {
         if (!var1.isEmpty()) {
            double var2 = Dolphin.this.getEyeY() - 0.30000001192092896D;
            ItemEntity var4 = new ItemEntity(Dolphin.this.level, Dolphin.this.getX(), var2, Dolphin.this.getZ(), var1);
            var4.setPickUpDelay(40);
            var4.setThrower(Dolphin.this.getUUID());
            float var5 = 0.3F;
            float var6 = Dolphin.this.random.nextFloat() * 6.2831855F;
            float var7 = 0.02F * Dolphin.this.random.nextFloat();
            var4.setDeltaMovement((double)(0.3F * -Mth.sin(Dolphin.this.getYRot() * 0.017453292F) * Mth.cos(Dolphin.this.getXRot() * 0.017453292F) + Mth.cos(var6) * var7), (double)(0.3F * Mth.sin(Dolphin.this.getXRot() * 0.017453292F) * 1.5F), (double)(0.3F * Mth.cos(Dolphin.this.getYRot() * 0.017453292F) * Mth.cos(Dolphin.this.getXRot() * 0.017453292F) + Mth.sin(var6) * var7));
            Dolphin.this.level.addFreshEntity(var4);
         }
      }
   }
}
