package net.minecraft.world.entity;

import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class ConversionTracker<M extends Mob> {
   private static final int NOT_CONVERTING = -1;
   private static final int DEFAULT_AFFLICTION_TIME = 0;
   private final M mob;
   private final EntityDataAccessor<Boolean> conversionId;
   private final Supplier<EntityType<? extends M>> convertsTo;
   private final Supplier<@LevelEvent.Value Integer> conversionSound;
   private final BooleanSupplier isAfflicted;
   private final String afflictionTimeTag;
   private final int totalAfflictionTime;
   private final String conversionTimeTag;
   private final int totalConversionTime;
   private final @Nullable BiConsumer<M, ServerLevel> postConversionStep;
   private int afflictionTime = 0;
   private int conversionTime;

   public ConversionTracker(final M mob, final EntityDataAccessor<Boolean> conversionId, final Supplier<EntityType<? extends M>> convertsTo, final Supplier<@LevelEvent.Value Integer> conversionSound, final BooleanSupplier isAfflicted, final String afflictionTimeTag, final int totalAfflictionTime, final String conversionTimeTag, final int totalConversionTime, final @Nullable BiConsumer<M, ServerLevel> postConversionStep) {
      super();
      this.mob = mob;
      this.conversionId = conversionId;
      this.convertsTo = convertsTo;
      this.conversionSound = conversionSound;
      this.isAfflicted = isAfflicted;
      this.afflictionTimeTag = afflictionTimeTag;
      this.totalAfflictionTime = totalAfflictionTime;
      this.conversionTimeTag = conversionTimeTag;
      this.totalConversionTime = totalConversionTime;
      this.postConversionStep = postConversionStep;
   }

   public void tick() {
      Level var2 = this.mob.level();
      if (var2 instanceof ServerLevel serverLevel) {
         if (this.mob.isAlive() && !this.mob.isNoAi()) {
            if (this.isAfflicted.getAsBoolean()) {
               if (this.isConverting()) {
                  --this.conversionTime;
                  if (this.conversionTime < 0) {
                     this.doConversion(serverLevel);
                  }
               } else {
                  ++this.afflictionTime;
                  if (this.afflictionTime >= this.totalAfflictionTime) {
                     this.startConversion(this.totalConversionTime);
                  }
               }
            } else {
               this.afflictionTime = -1;
               this.setConverting(false);
            }
         }
      }

   }

   public boolean isConverting() {
      return (Boolean)this.mob.getEntityData().get(this.conversionId);
   }

   private void setConverting(final boolean isConverting) {
      this.mob.getEntityData().set(this.conversionId, isConverting);
   }

   public void addAdditionalSaveData(final ValueOutput output) {
      output.putInt(this.conversionTimeTag, this.isConverting() ? this.conversionTime : -1);
      output.putInt(this.afflictionTimeTag, this.isAfflicted.getAsBoolean() ? this.afflictionTime : -1);
   }

   public void readAdditionalSaveData(final ValueInput input) {
      this.afflictionTime = input.getIntOr(this.afflictionTimeTag, 0);
      int conversionTime = input.getIntOr(this.conversionTimeTag, -1);
      if (conversionTime != -1) {
         this.startConversion(conversionTime);
      } else {
         this.setConverting(false);
      }

   }

   private void doConversion(final ServerLevel serverLevel) {
      this.mob.convertTo((EntityType)this.convertsTo.get(), ConversionParams.single(this.mob, true, true), (converted) -> {
         if (this.postConversionStep != null) {
            this.postConversionStep.accept(converted, serverLevel);
         }

         if (!converted.isSilent()) {
            serverLevel.levelEvent((Entity)null, (Integer)this.conversionSound.get(), converted.blockPosition(), 0);
         }

      });
   }

   public void startConversion(final int time) {
      this.conversionTime = time;
      this.setConverting(true);
   }

   public void setAfflictionTime(final int afflictionTime) {
      this.afflictionTime = afflictionTime;
   }

   public void setConversionTime(final int conversionTime) {
      this.conversionTime = conversionTime;
   }
}
