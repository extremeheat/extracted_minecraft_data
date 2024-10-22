package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import com.mojang.math.MatrixUtil;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PoseStack {
   private final Deque<PoseStack.Pose> poseStack = Util.make(Queues.newArrayDeque(), var0 -> {
      Matrix4f var1 = new Matrix4f();
      Matrix3f var2 = new Matrix3f();
      var0.add(new PoseStack.Pose(var1, var2));
   });

   public PoseStack() {
      super();
   }

   public void translate(double var1, double var3, double var5) {
      this.translate((float)var1, (float)var3, (float)var5);
   }

   public void translate(float var1, float var2, float var3) {
      PoseStack.Pose var4 = this.poseStack.getLast();
      var4.pose.translate(var1, var2, var3);
   }

   public void translate(Vec3 var1) {
      this.translate(var1.x, var1.y, var1.z);
   }

   public void scale(float var1, float var2, float var3) {
      PoseStack.Pose var4 = this.poseStack.getLast();
      var4.pose.scale(var1, var2, var3);
      if (Math.abs(var1) == Math.abs(var2) && Math.abs(var2) == Math.abs(var3)) {
         if (var1 < 0.0F || var2 < 0.0F || var3 < 0.0F) {
            var4.normal.scale(Math.signum(var1), Math.signum(var2), Math.signum(var3));
         }
      } else {
         var4.normal.scale(1.0F / var1, 1.0F / var2, 1.0F / var3);
         var4.trustedNormals = false;
      }
   }

   public void mulPose(Quaternionf var1) {
      PoseStack.Pose var2 = this.poseStack.getLast();
      var2.pose.rotate(var1);
      var2.normal.rotate(var1);
   }

   public void rotateAround(Quaternionf var1, float var2, float var3, float var4) {
      PoseStack.Pose var5 = this.poseStack.getLast();
      var5.pose.rotateAround(var1, var2, var3, var4);
      var5.normal.rotate(var1);
   }

   public void pushPose() {
      this.poseStack.addLast(new PoseStack.Pose(this.poseStack.getLast()));
   }

   public void popPose() {
      this.poseStack.removeLast();
   }

   public PoseStack.Pose last() {
      return this.poseStack.getLast();
   }

   public boolean clear() {
      return this.poseStack.size() == 1;
   }

   public void setIdentity() {
      PoseStack.Pose var1 = this.poseStack.getLast();
      var1.pose.identity();
      var1.normal.identity();
      var1.trustedNormals = true;
   }

   public void mulPose(Matrix4f var1) {
      PoseStack.Pose var2 = this.poseStack.getLast();
      var2.pose.mul(var1);
      if (!MatrixUtil.isPureTranslation(var1)) {
         if (MatrixUtil.isOrthonormal(var1)) {
            var2.normal.mul(new Matrix3f(var1));
         } else {
            var2.computeNormalMatrix();
         }
      }
   }

   public static final class Pose {
      final Matrix4f pose;
      final Matrix3f normal;
      boolean trustedNormals = true;

      Pose(Matrix4f var1, Matrix3f var2) {
         super();
         this.pose = var1;
         this.normal = var2;
      }

      Pose(PoseStack.Pose var1) {
         super();
         this.pose = new Matrix4f(var1.pose);
         this.normal = new Matrix3f(var1.normal);
         this.trustedNormals = var1.trustedNormals;
      }

      void computeNormalMatrix() {
         this.normal.set(this.pose).invert().transpose();
         this.trustedNormals = false;
      }

      public Matrix4f pose() {
         return this.pose;
      }

      public Matrix3f normal() {
         return this.normal;
      }

      public Vector3f transformNormal(Vector3f var1, Vector3f var2) {
         return this.transformNormal(var1.x, var1.y, var1.z, var2);
      }

      public Vector3f transformNormal(float var1, float var2, float var3, Vector3f var4) {
         Vector3f var5 = this.normal.transform(var1, var2, var3, var4);
         return this.trustedNormals ? var5 : var5.normalize();
      }

      public PoseStack.Pose copy() {
         return new PoseStack.Pose(this);
      }
   }
}
