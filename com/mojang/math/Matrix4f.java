package com.mojang.math;

import java.nio.FloatBuffer;

public final class Matrix4f {
   protected float m00;
   protected float m01;
   protected float m02;
   protected float m03;
   protected float m10;
   protected float m11;
   protected float m12;
   protected float m13;
   protected float m20;
   protected float m21;
   protected float m22;
   protected float m23;
   protected float m30;
   protected float m31;
   protected float m32;
   protected float m33;

   public Matrix4f() {
   }

   public Matrix4f(Matrix4f var1) {
      this.m00 = var1.m00;
      this.m01 = var1.m01;
      this.m02 = var1.m02;
      this.m03 = var1.m03;
      this.m10 = var1.m10;
      this.m11 = var1.m11;
      this.m12 = var1.m12;
      this.m13 = var1.m13;
      this.m20 = var1.m20;
      this.m21 = var1.m21;
      this.m22 = var1.m22;
      this.m23 = var1.m23;
      this.m30 = var1.m30;
      this.m31 = var1.m31;
      this.m32 = var1.m32;
      this.m33 = var1.m33;
   }

   public Matrix4f(Quaternion var1) {
      float var2 = var1.i();
      float var3 = var1.j();
      float var4 = var1.k();
      float var5 = var1.r();
      float var6 = 2.0F * var2 * var2;
      float var7 = 2.0F * var3 * var3;
      float var8 = 2.0F * var4 * var4;
      this.m00 = 1.0F - var7 - var8;
      this.m11 = 1.0F - var8 - var6;
      this.m22 = 1.0F - var6 - var7;
      this.m33 = 1.0F;
      float var9 = var2 * var3;
      float var10 = var3 * var4;
      float var11 = var4 * var2;
      float var12 = var2 * var5;
      float var13 = var3 * var5;
      float var14 = var4 * var5;
      this.m10 = 2.0F * (var9 + var14);
      this.m01 = 2.0F * (var9 - var14);
      this.m20 = 2.0F * (var11 - var13);
      this.m02 = 2.0F * (var11 + var13);
      this.m21 = 2.0F * (var10 + var12);
      this.m12 = 2.0F * (var10 - var12);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Matrix4f var2 = (Matrix4f)var1;
         return Float.compare(var2.m00, this.m00) == 0 && Float.compare(var2.m01, this.m01) == 0 && Float.compare(var2.m02, this.m02) == 0 && Float.compare(var2.m03, this.m03) == 0 && Float.compare(var2.m10, this.m10) == 0 && Float.compare(var2.m11, this.m11) == 0 && Float.compare(var2.m12, this.m12) == 0 && Float.compare(var2.m13, this.m13) == 0 && Float.compare(var2.m20, this.m20) == 0 && Float.compare(var2.m21, this.m21) == 0 && Float.compare(var2.m22, this.m22) == 0 && Float.compare(var2.m23, this.m23) == 0 && Float.compare(var2.m30, this.m30) == 0 && Float.compare(var2.m31, this.m31) == 0 && Float.compare(var2.m32, this.m32) == 0 && Float.compare(var2.m33, this.m33) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.m00 != 0.0F ? Float.floatToIntBits(this.m00) : 0;
      var1 = 31 * var1 + (this.m01 != 0.0F ? Float.floatToIntBits(this.m01) : 0);
      var1 = 31 * var1 + (this.m02 != 0.0F ? Float.floatToIntBits(this.m02) : 0);
      var1 = 31 * var1 + (this.m03 != 0.0F ? Float.floatToIntBits(this.m03) : 0);
      var1 = 31 * var1 + (this.m10 != 0.0F ? Float.floatToIntBits(this.m10) : 0);
      var1 = 31 * var1 + (this.m11 != 0.0F ? Float.floatToIntBits(this.m11) : 0);
      var1 = 31 * var1 + (this.m12 != 0.0F ? Float.floatToIntBits(this.m12) : 0);
      var1 = 31 * var1 + (this.m13 != 0.0F ? Float.floatToIntBits(this.m13) : 0);
      var1 = 31 * var1 + (this.m20 != 0.0F ? Float.floatToIntBits(this.m20) : 0);
      var1 = 31 * var1 + (this.m21 != 0.0F ? Float.floatToIntBits(this.m21) : 0);
      var1 = 31 * var1 + (this.m22 != 0.0F ? Float.floatToIntBits(this.m22) : 0);
      var1 = 31 * var1 + (this.m23 != 0.0F ? Float.floatToIntBits(this.m23) : 0);
      var1 = 31 * var1 + (this.m30 != 0.0F ? Float.floatToIntBits(this.m30) : 0);
      var1 = 31 * var1 + (this.m31 != 0.0F ? Float.floatToIntBits(this.m31) : 0);
      var1 = 31 * var1 + (this.m32 != 0.0F ? Float.floatToIntBits(this.m32) : 0);
      var1 = 31 * var1 + (this.m33 != 0.0F ? Float.floatToIntBits(this.m33) : 0);
      return var1;
   }

