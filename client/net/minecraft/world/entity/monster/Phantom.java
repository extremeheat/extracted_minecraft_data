package net.minecraft.world.entity.monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Phantom extends Mob implements Enemy {
   public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
   public static final int TICKS_PER_FLAP = Mth.ceil(24.166098F);
   private static final EntityDataAccessor<Integer> ID_SIZE;
   private Vec3 moveTargetPoint;
   private @Nullable BlockPos anchorPoint;
   private AttackPhase attackPhase;

   public Phantom(final EntityType<? extends Phantom> type, final Level level) {
      super(type, level);
      this.moveTargetPoint = Vec3.ZERO;
      this.attackPhase = Phantom.AttackPhase.CIRCLE;
      this.xpReward = 5;
      this.moveControl = new PhantomMoveControl(this);
      this.lookControl = new PhantomLookControl(this);
   }

   public boolean isFlapping() {
      return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
   }

   protected BodyRotationControl createBodyControl() {
      return new PhantomBodyRotationControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new PhantomAttackStrategyGoal());
      this.goalSelector.addGoal(2, new PhantomSweepAttackGoal());
      this.goalSelector.addGoal(3, new PhantomCircleAroundAnchorGoal());
      this.targetSelector.addGoal(1, new PhantomAttackPlayerTargetGoal());
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(ID_SIZE, 0);
   }

   public void setPhantomSize(final int size) {
      this.entityData.set(ID_SIZE, Mth.clamp(size, 0, 64));
   }

   private void updatePhantomSizeInfo() {
      this.refreshDimensions();
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)(6 + this.getPhantomSize()));
   }

   public int getPhantomSize() {
      return (Integer)this.entityData.get(ID_SIZE);
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (ID_SIZE.equals(accessor)) {
         this.updatePhantomSizeInfo();
      }

      super.onSyncedDataUpdated(accessor);
   }

   public int getUniqueFlapTickOffset() {
      return this.getId() * 3;
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         float anim = Mth.cos((double)((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * 0.017453292F + 3.1415927F));
         float nextAnim = Mth.cos((double)((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * 0.017453292F + 3.1415927F));
         if (anim > 0.0F && nextAnim <= 0.0F) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
         }

         float width = this.getBbWidth() * 1.48F;
         float c = Mth.cos((double)(this.getYRot() * 0.017453292F)) * width;
         float s = Mth.sin((double)(this.getYRot() * 0.017453292F)) * width;
         float h = (0.3F + anim * 0.45F) * this.getBbHeight() * 2.5F;
         this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)c, this.getY() + (double)h, this.getZ() + (double)s, 0.0, 0.0, 0.0);
         this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)c, this.getY() + (double)h, this.getZ() - (double)s, 0.0, 0.0, 0.0);
      }

   }

   protected void checkFallDamage(final double ya, final boolean onGround, final BlockState onState, final BlockPos pos) {
   }

   public boolean onClimbable() {
      return false;
   }

   public void travel(final Vec3 input) {
      this.travelFlying(input, 0.2F);
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      this.anchorPoint = this.blockPosition().above(5);
      this.setPhantomSize(0);
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.anchorPoint = (BlockPos)input.read("anchor_pos", BlockPos.CODEC).orElse((Object)null);
      this.setPhantomSize(input.getIntOr("size", 0));
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.storeNullable("anchor_pos", BlockPos.CODEC, this.anchorPoint);
      output.putInt("size", this.getPhantomSize());
   }

   public boolean shouldRenderAtSqrDistance(final double distance) {
      return true;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PHANTOM_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.PHANTOM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PHANTOM_DEATH;
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      int size = this.getPhantomSize();
      EntityDimensions originalDimensions = super.getDefaultDimensions(pose);
      return originalDimensions.scale(1.0F + 0.15F * (float)size);
   }

   private boolean canAttack(final ServerLevel level, final LivingEntity target, final TargetingConditions targetConditions) {
      return targetConditions.test(level, this, target);
   }

   static {
      ID_SIZE = SynchedEntityData.<Integer>defineId(Phantom.class, EntityDataSerializers.INT);
   }

   private static enum AttackPhase {
      CIRCLE,
      SWOOP;

      private AttackPhase() {
      }

      // $FF: synthetic method
      private static AttackPhase[] $values() {
         return new AttackPhase[]{CIRCLE, SWOOP};
      }
   }

   private class PhantomMoveControl extends MoveControl {
      private float speed;

      public PhantomMoveControl(final Mob mob) {
         Objects.requireNonNull(Phantom.this);
         super(mob);
         this.speed = 0.1F;
      }

      public void tick() {
         if (Phantom.this.horizontalCollision) {
            Phantom.this.setYRot(Phantom.this.getYRot() + 180.0F);
            this.speed = 0.1F;
         }

         double tdx = Phantom.this.moveTargetPoint.x - Phantom.this.getX();
         double tdy = Phantom.this.moveTargetPoint.y - Phantom.this.getY();
         double tdz = Phantom.this.moveTargetPoint.z - Phantom.this.getZ();
         double sd = Math.sqrt(tdx * tdx + tdz * tdz);
         if (Math.abs(sd) > 9.999999747378752E-6) {
            double yRelativeScale = 1.0 - Math.abs(tdy * 0.699999988079071) / sd;
            tdx *= yRelativeScale;
            tdz *= yRelativeScale;
            sd = Math.sqrt(tdx * tdx + tdz * tdz);
            double sd2 = Math.sqrt(tdx * tdx + tdz * tdz + tdy * tdy);
            float prev = Phantom.this.getYRot();
            float angle = (float)Mth.atan2(tdz, tdx);
            float a = Mth.wrapDegrees(Phantom.this.getYRot() + 90.0F);
            float b = Mth.wrapDegrees(angle * 57.295776F);
            Phantom.this.setYRot(Mth.approachDegrees(a, b, 4.0F) - 90.0F);
            Phantom.this.yBodyRot = Phantom.this.getYRot();
            if (Mth.degreesDifferenceAbs(prev, Phantom.this.getYRot()) < 3.0F) {
               this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
            } else {
               this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
            }

            float xRotD = (float)(-(Mth.atan2(-tdy, sd) * 57.2957763671875));
            Phantom.this.setXRot(xRotD);
            float moveAngle = Phantom.this.getYRot() + 90.0F;
            double txd = (double)(this.speed * Mth.cos((double)(moveAngle * 0.017453292F))) * Math.abs(tdx / sd2);
            double tzd = (double)(this.speed * Mth.sin((double)(moveAngle * 0.017453292F))) * Math.abs(tdz / sd2);
            double tyd = (double)(this.speed * Mth.sin((double)(xRotD * 0.017453292F))) * Math.abs(tdy / sd2);
            Vec3 movement = Phantom.this.getDeltaMovement();
            Phantom.this.setDeltaMovement(movement.add((new Vec3(txd, tyd, tzd)).subtract(movement).scale(0.2)));
         }

      }
   }

   private class PhantomBodyRotationControl extends BodyRotationControl {
      public PhantomBodyRotationControl(final Mob mob) {
         Objects.requireNonNull(Phantom.this);
         super(mob);
      }

      public void clientTick() {
         Phantom.this.yHeadRot = Phantom.this.yBodyRot;
         Phantom.this.yBodyRot = Phantom.this.getYRot();
      }
   }

   private static class PhantomLookControl extends LookControl {
      public PhantomLookControl(final Mob mob) {
         super(mob);
      }

      public void tick() {
      }
   }

   private abstract class PhantomMoveTargetGoal extends Goal {
      public PhantomMoveTargetGoal() {
         Objects.requireNonNull(Phantom.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      protected boolean touchingTarget() {
         return Phantom.this.moveTargetPoint.distanceToSqr(Phantom.this.getX(), Phantom.this.getY(), Phantom.this.getZ()) < 4.0;
      }
   }

   private class PhantomCircleAroundAnchorGoal extends PhantomMoveTargetGoal {
      private float angle;
      private float distance;
      private float height;
      private float clockwise;

      private PhantomCircleAroundAnchorGoal() {
         Objects.requireNonNull(Phantom.this);
         super();
      }

      public boolean canUse() {
         return Phantom.this.getTarget() == null || Phantom.this.attackPhase == Phantom.AttackPhase.CIRCLE;
      }

      public void start() {
         this.distance = 5.0F + Phantom.this.random.nextFloat() * 10.0F;
         this.height = -4.0F + Phantom.this.random.nextFloat() * 9.0F;
         this.clockwise = Phantom.this.random.nextBoolean() ? 1.0F : -1.0F;
         this.selectNext();
      }

      public void tick() {
         if (Phantom.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
            this.height = -4.0F + Phantom.this.random.nextFloat() * 9.0F;
         }

         if (Phantom.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
            ++this.distance;
            if (this.distance > 15.0F) {
               this.distance = 5.0F;
               this.clockwise = -this.clockwise;
            }
         }

         if (Phantom.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
            this.angle = Phantom.this.random.nextFloat() * 2.0F * 3.1415927F;
            this.selectNext();
         }

         if (this.touchingTarget()) {
            this.selectNext();
         }

         if (Phantom.this.moveTargetPoint.y < Phantom.this.getY() && !Phantom.this.level().isEmptyBlock(Phantom.this.blockPosition().below(1))) {
            this.height = Math.max(1.0F, this.height);
            this.selectNext();
         }

         if (Phantom.this.moveTargetPoint.y > Phantom.this.getY() && !Phantom.this.level().isEmptyBlock(Phantom.this.blockPosition().above(1))) {
            this.height = Math.min(-1.0F, this.height);
            this.selectNext();
         }

      }

      private void selectNext() {
         if (Phantom.this.anchorPoint == null) {
            Phantom.this.anchorPoint = Phantom.this.blockPosition();
         }

         this.angle += this.clockwise * 15.0F * 0.017453292F;
         Phantom.this.moveTargetPoint = Vec3.atLowerCornerOf(Phantom.this.anchorPoint).add((double)(this.distance * Mth.cos((double)this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin((double)this.angle)));
      }
   }

   private class PhantomSweepAttackGoal extends PhantomMoveTargetGoal {
      private static final int CAT_SEARCH_TICK_DELAY = 20;
      private boolean isScaredOfCat;
      private int catSearchTick;

      private PhantomSweepAttackGoal() {
         Objects.requireNonNull(Phantom.this);
         super();
      }

      public boolean canUse() {
         return Phantom.this.getTarget() != null && Phantom.this.attackPhase == Phantom.AttackPhase.SWOOP;
      }

      public boolean canContinueToUse() {
         LivingEntity target = Phantom.this.getTarget();
         if (target == null) {
            return false;
         } else if (!target.isAlive()) {
            return false;
         } else {
            if (target instanceof Player) {
               Player player = (Player)target;
               if (target.isSpectator() || player.isCreative()) {
                  return false;
               }
            }

            if (!this.canUse()) {
               return false;
            } else {
               if (Phantom.this.tickCount > this.catSearchTick) {
                  this.catSearchTick = Phantom.this.tickCount + 20;
                  List<Cat> cats = Phantom.this.level().getEntitiesOfClass(Cat.class, Phantom.this.getBoundingBox().inflate(16.0), EntitySelector.ENTITY_STILL_ALIVE);

                  for(Cat cat : cats) {
                     cat.hiss();
                  }

                  this.isScaredOfCat = !cats.isEmpty();
               }

               return !this.isScaredOfCat;
            }
         }
      }

      public void stop() {
         Phantom.this.setTarget((LivingEntity)null);
         Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
      }

      public void tick() {
         LivingEntity target = Phantom.this.getTarget();
         if (target != null) {
            Phantom.this.moveTargetPoint = new Vec3(target.getX(), target.getY(0.5), target.getZ());
            if (Phantom.this.getBoundingBox().inflate(0.20000000298023224).intersects(target.getBoundingBox())) {
               Phantom.this.doHurtTarget(getServerLevel(Phantom.this.level()), target);
               Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
               if (!Phantom.this.isSilent()) {
                  Phantom.this.level().levelEvent(1039, Phantom.this.blockPosition(), 0);
               }
            } else if (Phantom.this.horizontalCollision || Phantom.this.hurtTime > 0) {
               Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
            }

         }
      }
   }

   private class PhantomAttackStrategyGoal extends Goal {
      private int nextSweepTick;

      private PhantomAttackStrategyGoal() {
         Objects.requireNonNull(Phantom.this);
         super();
      }

      public boolean canUse() {
         LivingEntity target = Phantom.this.getTarget();
         return target != null ? Phantom.this.canAttack(getServerLevel(Phantom.this.level()), target, TargetingConditions.DEFAULT) : false;
      }

      public void start() {
         this.nextSweepTick = this.adjustedTickDelay(10);
         Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
         this.setAnchorAboveTarget();
      }

      public void stop() {
         if (Phantom.this.anchorPoint != null) {
            Phantom.this.anchorPoint = Phantom.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, Phantom.this.anchorPoint).above(10 + Phantom.this.random.nextInt(20));
         }

      }

      public void tick() {
         if (Phantom.this.attackPhase == Phantom.AttackPhase.CIRCLE) {
            --this.nextSweepTick;
            if (this.nextSweepTick <= 0) {
               Phantom.this.attackPhase = Phantom.AttackPhase.SWOOP;
               this.setAnchorAboveTarget();
               this.nextSweepTick = this.adjustedTickDelay((8 + Phantom.this.random.nextInt(4)) * 20);
               Phantom.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + Phantom.this.random.nextFloat() * 0.1F);
            }
         }

      }

      private void setAnchorAboveTarget() {
         if (Phantom.this.anchorPoint != null) {
            Phantom.this.anchorPoint = Phantom.this.getTarget().blockPosition().above(20 + Phantom.this.random.nextInt(20));
            if (Phantom.this.anchorPoint.getY() < Phantom.this.level().getSeaLevel()) {
               Phantom.this.anchorPoint = new BlockPos(Phantom.this.anchorPoint.getX(), Phantom.this.level().getSeaLevel() + 1, Phantom.this.anchorPoint.getZ());
            }

         }
      }
   }

   private class PhantomAttackPlayerTargetGoal extends Goal {
      private final TargetingConditions attackTargeting;
      private int nextScanTick;

      private PhantomAttackPlayerTargetGoal() {
         Objects.requireNonNull(Phantom.this);
         super();
         this.attackTargeting = TargetingConditions.forCombat().range(64.0);
         this.nextScanTick = reducedTickDelay(20);
      }

      public boolean canUse() {
         if (this.nextScanTick > 0) {
            --this.nextScanTick;
            return false;
         } else {
            this.nextScanTick = reducedTickDelay(60);
            ServerLevel level = getServerLevel(Phantom.this.level());
            List<Player> players = level.getNearbyPlayers(this.attackTargeting, Phantom.this, Phantom.this.getBoundingBox().inflate(16.0, 64.0, 16.0));
            if (!players.isEmpty()) {
               players.sort(Comparator.comparing(Entity::getY).reversed());

               for(Player player : players) {
                  if (Phantom.this.canAttack(level, player, TargetingConditions.DEFAULT)) {
                     Phantom.this.setTarget(player);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity target = Phantom.this.getTarget();
         return target != null ? Phantom.this.canAttack(getServerLevel(Phantom.this.level()), target, TargetingConditions.DEFAULT) : false;
      }
   }
}
