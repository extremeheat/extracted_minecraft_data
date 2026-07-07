package net.minecraft.world.entity;

import net.minecraft.core.PositionAndRotation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class LinearInterpolationHandler extends InterpolationHandler {
   private final InterpolationData interpolationData = new InterpolationData();

   public LinearInterpolationHandler(final Entity entity) {
      super(entity);
   }

   public LinearInterpolationHandler(final Entity entity, final int interpolationSteps) {
      super(entity, interpolationSteps);
   }

   public void setInterpolationLength(final int steps) {
      this.interpolationSteps = steps;
   }

   protected PositionAndRotation.Mutable interpolationData() {
      return this.interpolationData;
   }

   protected void startInterpolating(final PositionPath position, final float yRot, final float xRot) {
      this.interpolationData.set(position.endPosition(), yRot, xRot);
      this.interpolationData.remainingSteps = this.interpolationSteps;
   }

   protected void doInterpolate() {
      double alpha = 1.0 / (double)this.interpolationData.remainingSteps;
      Vec3 newPosition = this.entity.position().lerp(this.interpolationData.position(), alpha);
      float newYRot = (float)Mth.rotLerp(alpha, (double)this.entity.getYRot(), (double)this.interpolationData.yRot());
      float newXRot = (float)Mth.lerp(alpha, (double)this.entity.getXRot(), (double)this.interpolationData.xRot());
      this.entity.setPos(newPosition);
      this.entity.setRot(newYRot, newXRot);
      --this.interpolationData.remainingSteps;
   }

   public boolean hasActiveInterpolation() {
      return this.interpolationData.remainingSteps > 0;
   }

   public void cancel() {
      this.interpolationData.remainingSteps = 0;
   }

   private static class InterpolationData extends PositionAndRotation.Mutable {
      private int remainingSteps;

      private InterpolationData() {
         super();
      }
   }
}