   private static int bufferIndex(int var0, int var1) {
      return var1 * 4 + var0;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Matrix4f:\n");
      var1.append(this.m00);
      var1.append(" ");
      var1.append(this.m01);
      var1.append(" ");
      var1.append(this.m02);
      var1.append(" ");
      var1.append(this.m03);
      var1.append("\n");
      var1.append(this.m10);
      var1.append(" ");
      var1.append(this.m11);
      var1.append(" ");
      var1.append(this.m12);
      var1.append(" ");
      var1.append(this.m13);
      var1.append("\n");
      var1.append(this.m20);
      var1.append(" ");
      var1.append(this.m21);
      var1.append(" ");
      var1.append(this.m22);
      var1.append(" ");
      var1.append(this.m23);
      var1.append("\n");
      var1.append(this.m30);
      var1.append(" ");
      var1.append(this.m31);
      var1.append(" ");
      var1.append(this.m32);
      var1.append(" ");
      var1.append(this.m33);
      var1.append("\n");
      return var1.toString();
   }

   public void store(FloatBuffer var1) {
      var1.put(bufferIndex(0, 0), this.m00);
      var1.put(bufferIndex(0, 1), this.m01);
      var1.put(bufferIndex(0, 2), this.m02);
      var1.put(bufferIndex(0, 3), this.m03);
      var1.put(bufferIndex(1, 0), this.m10);
      var1.put(bufferIndex(1, 1), this.m11);
      var1.put(bufferIndex(1, 2), this.m12);
      var1.put(bufferIndex(1, 3), this.m13);
      var1.put(bufferIndex(2, 0), this.m20);
      var1.put(bufferIndex(2, 1), this.m21);
      var1.put(bufferIndex(2, 2), this.m22);
      var1.put(bufferIndex(2, 3), this.m23);
      var1.put(bufferIndex(3, 0), this.m30);
      var1.put(bufferIndex(3, 1), this.m31);
      var1.put(bufferIndex(3, 2), this.m32);
      var1.put(bufferIndex(3, 3), this.m33);
   }

   public void setIdentity() {
      this.m00 = 1.0F;
      this.m01 = 0.0F;
      this.m02 = 0.0F;
      this.m03 = 0.0F;
      this.m10 = 0.0F;
      this.m11 = 1.0F;
      this.m12 = 0.0F;
      this.m13 = 0.0F;
      this.m20 = 0.0F;
      this.m21 = 0.0F;
      this.m22 = 1.0F;
      this.m23 = 0.0F;
      this.m30 = 0.0F;
      this.m31 = 0.0F;
      this.m32 = 0.0F;
      this.m33 = 1.0F;
   }

