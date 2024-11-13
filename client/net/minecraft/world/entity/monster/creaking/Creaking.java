package net.minecraft.world.entity.monster.creaking;

import com.mojang.serialization.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CreakingHeartBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Creaking extends Monster {
   private static final EntityDataAccessor<Boolean> CAN_MOVE;
   private static final EntityDataAccessor<Boolean> IS_ACTIVE;
   private static final EntityDataAccessor<Boolean> IS_TEARING_DOWN;
   private static final int ATTACK_ANIMATION_DURATION = 15;
   private static final int MAX_HEALTH = 1;
   private static final float ATTACK_DAMAGE = 3.0F;
   private static final float FOLLOW_RANGE = 32.0F;
   private static final float ACTIVATION_RANGE_SQ = 144.0F;
   public static final int ATTACK_INTERVAL = 40;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.4F;
   public static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.3F;
   public static final int CREAKING_ORANGE = 16545810;
   public static final int CREAKING_GRAY = 6250335;
   public static final int INVULNERABILITY_ANIMATION_DURATION = 8;
   public static final int TWITCH_DEATH_DURATION = 45;
   private static final int MAX_PLAYER_STUCK_COUNTER = 4;
   private int attackAnimationRemainingTicks;
   public final AnimationState attackAnimationState = new AnimationState();
   public final AnimationState invulnerabilityAnimationState = new AnimationState();
   public final AnimationState deathAnimationState = new AnimationState();
   private int invulnerabilityAnimationRemainingTicks;
   private boolean eyesGlowing;
   private int nextFlickerTime;
   @Nullable
   BlockPos homePos;
   private int playerStuckCounter;

   public Creaking(EntityType<? extends Creaking> var1, Level var2) {
      super(var1, var2);
      this.lookControl = new CreakingLookControl(this);
      this.moveControl = new CreakingMoveControl(this);
      this.jumpControl = new CreakingJumpControl(this);
      GroundPathNavigation var3 = (GroundPathNavigation)this.getNavigation();
      var3.setCanFloat(true);
      this.xpReward = 0;
   }

   public void setTransient(BlockPos var1) {
      this.homePos = var1;
      this.setPathfindingMalus(PathType.DAMAGE_OTHER, 8.0F);
      this.setPathfindingMalus(PathType.POWDER_SNOW, 8.0F);
      this.setPathfindingMalus(PathType.LAVA, 8.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);
   }

   public boolean isTransient() {
      return this.homePos != null;
   }

   protected BodyRotationControl createBodyControl() {
      return new CreakingBodyRotationControl(this);
   }

   protected Brain.Provider<Creaking> brainProvider() {
      return CreakingAi.brainProvider();
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return CreakingAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(CAN_MOVE, true);
      var1.define(IS_ACTIVE, false);
      var1.define(IS_TEARING_DOWN, false);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 1.0).add(Attributes.MOVEMENT_SPEED, 0.4000000059604645).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.FOLLOW_RANGE, 32.0).add(Attributes.STEP_HEIGHT, 1.0625);
   }

   public boolean canMove() {
      return (Boolean)this.entityData.get(CAN_MOVE);
   }

   public boolean doHurtTarget(ServerLevel var1, Entity var2) {
      if (!(var2 instanceof LivingEntity)) {
         return false;
      } else {
         this.attackAnimationRemainingTicks = 15;
         this.level().broadcastEntityEvent(this, (byte)4);
         return super.doHurtTarget(var1, var2);
      }
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.homePos != null && !var2.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         if (!this.isInvulnerableTo(var1, var2) && this.invulnerabilityAnimationRemainingTicks <= 0 && !this.isDeadOrDying()) {
            Player var4 = this.blameSourceForDamage(var2);
            Entity var5 = var2.getDirectEntity();
            if (!(var5 instanceof LivingEntity) && !(var5 instanceof Projectile) && var4 == null) {
               return false;
            } else {
               this.invulnerabilityAnimationRemainingTicks = 8;
               this.level().broadcastEntityEvent(this, (byte)66);
               BlockEntity var7 = this.level().getBlockEntity(this.homePos);
               if (var7 instanceof CreakingHeartBlockEntity) {
                  CreakingHeartBlockEntity var6 = (CreakingHeartBlockEntity)var7;
                  if (var6.isProtector(this)) {
                     if (var4 != null) {
                        var6.creakingHurt();
                     }

                     this.playHurtSound(var2);
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      } else {
         return super.hurtServer(var1, var2, var3);
      }
   }

   public Player blameSourceForDamage(DamageSource var1) {
      this.resolveMobResponsibleForDamage(var1);
      return this.resolvePlayerResponsibleForDamage(var1);
   }

   public boolean isPushable() {
      return super.isPushable() && this.canMove();
   }

   public void push(double var1, double var3, double var5) {
      if (this.canMove()) {
         super.push(var1, var3, var5);
      }
   }

   public Brain<Creaking> getBrain() {
      return super.getBrain();
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("creakingBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      var2.pop();
      CreakingAi.updateActivity(this);
   }

   public void aiStep() {
      if (this.invulnerabilityAnimationRemainingTicks > 0) {
         --this.invulnerabilityAnimationRemainingTicks;
      }

      if (this.attackAnimationRemainingTicks > 0) {
         --this.attackAnimationRemainingTicks;
      }

      if (!this.level().isClientSide) {
         boolean var1 = (Boolean)this.entityData.get(CAN_MOVE);
         boolean var2 = this.checkCanMove();
         if (var2 != var1) {
            this.gameEvent(GameEvent.ENTITY_ACTION);
            if (var2) {
               this.makeSound(SoundEvents.CREAKING_UNFREEZE);
            } else {
               this.stopInPlace();
               this.makeSound(SoundEvents.CREAKING_FREEZE);
            }
         }

         this.entityData.set(CAN_MOVE, var2);
      }

      super.aiStep();
   }

   public void tick() {
      if (!this.level().isClientSide && this.homePos != null) {
         boolean var10000;
         label21: {
            BlockEntity var3 = this.level().getBlockEntity(this.homePos);
            if (var3 instanceof CreakingHeartBlockEntity) {
               CreakingHeartBlockEntity var2 = (CreakingHeartBlockEntity)var3;
               if (var2.isProtector(this)) {
                  var10000 = true;
                  break label21;
               }
            }

            var10000 = false;
         }

         boolean var1 = var10000;
         if (!var1) {
            this.setHealth(0.0F);
         }
      }

      super.tick();
      if (this.level().isClientSide) {
         this.setupAnimationStates();
         this.checkEyeBlink();
      }

   }

   protected void tickDeath() {
      if (this.isTransient() && this.isTearingDown()) {
         ++this.deathTime;
         if (!this.level().isClientSide() && this.deathTime > 45 && !this.isRemoved()) {
            this.tearDown();
         }
      } else {
         super.tickDeath();
      }

   }

   protected void updateWalkAnimation(float var1) {
      float var2 = Math.min(var1 * 25.0F, 3.0F);
      this.walkAnimation.update(var2, 0.4F, 1.0F);
   }

   private void setupAnimationStates() {
      this.attackAnimationState.animateWhen(this.attackAnimationRemainingTicks > 0, this.tickCount);
      this.invulnerabilityAnimationState.animateWhen(this.invulnerabilityAnimationRemainingTicks > 0, this.tickCount);
      this.deathAnimationState.animateWhen(this.isTearingDown(), this.tickCount);
   }

   public void tearDown() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         AABB var10 = this.getBoundingBox();
         Vec3 var3 = var10.getCenter();
         double var4 = var10.getXsize() * 0.3;
         double var6 = var10.getYsize() * 0.3;
         double var8 = var10.getZsize() * 0.3;
         var1.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK_CRUMBLE, Blocks.PALE_OAK_WOOD.defaultBlockState()), var3.x, var3.y, var3.z, 100, var4, var6, var8, 0.0);
         var1.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK_CRUMBLE, (BlockState)Blocks.CREAKING_HEART.defaultBlockState().setValue(CreakingHeartBlock.ACTIVE, true)), var3.x, var3.y, var3.z, 10, var4, var6, var8, 0.0);
      }

      this.makeSound(this.getDeathSound());
      this.remove(Entity.RemovalReason.DISCARDED);
   }

   public void creakingDeathEffects(DamageSource var1) {
      this.blameSourceForDamage(var1);
      this.die(var1);
      this.makeSound(SoundEvents.CREAKING_TWITCH);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 66) {
         this.invulnerabilityAnimationRemainingTicks = 8;
         this.playHurtSound(this.damageSources().generic());
      } else if (var1 == 4) {
         this.attackAnimationRemainingTicks = 15;
         this.playAttackSound();
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public boolean fireImmune() {
      return this.isTransient() || super.fireImmune();
   }

   protected boolean canAddPassenger(Entity var1) {
      return !this.isTransient() && super.canAddPassenger(var1);
   }

   protected boolean couldAcceptPassenger() {
      return !this.isTransient() && super.couldAcceptPassenger();
   }

   protected void addPassenger(Entity var1) {
      if (this.isTransient()) {
         throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
      }
   }

   public boolean canUsePortal(boolean var1) {
      return !this.isTransient() && super.canUsePortal(var1);
   }

   protected PathNavigation createNavigation(Level var1) {
      return new CreakingPathNavigation(this, var1);
   }

   public boolean playerIsStuckInYou() {
      List var1 = (List)this.brain.getMemory(MemoryModuleType.NEAREST_PLAYERS).orElse(List.of());
      if (var1.isEmpty()) {
         this.playerStuckCounter = 0;
         return false;
      } else {
         AABB var2 = this.getBoundingBox();

         for(Player var4 : var1) {
            if (var2.contains(var4.getEyePosition())) {
               ++this.playerStuckCounter;
               return this.playerStuckCounter > 4;
            }
         }

         this.playerStuckCounter = 0;
         return false;
      }
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("home_pos")) {
         this.setTransient((BlockPos)NbtUtils.readBlockPos(var1, "home_pos").orElseThrow());
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.homePos != null) {
         var1.put("home_pos", NbtUtils.writeBlockPos(this.homePos));
      }

   }

   public void setTearingDown() {
      this.entityData.set(IS_TEARING_DOWN, true);
   }

   public boolean isTearingDown() {
      return (Boolean)this.entityData.get(IS_TEARING_DOWN);
   }

   public boolean hasGlowingEyes() {
      return this.eyesGlowing;
   }

   public void checkEyeBlink() {
      if (this.deathTime > this.nextFlickerTime) {
         this.nextFlickerTime = this.deathTime + this.getRandom().nextIntBetweenInclusive(this.eyesGlowing ? 2 : this.deathTime / 4, this.eyesGlowing ? 8 : this.deathTime / 2);
         this.eyesGlowing = !this.eyesGlowing;
      }

   }

   public void playAttackSound() {
      this.makeSound(SoundEvents.CREAKING_ATTACK);
   }

   protected SoundEvent getAmbientSound() {
      return this.isActive() ? null : SoundEvents.CREAKING_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CREAKING_SWAY;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.CREAKING_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.CREAKING_STEP, 0.15F, 1.0F);
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public void knockback(double var1, double var3, double var5) {
      if (this.canMove()) {
         super.knockback(var1, var3, var5);
      }
   }

   public boolean checkCanMove() {
      List var1 = (List)this.brain.getMemory(MemoryModuleType.NEAREST_PLAYERS).orElse(List.of());
      boolean var2 = this.isActive();
      if (var1.isEmpty()) {
         if (var2) {
            this.deactivate();
         }

         return true;
      } else {
         boolean var3 = false;

         for(Player var5 : var1) {
            if (this.canAttack(var5) && !this.isAlliedTo(var5)) {
               var3 = true;
               if ((!var2 || LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM.test(var5)) && this.isLookingAtMe(var5, 0.5, false, true, new double[]{this.getEyeY(), this.getY() + 0.5 * (double)this.getScale(), (this.getEyeY() + this.getY()) / 2.0})) {
                  if (var2) {
                     return false;
                  }

                  if (var5.distanceToSqr(this) < 144.0) {
                     this.activate(var5);
                     return false;
                  }
               }
            }
         }

         if (!var3 && var2) {
            this.deactivate();
         }

         return true;
      }
   }

   public void activate(Player var1) {
      this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, var1);
      this.gameEvent(GameEvent.ENTITY_ACTION);
      this.makeSound(SoundEvents.CREAKING_ACTIVATE);
      this.setIsActive(true);
   }

   public void deactivate() {
      this.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
      this.gameEvent(GameEvent.ENTITY_ACTION);
      this.makeSound(SoundEvents.CREAKING_DEACTIVATE);
      this.setIsActive(false);
   }

   public void setIsActive(boolean var1) {
      this.entityData.set(IS_ACTIVE, var1);
   }

   public boolean isActive() {
      return (Boolean)this.entityData.get(IS_ACTIVE);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   static {
      CAN_MOVE = SynchedEntityData.<Boolean>defineId(Creaking.class, EntityDataSerializers.BOOLEAN);
      IS_ACTIVE = SynchedEntityData.<Boolean>defineId(Creaking.class, EntityDataSerializers.BOOLEAN);
      IS_TEARING_DOWN = SynchedEntityData.<Boolean>defineId(Creaking.class, EntityDataSerializers.BOOLEAN);
   }

   class CreakingLookControl extends LookControl {
      public CreakingLookControl(final Creaking var2) {
         super(var2);
      }

      public void tick() {
         if (Creaking.this.canMove()) {
            super.tick();
         }

      }
   }

   class CreakingMoveControl extends MoveControl {
      public CreakingMoveControl(final Creaking var2) {
         super(var2);
      }

      public void tick() {
         if (Creaking.this.canMove()) {
            super.tick();
         }

      }
   }

   class CreakingJumpControl extends JumpControl {
      public CreakingJumpControl(final Creaking var2) {
         super(var2);
      }

      public void tick() {
         if (Creaking.this.canMove()) {
            super.tick();
         } else {
            Creaking.this.setJumping(false);
         }

      }
   }

   class CreakingBodyRotationControl extends BodyRotationControl {
      public CreakingBodyRotationControl(final Creaking var2) {
         super(var2);
      }

      public void clientTick() {
         if (Creaking.this.canMove()) {
            super.clientTick();
         }

      }
   }

   class HomeNodeEvaluator extends WalkNodeEvaluator {
      private static final int MAX_DISTANCE_TO_HOME_SQ = 1024;

      HomeNodeEvaluator() {
         super();
      }

      public PathType getPathType(PathfindingContext var1, int var2, int var3, int var4) {
         BlockPos var5 = Creaking.this.homePos;
         if (var5 == null) {
            return super.getPathType(var1, var2, var3, var4);
         } else {
            double var6 = var5.distSqr(new Vec3i(var2, var3, var4));
            return var6 > 1024.0 && var6 >= var5.distSqr(var1.mobPosition()) ? PathType.BLOCKED : super.getPathType(var1, var2, var3, var4);
         }
      }
   }

   class CreakingPathNavigation extends GroundPathNavigation {
      CreakingPathNavigation(final Creaking var2, final Level var3) {
         super(var2, var3);
      }

      public void tick() {
         if (Creaking.this.canMove()) {
            super.tick();
         }

      }

      protected PathFinder createPathFinder(int var1) {
         this.nodeEvaluator = Creaking.this.new HomeNodeEvaluator();
         this.nodeEvaluator.setCanPassDoors(true);
         return new PathFinder(this.nodeEvaluator, var1);
      }
   }
}
