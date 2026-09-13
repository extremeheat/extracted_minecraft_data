package net.minecraft.client.renderer.culling;

public class ClippingHelper {
   public float[][] field_78557_a = new float[16][16];
   public float[] field_78555_b = new float[16];
   public float[] field_78556_c = new float[16];
   public float[] field_78554_d = new float[16];

   public ClippingHelper() {
      super();
   }

   public boolean func_78553_b(double var1, double var3, double var5, double var7, double var9, double var11) {
      for(int var13 = 0; var13 < 6; ++var13) {
         if (!(
               (double)this.field_78557_a[var13][0] * var1
                     + (double)this.field_78557_a[var13][1] * var3
                     + (double)this.field_78557_a[var13][2] * var5
                     + (double)this.field_78557_a[var13][3]
                  > 0.0
            )
            && !(
               (double)this.field_78557_a[var13][0] * var7
                     + (double)this.field_78557_a[var13][1] * var3
                     + (double)this.field_78557_a[var13][2] * var5
                     + (double)this.field_78557_a[var13][3]
                  > 0.0
            )
            && !(
               (double)this.field_78557_a[var13][0] * var1
                     + (double)this.field_78557_a[var13][1] * var9
                     + (double)this.field_78557_a[var13][2] * var5
                     + (double)this.field_78557_a[var13][3]
                  > 0.0
            )
            && !(
               (double)this.field_78557_a[var13][0] * var7
                     + (double)this.field_78557_a[var13][1] * var9
                     + (double)this.field_78557_a[var13][2] * var5
                     + (double)this.field_78557_a[var13][3]
                  > 0.0
            )
            && !(
               (double)this.field_78557_a[var13][0] * var1
                     + (double)this.field_78557_a[var13][1] * var3
                     + (double)this.field_78557_a[var13][2] * var11
                     + (double)this.field_78557_a[var13][3]
                  > 0.0
            )
            && !(
               (double)this.field_78557_a[var13][0] * var7
                     + (double)this.field_78557_a[var13][1] * var3
                     + (double)this.field_78557_a[var13][2] * var11
                     + (double)this.field_78557_a[var13][3]
                  > 0.0
            )
            && !(
               (double)this.field_78557_a[var13][0] * var1
                     + (double)this.field_78557_a[var13][1] * var9
                     + (double)this.field_78557_a[var13][2] * var11
                     + (double)this.field_78557_a[var13][3]
                  > 0.0
            )
            && !(
               (double)this.field_78557_a[var13][0] * var7
                     + (double)this.field_78557_a[var13][1] * var9
                     + (double)this.field_78557_a[var13][2] * var11
                     + (double)this.field_78557_a[var13][3]
                  > 0.0
            )) {
            return false;
         }
      }

      return true;
   }
}