   public float adjugateAndDet() {
      float var1 = this.m00 * this.m11 - this.m01 * this.m10;
      float var2 = this.m00 * this.m12 - this.m02 * this.m10;
      float var3 = this.m00 * this.m13 - this.m03 * this.m10;
      float var4 = this.m01 * this.m12 - this.m02 * this.m11;
      float var5 = this.m01 * this.m13 - this.m03 * this.m11;
      float var6 = this.m02 * this.m13 - this.m03 * this.m12;
      float var7 = this.m20 * this.m31 - this.m21 * this.m30;
      float var8 = this.m20 * this.m32 - this.m22 * this.m30;
      float var9 = this.m20 * this.m33 - this.m23 * this.m30;
      float var10 = this.m21 * this.m32 - this.m22 * this.m31;
      float var11 = this.m21 * this.m33 - this.m23 * this.m31;
      float var12 = this.m22 * this.m33 - this.m23 * this.m32;
      float var13 = this.m11 * var12 - this.m12 * var11 + this.m13 * var10;
      float var14 = -this.m10 * var12 + this.m12 * var9 - this.m13 * var8;
      float var15 = this.m10 * var11 - this.m11 * var9 + this.m13 * var7;
      float var16 = -this.m10 * var10 + this.m11 * var8 - this.m12 * var7;
      float var17 = -this.m01 * var12 + this.m02 * var11 - this.m03 * var10;
      float var18 = this.m00 * var12 - this.m02 * var9 + this.m03 * var8;
      float var19 = -this.m00 * var11 + this.m01 * var9 - this.m03 * var7;
      float var20 = this.m00 * var10 - this.m01 * var8 + this.m02 * var7;
      float var21 = this.m31 * var6 - this.m32 * var5 + this.m33 * var4;
      float var22 = -this.m30 * var6 + this.m32 * var3 - this.m33 * var2;
      float var23 = this.m30 * var5 - this.m31 * var3 + this.m33 * var1;
      float var24 = -this.m30 * var4 + this.m31 * var2 - this.m32 * var1;
      float var25 = -this.m21 * var6 + this.m22 * var5 - this.m23 * var4;
      float var26 = this.m20 * var6 - this.m22 * var3 + this.m23 * var2;
      float var27 = -this.m20 * var5 + this.m21 * var3 - this.m23 * var1;
      float var28 = this.m20 * var4 - this.m21 * var2 + this.m22 * var1;
      this.m00 = var13;
      this.m10 = var14;
      this.m20 = var15;
      this.m30 = var16;
      this.m01 = var17;
      this.m11 = var18;
      this.m21 = var19;
      this.m31 = var20;
      this.m02 = var21;
      this.m12 = var22;
      this.m22 = var23;
      this.m32 = var24;
      this.m03 = var25;
      this.m13 = var26;
      this.m23 = var27;
      this.m33 = var28;
      return var1 * var12 - var2 * var11 + var3 * var10 + var4 * var9 - var5 * var8 + var6 * var7;
   }

   public void transpose() {
      float var1 = this.m10;
      this.m10 = this.m01;
      this.m01 = var1;
      var1 = this.m20;
      this.m20 = this.m02;
      this.m02 = var1;
      var1 = this.m21;
      this.m21 = this.m12;
      this.m12 = var1;
      var1 = this.m30;
      this.m30 = this.m03;
      this.m03 = var1;
      var1 = this.m31;
      this.m31 = this.m13;
      this.m13 = var1;
      var1 = this.m32;
      this.m32 = this.m23;
      this.m23 = var1;
   }

   public boolean invert() {
      float var1 = this.adjugateAndDet();
      if (Math.abs(var1) > 1.0E-6F) {
         this.multiply(var1);
         return true;
      } else {
         return false;
      }
   }

