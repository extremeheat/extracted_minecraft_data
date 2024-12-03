package net.minecraft.world.entity.monster.breeze;

import com.mojang.serialization.Dynamic;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Breeze extends Monster {
   private static final int SLIDE_PARTICLES_AMOUNT = 20;
   private static final int IDLE_PARTICLES_AMOUNT = 1;
   private static final int JUMP_DUST_PARTICLES_AMOUNT = 20;
   private static final int JUMP_TRAIL_PARTICLES_AMOUNT = 3;
   private static final int JUMP_TRAIL_DURATION_TICKS = 5;
   private static final int JUMP_CIRCLE_DISTANCE_Y = 10;
   private static final float FALL_DISTANCE_SOUND_TRIGGER_THRESHOLD = 3.0F;
   private static final int WHIRL_SOUND_FREQUENCY_MIN = 1;
   private static final int WHIRL_SOUND_FREQUENCY_MAX = 80;
   public AnimationState idle = new AnimationState();
   public AnimationState slide = new AnimationState();
   public AnimationState slideBack = new AnimationState();
   public AnimationState longJump = new AnimationState();
   public AnimationState shoot = new AnimationState();
   public AnimationState inhale = new AnimationState();
   private int jumpTrailStartedTick = 0;
   private int soundTick = 0;
   private static final ProjectileDeflection PROJECTILE_DEFLECTION = (var0, var1, var2) -> {
      var1.level().playSound((Player)null, (Entity)var1, SoundEvents.BREEZE_DEFLECT, var1.getSoundSource(), 1.0F, 1.0F);
      ProjectileDeflection.REVERSE.deflect(var0, var1, var2);
   };

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.6299999952316284).add(Attributes.MAX_HEALTH, 30.0).add(Attributes.FOLLOW_RANGE, 24.0).add(Attributes.ATTACK_DAMAGE, 3.0);
   }

   public Breeze(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
      this.xpReward = 10;
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return BreezeAi.makeBrain(this, this.brainProvider().makeBrain(var1));
   }

   public Brain<Breeze> getBrain() {
      return super.getBrain();
   }

   protected Brain.Provider<Breeze> brainProvider() {
      return Brain.<Breeze>provider(BreezeAi.MEMORY_TYPES, BreezeAi.SENSOR_TYPES);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (this.level().isClientSide() && DATA_POSE.equals(var1)) {
         this.resetAnimations();
         Pose var2 = this.getPose();
         switch (var2) {
            case SHOOTING -> this.shoot.startIfStopped(this.tickCount);
            case INHALING -> this.inhale.startIfStopped(this.tickCount);
            case SLIDING -> this.slide.startIfStopped(this.tickCount);
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   private void resetAnimations() {
      this.shoot.stop();
      this.idle.stop();
      this.inhale.stop();
      this.longJump.stop();
   }

   public void tick() {
      Pose var1 = this.getPose();
      switch (var1) {
         case SHOOTING:
         case INHALING:
         case STANDING:
            this.resetJumpTrail().emitGroundParticles(1 + this.getRandom().nextInt(1));
            break;
         case SLIDING:
            this.emitGroundParticles(20);
            break;
         case LONG_JUMPING:
            this.longJump.startIfStopped(this.tickCount);
            this.emitJumpTrailParticles();
      }

      this.idle.startIfStopped(this.tickCount);
      if (var1 != Pose.SLIDING && this.slide.isStarted()) {
         this.slideBack.start(this.tickCount);
         this.slide.stop();
      }

      this.soundTick = this.soundTick == 0 ? this.random.nextIntBetweenInclusive(1, 80) : this.soundTick - 1;
      if (this.soundTick == 0) {
         this.playWhirlSound();
      }

      super.tick();
   }

   public Breeze resetJumpTrail() {
      this.jumpTrailStartedTick = 0;
      return this;
   }

   public void emitJumpTrailParticles() {
      if (++this.jumpTrailStartedTick <= 5) {
         BlockState var1 = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
         Vec3 var2 = this.getDeltaMovement();
         Vec3 var3 = this.position().add(var2).add(0.0, 0.10000000149011612, 0.0);

         for(int var4 = 0; var4 < 3; ++var4) {
            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var1), var3.x, var3.y, var3.z, 0.0, 0.0, 0.0);
         }

      }
   }

   public void emitGroundParticles(int var1) {
      if (!this.isPassenger()) {
         Vec3 var2 = this.getBoundingBox().getCenter();
         Vec3 var3 = new Vec3(var2.x, this.position().y, var2.z);
         BlockState var4 = !this.getInBlockState().isAir() ? this.getInBlockState() : this.getBlockStateOn();
         if (var4.getRenderShape() != RenderShape.INVISIBLE) {
            for(int var5 = 0; var5 < var1; ++var5) {
               this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, var4), var3.x, var3.y, var3.z, 0.0, 0.0, 0.0);
            }

         }
      }
   }

   public void playAmbientSound() {
      if (this.getTarget() == null || !this.onGround()) {
         this.level().playLocalSound(this, this.getAmbientSound(), this.getSoundSource(), 1.0F, 1.0F);
      }
   }

   public void playWhirlSound() {
      float var1 = 0.7F + 0.4F * this.random.nextFloat();
      float var2 = 0.8F + 0.2F * this.random.nextFloat();
      this.level().playLocalSound(this, SoundEvents.BREEZE_WHIRL, this.getSoundSource(), var2, var1);
   }

   public ProjectileDeflection deflection(Projectile var1) {
      if (var1.getType() != EntityType.BREEZE_WIND_CHARGE && var1.getType() != EntityType.WIND_CHARGE) {
         return this.getType().is(EntityTypeTags.DEFLECTS_PROJECTILES) ? PROJECTILE_DEFLECTION : ProjectileDeflection.NONE;
      } else {
         return ProjectileDeflection.NONE;
      }
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BREEZE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.BREEZE_HURT;
   }

   protected SoundEvent getAmbientSound() {
      return this.onGround() ? SoundEvents.BREEZE_IDLE_GROUND : SoundEvents.BREEZE_IDLE_AIR;
   }

   public Optional<LivingEntity> getHurtBy() {
      return this.getBrain().getMemory(MemoryModuleType.HURT_BY).map(DamageSource::getEntity).filter((var0) -> var0 instanceof LivingEntity).map((var0) -> (LivingEntity)var0);
   }

   public boolean withinInnerCircleRange(Vec3 var1) {
      Vec3 var2 = this.blockPosition().getCenter();
      return var1.closerThan(var2, 4.0, 10.0);
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("breezeBrain");
      this.getBrain().tick(var1, this);
      var2.popPush("breezeActivityUpdate");
      BreezeAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
      DebugPackets.sendBreezeInfo(this);
   }

   public boolean canAttackType(EntityType<?> var1) {
      return var1 == EntityType.PLAYER || var1 == EntityType.IRON_GOLEM;
   }

   public int getMaxHeadYRot() {
      return 30;
   }

   public int getHeadRotSpeed() {
      return 25;
   }

   public double getFiringYPosition() {
      return this.getY() + (double)(this.getBbHeight() / 2.0F) + 0.30000001192092896;
   }

   public boolean isInvulnerableTo(ServerLevel var1, DamageSource var2) {
      return var2.getEntity() instanceof Breeze || super.isInvulnerableTo(var1, var2);
   }

   public double getFluidJumpThreshold() {
      return (double)this.getEyeHeight();
   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      if (var1 > 3.0F) {
         this.playSound(SoundEvents.BREEZE_LAND, 1.0F, 1.0F);
      }

      return super.causeFallDamage(var1, var2, var3);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }
}
