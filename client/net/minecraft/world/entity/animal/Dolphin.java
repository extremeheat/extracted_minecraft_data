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
import net.minecraft.tags.StructureTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class Dolphin extends WaterAnimal {
   private static final EntityDataAccessor<BlockPos> TREASURE_POS = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<Boolean> GOT_FISH = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> MOISTNESS_LEVEL = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.INT);
   static final TargetingConditions SWIM_WITH_PLAYER_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();
   public static final int TOTAL_AIR_SUPPLY = 4800;
   private static final int TOTAL_MOISTNESS_LEVEL = 2400;
   public static final Predicate<ItemEntity> ALLOWED_ITEMS = var0 -> !var0.hasPickUpDelay() && var0.isAlive() && var0.isInWater();

   public Dolphin(EntityType<? extends Dolphin> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
      this.lookControl = new SmoothSwimmingLookControl(this, 10);
      this.setCanPickUpLoot(true);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      this.setAirSupply(this.getMaxAirSupply());
      this.setXRot(0.0F);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   @Override
   protected void handleAirSupply(int var1) {
   }

   public void setTreasurePos(BlockPos var1) {
      this.entityData.set(TREASURE_POS, var1);
   }

   public BlockPos getTreasurePos() {
      return this.entityData.get(TREASURE_POS);
   }

   public boolean gotFish() {
      return this.entityData.get(GOT_FISH);
   }

   public void setGotFish(boolean var1) {
      this.entityData.set(GOT_FISH, var1);
   }

   public int getMoistnessLevel() {
      return this.entityData.get(MOISTNESS_LEVEL);
   }

   public void setMoisntessLevel(int var1) {
      this.entityData.set(MOISTNESS_LEVEL, var1);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(TREASURE_POS, BlockPos.ZERO);
      var1.define(GOT_FISH, false);
      var1.define(MOISTNESS_LEVEL, 2400);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("TreasurePosX", this.getTreasurePos().getX());
      var1.putInt("TreasurePosY", this.getTreasurePos().getY());
      var1.putInt("TreasurePosZ", this.getTreasurePos().getZ());
      var1.putBoolean("GotFish", this.gotFish());
      var1.putInt("Moistness", this.getMoistnessLevel());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      int var2 = var1.getInt("TreasurePosX");
      int var3 = var1.getInt("TreasurePosY");
      int var4 = var1.getInt("TreasurePosZ");
      this.setTreasurePos(new BlockPos(var2, var3, var4));
      super.readAdditionalSaveData(var1);
      this.setGotFish(var1.getBoolean("GotFish"));
      this.setMoisntessLevel(var1.getInt("Moistness"));
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new BreathAirGoal(this));
      this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
      this.goalSelector.addGoal(1, new Dolphin.DolphinSwimToTreasureGoal(this));
      this.goalSelector.addGoal(2, new Dolphin.DolphinSwimWithPlayerGoal(this, 4.0));
      this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0, 10));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
      this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2000000476837158, true));
      this.goalSelector.addGoal(8, new Dolphin.PlayWithItemsGoal());
      this.goalSelector.addGoal(8, new FollowBoatGoal(this));
      this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, Guardian.class, 8.0F, 1.0, 1.0));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Guardian.class).setAlertOthers());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 1.2000000476837158).add(Attributes.ATTACK_DAMAGE, 3.0);
   }

   @Override
   protected PathNavigation createNavigation(Level var1) {
      return new WaterBoundPathNavigation(this, var1);
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      boolean var2 = var1.hurt(this.damageSources().mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
      if (var2) {
         this.doEnchantDamageEffects(this, var1);
         this.playSound(SoundEvents.DOLPHIN_ATTACK, 1.0F, 1.0F);
      }

      return var2;
   }

   @Override
   public int getMaxAirSupply() {
      return 4800;
   }

   @Override
   protected int increaseAirSupply(int var1) {
      return this.getMaxAirSupply();
   }

   @Override
   public int getMaxHeadXRot() {
      return 1;
   }

   @Override
   public int getMaxHeadYRot() {
      return 1;
   }

   @Override
   protected boolean canRide(Entity var1) {
      return true;
   }

   @Override
   public boolean canTakeItem(ItemStack var1) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(var1);
      return !this.getItemBySlot(var2).isEmpty() ? false : var2 == EquipmentSlot.MAINHAND && super.canTakeItem(var1);
   }

   @Override
   protected void pickUpItem(ItemEntity var1) {
      if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
         ItemStack var2 = var1.getItem();
         if (this.canHoldItem(var2)) {
            this.onItemPickup(var1);
            this.setItemSlot(EquipmentSlot.MAINHAND, var2);
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.take(var1, var2.getCount());
            var1.discard();
         }
      }
   }

   @Override
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
               this.hurt(this.damageSources().dryOut(), 1.0F);
            }

            if (this.onGround()) {
               this.setDeltaMovement(
                  this.getDeltaMovement()
                     .add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F))
               );
               this.setYRot(this.random.nextFloat() * 360.0F);
               this.setOnGround(false);
               this.hasImpulse = true;
            }
         }

         if (this.level().isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03) {
            Vec3 var1 = this.getViewVector(0.0F);
            float var2 = Mth.cos(this.getYRot() * 0.017453292F) * 0.3F;
            float var3 = Mth.sin(this.getYRot() * 0.017453292F) * 0.3F;
            float var4 = 1.2F - this.random.nextFloat() * 0.7F;

            for (int var5 = 0; var5 < 2; var5++) {
               this.level()
                  .addParticle(
                     ParticleTypes.DOLPHIN,
                     this.getX() - var1.x * (double)var4 + (double)var2,
                     this.getY() - var1.y,
                     this.getZ() - var1.z * (double)var4 + (double)var3,
                     0.0,
                     0.0,
                     0.0
                  );
               this.level()
                  .addParticle(
                     ParticleTypes.DOLPHIN,
                     this.getX() - var1.x * (double)var4 - (double)var2,
                     this.getY() - var1.y,
                     this.getZ() - var1.z * (double)var4 - (double)var3,
                     0.0,
                     0.0,
                     0.0
                  );
            }
         }
      }
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 38) {
         this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
      } else {
         super.handleEntityEvent(var1);
      }
   }

   private void addParticlesAroundSelf(ParticleOptions var1) {
      for (int var2 = 0; var2 < 7; var2++) {
         double var3 = this.random.nextGaussian() * 0.01;
         double var5 = this.random.nextGaussian() * 0.01;
         double var7 = this.random.nextGaussian() * 0.01;
         this.level().addParticle(var1, this.getRandomX(1.0), this.getRandomY() + 0.2, this.getRandomZ(1.0), var3, var5, var7);
      }
   }

   @Override
   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!var3.isEmpty() && var3.is(ItemTags.FISHES)) {
         if (!this.level().isClientSide) {
            this.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 1.0F);
         }

         this.setGotFish(true);
         var3.consume(1, var1);
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.DOLPHIN_HURT;
   }

   @Nullable
   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.DOLPHIN_DEATH;
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      return this.isInWater() ? SoundEvents.DOLPHIN_AMBIENT_WATER : SoundEvents.DOLPHIN_AMBIENT;
   }

   @Override
   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.DOLPHIN_SPLASH;
   }

   @Override
   protected SoundEvent getSwimSound() {
      return SoundEvents.DOLPHIN_SWIM;
   }

   protected boolean closeToNextPos() {
      BlockPos var1 = this.getNavigation().getTargetPos();
      return var1 != null ? var1.closerToCenterThan(this.position(), 12.0) : false;
   }

   @Override
   public void travel(Vec3 var1) {
      if (this.isEffectiveAi() && this.isInWater()) {
         this.moveRelative(this.getSpeed(), var1);
         this.move(MoverType.SELF, this.getDeltaMovement());
         this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
         if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
         }
      } else {
         super.travel(var1);
      }
   }

   @Override
   public boolean canBeLeashed(Player var1) {
      return true;
   }

   static class DolphinSwimToTreasureGoal extends Goal {
      private final Dolphin dolphin;
      private boolean stuck;

      DolphinSwimToTreasureGoal(Dolphin var1) {
         super();
         this.dolphin = var1;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      @Override
      public boolean isInterruptable() {
         return false;
      }

      @Override
      public boolean canUse() {
         return this.dolphin.gotFish() && this.dolphin.getAirSupply() >= 100;
      }

      @Override
      public boolean canContinueToUse() {
         BlockPos var1 = this.dolphin.getTreasurePos();
         return !BlockPos.containing((double)var1.getX(), this.dolphin.getY(), (double)var1.getZ()).closerToCenterThan(this.dolphin.position(), 4.0)
            && !this.stuck
            && this.dolphin.getAirSupply() >= 100;
      }

      @Override
      public void start() {
         if (this.dolphin.level() instanceof ServerLevel) {
            ServerLevel var1 = (ServerLevel)this.dolphin.level();
            this.stuck = false;
            this.dolphin.getNavigation().stop();
            BlockPos var2 = this.dolphin.blockPosition();
            BlockPos var3 = var1.findNearestMapStructure(StructureTags.DOLPHIN_LOCATED, var2, 50, false);
            if (var3 != null) {
               this.dolphin.setTreasurePos(var3);
               var1.broadcastEntityEvent(this.dolphin, (byte)38);
            } else {
               this.stuck = true;
            }
         }
      }

      @Override
      public void stop() {
         BlockPos var1 = this.dolphin.getTreasurePos();
         if (BlockPos.containing((double)var1.getX(), this.dolphin.getY(), (double)var1.getZ()).closerToCenterThan(this.dolphin.position(), 4.0) || this.stuck) {
            this.dolphin.setGotFish(false);
         }
      }

      @Override
      public void tick() {
         Level var1 = this.dolphin.level();
         if (this.dolphin.closeToNextPos() || this.dolphin.getNavigation().isDone()) {
            Vec3 var2 = Vec3.atCenterOf(this.dolphin.getTreasurePos());
            Vec3 var3 = DefaultRandomPos.getPosTowards(this.dolphin, 16, 1, var2, 0.39269909262657166);
            if (var3 == null) {
               var3 = DefaultRandomPos.getPosTowards(this.dolphin, 8, 4, var2, 1.5707963705062866);
            }

            if (var3 != null) {
               BlockPos var4 = BlockPos.containing(var3);
               if (!var1.getFluidState(var4).is(FluidTags.WATER) || !var1.getBlockState(var4).isPathfindable(PathComputationType.WATER)) {
                  var3 = DefaultRandomPos.getPosTowards(this.dolphin, 8, 5, var2, 1.5707963705062866);
               }
            }

            if (var3 == null) {
               this.stuck = true;
               return;
            }

            this.dolphin.getLookControl().setLookAt(var3.x, var3.y, var3.z, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
            this.dolphin.getNavigation().moveTo(var3.x, var3.y, var3.z, 1.3);
            if (var1.random.nextInt(this.adjustedTickDelay(80)) == 0) {
               var1.broadcastEntityEvent(this.dolphin, (byte)38);
            }
         }
      }
   }

   static class DolphinSwimWithPlayerGoal extends Goal {
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

      @Override
      public boolean canUse() {
         this.player = this.dolphin.level().getNearestPlayer(Dolphin.SWIM_WITH_PLAYER_TARGETING, this.dolphin);
         return this.player == null ? false : this.player.isSwimming() && this.dolphin.getTarget() != this.player;
      }

      @Override
      public boolean canContinueToUse() {
         return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr(this.player) < 256.0;
      }

      @Override
      public void start() {
         this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
      }

      @Override
      public void stop() {
         this.player = null;
         this.dolphin.getNavigation().stop();
      }

      @Override
      public void tick() {
         this.dolphin.getLookControl().setLookAt(this.player, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
         if (this.dolphin.distanceToSqr(this.player) < 6.25) {
            this.dolphin.getNavigation().stop();
         } else {
            this.dolphin.getNavigation().moveTo(this.player, this.speedModifier);
         }

         if (this.player.isSwimming() && this.player.level().random.nextInt(6) == 0) {
            this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
         }
      }
   }

   class PlayWithItemsGoal extends Goal {
      private int cooldown;

      PlayWithItemsGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         if (this.cooldown > Dolphin.this.tickCount) {
            return false;
         } else {
            List var1 = Dolphin.this.level().getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Dolphin.ALLOWED_ITEMS);
            return !var1.isEmpty() || !Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
         }
      }

      @Override
      public void start() {
         List var1 = Dolphin.this.level().getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Dolphin.ALLOWED_ITEMS);
         if (!var1.isEmpty()) {
            Dolphin.this.getNavigation().moveTo((Entity)var1.get(0), 1.2000000476837158);
            Dolphin.this.playSound(SoundEvents.DOLPHIN_PLAY, 1.0F, 1.0F);
         }

         this.cooldown = 0;
      }

      @Override
      public void stop() {
         ItemStack var1 = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!var1.isEmpty()) {
            this.drop(var1);
            Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.cooldown = Dolphin.this.tickCount + Dolphin.this.random.nextInt(100);
         }
      }

      @Override
      public void tick() {
         List var1 = Dolphin.this.level().getEntitiesOfClass(ItemEntity.class, Dolphin.this.getBoundingBox().inflate(8.0, 8.0, 8.0), Dolphin.ALLOWED_ITEMS);
         ItemStack var2 = Dolphin.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!var2.isEmpty()) {
            this.drop(var2);
            Dolphin.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
         } else if (!var1.isEmpty()) {
            Dolphin.this.getNavigation().moveTo((Entity)var1.get(0), 1.2000000476837158);
         }
      }

      private void drop(ItemStack var1) {
         if (!var1.isEmpty()) {
            double var2 = Dolphin.this.getEyeY() - 0.30000001192092896;
            ItemEntity var4 = new ItemEntity(Dolphin.this.level(), Dolphin.this.getX(), var2, Dolphin.this.getZ(), var1);
            var4.setPickUpDelay(40);
            var4.setThrower(Dolphin.this);
            float var5 = 0.3F;
            float var6 = Dolphin.this.random.nextFloat() * 6.2831855F;
            float var7 = 0.02F * Dolphin.this.random.nextFloat();
            var4.setDeltaMovement(
               (double)(0.3F * -Mth.sin(Dolphin.this.getYRot() * 0.017453292F) * Mth.cos(Dolphin.this.getXRot() * 0.017453292F) + Mth.cos(var6) * var7),
               (double)(0.3F * Mth.sin(Dolphin.this.getXRot() * 0.017453292F) * 1.5F),
               (double)(0.3F * Mth.cos(Dolphin.this.getYRot() * 0.017453292F) * Mth.cos(Dolphin.this.getXRot() * 0.017453292F) + Mth.sin(var6) * var7)
            );
            Dolphin.this.level().addFreshEntity(var4);
         }
      }
   }
}
