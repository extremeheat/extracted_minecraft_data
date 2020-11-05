package com.mojang.math;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Triple;

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

   public static Matrix3f createScaleMatrix(float var0, float var1, float var2) {
      Matrix3f var3 = new Matrix3f();
      var3.m00 = var0;
      var3.m11 = var1;
      var3.m22 = var2;
      return var3;
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

   private static Pair<Float, Float> approxGivensQuat(float var0, float var1, float var2) {
      float var3 = 2.0F * (var0 - var2);
      if (G * var1 * var1 < var3 * var3) {
         float var5 = Mth.fastInvSqrt(var1 * var1 + var3 * var3);
         return Pair.of(var5 * var1, var5 * var3);
      } else {
         return Pair.of(SS, CS);
      }
   }

   private static Pair<Float, Float> qrGivensQuat(float var0, float var1) {
      float var2 = (float)Math.hypot((double)var0, (double)var1);
      float var3 = var2 > 1.0E-6F ? var1 : 0.0F;
      float var4 = Math.abs(var0) + Math.max(var2, 1.0E-6F);
      float var5;
      if (var0 < 0.0F) {
         var5 = var3;
         var3 = var4;
         var4 = var5;
      }

      var5 = Mth.fastInvSqrt(var4 * var4 + var3 * var3);
      var4 *= var5;
      var3 *= var5;
      return Pair.of(var3, var4);
   }

   private static Quaternion stepJacobi(Matrix3f var0) {
      Matrix3f var1 = new Matrix3f();
      Quaternion var2 = Quaternion.ONE.copy();
      Pair var3;
      Float var4;
      Float var5;
      Quaternion var6;
      float var7;
      float var8;
      float var9;
      if (var0.m01 * var0.m01 + var0.m10 * var0.m10 > 1.0E-6F) {
         var3 = approxGivensQuat(var0.m00, 0.5F * (var0.m01 + var0.m10), var0.m11);
         var4 = (Float)var3.getFirst();
         var5 = (Float)var3.getSecond();
         var6 = new Quaternion(0.0F, 0.0F, var4, var5);
         var7 = var5 * var5 - var4 * var4;
         var8 = -2.0F * var4 * var5;
         var9 = var5 * var5 + var4 * var4;
         var2.mul(var6);
         var1.setIdentity();
         var1.m00 = var7;
         var1.m11 = var7;
         var1.m10 = -var8;
         var1.m01 = var8;
         var1.m22 = var9;
         var0.mul(var1);
         var1.transpose();
         var1.mul(var0);
         var0.load(var1);
      }

      if (var0.m02 * var0.m02 + var0.m20 * var0.m20 > 1.0E-6F) {
         var3 = approxGivensQuat(var0.m00, 0.5F * (var0.m02 + var0.m20), var0.m22);
         float var10 = -(Float)var3.getFirst();
         var5 = (Float)var3.getSecond();
         var6 = new Quaternion(0.0F, var10, 0.0F, var5);
         var7 = var5 * var5 - var10 * var10;
         var8 = -2.0F * var10 * var5;
         var9 = var5 * var5 + var10 * var10;
         var2.mul(var6);
         var1.setIdentity();
         var1.m00 = var7;
         var1.m22 = var7;
         var1.m20 = var8;
         var1.m02 = -var8;
         var1.m11 = var9;
         var0.mul(var1);
         var1.transpose();
         var1.mul(var0);
         var0.load(var1);
      }

      if (var0.m12 * var0.m12 + var0.m21 * var0.m21 > 1.0E-6F) {
         var3 = approxGivensQuat(var0.m11, 0.5F * (var0.m12 + var0.m21), var0.m22);
         var4 = (Float)var3.getFirst();
         var5 = (Float)var3.getSecond();
         var6 = new Quaternion(var4, 0.0F, 0.0F, var5);
         var7 = var5 * var5 - var4 * var4;
         var8 = -2.0F * var4 * var5;
         var9 = var5 * var5 + var4 * var4;
         var2.mul(var6);
         var1.setIdentity();
         var1.m11 = var7;
         var1.m22 = var7;
         var1.m21 = -var8;
         var1.m12 = var8;
         var1.m00 = var9;
         var0.mul(var1);
         var1.transpose();
         var1.mul(var0);
         var0.load(var1);
      }

      return var2;
   }

   public void transpose() {
      float var1 = this.m01;
      this.m01 = this.m10;
      this.m10 = var1;
      var1 = this.m02;
      this.m02 = this.m20;
      this.m20 = var1;
      var1 = this.m12;
      this.m12 = this.m21;
      this.m21 = var1;
   }

   public Triple<Quaternion, Vector3f, Quaternion> svdDecompose() {
      Quaternion var1 = Quaternion.ONE.copy();
      Quaternion var2 = Quaternion.ONE.copy();
      Matrix3f var3 = this.copy();
      var3.transpose();
      var3.mul(this);

      for(int var4 = 0; var4 < 5; ++var4) {
         var2.mul(stepJacobi(var3));
      }

      var2.normalize();
      Matrix3f var29 = new Matrix3f(this);
      var29.mul(new Matrix3f(var2));
      float var6 = 1.0F;
      Pair var5 = qrGivensQuat(var29.m00, var29.m10);
      Float var7 = (Float)var5.getFirst();
      Float var8 = (Float)var5.getSecond();
      float var9 = var8 * var8 - var7 * var7;
      float var10 = -2.0F * var7 * var8;
      float var11 = var8 * var8 + var7 * var7;
      Quaternion var12 = new Quaternion(0.0F, 0.0F, var7, var8);
      var1.mul(var12);
      Matrix3f var13 = new Matrix3f();
      var13.setIdentity();
      var13.m00 = var9;
      var13.m11 = var9;
      var13.m10 = var10;
      var13.m01 = -var10;
      var13.m22 = var11;
      var6 *= var11;
      var13.mul(var29);
      var5 = qrGivensQuat(var13.m00, var13.m20);
      float var14 = -(Float)var5.getFirst();
      Float var15 = (Float)var5.getSecond();
      float var16 = var15 * var15 - var14 * var14;
      float var17 = -2.0F * var14 * var15;
      float var18 = var15 * var15 + var14 * var14;
      Quaternion var19 = new Quaternion(0.0F, var14, 0.0F, var15);
      var1.mul(var19);
      Matrix3f var20 = new Matrix3f();
      var20.setIdentity();
      var20.m00 = var16;
      var20.m22 = var16;
      var20.m20 = -var17;
      var20.m02 = var17;
      var20.m11 = var18;
      var6 *= var18;
      var20.mul(var13);
      var5 = qrGivensQuat(var20.m11, var20.m21);
      Float var21 = (Float)var5.getFirst();
      Float var22 = (Float)var5.getSecond();
      float var23 = var22 * var22 - var21 * var21;
      float var24 = -2.0F * var21 * var22;
      float var25 = var22 * var22 + var21 * var21;
      Quaternion var26 = new Quaternion(var21, 0.0F, 0.0F, var22);
      var1.mul(var26);
      Matrix3f var27 = new Matrix3f();
      var27.setIdentity();
      var27.m11 = var23;
      var27.m22 = var23;
      var27.m21 = var24;
      var27.m12 = -var24;
      var27.m00 = var25;
      var6 *= var25;
      var27.mul(var20);
      var6 = 1.0F / var6;
      var1.mul((float)Math.sqrt((double)var6));
      Vector3f var28 = new Vector3f(var27.m00 * var6, var27.m11 * var6, var27.m22 * var6);
      return Triple.of(var1, var28, var2);
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

   public void load(Matrix3f var1) {
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

   public void setIdentity() {
      this.m00 = 1.0F;
      this.m01 = 0.0F;
      this.m02 = 0.0F;
      this.m10 = 0.0F;
      this.m11 = 1.0F;
      this.m12 = 0.0F;
      this.m20 = 0.0F;
      this.m21 = 0.0F;
      this.m22 = 1.0F;
   }

   public float adjugateAndDet() {
      float var1 = this.m11 * this.m22 - this.m12 * this.m21;
      float var2 = -(this.m10 * this.m22 - this.m12 * this.m20);
      float var3 = this.m10 * this.m21 - this.m11 * this.m20;
      float var4 = -(this.m01 * this.m22 - this.m02 * this.m21);
      float var5 = this.m00 * this.m22 - this.m02 * this.m20;
      float var6 = -(this.m00 * this.m21 - this.m01 * this.m20);
      float var7 = this.m01 * this.m12 - this.m02 * this.m11;
      float var8 = -(this.m00 * this.m12 - this.m02 * this.m10);
      float var9 = this.m00 * this.m11 - this.m01 * this.m10;
      float var10 = this.m00 * var1 + this.m01 * var2 + this.m02 * var3;
      this.m00 = var1;
      this.m10 = var2;
      this.m20 = var3;
      this.m01 = var4;
      this.m11 = var5;
      this.m21 = var6;
      this.m02 = var7;
      this.m12 = var8;
      this.m22 = var9;
      return var10;
   }

   public boolean invert() {
      float var1 = this.adjugateAndDet();
      if (Math.abs(var1) > 1.0E-6F) {
         this.mul(var1);
         return true;
      } else {
         return false;
      }
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

   public void mul(Quaternion var1) {
      this.mul(new Matrix3f(var1));
   }

   public void mul(float var1) {
      this.m00 *= var1;
      this.m01 *= var1;
      this.m02 *= var1;
      this.m10 *= var1;
      this.m11 *= var1;
      this.m12 *= var1;
      this.m20 *= var1;
      this.m21 *= var1;
      this.m22 *= var1;
   }

   public Matrix3f copy() {
      return new Matrix3f(this);
   }
}
