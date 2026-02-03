package net.minecraft.world.entity.monster.skeleton;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Skeleton extends AbstractSkeleton {
   private static final int TOTAL_CONVERSION_TIME = 300;
   private static final EntityDataAccessor<Boolean> DATA_STRAY_CONVERSION_ID;
   public static final String CONVERSION_TAG = "StrayConversionTime";
   private static final int NOT_CONVERTING = -1;
   private int inPowderSnowTime;
   private int conversionTime;

   public Skeleton(final EntityType<? extends Skeleton> type, final Level level) {
      super(type, level);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_STRAY_CONVERSION_ID, false);
   }

   public boolean isFreezeConverting() {
      return (Boolean)this.getEntityData().get(DATA_STRAY_CONVERSION_ID);
   }

   public void setFreezeConverting(final boolean isConverting) {
      this.entityData.set(DATA_STRAY_CONVERSION_ID, isConverting);
   }

   public boolean isShaking() {
      return this.isFreezeConverting();
   }

   public void tick() {
      if (!this.level().isClientSide() && this.isAlive() && !this.isNoAi()) {
         if (this.isInPowderSnow) {
            if (this.isFreezeConverting()) {
               --this.conversionTime;
               if (this.conversionTime < 0) {
                  this.doFreezeConversion();
               }
            } else {
               ++this.inPowderSnowTime;
               if (this.inPowderSnowTime >= 140) {
                  this.startFreezeConversion(300);
               }
            }
         } else {
            this.inPowderSnowTime = -1;
            this.setFreezeConverting(false);
         }
      }

      super.tick();
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("StrayConversionTime", this.isFreezeConverting() ? this.conversionTime : -1);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      int conversionTime = input.getIntOr("StrayConversionTime", -1);
      if (conversionTime != -1) {
         this.startFreezeConversion(conversionTime);
      } else {
         this.setFreezeConverting(false);
      }

   }

   @VisibleForTesting
   public void startFreezeConversion(final int time) {
      this.conversionTime = time;
      this.setFreezeConverting(true);
   }

   protected void doFreezeConversion() {
      this.convertTo(EntityType.STRAY, ConversionParams.single(this, true, true), (stray) -> {
         if (!this.isSilent()) {
            this.level().levelEvent((Entity)null, 1048, this.blockPosition(), 0);
         }

      });
   }

   public boolean canFreeze() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.SKELETON_STEP;
   }

   static {
      DATA_STRAY_CONVERSION_ID = SynchedEntityData.<Boolean>defineId(Skeleton.class, EntityDataSerializers.BOOLEAN);
   }
}
