package com.mojang.blaze3d.vertex;

import com.google.common.collect.Queues;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import java.util.Deque;
import net.minecraft.Util;
import net.minecraft.util.Mth;

public class PoseStack {
   private final Deque<Pose> poseStack = (Deque)Util.make(Queues.newArrayDeque(), (var0) -> {
      Matrix4f var1 = new Matrix4f();
      var1.setIdentity();
      Matrix3f var2 = new Matrix3f();
      var2.setIdentity();
      var0.add(new Pose(var1, var2));
   });

   public PoseStack() {
      super();
   }

   public void translate(double var1, double var3, double var5) {
      Pose var7 = (Pose)this.poseStack.getLast();
      var7.pose.multiplyWithTranslation((float)var1, (float)var3, (float)var5);
   }

   public void scale(float var1, float var2, float var3) {
      Pose var4 = (Pose)this.poseStack.getLast();
      var4.pose.multiply(Matrix4f.createScaleMatrix(var1, var2, var3));
      if (var1 == var2 && var2 == var3) {
         if (var1 > 0.0F) {
            return;
         }

         var4.normal.mul(-1.0F);
      }

      float var5 = 1.0F / var1;
      float var6 = 1.0F / var2;
      float var7 = 1.0F / var3;
      float var8 = Mth.fastInvCubeRoot(var5 * var6 * var7);
      var4.normal.mul(Matrix3f.createScaleMatrix(var8 * var5, var8 * var6, var8 * var7));
   }

   public void mulPose(Quaternion var1) {
      Pose var2 = (Pose)this.poseStack.getLast();
      var2.pose.multiply(var1);
      var2.normal.mul(var1);
   }

   public void pushPose() {
      Pose var1 = (Pose)this.poseStack.getLast();
      this.poseStack.addLast(new Pose(var1.pose.copy(), var1.normal.copy()));
   }

   public void popPose() {
      this.poseStack.removeLast();
   }

   public Pose last() {
      return (Pose)this.poseStack.getLast();
   }

   public boolean clear() {
      return this.poseStack.size() == 1;
   }

   public void setIdentity() {
      Pose var1 = (Pose)this.poseStack.getLast();
      var1.pose.setIdentity();
      var1.normal.setIdentity();
   }

   public void mulPoseMatrix(Matrix4f var1) {
      ((Pose)this.poseStack.getLast()).pose.multiply(var1);
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
