package net.minecraft.client.renderer.block.model;

import com.mojang.math.MatrixUtil;
import net.minecraft.core.Direction;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record BlockElementRotation(Vector3fc origin, RotationValue value, boolean rescale, Matrix4fc transform) {
   public BlockElementRotation(Vector3fc var1, RotationValue var2, boolean var3) {
      this(var1, var2, var3, computeTransform(var2, var3));
   }

   public BlockElementRotation(Vector3fc var1, RotationValue var2, boolean var3, Matrix4fc var4) {
      super();
      this.origin = var1;
      this.value = var2;
      this.rescale = var3;
      this.transform = var4;
   }

   private static Matrix4f computeTransform(RotationValue var0, boolean var1) {
      Matrix4f var2 = var0.transformation();
      if (var1 && !MatrixUtil.isIdentity(var2)) {
         Vector3fc var3 = computeRescale(var2);
         var2.scale(var3);
      }

      return var2;
   }

   private static Vector3fc computeRescale(Matrix4fc var0) {
      Vector3f var1 = new Vector3f();
      float var2 = scaleFactorForAxis(var0, Direction.Axis.X, var1);
      float var3 = scaleFactorForAxis(var0, Direction.Axis.Y, var1);
      float var4 = scaleFactorForAxis(var0, Direction.Axis.Z, var1);
      return var1.set(var2, var3, var4);
   }

   private static float scaleFactorForAxis(Matrix4fc var0, Direction.Axis var1, Vector3f var2) {
      Vector3f var3 = var2.set(var1.getPositive().getUnitVec3f());
      Vector3f var4 = var0.transformDirection(var3);
      float var5 = Math.abs(var4.x);
      float var6 = Math.abs(var4.y);
      float var7 = Math.abs(var4.z);
      float var8 = Math.max(Math.max(var5, var6), var7);
      return 1.0F / var8;
   }

   public static record SingleAxisRotation(Direction.Axis axis, float angle) implements RotationValue {
      public SingleAxisRotation(Direction.Axis var1, float var2) {
         super();
         this.axis = var1;
         this.angle = var2;
      }

      public Matrix4f transformation() {
         Matrix4f var1 = new Matrix4f();
         if (this.angle == 0.0F) {
            return var1;
         } else {
            Vector3fc var2 = this.axis.getPositive().getUnitVec3f();
            var1.rotation(this.angle * 0.017453292F, var2);
            return var1;
         }
      }
   }

   public static record EulerXYZRotation(float x, float y, float z) implements RotationValue {
      public EulerXYZRotation(float var1, float var2, float var3) {
         super();
         this.x = var1;
         this.y = var2;
         this.z = var3;
      }

      public Matrix4f transformation() {
         return (new Matrix4f()).rotationZYX(this.z * 0.017453292F, this.y * 0.017453292F, this.x * 0.017453292F);
      }
   }

   public interface RotationValue {
      Matrix4f transformation();
   }
}
