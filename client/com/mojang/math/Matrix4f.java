package com.mojang.math;

import java.nio.FloatBuffer;
import java.util.Arrays;

public final class Matrix4f {
   private final float[] values;

   public Matrix4f() {
      super();
      this.values = new float[16];
   }

   public Matrix4f(Quaternion var1) {
      this();
      float var2 = var1.i();
      float var3 = var1.j();
      float var4 = var1.k();
      float var5 = var1.r();
      float var6 = 2.0F * var2 * var2;
      float var7 = 2.0F * var3 * var3;
      float var8 = 2.0F * var4 * var4;
      this.values[0] = 1.0F - var7 - var8;
      this.values[5] = 1.0F - var8 - var6;
      this.values[10] = 1.0F - var6 - var7;
      this.values[15] = 1.0F;
      float var9 = var2 * var3;
      float var10 = var3 * var4;
      float var11 = var4 * var2;
      float var12 = var2 * var5;
      float var13 = var3 * var5;
      float var14 = var4 * var5;
      this.values[1] = 2.0F * (var9 + var14);
      this.values[4] = 2.0F * (var9 - var14);
      this.values[2] = 2.0F * (var11 - var13);
      this.values[8] = 2.0F * (var11 + var13);
      this.values[6] = 2.0F * (var10 + var12);
      this.values[9] = 2.0F * (var10 - var12);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Matrix4f var2 = (Matrix4f)var1;
         return Arrays.equals(this.values, var2.values);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.values);
   }

   public void load(FloatBuffer var1) {
      this.load(var1, false);
   }

   public void load(FloatBuffer var1, boolean var2) {
      if (var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            for(int var4 = 0; var4 < 4; ++var4) {
               this.values[var3 * 4 + var4] = var1.get(var4 * 4 + var3);
            }
         }
      } else {
         var1.get(this.values);
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Matrix4f:\n");

      for(int var2 = 0; var2 < 4; ++var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            var1.append(this.values[var2 + var3 * 4]);
            if (var3 != 3) {
               var1.append(" ");
            }
         }

         var1.append("\n");
      }

      return var1.toString();
   }

   public void store(FloatBuffer var1) {
      this.store(var1, false);
   }

   public void store(FloatBuffer var1, boolean var2) {
      if (var2) {
         for(int var3 = 0; var3 < 4; ++var3) {
            for(int var4 = 0; var4 < 4; ++var4) {
               var1.put(var4 * 4 + var3, this.values[var3 * 4 + var4]);
            }
         }
      } else {
         var1.put(this.values);
      }

   }

   public void set(int var1, int var2, float var3) {
      this.values[var1 + 4 * var2] = var3;
   }

   public static Matrix4f perspective(double var0, float var2, float var3, float var4) {
      float var5 = (float)(1.0D / Math.tan(var0 * 0.01745329238474369D / 2.0D));
      Matrix4f var6 = new Matrix4f();
      var6.set(0, 0, var5 / var2);
      var6.set(1, 1, var5);
      var6.set(2, 2, (var4 + var3) / (var3 - var4));
      var6.set(3, 2, -1.0F);
      var6.set(2, 3, 2.0F * var4 * var3 / (var3 - var4));
      return var6;
   }

   public static Matrix4f orthographic(float var0, float var1, float var2, float var3) {
      Matrix4f var4 = new Matrix4f();
      var4.set(0, 0, 2.0F / var0);
      var4.set(1, 1, 2.0F / var1);
      float var5 = var3 - var2;
      var4.set(2, 2, -2.0F / var5);
      var4.set(3, 3, 1.0F);
      var4.set(0, 3, -1.0F);
      var4.set(1, 3, -1.0F);
      var4.set(2, 3, -(var3 + var2) / var5);
      return var4;
   }
}
