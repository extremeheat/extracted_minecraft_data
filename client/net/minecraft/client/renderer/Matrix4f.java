package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.util.Arrays;

public final class Matrix4f {
   private final float[] field_195888_a;

   public Matrix4f() {
      super();
      this.field_195888_a = new float[16];
   }

   public Matrix4f(Quaternion var1) {
      this();
      float var2 = var1.func_195889_a();
      float var3 = var1.func_195891_b();
      float var4 = var1.func_195893_c();
      float var5 = var1.func_195894_d();
      float var6 = 2.0F * var2 * var2;
      float var7 = 2.0F * var3 * var3;
      float var8 = 2.0F * var4 * var4;
      this.field_195888_a[0] = 1.0F - var7 - var8;
      this.field_195888_a[5] = 1.0F - var8 - var6;
      this.field_195888_a[10] = 1.0F - var6 - var7;
      this.field_195888_a[15] = 1.0F;
      float var9 = var2 * var3;
      float var10 = var3 * var4;
      float var11 = var4 * var2;
      float var12 = var2 * var5;
      float var13 = var3 * var5;
      float var14 = var4 * var5;
      this.field_195888_a[1] = 2.0F * (var9 + var14);
      this.field_195888_a[4] = 2.0F * (var9 - var14);
      this.field_195888_a[2] = 2.0F * (var11 - var13);
      this.field_195888_a[8] = 2.0F * (var11 + var13);
      this.field_195888_a[6] = 2.0F * (var10 + var12);
      this.field_195888_a[9] = 2.0F * (var10 - var12);
   }

   public Matrix4f(Matrix4f var1) {
      super();
      this.field_195888_a = Arrays.copyOf(var1.field_195888_a, 16);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Matrix4f var2 = (Matrix4f)var1;
         return Arrays.equals(this.field_195888_a, var2.field_195888_a);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.field_195888_a);
   }

   public void func_195874_a(FloatBuffer var1) {
      this.func_195883_a(var1, false);
   }

   public void func_195883_a(FloatBuffer var1, boolean var2) {
      if (var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            for(int var4 = 0; var4 < 4; ++var4) {
               this.field_195888_a[var3 * 4 + var4] = var1.get(var4 * 4 + var3);
            }
         }
      } else {
         var1.get(this.field_195888_a);
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Matrix4f:\n");

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            var1.append(this.field_195888_a[var2 + var3 * 4]);
            if (var3 != 3) {
               var1.append(" ");
            }
         }

         var1.append("\n");
      }

      return var1.toString();
   }

   public void func_195879_b(FloatBuffer var1) {
      this.func_195873_b(var1, false);
   }

   public void func_195873_b(FloatBuffer var1, boolean var2) {
      if (var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            for(int var4 = 0; var4 < 4; ++var4) {
               var1.put(var4 * 4 + var3, this.field_195888_a[var3 * 4 + var4]);
            }
         }
      } else {
         var1.put(this.field_195888_a);
      }

   }

   public void func_195884_a() {
      this.field_195888_a[0] = 1.0F;
      this.field_195888_a[1] = 0.0F;
      this.field_195888_a[2] = 0.0F;
      this.field_195888_a[3] = 0.0F;
      this.field_195888_a[4] = 0.0F;
      this.field_195888_a[5] = 1.0F;
      this.field_195888_a[6] = 0.0F;
      this.field_195888_a[7] = 0.0F;
      this.field_195888_a[8] = 0.0F;
      this.field_195888_a[9] = 0.0F;
      this.field_195888_a[10] = 1.0F;
      this.field_195888_a[11] = 0.0F;
      this.field_195888_a[12] = 0.0F;
      this.field_195888_a[13] = 0.0F;
      this.field_195888_a[14] = 0.0F;
      this.field_195888_a[15] = 1.0F;
   }

   public float func_195885_a(int var1, int var2) {
      return this.field_195888_a[var1 + 4 * var2];
   }

   public void func_195878_a(int var1, int var2, float var3) {
      this.field_195888_a[var1 + 4 * var2] = var3;
   }

   public void func_195882_a(Matrix4f var1) {
      float[] var2 = Arrays.copyOf(this.field_195888_a, 16);

      for(int var3 = 0; var3 < 4; ++var3) {
         for(int var4 = 0; var4 < 4; ++var4) {
            this.field_195888_a[var3 + var4 * 4] = 0.0F;

            for(int var5 = 0; var5 < 4; ++var5) {
               float[] var10000 = this.field_195888_a;
               var10000[var3 + var4 * 4] += var2[var3 + var5 * 4] * var1.field_195888_a[var5 + var4 * 4];
            }
         }
      }

   }

   public void func_195875_a(float var1) {
      for(int var2 = 0; var2 < 16; ++var2) {
         float[] var10000 = this.field_195888_a;
         var10000[var2] *= var1;
      }

   }

   public void func_195880_b(Matrix4f var1) {
      for(int var2 = 0; var2 < 16; ++var2) {
         float[] var10000 = this.field_195888_a;
         var10000[var2] += var1.field_195888_a[var2];
      }

   }

   public void func_195886_c(Matrix4f var1) {
      for(int var2 = 0; var2 < 16; ++var2) {
         float[] var10000 = this.field_195888_a;
         var10000[var2] -= var1.field_195888_a[var2];
      }

   }

   public float func_195881_b() {
      float var1 = 0.0F;

      for(int var2 = 0; var2 < 4; ++var2) {
         var1 += this.field_195888_a[var2 + 4 * var2];
      }

      return var1;
   }

   public void func_195887_c() {
      Matrix4f var1 = new Matrix4f();
      Matrix4f var2 = new Matrix4f(this);
      Matrix4f var3 = new Matrix4f(this);
      var2.func_195882_a(this);
      var3.func_195882_a(var2);
      float var4 = this.func_195881_b();
      float var5 = var2.func_195881_b();
      float var6 = var3.func_195881_b();
      this.func_195875_a((var5 - var4 * var4) / 2.0F);
      var1.func_195884_a();
      var1.func_195875_a((var4 * var4 * var4 - 3.0F * var4 * var5 + 2.0F * var6) / 6.0F);
      this.func_195880_b(var1);
      var2.func_195875_a(var4);
      this.func_195880_b(var2);
      this.func_195886_c(var3);
   }

   public static Matrix4f func_195876_a(double var0, float var2, float var3, float var4) {
      float var5 = (float)(1.0D / Math.tan(var0 * 0.01745329238474369D / 2.0D));
      Matrix4f var6 = new Matrix4f();
      var6.func_195878_a(0, 0, var5 / var2);
      var6.func_195878_a(1, 1, var5);
      var6.func_195878_a(2, 2, (var4 + var3) / (var3 - var4));
      var6.func_195878_a(3, 2, -1.0F);
      var6.func_195878_a(2, 3, 2.0F * var4 * var3 / (var3 - var4));
      return var6;
   }

   public static Matrix4f func_195877_a(float var0, float var1, float var2, float var3) {
      Matrix4f var4 = new Matrix4f();
      var4.func_195878_a(0, 0, 2.0F / var0);
      var4.func_195878_a(1, 1, 2.0F / var1);
      float var5 = var3 - var2;
      var4.func_195878_a(2, 2, -2.0F / var5);
      var4.func_195878_a(3, 3, 1.0F);
      var4.func_195878_a(0, 3, -1.0F);
      var4.func_195878_a(1, 3, -1.0F);
      var4.func_195878_a(2, 3, -(var3 + var2) / var5);
      return var4;
   }
}
