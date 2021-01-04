package net.minecraft.client.renderer.culling;

public class FrustumData {
   public final float[][] frustumData = new float[6][4];
   public final float[] projectionMatrix = new float[16];
   public final float[] modelViewMatrix = new float[16];
   public final float[] clip = new float[16];

   public FrustumData() {
      super();
   }

   private double discriminant(float[] var1, double var2, double var4, double var6) {
      return (double)var1[0] * var2 + (double)var1[1] * var4 + (double)var1[2] * var6 + (double)var1[3];
   }

   public boolean cubeInFrustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      for(int var13 = 0; var13 < 6; ++var13) {
         float[] var14 = this.frustumData[var13];
         if (this.discriminant(var14, var1, var3, var5) <= 0.0D && this.discriminant(var14, var7, var3, var5) <= 0.0D && this.discriminant(var14, var1, var9, var5) <= 0.0D && this.discriminant(var14, var7, var9, var5) <= 0.0D && this.discriminant(var14, var1, var3, var11) <= 0.0D && this.discriminant(var14, var7, var3, var11) <= 0.0D && this.discriminant(var14, var1, var9, var11) <= 0.0D && this.discriminant(var14, var7, var9, var11) <= 0.0D) {
            return false;
         }
      }

      return true;
   }
}