   public void multiply(Matrix4f var1) {
      float var2 = this.m00 * var1.m00 + this.m01 * var1.m10 + this.m02 * var1.m20 + this.m03 * var1.m30;
      float var3 = this.m00 * var1.m01 + this.m01 * var1.m11 + this.m02 * var1.m21 + this.m03 * var1.m31;
      float var4 = this.m00 * var1.m02 + this.m01 * var1.m12 + this.m02 * var1.m22 + this.m03 * var1.m32;
      float var5 = this.m00 * var1.m03 + this.m01 * var1.m13 + this.m02 * var1.m23 + this.m03 * var1.m33;
      float var6 = this.m10 * var1.m00 + this.m11 * var1.m10 + this.m12 * var1.m20 + this.m13 * var1.m30;
      float var7 = this.m10 * var1.m01 + this.m11 * var1.m11 + this.m12 * var1.m21 + this.m13 * var1.m31;
      float var8 = this.m10 * var1.m02 + this.m11 * var1.m12 + this.m12 * var1.m22 + this.m13 * var1.m32;
      float var9 = this.m10 * var1.m03 + this.m11 * var1.m13 + this.m12 * var1.m23 + this.m13 * var1.m33;
      float var10 = this.m20 * var1.m00 + this.m21 * var1.m10 + this.m22 * var1.m20 + this.m23 * var1.m30;
      float var11 = this.m20 * var1.m01 + this.m21 * var1.m11 + this.m22 * var1.m21 + this.m23 * var1.m31;
      float var12 = this.m20 * var1.m02 + this.m21 * var1.m12 + this.m22 * var1.m22 + this.m23 * var1.m32;
      float var13 = this.m20 * var1.m03 + this.m21 * var1.m13 + this.m22 * var1.m23 + this.m23 * var1.m33;
      float var14 = this.m30 * var1.m00 + this.m31 * var1.m10 + this.m32 * var1.m20 + this.m33 * var1.m30;
      float var15 = this.m30 * var1.m01 + this.m31 * var1.m11 + this.m32 * var1.m21 + this.m33 * var1.m31;
      float var16 = this.m30 * var1.m02 + this.m31 * var1.m12 + this.m32 * var1.m22 + this.m33 * var1.m32;
      float var17 = this.m30 * var1.m03 + this.m31 * var1.m13 + this.m32 * var1.m23 + this.m33 * var1.m33;
      this.m00 = var2;
      this.m01 = var3;
      this.m02 = var4;
      this.m03 = var5;
      this.m10 = var6;
      this.m11 = var7;
      this.m12 = var8;
      this.m13 = var9;
      this.m20 = var10;
      this.m21 = var11;
      this.m22 = var12;
      this.m23 = var13;
      this.m30 = var14;
      this.m31 = var15;
      this.m32 = var16;
      this.m33 = var17;
   }

   public void multiply(Quaternion var1) {
      this.multiply(new Matrix4f(var1));
   }

   public void multiply(float var1) {
      this.m00 *= var1;
      this.m01 *= var1;
      this.m02 *= var1;
      this.m03 *= var1;
      this.m10 *= var1;
      this.m11 *= var1;
      this.m12 *= var1;
      this.m13 *= var1;
      this.m20 *= var1;
      this.m21 *= var1;
      this.m22 *= var1;
      this.m23 *= var1;
      this.m30 *= var1;
      this.m31 *= var1;
      this.m32 *= var1;
      this.m33 *= var1;
   }

   public static Matrix4f perspective(double var0, float var2, float var3, float var4) {
      float var5 = (float)(1.0D / Math.tan(var0 * 0.01745329238474369D / 2.0D));
      Matrix4f var6 = new Matrix4f();
      var6.m00 = var5 / var2;
      var6.m11 = var5;
      var6.m22 = (var4 + var3) / (var3 - var4);
      var6.m32 = -1.0F;
      var6.m23 = 2.0F * var4 * var3 / (var3 - var4);
      return var6;
   }

   public static Matrix4f orthographic(float var0, float var1, float var2, float var3) {
      Matrix4f var4 = new Matrix4f();
      var4.m00 = 2.0F / var0;
      var4.m11 = 2.0F / var1;
      float var5 = var3 - var2;
      var4.m22 = -2.0F / var5;
      var4.m33 = 1.0F;
      var4.m03 = -1.0F;
      var4.m13 = -1.0F;
      var4.m23 = -(var3 + var2) / var5;
      return var4;
   }

   public void translate(Vector3f var1) {
      this.m03 += var1.x();
      this.m13 += var1.y();
      this.m23 += var1.z();
   }

   public Matrix4f copy() {
      return new Matrix4f(this);
   }

   public static Matrix4f createScaleMatrix(float var0, float var1, float var2) {
      Matrix4f var3 = new Matrix4f();
      var3.m00 = var0;
      var3.m11 = var1;
      var3.m22 = var2;
      var3.m33 = 1.0F;
      return var3;
   }

   public static Matrix4f createTranslateMatrix(float var0, float var1, float var2) {
      Matrix4f var3 = new Matrix4f();
      var3.m00 = 1.0F;
      var3.m11 = 1.0F;
      var3.m22 = 1.0F;
      var3.m33 = 1.0F;
      var3.m03 = var0;
      var3.m13 = var1;
      var3.m23 = var2;
      return var3;
   }
}
