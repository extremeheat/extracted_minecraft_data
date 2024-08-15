package net.minecraft.world.entity.animal;

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
   private float tx;
   private float ty;
   private float tz;

   public Squid(EntityType<? extends Squid> var1, Level var2) {
      super(var1, var2);
      this.random.setSeed((long)this.getId());
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(0, new Squid.SquidRandomMovementGoal(this));
      this.goalSelector.addGoal(1, new Squid.SquidFleeGoal());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.SQUID_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SQUID_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.SQUID_DEATH;
   }

   protected SoundEvent getSquirtSound() {
      return SoundEvents.SQUID_SQUIRT;
   }

   @Override
   public boolean canBeLeashed() {
      return true;
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   @Nullable
   @Override
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return EntityType.SQUID.create(var1, EntitySpawnReason.BREEDING);
   }

   @Override
   protected double getDefaultGravity() {
      return 0.08;
   }

   @Override
   public void aiStep() {
      super.aiStep();
      this.xBodyRotO = this.xBodyRot;
      this.zBodyRotO = this.zBodyRot;
      this.oldTentacleMovement = this.tentacleMovement;
      this.oldTentacleAngle = this.tentacleAngle;
      this.tentacleMovement = this.tentacleMovement + this.tentacleSpeed;
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

         if (!this.level().isClientSide) {
            this.setDeltaMovement((double)(this.tx * this.speed), (double)(this.ty * this.speed), (double)(this.tz * this.speed));
         }

         Vec3 var4 = this.getDeltaMovement();
         double var2 = var4.horizontalDistance();
         this.yBodyRot = this.yBodyRot + (-((float)Mth.atan2(var4.x, var4.z)) * 57.295776F - this.yBodyRot) * 0.1F;
         this.setYRot(this.yBodyRot);
         this.zBodyRot = this.zBodyRot + 3.1415927F * this.rotateSpeed * 1.5F;
         this.xBodyRot = this.xBodyRot + (-((float)Mth.atan2(var2, var4.y)) * 57.295776F - this.xBodyRot) * 0.1F;
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

         this.xBodyRot = this.xBodyRot + (-90.0F - this.xBodyRot) * 0.02F;
      }
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (super.hurt(var1, var2) && this.getLastHurtByMob() != null) {
         if (!this.level().isClientSide) {
            this.spawnInk();
         }

         return true;
      } else {
         return false;
      }
   }

   private Vec3 rotateVector(Vec3 var1) {
      Vec3 var2 = var1.xRot(this.xBodyRotO * 0.017453292F);
      return var2.yRot(-this.yBodyRotO * 0.017453292F);
   }

   private void spawnInk() {
      this.makeSound(this.getSquirtSound());
      Vec3 var1 = this.rotateVector(new Vec3(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());

      for (int var2 = 0; var2 < 30; var2++) {
         Vec3 var3 = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
         Vec3 var4 = var3.scale(0.3 + (double)(this.random.nextFloat() * 2.0F));
         ((ServerLevel)this.level()).sendParticles(this.getInkParticle(), var1.x, var1.y + 0.5, var1.z, 0, var4.x, var4.y, var4.z, 0.10000000149011612);
      }
   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.SQUID_INK;
   }

   @Override
   public void travel(Vec3 var1) {
      if (this.isControlledByLocalInstance()) {
         this.move(MoverType.SELF, this.getDeltaMovement());
      }
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 19) {
         this.tentacleMovement = 0.0F;
      } else {
         super.handleEntityEvent(var1);
      }
   }

   public void setMovementVector(float var1, float var2, float var3) {
      this.tx = var1;
      this.ty = var2;
      this.tz = var3;
   }

   public boolean hasMovementVector() {
      return this.tx != 0.0F || this.ty != 0.0F || this.tz != 0.0F;
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      if (this.random.nextFloat() > 0.95F) {
         this.setBaby(true);
      }

      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   class SquidFleeGoal extends Goal {
      private static final float SQUID_FLEE_SPEED = 3.0F;
      private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
      private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;
      private int fleeTicks;

      SquidFleeGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         LivingEntity var1 = Squid.this.getLastHurtByMob();
         return Squid.this.isInWater() && var1 != null ? Squid.this.distanceToSqr(var1) < 100.0 : false;
      }

      @Override
      public void start() {
         this.fleeTicks = 0;
      }

      @Override
      public boolean requiresUpdateEveryTick() {
         return true;
      }

      @Override
      public void tick() {
         this.fleeTicks++;
         LivingEntity var1 = Squid.this.getLastHurtByMob();
         if (var1 != null) {
            Vec3 var2 = new Vec3(Squid.this.getX() - var1.getX(), Squid.this.getY() - var1.getY(), Squid.this.getZ() - var1.getZ());
            BlockState var3 = Squid.this.level()
               .getBlockState(BlockPos.containing(Squid.this.getX() + var2.x, Squid.this.getY() + var2.y, Squid.this.getZ() + var2.z));
            FluidState var4 = Squid.this.level()
               .getFluidState(BlockPos.containing(Squid.this.getX() + var2.x, Squid.this.getY() + var2.y, Squid.this.getZ() + var2.z));
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

               Squid.this.setMovementVector((float)var2.x / 20.0F, (float)var2.y / 20.0F, (float)var2.z / 20.0F);
            }

            if (this.fleeTicks % 10 == 5) {
               Squid.this.level().addParticle(ParticleTypes.BUBBLE, Squid.this.getX(), Squid.this.getY(), Squid.this.getZ(), 0.0, 0.0, 0.0);
            }
         }
      }
   }

   class SquidRandomMovementGoal extends Goal {
      private final Squid squid;

      public SquidRandomMovementGoal(final Squid nullx) {
         super();
         this.squid = nullx;
      }

      @Override
      public boolean canUse() {
         return true;
      }

      @Override
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
}
