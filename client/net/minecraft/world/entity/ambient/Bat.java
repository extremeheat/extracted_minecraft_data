package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Bat extends AmbientCreature {
   public static final float FLAP_DEGREES_PER_TICK = 74.48451F;
   public static final int TICKS_PER_FLAP = Mth.ceil(2.4166098F);
   private static final EntityDataAccessor<Byte> DATA_ID_FLAGS;
   private static final int FLAG_RESTING = 1;
   private static final TargetingConditions BAT_RESTING_TARGETING;
   @Nullable
   private BlockPos targetPosition;

   public Bat(EntityType<? extends Bat> var1, Level var2) {
      super(var1, var2);
      this.setResting(true);
   }

   public boolean isFlapping() {
      return !this.isResting() && this.tickCount % TICKS_PER_FLAP == 0;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FLAGS, (byte)0);
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   public float getVoicePitch() {
      return super.getVoicePitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BAT_DEATH;
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity var1) {
   }

   protected void pushEntities() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0D);
   }

   public boolean isResting() {
      return ((Byte)this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
   }

   public void setResting(boolean var1) {
      byte var2 = (Byte)this.entityData.get(DATA_ID_FLAGS);
      if (var1) {
         this.entityData.set(DATA_ID_FLAGS, (byte)(var2 | 1));
      } else {
         this.entityData.set(DATA_ID_FLAGS, (byte)(var2 & -2));
      }

   }

   public void tick() {
      super.tick();
      if (this.isResting()) {
         this.setDeltaMovement(Vec3.ZERO);
         this.setPosRaw(this.getX(), (double)Mth.floor(this.getY()) + 1.0D - (double)this.getBbHeight(), this.getZ());
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));
      }

   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      BlockPos var1 = this.blockPosition();
      BlockPos var2 = var1.above();
      if (this.isResting()) {
         boolean var3 = this.isSilent();
         if (this.level.getBlockState(var2).isRedstoneConductor(this.level, var1)) {
            if (this.random.nextInt(200) == 0) {
               this.yHeadRot = (float)this.random.nextInt(360);
            }

            if (this.level.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
               this.setResting(false);
               if (!var3) {
                  this.level.levelEvent((Player)null, 1025, var1, 0);
               }
            }
         } else {
            this.setResting(false);
            if (!var3) {
               this.level.levelEvent((Player)null, 1025, var1, 0);
            }
         }
      } else {
         if (this.targetPosition != null && (!this.level.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level.getMinBuildHeight())) {
            this.targetPosition = null;
         }

         if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerThan(this.position(), 2.0D)) {
            this.targetPosition = new BlockPos(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0D, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
         }

         double var13 = (double)this.targetPosition.getX() + 0.5D - this.getX();
         double var5 = (double)this.targetPosition.getY() + 0.1D - this.getY();
         double var7 = (double)this.targetPosition.getZ() + 0.5D - this.getZ();
         Vec3 var9 = this.getDeltaMovement();
         Vec3 var10 = var9.add((Math.signum(var13) * 0.5D - var9.field_414) * 0.10000000149011612D, (Math.signum(var5) * 0.699999988079071D - var9.field_415) * 0.10000000149011612D, (Math.signum(var7) * 0.5D - var9.field_416) * 0.10000000149011612D);
         this.setDeltaMovement(var10);
         float var11 = (float)(Mth.atan2(var10.field_416, var10.field_414) * 57.2957763671875D) - 90.0F;
         float var12 = Mth.wrapDegrees(var11 - this.getYRot());
         this.zza = 0.5F;
         this.setYRot(this.getYRot() + var12);
         if (this.random.nextInt(100) == 0 && this.level.getBlockState(var2).isRedstoneConductor(this.level, var2)) {
            this.setResting(true);
         }
      }

   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   public boolean causeFallDamage(float var1, float var2, DamageSource var3) {
      return false;
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         if (!this.level.isClientSide && this.isResting()) {
            this.setResting(false);
         }

         return super.hurt(var1, var2);
      }
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.entityData.set(DATA_ID_FLAGS, var1.getByte("BatFlags"));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putByte("BatFlags", (Byte)this.entityData.get(DATA_ID_FLAGS));
   }

   public static boolean checkBatSpawnRules(EntityType<Bat> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      if (var3.getY() >= var1.getSeaLevel()) {
         return false;
      } else {
         int var5 = var1.getMaxLocalRawBrightness(var3);
         byte var6 = 4;
         if (isHalloween()) {
            var6 = 7;
         } else if (var4.nextBoolean()) {
            return false;
         }

         return var5 > var4.nextInt(var6) ? false : checkMobSpawnRules(var0, var1, var2, var3, var4);
      }
   }

   private static boolean isHalloween() {
      LocalDate var0 = LocalDate.now();
      int var1 = var0.get(ChronoField.DAY_OF_MONTH);
      int var2 = var0.get(ChronoField.MONTH_OF_YEAR);
      return var2 == 10 && var1 >= 20 || var2 == 11 && var1 <= 3;
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return var2.height / 2.0F;
   }

   static {
      DATA_ID_FLAGS = SynchedEntityData.defineId(Bat.class, EntityDataSerializers.BYTE);
      BAT_RESTING_TARGETING = TargetingConditions.forNonCombat().range(4.0D);
   }
}
