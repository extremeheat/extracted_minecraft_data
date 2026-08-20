package net.minecraft.world.entity;

import net.minecraft.core.PositionAndRotation;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface InterpolationHandler {
   InterpolationHandler NO_OP = new NoOpInterpolationHandler();

   default InterpolationTracker interpolationTracker() {
      return InterpolationTracker.NO_OP;
   }

   @Nullable PositionAndRotation target();

   boolean interpolateTo(@Nullable PositionPath position, float yRot, float xRot, boolean hasRotation);

   void interpolate();

   void applyPredictedMovement(Vec3 delta);

   boolean hasActiveInterpolation();

   void cancel();

   public static class NoOpInterpolationHandler implements InterpolationHandler {
      public NoOpInterpolationHandler() {
         super();
      }

      public @Nullable PositionAndRotation target() {
         return null;
      }

      public boolean interpolateTo(final @Nullable PositionPath position, final float yRot, final float xRot, final boolean hasRotation) {
         return false;
      }

      public void interpolate() {
      }

      public void applyPredictedMovement(final Vec3 delta) {
      }

      public boolean hasActiveInterpolation() {
         return false;
      }

      public void cancel() {
      }
   }
}
