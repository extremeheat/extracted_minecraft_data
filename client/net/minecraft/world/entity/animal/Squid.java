package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class Squid extends WaterAnimal {
   public float xBodyRot;
   public float xBodyRotO;
   public float zBodyRot;
   public float zBodyRotO;
   public float tentacleMovement;
   public float oldTentacleMovement;
   public float tentacleAngle;
   public float oldTentacleAngle;
   private float speed;
   private float tentacleSpeed;
   private float rotateSpeed;
   // $FF: renamed from: tx float
   private float field_22;
   // $FF: renamed from: ty float
   private float field_23;
   // $FF: renamed from: tz float
   private float field_24;

   public Squid(EntityType<? extends Squid> var1, Level var2) {
      super(var1, var2);
      this.random.setSeed((long)this.getId());
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Squid.SquidRandomMovementGoal(this));
      this.goalSelector.addGoal(1, new Squid.SquidFleeGoal());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D);
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return var2.height * 0.5F;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SQUID_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SQUID_DEATH;
   }

   protected SoundEvent getSquirtSound() {
      return SoundEvents.SQUID_SQUIRT;
   }

   public boolean canBeLeashed(Player var1) {
      return !this.isLeashed();
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   public void aiStep() {
      super.aiStep();
      this.xBodyRotO = this.xBodyRot;
      this.zBodyRotO = this.zBodyRot;
      this.oldTentacleMovement = this.tentacleMovement;
      this.oldTentacleAngle = this.tentacleAngle;
      this.tentacleMovement += this.tentacleSpeed;
      if ((double)this.tentacleMovement > 6.283185307179586D) {
         if (this.level.isClientSide) {
            this.tentacleMovement = 6.2831855F;
         } else {
            this.tentacleMovement = (float)((double)this.tentacleMovement - 6.283185307179586D);
            if (this.random.nextInt(10) == 0) {
               this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.level.broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.isInWaterOrBubble()) {
         if (this.tentacleMovement < 3.1415927F) {
            float var1 = this.tentacleMovement / 3.1415927F;
            this.tentacleAngle = Mth.sin(var1 * var1 * 3.1415927F) * 3.1415927F * 0.25F;
            if ((double)var1 > 0.75D) {
               this.speed = 1.0F;
               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            this.speed *= 0.9F;
            this.rotateSpeed *= 0.99F;
         }

         if (!this.level.isClientSide) {
            this.setDeltaMovement((double)(this.field_22 * this.speed), (double)(this.field_23 * this.speed), (double)(this.field_24 * this.speed));
         }

         Vec3 var4 = this.getDeltaMovement();
         double var2 = var4.horizontalDistance();
         this.yBodyRot += (-((float)Mth.atan2(var4.field_414, var4.field_416)) * 57.295776F - this.yBodyRot) * 0.1F;
         this.setYRot(this.yBodyRot);
         this.zBodyRot = (float)((double)this.zBodyRot + 3.141592653589793D * (double)this.rotateSpeed * 1.5D);
         this.xBodyRot += (-((float)Mth.atan2(var2, var4.field_415)) * 57.295776F - this.xBodyRot) * 0.1F;
      } else {
         this.tentacleAngle = Mth.abs(Mth.sin(this.tentacleMovement)) * 3.1415927F * 0.25F;
         if (!this.level.isClientSide) {
            double var5 = this.getDeltaMovement().field_415;
            if (this.hasEffect(MobEffects.LEVITATION)) {
               var5 = 0.05D * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
            } else if (!this.isNoGravity()) {
               var5 -= 0.08D;
            }

            this.setDeltaMovement(0.0D, var5 * 0.9800000190734863D, 0.0D);
         }

         this.xBodyRot = (float)((double)this.xBodyRot + (double)(-90.0F - this.xBodyRot) * 0.02D);
      }

   }

   public boolean hurt(DamageSource var1, float var2) {
      if (super.hurt(var1, var2) && this.getLastHurtByMob() != null) {
         this.spawnInk();
         return true;
      } else {
         return false;
      }
   }

   private Vec3 rotateVector(Vec3 var1) {
      Vec3 var2 = var1.xRot(this.xBodyRotO * 0.017453292F);
      var2 = var2.yRot(-this.yBodyRotO * 0.017453292F);
      return var2;
   }

   private void spawnInk() {
      this.playSound(this.getSquirtSound(), this.getSoundVolume(), this.getVoicePitch());
      Vec3 var1 = this.rotateVector(new Vec3(0.0D, -1.0D, 0.0D)).add(this.getX(), this.getY(), this.getZ());

      for(int var2 = 0; var2 < 30; ++var2) {
         Vec3 var3 = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6D - 0.3D, -1.0D, (double)this.random.nextFloat() * 0.6D - 0.3D));
         Vec3 var4 = var3.scale(0.3D + (double)(this.random.nextFloat() * 2.0F));
         ((ServerLevel)this.level).sendParticles(this.getInkParticle(), var1.field_414, var1.field_415 + 0.5D, var1.field_416, 0, var4.field_414, var4.field_415, var4.field_416, 0.10000000149011612D);
      }

   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.SQUID_INK;
   }

   public void travel(Vec3 var1) {
      this.move(MoverType.SELF, this.getDeltaMovement());
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 19) {
         this.tentacleMovement = 0.0F;
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public void setMovementVector(float var1, float var2, float var3) {
      this.field_22 = var1;
      this.field_23 = var2;
      this.field_24 = var3;
   }

   public boolean hasMovementVector() {
      return this.field_22 != 0.0F || this.field_23 != 0.0F || this.field_24 != 0.0F;
   }

   private class SquidRandomMovementGoal extends Goal {
      private final Squid squid;

      public SquidRandomMovementGoal(Squid var2) {
         super();
         this.squid = var2;
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         int var1 = this.squid.getNoActionTime();
         if (var1 > 100) {
            this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
         } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
            float var2 = this.squid.getRandom().nextFloat() * 6.2831855F;
            float var3 = Mth.cos(var2) * 0.2F;
            float var4 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
            float var5 = Mth.sin(var2) * 0.2F;
            this.squid.setMovementVector(var3, var4, var5);
         }

      }
   }

   private class SquidFleeGoal extends Goal {
      private static final float SQUID_FLEE_SPEED = 3.0F;
      private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
      private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;
      private int fleeTicks;

      SquidFleeGoal() {
         super();
      }

      public boolean canUse() {
         LivingEntity var1 = Squid.this.getLastHurtByMob();
         if (Squid.this.isInWater() && var1 != null) {
            return Squid.this.distanceToSqr(var1) < 100.0D;
         } else {
            return false;
         }
      }

      public void start() {
         this.fleeTicks = 0;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         ++this.fleeTicks;
         LivingEntity var1 = Squid.this.getLastHurtByMob();
         if (var1 != null) {
            Vec3 var2 = new Vec3(Squid.this.getX() - var1.getX(), Squid.this.getY() - var1.getY(), Squid.this.getZ() - var1.getZ());
            BlockState var3 = Squid.this.level.getBlockState(new BlockPos(Squid.this.getX() + var2.field_414, Squid.this.getY() + var2.field_415, Squid.this.getZ() + var2.field_416));
            FluidState var4 = Squid.this.level.getFluidState(new BlockPos(Squid.this.getX() + var2.field_414, Squid.this.getY() + var2.field_415, Squid.this.getZ() + var2.field_416));
            if (var4.method_56(FluidTags.WATER) || var3.isAir()) {
               double var5 = var2.length();
               if (var5 > 0.0D) {
                  var2.normalize();
                  float var7 = 3.0F;
                  if (var5 > 5.0D) {
                     var7 = (float)((double)var7 - (var5 - 5.0D) / 5.0D);
                  }

                  if (var7 > 0.0F) {
                     var2 = var2.scale((double)var7);
                  }
               }

               if (var3.isAir()) {
                  var2 = var2.subtract(0.0D, var2.field_415, 0.0D);
               }

               Squid.this.setMovementVector((float)var2.field_414 / 20.0F, (float)var2.field_415 / 20.0F, (float)var2.field_416 / 20.0F);
            }

            if (this.fleeTicks % 10 == 5) {
               Squid.this.level.addParticle(ParticleTypes.BUBBLE, Squid.this.getX(), Squid.this.getY(), Squid.this.getZ(), 0.0D, 0.0D, 0.0D);
            }

         }
      }
   }
}
