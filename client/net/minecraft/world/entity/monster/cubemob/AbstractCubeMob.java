package net.minecraft.world.entity.monster.cubemob;

import com.google.common.annotations.VisibleForTesting;
import java.util.EnumSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.ConversionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import org.jspecify.annotations.Nullable;

public abstract class AbstractCubeMob extends AgeableMob {
   protected static final EntityDataAccessor<Integer> ID_SIZE;
   public static final int MIN_SIZE = 1;
   public static final int MAX_SIZE = 127;
   public static final int MAX_NATURAL_SIZE = 4;
   private static final boolean DEFAULT_WAS_ON_GROUND = false;
   public float targetSquish;
   public float squish;
   public float oSquish;
   private boolean wasOnGround = false;

   protected AbstractCubeMob(final EntityType<? extends AbstractCubeMob> type, final Level level) {
      super(type, level);
      this.fixupDimensions();
      this.moveControl = new CubeMobMoveControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new CubeMobFloatGoal(this));
      this.goalSelector.addGoal(4, new CubeMobRandomDirectionGoal(this));
      this.goalSelector.addGoal(5, new CubeMobKeepOnJumpingGoal(this));
      this.addBehaviourGoals();
      this.addTargetingGoals();
   }

   protected abstract void addBehaviourGoals();

   protected abstract void addTargetingGoals();

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(ID_SIZE, 1);
   }

   @VisibleForTesting
   public void setSize(final int size, final boolean updateHealth) {
      int actualSize = Mth.clamp(size, 1, 127);
      this.entityData.set(ID_SIZE, actualSize);
      this.reapplyPosition();
      this.refreshDimensions();
      this.setCubeMobHealth(actualSize);
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)(0.2F + 0.1F * (float)actualSize));
      if (updateHealth) {
         this.setHealth(this.getMaxHealth());
      }

   }

   protected void setCubeMobHealth(final int actualSize) {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(actualSize * actualSize));
   }

   public int getSize() {
      return (Integer)this.entityData.get(ID_SIZE);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("Size", this.getSize() - 1);
      output.putBoolean("wasOnGround", this.wasOnGround);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      this.setSize(input.getIntOr("Size", 0) + 1, false);
      super.readAdditionalSaveData(input);
      this.wasOnGround = input.getBooleanOr("wasOnGround", false);
   }

   public boolean isTiny() {
      return this.getSize() <= 1;
   }

   protected abstract @Nullable ParticleOptions getParticleType();

   public void tick() {
      this.oSquish = this.squish;
      this.squish += (this.targetSquish - this.squish) * 0.5F;
      super.tick();
      if (this.onGround() && !this.wasOnGround) {
         float size = this.getDimensions(this.getPose()).width() * 2.0F;
         float radius = size / 2.0F;

         for(int i = 0; (float)i < size * 16.0F; ++i) {
            float dir = this.random.nextFloat() * 6.2831855F;
            float d = this.random.nextFloat() * 0.5F + 0.5F;
            float xd = Mth.sin((double)dir) * radius * d;
            float zd = Mth.cos((double)dir) * radius * d;
            ParticleOptions particleType = this.getParticleType();
            if (particleType != null) {
               this.level().addParticle(particleType, this.getX() + (double)xd, this.getY(), this.getZ() + (double)zd, 0.0, 0.0, 0.0);
            }
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
      double oldX = this.getX();
      double oldY = this.getY();
      double oldZ = this.getZ();
      super.refreshDimensions();
      this.setPos(oldX, oldY, oldZ);
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (ID_SIZE.equals(accessor)) {
         this.refreshDimensions();
         this.setYRot(this.yHeadRot);
         this.yBodyRot = this.yHeadRot;
         if (this.isInWater() && this.random.nextInt(20) == 0) {
            this.doWaterSplashEffect();
         }
      }

      super.onSyncedDataUpdated(accessor);
   }

   public EntityType<? extends AbstractCubeMob> getType() {
      return super.getType();
   }

   public void remove(final Entity.RemovalReason reason) {
      int size = this.getSize();
      if (!this.level().isClientSide() && size > 1 && this.isDeadOrDying()) {
         float width = this.getDimensions(this.getPose()).width();
         float xzCubeSpawnOffset = width / 2.0F;
         int halfSize = size / 2;
         int count = this.getSplitCount();
         PlayerTeam team = this.getTeam();

         for(int i = 0; i < count; ++i) {
            float xd = ((float)(i % 2) - 0.5F) * xzCubeSpawnOffset;
            float zd = ((float)(i / 2) - 0.5F) * xzCubeSpawnOffset;
            this.convertTo(this.getType(), new ConversionParams(ConversionType.SPLIT_ON_DEATH, false, false, team), EntitySpawnReason.TRIGGERED, (cubeMob) -> this.setUpSplitCube(cubeMob, halfSize, xd, zd));
         }
      }

      super.remove(reason);
   }

   protected void setUpSplitCube(final AbstractCubeMob cubeMob, final int halfSize, final float xd, final float zd) {
      cubeMob.setSize(halfSize, true);
      cubeMob.snapTo(this.getX() + (double)xd, this.getY() + 0.5, this.getZ() + (double)zd, this.random.nextFloat() * 360.0F, 0.0F);
   }

   protected int getSplitCount() {
      return 2 + this.random.nextInt(3);
   }

   public void push(final Entity entity) {
      super.push(entity);
      if (entity instanceof IronGolem && this.canDealDamage()) {
         this.dealDamage((LivingEntity)entity);
      }

   }

   public void playerTouch(final Player player) {
      if (this.canDealDamage()) {
         this.dealDamage(player);
      }

   }

   protected void dealDamage(final LivingEntity target) {
      Level var3 = this.level();
      if (var3 instanceof ServerLevel level) {
         if (this.isAlive() && this.doTeamsAllowDamage(target) && this.isWithinMeleeAttackRange(target) && this.hasLineOfSight(target)) {
            DamageSource damageSource = this.damageSources().mobAttack(this);
            if (target.hurtServer(level, damageSource, this.getAttackDamage())) {
               this.playSound(SoundEvents.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
               EnchantmentHelper.doPostAttackEffects(level, target, damageSource);
            }
         }
      }

   }

   protected Vec3 getPassengerAttachmentPoint(final Entity passenger, final EntityDimensions dimensions, final float scale) {
      return new Vec3(0.0, (double)dimensions.height() - 0.015625 * (double)this.getSize() * (double)scale, 0.0);
   }

   protected boolean canDealDamage() {
      return !this.isTiny() && this.isEffectiveAi();
   }

   protected float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return this.getType().getDimensions().scale((float)this.getSize());
   }

   public void jumpFromGround() {
      Vec3 movement = this.getDeltaMovement();
      this.setDeltaMovement(movement.x, (double)this.getJumpPower(), movement.z);
      this.needsSync = true;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
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

   public float getSoundPitch() {
      float pitchAdjuster = this.isTiny() ? 1.4F : 0.8F;
      return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * pitchAdjuster;
   }

   protected abstract SoundEvent getJumpSound();

   protected abstract SoundEvent getHurtSound(final DamageSource source);

   protected abstract SoundEvent getDeathSound();

   protected abstract SoundEvent getSquishSound();

   public @Nullable AbstractCubeMob getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      return null;
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
      this.setSpawnSize(level, difficulty);
      return data;
   }

   protected void setSpawnSize(final ServerLevelAccessor level, final DifficultyInstance difficulty) {
      RandomSource random = level.getRandom();
      int sizeScale = random.nextInt(3);
      if (sizeScale < 2 && random.nextFloat() < 0.5F * difficulty.getSpecialMultiplier()) {
         ++sizeScale;
      }

      int size = 1 << sizeScale;
      this.setSize(size, true);
   }

   static {
      ID_SIZE = SynchedEntityData.<Integer>defineId(AbstractCubeMob.class, EntityDataSerializers.INT);
   }

   protected static class CubeMobMoveControl<T extends AbstractCubeMob> extends MoveControl<T> {
      private float yRot;
      private int jumpDelay;
      private boolean isAggressive;

      public CubeMobMoveControl(final T cubeMob) {
         super(cubeMob);
         this.yRot = 180.0F * cubeMob.getYRot() / 3.1415927F;
      }

      public void setDirection(final float yRot, final boolean isAggressive) {
         this.yRot = yRot;
         this.isAggressive = isAggressive;
      }

      public void setWantedMovement(final double speedModifier) {
         this.speedModifier = speedModifier;
         this.operation = MoveControl.Operation.MOVE_TO;
      }

      public void tick() {
         ((AbstractCubeMob)this.mob).setYRot(this.rotlerp(((AbstractCubeMob)this.mob).getYRot(), this.yRot, 90.0F));
         (this.mob).yHeadRot = ((AbstractCubeMob)this.mob).getYRot();
         (this.mob).yBodyRot = ((AbstractCubeMob)this.mob).getYRot();
         if (this.operation != MoveControl.Operation.MOVE_TO) {
            ((AbstractCubeMob)this.mob).setZza(0.0F);
         } else {
            this.operation = MoveControl.Operation.WAIT;
            if (((AbstractCubeMob)this.mob).onGround()) {
               ((AbstractCubeMob)this.mob).setSpeed((float)(this.speedModifier * ((AbstractCubeMob)this.mob).getAttributeValue(Attributes.MOVEMENT_SPEED)));
               if (this.jumpDelay-- <= 0) {
                  this.jumpDelay = ((AbstractCubeMob)this.mob).getJumpDelay();
                  if (this.isAggressive) {
                     this.jumpDelay /= 3;
                  }

                  ((AbstractCubeMob)this.mob).getJumpControl().jump();
                  if (((AbstractCubeMob)this.mob).doPlayJumpSound()) {
                     ((AbstractCubeMob)this.mob).playSound(((AbstractCubeMob)this.mob).getJumpSound(), ((AbstractCubeMob)this.mob).getSoundVolume(), ((AbstractCubeMob)this.mob).getSoundPitch());
                  }
               } else {
                  (this.mob).xxa = 0.0F;
                  (this.mob).zza = 0.0F;
                  ((AbstractCubeMob)this.mob).setSpeed(0.0F);
               }
            } else {
               ((AbstractCubeMob)this.mob).setSpeed((float)(this.speedModifier * ((AbstractCubeMob)this.mob).getAttributeValue(Attributes.MOVEMENT_SPEED)));
            }

         }
      }
   }

   protected static class CubeMobAttackGoal extends Goal {
      private final AbstractCubeMob cubeMob;
      private int growTiredTimer;

      public CubeMobAttackGoal(final AbstractCubeMob cubeMob) {
         super();
         this.cubeMob = cubeMob;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity target = this.cubeMob.getTarget();
         if (target == null) {
            return false;
         } else {
            return !this.cubeMob.canAttack(target) ? false : this.cubeMob.getMoveControl() instanceof CubeMobMoveControl;
         }
      }

      public void start() {
         this.growTiredTimer = reducedTickDelay(300);
         super.start();
      }

      public boolean canContinueToUse() {
         LivingEntity target = this.cubeMob.getTarget();
         if (target == null) {
            return false;
         } else if (!this.cubeMob.canAttack(target)) {
            return false;
         } else {
            return --this.growTiredTimer > 0;
         }
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity target = this.cubeMob.getTarget();
         if (target != null) {
            this.cubeMob.lookAt(target, 10.0F, 10.0F);
         }

         MoveControl var3 = this.cubeMob.getMoveControl();
         if (var3 instanceof CubeMobMoveControl cubeMobMoveControl) {
            cubeMobMoveControl.setDirection(this.cubeMob.getYRot(), this.cubeMob.canDealDamage());
         }

      }
   }

   private static class CubeMobRandomDirectionGoal extends Goal {
      private final AbstractCubeMob cubeMob;
      private float chosenDegrees;
      private int nextRandomizeTime;

      public CubeMobRandomDirectionGoal(final AbstractCubeMob cubeMob) {
         super();
         this.cubeMob = cubeMob;
         this.setFlags(EnumSet.of(Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return this.cubeMob.getTarget() == null && (this.cubeMob.onGround() || this.cubeMob.isInWater() || this.cubeMob.isInLava() || this.cubeMob.hasEffect(MobEffects.LEVITATION)) && this.cubeMob.getMoveControl() instanceof CubeMobMoveControl;
      }

      public void tick() {
         if (--this.nextRandomizeTime <= 0) {
            this.nextRandomizeTime = this.adjustedTickDelay(40 + this.cubeMob.getRandom().nextInt(60));
            this.chosenDegrees = (float)this.cubeMob.getRandom().nextInt(360);
         }

         MoveControl var2 = this.cubeMob.getMoveControl();
         if (var2 instanceof CubeMobMoveControl cubeMobMoveControl) {
            cubeMobMoveControl.setDirection(this.chosenDegrees, false);
         }

      }
   }

   private static class CubeMobFloatGoal extends Goal {
      private final AbstractCubeMob cubeMob;

      public CubeMobFloatGoal(final AbstractCubeMob mob) {
         super();
         this.cubeMob = mob;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
         mob.getNavigation().setCanFloat(true);
      }

      public boolean canUse() {
         return (this.cubeMob.isInWater() || this.cubeMob.isInLava()) && this.cubeMob.getMoveControl() instanceof CubeMobMoveControl;
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (this.cubeMob.getRandom().nextFloat() < 0.8F) {
            this.cubeMob.getJumpControl().jump();
         }

         MoveControl var2 = this.cubeMob.getMoveControl();
         if (var2 instanceof CubeMobMoveControl cubeMobMoveControl) {
            cubeMobMoveControl.setWantedMovement(1.2);
         }

      }
   }

   private static class CubeMobKeepOnJumpingGoal extends Goal {
      private final AbstractCubeMob cubeMob;

      public CubeMobKeepOnJumpingGoal(final AbstractCubeMob mob) {
         super();
         this.cubeMob = mob;
         this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !this.cubeMob.isPassenger();
      }

      public void tick() {
         MoveControl var2 = this.cubeMob.getMoveControl();
         if (var2 instanceof CubeMobMoveControl cubeMobMoveControl) {
            cubeMobMoveControl.setWantedMovement(1.0);
         }

      }
   }
}
