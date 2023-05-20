package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

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

   public void scale(float var1, float var2, float var3) {
      PoseStack.Pose var4 = this.poseStack.getLast();
      var4.pose.scale(var1, var2, var3);
      if (var1 == var2 && var2 == var3) {
         if (var1 > 0.0F) {
            return;
         }

         var4.normal.scale(-1.0F);
      }

      float var5 = 1.0F / var1;
      float var6 = 1.0F / var2;
      float var7 = 1.0F / var3;
      float var8 = Mth.fastInvCubeRoot(var5 * var6 * var7);
      var4.normal.scale(var8 * var5, var8 * var6, var8 * var7);
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
      PoseStack.Pose var1 = this.poseStack.getLast();
      this.poseStack.addLast(new PoseStack.Pose(new Matrix4f(var1.pose), new Matrix3f(var1.normal)));
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
   }

   public void mulPoseMatrix(Matrix4f var1) {
      this.poseStack.getLast().pose.mul(var1);
   }

   public static final class Pose {
      final Matrix4f pose;
      final Matrix3f normal;

      Pose(Matrix4f var1, Matrix3f var2) {
         super();
         this.pose = var1;
         this.normal = var2;
      }

      public Matrix4f pose() {
         return this.pose;
      }

      public Matrix3f normal() {
         return this.normal;
      }
   }
}
