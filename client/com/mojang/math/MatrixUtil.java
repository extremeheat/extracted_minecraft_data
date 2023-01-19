package com.mojang.math;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtil {
   private static final float G = 3.0F + 2.0F * (float)Math.sqrt(2.0);
   private static final float CS = (float)Math.cos(0.39269908169872414);
   private static final float SS = (float)Math.sin(0.39269908169872414);

   private MatrixUtil() {
      super();
   }

   public static Matrix4f mulComponentWise(Matrix4f var0, float var1) {
      return var0.set(
         var0.m00() * var1,
         var0.m01() * var1,
         var0.m02() * var1,
         var0.m03() * var1,
         var0.m10() * var1,
         var0.m11() * var1,
         var0.m12() * var1,
         var0.m13() * var1,
         var0.m20() * var1,
         var0.m21() * var1,
         var0.m22() * var1,
         var0.m23() * var1,
         var0.m30() * var1,
         var0.m31() * var1,
         var0.m32() * var1,
         var0.m33() * var1
      );
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
      if (var0 < 0.0F) {
         float var5 = var3;
         var3 = var4;
         var4 = var5;
      }

      float var8 = Mth.fastInvSqrt(var4 * var4 + var3 * var3);
      var4 *= var8;
      var3 *= var8;
      return Pair.of(var3, var4);
   }

   private static Quaternionf stepJacobi(Matrix3f var0) {
      Matrix3f var1 = new Matrix3f();
      Quaternionf var2 = new Quaternionf();
      if (var0.m01 * var0.m01 + var0.m10 * var0.m10 > 1.0E-6F) {
         Pair var3 = approxGivensQuat(var0.m00, 0.5F * (var0.m01 + var0.m10), var0.m11);
         Float var4 = (Float)var3.getFirst();
         Float var5 = (Float)var3.getSecond();
         Quaternionf var6 = new Quaternionf(0.0F, 0.0F, var4, var5);
         float var7 = var5 * var5 - var4 * var4;
         float var8 = -2.0F * var4 * var5;
         float var9 = var5 * var5 + var4 * var4;
         var2.mul(var6);
         var1.m00 = var7;
         var1.m11 = var7;
         var1.m01 = -var8;
         var1.m10 = var8;
         var1.m22 = var9;
         var0.mul(var1);
         var1.transpose();
         var1.mul(var0);
         var0.set(var1);
      }

      if (var0.m02 * var0.m02 + var0.m20 * var0.m20 > 1.0E-6F) {
         Pair var10 = approxGivensQuat(var0.m00, 0.5F * (var0.m02 + var0.m20), var0.m22);
         float var12 = -var10.getFirst();
         Float var14 = (Float)var10.getSecond();
         Quaternionf var16 = new Quaternionf(0.0F, var12, 0.0F, var14);
         float var18 = var14 * var14 - var12 * var12;
         float var20 = -2.0F * var12 * var14;
         float var22 = var14 * var14 + var12 * var12;
         var2.mul(var16);
         var1.m00 = var18;
         var1.m22 = var18;
         var1.m02 = var20;
         var1.m20 = -var20;
         var1.m11 = var22;
         var0.mul(var1);
         var1.transpose();
         var1.mul(var0);
         var0.set(var1);
      }

      if (var0.m12 * var0.m12 + var0.m21 * var0.m21 > 1.0E-6F) {
         Pair var11 = approxGivensQuat(var0.m11, 0.5F * (var0.m12 + var0.m21), var0.m22);
         Float var13 = (Float)var11.getFirst();
         Float var15 = (Float)var11.getSecond();
         Quaternionf var17 = new Quaternionf(var13, 0.0F, 0.0F, var15);
         float var19 = var15 * var15 - var13 * var13;
         float var21 = -2.0F * var13 * var15;
         float var23 = var15 * var15 + var13 * var13;
         var2.mul(var17);
         var1.m11 = var19;
         var1.m22 = var19;
         var1.m12 = -var21;
         var1.m21 = var21;
         var1.m00 = var23;
         var0.mul(var1);
         var1.transpose();
         var1.mul(var0);
         var0.set(var1);
      }

      return var2;
   }

   public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f var0) {
      Quaternionf var1 = new Quaternionf();
      Quaternionf var2 = new Quaternionf();
      Matrix3f var3 = new Matrix3f(var0);
      var3.transpose();
      var3.mul(var0);

      for(int var4 = 0; var4 < 5; ++var4) {
         var2.mul(stepJacobi(var3));
      }

      var2.normalize();
      Matrix3f var29 = new Matrix3f(var0);
      var29.rotate(var2);
      float var6 = 1.0F;
      Pair var5 = qrGivensQuat(var29.m00, var29.m01);
      Float var7 = (Float)var5.getFirst();
      Float var8 = (Float)var5.getSecond();
      float var9 = var8 * var8 - var7 * var7;
      float var10 = -2.0F * var7 * var8;
      float var11 = var8 * var8 + var7 * var7;
      Quaternionf var12 = new Quaternionf(0.0F, 0.0F, var7, var8);
      var1.mul(var12);
      Matrix3f var13 = new Matrix3f();
      var13.m00 = var9;
      var13.m11 = var9;
      var13.m01 = var10;
      var13.m10 = -var10;
      var13.m22 = var11;
      var6 *= var11;
      var13.mul(var29);
      var5 = qrGivensQuat(var13.m00, var13.m02);
      float var14 = -var5.getFirst();
      Float var15 = (Float)var5.getSecond();
      float var16 = var15 * var15 - var14 * var14;
      float var17 = -2.0F * var14 * var15;
      float var18 = var15 * var15 + var14 * var14;
      Quaternionf var19 = new Quaternionf(0.0F, var14, 0.0F, var15);
      var1.mul(var19);
      Matrix3f var20 = new Matrix3f();
      var20.m00 = var16;
      var20.m22 = var16;
      var20.m02 = -var17;
      var20.m20 = var17;
      var20.m11 = var18;
      var6 *= var18;
      var20.mul(var13);
      var5 = qrGivensQuat(var20.m11, var20.m12);
      Float var21 = (Float)var5.getFirst();
      Float var22 = (Float)var5.getSecond();
      float var23 = var22 * var22 - var21 * var21;
      float var24 = -2.0F * var21 * var22;
      float var25 = var22 * var22 + var21 * var21;
      Quaternionf var26 = new Quaternionf(var21, 0.0F, 0.0F, var22);
      var1.mul(var26);
      Matrix3f var27 = new Matrix3f();
      var27.m11 = var23;
      var27.m22 = var23;
      var27.m12 = var24;
      var27.m21 = -var24;
      var27.m00 = var25;
      var6 *= var25;
      var27.mul(var20);
      var6 = 1.0F / var6;
      var1.mul((float)Math.sqrt((double)var6));
      Vector3f var28 = new Vector3f(var27.m00 * var6, var27.m11 * var6, var27.m22 * var6);
      return Triple.of(var1, var28, var2);
   }

   public static Matrix4x3f toAffine(Matrix4f var0) {
      float var1 = 1.0F / var0.m33();
      return new Matrix4x3f().set(var0).scaleLocal(var1, var1, var1);
   }
}
