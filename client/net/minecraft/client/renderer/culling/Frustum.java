package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Frustum {
   public static final int OFFSET_STEP = 4;
   private final FrustumIntersection intersection = new FrustumIntersection();
   private final Matrix4f matrix = new Matrix4f();
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
      this.intersection.set(var1.matrix);
      this.matrix.set(var1.matrix);
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

      for(double var12 = Math.ceil(this.camZ / (double)var1) * (double)var1; this.intersection.intersectAab((float)(var2 - this.camX), (float)(var4 - this.camY), (float)(var6 - this.camZ), (float)(var8 - this.camX), (float)(var10 - this.camY), (float)(var12 - this.camZ)) != -2; this.camZ -= (double)(this.viewVector.z() * 4.0F)) {
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
      var2.mul(var1, this.matrix);
      this.intersection.set(this.matrix);
      this.viewVector = this.matrix.transformTranspose(new Vector4f(0.0F, 0.0F, 1.0F, 0.0F));
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
      return this.intersection.testAab(var13, var14, var15, var16, var17, var18);
   }
}
