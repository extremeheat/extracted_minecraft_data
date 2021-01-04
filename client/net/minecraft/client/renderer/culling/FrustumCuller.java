package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AABB;

public class FrustumCuller implements Culler {
   private final FrustumData frustum;
   private double xOff;
   private double yOff;
   private double zOff;

   public FrustumCuller() {
      this(Frustum.getFrustum());
   }

   public FrustumCuller(FrustumData var1) {
      super();
      this.frustum = var1;
   }

   public void prepare(double var1, double var3, double var5) {
      this.xOff = var1;
      this.yOff = var3;
      this.zOff = var5;
   }

   public boolean cubeInFrustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.frustum.cubeInFrustum(var1 - this.xOff, var3 - this.yOff, var5 - this.zOff, var7 - this.xOff, var9 - this.yOff, var11 - this.zOff);
   }

   public boolean isVisible(AABB var1) {
      return this.cubeInFrustum(var1.minX, var1.minY, var1.minZ, var1.maxX, var1.maxY, var1.maxZ);
   }
}
