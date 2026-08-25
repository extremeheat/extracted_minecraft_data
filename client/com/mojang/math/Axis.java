package com.mojang.math;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3fc;

public interface Axis {
   Axis XN = new Axis() {
      public Quaternionf rotation(final float angle) {
         return (new Quaternionf()).rotationX(-angle);
      }

      public Matrix3f rotate(final Matrix3f matrix, final float angle) {
         return matrix.rotateX(-angle);
      }

      public Matrix4f rotate(final Matrix4f matrix, final float angle) {
         return matrix.rotateX(-angle);
      }

      public String toString() {
         return "<rotation around -X>";
      }
   };
   Axis XP = new Axis() {
      public Quaternionf rotation(final float angle) {
         return (new Quaternionf()).rotationX(angle);
      }

      public Matrix3f rotate(final Matrix3f matrix, final float angle) {
         return matrix.rotateX(angle);
      }

      public Matrix4f rotate(final Matrix4f matrix, final float angle) {
         return matrix.rotateX(angle);
      }

      public String toString() {
         return "<rotation around +X>";
      }
   };
   Axis YN = new Axis() {
      public Quaternionf rotation(final float angle) {
         return (new Quaternionf()).rotationY(-angle);
      }

      public Matrix3f rotate(final Matrix3f matrix, final float angle) {
         return matrix.rotateY(-angle);
      }

      public Matrix4f rotate(final Matrix4f matrix, final float angle) {
         return matrix.rotateY(-angle);
      }

      public String toString() {
         return "<rotation around -Y>";
      }
   };
   Axis YP = new Axis() {
      public Quaternionf rotation(final float angle) {
         return (new Quaternionf()).rotationY(angle);
      }

      public Matrix3f rotate(final Matrix3f matrix, final float angle) {
         return matrix.rotateY(angle);
      }

      public Matrix4f rotate(final Matrix4f matrix, final float angle) {
         return matrix.rotateY(angle);
      }

      public String toString() {
         return "<rotation around +Y>";
      }
   };
   Axis ZN = new Axis() {
      public Quaternionf rotation(final float angle) {
         return (new Quaternionf()).rotationZ(-angle);
      }

      public Matrix3f rotate(final Matrix3f matrix, final float angle) {
         return matrix.rotateZ(-angle);
      }

      public Matrix4f rotate(final Matrix4f matrix, final float angle) {
         return matrix.rotateZ(-angle);
      }

      public String toString() {
         return "<rotation around -Z>";
      }
   };
   Axis ZP = new Axis() {
      public Quaternionf rotation(final float angle) {
         return (new Quaternionf()).rotationZ(angle);
      }

      public Matrix3f rotate(final Matrix3f matrix, final float angle) {
         return matrix.rotateZ(angle);
      }

      public Matrix4f rotate(final Matrix4f matrix, final float angle) {
         return matrix.rotateZ(angle);
      }

      public String toString() {
         return "<rotation around +Z>";
      }
   };

   static Axis of(final Vector3fc axis) {
      return new Axis() {
         public Quaternionf rotation(final float angle) {
            return (new Quaternionf()).rotationAxis(angle, axis);
         }

         public Matrix3f rotate(final Matrix3f matrix, final float angle) {
            return matrix.rotate(angle, axis);
         }

         public Matrix4f rotate(final Matrix4f matrix, final float angle) {
            return matrix.rotate(angle, axis);
         }

         public String toString() {
            return "<rotation around " + String.valueOf(axis) + ">";
         }
      };
   }

   Quaternionf rotation(float angle);

   default Quaternionf rotationDegrees(final float angle) {
      return this.rotation(angle * 0.017453292F);
   }

   Matrix3f rotate(Matrix3f matrix, float angle);

   default Matrix3f rotateDegrees(final Matrix3f matrix, final float angle) {
      return this.rotate(matrix, angle * 0.017453292F);
   }

   Matrix4f rotate(Matrix4f matrix, float angle);

   default Matrix4f rotateDegrees(final Matrix4f matrix, final float angle) {
      return this.rotate(matrix, angle * 0.017453292F);
   }
}
