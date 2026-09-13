package net.minecraft.client.renderer.culling;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ClippingHelperImpl extends ClippingHelper {
   private static ClippingHelperImpl field_78563_e = new ClippingHelperImpl();
   private FloatBuffer field_78561_f = GLAllocation.func_74529_h(16);
   private FloatBuffer field_78562_g = GLAllocation.func_74529_h(16);
   private FloatBuffer field_78564_h = GLAllocation.func_74529_h(16);

   public ClippingHelperImpl() {
      super();
   }

   public static ClippingHelper func_78558_a() {
      field_78563_e.func_78560_b();
      return field_78563_e;
   }

   private void func_78559_a(float[][] var1, int var2) {
      float var3 = MathHelper.func_76129_c(var1[var2][0] * var1[var2][0] + var1[var2][1] * var1[var2][1] + var1[var2][2] * var1[var2][2]);
      var1[var2][0] /= var3;
      var1[var2][1] /= var3;
      var1[var2][2] /= var3;
      var1[var2][3] /= var3;
   }

   private void func_78560_b() {
      ((Buffer)this.field_78561_f).clear();
      ((Buffer)this.field_78562_g).clear();
      ((Buffer)this.field_78564_h).clear();
      GL11.glGetFloat(2983, this.field_78561_f);
      GL11.glGetFloat(2982, this.field_78562_g);
      ((Buffer)this.field_78561_f).flip().limit(16);
      this.field_78561_f.get(this.field_78555_b);
      ((Buffer)this.field_78562_g).flip().limit(16);
      this.field_78562_g.get(this.field_78556_c);
      this.field_78554_d[0] = this.field_78556_c[0] * this.field_78555_b[0]
         + this.field_78556_c[1] * this.field_78555_b[4]
         + this.field_78556_c[2] * this.field_78555_b[8]
         + this.field_78556_c[3] * this.field_78555_b[12];
      this.field_78554_d[1] = this.field_78556_c[0] * this.field_78555_b[1]
         + this.field_78556_c[1] * this.field_78555_b[5]
         + this.field_78556_c[2] * this.field_78555_b[9]
         + this.field_78556_c[3] * this.field_78555_b[13];
      this.field_78554_d[2] = this.field_78556_c[0] * this.field_78555_b[2]
         + this.field_78556_c[1] * this.field_78555_b[6]
         + this.field_78556_c[2] * this.field_78555_b[10]
         + this.field_78556_c[3] * this.field_78555_b[14];
      this.field_78554_d[3] = this.field_78556_c[0] * this.field_78555_b[3]
         + this.field_78556_c[1] * this.field_78555_b[7]
         + this.field_78556_c[2] * this.field_78555_b[11]
         + this.field_78556_c[3] * this.field_78555_b[15];
      this.field_78554_d[4] = this.field_78556_c[4] * this.field_78555_b[0]
         + this.field_78556_c[5] * this.field_78555_b[4]
         + this.field_78556_c[6] * this.field_78555_b[8]
         + this.field_78556_c[7] * this.field_78555_b[12];
      this.field_78554_d[5] = this.field_78556_c[4] * this.field_78555_b[1]
         + this.field_78556_c[5] * this.field_78555_b[5]
         + this.field_78556_c[6] * this.field_78555_b[9]
         + this.field_78556_c[7] * this.field_78555_b[13];
      this.field_78554_d[6] = this.field_78556_c[4] * this.field_78555_b[2]
         + this.field_78556_c[5] * this.field_78555_b[6]
         + this.field_78556_c[6] * this.field_78555_b[10]
         + this.field_78556_c[7] * this.field_78555_b[14];
      this.field_78554_d[7] = this.field_78556_c[4] * this.field_78555_b[3]
         + this.field_78556_c[5] * this.field_78555_b[7]
         + this.field_78556_c[6] * this.field_78555_b[11]
         + this.field_78556_c[7] * this.field_78555_b[15];
      this.field_78554_d[8] = this.field_78556_c[8] * this.field_78555_b[0]
         + this.field_78556_c[9] * this.field_78555_b[4]
         + this.field_78556_c[10] * this.field_78555_b[8]
         + this.field_78556_c[11] * this.field_78555_b[12];
      this.field_78554_d[9] = this.field_78556_c[8] * this.field_78555_b[1]
         + this.field_78556_c[9] * this.field_78555_b[5]
         + this.field_78556_c[10] * this.field_78555_b[9]
         + this.field_78556_c[11] * this.field_78555_b[13];
      this.field_78554_d[10] = this.field_78556_c[8] * this.field_78555_b[2]
         + this.field_78556_c[9] * this.field_78555_b[6]
         + this.field_78556_c[10] * this.field_78555_b[10]
         + this.field_78556_c[11] * this.field_78555_b[14];
      this.field_78554_d[11] = this.field_78556_c[8] * this.field_78555_b[3]
         + this.field_78556_c[9] * this.field_78555_b[7]
         + this.field_78556_c[10] * this.field_78555_b[11]
         + this.field_78556_c[11] * this.field_78555_b[15];
      this.field_78554_d[12] = this.field_78556_c[12] * this.field_78555_b[0]
         + this.field_78556_c[13] * this.field_78555_b[4]
         + this.field_78556_c[14] * this.field_78555_b[8]
         + this.field_78556_c[15] * this.field_78555_b[12];
      this.field_78554_d[13] = this.field_78556_c[12] * this.field_78555_b[1]
         + this.field_78556_c[13] * this.field_78555_b[5]
         + this.field_78556_c[14] * this.field_78555_b[9]
         + this.field_78556_c[15] * this.field_78555_b[13];
      this.field_78554_d[14] = this.field_78556_c[12] * this.field_78555_b[2]
         + this.field_78556_c[13] * this.field_78555_b[6]
         + this.field_78556_c[14] * this.field_78555_b[10]
         + this.field_78556_c[15] * this.field_78555_b[14];
      this.field_78554_d[15] = this.field_78556_c[12] * this.field_78555_b[3]
         + this.field_78556_c[13] * this.field_78555_b[7]
         + this.field_78556_c[14] * this.field_78555_b[11]
         + this.field_78556_c[15] * this.field_78555_b[15];
      this.field_78557_a[0][0] = this.field_78554_d[3] - this.field_78554_d[0];
      this.field_78557_a[0][1] = this.field_78554_d[7] - this.field_78554_d[4];
      this.field_78557_a[0][2] = this.field_78554_d[11] - this.field_78554_d[8];
      this.field_78557_a[0][3] = this.field_78554_d[15] - this.field_78554_d[12];
      this.func_78559_a(this.field_78557_a, 0);
      this.field_78557_a[1][0] = this.field_78554_d[3] + this.field_78554_d[0];
      this.field_78557_a[1][1] = this.field_78554_d[7] + this.field_78554_d[4];
      this.field_78557_a[1][2] = this.field_78554_d[11] + this.field_78554_d[8];
      this.field_78557_a[1][3] = this.field_78554_d[15] + this.field_78554_d[12];
      this.func_78559_a(this.field_78557_a, 1);
      this.field_78557_a[2][0] = this.field_78554_d[3] + this.field_78554_d[1];
      this.field_78557_a[2][1] = this.field_78554_d[7] + this.field_78554_d[5];
      this.field_78557_a[2][2] = this.field_78554_d[11] + this.field_78554_d[9];
      this.field_78557_a[2][3] = this.field_78554_d[15] + this.field_78554_d[13];
      this.func_78559_a(this.field_78557_a, 2);
      this.field_78557_a[3][0] = this.field_78554_d[3] - this.field_78554_d[1];
      this.field_78557_a[3][1] = this.field_78554_d[7] - this.field_78554_d[5];
      this.field_78557_a[3][2] = this.field_78554_d[11] - this.field_78554_d[9];
      this.field_78557_a[3][3] = this.field_78554_d[15] - this.field_78554_d[13];
      this.func_78559_a(this.field_78557_a, 3);
      this.field_78557_a[4][0] = this.field_78554_d[3] - this.field_78554_d[2];
      this.field_78557_a[4][1] = this.field_78554_d[7] - this.field_78554_d[6];
      this.field_78557_a[4][2] = this.field_78554_d[11] - this.field_78554_d[10];
      this.field_78557_a[4][3] = this.field_78554_d[15] - this.field_78554_d[14];
      this.func_78559_a(this.field_78557_a, 4);
      this.field_78557_a[5][0] = this.field_78554_d[3] + this.field_78554_d[2];
      this.field_78557_a[5][1] = this.field_78554_d[7] + this.field_78554_d[6];
      this.field_78557_a[5][2] = this.field_78554_d[11] + this.field_78554_d[10];
      this.field_78557_a[5][3] = this.field_78554_d[15] + this.field_78554_d[14];
      this.func_78559_a(this.field_78557_a, 5);
   }
}
