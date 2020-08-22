package net.minecraft.world.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
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
import net.minecraft.world.entity.ai.control.DolphinLookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
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
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class Dolphin extends WaterAnimal {
   private static final EntityDataAccessor TREASURE_POS;
   private static final EntityDataAccessor GOT_FISH;
   private static final EntityDataAccessor MOISNTESS_LEVEL;
   private static final TargetingConditions SWIM_WITH_PLAYER_TARGETING;
   public static final Predicate ALLOWED_ITEMS;

   public Dolphin(EntityType var1, Level var2) {
      super(var1, var2);
      this.moveControl = new Dolphin.DolphinMoveControl(this);
      this.lookControl = new DolphinLookControl(this, 10);
      this.setCanPickUpLoot(true);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.setAirSupply(this.getMaxAirSupply());
      this.xRot = 0.0F;
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
      return (Integer)this.entityData.get(MOISNTESS_LEVEL);
   }

   public void setMoisntessLevel(int var1) {
      this.entityData.set(MOISNTESS_LEVEL, var1);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(TREASURE_POS, BlockPos.ZERO);
      this.entityData.define(GOT_FISH, false);
      this.entityData.define(MOISNTESS_LEVEL, 2400);
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

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.2000000476837158D);
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new WaterBoundPathNavigation(this, var1);
   }

   public boolean doHurtTarget(Entity var1) {
      boolean var2 = var1.hurt(DamageSource.mobAttack(this), (float)((int)this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue()));
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
            this.setItemSlot(EquipmentSlot.MAINHAND, var2);
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0F;
            this.take(var1, var2.getCount());
            var1.remove();
         }
      }

   }

   public void tick() {
      super.tick();
      if (!this.isNoAi()) {
         if (this.isInWaterRainOrBubble()) {
            this.setMoisntessLevel(2400);
         } else {
            this.setMoisntessLevel(this.getMoistnessLevel() - 1);
            if (this.getMoistnessLevel() <= 0) {
               this.hurt(DamageSource.DRY_OUT, 1.0F);
            }

            if (this.onGround) {
               this.setDeltaMovement(this.getDeltaMovement().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
               this.yRot = this.random.nextFloat() * 360.0F;
               this.onGround = false;
               this.hasImpulse = true;
            }
         }

         if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
            Vec3 var1 = this.getViewVector(0.0F);
            float var2 = Mth.cos(this.yRot * 0.017453292F) * 0.3F;
            float var3 = Mth.sin(this.yRot * 0.017453292F) * 0.3F;
            float var4 = 1.2F - this.random.nextFloat() * 0.7F;

            for(int var5 = 0; var5 < 2; ++var5) {
               this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - var1.x * (double)var4 + (double)var2, this.getY() - var1.y, this.getZ() - var1.z * (double)var4 + (double)var3, 0.0D, 0.0D, 0.0D);
               this.level.addParticle(ParticleTypes.DOLPHIN, this.getX() - var1.x * (double)var4 - (double)var2, this.getY() - var1.y, this.getZ() - var1.z * (double)var4 - (double)var3, 0.0D, 0.0D, 0.0D);
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

   protected boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!var3.isEmpty() && var3.getItem().is(ItemTags.FISHES)) {
         if (!this.level.isClientSide) {
            this.playSound(SoundEvents.DOLPHIN_EAT, 1.0F, 1.0F);
         }

         this.setGotFish(true);
         if (!var1.abilities.instabuild) {
            var3.shrink(1);
         }

         return true;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   public static boolean checkDolphinSpawnRules(EntityType var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return var3.getY() > 45 && var3.getY() < var1.getSeaLevel() && (var1.getBiome(var3) != Biomes.OCEAN || var1.getBiome(var3) != Biomes.DEEP_OCEAN) && var1.getFluidState(var3).is(FluidTags.WATER);
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
      MOISNTESS_LEVEL = SynchedEntityData.defineId(Dolphin.class, EntityDataSerializers.INT);
      SWIM_WITH_PLAYER_TARGETING = (new TargetingConditions()).range(10.0D).allowSameTeam().allowInvulnerable().allowUnseeable();
      ALLOWED_ITEMS = (var0) -> {
         return !var0.hasPickUpDelay() && var0.isAlive() && var0.isInWater();
      };
   }

   static class DolphinSwimToTreasureGoal extends Goal {
      private final Dolphin dolphin;
      private boolean stuck;

      DolphinSwimToTreasureGoal(Dolphin var1) {
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
            BlockPos var2 = new BlockPos(this.dolphin);
            String var3 = (double)var1.random.nextFloat() >= 0.5D ? "Ocean_Ruin" : "Shipwreck";
            BlockPos var4 = var1.findNearestMapFeature(var3, var2, 50, false);
            if (var4 == null) {
               BlockPos var5 = var1.findNearestMapFeature(var3.equals("Ocean_Ruin") ? "Shipwreck" : "Ocean_Ruin", var2, 50, false);
               if (var5 == null) {
                  this.stuck = true;
                  return;
               }

               this.dolphin.setTreasurePos(var5);
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
            Vec3 var2 = new Vec3(this.dolphin.getTreasurePos());
            Vec3 var3 = RandomPos.getPosTowards(this.dolphin, 16, 1, var2, 0.39269909262657166D);
            if (var3 == null) {
               var3 = RandomPos.getPosTowards(this.dolphin, 8, 4, var2);
            }

            if (var3 != null) {
               BlockPos var4 = new BlockPos(var3);
               if (!var1.getFluidState(var4).is(FluidTags.WATER) || !var1.getBlockState(var4).isPathfindable(var1, var4, PathComputationType.WATER)) {
                  var3 = RandomPos.getPosTowards(this.dolphin, 8, 5, var2);
               }
            }

            if (var3 == null) {
               this.stuck = true;
               return;
            }

            this.dolphin.getLookControl().setLookAt(var3.x, var3.y, var3.z, (float)(this.dolphin.getMaxHeadYRot() + 20), (float)this.dolphin.getMaxHeadXRot());
            this.dolphin.getNavigation().moveTo(var3.x, var3.y, var3.z, 1.3D);
            if (var1.random.nextInt(80) == 0) {
               var1.broadcastEntityEvent(this.dolphin, (byte)38);
            }
         }

      }
   }

   static class DolphinSwimWithPlayerGoal extends Goal {
      private final Dolphin dolphin;
      private final double speedModifier;
      private Player player;

      DolphinSwimWithPlayerGoal(Dolphin var1, double var2) {
         this.dolphin = var1;
         this.speedModifier = var2;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         this.player = this.dolphin.level.getNearestPlayer(Dolphin.SWIM_WITH_PLAYER_TARGETING, this.dolphin);
         return this.player == null ? false : this.player.isSwimming();
      }

      public boolean canContinueToUse() {
         return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr(this.player) < 256.0D;
      }

      public void start() {
         this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100));
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
            this.player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100));
         }

      }
   }

   class PlayWithItemsGoal extends Goal {
      private int cooldown;

      private PlayWithItemsGoal() {
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
            var4.setDeltaMovement((double)(0.3F * -Mth.sin(Dolphin.this.yRot * 0.017453292F) * Mth.cos(Dolphin.this.xRot * 0.017453292F) + Mth.cos(var6) * var7), (double)(0.3F * Mth.sin(Dolphin.this.xRot * 0.017453292F) * 1.5F), (double)(0.3F * Mth.cos(Dolphin.this.yRot * 0.017453292F) * Mth.cos(Dolphin.this.xRot * 0.017453292F) + Mth.sin(var6) * var7));
            Dolphin.this.level.addFreshEntity(var4);
         }
      }

      // $FF: synthetic method
      PlayWithItemsGoal(Object var2) {
         this();
      }
   }

   static class DolphinMoveControl extends MoveControl {
      private final Dolphin dolphin;

      public DolphinMoveControl(Dolphin var1) {
         super(var1);
         this.dolphin = var1;
      }

      public void tick() {
         if (this.dolphin.isInWater()) {
            this.dolphin.setDeltaMovement(this.dolphin.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
         }

         if (this.operation == MoveControl.Operation.MOVE_TO && !this.dolphin.getNavigation().isDone()) {
            double var1 = this.wantedX - this.dolphin.getX();
            double var3 = this.wantedY - this.dolphin.getY();
            double var5 = this.wantedZ - this.dolphin.getZ();
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            if (var7 < 2.500000277905201E-7D) {
               this.mob.setZza(0.0F);
            } else {
               float var9 = (float)(Mth.atan2(var5, var1) * 57.2957763671875D) - 90.0F;
               this.dolphin.yRot = this.rotlerp(this.dolphin.yRot, var9, 10.0F);
               this.dolphin.yBodyRot = this.dolphin.yRot;
               this.dolphin.yHeadRot = this.dolphin.yRot;
               float var10 = (float)(this.speedModifier * this.dolphin.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
               if (this.dolphin.isInWater()) {
                  this.dolphin.setSpeed(var10 * 0.02F);
                  float var11 = -((float)(Mth.atan2(var3, (double)Mth.sqrt(var1 * var1 + var5 * var5)) * 57.2957763671875D));
                  var11 = Mth.clamp(Mth.wrapDegrees(var11), -85.0F, 85.0F);
                  this.dolphin.xRot = this.rotlerp(this.dolphin.xRot, var11, 5.0F);
                  float var12 = Mth.cos(this.dolphin.xRot * 0.017453292F);
                  float var13 = Mth.sin(this.dolphin.xRot * 0.017453292F);
                  this.dolphin.zza = var12 * var10;
                  this.dolphin.yya = -var13 * var10;
               } else {
                  this.dolphin.setSpeed(var10 * 0.1F);
               }

            }
         } else {
            this.dolphin.setSpeed(0.0F);
            this.dolphin.setXxa(0.0F);
            this.dolphin.setYya(0.0F);
            this.dolphin.setZza(0.0F);
         }
      }
   }
}
