package net.minecraft.world.entity.animal.squid;

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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AgeableWaterCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

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
   private Vec3 movementVector;
   private static final EntityDimensions BABY_DIMENSIONS = EntityDimensions.scalable(0.5F, 0.5F).withEyeHeight(0.37F);

   public Squid(final EntityType<? extends Squid> type, final Level level) {
      super(type, level);
      this.movementVector = Vec3.ZERO;
      this.random.setSeed((long)this.getId());
      this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new SquidRandomMovementGoal(this));
      this.goalSelector.addGoal(1, new SquidFleeGoal());
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SQUID_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
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

   public @Nullable AgeableMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      return EntityTypes.SQUID.create(level, EntitySpawnReason.BREEDING);
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
         if (this.level().isClientSide()) {
            this.tentacleMovement = 6.2831855F;
         } else {
            this.tentacleMovement -= 6.2831855F;
            if (this.random.nextInt(10) == 0) {
               this.tentacleSpeed = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }

            this.level().broadcastEntityEvent(this, (byte)19);
         }
      }

      if (this.isInWater()) {
         if (this.tentacleMovement < 3.1415927F) {
            float tentacleScale = this.tentacleMovement / 3.1415927F;
            this.tentacleAngle = Mth.sin((double)(tentacleScale * tentacleScale * 3.1415927F)) * 3.1415927F * 0.25F;
            if ((double)tentacleScale > 0.75) {
               if (this.isLocalInstanceAuthoritative()) {
                  this.setDeltaMovement(this.movementVector);
               }

               this.rotateSpeed = 1.0F;
            } else {
               this.rotateSpeed *= 0.8F;
            }
         } else {
            this.tentacleAngle = 0.0F;
            if (this.isLocalInstanceAuthoritative()) {
               this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            }

            this.rotateSpeed *= 0.99F;
         }

         Vec3 movement = this.getDeltaMovement();
         double horizontalMovement = movement.horizontalDistance();
         this.yBodyRot += (-((float)Mth.atan2(movement.x, movement.z)) * 57.295776F - this.yBodyRot) * 0.1F;
         this.setYRot(this.yBodyRot);
         this.zBodyRot += 3.1415927F * this.rotateSpeed * 1.5F;
         this.xBodyRot += (-((float)Mth.atan2(horizontalMovement, movement.y)) * 57.295776F - this.xBodyRot) * 0.1F;
      } else {
         this.tentacleAngle = Mth.abs(Mth.sin((double)this.tentacleMovement)) * 3.1415927F * 0.25F;
         if (!this.level().isClientSide()) {
            double yd = this.getDeltaMovement().y;
            if (this.hasEffect(MobEffects.LEVITATION)) {
               yd = 0.05 * (double)(this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1);
            } else {
               yd -= this.getGravity();
            }

            this.setDeltaMovement(0.0, yd * (double)this.getAirDrag(), 0.0);
         }

         this.xBodyRot += (-90.0F - this.xBodyRot) * 0.02F;
      }

   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      if (super.hurtServer(level, source, damage) && this.getLastHurtByMob() != null) {
         this.spawnInk();
         return true;
      } else {
         return false;
      }
   }

   private Vec3 rotateVector(final Vec3 vec) {
      Vec3 v = vec.xRot(this.xBodyRotO * 0.017453292F);
      v = v.yRot(-this.yBodyRotO * 0.017453292F);
      return v;
   }

   private void spawnInk() {
      this.makeSound(this.getSquirtSound());
      Vec3 pos = this.rotateVector(new Vec3(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());

      for(int i = 0; i < 30; ++i) {
         Vec3 dir = this.rotateVector(new Vec3((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
         float inkPosOffsetScale = this.isBaby() ? 0.1F : 0.3F;
         Vec3 dirOffset = dir.scale((double)(inkPosOffsetScale + this.random.nextFloat() * 2.0F));
         ((ServerLevel)this.level()).sendParticles(this.getInkParticle(), pos.x, pos.y + 0.5, pos.z, 0, dirOffset.x, dirOffset.y, dirOffset.z, 0.10000000149011612);
      }

   }

   protected ParticleOptions getInkParticle() {
      return ParticleTypes.SQUID_INK;
   }

   public void travel(final Vec3 input) {
      this.move(MoverType.SELF, this.getDeltaMovement());
   }

   public void handleEntityEvent(final byte id) {
      if (id == 19) {
         this.tentacleMovement = 0.0F;
      } else {
         super.handleEntityEvent(id);
      }

   }

   public boolean hasMovementVector() {
      return this.movementVector.lengthSqr() > 9.999999747378752E-6;
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      SpawnGroupData spawnGroupData = (SpawnGroupData)Objects.requireNonNullElseGet(groupData, () -> new AgeableMob.AgeableMobGroupData(0.05F));
      return super.finalizeSpawn(level, difficulty, spawnReason, spawnGroupData);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
   }

   private static class SquidRandomMovementGoal extends Goal {
      private final Squid squid;

      public SquidRandomMovementGoal(final Squid squid) {
         super();
         this.squid = squid;
      }

      public boolean canUse() {
         return true;
      }

      public void tick() {
         int noActionTime = this.squid.getNoActionTime();
         if (noActionTime > 100) {
            this.squid.movementVector = Vec3.ZERO;
         } else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
            float angle = this.squid.getRandom().nextFloat() * 6.2831855F;
            this.squid.movementVector = new Vec3((double)(Mth.cos((double)angle) * 0.2F), (double)(-0.1F + this.squid.getRandom().nextFloat() * 0.2F), (double)(Mth.sin((double)angle) * 0.2F));
         }

      }
   }

   private class SquidFleeGoal extends Goal {
      private static final float SQUID_FLEE_SPEED = 3.0F;
      private static final float SQUID_FLEE_MIN_DISTANCE = 5.0F;
      private static final float SQUID_FLEE_MAX_DISTANCE = 10.0F;
      private int fleeTicks;

      private SquidFleeGoal() {
         Objects.requireNonNull(Squid.this);
         super();
      }

      public boolean canUse() {
         LivingEntity entity = Squid.this.getLastHurtByMob();
         if (Squid.this.isInWater() && entity != null) {
            return Squid.this.distanceToSqr(entity) < 100.0;
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
         LivingEntity lastHurtByMob = Squid.this.getLastHurtByMob();
         if (lastHurtByMob != null) {
            Vec3 fleeTo = new Vec3(Squid.this.getX() - lastHurtByMob.getX(), Squid.this.getY() - lastHurtByMob.getY(), Squid.this.getZ() - lastHurtByMob.getZ());
            BlockState blockState = Squid.this.level().getBlockState(BlockPos.containing(Squid.this.getX() + fleeTo.x, Squid.this.getY() + fleeTo.y, Squid.this.getZ() + fleeTo.z));
            FluidState fluidState = Squid.this.level().getFluidState(BlockPos.containing(Squid.this.getX() + fleeTo.x, Squid.this.getY() + fleeTo.y, Squid.this.getZ() + fleeTo.z));
            if (fluidState.is(FluidTags.WATER) || blockState.isAir()) {
               double length = fleeTo.length();
               if (length > 0.0) {
                  fleeTo.normalize();
                  double avoidSpeed = 3.0;
                  if (length > 5.0) {
                     avoidSpeed -= (length - 5.0) / 5.0;
                  }

                  if (avoidSpeed > 0.0) {
                     fleeTo = fleeTo.scale(avoidSpeed);
                  }
               }

               if (blockState.isAir()) {
                  fleeTo = fleeTo.subtract(0.0, fleeTo.y, 0.0);
               }

               Squid.this.movementVector = new Vec3(fleeTo.x / 20.0, fleeTo.y / 20.0, fleeTo.z / 20.0);
            }

            if (this.fleeTicks % 10 == 5) {
               Squid.this.level().addParticle(ParticleTypes.BUBBLE, Squid.this.getX(), Squid.this.getY(), Squid.this.getZ(), 0.0, 0.0, 0.0);
            }

         }
      }
   }
}
