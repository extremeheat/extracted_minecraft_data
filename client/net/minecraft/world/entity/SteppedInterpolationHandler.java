package net.minecraft.world.entity;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.PositionAndRotation;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SteppedInterpolationHandler extends AbstractInterpolationHandler {
   private final InterpolationData interpolationData = new InterpolationData();
   private final int updateInterval;

   private SteppedInterpolationHandler(final Entity entity, final int interpolationSteps) {
      super(entity, interpolationSteps);
      this.updateInterval = entity.getType().updateInterval();
   }

   private SteppedInterpolationHandler(final Entity entity) {
      super(entity, entity.getType().updateInterval());
      this.updateInterval = this.interpolationSteps;
   }

   public static InterpolationHandler create(final Entity entity, final int interpolationSteps) {
      return (InterpolationHandler)(entity.level().isClientSide() ? new SteppedInterpolationHandler(entity, interpolationSteps) : new SteppedInterpolationTracker(entity));
   }

   public static InterpolationHandler create(final Entity entity) {
      return (InterpolationHandler)(entity.level().isClientSide() ? new SteppedInterpolationHandler(entity) : new SteppedInterpolationTracker(entity));
   }

   protected PositionAndRotation.Mutable interpolationData() {
      return this.interpolationData;
   }

   protected void startInterpolating(final PositionPath position, final float yRot, final float xRot) {
      if (position instanceof PositionPath.Stepped stepped) {
         if (stepped.startImmediately()) {
            this.cancel();
         }
      }

      if (!this.hasActiveInterpolation()) {
         this.interpolationData.setStartingPoint(this.entity.position(), this.entity.getYRot(), this.entity.getXRot());
      }

      Vec3 endPosition = position.endPosition();
      if (Objects.equals(endPosition, this.interpolationData.position())) {
         this.interpolationData.addStep(endPosition, yRot, xRot, this.updateInterval);
      } else {
         this.interpolationData.addSteps(position, yRot, xRot, this.updateInterval);
      }

      this.interpolationData.set(endPosition, yRot, xRot);
      this.interpolationData.remainingTicks = (float)this.interpolationSteps;
   }

   protected void doInterpolate() {
      this.interpolationData.advance(this.entity.level().getRelativeTickSpeed());
      PositionAndRotation target = this.interpolationData.currentStepPosRot;
      this.entity.setPos(target.position());
      this.entity.setRot(target.yRot(), target.xRot());
   }

   public boolean hasActiveInterpolation() {
      return this.interpolationData.remainingTicks > 0.0F;
   }

   public void cancel() {
      this.interpolationData.reset();
   }

   private static class Step extends PositionAndRotation.Mutable {
      private float weight;

      public Step(final Vec3 position, final float yRot, final float xRot, final int weight) {
         super();
         this.weight = (float)weight;
         this.set(position, yRot, xRot);
      }
   }

   private static class InterpolationData extends PositionAndRotation.Mutable {
      private final List<Step> remainingSteps = new ArrayListDeque<Step>();
      private final PositionAndRotation.Mutable currentStepPosRot = new PositionAndRotation.Mutable();
      private float remainingTicks;
      private float totalWeight;

      private InterpolationData() {
         super();
      }

      private void advance(final float ticks) {
         float advanceByWeight = ticks * this.totalWeight / this.remainingTicks;
         this.totalWeight -= advanceByWeight;
         this.remainingTicks -= ticks;
         if (!this.advanceToNextStep(advanceByWeight)) {
            this.reset();
         }

      }

      private boolean advanceToNextStep(float advanceByWeight) {
         while(!this.remainingSteps.isEmpty()) {
            Step step = (Step)this.remainingSteps.getFirst();
            float weight = step.weight;
            if (advanceByWeight < weight) {
               float a = advanceByWeight / weight;
               if (a > 0.0F) {
                  this.currentStepPosRot.set(this.currentStepPosRot.position().lerp(step.position(), (double)a), Mth.rotLerp(a, this.currentStepPosRot.yRot(), step.yRot()), Mth.lerp(a, this.currentStepPosRot.xRot(), step.xRot()));
               }

               step.weight -= advanceByWeight;
               return true;
            }

            advanceByWeight -= weight;
            this.currentStepPosRot.set(step.position(), step.yRot(), step.xRot());
            this.remainingSteps.removeFirst();
         }

         return false;
      }

      private void reset() {
         this.remainingSteps.clear();
         this.remainingTicks = 0.0F;
         this.totalWeight = 0.0F;
      }

      public void addDelta(final Vec3 delta) {
         super.addDelta(delta);

         for(Step step : this.remainingSteps) {
            step.addDelta(delta);
         }

         this.currentStepPosRot.addDelta(delta);
      }

      public void addRotation(final float yRot, final float xRot) {
         super.addRotation(yRot, xRot);

         for(Step step : this.remainingSteps) {
            step.addRotation(yRot, xRot);
         }

         this.currentStepPosRot.addRotation(yRot, xRot);
      }

      private void setStartingPoint(final Vec3 position, final float yRot, final float xRot) {
         this.currentStepPosRot.set(position, yRot, xRot);
      }

      private void addStep(final Vec3 position, final float yRot, final float xRot, final int weight) {
         this.remainingSteps.add(new Step(position, yRot, xRot, weight));
         this.totalWeight += (float)weight;
      }

      private void addSteps(final PositionPath position, final float yRot, final float xRot, final int weight) {
         Objects.requireNonNull(position);
         byte var6 = 0;
         //$FF: var6->value
         //0->net/minecraft/world/entity/PositionPath$Linear
         //1->net/minecraft/world/entity/PositionPath$Stepped
         switch (position.typeSwitch<invokedynamic>(position, var6)) {
            case 0:
               PositionPath.Linear var7 = (PositionPath.Linear)position;
               PositionPath.Linear var27 = var7;

               try {
                  var28 = var27.endPosition();
               } catch (Throwable var19) {
                  throw new MatchException(var19.toString(), var19);
               }

               Vec3 pos = var28;
               this.addStep(pos, yRot, xRot, weight);
               break;
            case 1:
               PositionPath.Stepped pos = (PositionPath.Stepped)position;
               PositionPath.Stepped var10000 = pos;

               try {
                  var10000.endPosition();
               } catch (Throwable var18) {
                  throw new MatchException(var18.toString(), var18);
               }

               var10000 = pos;

               try {
                  var25 = var10000.steps();
               } catch (Throwable var17) {
                  throw new MatchException(var17.toString(), var17);
               }

               List var11 = var25;
               List<PositionStep> steps = var11;
               var10000 = pos;

               try {
                  var10000.startImmediately();
               } catch (Throwable var16) {
                  throw new MatchException(var16.toString(), var16);
               }

               if (yRot == this.yRot() && xRot == this.xRot()) {
                  for(PositionStep step : var11) {
                     this.addStep(step.position(), yRot, xRot, step.tickOffset());
                  }

                  return;
               }

               int totalInterpolationTicks = getInterpolationTicks(var11);
               int offset = 0;

               for(PositionStep step : steps) {
                  offset += step.tickOffset();
                  float a = (float)offset / (float)totalInterpolationTicks;
                  this.addStep(step.position(), Mth.rotLerp(a, this.yRot(), yRot), Mth.lerp(a, this.xRot(), xRot), step.tickOffset());
               }
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

      }

      private static int getInterpolationTicks(final List<PositionStep> steps) {
         int ticks = 0;

         for(PositionStep step : steps) {
            ticks += step.tickOffset();
         }

         return ticks;
      }
   }
}
