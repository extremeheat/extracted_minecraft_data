package net.minecraft.world.entity.pets;

import com.google.common.annotations.VisibleForTesting;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class PetSlime extends AbstractPet {
   private static final EntityDataAccessor<Integer> ID_SIZE;
   public static final int MIN_SIZE = 1;
   public static final int MAX_SIZE = 127;
   public static final int MAX_NATURAL_SIZE = 4;
   private static final boolean DEFAULT_WAS_ON_GROUND = false;
   public float targetSquish;
   public float squish;
   public float oSquish;
   private boolean wasOnGround = false;

   public PetSlime(EntityType<? extends PetSlime> var1, Level var2) {
      super(var1, var2);
      this.fixupDimensions();
      this.moveControl = new SlimeMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new SlimeFloatGoal(this));
      this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(7, new SlimeKeepOnJumpingGoal(this));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(ID_SIZE, 1);
   }

   @VisibleForTesting
   public void setSize(int var1, boolean var2) {
      boolean var3 = true;
      this.entityData.set(ID_SIZE, 1);
      this.reapplyPosition();
      this.refreshDimensions();
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10.0);
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896);
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(1.0);
      if (var2) {
         this.setHealth(this.getMaxHealth());
      }

      this.xpReward = 1;
   }

   public int getSize() {
      return (Integer)this.entityData.get(ID_SIZE);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Size", this.getSize() - 1);
      var1.putBoolean("wasOnGround", this.wasOnGround);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      this.setSize(var1.getIntOr("Size", 0) + 1, false);
      super.readAdditionalSaveData(var1);
      this.wasOnGround = var1.getBooleanOr("wasOnGround", false);
   }

   public boolean isTiny() {
      return this.getSize() <= 1;
   }

   protected ParticleOptions getParticleType() {
      return ParticleTypes.ITEM_SLIME;
   }

   protected boolean shouldDespawnInPeaceful() {
      return this.getSize() > 0;
   }

   public void tick() {
      this.oSquish = this.squish;
      this.squish += (this.targetSquish - this.squish) * 0.5F;
      super.tick();
      if (this.onGround() && !this.wasOnGround) {
         float var1 = this.getDimensions(this.getPose()).width() * 2.0F;
         float var2 = var1 / 2.0F;

         for(int var3 = 0; (float)var3 < var1 * 16.0F; ++var3) {
            float var4 = this.random.nextFloat() * 6.2831855F;
            float var5 = this.random.nextFloat() * 0.5F + 0.5F;
            float var6 = Mth.sin(var4) * var2 * var5;
            float var7 = Mth.cos(var4) * var2 * var5;
            this.level().addParticle(this.getParticleType(), this.getX() + (double)var6, this.getY(), this.getZ() + (double)var7, 0.0, 0.0, 0.0);
         }

         this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         this.targetSquish = -0.5F;
      } else if (!this.onGround() && this.wasOnGround) {
         this.targetSquish = 1.0F;
      }

      this.wasOnGround = this.onGround();
      this.decreaseSquish();
   }

   protected void decreaseSquish() {
      this.targetSquish *= 0.6F;
   }

   protected int getJumpDelay() {
      return this.random.nextInt(20) + 10;
   }

   public void refreshDimensions() {
      double var1 = this.getX();
      double var3 = this.getY();
      double var5 = this.getZ();
      super.refreshDimensions();
      this.setPos(var1, var3, var5);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (ID_SIZE.equals(var1)) {
         this.refreshDimensions();
         this.setYRot(this.yHeadRot);
         this.yBodyRot = this.yHeadRot;
         if (this.isInWater() && this.random.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   public EntityType<? extends PetSlime> getType() {
      return super.getType();
   }

   protected Vec3 getPassengerAttachmentPoint(Entity var1, EntityDimensions var2, float var3) {
      return new Vec3(0.0, (double)var2.height() - 0.015625 * (double)this.getSize() * (double)var3, 0.0);
   }

   protected SoundEvent getSquishSound() {
      return SoundEvents.SLIME_SQUISH_SMALL;
   }

   public static boolean checkSlimeSpawnRules(EntityType<PetSlime> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return true;
   }

   protected float getSoundVolume() {
      return 0.4F * (float)this.getSize();
   }

   public int getMaxHeadXRot() {
      return 0;
   }

   protected boolean doPlayJumpSound() {
      return this.getSize() > 0;
   }

   public void jumpFromGround() {
      Vec3 var1 = this.getDeltaMovement();
      this.setDeltaMovement(var1.x, (double)this.getJumpPower(), var1.z);
      this.hasImpulse = true;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      int var6 = var5.nextInt(3);
      if (var6 < 2 && var5.nextFloat() < 0.5F * var2.getSpecialMultiplier()) {
         ++var6;
      }

      int var7 = 1 << var6;
      this.setSize(var7, true);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   float getSoundPitch() {
      float var1 = this.isTiny() ? 1.4F : 0.8F;
      return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * var1;
   }

   protected SoundEvent getJumpSound() {
      return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return super.getDefaultDimensions(var1).scale((float)this.getSize());
   }

   static {
      ID_SIZE = SynchedEntityData.<Integer>defineId(PetSlime.class, EntityDataSerializers.INT);
   }

   static class SlimeMoveControl extends MoveControl {
      private float yRot;
      private int jumpDelay;
      private final PetSlime slime;
      private boolean isAggressive;

      public SlimeMoveControl(PetSlime var1) {
         super(var1);
         this.slime = var1;
         this.yRot = 180.0F * var1.getYRot() / 3.1415927F;
      }

      public void setDirection(float var1, boolean var2) {
         this.yRot = var1;
         this.isAggressive = var2;
      }

      public void setWantedMovement(double var1) {
         this.speedModifier = var1;
         this.operation = MoveControl.Operation.MOVE_TO;
      }

      public void tick() {
         this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
         this.mob.yHeadRot = this.mob.getYRot();
         this.mob.yBodyRot = this.mob.getYRot();
         if (this.operation != MoveControl.Operation.MOVE_TO) {
            this.mob.setZza(0.0F);
         } else {
            this.operation = MoveControl.Operation.WAIT;
            if (this.mob.onGround()) {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = this.slime.getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  this.slime.getJumpControl().jump();
                  if (this.slime.doPlayJumpSound()) {
                     this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                  }
               } else {
                  this.slime.xxa = 0.0F;
                  this.slime.zza = 0.0F;
                  this.mob.setSpeed(0.0F);
               }
            } else {
               this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }

         }
      }
   }

   static class SlimeFloatGoal extends Goal {
      private final PetSlime slime;

      public SlimeFloatGoal(PetSlime var1) {
         super();
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
         var1.getNavigation().setCanFloat(true);
      }

      public boolean canUse() {
         return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeMoveControl;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (this.slime.getRandom().nextFloat() < 0.8F) {
            this.slime.getJumpControl().jump();
         }

         MoveControl var2 = this.slime.getMoveControl();
         if (var2 instanceof SlimeMoveControl var1) {
            var1.setWantedMovement(1.2);
         }

      }
   }

   static class SlimeKeepOnJumpingGoal extends Goal {
      private final PetSlime slime;

      public SlimeKeepOnJumpingGoal(PetSlime var1) {
         super();
         this.slime = var1;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !this.slime.isPassenger();
      }

      public void tick() {
         MoveControl var2 = this.slime.getMoveControl();
         if (var2 instanceof SlimeMoveControl var1) {
            var1.setWantedMovement(1.0);
         }

      }
   }
}
