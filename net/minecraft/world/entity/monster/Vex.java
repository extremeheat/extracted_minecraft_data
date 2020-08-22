package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

public class Vex extends Monster {
   protected static final EntityDataAccessor DATA_FLAGS_ID;
   private Mob owner;
   @Nullable
   private BlockPos boundOrigin;
   private boolean hasLimitedLife;
   private int limitedLifeTicks;

   public Vex(EntityType var1, Level var2) {
      super(var1, var2);
      this.moveControl = new Vex.VexMoveControl(this);
      this.xpReward = 3;
   }

   public void move(MoverType var1, Vec3 var2) {
      super.move(var1, var2);
      this.checkInsideBlocks();
   }

   public void tick() {
      this.noPhysics = true;
      super.tick();
      this.noPhysics = false;
      this.setNoGravity(true);
      if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
         this.limitedLifeTicks = 20;
         this.hurt(DamageSource.STARVE, 1.0F);
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(4, new Vex.VexChargeAttackGoal());
      this.goalSelector.addGoal(8, new Vex.VexRandomMoveGoal());
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers());
      this.targetSelector.addGoal(2, new Vex.VexCopyOwnerTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(14.0D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)0);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("BoundX")) {
         this.boundOrigin = new BlockPos(var1.getInt("BoundX"), var1.getInt("BoundY"), var1.getInt("BoundZ"));
      }

      if (var1.contains("LifeTicks")) {
         this.setLimitedLife(var1.getInt("LifeTicks"));
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.boundOrigin != null) {
         var1.putInt("BoundX", this.boundOrigin.getX());
         var1.putInt("BoundY", this.boundOrigin.getY());
         var1.putInt("BoundZ", this.boundOrigin.getZ());
      }

      if (this.hasLimitedLife) {
         var1.putInt("LifeTicks", this.limitedLifeTicks);
      }

   }

   public Mob getOwner() {
      return this.owner;
   }

   @Nullable
   public BlockPos getBoundOrigin() {
      return this.boundOrigin;
   }

   public void setBoundOrigin(@Nullable BlockPos var1) {
      this.boundOrigin = var1;
   }

   private boolean getVexFlag(int var1) {
      byte var2 = (Byte)this.entityData.get(DATA_FLAGS_ID);
      return (var2 & var1) != 0;
   }

   private void setVexFlag(int var1, boolean var2) {
      byte var3 = (Byte)this.entityData.get(DATA_FLAGS_ID);
      int var4;
      if (var2) {
         var4 = var3 | var1;
      } else {
         var4 = var3 & ~var1;
      }

      this.entityData.set(DATA_FLAGS_ID, (byte)(var4 & 255));
   }

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setIsCharging(boolean var1) {
      this.setVexFlag(1, var1);
   }

   public void setOwner(Mob var1) {
      this.owner = var1;
   }

   public void setLimitedLife(int var1) {
      this.hasLimitedLife = true;
      this.limitedLifeTicks = var1;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.VEX_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VEX_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.VEX_HURT;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.populateDefaultEquipmentSlots(var2);
      this.populateDefaultEquipmentEnchantments(var2);
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance var1) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
      this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
   }

   static {
      DATA_FLAGS_ID = SynchedEntityData.defineId(Vex.class, EntityDataSerializers.BYTE);
   }

   class VexCopyOwnerTargetGoal extends TargetGoal {
      private final TargetingConditions copyOwnerTargeting = (new TargetingConditions()).allowUnseeable().ignoreInvisibilityTesting();

      public VexCopyOwnerTargetGoal(PathfinderMob var2) {
         super(var2, false);
      }

      public boolean canUse() {
         return Vex.this.owner != null && Vex.this.owner.getTarget() != null && this.canAttack(Vex.this.owner.getTarget(), this.copyOwnerTargeting);
      }

      public void start() {
         Vex.this.setTarget(Vex.this.owner.getTarget());
         super.start();
      }
   }

   class VexRandomMoveGoal extends Goal {
      public VexRandomMoveGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(7) == 0;
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void tick() {
         BlockPos var1 = Vex.this.getBoundOrigin();
         if (var1 == null) {
            var1 = new BlockPos(Vex.this);
         }

         for(int var2 = 0; var2 < 3; ++var2) {
            BlockPos var3 = var1.offset(Vex.this.random.nextInt(15) - 7, Vex.this.random.nextInt(11) - 5, Vex.this.random.nextInt(15) - 7);
            if (Vex.this.level.isEmptyBlock(var3)) {
               Vex.this.moveControl.setWantedPosition((double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, 0.25D);
               if (Vex.this.getTarget() == null) {
                  Vex.this.getLookControl().setLookAt((double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }

   class VexChargeAttackGoal extends Goal {
      public VexChargeAttackGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (Vex.this.getTarget() != null && !Vex.this.getMoveControl().hasWanted() && Vex.this.random.nextInt(7) == 0) {
            return Vex.this.distanceToSqr(Vex.this.getTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return Vex.this.getMoveControl().hasWanted() && Vex.this.isCharging() && Vex.this.getTarget() != null && Vex.this.getTarget().isAlive();
      }

      public void start() {
         LivingEntity var1 = Vex.this.getTarget();
         Vec3 var2 = var1.getEyePosition(1.0F);
         Vex.this.moveControl.setWantedPosition(var2.x, var2.y, var2.z, 1.0D);
         Vex.this.setIsCharging(true);
         Vex.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
      }

      public void stop() {
         Vex.this.setIsCharging(false);
      }

      public void tick() {
         LivingEntity var1 = Vex.this.getTarget();
         if (Vex.this.getBoundingBox().intersects(var1.getBoundingBox())) {
            Vex.this.doHurtTarget(var1);
            Vex.this.setIsCharging(false);
         } else {
            double var2 = Vex.this.distanceToSqr(var1);
            if (var2 < 9.0D) {
               Vec3 var4 = var1.getEyePosition(1.0F);
               Vex.this.moveControl.setWantedPosition(var4.x, var4.y, var4.z, 1.0D);
            }
         }

      }
   }

   class VexMoveControl extends MoveControl {
      public VexMoveControl(Vex var2) {
         super(var2);
      }

      public void tick() {
         if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 var1 = new Vec3(this.wantedX - Vex.this.getX(), this.wantedY - Vex.this.getY(), this.wantedZ - Vex.this.getZ());
            double var2 = var1.length();
            if (var2 < Vex.this.getBoundingBox().getSize()) {
               this.operation = MoveControl.Operation.WAIT;
               Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().scale(0.5D));
            } else {
               Vex.this.setDeltaMovement(Vex.this.getDeltaMovement().add(var1.scale(this.speedModifier * 0.05D / var2)));
               if (Vex.this.getTarget() == null) {
                  Vec3 var4 = Vex.this.getDeltaMovement();
                  Vex.this.yRot = -((float)Mth.atan2(var4.x, var4.z)) * 57.295776F;
                  Vex.this.yBodyRot = Vex.this.yRot;
               } else {
                  double var8 = Vex.this.getTarget().getX() - Vex.this.getX();
                  double var6 = Vex.this.getTarget().getZ() - Vex.this.getZ();
                  Vex.this.yRot = -((float)Mth.atan2(var8, var6)) * 57.295776F;
                  Vex.this.yBodyRot = Vex.this.yRot;
               }
            }

         }
      }
   }
}
