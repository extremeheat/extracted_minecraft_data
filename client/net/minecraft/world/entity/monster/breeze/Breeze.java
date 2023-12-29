package net.minecraft.world.entity.monster.breeze;

import com.mojang.serialization.Dynamic;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class Breeze extends Monster {
   private static final int SLIDE_PARTICLES_AMOUNT = 20;
   private static final int IDLE_PARTICLES_AMOUNT = 1;
   private static final int JUMP_DUST_PARTICLES_AMOUNT = 20;
   private static final int JUMP_TRAIL_PARTICLES_AMOUNT = 3;
   private static final int JUMP_TRAIL_DURATION_TICKS = 5;
   private static final int JUMP_CIRCLE_DISTANCE_Y = 10;
   private static final float FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD = 3.0F;
   public AnimationState idle = new AnimationState();
   public AnimationState slide = new AnimationState();
   public AnimationState longJump = new AnimationState();
   public AnimationState shoot = new AnimationState();
   public AnimationState inhale = new AnimationState();
   private int jumpTrailStartedTick = 0;

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes()
         .add(Attributes.MOVEMENT_SPEED, 0.6000000238418579)
         .add(Attributes.MAX_HEALTH, 30.0)
         .add(Attributes.FOLLOW_RANGE, 24.0)
         .add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   public Breeze(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(BlockPathTypes.DANGER_TRAPDOOR, -1.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return BreezeAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   public Brain<Breeze> getBrain() {
      return super.getBrain();
   }

   @Override
   protected Brain.Provider<Breeze> brainProvider() {
      return Brain.provider(BreezeAi.MEMORY_TYPES, BreezeAi.SENSOR_TYPES);
   }

   @Override
   public boolean canAttack(LivingEntity var1) {
      return var1.getType() != EntityType.BREEZE && super.canAttack(var1);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (this.level().isClientSide() && DATA_POSE.equals(var1)) {
         this.resetAnimations();
         Pose var2 = this.getPose();
         switch(var2) {
            case SHOOTING:
               this.shoot.startIfStopped(this.tickCount);
               break;
            case INHALING:
               this.longJump.startIfStopped(this.tickCount);
               break;
            case SLIDING:
               this.slide.startIfStopped(this.tickCount);
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   private void resetAnimations() {
      this.shoot.stop();
      this.idle.stop();
      this.inhale.stop();
      this.longJump.stop();
      this.slide.stop();
   }

   @Override
   public void tick() {
      switch(this.getPose()) {
         case SHOOTING:
         case INHALING:
         case STANDING:
            this.resetJumpTrail().emitGroundParticles(1 + this.getRandom().nextInt(1));
            break;
         case SLIDING:
            this.emitGroundParticles(20);
            break;
         case LONG_JUMPING:
            this.emitJumpTrailParticles();
      }

      super.tick();
   }

   public Breeze resetJumpTrail() {
      this.jumpTrailStartedTick = 0;
      return this;
   }

   public Breeze emitJumpDustParticles() {
      Vec3 var1 = this.position().add(0.0, 0.10000000149011612, 0.0);

      for(int var2 = 0; var2 < 20; ++var2) {
         this.level().addParticle(ParticleTypes.GUST_DUST, var1.x, var1.y, var1.z, 0.0, 0.0, 0.0);
      }

      return this;
   }

   public void emitJumpTrailParticles() {
      if (++this.jumpTrailStartedTick <= 5) {
         BlockState var1 = this.level().getBlockState(this.blockPosition().below());
         Vec3 var2 = this.getDeltaMovement();
         Vec3 var3 = this.position().add(var2).add(0.0, 0.10000000149011612, 0.0);

         for(int var4 = 0; var4 < 3; ++var4) {
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var1), var3.x, var3.y, var3.z, 0.0, 0.0, 0.0);
         }
      }
   }

   public void emitGroundParticles(int var1) {
      Vec3 var2 = this.getBoundingBox().getCenter();
      Vec3 var3 = new Vec3(var2.x, this.position().y, var2.z);
      BlockState var4 = this.level().getBlockState(this.blockPosition().below());
      if (var4.getRenderShape() != RenderShape.INVISIBLE) {
         for(int var5 = 0; var5 < var1; ++var5) {
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var4), var3.x, var3.y, var3.z, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public void playAmbientSound() {
      this.level().playLocalSound(this, this.getAmbientSound(), this.getSoundSource(), 1.0F, 1.0F);
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.BREEZE_DEATH;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.BREEZE_HURT;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.onGround() ? SoundEvents.BREEZE_IDLE_GROUND : SoundEvents.BREEZE_IDLE_AIR;
   }

   public boolean withinOuterCircleRange(Vec3 var1) {
      Vec3 var2 = this.blockPosition().getCenter();
      return var1.closerThan(var2, 20.0, 10.0) && !var1.closerThan(var2, 8.0, 10.0);
   }

   public boolean withinMiddleCircleRange(Vec3 var1) {
      Vec3 var2 = this.blockPosition().getCenter();
      return var1.closerThan(var2, 8.0, 10.0) && !var1.closerThan(var2, 4.0, 10.0);
   }

   public boolean withinInnerCircleRange(Vec3 var1) {
      Vec3 var2 = this.blockPosition().getCenter();
      return var1.closerThan(var2, 4.0, 10.0);
   }

   @Override
   protected void customServerAiStep() {
      this.level().getProfiler().push("breezeBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().popPush("breezeActivityUpdate");
      this.level().getProfiler().pop();
      super.customServerAiStep();
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
      DebugPackets.sendBreezeInfo(this);
   }

   @Override
   public boolean canAttackType(EntityType<?> var1) {
      return var1 == EntityType.PLAYER;
   }

   @Override
   public int getMaxHeadYRot() {
      return 30;
   }

   @Override
   public int getHeadRotSpeed() {
      return 25;
   }

   public double getSnoutYPosition() {
      return this.getEyeY() - 0.4;
   }

   @Override
   public boolean isInvulnerableTo(DamageSource var1) {
      return var1.is(DamageTypeTags.BREEZE_IMMUNE_TO) || var1.getEntity() instanceof Breeze || super.isInvulnerableTo(var1);
   }

   @Override
   public double getFluidJumpThreshold() {
      return (double)this.getEyeHeight();
   }

   @Override
   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      if (var1 > 3.0F) {
         this.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
      }

      return super.causeFallDamage(var1, var2, var3);
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }
}
