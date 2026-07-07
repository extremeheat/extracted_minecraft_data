package net.minecraft.world.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.PositionAndRotation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SteppedInterpolationHandler extends InterpolationHandler {
   private final InterpolationData interpolationData = new InterpolationData();
   private final SteppedInterpolationTracker interpolationTracker;

   public SteppedInterpolationHandler(final Entity entity) {
      super(entity);
      this.interpolationTracker = new SteppedInterpolationTracker(entity);
   }

   public InterpolationTracker interpolationTracker() {
      return this.interpolationTracker;
   }

   protected PositionAndRotation.Mutable interpolationData() {
      return this.interpolationData;
   }

   protected void startInterpolating(final PositionPath position, final float yRot, final float xRot) {
      Vec3 endPosition = position.endPosition();
      if (!Objects.equals(endPosition, this.interpolationData.position())) {
         this.interpolationData.addSteps(position, this.entity.position(), this.interpolationSteps);
      }

      int rotationSteps = this.interpolationSteps;
      if (position instanceof PositionPath.Stepped var6) {
         PositionPath.Stepped var10000 = var6;

         try {
            var10000.endPosition();
         } catch (Throwable var11) {
            throw new MatchException(var11.toString(), var11);
         }

         var10000 = var6;

         try {
            var14 = var10000.steps();
         } catch (Throwable var10) {
            throw new MatchException(var10.toString(), var10);
         }

         List steps = var14;
         rotationSteps = 0;

         for(PositionStep step : steps) {
            rotationSteps += step.tickOffset();
         }
      }

      this.interpolationData.set(endPosition, yRot, xRot);
      this.interpolationData.remainingRotationSteps = (float)rotationSteps;
   }

   protected void doInterpolate() {
      Vec3 newPosition = this.interpolationData.getNewPosition();
      float alpha = 1.0F / Math.max(this.interpolationData.remainingRotationSteps, 1.0F);
      float newYRot = Mth.rotLerp(alpha, this.entity.getYRot(), this.interpolationData.yRot());
      float newXRot = Mth.lerp(alpha, this.entity.getXRot(), this.interpolationData.xRot());
      this.entity.setPos(newPosition);
      this.entity.setRot(newYRot, newXRot);
      float tick = this.entity.level().getRelativeTickSpeed();
      InterpolationData var10000 = this.interpolationData;
      var10000.currentStepTicks += tick;
      var10000 = this.interpolationData;
      var10000.remainingRotationSteps -= tick;
   }

   public void applyPredictedMovement(final Vec3 delta) {
      super.applyPredictedMovement(delta);
      if (!this.entity.level().isClientSide()) {
         this.interpolationTracker.applyPredictedMovement(delta);
      }

   }

   public boolean hasActiveInterpolation() {
      return this.interpolationData.remainingRotationSteps > 0.0F || !this.interpolationData.remainingSteps.isEmpty();
   }

   public void cancel() {
      this.interpolationData.remainingSteps.clear();
      this.interpolationData.remainingRotationSteps = 0.0F;
   }

   private static class InterpolationData extends PositionAndRotation.Mutable {
      private final LinkedList<PositionStep> remainingSteps = new LinkedList();
      private Vec3 lastStepPosition;
      private float currentStepTicks;
      private float remainingRotationSteps;

      private InterpolationData() {
         super();
         this.lastStepPosition = Vec3.ZERO;
      }

      public void addDelta(final Vec3 delta) {
         super.addDelta(delta);
         this.remainingSteps.replaceAll((step) -> step.addDelta(delta));
         this.lastStepPosition = this.lastStepPosition.add(delta);
      }

      private void addSteps(final PositionPath position, final Vec3 startingPosition, final int interpolationSteps) {
         if (this.remainingSteps.isEmpty()) {
            this.lastStepPosition = startingPosition;
            this.currentStepTicks = 1.0F;
         }

         Objects.requireNonNull(position);
         byte var5 = 0;
         //$FF: var5->value
         //0->net/minecraft/world/entity/PositionPath$Linear
         //1->net/minecraft/world/entity/PositionPath$Stepped
         switch (position.typeSwitch<invokedynamic>(position, var5)) {
            case 0:
               PositionPath.Linear var6 = (PositionPath.Linear)position;
               PositionPath.Linear var17 = var6;

               try {
                  var18 = var17.endPosition();
               } catch (Throwable var13) {
                  throw new MatchException(var13.toString(), var13);
               }

               Vec3 pos = var18;
               this.remainingSteps.add(new PositionStep(pos, interpolationSteps));
               break;
            case 1:
               PositionPath.Stepped pos = (PositionPath.Stepped)position;
               PositionPath.Stepped var10000 = pos;

               try {
                  var10000.endPosition();
               } catch (Throwable var12) {
                  throw new MatchException(var12.toString(), var12);
               }

               var10000 = pos;

               try {
                  var16 = var10000.steps();
               } catch (Throwable var11) {
                  throw new MatchException(var11.toString(), var11);
               }

               List steps = var16;
               this.remainingSteps.addAll(steps);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

      }

      private Vec3 getNewPosition() {
         while(!this.remainingSteps.isEmpty()) {
            PositionStep step = (PositionStep)this.remainingSteps.getFirst();
            int offset = step.tickOffset();
            if (this.currentStepTicks < (float)offset) {
               double a = (double)(this.currentStepTicks / (float)offset);
               return this.lastStepPosition.lerp(step.position(), a);
            }

            this.currentStepTicks -= (float)offset;
            this.lastStepPosition = step.position();
            this.remainingSteps.removeFirst();
         }

         return this.position();
      }
   }
}
