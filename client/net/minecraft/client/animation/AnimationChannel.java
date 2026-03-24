package net.minecraft.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record AnimationChannel(Target target, Keyframe... keyframes) {
   public AnimationChannel {
      super();
   }

   public static class Targets {
      public static final Target POSITION = ModelPart::offsetPos;
      public static final Target ROTATION = ModelPart::offsetRotation;
      public static final Target SCALE = ModelPart::offsetScale;

      public Targets() {
         super();
      }
   }

   public static class Interpolations {
      public static final Interpolation LINEAR = (vector, alpha, keyframes, prev, next, targetScale) -> {
         Vector3fc point0 = keyframes[prev].postTarget();
         Vector3fc point1 = keyframes[next].preTarget();
         return point0.lerp(point1, alpha, vector).mul(targetScale);
      };
      public static final Interpolation CATMULLROM = (vector, alpha, keyframes, prev, next, targetScale) -> {
         Vector3fc point0 = keyframes[Math.max(0, prev - 1)].postTarget();
         Vector3fc point1 = keyframes[prev].postTarget();
         Vector3fc point2 = keyframes[next].postTarget();
         Vector3fc point3 = keyframes[Math.min(keyframes.length - 1, next + 1)].postTarget();
         vector.set(Mth.catmullrom(alpha, point0.x(), point1.x(), point2.x(), point3.x()) * targetScale, Mth.catmullrom(alpha, point0.y(), point1.y(), point2.y(), point3.y()) * targetScale, Mth.catmullrom(alpha, point0.z(), point1.z(), point2.z(), point3.z()) * targetScale);
         return vector;
      };

      public Interpolations() {
         super();
      }
   }

   public interface Interpolation {
      Vector3f apply(final Vector3f vector, final float alpha, final Keyframe[] keyframes, final int prev, final int next, final float targetScale);
   }

   public interface Target {
      void apply(final ModelPart animationBone, final Vector3f target);
   }
}
