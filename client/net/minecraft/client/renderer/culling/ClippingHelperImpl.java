package net.minecraft.client.renderer.culling;

import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class ClippingHelperImpl extends ClippingHelper {
   private static final ClippingHelperImpl field_195630_e = new ClippingHelperImpl();
   private final FloatBuffer field_78561_f = GLAllocation.func_74529_h(16);
   private final FloatBuffer field_78562_g = GLAllocation.func_74529_h(16);
   private final FloatBuffer field_78564_h = GLAllocation.func_74529_h(16);

   public ClippingHelperImpl() {
      super();
   }

   public static ClippingHelper func_78558_a() {
      field_195630_e.func_78560_b();
      return field_195630_e;
   }

   private void func_180547_a(float[] var1) {
      float var2 = MathHelper.func_76129_c(var1[0] * var1[0] + var1[1] * var1[1] + var1[2] * var1[2]);
      var1[0] /= var2;
      var1[1] /= var2;
      var1[2] /= var2;
      var1[3] /= var2;
   }

   public void func_78560_b() {
      this.field_78561_f.clear();
      this.field_78562_g.clear();
      this.field_78564_h.clear();
      GlStateManager.func_179111_a(2983, this.field_78561_f);
      GlStateManager.func_179111_a(2982, this.field_78562_g);
      float[] var1 = this.field_178625_b;
      float[] var2 = this.field_178626_c;
      this.field_78561_f.flip().limit(16);
      this.field_78561_f.get(var1);
      this.field_78562_g.flip().limit(16);
      this.field_78562_g.get(var2);
      this.field_78554_d[0] = var2[0] * var1[0] + var2[1] * var1[4] + var2[2] * var1[8] + var2[3] * var1[12];
      this.field_78554_d[1] = var2[0] * var1[1] + var2[1] * var1[5] + var2[2] * var1[9] + var2[3] * var1[13];
      this.field_78554_d[2] = var2[0] * var1[2] + var2[1] * var1[6] + var2[2] * var1[10] + var2[3] * var1[14];
      this.field_78554_d[3] = var2[0] * var1[3] + var2[1] * var1[7] + var2[2] * var1[11] + var2[3] * var1[15];
      this.field_78554_d[4] = var2[4] * var1[0] + var2[5] * var1[4] + var2[6] * var1[8] + var2[7] * var1[12];
      this.field_78554_d[5] = var2[4] * var1[1] + var2[5] * var1[5] + var2[6] * var1[9] + var2[7] * var1[13];
      this.field_78554_d[6] = var2[4] * var1[2] + var2[5] * var1[6] + var2[6] * var1[10] + var2[7] * var1[14];
      this.field_78554_d[7] = var2[4] * var1[3] + var2[5] * var1[7] + var2[6] * var1[11] + var2[7] * var1[15];
      this.field_78554_d[8] = var2[8] * var1[0] + var2[9] * var1[4] + var2[10] * var1[8] + var2[11] * var1[12];
      this.field_78554_d[9] = var2[8] * var1[1] + var2[9] * var1[5] + var2[10] * var1[9] + var2[11] * var1[13];
      this.field_78554_d[10] = var2[8] * var1[2] + var2[9] * var1[6] + var2[10] * var1[10] + var2[11] * var1[14];
      this.field_78554_d[11] = var2[8] * var1[3] + var2[9] * var1[7] + var2[10] * var1[11] + var2[11] * var1[15];
      this.field_78554_d[12] = var2[12] * var1[0] + var2[13] * var1[4] + var2[14] * var1[8] + var2[15] * var1[12];
      this.field_78554_d[13] = var2[12] * var1[1] + var2[13] * var1[5] + var2[14] * var1[9] + var2[15] * var1[13];
      this.field_78554_d[14] = var2[12] * var1[2] + var2[13] * var1[6] + var2[14] * var1[10] + var2[15] * var1[14];
      this.field_78554_d[15] = var2[12] * var1[3] + var2[13] * var1[7] + var2[14] * var1[11] + var2[15] * var1[15];
      float[] var3 = this.field_78557_a[0];
      var3[0] = this.field_78554_d[3] - this.field_78554_d[0];
      var3[1] = this.field_78554_d[7] - this.field_78554_d[4];
      var3[2] = this.field_78554_d[11] - this.field_78554_d[8];
      var3[3] = this.field_78554_d[15] - this.field_78554_d[12];
      this.func_180547_a(var3);
      float[] var4 = this.field_78557_a[1];
      var4[0] = this.field_78554_d[3] + this.field_78554_d[0];
      var4[1] = this.field_78554_d[7] + this.field_78554_d[4];
      var4[2] = this.field_78554_d[11] + this.field_78554_d[8];
      var4[3] = this.field_78554_d[15] + this.field_78554_d[12];
      this.func_180547_a(var4);
      float[] var5 = this.field_78557_a[2];
      var5[0] = this.field_78554_d[3] + this.field_78554_d[1];
      var5[1] = this.field_78554_d[7] + this.field_78554_d[5];
      var5[2] = this.field_78554_d[11] + this.field_78554_d[9];
      var5[3] = this.field_78554_d[15] + this.field_78554_d[13];
      this.func_180547_a(var5);
      float[] var6 = this.field_78557_a[3];
      var6[0] = this.field_78554_d[3] - this.field_78554_d[1];
      var6[1] = this.field_78554_d[7] - this.field_78554_d[5];
      var6[2] = this.field_78554_d[11] - this.field_78554_d[9];
      var6[3] = this.field_78554_d[15] - this.field_78554_d[13];
      this.func_180547_a(var6);
      float[] var7 = this.field_78557_a[4];
      var7[0] = this.field_78554_d[3] - this.field_78554_d[2];
      var7[1] = this.field_78554_d[7] - this.field_78554_d[6];
      var7[2] = this.field_78554_d[11] - this.field_78554_d[10];
      var7[3] = this.field_78554_d[15] - this.field_78554_d[14];
      this.func_180547_a(var7);
      float[] var8 = this.field_78557_a[5];
      var8[0] = this.field_78554_d[3] + this.field_78554_d[2];
      var8[1] = this.field_78554_d[7] + this.field_78554_d[6];
      var8[2] = this.field_78554_d[11] + this.field_78554_d[10];
      var8[3] = this.field_78554_d[15] + this.field_78554_d[14];
      this.func_180547_a(var8);
   }
}
