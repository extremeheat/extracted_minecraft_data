package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Vex extends Monster implements TraceableEntity {
   public static final float FLAP_DEGREES_PER_TICK = 45.836624F;
   public static final int TICKS_PER_FLAP = Mth.ceil(3.9269907F);
   protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
   private static final int FLAG_IS_CHARGING = 1;
   private @Nullable EntityReference<Mob> owner;
   private @Nullable BlockPos boundOrigin;
   private boolean hasLimitedLife;
   private int limitedLifeTicks;

   public Vex(final EntityType<? extends Vex> type, final Level level) {
      super(type, level);
      this.moveControl = new VexMoveControl(this);
      this.xpReward = 3;
   }

   public boolean isFlapping() {
      return this.tickCount % TICKS_PER_FLAP == 0;
   }

   protected boolean isAffectedByBlocks() {
      return !this.isRemoved();
   }

   public void tick() {
      this.noPhysics = true;
      super.tick();
      this.noPhysics = false;
      this.setNoGravity(true);
      if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
         this.limitedLifeTicks = 20;
         this.hurt(this.damageSources().starve(), 1.0F);
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(4, new VexChargeAttackGoal());
      this.goalSelector.addGoal(8, new VexRandomMoveGoal());
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers());
      this.targetSelector.addGoal(2, new VexCopyOwnerTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.ATTACK_DAMAGE, 4.0);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.boundOrigin = (BlockPos)input.read("bound_pos", BlockPos.CODEC).orElse((Object)null);
      input.getInt("life_ticks").ifPresentOrElse(this::setLimitedLife, () -> this.hasLimitedLife = false);
      this.owner = EntityReference.<Mob>read(input, "owner");
   }

   public void restoreFrom(final Entity oldEntity) {
      super.restoreFrom(oldEntity);
      if (oldEntity instanceof Vex vex) {
         this.owner = vex.owner;
      }

   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.storeNullable("bound_pos", BlockPos.CODEC, this.boundOrigin);
      if (this.hasLimitedLife) {
         output.putInt("life_ticks", this.limitedLifeTicks);
      }

      EntityReference.store(this.owner, output, "owner");
   }

   public @Nullable Mob getOwner() {
      return (Mob)EntityReference.get(this.owner, this.level(), Mob.class);
   }

   public @Nullable BlockPos getBoundOrigin() {
      return this.boundOrigin;
   }

   public void setBoundOrigin(final @Nullable BlockPos boundOrigin) {
      this.boundOrigin = boundOrigin;
   }

   private boolean getVexFlag(final int flag) {
      int flags = (Byte)this.entityData.get(DATA_FLAGS_ID);
      return (flags & flag) != 0;
   }

   private void setVexFlag(final int flag, final boolean value) {
      int flags = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (value) {
         flags |= flag;
      } else {
         flags &= ~flag;
      }

      this.entityData.set(DATA_FLAGS_ID, (byte)(flags & 255));
   }

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setIsCharging(final boolean value) {
      this.setVexFlag(1, value);
   }

   public void setOwner(final Mob owner) {
      this.owner = EntityReference.of(owner);
   }

   public void setLimitedLife(final int lifeTicks) {
      this.hasLimitedLife = true;
      this.limitedLifeTicks = lifeTicks;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VEX_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VEX_DEATH;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.VEX_HURT;
   }

   public float getLightLevelDependentMagicValue() {
      return 1.0F;
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      RandomSource random = level.getRandom();
      this.populateDefaultEquipmentSlots(random, difficulty);
      this.populateDefaultEquipmentEnchantments(level, random, difficulty);
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   protected void populateDefaultEquipmentSlots(final RandomSource random, final DifficultyInstance difficulty) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.<Byte>defineId(Vex.class, EntityDataSerializers.BYTE);
   }

   private class VexMoveControl<T extends Mob> extends MoveControl<T> {
      public VexMoveControl(final T vex) {
         Objects.requireNonNull(Vex.this);
         super(vex);
      }

      public void tick() {
         if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 delta = new Vec3(this.wantedX - Vex.this.getX(), this.wantedY - Vex.this.getY(), this.wantedZ - Vex.this.getZ());
            double deltaLength = delta.length();
            if (deltaLength < Vex.this.getBoundingBox().getSize()) {
               this.operation = MoveControl.Operation.WAIT;
               Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().scale(0.5));
            } else {
               Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().add(delta.scale(this.speedModifier * 0.05 / deltaLength)));
               if (Vex.this.getTarget() == null) {
                  Vec3 movement = Vex.this.getDeltaMovement();
                  Vex.this.setYRot(-((float)Mth.atan2(movement.x, movement.z)) * 57.295776F);
                  Vex.this.yBodyRot = Vex.this.getYRot();
               } else {
                  double tx = Vex.this.getTarget().getX() - Vex.this.getX();
                  double tz = Vex.this.getTarget().getZ() - Vex.this.getZ();
                  Vex.this.setYRot(-((float)Mth.atan2(tx, tz)) * 57.295776F);
                  Vex.this.yBodyRot = Vex.this.getYRot();
               }
            }

         }
      }
   }

   private class VexChargeAttackGoal extends Goal {
      public VexChargeAttackGoal() {
         Objects.requireNonNull(Vex.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         LivingEntity target = Vex.this.getTarget();
         if (target != null && target.isAlive() && !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(reducedTickDelay(7)) == 0) {
            return Vex.this.distanceToSqr(target) > 4.0;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return Vex.this.getMoveControl().hasWanted() && Vex.this.isCharging() && Vex.this.getTarget() != null && Vex.this.getTarget().isAlive();
      }

      public void start() {
         LivingEntity attackTarget = Vex.this.getTarget();
         if (attackTarget != null) {
            Vec3 eyePosition = attackTarget.getEyePosition();
            Vex.this.moveControl.setWantedPosition(eyePosition.x, eyePosition.y, eyePosition.z, 1.0);
         }

         Vex.this.setIsCharging(true);
         Vex.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
      }

      public void stop() {
         Vex.this.setIsCharging(false);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         LivingEntity attackTarget = Vex.this.getTarget();
         if (attackTarget != null) {
            if (Vex.this.getBoundingBox().intersects(attackTarget.getBoundingBox())) {
               Vex.this.doHurtTarget(getServerLevel(Vex.this.level()), attackTarget);
               Vex.this.setIsCharging(false);
            } else {
               double distance = Vex.this.distanceToSqr(attackTarget);
               if (distance < 9.0) {
                  Vec3 eyePosition = attackTarget.getEyePosition();
                  Vex.this.moveControl.setWantedPosition(eyePosition.x, eyePosition.y, eyePosition.z, 1.0);
               }
            }

         }
      }
   }

   private class VexRandomMoveGoal extends Goal {
      public VexRandomMoveGoal() {
         Objects.requireNonNull(Vex.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(reducedTickDelay(7)) == 0;
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void tick() {
         BlockPos boundOrigin = Vex.this.getBoundOrigin();
         if (boundOrigin == null) {
            boundOrigin = Vex.this.blockPosition();
         }

         for(int attempts = 0; attempts < 3; ++attempts) {
            BlockPos testPos = boundOrigin.offset(Vex.this.random.nextInt(15) - 7, Vex.this.random.nextInt(11) - 5, Vex.this.random.nextInt(15) - 7);
            if (Vex.this.level().isEmptyBlock(testPos)) {
               Vex.this.moveControl.setWantedPosition((double)testPos.getX() + 0.5, (double)testPos.getY() + 0.5, (double)testPos.getZ() + 0.5, 0.25);
               if (Vex.this.getTarget() == null) {
                  Vex.this.getLookControl().setLookAt((double)testPos.getX() + 0.5, (double)testPos.getY() + 0.5, (double)testPos.getZ() + 0.5, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }

   private class VexCopyOwnerTargetGoal extends TargetGoal {
      private final TargetingConditions copyOwnerTargeting;

      public VexCopyOwnerTargetGoal(final PathfinderMob mob) {
         Objects.requireNonNull(Vex.this);
         super(mob, false);
         this.copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
      }

      public boolean canUse() {
         Mob owner = Vex.this.getOwner();
         return owner != null && owner.getTarget() != null && this.canAttack(owner.getTarget(), this.copyOwnerTargeting);
      }

      public void start() {
         Mob owner = Vex.this.getOwner();
         Vex.this.setTarget(owner != null ? owner.getTarget() : null);
         super.start();
      }
   }
}
