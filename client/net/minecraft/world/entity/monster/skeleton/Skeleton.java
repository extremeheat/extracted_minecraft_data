package net.minecraft.world.entity.monster.skeleton;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.BiConsumer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ConversionTracker;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Skeleton extends AbstractSkeleton {
   private static final EntityDataAccessor<Boolean> DATA_STRAY_CONVERSION_ID;
   private final ConversionTracker<AbstractSkeleton> freezingTracker;

   public Skeleton(final EntityType<? extends Skeleton> type, final Level level) {
      super(type, level);
      this.freezingTracker = new ConversionTracker<AbstractSkeleton>(this, DATA_STRAY_CONVERSION_ID, () -> EntityTypes.STRAY, () -> 1048, () -> this.isInPowderSnow, "FreezingTime", 140, "StrayConversionTime", 300, (BiConsumer)null);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_STRAY_CONVERSION_ID, false);
   }

   public boolean isShaking() {
      return this.freezingTracker.isConverting();
   }

   public void tick() {
      this.freezingTracker.tick();
      super.tick();
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      this.freezingTracker.addAdditionalSaveData(output);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.freezingTracker.readAdditionalSaveData(input);
   }

   @VisibleForTesting
   public void startFreezeConversion(final int time) {
      this.freezingTracker.startConversion(time);
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

   protected SoundEvent getStepSound() {
      return SoundEvents.SKELETON_STEP;
   }

   static {
      DATA_STRAY_CONVERSION_ID = SynchedEntityData.<Boolean>defineId(Skeleton.class, EntityDataSerializers.BOOLEAN);
   }
}
