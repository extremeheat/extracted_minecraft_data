package net.minecraft.client.renderer.block.model;

import com.mojang.math.MatrixUtil;
import net.minecraft.core.Direction;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record BlockElementRotation(Vector3fc origin, RotationValue value, boolean rescale, Matrix4fc transform) {
   public BlockElementRotation(final Vector3fc origin, final RotationValue value, final boolean rescale) {
      this(origin, value, rescale, computeTransform(value, rescale));
   }

   public BlockElementRotation {
      super();
   }

   private static Matrix4f computeTransform(final RotationValue value, final boolean rescale) {
      Matrix4f result = value.transformation();
      if (rescale && !MatrixUtil.isIdentity(result)) {
         Vector3fc scale = computeRescale(result);
         result.scale(scale);
      }

      return result;
   }

   private static Vector3fc computeRescale(final Matrix4fc rotation) {
      Vector3f scratch = new Vector3f();
      float scaleX = scaleFactorForAxis(rotation, Direction.Axis.X, scratch);
      float scaleY = scaleFactorForAxis(rotation, Direction.Axis.Y, scratch);
      float scaleZ = scaleFactorForAxis(rotation, Direction.Axis.Z, scratch);
      return scratch.set(scaleX, scaleY, scaleZ);
   }

   private static float scaleFactorForAxis(final Matrix4fc rotation, final Direction.Axis axis, final Vector3f scratch) {
      Vector3f axisUnit = scratch.set(axis.getPositive().getUnitVec3f());
      Vector3f transformedAxisUnit = rotation.transformDirection(axisUnit);
      float absX = Math.abs(transformedAxisUnit.x);
      float absY = Math.abs(transformedAxisUnit.y);
      float absZ = Math.abs(transformedAxisUnit.z);
      float maxComponent = Math.max(Math.max(absX, absY), absZ);
      return 1.0F / maxComponent;
   }

   public static record SingleAxisRotation(Direction.Axis axis, float angle) implements RotationValue {
      public SingleAxisRotation {
         super();
      }

      public Matrix4f transformation() {
         Matrix4f result = new Matrix4f();
         if (this.angle == 0.0F) {
            return result;
         } else {
            Vector3fc rotateAround = this.axis.getPositive().getUnitVec3f();
            result.rotation(this.angle * 0.017453292F, rotateAround);
            return result;
         }
      }
   }

   public static record EulerXYZRotation(float x, float y, float z) implements RotationValue {
      public EulerXYZRotation {
         super();
      }

      public Matrix4f transformation() {
         return (new Matrix4f()).rotationZYX(this.z * 0.017453292F, this.y * 0.017453292F, this.x * 0.017453292F);
      }
   }

   public interface RotationValue {
      Matrix4f transformation();
   }
}
