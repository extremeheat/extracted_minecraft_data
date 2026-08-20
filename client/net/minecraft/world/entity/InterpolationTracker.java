package net.minecraft.world.entity;

import net.minecraft.world.phys.Vec3;

public interface InterpolationTracker {
   InterpolationTracker NO_OP = new NoOpInterpolationTracker();

   void updateTracking(Vec3 trackingPos);

   PositionPath getPositionPath(Vec3 trackingPos);

   void clear();

   public static record NoOpInterpolationTracker() implements InterpolationTracker {
      public NoOpInterpolationTracker() {
         super();
      }

      public void updateTracking(final Vec3 trackingPos) {
      }

      public PositionPath getPositionPath(final Vec3 trackingPos) {
         return PositionPath.of(trackingPos);
      }

      public void clear() {
      }
   }
}
