package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Blaze extends Monster {
   private float allowedHeightOffset = 0.5F;
   private int nextHeightOffsetChangeTick;
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(Blaze.class, EntityDataSerializers.BYTE);

   public Blaze(EntityType<? extends Blaze> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.WATER, -1.0F);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.xpReward = 10;
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(4, new Blaze.BlazeAttackGoal(this));
      this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.ATTACK_DAMAGE, 6.0)
         .add(Attributes.MOVEMENT_SPEED, 0.23000000417232513)
         .add(Attributes.FOLLOW_RANGE, 48.0);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_FLAGS_ID, (byte)0);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.BLAZE_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.BLAZE_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.BLAZE_DEATH;
   }

   @Override
   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   @Override
   public void aiStep() {
      if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
      }

      if (this.level().isClientSide) {
         if (this.random.nextInt(24) == 0 && !this.isSilent()) {
            this.level()
               .playLocalSound(
                  this.getX() + 0.5,
                  this.getY() + 0.5,
                  this.getZ() + 0.5,
                  SoundEvents.BLAZE_BURN,
                  this.getSoundSource(),
                  1.0F + this.random.nextFloat(),
                  this.random.nextFloat() * 0.7F + 0.3F,
                  false
               );
         }

         for (int var1 = 0; var1 < 2; var1++) {
            this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
         }
      }

      super.aiStep();
   }

   @Override
   public boolean isSensitiveToWater() {
      return true;
   }

   @Override
   protected void customServerAiStep() {
      this.nextHeightOffsetChangeTick--;
      if (this.nextHeightOffsetChangeTick <= 0) {
         this.nextHeightOffsetChangeTick = 100;
         this.allowedHeightOffset = (float)this.random.triangle(0.5, 6.891);
      }

      LivingEntity var1 = this.getTarget();
      if (var1 != null && var1.getEyeY() > this.getEyeY() + (double)this.allowedHeightOffset && this.canAttack(var1)) {
         Vec3 var2 = this.getDeltaMovement();
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, (0.30000001192092896 - var2.y) * 0.30000001192092896, 0.0));
         this.hasImpulse = true;
      }

      super.customServerAiStep();
   }

   @Override
   public boolean isOnFire() {
      return this.isCharged();
   }

   private boolean isCharged() {
      return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
   }

   void setCharged(boolean var1) {
      byte var2 = this.entityData.get(DATA_FLAGS_ID);
      if (var1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 = (byte)(var2 & -2);
      }

      this.entityData.set(DATA_FLAGS_ID, var2);
   }

   static class BlazeAttackGoal extends Goal {
      private final Blaze blaze;
      private int attackStep;
      private int attackTime;
      private int lastSeen;

      public BlazeAttackGoal(Blaze var1) {
         super();
         this.blaze = var1;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         LivingEntity var1 = this.blaze.getTarget();
         return var1 != null && var1.isAlive() && this.blaze.canAttack(var1);
      }

      @Override
      public void start() {
         this.attackStep = 0;
      }

      @Override
      public void stop() {
         this.blaze.setCharged(false);
         this.lastSeen = 0;
      }

      @Override
      public boolean requiresUpdateEveryTick() {
         return true;
      }

      @Override
      public void tick() {
         this.attackTime--;
         LivingEntity var1 = this.blaze.getTarget();
         if (var1 != null) {
            boolean var2 = this.blaze.getSensing().hasLineOfSight(var1);
            if (var2) {
               this.lastSeen = 0;
            } else {
               this.lastSeen++;
            }

            double var3 = this.blaze.distanceToSqr(var1);
            if (var3 < 4.0) {
               if (!var2) {
                  return;
               }

               if (this.attackTime <= 0) {
                  this.attackTime = 20;
                  this.blaze.doHurtTarget(var1);
               }

               this.blaze.getMoveControl().setWantedPosition(var1.getX(), var1.getY(), var1.getZ(), 1.0);
            } else if (var3 < this.getFollowDistance() * this.getFollowDistance() && var2) {
               double var5 = var1.getX() - this.blaze.getX();
               double var7 = var1.getY(0.5) - this.blaze.getY(0.5);
               double var9 = var1.getZ() - this.blaze.getZ();
               if (this.attackTime <= 0) {
                  this.attackStep++;
                  if (this.attackStep == 1) {
                     this.attackTime = 60;
                     this.blaze.setCharged(true);
                  } else if (this.attackStep <= 4) {
                     this.attackTime = 6;
                  } else {
                     this.attackTime = 100;
                     this.attackStep = 0;
                     this.blaze.setCharged(false);
                  }

                  if (this.attackStep > 1) {
                     double var11 = Math.sqrt(Math.sqrt(var3)) * 0.5;
                     if (!this.blaze.isSilent()) {
                        this.blaze.level().levelEvent(null, 1018, this.blaze.blockPosition(), 0);
                     }

                     for (int var13 = 0; var13 < 1; var13++) {
                        Vec3 var14 = new Vec3(this.blaze.getRandom().triangle(var5, 2.297 * var11), var7, this.blaze.getRandom().triangle(var9, 2.297 * var11));
                        SmallFireball var15 = new SmallFireball(this.blaze.level(), this.blaze, var14.normalize());
                        var15.setPos(var15.getX(), this.blaze.getY(0.5) + 0.5, var15.getZ());
                        this.blaze.level().addFreshEntity(var15);
                     }
                  }
               }

               this.blaze.getLookControl().setLookAt(var1, 10.0F, 10.0F);
            } else if (this.lastSeen < 5) {
               this.blaze.getMoveControl().setWantedPosition(var1.getX(), var1.getY(), var1.getZ(), 1.0);
            }

            super.tick();
         }
      }

      private double getFollowDistance() {
         return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
      }
   }
}
