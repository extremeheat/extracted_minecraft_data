package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.ShulkerSharedHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Shulker extends AbstractGolem implements Enemy {
   private static final UUID COVERED_ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   private static final AttributeModifier COVERED_ARMOR_MODIFIER;
   protected static final EntityDataAccessor<Direction> DATA_ATTACH_FACE_ID;
   protected static final EntityDataAccessor<Optional<BlockPos>> DATA_ATTACH_POS_ID;
   protected static final EntityDataAccessor<Byte> DATA_PEEK_ID;
   protected static final EntityDataAccessor<Byte> DATA_COLOR_ID;
   private float currentPeekAmountO;
   private float currentPeekAmount;
   private BlockPos oldAttachPosition = null;
   private int clientSideTeleportInterpolation;

   public Shulker(EntityType<? extends Shulker> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(4, new Shulker.ShulkerAttackGoal());
      this.goalSelector.addGoal(7, new Shulker.ShulkerPeekGoal());
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{this.getClass()})).setAlertOthers());
      this.targetSelector.addGoal(2, new Shulker.ShulkerNearestAttackGoal(this));
      this.targetSelector.addGoal(3, new Shulker.ShulkerDefenseAttackGoal(this));
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHULKER_AMBIENT;
   }

   public void playAmbientSound() {
      if (!this.isClosed()) {
         super.playAmbientSound();
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHULKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isClosed() ? SoundEvents.SHULKER_HURT_CLOSED : SoundEvents.SHULKER_HURT;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ATTACH_FACE_ID, Direction.DOWN);
      this.entityData.define(DATA_ATTACH_POS_ID, Optional.empty());
      this.entityData.define(DATA_PEEK_ID, (byte)0);
      this.entityData.define(DATA_COLOR_ID, (byte)16);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D);
   }

   protected BodyRotationControl createBodyControl() {
      return new Shulker.ShulkerBodyRotationControl(this);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.entityData.set(DATA_ATTACH_FACE_ID, Direction.from3DDataValue(var1.getByte("AttachFace")));
      this.entityData.set(DATA_PEEK_ID, var1.getByte("Peek"));
      this.entityData.set(DATA_COLOR_ID, var1.getByte("Color"));
      if (var1.contains("APX")) {
         int var2 = var1.getInt("APX");
         int var3 = var1.getInt("APY");
         int var4 = var1.getInt("APZ");
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(new BlockPos(var2, var3, var4)));
      } else {
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.empty());
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putByte("AttachFace", (byte)((Direction)this.entityData.get(DATA_ATTACH_FACE_ID)).get3DDataValue());
      var1.putByte("Peek", (Byte)this.entityData.get(DATA_PEEK_ID));
      var1.putByte("Color", (Byte)this.entityData.get(DATA_COLOR_ID));
      BlockPos var2 = this.getAttachPosition();
      if (var2 != null) {
         var1.putInt("APX", var2.getX());
         var1.putInt("APY", var2.getY());
         var1.putInt("APZ", var2.getZ());
      }

   }

   public void tick() {
      super.tick();
      BlockPos var1 = (BlockPos)((Optional)this.entityData.get(DATA_ATTACH_POS_ID)).orElse((Object)null);
      if (var1 == null && !this.level.isClientSide) {
         var1 = this.blockPosition();
         this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(var1));
      }

      float var2;
      if (this.isPassenger()) {
         var1 = null;
         var2 = this.getVehicle().yRot;
         this.yRot = var2;
         this.yBodyRot = var2;
         this.yBodyRotO = var2;
         this.clientSideTeleportInterpolation = 0;
      } else if (!this.level.isClientSide) {
         BlockState var13 = this.level.getBlockState(var1);
         Direction var3;
         if (!var13.isAir()) {
            if (var13.is(Blocks.MOVING_PISTON)) {
               var3 = (Direction)var13.getValue(PistonBaseBlock.FACING);
               if (this.level.isEmptyBlock(var1.relative(var3))) {
                  var1 = var1.relative(var3);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(var1));
               } else {
                  this.teleportSomewhere();
               }
            } else if (var13.is(Blocks.PISTON_HEAD)) {
               var3 = (Direction)var13.getValue(PistonHeadBlock.FACING);
               if (this.level.isEmptyBlock(var1.relative(var3))) {
                  var1 = var1.relative(var3);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(var1));
               } else {
                  this.teleportSomewhere();
               }
            } else {
               this.teleportSomewhere();
            }
         }

         var3 = this.getAttachFace();
         if (!this.canAttachOnBlockFace(var1, var3)) {
            Direction var4 = this.findAttachableFace(var1);
            if (var4 != null) {
               this.entityData.set(DATA_ATTACH_FACE_ID, var4);
            } else {
               this.teleportSomewhere();
            }
         }
      }

      var2 = (float)this.getRawPeekAmount() * 0.01F;
      this.currentPeekAmountO = this.currentPeekAmount;
      if (this.currentPeekAmount > var2) {
         this.currentPeekAmount = Mth.clamp(this.currentPeekAmount - 0.05F, var2, 1.0F);
      } else if (this.currentPeekAmount < var2) {
         this.currentPeekAmount = Mth.clamp(this.currentPeekAmount + 0.05F, 0.0F, var2);
      }

      if (var1 != null) {
         if (this.level.isClientSide) {
            if (this.clientSideTeleportInterpolation > 0 && this.oldAttachPosition != null) {
               --this.clientSideTeleportInterpolation;
            } else {
               this.oldAttachPosition = var1;
            }
         }

         this.setPosAndOldPos((double)var1.getX() + 0.5D, (double)var1.getY(), (double)var1.getZ() + 0.5D);
         double var14 = 0.5D - (double)Mth.sin((0.5F + this.currentPeekAmount) * 3.1415927F) * 0.5D;
         double var5 = 0.5D - (double)Mth.sin((0.5F + this.currentPeekAmountO) * 3.1415927F) * 0.5D;
         Direction var7 = this.getAttachFace().getOpposite();
         this.setBoundingBox((new AABB(this.getX() - 0.5D, this.getY(), this.getZ() - 0.5D, this.getX() + 0.5D, this.getY() + 1.0D, this.getZ() + 0.5D)).expandTowards((double)var7.getStepX() * var14, (double)var7.getStepY() * var14, (double)var7.getStepZ() * var14));
         double var8 = var14 - var5;
         if (var8 > 0.0D) {
            List var10 = this.level.getEntities(this, this.getBoundingBox());
            if (!var10.isEmpty()) {
               Iterator var11 = var10.iterator();

               while(var11.hasNext()) {
                  Entity var12 = (Entity)var11.next();
                  if (!(var12 instanceof Shulker) && !var12.noPhysics) {
                     var12.move(MoverType.SHULKER, new Vec3(var8 * (double)var7.getStepX(), var8 * (double)var7.getStepY(), var8 * (double)var7.getStepZ()));
                  }
               }
            }
         }
      }

   }

   public void move(MoverType var1, Vec3 var2) {
      if (var1 == MoverType.SHULKER_BOX) {
         this.teleportSomewhere();
      } else {
         super.move(var1, var2);
      }

   }

   public void setPos(double var1, double var3, double var5) {
      super.setPos(var1, var3, var5);
      if (this.entityData != null && this.tickCount != 0) {
         Optional var7 = (Optional)this.entityData.get(DATA_ATTACH_POS_ID);
         Optional var8 = Optional.of(new BlockPos(var1, var3, var5));
         if (!var8.equals(var7)) {
            this.entityData.set(DATA_ATTACH_POS_ID, var8);
            this.entityData.set(DATA_PEEK_ID, (byte)0);
            this.hasImpulse = true;
         }

      }
   }

   @Nullable
   protected Direction findAttachableFace(BlockPos var1) {
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction var5 = var2[var4];
         if (this.canAttachOnBlockFace(var1, var5)) {
            return var5;
         }
      }

      return null;
   }

   private boolean canAttachOnBlockFace(BlockPos var1, Direction var2) {
      return this.level.loadedAndEntityCanStandOnFace(var1.relative(var2), this, var2.getOpposite()) && this.level.noCollision(this, ShulkerSharedHelper.openBoundingBox(var1, var2.getOpposite()));
   }

   protected boolean teleportSomewhere() {
      if (!this.isNoAi() && this.isAlive()) {
         BlockPos var1 = this.blockPosition();

         for(int var2 = 0; var2 < 5; ++var2) {
            BlockPos var3 = var1.offset(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));
            if (var3.getY() > this.level.getMinBuildHeight() && this.level.isEmptyBlock(var3) && this.level.getWorldBorder().isWithinBounds(var3) && this.level.noCollision(this, new AABB(var3))) {
               Direction var4 = this.findAttachableFace(var3);
               if (var4 != null) {
                  this.unRide();
                  this.entityData.set(DATA_ATTACH_FACE_ID, var4);
                  this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
                  this.entityData.set(DATA_ATTACH_POS_ID, Optional.of(var3));
                  this.entityData.set(DATA_PEEK_ID, (byte)0);
                  this.setTarget((LivingEntity)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void aiStep() {
      super.aiStep();
      this.setDeltaMovement(Vec3.ZERO);
      if (!this.isNoAi()) {
         this.yBodyRotO = 0.0F;
         this.yBodyRot = 0.0F;
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_ATTACH_POS_ID.equals(var1) && this.level.isClientSide && !this.isPassenger()) {
         BlockPos var2 = this.getAttachPosition();
         if (var2 != null) {
            if (this.oldAttachPosition == null) {
               this.oldAttachPosition = var2;
            } else {
               this.clientSideTeleportInterpolation = 6;
            }

            this.setPosAndOldPos((double)var2.getX() + 0.5D, (double)var2.getY(), (double)var2.getZ() + 0.5D);
         }
      }

      super.onSyncedDataUpdated(var1);
   }

   public void lerpTo(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.lerpSteps = 0;
   }

   public boolean hurt(DamageSource var1, float var2) {
      Entity var3;
      if (this.isClosed()) {
         var3 = var1.getDirectEntity();
         if (var3 instanceof AbstractArrow) {
            return false;
         }
      }

      if (!super.hurt(var1, var2)) {
         return false;
      } else {
         if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
            this.teleportSomewhere();
         } else if (var1.isProjectile()) {
            var3 = var1.getDirectEntity();
            if (var3 != null && var3.getType() == EntityType.SHULKER_BULLET) {
               this.hitByShulkerBullet();
            }
         }

         return true;
      }
   }

   private boolean isClosed() {
      return this.getRawPeekAmount() == 0;
   }

   private void hitByShulkerBullet() {
      Vec3 var1 = this.position();
      AABB var2 = this.getBoundingBox();
      if (!this.isClosed() && this.teleportSomewhere()) {
         int var3 = this.level.getEntities((EntityTypeTest)EntityType.SHULKER, var2.inflate(8.0D), Entity::isAlive).size();
         float var4 = (float)(var3 - 1) / 5.0F;
         if (this.level.random.nextFloat() >= var4) {
            Shulker var5 = (Shulker)EntityType.SHULKER.create(this.level);
            DyeColor var6 = this.getColor();
            if (var6 != null) {
               var5.setColor(var6);
            }

            var5.moveTo(var1);
            this.level.addFreshEntity(var5);
         }
      }
   }

   public boolean canBeCollidedWith() {
      return this.isAlive();
   }

   public Direction getAttachFace() {
      return (Direction)this.entityData.get(DATA_ATTACH_FACE_ID);
   }

   @Nullable
   public BlockPos getAttachPosition() {
      return (BlockPos)((Optional)this.entityData.get(DATA_ATTACH_POS_ID)).orElse((Object)null);
   }

   public void setAttachPosition(@Nullable BlockPos var1) {
      this.entityData.set(DATA_ATTACH_POS_ID, Optional.ofNullable(var1));
   }

   public int getRawPeekAmount() {
      return (Byte)this.entityData.get(DATA_PEEK_ID);
   }

   public void setRawPeekAmount(int var1) {
      if (!this.level.isClientSide) {
         this.getAttribute(Attributes.ARMOR).removeModifier(COVERED_ARMOR_MODIFIER);
         if (var1 == 0) {
            this.getAttribute(Attributes.ARMOR).addPermanentModifier(COVERED_ARMOR_MODIFIER);
            this.playSound(SoundEvents.SHULKER_CLOSE, 1.0F, 1.0F);
         } else {
            this.playSound(SoundEvents.SHULKER_OPEN, 1.0F, 1.0F);
         }
      }

      this.entityData.set(DATA_PEEK_ID, (byte)var1);
   }

   public float getClientPeekAmount(float var1) {
      return Mth.lerp(var1, this.currentPeekAmountO, this.currentPeekAmount);
   }

   public int getClientSideTeleportInterpolation() {
      return this.clientSideTeleportInterpolation;
   }

   public BlockPos getOldAttachPosition() {
      return this.oldAttachPosition;
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.5F;
   }

   public int getMaxHeadXRot() {
      return 180;
   }

   public int getMaxHeadYRot() {
      return 180;
   }

   public void push(Entity var1) {
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public boolean hasValidInterpolationPositions() {
      return this.oldAttachPosition != null && this.getAttachPosition() != null;
   }

   public void setColor(DyeColor var1) {
      this.entityData.set(DATA_COLOR_ID, (byte)var1.getId());
   }

   @Nullable
   public DyeColor getColor() {
      byte var1 = (Byte)this.entityData.get(DATA_COLOR_ID);
      return var1 != 16 && var1 <= 15 ? DyeColor.byId(var1) : null;
   }

   static {
      COVERED_ARMOR_MODIFIER = new AttributeModifier(COVERED_ARMOR_MODIFIER_UUID, "Covered armor bonus", 20.0D, AttributeModifier.Operation.ADDITION);
      DATA_ATTACH_FACE_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.DIRECTION);
      DATA_ATTACH_POS_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
      DATA_PEEK_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
      DATA_COLOR_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
   }

   static class ShulkerDefenseAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public ShulkerDefenseAttackGoal(Shulker var1) {
         super(var1, LivingEntity.class, 10, true, false, (var0) -> {
            return var0 instanceof Enemy;
         });
      }

      public boolean canUse() {
         return this.mob.getTeam() == null ? false : super.canUse();
      }

      protected AABB getTargetSearchArea(double var1) {
         Direction var3 = ((Shulker)this.mob).getAttachFace();
         if (var3.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().inflate(4.0D, var1, var1);
         } else {
            return var3.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().inflate(var1, var1, 4.0D) : this.mob.getBoundingBox().inflate(var1, 4.0D, var1);
         }
      }
   }

   class ShulkerNearestAttackGoal extends NearestAttackableTargetGoal<Player> {
      public ShulkerNearestAttackGoal(Shulker var2) {
         super(var2, Player.class, true);
      }

      public boolean canUse() {
         return Shulker.this.level.getDifficulty() == Difficulty.PEACEFUL ? false : super.canUse();
      }

      protected AABB getTargetSearchArea(double var1) {
         Direction var3 = ((Shulker)this.mob).getAttachFace();
         if (var3.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().inflate(4.0D, var1, var1);
         } else {
            return var3.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().inflate(var1, var1, 4.0D) : this.mob.getBoundingBox().inflate(var1, 4.0D, var1);
         }
      }
   }

   class ShulkerAttackGoal extends Goal {
      private int attackTime;

      public ShulkerAttackGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity var1 = Shulker.this.getTarget();
         if (var1 != null && var1.isAlive()) {
            return Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL;
         } else {
            return false;
         }
      }

      public void start() {
         this.attackTime = 20;
         Shulker.this.setRawPeekAmount(100);
      }

      public void stop() {
         Shulker.this.setRawPeekAmount(0);
      }

      public void tick() {
         if (Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL) {
            --this.attackTime;
            LivingEntity var1 = Shulker.this.getTarget();
            Shulker.this.getLookControl().setLookAt(var1, 180.0F, 180.0F);
            double var2 = Shulker.this.distanceToSqr(var1);
            if (var2 < 400.0D) {
               if (this.attackTime <= 0) {
                  this.attackTime = 20 + Shulker.this.random.nextInt(10) * 20 / 2;
                  Shulker.this.level.addFreshEntity(new ShulkerBullet(Shulker.this.level, Shulker.this, var1, Shulker.this.getAttachFace().getAxis()));
                  Shulker.this.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (Shulker.this.random.nextFloat() - Shulker.this.random.nextFloat()) * 0.2F + 1.0F);
               }
            } else {
               Shulker.this.setTarget((LivingEntity)null);
            }

            super.tick();
         }
      }
   }

   class ShulkerPeekGoal extends Goal {
      private int peekTime;

      private ShulkerPeekGoal() {
         super();
      }

      public boolean canUse() {
         return Shulker.this.getTarget() == null && Shulker.this.random.nextInt(40) == 0;
      }

      public boolean canContinueToUse() {
         return Shulker.this.getTarget() == null && this.peekTime > 0;
      }

      public void start() {
         this.peekTime = 20 * (1 + Shulker.this.random.nextInt(3));
         Shulker.this.setRawPeekAmount(30);
      }

      public void stop() {
         if (Shulker.this.getTarget() == null) {
            Shulker.this.setRawPeekAmount(0);
         }

      }

      public void tick() {
         --this.peekTime;
      }

      // $FF: synthetic method
      ShulkerPeekGoal(Object var2) {
         this();
      }
   }

   class ShulkerBodyRotationControl extends BodyRotationControl {
      public ShulkerBodyRotationControl(Mob var2) {
         super(var2);
      }

      public void clientTick() {
      }
   }
}
