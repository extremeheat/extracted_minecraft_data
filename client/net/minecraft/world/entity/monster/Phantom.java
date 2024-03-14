package net.minecraft.world.entity.monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
   private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Phantom.class, EntityDataSerializers.INT);
   Vec3 moveTargetPoint = Vec3.ZERO;
   BlockPos anchorPoint = BlockPos.ZERO;
   Phantom.AttackPhase attackPhase = Phantom.AttackPhase.CIRCLE;

   public Phantom(EntityType<? extends Phantom> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
      this.moveControl = new Phantom.PhantomMoveControl(this);
      this.lookControl = new Phantom.PhantomLookControl(this);
   }

   @Override
   public boolean isFlapping() {
      return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
   }

   @Override
   protected BodyRotationControl createBodyControl() {
      return new Phantom.PhantomBodyRotationControl(this);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new Phantom.PhantomAttackStrategyGoal());
      this.goalSelector.addGoal(2, new Phantom.PhantomSweepAttackGoal());
      this.goalSelector.addGoal(3, new Phantom.PhantomCircleAroundAnchorGoal());
      this.targetSelector.addGoal(1, new Phantom.PhantomAttackPlayerTargetGoal());
   }

   @Override
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
      return this.entityData.get(ID_SIZE);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (ID_SIZE.equals(var1)) {
         this.updatePhantomSizeInfo();
      }

      super.onSyncedDataUpdated(var1);
   }

   public int getUniqueFlapTickOffset() {
      return this.getId() * 3;
   }

   @Override
   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         float var1 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * 0.017453292F + 3.1415927F);
         float var2 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * 0.017453292F + 3.1415927F);
         if (var1 > 0.0F && var2 <= 0.0F) {
            this.level()
               .playLocalSound(
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  SoundEvents.PHANTOM_FLAP,
                  this.getSoundSource(),
                  0.95F + this.random.nextFloat() * 0.05F,
                  0.95F + this.random.nextFloat() * 0.05F,
                  false
               );
         }

         float var3 = this.getBbWidth() * 1.48F;
         float var4 = Mth.cos(this.getYRot() * 0.017453292F) * var3;
         float var5 = Mth.sin(this.getYRot() * 0.017453292F) * var3;
         float var6 = (0.3F + var1 * 0.45F) * this.getBbHeight() * 2.5F;
         this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)var4, this.getY() + (double)var6, this.getZ() + (double)var5, 0.0, 0.0, 0.0);
         this.level().addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)var4, this.getY() + (double)var6, this.getZ() - (double)var5, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public void aiStep() {
      if (this.isAlive() && this.isSunBurnTick()) {
         this.igniteForSeconds(8);
      }

      super.aiStep();
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
   }

   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      this.anchorPoint = this.blockPosition().above(5);
      this.setPhantomSize(0);
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("AX")) {
         this.anchorPoint = new BlockPos(var1.getInt("AX"), var1.getInt("AY"), var1.getInt("AZ"));
      }

      this.setPhantomSize(var1.getInt("Size"));
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("AX", this.anchorPoint.getX());
      var1.putInt("AY", this.anchorPoint.getY());
      var1.putInt("AZ", this.anchorPoint.getZ());
      var1.putInt("Size", this.getPhantomSize());
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      return true;
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.PHANTOM_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PHANTOM_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.PHANTOM_DEATH;
   }

   @Override
   protected float getSoundVolume() {
      return 1.0F;
   }

   @Override
   public boolean canAttackType(EntityType<?> var1) {
      return true;
   }

   @Override
   public EntityDimensions getDefaultDimensions(Pose var1) {
      int var2 = this.getPhantomSize();
      EntityDimensions var3 = super.getDefaultDimensions(var1);
      return var3.scale(1.0F + 0.15F * (float)var2);
   }

   static enum AttackPhase {
      CIRCLE,
      SWOOP;

      private AttackPhase() {
      }
   }

   class PhantomAttackPlayerTargetGoal extends Goal {
      private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);
      private int nextScanTick = reducedTickDelay(20);

      PhantomAttackPlayerTargetGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         if (this.nextScanTick > 0) {
            --this.nextScanTick;
            return false;
         } else {
            this.nextScanTick = reducedTickDelay(60);
            List var1 = Phantom.this.level().getNearbyPlayers(this.attackTargeting, Phantom.this, Phantom.this.getBoundingBox().inflate(16.0, 64.0, 16.0));
            if (!var1.isEmpty()) {
               var1.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

               for(Player var3 : var1) {
                  if (Phantom.this.canAttack(var3, TargetingConditions.DEFAULT)) {
                     Phantom.this.setTarget(var3);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      @Override
      public boolean canContinueToUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         return var1 != null ? Phantom.this.canAttack(var1, TargetingConditions.DEFAULT) : false;
      }
   }

   class PhantomAttackStrategyGoal extends Goal {
      private int nextSweepTick;

      PhantomAttackStrategyGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         return var1 != null ? Phantom.this.canAttack(var1, TargetingConditions.DEFAULT) : false;
      }

      @Override
      public void start() {
         this.nextSweepTick = this.adjustedTickDelay(10);
         Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
         this.setAnchorAboveTarget();
      }

      @Override
      public void stop() {
         Phantom.this.anchorPoint = Phantom.this.level()
            .getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, Phantom.this.anchorPoint)
            .above(10 + Phantom.this.random.nextInt(20));
      }

      @Override
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

   class PhantomBodyRotationControl extends BodyRotationControl {
      public PhantomBodyRotationControl(Mob var2) {
         super(var2);
      }

      @Override
      public void clientTick() {
         Phantom.this.yHeadRot = Phantom.this.yBodyRot;
         Phantom.this.yBodyRot = Phantom.this.getYRot();
      }
   }

   class PhantomCircleAroundAnchorGoal extends Phantom.PhantomMoveTargetGoal {
      private float angle;
      private float distance;
      private float height;
      private float clockwise;

      PhantomCircleAroundAnchorGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         return Phantom.this.getTarget() == null || Phantom.this.attackPhase == Phantom.AttackPhase.CIRCLE;
      }

      @Override
      public void start() {
         this.distance = 5.0F + Phantom.this.random.nextFloat() * 10.0F;
         this.height = -4.0F + Phantom.this.random.nextFloat() * 9.0F;
         this.clockwise = Phantom.this.random.nextBoolean() ? 1.0F : -1.0F;
         this.selectNext();
      }

      @Override
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
         Phantom.this.moveTargetPoint = Vec3.atLowerCornerOf(Phantom.this.anchorPoint)
            .add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
      }
   }

   class PhantomLookControl extends LookControl {
      public PhantomLookControl(Mob var2) {
         super(var2);
      }

      @Override
      public void tick() {
      }
   }

   class PhantomMoveControl extends MoveControl {
      private float speed = 0.1F;

      public PhantomMoveControl(Mob var2) {
         super(var2);
      }

      @Override
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
            Phantom.this.setDeltaMovement(var25.add(new Vec3(var19, var23, var21).subtract(var25).scale(0.2)));
         }
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

   class PhantomSweepAttackGoal extends Phantom.PhantomMoveTargetGoal {
      private static final int CAT_SEARCH_TICK_DELAY = 20;
      private boolean isScaredOfCat;
      private int catSearchTick;

      PhantomSweepAttackGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         return Phantom.this.getTarget() != null && Phantom.this.attackPhase == Phantom.AttackPhase.SWOOP;
      }

      @Override
      public boolean canContinueToUse() {
         LivingEntity var1 = Phantom.this.getTarget();
         if (var1 == null) {
            return false;
         } else if (!var1.isAlive()) {
            return false;
         } else {
            if (var1 instanceof Player var2 && (var1.isSpectator() || var2.isCreative())) {
               return false;
            }

            if (!this.canUse()) {
               return false;
            } else {
               if (Phantom.this.tickCount > this.catSearchTick) {
                  this.catSearchTick = Phantom.this.tickCount + 20;
                  List var5 = Phantom.this.level()
                     .getEntitiesOfClass(Cat.class, Phantom.this.getBoundingBox().inflate(16.0), EntitySelector.ENTITY_STILL_ALIVE);

                  for(Cat var4 : var5) {
                     var4.hiss();
                  }

                  this.isScaredOfCat = !var5.isEmpty();
               }

               return !this.isScaredOfCat;
            }
         }
      }

      @Override
      public void start() {
      }

      @Override
      public void stop() {
         Phantom.this.setTarget(null);
         Phantom.this.attackPhase = Phantom.AttackPhase.CIRCLE;
      }

      @Override
      public void tick() {
         LivingEntity var1 = Phantom.this.getTarget();
         if (var1 != null) {
            Phantom.this.moveTargetPoint = new Vec3(var1.getX(), var1.getY(0.5), var1.getZ());
            if (Phantom.this.getBoundingBox().inflate(0.20000000298023224).intersects(var1.getBoundingBox())) {
               Phantom.this.doHurtTarget(var1);
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
}
