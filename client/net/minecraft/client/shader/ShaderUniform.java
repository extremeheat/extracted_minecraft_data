package net.minecraft.client.shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderUniform {
   private static final Logger field_148104_a = LogManager.getLogger();
   private int field_148102_b;
   private final int field_148103_c;
   private final int field_148100_d;
   private final IntBuffer field_148101_e;
   private final FloatBuffer field_148098_f;
   private final String field_148099_g;
   private boolean field_148105_h;
   private final ShaderManager field_148106_i;

   public ShaderUniform(String var1, int var2, int var3, ShaderManager var4) {
      super();
      this.field_148099_g = var1;
      this.field_148103_c = var3;
      this.field_148100_d = var2;
      this.field_148106_i = var4;
      if (var2 <= 3) {
         this.field_148101_e = BufferUtils.createIntBuffer(var3);
         this.field_148098_f = null;
      } else {
         this.field_148101_e = null;
         this.field_148098_f = BufferUtils.createFloatBuffer(var3);
      }

      this.field_148102_b = -1;
      this.func_148096_h();
   }

   private void func_148096_h() {
      this.field_148105_h = true;
      if (this.field_148106_i != null) {
         this.field_148106_i.func_147985_d();
      }

   }

   public static int func_148085_a(String var0) {
      byte var1 = -1;
      if (var0.equals("int")) {
         var1 = 0;
      } else if (var0.equals("float")) {
         var1 = 4;
      } else if (var0.startsWith("matrix")) {
         if (var0.endsWith("2x2")) {
            var1 = 8;
         } else if (var0.endsWith("3x3")) {
            var1 = 9;
         } else if (var0.endsWith("4x4")) {
            var1 = 10;
         }
      }

      return var1;
   }

   public void func_148084_b(int var1) {
      this.field_148102_b = var1;
   }

   public String func_148086_a() {
      return this.field_148099_g;
   }

   public void func_148090_a(float var1) {
      this.field_148098_f.position(0);
      this.field_148098_f.put(0, var1);
      this.func_148096_h();
   }

   public void func_148087_a(float var1, float var2) {
      this.field_148098_f.position(0);
      this.field_148098_f.put(0, var1);
      this.field_148098_f.put(1, var2);
      this.func_148096_h();
   }

   public void func_148095_a(float var1, float var2, float var3) {
      this.field_148098_f.position(0);
      this.field_148098_f.put(0, var1);
      this.field_148098_f.put(1, var2);
      this.field_148098_f.put(2, var3);
      this.func_148096_h();
   }

   public void func_148081_a(float var1, float var2, float var3, float var4) {
      this.field_148098_f.position(0);
      this.field_148098_f.put(var1);
      this.field_148098_f.put(var2);
      this.field_148098_f.put(var3);
      this.field_148098_f.put(var4);
      this.field_148098_f.flip();
      this.func_148096_h();
   }

   public void func_148092_b(float var1, float var2, float var3, float var4) {
      this.field_148098_f.position(0);
      if (this.field_148100_d >= 4) {
         this.field_148098_f.put(0, var1);
      }

      if (this.field_148100_d >= 5) {
         this.field_148098_f.put(1, var2);
      }

      if (this.field_148100_d >= 6) {
         this.field_148098_f.put(2, var3);
      }

      if (this.field_148100_d >= 7) {
         this.field_148098_f.put(3, var4);
      }

      this.func_148096_h();
   }

   public void func_148083_a(int var1, int var2, int var3, int var4) {
      this.field_148101_e.position(0);
      if (this.field_148100_d >= 0) {
         this.field_148101_e.put(0, var1);
      }

      if (this.field_148100_d >= 1) {
         this.field_148101_e.put(1, var2);
      }

      if (this.field_148100_d >= 2) {
         this.field_148101_e.put(2, var3);
      }

      if (this.field_148100_d >= 3) {
         this.field_148101_e.put(3, var4);
      }

      this.func_148096_h();
   }

   public void func_148097_a(float[] var1) {
      if (var1.length < this.field_148103_c) {
         field_148104_a.warn("Uniform.set called with a too-small value array (expected " + this.field_148103_c + ", got " + var1.length + "). Ignoring.");
      } else {
         this.field_148098_f.position(0);
         this.field_148098_f.put(var1);
         this.field_148098_f.position(0);
         this.func_148096_h();
      }
   }

   public void func_148094_a(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16) {
      this.field_148098_f.position(0);
      this.field_148098_f.put(0, var1);
      this.field_148098_f.put(1, var2);
      this.field_148098_f.put(2, var3);
      this.field_148098_f.put(3, var4);
      this.field_148098_f.put(4, var5);
      this.field_148098_f.put(5, var6);
      this.field_148098_f.put(6, var7);
      this.field_148098_f.put(7, var8);
      this.field_148098_f.put(8, var9);
      this.field_148098_f.put(9, var10);
      this.field_148098_f.put(10, var11);
      this.field_148098_f.put(11, var12);
      this.field_148098_f.put(12, var13);
      this.field_148098_f.put(13, var14);
      this.field_148098_f.put(14, var15);
      this.field_148098_f.put(15, var16);
      this.func_148096_h();
   }

   public void func_148088_a(Matrix4f var1) {
      this.func_148094_a(var1.m00, var1.m01, var1.m02, var1.m03, var1.m10, var1.m11, var1.m12, var1.m13, var1.m20, var1.m21, var1.m22, var1.m23, var1.m30, var1.m31, var1.m32, var1.m33);
   }

   public void func_148093_b() {
      if (!this.field_148105_h) {
      }

      this.field_148105_h = false;
      if (this.field_148100_d <= 3) {
         this.func_148091_i();
      } else if (this.field_148100_d <= 7) {
         this.func_148089_j();
      } else {
         if (this.field_148100_d > 10) {
            field_148104_a.warn("Uniform.upload called, but type value (" + this.field_148100_d + ") is not " + "a valid type. Ignoring.");
            return;
         }

         this.func_148082_k();
      }

   }

   private void func_148091_i() {
      switch(this.field_148100_d) {
      case 0:
         OpenGlHelper.func_153181_a(this.field_148102_b, this.field_148101_e);
         break;
      case 1:
         OpenGlHelper.func_153182_b(this.field_148102_b, this.field_148101_e);
         break;
      case 2:
         OpenGlHelper.func_153192_c(this.field_148102_b, this.field_148101_e);
         break;
      case 3:
         OpenGlHelper.func_153162_d(this.field_148102_b, this.field_148101_e);
         break;
      default:
         field_148104_a.warn("Uniform.upload called, but count value (" + this.field_148103_c + ") is " + " not in the range of 1 to 4. Ignoring.");
      }

   }

   private void func_148089_j() {
      switch(this.field_148100_d) {
      case 4:
         OpenGlHelper.func_153168_a(this.field_148102_b, this.field_148098_f);
         break;
      case 5:
         OpenGlHelper.func_153177_b(this.field_148102_b, this.field_148098_f);
         break;
      case 6:
         OpenGlHelper.func_153191_c(this.field_148102_b, this.field_148098_f);
         break;
      case 7:
         OpenGlHelper.func_153159_d(this.field_148102_b, this.field_148098_f);
         break;
      default:
         field_148104_a.warn("Uniform.upload called, but count value (" + this.field_148103_c + ") is " + "not in the range of 1 to 4. Ignoring.");
      }

   }

   private void func_148082_k() {
      switch(this.field_148100_d) {
      case 8:
         OpenGlHelper.func_153173_a(this.field_148102_b, true, this.field_148098_f);
         break;
      case 9:
         OpenGlHelper.func_153189_b(this.field_148102_b, true, this.field_148098_f);
         break;
      case 10:
         OpenGlHelper.func_153160_c(this.field_148102_b, true, this.field_148098_f);
      }

   }
}
