package net.minecraft.world.entity.animal.happyghast;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class HappyGhast extends Animal {
   public static final float BABY_SCALE = 0.2375F;
   public static final int WANDER_GROUND_DISTANCE = 16;
   public static final int SMALL_RESTRICTION_RADIUS = 32;
   public static final int LARGE_RESTRICTION_RADIUS = 64;
   public static final int RESTRICTION_RADIUS_BUFFER = 16;
   public static final int FAST_HEALING_TICKS = 20;
   public static final int SLOW_HEALING_TICKS = 600;
   public static final int MAX_PASSANGERS = 4;
   private static final int STILL_TIMEOUT_ON_LOAD_GRACE_PERIOD = 60;
   private static final int MAX_STILL_TIMEOUT = 10;
   public static final float SPEED_MULTIPLIER_WHEN_PANICKING = 2.0F;
   private int leashHolderTime = 0;
   private int serverStillTimeout;
   private static final EntityDataAccessor<Boolean> IS_LEASH_HOLDER;
   private static final EntityDataAccessor<Boolean> STAYS_STILL;
   private static final float MAX_SCALE = 1.0F;

   public HappyGhast(EntityType<? extends HappyGhast> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new Ghast.GhastMoveControl(this, true, this::isOnStillTimeout);
      this.lookControl = new HappyGhastLookControl();
   }

   private void setServerStillTimeout(int var1) {
      if (this.serverStillTimeout <= 0 && var1 > 0) {
         Level var3 = this.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
            var2.getChunkSource().chunkMap.sendToTrackingPlayers(this, ClientboundEntityPositionSyncPacket.of(this));
         }
      }

      this.serverStillTimeout = var1;
      this.syncStayStillFlag();
   }

   private PathNavigation createBabyNavigation(Level var1) {
      return new BabyFlyingPathNavigation(this, var1);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(3, new HappyGhastFloatGoal());
      this.goalSelector.addGoal(4, new TemptGoal.ForNonPathfinders(this, 1.0, (var1) -> !this.isWearingBodyArmor() && !this.isBaby() ? var1.is(ItemTags.HAPPY_GHAST_TEMPT_ITEMS) : var1.is(ItemTags.HAPPY_GHAST_FOOD), false, 7.0));
      this.goalSelector.addGoal(5, new Ghast.RandomFloatAroundGoal(this, 16));
   }

   private void adultGhastSetup() {
      this.moveControl = new Ghast.GhastMoveControl(this, true, this::isOnStillTimeout);
      this.lookControl = new HappyGhastLookControl();
      this.navigation = this.createNavigation(this.level());
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         this.removeAllGoals((var0) -> true);
         this.registerGoals();
         this.brain.stopAll(var1, this);
         this.brain.clearMemories();
      }

   }

   private void babyGhastSetup() {
      this.moveControl = new FlyingMoveControl(this, 180, true);
      this.lookControl = new LookControl(this);
      this.navigation = this.createBabyNavigation(this.level());
      this.setServerStillTimeout(0);
      this.removeAllGoals((var0) -> true);
   }

   protected void ageBoundaryReached() {
      if (this.isBaby()) {
         this.babyGhastSetup();
      } else {
         this.adultGhastSetup();
      }

      super.ageBoundaryReached();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.TEMPT_RANGE, 16.0).add(Attributes.FLYING_SPEED, 0.05).add(Attributes.MOVEMENT_SPEED, 0.05).add(Attributes.FOLLOW_RANGE, 16.0).add(Attributes.CAMERA_DISTANCE, 8.0);
   }

   protected float sanitizeScale(float var1) {
      return Math.min(var1, 1.0F);
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   public boolean onClimbable() {
      return false;
   }

   public void travel(Vec3 var1) {
      float var2 = (float)this.getAttributeValue(Attributes.FLYING_SPEED) * 5.0F / 3.0F;
      this.travelFlying(var1, var2, var2, var2);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      if (!var2.isEmptyBlock(var1)) {
         return 0.0F;
      } else {
         return var2.isEmptyBlock(var1.below()) && !var2.isEmptyBlock(var1.below(2)) ? 10.0F : 5.0F;
      }
   }

   public boolean canBreatheUnderwater() {
      return this.isBaby() ? true : super.canBreatheUnderwater();
   }

   protected boolean shouldStayCloseToLeashHolder() {
      return false;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
   }

   public float getVoicePitch() {
      return 1.0F;
   }

   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   public int getAmbientSoundInterval() {
      int var1 = super.getAmbientSoundInterval();
      return this.isVehicle() ? var1 * 6 : var1;
   }

   protected SoundEvent getAmbientSound() {
      return this.isBaby() ? SoundEvents.GHASTLING_AMBIENT : SoundEvents.HAPPY_GHAST_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isBaby() ? SoundEvents.GHASTLING_HURT : SoundEvents.HAPPY_GHAST_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.GHASTLING_DEATH : SoundEvents.HAPPY_GHAST_DEATH;
   }

   protected float getSoundVolume() {
      return this.isBaby() ? 1.0F : 4.0F;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   public @Nullable AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.HAPPY_GHAST.create(var1, EntitySpawnReason.BREEDING);
   }

   public boolean canFallInLove() {
      return false;
   }

   public float getAgeScale() {
      return this.isBaby() ? 0.2375F : 1.0F;
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.HAPPY_GHAST_FOOD);
   }

   public boolean canUseSlot(EquipmentSlot var1) {
      if (var1 != EquipmentSlot.BODY) {
         return super.canUseSlot(var1);
      } else {
         return this.isAlive() && !this.isBaby();
      }
   }

   protected boolean canDispenserEquipIntoSlot(EquipmentSlot var1) {
      return var1 == EquipmentSlot.BODY;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      if (this.isBaby()) {
         return super.mobInteract(var1, var2);
      } else {
         ItemStack var3 = var1.getItemInHand(var2);
         if (!var3.isEmpty()) {
            InteractionResult var4 = var3.interactLivingEntity(var1, this, var2);
            if (var4.consumesAction()) {
               return var4;
            }
         }

         if (this.isWearingBodyArmor() && !var1.isSecondaryUseActive()) {
            this.doPlayerRide(var1);
            return InteractionResult.SUCCESS;
         } else {
            return super.mobInteract(var1, var2);
         }
      }
   }

   private void doPlayerRide(Player var1) {
      if (!this.level().isClientSide()) {
         var1.startRiding(this);
      }

   }

   protected void addPassenger(Entity var1) {
      if (!this.isVehicle()) {
         this.level().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.HARNESS_GOGGLES_DOWN, this.getSoundSource(), 1.0F, 1.0F);
      }

      super.addPassenger(var1);
      if (!this.level().isClientSide()) {
         if (!this.scanPlayerAboveGhast()) {
            this.setServerStillTimeout(0);
         } else if (this.serverStillTimeout > 10) {
            this.setServerStillTimeout(10);
         }
      }

   }

   protected void removePassenger(Entity var1) {
      super.removePassenger(var1);
      if (!this.level().isClientSide()) {
         this.setServerStillTimeout(10);
      }

      if (!this.isVehicle()) {
         this.clearHome();
         this.level().playSound((Entity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.HARNESS_GOGGLES_UP, this.getSoundSource(), 1.0F, 1.0F);
      }

   }

   protected boolean canAddPassenger(Entity var1) {
      return this.getPassengers().size() < 4;
   }

   public @Nullable LivingEntity getControllingPassenger() {
      Entity var1 = this.getFirstPassenger();
      if (this.isWearingBodyArmor() && !this.isOnStillTimeout() && var1 instanceof Player var2) {
         return var2;
      } else {
         return super.getControllingPassenger();
      }
   }

   protected Vec3 getRiddenInput(Player var1, Vec3 var2) {
      float var3 = var1.xxa;
      float var4 = 0.0F;
      float var5 = 0.0F;
      if (var1.zza != 0.0F) {
         float var6 = Mth.cos((double)(var1.getXRot() * 0.017453292F));
         float var7 = -Mth.sin((double)(var1.getXRot() * 0.017453292F));
         if (var1.zza < 0.0F) {
            var6 *= -0.5F;
            var7 *= -0.5F;
         }

         var5 = var7;
         var4 = var6;
      }

      if (var1.isJumping()) {
         var5 += 0.5F;
      }

      return (new Vec3((double)var3, (double)var5, (double)var4)).scale(3.9000000953674316 * this.getAttributeValue(Attributes.FLYING_SPEED));
   }

   protected Vec2 getRiddenRotation(LivingEntity var1) {
      return new Vec2(var1.getXRot() * 0.5F, var1.getYRot());
   }

   protected void tickRidden(Player var1, Vec3 var2) {
      super.tickRidden(var1, var2);
      Vec2 var3 = this.getRiddenRotation(var1);
      float var4 = this.getYRot();
      float var5 = Mth.wrapDegrees(var3.y - var4);
      float var6 = 0.08F;
      var4 += var5 * 0.08F;
      this.setRot(var4, var3.x);
      this.yRotO = this.yBodyRot = this.yHeadRot = var4;
   }

   protected Brain.Provider<HappyGhast> brainProvider() {
      return HappyGhastAi.brainProvider();
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return HappyGhastAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   protected void customServerAiStep(ServerLevel var1) {
      if (this.isBaby()) {
         ProfilerFiller var2 = Profiler.get();
         var2.push("happyGhastBrain");
         this.brain.tick(var1, this);
         var2.pop();
         var2.push("happyGhastActivityUpdate");
         HappyGhastAi.updateActivity(this);
         var2.pop();
      }

      this.checkRestriction();
      super.customServerAiStep(var1);
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.leashHolderTime > 0) {
            --this.leashHolderTime;
         }

         this.setLeashHolder(this.leashHolderTime > 0);
         if (this.serverStillTimeout > 0) {
            if (this.tickCount > 60) {
               --this.serverStillTimeout;
            }

            this.setServerStillTimeout(this.serverStillTimeout);
         }

         if (this.scanPlayerAboveGhast()) {
            this.setServerStillTimeout(10);
         }

      }
   }

   public void aiStep() {
      if (!this.level().isClientSide()) {
         this.setRequiresPrecisePosition(this.isOnStillTimeout());
      }

      super.aiStep();
      this.continuousHeal();
   }

   private int getHappyGhastRestrictionRadius() {
      return !this.isBaby() && this.getItemBySlot(EquipmentSlot.BODY).isEmpty() ? 64 : 32;
   }

   private void checkRestriction() {
      if (!this.isLeashed() && !this.isVehicle()) {
         int var1 = this.getHappyGhastRestrictionRadius();
         if (!this.hasHome() || !this.getHomePosition().closerThan(this.blockPosition(), (double)(var1 + 16)) || var1 != this.getHomeRadius()) {
            this.setHomeTo(this.blockPosition(), var1);
         }
      }
   }

   private void continuousHeal() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         if (this.isAlive() && this.deathTime == 0 && this.getMaxHealth() != this.getHealth()) {
            boolean var3 = this.isInClouds() || var1.precipitationAt(this.blockPosition()) != Biome.Precipitation.NONE;
            if (this.tickCount % (var3 ? 20 : 600) == 0) {
               this.heal(1.0F);
            }

            return;
         }
      }

   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(IS_LEASH_HOLDER, false);
      var1.define(STAYS_STILL, false);
   }

   private void setLeashHolder(boolean var1) {
      this.entityData.set(IS_LEASH_HOLDER, var1);
   }

   public boolean isLeashHolder() {
      return (Boolean)this.entityData.get(IS_LEASH_HOLDER);
   }

   private void syncStayStillFlag() {
      this.entityData.set(STAYS_STILL, this.serverStillTimeout > 0);
   }

   public boolean staysStill() {
      return (Boolean)this.entityData.get(STAYS_STILL);
   }

   public boolean supportQuadLeashAsHolder() {
      return true;
   }

   public Vec3[] getQuadLeashHolderOffsets() {
      return Leashable.createQuadLeashOffsets(this, -0.03125, 0.4375, 0.46875, 0.03125);
   }

   public Vec3 getLeashOffset() {
      return Vec3.ZERO;
   }

   public double leashElasticDistance() {
      return 10.0;
   }

   public double leashSnapDistance() {
      return 16.0;
   }

   public void onElasticLeashPull() {
      super.onElasticLeashPull();
      this.getMoveControl().setWait();
   }

   public void notifyLeashHolder(Leashable var1) {
      if (var1.supportQuadLeash()) {
         this.leashHolderTime = 5;
      }

   }

   public void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("still_timeout", this.serverStillTimeout);
   }

   public void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setServerStillTimeout(var1.getIntOr("still_timeout", 0));
   }

   public boolean isOnStillTimeout() {
      return this.staysStill() || this.serverStillTimeout > 0;
   }

   private boolean scanPlayerAboveGhast() {
      AABB var1 = this.getBoundingBox();
      AABB var2 = new AABB(var1.minX - 1.0, var1.maxY - 9.999999747378752E-6, var1.minZ - 1.0, var1.maxX + 1.0, var1.maxY + var1.getYsize() / 2.0, var1.maxZ + 1.0);

      for(Player var4 : this.level().players()) {
         if (!var4.isSpectator()) {
            Entity var5 = var4.getRootVehicle();
            if (!(var5 instanceof HappyGhast) && var2.contains(var5.position())) {
               return true;
            }
         }
      }

      return false;
   }

   protected BodyRotationControl createBodyControl() {
      return new HappyGhastBodyRotationControl();
   }

   public boolean canBeCollidedWith(@Nullable Entity var1) {
      if (!this.isBaby() && this.isAlive()) {
         if (this.level().isClientSide() && var1 instanceof Player && var1.position().y >= this.getBoundingBox().maxY) {
            return true;
         } else {
            return this.isVehicle() && var1 instanceof HappyGhast ? true : this.isOnStillTimeout();
         }
      } else {
         return false;
      }
   }

   public boolean isFlyingVehicle() {
      return !this.isBaby();
   }

   static {
      IS_LEASH_HOLDER = SynchedEntityData.<Boolean>defineId(HappyGhast.class, EntityDataSerializers.BOOLEAN);
      STAYS_STILL = SynchedEntityData.<Boolean>defineId(HappyGhast.class, EntityDataSerializers.BOOLEAN);
   }

   static class BabyFlyingPathNavigation extends FlyingPathNavigation {
      public BabyFlyingPathNavigation(HappyGhast var1, Level var2) {
         super(var1, var2);
         this.setCanOpenDoors(false);
         this.setCanFloat(true);
         this.setRequiredPathLength(48.0F);
      }

      protected boolean canMoveDirectly(Vec3 var1, Vec3 var2) {
         return isClearForMovementBetween(this.mob, var1, var2, false);
      }
   }

   class HappyGhastFloatGoal extends FloatGoal {
      public HappyGhastFloatGoal() {
         super(HappyGhast.this);
      }

      public boolean canUse() {
         return !HappyGhast.this.isOnStillTimeout() && super.canUse();
      }
   }

   class HappyGhastLookControl extends LookControl {
      HappyGhastLookControl() {
         super(HappyGhast.this);
      }

      public void tick() {
         if (HappyGhast.this.isOnStillTimeout()) {
            float var5 = wrapDegrees90(HappyGhast.this.getYRot());
            HappyGhast.this.setYRot(HappyGhast.this.getYRot() - var5);
            HappyGhast.this.setYHeadRot(HappyGhast.this.getYRot());
         } else if (this.lookAtCooldown > 0) {
            --this.lookAtCooldown;
            double var1 = this.wantedX - HappyGhast.this.getX();
            double var3 = this.wantedZ - HappyGhast.this.getZ();
            HappyGhast.this.setYRot(-((float)Mth.atan2(var1, var3)) * 57.295776F);
            HappyGhast.this.yBodyRot = HappyGhast.this.getYRot();
            HappyGhast.this.yHeadRot = HappyGhast.this.yBodyRot;
         } else {
            Ghast.faceMovementDirection(this.mob);
         }
      }

      public static float wrapDegrees90(float var0) {
         float var1 = var0 % 90.0F;
         if (var1 >= 45.0F) {
            var1 -= 90.0F;
         }

         if (var1 < -45.0F) {
            var1 += 90.0F;
         }

         return var1;
      }
   }

   class HappyGhastBodyRotationControl extends BodyRotationControl {
      public HappyGhastBodyRotationControl() {
         super(HappyGhast.this);
      }

      public void clientTick() {
         if (HappyGhast.this.isVehicle()) {
            HappyGhast.this.yHeadRot = HappyGhast.this.getYRot();
            HappyGhast.this.yBodyRot = HappyGhast.this.yHeadRot;
         }

         super.clientTick();
      }
   }
}
