package net.minecraft.client.renderer.culling;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.AABB;

public class Frustum {
   private final Vector4f[] frustumData = new Vector4f[6];
   private double camX;
   private double camY;
   private double camZ;

   public Frustum(Matrix4f var1, Matrix4f var2) {
      this.calculateFrustum(var1, var2);
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
         if (var8.dot(new Vector4f(var1, var2, var3, 1.0F)) <= 0.0F && var8.dot(new Vector4f(var4, var2, var3, 1.0F)) <= 0.0F && var8.dot(new Vector4f(var1, var5, var3, 1.0F)) <= 0.0F && var8.dot(new Vector4f(var4, var5, var3, 1.0F)) <= 0.0F && var8.dot(new Vector4f(var1, var2, var6, 1.0F)) <= 0.0F && var8.dot(new Vector4f(var4, var2, var6, 1.0F)) <= 0.0F && var8.dot(new Vector4f(var1, var5, var6, 1.0F)) <= 0.0F && var8.dot(new Vector4f(var4, var5, var6, 1.0F)) <= 0.0F) {
            return false;
         }
      }

      return true;
   }
}
