package net.minecraft.world.entity.animal;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Squid extends AgeableWaterCreature {
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
   Vec3 movementVector;

   public Squid(EntityType<? extends Squid> var1, Level var2) {
      super(var1, var2);
      this.movementVector = Vec3.ZERO;
      this.random.setSeed((long)this.getId());
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SquidRandomMovementGoal(this, this));
      this.goalSelector.addGoal(1, new SquidFleeGoal());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0);
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

   public boolean canBeLeashed() {
      return true;
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   public @Nullable AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return (AgeableMob)EntityType.SQUID.create(var1, EntitySpawnReason.BREEDING);
   }

   protected double getDefaultGravity() {
      return 0.08;
   }

   public void aiStep() {
      super.aiStep();
      this.xBodyRotO = this.xBodyRot;
      this.zBodyRotO = this.zBodyRot;
      this.oldTentacleMovement = this.tentacleMovement;
      this.oldTentacleAngle = this.tentacleAngle;
      this.tentacleMovement += this.tentacleSpeed;
      if ((double)this.tentacleMovement > 6.283185307179586) {
         if (this.level().isClientSide) {
            this.tentacleMovement = 6.2831855F;
         } else {
            this.tentacleMovement -= 6.2831855F;
            if (this.random.nextInt(10) == 0) {
               this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.level().broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.isInWaterOrBubble()) {
         if (this.tentacleMovement < 3.1415927F) {
            float var1 = this.tentacleMovement / 3.1415927F;
            this.tentacleAngle = Mth.sin(var1 * var1 * 3.1415927F) * 3.1415927F * 0.25F;
            if ((double)var1 > 0.75) {
               if (this.isControlledByLocalInstance()) {
                  this.setDeltaMovement(this.movementVector);
               }

               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            if (this.isControlledByLocalInstance()) {
               this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            }

            this.rotateSpeed *= 0.99F;
         }

         Vec3 var4 = this.getDeltaMovement();
         double var2 = var4.horizontalDistance();
         this.yBodyRot += (-((float)Mth.atan2(var4.x, var4.z)) * 57.295776F - this.yBodyRot) * 0.1F;
         this.setYRot(this.yBodyRot);
         this.zBodyRot += 3.1415927F * this.rotateSpeed * 1.5F;
         this.xBodyRot += (-((float)Mth.atan2(var2, var4.y)) * 57.295776F - this.xBodyRot) * 0.1F;
      } else {
         this.tentacleAngle = Mth.abs(Mth.sin(this.tentacleMovement)) * 3.1415927F * 0.25F;
         if (!this.level().isClientSide) {
            double var5 = this.getDeltaMovement().y;
            if (this.hasEffect(MobEffects.LEVITATION)) {
               var5 = 0.05 * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
            } else {
               var5 -= this.getGravity();
            }

            this.setDeltaMovement(0.0, var5 * 0.9800000190734863, 0.0);
         }

         this.xBodyRot += (-90.0F - this.xBodyRot) * 0.02F;
      }

   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (super.hurtServer(var1, var2, var3) && this.getLastHurtByMob() != null) {
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
      this.makeSound(this.getSquirtSound());
      Vec3 var1 = this.rotateVector(new Vec3(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());

      for(int var2 = 0; var2 < 30; ++var2) {
         Vec3 var3 = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
         float var4 = this.isBaby() ? 0.1F : 0.3F;
         Vec3 var5 = var3.scale((double)(var4 + this.random.nextFloat() * 2.0F));
         ((ServerLevel)this.level()).sendParticles(this.getInkParticle(), var1.x, var1.y + 0.5, var1.z, 0, var5.x, var5.y, var5.z, 0.10000000149011612);
      }

   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.SQUID_INK;
   }

   public void travel(Vec3 var1) {
      if (this.isControlledByLocalInstance()) {
         this.move(MoverType.SELF, this.getDeltaMovement());
      }

   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 19) {
         this.tentacleMovement = 0.0F;
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public boolean hasMovementVector() {
      return this.movementVector.lengthSqr() > 9.999999747378752E-6;
   }

   public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      SpawnGroupData var5 = (SpawnGroupData)Objects.requireNonNullElseGet(var4, () -> {
         return new AgeableMob.AgeableMobGroupData(0.05F);
      });
      return super.finalizeSpawn(var1, var2, var3, var5);
   }

   private class SquidRandomMovementGoal extends Goal {
      private final Squid squid;

      public SquidRandomMovementGoal(final Squid var1, final Squid var2) {
         super();
         this.squid = var2;
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         int var1 = this.squid.getNoActionTime();
         if (var1 > 100) {
            this.squid.movementVector = Vec3.ZERO;
         } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
            float var2 = this.squid.getRandom().nextFloat() * 6.2831855F;
            this.squid.movementVector = new Vec3((double)(Mth.cos(var2) * 0.2F), (double)(-0.1F + this.squid.getRandom().nextFloat() * 0.2F), (double)(Mth.sin(var2) * 0.2F));
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
            return Squid.this.distanceToSqr(var1) < 100.0;
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
            BlockState var3 = Squid.this.level().getBlockState(BlockPos.containing(Squid.this.getX() + var2.x, Squid.this.getY() + var2.y, Squid.this.getZ() + var2.z));
            FluidState var4 = Squid.this.level().getFluidState(BlockPos.containing(Squid.this.getX() + var2.x, Squid.this.getY() + var2.y, Squid.this.getZ() + var2.z));
            if (var4.is(FluidTags.WATER) || var3.isAir()) {
               double var5 = var2.length();
               if (var5 > 0.0) {
                  var2.normalize();
                  double var7 = 3.0;
                  if (var5 > 5.0) {
                     var7 -= (var5 - 5.0) / 5.0;
                  }

                  if (var7 > 0.0) {
                     var2 = var2.scale(var7);
                  }
               }

               if (var3.isAir()) {
                  var2 = var2.subtract(0.0, var2.y, 0.0);
               }

               Squid.this.movementVector = new Vec3(var2.x / 20.0, var2.y / 20.0, var2.z / 20.0);
            }

            if (this.fleeTicks % 10 == 5) {
               Squid.this.level().addParticle(ParticleTypes.BUBBLE, Squid.this.getX(), Squid.this.getY(), Squid.this.getZ(), 0.0, 0.0, 0.0);
            }

         }
      }
   }
}
