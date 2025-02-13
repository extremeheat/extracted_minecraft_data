package com.mojang.blaze3d.vertex;

import com.mojang.math.MatrixUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PoseStack {
   private final List<Pose> poses = new ArrayList(16);
   private int lastIndex;

   public PoseStack() {
      super();
      this.poses.add(new Pose());
   }

   public void translate(double var1, double var3, double var5) {
      this.translate((float)var1, (float)var3, (float)var5);
   }

   public void translate(float var1, float var2, float var3) {
      this.last().translate(var1, var2, var3);
   }

   public void translate(Vec3 var1) {
      this.translate(var1.x, var1.y, var1.z);
   }

   public void scale(float var1, float var2, float var3) {
      this.last().scale(var1, var2, var3);
   }

   public void mulPose(Quaternionfc var1) {
      this.last().rotate(var1);
   }

   public void rotateAround(Quaternionfc var1, float var2, float var3, float var4) {
      this.last().rotateAround(var1, var2, var3, var4);
   }

   public void pushPose() {
      Pose var1 = this.last();
      ++this.lastIndex;
      if (this.lastIndex >= this.poses.size()) {
         this.poses.add(var1.copy());
      } else {
         ((Pose)this.poses.get(this.lastIndex)).set(var1);
      }

   }

   public void popPose() {
      if (this.lastIndex == 0) {
         throw new NoSuchElementException();
      } else {
         --this.lastIndex;
      }
   }

   public Pose last() {
      return (Pose)this.poses.get(this.lastIndex);
   }

   public boolean isEmpty() {
      return this.lastIndex == 0;
   }

   public void setIdentity() {
      this.last().setIdentity();
   }

   public void mulPose(Matrix4fc var1) {
      this.last().mulPose(var1);
   }

   public static final class Pose {
      private final Matrix4f pose = new Matrix4f();
      private final Matrix3f normal = new Matrix3f();
      private boolean trustedNormals = true;

      public Pose() {
         super();
      }

      private void computeNormalMatrix() {
         this.normal.set(this.pose).invert().transpose();
         this.trustedNormals = false;
      }

      void set(Pose var1) {
         this.pose.set(var1.pose);
         this.normal.set(var1.normal);
         this.trustedNormals = var1.trustedNormals;
      }

      public Matrix4f pose() {
         return this.pose;
      }

      public Matrix3f normal() {
         return this.normal;
      }

      public Vector3f transformNormal(Vector3fc var1, Vector3f var2) {
         return this.transformNormal(var1.x(), var1.y(), var1.z(), var2);
      }

      public Vector3f transformNormal(float var1, float var2, float var3, Vector3f var4) {
         Vector3f var5 = this.normal.transform(var1, var2, var3, var4);
         return this.trustedNormals ? var5 : var5.normalize();
      }

      public Matrix4f translate(float var1, float var2, float var3) {
         return this.pose.translate(var1, var2, var3);
      }

      public void scale(float var1, float var2, float var3) {
         this.pose.scale(var1, var2, var3);
         if (Math.abs(var1) == Math.abs(var2) && Math.abs(var2) == Math.abs(var3)) {
            if (var1 < 0.0F || var2 < 0.0F || var3 < 0.0F) {
               this.normal.scale(Math.signum(var1), Math.signum(var2), Math.signum(var3));
            }

         } else {
            this.normal.scale(1.0F / var1, 1.0F / var2, 1.0F / var3);
            this.trustedNormals = false;
         }
      }

      public void rotate(Quaternionfc var1) {
         this.pose.rotate(var1);
         this.normal.rotate(var1);
      }

      public void rotateAround(Quaternionfc var1, float var2, float var3, float var4) {
         this.pose.rotateAround(var1, var2, var3, var4);
         this.normal.rotate(var1);
      }

      public void setIdentity() {
         this.pose.identity();
         this.normal.identity();
         this.trustedNormals = true;
      }

      public void mulPose(Matrix4fc var1) {
         this.pose.mul(var1);
         if (!MatrixUtil.isPureTranslation(var1)) {
            if (MatrixUtil.isOrthonormal(var1)) {
               this.normal.mul(new Matrix3f(var1));
            } else {
               this.computeNormalMatrix();
            }
         }

      }

      public Pose copy() {
         Pose var1 = new Pose();
         var1.set(this);
         return var1;
      }
   }
}
