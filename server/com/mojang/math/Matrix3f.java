package com.mojang.math;

public final class Matrix3f {
   private static final float G = 3.0F + 2.0F * (float)Math.sqrt(2.0D);
   private static final float CS = (float)Math.cos(0.39269908169872414D);
   private static final float SS = (float)Math.sin(0.39269908169872414D);
   private static final float SQ2 = 1.0F / (float)Math.sqrt(2.0D);
   protected float m00;
   protected float m01;
   protected float m02;
   protected float m10;
   protected float m11;
   protected float m12;
   protected float m20;
   protected float m21;
   protected float m22;

   public Matrix3f() {
      super();
   }

   public Matrix3f(Quaternion var1) {
      super();
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

   public Matrix3f(Matrix4f var1) {
      super();
      this.m00 = var1.m00;
      this.m01 = var1.m01;
      this.m02 = var1.m02;
      this.m10 = var1.m10;
      this.m11 = var1.m11;
      this.m12 = var1.m12;
      this.m20 = var1.m20;
      this.m21 = var1.m21;
      this.m22 = var1.m22;
   }

   public Matrix3f(Matrix3f var1) {
      super();
      this.m00 = var1.m00;
      this.m01 = var1.m01;
      this.m02 = var1.m02;
      this.m10 = var1.m10;
      this.m11 = var1.m11;
      this.m12 = var1.m12;
      this.m20 = var1.m20;
      this.m21 = var1.m21;
      this.m22 = var1.m22;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Matrix3f var2 = (Matrix3f)var1;
         return Float.compare(var2.m00, this.m00) == 0 && Float.compare(var2.m01, this.m01) == 0 && Float.compare(var2.m02, this.m02) == 0 && Float.compare(var2.m10, this.m10) == 0 && Float.compare(var2.m11, this.m11) == 0 && Float.compare(var2.m12, this.m12) == 0 && Float.compare(var2.m20, this.m20) == 0 && Float.compare(var2.m21, this.m21) == 0 && Float.compare(var2.m22, this.m22) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.m00 != 0.0F ? Float.floatToIntBits(this.m00) : 0;
      var1 = 31 * var1 + (this.m01 != 0.0F ? Float.floatToIntBits(this.m01) : 0);
      var1 = 31 * var1 + (this.m02 != 0.0F ? Float.floatToIntBits(this.m02) : 0);
      var1 = 31 * var1 + (this.m10 != 0.0F ? Float.floatToIntBits(this.m10) : 0);
      var1 = 31 * var1 + (this.m11 != 0.0F ? Float.floatToIntBits(this.m11) : 0);
      var1 = 31 * var1 + (this.m12 != 0.0F ? Float.floatToIntBits(this.m12) : 0);
      var1 = 31 * var1 + (this.m20 != 0.0F ? Float.floatToIntBits(this.m20) : 0);
      var1 = 31 * var1 + (this.m21 != 0.0F ? Float.floatToIntBits(this.m21) : 0);
      var1 = 31 * var1 + (this.m22 != 0.0F ? Float.floatToIntBits(this.m22) : 0);
      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Matrix3f:\n");
      var1.append(this.m00);
      var1.append(" ");
      var1.append(this.m01);
      var1.append(" ");
      var1.append(this.m02);
      var1.append("\n");
      var1.append(this.m10);
      var1.append(" ");
      var1.append(this.m11);
      var1.append(" ");
      var1.append(this.m12);
      var1.append("\n");
      var1.append(this.m20);
      var1.append(" ");
      var1.append(this.m21);
      var1.append(" ");
      var1.append(this.m22);
      var1.append("\n");
      return var1.toString();
   }

   public void set(int var1, int var2, float var3) {
      if (var1 == 0) {
         if (var2 == 0) {
            this.m00 = var3;
         } else if (var2 == 1) {
            this.m01 = var3;
         } else {
            this.m02 = var3;
         }
      } else if (var1 == 1) {
         if (var2 == 0) {
            this.m10 = var3;
         } else if (var2 == 1) {
            this.m11 = var3;
         } else {
            this.m12 = var3;
         }
      } else if (var2 == 0) {
         this.m20 = var3;
      } else if (var2 == 1) {
         this.m21 = var3;
      } else {
         this.m22 = var3;
      }

   }

   public void mul(Matrix3f var1) {
      float var2 = this.m00 * var1.m00 + this.m01 * var1.m10 + this.m02 * var1.m20;
      float var3 = this.m00 * var1.m01 + this.m01 * var1.m11 + this.m02 * var1.m21;
      float var4 = this.m00 * var1.m02 + this.m01 * var1.m12 + this.m02 * var1.m22;
      float var5 = this.m10 * var1.m00 + this.m11 * var1.m10 + this.m12 * var1.m20;
      float var6 = this.m10 * var1.m01 + this.m11 * var1.m11 + this.m12 * var1.m21;
      float var7 = this.m10 * var1.m02 + this.m11 * var1.m12 + this.m12 * var1.m22;
      float var8 = this.m20 * var1.m00 + this.m21 * var1.m10 + this.m22 * var1.m20;
      float var9 = this.m20 * var1.m01 + this.m21 * var1.m11 + this.m22 * var1.m21;
      float var10 = this.m20 * var1.m02 + this.m21 * var1.m12 + this.m22 * var1.m22;
      this.m00 = var2;
      this.m01 = var3;
      this.m02 = var4;
      this.m10 = var5;
      this.m11 = var6;
      this.m12 = var7;
      this.m20 = var8;
      this.m21 = var9;
      this.m22 = var10;
   }
}
