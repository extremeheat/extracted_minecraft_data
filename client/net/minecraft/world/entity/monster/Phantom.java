package net.minecraft.world.entity.monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.FlyingMob;
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
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Phantom extends FlyingMob implements Enemy {
   public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
   public static final int TICKS_PER_FLAP = Mth.ceil(24.166098F);
   private static final EntityDataAccessor<Integer> ID_SIZE;
   Vec3 moveTargetPoint;
   BlockPos anchorPoint;
   AttackPhase attackPhase;

   public Phantom(EntityType<? extends Phantom> var1, Level var2) {
      super(var1, var2);
      this.moveTargetPoint = Vec3.ZERO;
      this.anchorPoint = BlockPos.ZERO;
      this.attackPhase = Phantom.AttackPhase.CIRCLE;
      this.xpReward = 5;
      this.moveControl = new PhantomMoveControl(this);
      this.lookControl = new PhantomLookControl(this, this);
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

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(ID_SIZE, 0);
   }

   public void setPhantomSize(int var1) {
      this.entityData.set(ID_SIZE, Mth.clamp(var1, 0, 64));
   }

   private void updatePhantomSizeInfo() {
      this.refreshDimensions();
      this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)(6 + this.getPhantomSize()));
   }

   public int getPhantomSize() {
      return (Integer)this.entityData.get(ID_SIZE);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (ID_SIZE.equals(var1)) {
         this.updatePhantomSizeInfo();
      }

      super.onSyncedDataUpdated(var1);
   }

   public int getUniqueFlapTickOffset() {
      return this.getId() * 3;
   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         float var1 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * 0.017453292F + 3.1415927F);
         float var2 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * 0.017453292F + 3.1415927F);
         if (var1 > 0.0F && var2 <= 0.0F) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
         }

         float var3 = this.getBbWidth() * 1.48F;
         float var4 = Mth.cos(this.getYRot() * 0.017453292F) * var3;
         float var5 = Mth.sin(this.getYRot() * 0.017453292F) * var3;
         float var6 = (0.3F + var1 * 0.45F) * this.getBbHeight() * 2.5F;
         this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)var4, this.getY() + (double)var6, this.getZ() + (double)var5, 0.0, 0.0, 0.0);
         this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)var4, this.getY() + (double)var6, this.getZ() - (double)var5, 0.0, 0.0, 0.0);
      }

   }

   public void aiStep() {
      if (this.isAlive() && this.isSunBurnTick()) {
         this.igniteForSeconds(8.0F);
      }

      super.aiStep();
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      this.anchorPoint = this.blockPosition().above(5);
      this.setPhantomSize(0);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("AX")) {
         this.anchorPoint = new BlockPos(var1.getInt("AX"), var1.getInt("AY"), var1.getInt("AZ"));
      }

      this.setPhantomSize(var1.getInt("Size"));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("AX", this.anchorPoint.getX());
      var1.putInt("AY", this.anchorPoint.getY());
      var1.putInt("AZ", this.anchorPoint.getZ());
      var1.putInt("Size", this.getPhantomSize());
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      return true;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PHANTOM_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PHANTOM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PHANTOM_DEATH;
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public boolean canAttackType(EntityType<?> var1) {
      return true;
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      int var2 = this.getPhantomSize();
      EntityDimensions var3 = super.getDefaultDimensions(var1);
      return var3.scale(1.0F + 0.15F * (float)var2);
   }

   boolean canAttack(ServerLevel var1, LivingEntity var2, TargetingConditions var3) {
      return var3.test(var1, this, var2);
   }

   static {
      ID_SIZE = SynchedEntityData.defineId(Phantom.class, EntityDataSerializers.INT);
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

   class PhantomMoveControl extends MoveControl {
      private float speed = 0.1F;

      public PhantomMoveControl(final Mob var2) {
         super(var2);
      }

      public void tick() {
         if (Phantom.this.horizontalCollision) {
            Phantom.this.setYRot(Phantom.this.getYRot() + 180.0F);
            this.speed = 0.1F;
         }

         double var1 = Phantom.this.moveTargetPoint.x - Phantom.this.getX();
         double var3 = Phantom.this.moveTargetPoint.y - Phantom.this.getY();
         double var5 = Phantom.this.moveTargetPoint.z - Phantom.this.getZ();
         double var7 = Math.sqrt(var1 * var1 + var5 * var5);
         if (Math.abs(var7) > 9.999999747378752E-6) {
            double var9 = 1.0 - Math.abs(var3 * 0.699999988079071) / var7;
            var1 *= var9;
            var5 *= var9;
            var7 = Math.sqrt(var1 * var1 + var5 * var5);
            double var11 = Math.sqrt(var1 * var1 + var5 * var5 + var3 * var3);
            float var13 = Phantom.this.getYRot();
            float var14 = (float)Mth.atan2(var5, var1);
            float var15 = Mth.wrapDegrees(Phantom.this.getYRot() + 90.0F);
            float var16 = Mth.wrapDegrees(var14 * 57.295776F);
            Phantom.this.setYRot(Mth.approachDegrees(var15, var16, 4.0F) - 90.0F);
            Phantom.this.yBodyRot = Phantom.this.getYRot();
            if (Mth.degreesDifferenceAbs(var13, Phantom.this.getYRot()) < 3.0F) {
               this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
            } else {
               this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
            }

            float var17 = (float)(-(Mth.atan2(-var3, var7) * 57.2957763671875));
            Phantom.this.setXRot(var17);
            float var18 = Phantom.this.getYRot() + 90.0F;
            double var19 = (double)(this.speed * Mth.cos(var18 * 0.017453292F)) * Math.abs(var1 / var11);
            double var21 = (double)(this.speed * Mth.sin(var18 * 0.017453292F)) * Math.abs(var5 / var11);
            double var23 = (double)(this.speed * Mth.sin(var17 * 0.017453292F)) * Math.abs(var3 / var11);
            Vec3 var25 = Phantom.this.getDeltaMovement();
            Phantom.this.setDeltaMovement(var25.add((new Vec3(var19, var23, var21)).subtract(var25).scale(0.2)));
         }

      }
   }

   class PhantomLookControl extends LookControl {
      public PhantomLookControl(final Phantom var1, final Mob var2) {
         super(var2);
      }

      public void tick() {
      }
   }

   class PhantomBodyRotationControl extends BodyRotationControl {
      public PhantomBodyRotationControl(final Mob var2) {
         super(var2);
      }

      public void clientTick() {
         Phantom.this.yHeadRot = Phantom.this.yBodyRot;
         Phantom.this.yBodyRot = Phantom.this.getYRot();
      }
   }

   class PhantomAttackStrategyGoal extends Goal {
      private int nextSweepTick;

      PhantomAttackStrategyGoal() {
         super();
      }

      public boolean canUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         return var1 != null ? Phantom.this.canAttack(getServerLevel(Phantom.this.level()), var1, TargetingConditions.DEFAULT) : false;
      }

      public void start() {
         this.nextSweepTick = this.adjustedTickDelay(10);
         Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
         this.setAnchorAboveTarget();
      }

      public void stop() {
         Phantom.this.anchorPoint = Phantom.this.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, Phantom.this.anchorPoint).above(10 + Phantom.this.random.nextInt(20));
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
         Phantom.this.anchorPoint = Phantom.this.getTarget().blockPosition().above(20 + Phantom.this.random.nextInt(20));
         if (Phantom.this.anchorPoint.getY() < Phantom.this.level().getSeaLevel()) {
            Phantom.this.anchorPoint = new BlockPos(Phantom.this.anchorPoint.getX(), Phantom.this.level().getSeaLevel() + 1, Phantom.this.anchorPoint.getZ());
         }

      }
   }

   class PhantomSweepAttackGoal extends PhantomMoveTargetGoal {
      private static final int CAT_SEARCH_TICK_DELAY = 20;
      private boolean isScaredOfCat;
      private int catSearchTick;

      PhantomSweepAttackGoal() {
         super();
      }

      public boolean canUse() {
         return Phantom.this.getTarget() != null && Phantom.this.attackPhase == Phantom.AttackPhase.SWOOP;
      }

      public boolean canContinueToUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         if (var1 == null) {
            return false;
         } else if (!var1.isAlive()) {
            return false;
         } else {
            if (var1 instanceof Player) {
               Player var2 = (Player)var1;
               if (var1.isSpectator() || var2.isCreative()) {
                  return false;
               }
            }

            if (!this.canUse()) {
               return false;
            } else {
               if (Phantom.this.tickCount > this.catSearchTick) {
                  this.catSearchTick = Phantom.this.tickCount + 20;
                  List var5 = Phantom.this.level().getEntitiesOfClass(Cat.class, Phantom.this.getBoundingBox().inflate(16.0), EntitySelector.ENTITY_STILL_ALIVE);
                  Iterator var3 = var5.iterator();

                  while(var3.hasNext()) {
                     Cat var4 = (Cat)var3.next();
                     var4.hiss();
                  }

                  this.isScaredOfCat = !var5.isEmpty();
               }

               return !this.isScaredOfCat;
            }
         }
      }

      public void start() {
      }

      public void stop() {
         Phantom.this.setTarget((LivingEntity)null);
         Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
      }

      public void tick() {
         LivingEntity var1 = Phantom.this.getTarget();
         if (var1 != null) {
            Phantom.this.moveTargetPoint = new Vec3(var1.getX(), var1.getY(0.5), var1.getZ());
            if (Phantom.this.getBoundingBox().inflate(0.20000000298023224).intersects(var1.getBoundingBox())) {
               Phantom.this.doHurtTarget(getServerLevel(Phantom.this.level()), var1);
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

   class PhantomCircleAroundAnchorGoal extends PhantomMoveTargetGoal {
      private float angle;
      private float distance;
      private float height;
      private float clockwise;

      PhantomCircleAroundAnchorGoal() {
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
         if (BlockPos.ZERO.equals(Phantom.this.anchorPoint)) {
            Phantom.this.anchorPoint = Phantom.this.blockPosition();
         }

         this.angle += this.clockwise * 15.0F * 0.017453292F;
         Phantom.this.moveTargetPoint = Vec3.atLowerCornerOf(Phantom.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
      }
   }

   class PhantomAttackPlayerTargetGoal extends Goal {
      private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);
      private int nextScanTick = reducedTickDelay(20);

      PhantomAttackPlayerTargetGoal() {
         super();
      }

      public boolean canUse() {
         if (this.nextScanTick > 0) {
            --this.nextScanTick;
            return false;
         } else {
            this.nextScanTick = reducedTickDelay(60);
            ServerLevel var1 = getServerLevel(Phantom.this.level());
            List var2 = var1.getNearbyPlayers(this.attackTargeting, Phantom.this, Phantom.this.getBoundingBox().inflate(16.0, 64.0, 16.0));
            if (!var2.isEmpty()) {
               var2.sort(Comparator.comparing(Entity::getY).reversed());
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  Player var4 = (Player)var3.next();
                  if (Phantom.this.canAttack(var1, var4, TargetingConditions.DEFAULT)) {
                     Phantom.this.setTarget(var4);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         return var1 != null ? Phantom.this.canAttack(getServerLevel(Phantom.this.level()), var1, TargetingConditions.DEFAULT) : false;
      }
   }

   abstract class PhantomMoveTargetGoal extends Goal {
      public PhantomMoveTargetGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      protected boolean touchingTarget() {
         return Phantom.this.moveTargetPoint.distanceToSqr(Phantom.this.getX(), Phantom.this.getY(), Phantom.this.getZ()) < 4.0;
      }
   }
}
