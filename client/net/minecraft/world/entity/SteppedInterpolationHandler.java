package net.minecraft.world.entity;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.PositionAndRotation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SteppedInterpolationHandler extends AbstractInterpolationHandler {
   private final InterpolationData interpolationData = new InterpolationData();

   private SteppedInterpolationHandler(final Entity entity) {
      super(entity, entity.getType().updateInterval());
   }

   public static InterpolationHandler create(final Entity entity) {
      return (InterpolationHandler)(entity.level().isClientSide() ? new SteppedInterpolationHandler(entity) : new SteppedInterpolationTracker(entity));
   }

   protected PositionAndRotation.Mutable interpolationData() {
      return this.interpolationData;
   }

   protected void startInterpolating(final PositionPath position, final float yRot, final float xRot) {
      if (!this.hasActiveInterpolation()) {
         this.interpolationData.setStartingPoint(this.entity.position(), this.entity.getYRot(), this.entity.getXRot());
      }

      Vec3 endPosition = position.endPosition();
      if (Objects.equals(endPosition, this.interpolationData.position())) {
         this.interpolationData.addStep(endPosition, yRot, xRot, this.interpolationSteps);
      } else {
         this.interpolationData.addSteps(position, yRot, xRot, this.interpolationSteps);
      }

      this.interpolationData.set(endPosition, yRot, xRot);
   }

   protected void doInterpolate() {
      PositionAndRotation target = this.interpolationData.getNewPositionAndRotation();
      this.entity.setPos(target.position());
      this.entity.setRot(target.yRot(), target.xRot());
      float tick = this.entity.level().getRelativeTickSpeed();
      this.interpolationData.advance(tick, this.interpolationSteps);
   }

   public boolean hasActiveInterpolation() {
      return !this.interpolationData.remainingSteps.isEmpty();
   }

   public void cancel() {
      this.interpolationData.reset();
   }

   private static class Step extends PositionAndRotation.Mutable {
      private final int tickOffset;

      public Step(final Vec3 position, final float yRot, final float xRot, final int tickOffset) {
         super();
         this.tickOffset = tickOffset;
         this.set(position, yRot, xRot);
      }
   }

   private static class InterpolationData extends PositionAndRotation.Mutable {
      private final LinkedList<Step> remainingSteps = new LinkedList();
      private final PositionAndRotation.Mutable lastStepPosRot = new PositionAndRotation.Mutable();
      private float currentStepTicks;
      private float remainingTicks;
      private float interpolationSpeed = 1.0F;

      private InterpolationData() {
         super();
      }

      private void advance(float ticks, final int interpolationSteps) {
         float targetSpeed = Math.max(this.remainingTicks / (float)interpolationSteps, 1.0F);
         this.interpolationSpeed = Mth.lerp(1.0F / (float)interpolationSteps, this.interpolationSpeed, targetSpeed);
         if (ticks * this.interpolationSpeed < this.remainingTicks) {
            ticks *= this.interpolationSpeed;
         } else {
            ticks = this.remainingTicks;
            this.interpolationSpeed = 1.0F;
         }

         this.currentStepTicks += ticks;
         this.remainingTicks -= ticks;
      }

      private void reset() {
         this.remainingSteps.clear();
         this.remainingTicks = 0.0F;
         this.interpolationSpeed = 1.0F;
      }

      public void addDelta(final Vec3 delta) {
         super.addDelta(delta);

         for(Step step : this.remainingSteps) {
            step.addDelta(delta);
         }

         this.lastStepPosRot.addDelta(delta);
      }

      public void addRotation(final float yRot, final float xRot) {
         super.addRotation(yRot, xRot);

         for(Step step : this.remainingSteps) {
            step.addRotation(yRot, xRot);
         }

         this.lastStepPosRot.addRotation(yRot, xRot);
      }

      private void setStartingPoint(final Vec3 position, final float yRot, final float xRot) {
         this.lastStepPosRot.set(position, yRot, xRot);
         this.currentStepTicks = 1.0F;
      }

      private void addStep(final Vec3 position, final float yRot, final float xRot, final int interpolationSteps) {
         this.remainingSteps.add(new Step(position, yRot, xRot, interpolationSteps));
         this.remainingTicks += (float)interpolationSteps;
      }

      private void addSteps(final PositionPath position, final float yRot, final float xRot, final int interpolationSteps) {
         Objects.requireNonNull(position);
         byte var6 = 0;
         //$FF: var6->value
         //0->net/minecraft/world/entity/PositionPath$Linear
         //1->net/minecraft/world/entity/PositionPath$Stepped
         switch (position.typeSwitch<invokedynamic>(position, var6)) {
            case 0:
               PositionPath.Linear var7 = (PositionPath.Linear)position;
               PositionPath.Linear var25 = var7;

               try {
                  var26 = var25.endPosition();
               } catch (Throwable var18) {
                  throw new MatchException(var18.toString(), var18);
               }

               Vec3 pos = var26;
               this.addStep(pos, yRot, xRot, interpolationSteps);
               break;
            case 1:
               PositionPath.Stepped pos = (PositionPath.Stepped)position;
               PositionPath.Stepped var10000 = pos;

               try {
                  var10000.endPosition();
               } catch (Throwable var17) {
                  throw new MatchException(var17.toString(), var17);
               }

               var10000 = pos;

               try {
                  var24 = var10000.steps();
               } catch (Throwable var16) {
                  throw new MatchException(var16.toString(), var16);
               }

               List var11 = var24;
               List<PositionStep> steps = var11;
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

      private PositionAndRotation getNewPositionAndRotation() {
         while(!this.remainingSteps.isEmpty()) {
            Step step = (Step)this.remainingSteps.getFirst();
            int offset = step.tickOffset;
            if (this.currentStepTicks < (float)offset) {
               float a = this.currentStepTicks / (float)offset;
               return PositionAndRotation.of(this.lastStepPosRot.position().lerp(step.position(), (double)a), Mth.rotLerp(a, this.lastStepPosRot.yRot(), step.yRot()), Mth.lerp(a, this.lastStepPosRot.xRot(), step.xRot()));
            }

            this.currentStepTicks -= (float)offset;
            this.lastStepPosRot.set(step.position(), step.yRot(), step.xRot());
            this.remainingSteps.removeFirst();
         }

         return this;
      }
   }
}
