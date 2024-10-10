package net.minecraft.world.entity.monster.creaking;

import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class Creaking extends Monster {
   private static final EntityDataAccessor<Boolean> CAN_MOVE = SynchedEntityData.defineId(Creaking.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> IS_ACTIVE = SynchedEntityData.defineId(Creaking.class, EntityDataSerializers.BOOLEAN);
   private static final int ATTACK_ANIMATION_DURATION = 20;
   private static final int MAX_HEALTH = 1;
   private static final float ATTACK_DAMAGE = 2.0F;
   private static final float FOLLOW_RANGE = 32.0F;
   private static final float ACTIVATION_RANGE_SQ = 144.0F;
   public static final int ATTACK_INTERVAL = 40;
   private static final float MOVEMENT_SPEED_WHEN_FIGHTING = 0.3F;
   public static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.2F;
   public static final int CREAKING_ORANGE = 16545810;
   public static final int CREAKING_GRAY = 6250335;
   private int attackAnimationRemainingTicks;
   public final AnimationState attackAnimationState = new AnimationState();
   public final AnimationState invulnerabilityAnimationState = new AnimationState();

   public Creaking(EntityType<? extends Creaking> var1, Level var2) {
      super(var1, var2);
      this.lookControl = new Creaking.CreakingLookControl(this);
      this.moveControl = new Creaking.CreakingMoveControl(this);
      this.jumpControl = new Creaking.CreakingJumpControl(this);
      GroundPathNavigation var3 = (GroundPathNavigation)this.getNavigation();
      var3.setCanFloat(true);
      this.xpReward = 0;
   }

   @Override
   protected BodyRotationControl createBodyControl() {
      return new Creaking.CreakingBodyRotationControl(this);
   }

   @Override
   protected Brain.Provider<Creaking> brainProvider() {
      return CreakingAi.brainProvider();
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return CreakingAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(CAN_MOVE, true);
      var1.define(IS_ACTIVE, false);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.MAX_HEALTH, 1.0)
         .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
         .add(Attributes.ATTACK_DAMAGE, 2.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.STEP_HEIGHT, 1.0);
   }

   public boolean canMove() {
      return this.entityData.get(CAN_MOVE);
   }

   @Override
   public boolean doHurtTarget(ServerLevel var1, Entity var2) {
      if (!(var2 instanceof LivingEntity)) {
         return false;
      } else {
         this.attackAnimationRemainingTicks = 20;
         this.level().broadcastEntityEvent(this, (byte)4);
         return super.doHurtTarget(var1, var2);
      }
   }

   @Override
   public boolean isPushable() {
      return super.isPushable() && this.canMove();
   }

   @Override
   public Brain<Creaking> getBrain() {
      return (Brain<Creaking>)super.getBrain();
   }

   @Override
   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("creakingBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      var2.pop();
      CreakingAi.updateActivity(this);
   }

   @Override
   public void aiStep() {
      if (this.attackAnimationRemainingTicks > 0) {
         this.attackAnimationRemainingTicks--;
      }

      if (!this.level().isClientSide) {
         boolean var1 = this.entityData.get(CAN_MOVE);
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

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         this.setupAnimationStates();
      }
   }

   private void setupAnimationStates() {
      this.attackAnimationState.animateWhen(this.attackAnimationRemainingTicks > 0, this.tickCount);
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 4) {
         this.attackAnimationRemainingTicks = 20;
         this.playAttackSound();
      } else {
         super.handleEntityEvent(var1);
      }
   }

   @Override
   public void playAttackSound() {
      this.makeSound(SoundEvents.CREAKING_ATTACK);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.isActive() ? null : SoundEvents.CREAKING_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.CREAKING_SWAY;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.CREAKING_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.CREAKING_STEP, 0.15F, 1.0F);
   }

   @Nullable
   @Override
   public LivingEntity getTarget() {
      return this.getTargetFromBrain();
   }

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   @Override
   public void knockback(double var1, double var3, double var5) {
      if (this.canMove()) {
         super.knockback(var1, var3, var5);
      }
   }

   public boolean checkCanMove() {
      List var1 = this.brain.getMemory(MemoryModuleType.NEAREST_PLAYERS).orElse(List.of());
      if (var1.isEmpty()) {
         if (this.isActive()) {
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.makeSound(SoundEvents.CREAKING_DEACTIVATE);
            this.setIsActive(false);
         }

         return true;
      } else {
         Predicate var2 = this.isActive() ? LivingEntity.PLAYER_NOT_WEARING_DISGUISE_ITEM : var0 -> true;

         for (Player var4 : var1) {
            if (!var4.isCreative()
               && this.isLookingAtMe(var4, 0.5, false, true, var2, new DoubleSupplier[]{this::getEyeY, this::getY, () -> (this.getEyeY() + this.getY()) / 2.0})
               )
             {
               if (this.isActive()) {
                  return false;
               }

               if (var4.distanceToSqr(this) < 144.0) {
                  this.gameEvent(GameEvent.ENTITY_ACTION);
                  this.makeSound(SoundEvents.CREAKING_ACTIVATE);
                  this.setIsActive(true);
                  this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, var4);
                  return false;
               }
            }
         }

         return true;
      }
   }

   public void setIsActive(boolean var1) {
      this.entityData.set(IS_ACTIVE, var1);
   }

   public boolean isActive() {
      return this.entityData.get(IS_ACTIVE);
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return 0.0F;
   }

   class CreakingBodyRotationControl extends BodyRotationControl {
      public CreakingBodyRotationControl(final Creaking nullx) {
         super(nullx);
      }

      @Override
      public void clientTick() {
         if (Creaking.this.canMove()) {
            super.clientTick();
         }
      }
   }

   class CreakingJumpControl extends JumpControl {
      public CreakingJumpControl(final Creaking nullx) {
         super(nullx);
      }

      @Override
      public void tick() {
         if (Creaking.this.canMove()) {
            super.tick();
         } else {
            Creaking.this.setJumping(false);
         }
      }
   }

   class CreakingLookControl extends LookControl {
      public CreakingLookControl(final Creaking nullx) {
         super(nullx);
      }

      @Override
      public void tick() {
         if (Creaking.this.canMove()) {
            super.tick();
         }
      }
   }

   class CreakingMoveControl extends MoveControl {
      public CreakingMoveControl(final Creaking nullx) {
         super(nullx);
      }

      @Override
      public void tick() {
         if (Creaking.this.canMove()) {
            super.tick();
         }
      }
   }
}