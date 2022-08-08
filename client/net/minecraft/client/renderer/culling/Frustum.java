package net.minecraft.client.renderer.culling;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.AABB;

public class Frustum {
   public static final int OFFSET_STEP = 4;
   private final Vector4f[] frustumData = new Vector4f[6];
   private Vector4f viewVector;
   private double camX;
   private double camY;
   private double camZ;

   public Frustum(Matrix4f var1, Matrix4f var2) {
      super();
      this.calculateFrustum(var1, var2);
   }

   public Frustum(Frustum var1) {
      super();
      System.arraycopy(var1.frustumData, 0, this.frustumData, 0, var1.frustumData.length);
      this.camX = var1.camX;
      this.camY = var1.camY;
      this.camZ = var1.camZ;
      this.viewVector = var1.viewVector;
   }

   public Frustum offsetToFullyIncludeCameraCube(int var1) {
      double var2 = Math.floor(this.camX / (double)var1) * (double)var1;
      double var4 = Math.floor(this.camY / (double)var1) * (double)var1;
      double var6 = Math.floor(this.camZ / (double)var1) * (double)var1;
      double var8 = Math.ceil(this.camX / (double)var1) * (double)var1;
      double var10 = Math.ceil(this.camY / (double)var1) * (double)var1;

      for(double var12 = Math.ceil(this.camZ / (double)var1) * (double)var1; !this.cubeCompletelyInFrustum((float)(var2 - this.camX), (float)(var4 - this.camY), (float)(var6 - this.camZ), (float)(var8 - this.camX), (float)(var10 - this.camY), (float)(var12 - this.camZ)); this.camZ -= (double)(this.viewVector.z() * 4.0F)) {
         this.camX -= (double)(this.viewVector.x() * 4.0F);
         this.camY -= (double)(this.viewVector.y() * 4.0F);
      }

      return this;
   }

   public void prepare(double var1, double var3, double var5) {
      this.camX = var1;
      this.camY = var3;
      this.camZ = var5;
   }

   private void calculateFrustum(Matrix4f var1, Matrix4f var2) {
      Matrix4f var3 = var2.copy();
      var3.multiply(var1);
      var3.transpose();
      this.viewVector = new Vector4f(0.0F, 0.0F, 1.0F, 0.0F);
      this.viewVector.transform(var3);
      this.getPlane(var3, -1, 0, 0, 0);
      this.getPlane(var3, 1, 0, 0, 1);
      this.getPlane(var3, 0, -1, 0, 2);
      this.getPlane(var3, 0, 1, 0, 3);
      this.getPlane(var3, 0, 0, -1, 4);
      this.getPlane(var3, 0, 0, 1, 5);
   }

   private void getPlane(Matrix4f var1, int var2, int var3, int var4, int var5) {
      Vector4f var6 = new Vector4f((float)var2, (float)var3, (float)var4, 1.0F);
      var6.transform(var1);
      var6.normalize();
      this.frustumData[var5] = var6;
   }

   public boolean isVisible(AABB var1) {
      return this.cubeInFrustum(var1.minX, var1.minY, var1.minZ, var1.maxX, var1.maxY, var1.maxZ);
   }

   private boolean cubeInFrustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      float var13 = (float)(var1 - this.camX);
      float var14 = (float)(var3 - this.camY);
      float var15 = (float)(var5 - this.camZ);
      float var16 = (float)(var7 - this.camX);
      float var17 = (float)(var9 - this.camY);
      float var18 = (float)(var11 - this.camZ);
      return this.cubeInFrustum(var13, var14, var15, var16, var17, var18);
   }

   private boolean cubeInFrustum(float var1, float var2, float var3, float var4, float var5, float var6) {
      for(int var7 = 0; var7 < 6; ++var7) {
         Vector4f var8 = this.frustumData[var7];
         if (!(var8.dot(new Vector4f(var1, var2, var3, 1.0F)) > 0.0F) && !(var8.dot(new Vector4f(var4, var2, var3, 1.0F)) > 0.0F) && !(var8.dot(new Vector4f(var1, var5, var3, 1.0F)) > 0.0F) && !(var8.dot(new Vector4f(var4, var5, var3, 1.0F)) > 0.0F) && !(var8.dot(new Vector4f(var1, var2, var6, 1.0F)) > 0.0F) && !(var8.dot(new Vector4f(var4, var2, var6, 1.0F)) > 0.0F) && !(var8.dot(new Vector4f(var1, var5, var6, 1.0F)) > 0.0F) && !(var8.dot(new Vector4f(var4, var5, var6, 1.0F)) > 0.0F)) {
            return false;
         }
      }

      return true;
   }

   private boolean cubeCompletelyInFrustum(float var1, float var2, float var3, float var4, float var5, float var6) {
      for(int var7 = 0; var7 < 6; ++var7) {
         Vector4f var8 = this.frustumData[var7];
         if (var8.dot(new Vector4f(var1, var2, var3, 1.0F)) <= 0.0F) {
            return false;
         }

         if (var8.dot(new Vector4f(var4, var2, var3, 1.0F)) <= 0.0F) {
            return false;
         }

         if (var8.dot(new Vector4f(var1, var5, var3, 1.0F)) <= 0.0F) {
            return false;
         }

         if (var8.dot(new Vector4f(var4, var5, var3, 1.0F)) <= 0.0F) {
            return false;
         }

         if (var8.dot(new Vector4f(var1, var2, var6, 1.0F)) <= 0.0F) {
            return false;
         }

         if (var8.dot(new Vector4f(var4, var2, var6, 1.0F)) <= 0.0F) {
            return false;
         }

         if (var8.dot(new Vector4f(var1, var5, var6, 1.0F)) <= 0.0F) {
            return false;
         }

         if (var8.dot(new Vector4f(var4, var5, var6, 1.0F)) <= 0.0F) {
            return false;
         }
      }

      return true;
   }
}
