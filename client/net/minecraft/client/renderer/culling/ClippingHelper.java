package net.minecraft.client.renderer.culling;

public class ClippingHelper {
   public float[][] field_78557_a = new float[6][4];
   public float[] field_178625_b = new float[16];
   public float[] field_178626_c = new float[16];
   public float[] field_78554_d = new float[16];

   public ClippingHelper() {
      super();
   }

   private double func_178624_a(float[] var1, double var2, double var4, double var6) {
      return (double)var1[0] * var2 + (double)var1[1] * var4 + (double)var1[2] * var6 + (double)var1[3];
   }

   public boolean func_78553_b(double var1, double var3, double var5, double var7, double var9, double var11) {
      for(int var13 = 0; var13 < 6; ++var13) {
         float[] var14 = this.field_78557_a[var13];
         if (this.func_178624_a(var14, var1, var3, var5) <= 0.0D && this.func_178624_a(var14, var7, var3, var5) <= 0.0D && this.func_178624_a(var14, var1, var9, var5) <= 0.0D && this.func_178624_a(var14, var7, var9, var5) <= 0.0D && this.func_178624_a(var14, var1, var3, var11) <= 0.0D && this.func_178624_a(var14, var7, var3, var11) <= 0.0D && this.func_178624_a(var14, var1, var9, var11) <= 0.0D && this.func_178624_a(var14, var7, var9, var11) <= 0.0D) {
            return false;
         }
      }

      return true;
   }
}
