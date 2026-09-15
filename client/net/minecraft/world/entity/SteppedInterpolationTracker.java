package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.phys.Vec3;

public class SteppedInterpolationTracker extends InterpolationHandler.NoOpInterpolationHandler implements InterpolationTracker {
   private final List<PositionStep> trackedSteps = new ArrayList();
   private int ticksSinceLastStep = -1;
   private Vec3 predictedDelta;
   private final Entity entity;

   public SteppedInterpolationTracker(final Entity entity) {
      super();
      this.predictedDelta = Vec3.ZERO;
      this.entity = entity;
   }

   public InterpolationTracker interpolationTracker() {
      return this;
   }

   public void applyPredictedMovement(final Vec3 delta) {
      this.predictedDelta = this.predictedDelta.add(delta);
   }

   private void addStep(final Vec3 pos) {
      if (this.ticksSinceLastStep > 0) {
         this.trackedSteps.add(new PositionStep(pos, this.ticksSinceLastStep));
         this.ticksSinceLastStep = 0;
      }

   }

   public void updateTracking(final Vec3 trackingPos) {
      if (this.predictedDelta.lengthSqr() > 9.999999747378752E-6) {
         this.trackedSteps.replaceAll((step) -> step.addDelta(this.predictedDelta));
      }

      this.predictedDelta = Vec3.ZERO;
      ++this.ticksSinceLastStep;
      if (this.entity.syncPosition) {
         this.entity.syncPosition = false;
         this.addStep(trackingPos);
      }

   }

   public PositionPath getPositionPath(final Vec3 currentPosition) {
      this.addStep(currentPosition);
      return this.trackedSteps.isEmpty() ? PositionPath.of(currentPosition) : PositionPath.stepped(List.copyOf(this.trackedSteps));
   }

   public void clear() {
      this.trackedSteps.clear();
      this.ticksSinceLastStep = 0;
   }
}
