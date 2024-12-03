package com.mojang.math;

import org.apache.commons.lang3.tuple.Triple;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtil {
   private static final float G = 3.0F + 2.0F * Math.sqrt(2.0F);
   private static final GivensParameters PI_4 = GivensParameters.fromPositiveAngle(0.7853982F);

   private MatrixUtil() {
      super();
   }

   public static Matrix4f mulComponentWise(Matrix4f var0, float var1) {
      return var0.set(var0.m00() * var1, var0.m01() * var1, var0.m02() * var1, var0.m03() * var1, var0.m10() * var1, var0.m11() * var1, var0.m12() * var1, var0.m13() * var1, var0.m20() * var1, var0.m21() * var1, var0.m22() * var1, var0.m23() * var1, var0.m30() * var1, var0.m31() * var1, var0.m32() * var1, var0.m33() * var1);
   }

   private static GivensParameters approxGivensQuat(float var0, float var1, float var2) {
      float var3 = 2.0F * (var0 - var2);
      return G * var1 * var1 < var3 * var3 ? GivensParameters.fromUnnormalized(var1, var3) : PI_4;
   }

   private static GivensParameters qrGivensQuat(float var0, float var1) {
      float var2 = (float)java.lang.Math.hypot((double)var0, (double)var1);
      float var3 = var2 > 1.0E-6F ? var1 : 0.0F;
      float var4 = Math.abs(var0) + Math.max(var2, 1.0E-6F);
      if (var0 < 0.0F) {
         float var5 = var3;
         var3 = var4;
         var4 = var5;
      }

      return GivensParameters.fromUnnormalized(var3, var4);
   }

   private static void similarityTransform(Matrix3f var0, Matrix3f var1) {
      var0.mul(var1);
      var1.transpose();
      var1.mul(var0);
      var0.set(var1);
   }

   private static void stepJacobi(Matrix3f var0, Matrix3f var1, Quaternionf var2, Quaternionf var3) {
      if (var0.m01 * var0.m01 + var0.m10 * var0.m10 > 1.0E-6F) {
         GivensParameters var4 = approxGivensQuat(var0.m00, 0.5F * (var0.m01 + var0.m10), var0.m11);
         Quaternionf var5 = var4.aroundZ(var2);
         var3.mul(var5);
         var4.aroundZ(var1);
         similarityTransform(var0, var1);
      }

      if (var0.m02 * var0.m02 + var0.m20 * var0.m20 > 1.0E-6F) {
         GivensParameters var6 = approxGivensQuat(var0.m00, 0.5F * (var0.m02 + var0.m20), var0.m22).inverse();
         Quaternionf var8 = var6.aroundY(var2);
         var3.mul(var8);
         var6.aroundY(var1);
         similarityTransform(var0, var1);
      }

      if (var0.m12 * var0.m12 + var0.m21 * var0.m21 > 1.0E-6F) {
         GivensParameters var7 = approxGivensQuat(var0.m11, 0.5F * (var0.m12 + var0.m21), var0.m22);
         Quaternionf var9 = var7.aroundX(var2);
         var3.mul(var9);
         var7.aroundX(var1);
         similarityTransform(var0, var1);
      }

   }

   public static Quaternionf eigenvalueJacobi(Matrix3f var0, int var1) {
      Quaternionf var2 = new Quaternionf();
      Matrix3f var3 = new Matrix3f();
      Quaternionf var4 = new Quaternionf();

      for(int var5 = 0; var5 < var1; ++var5) {
         stepJacobi(var0, var3, var4, var2);
      }

      var2.normalize();
      return var2;
   }

   public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f var0) {
      Matrix3f var1 = new Matrix3f(var0);
      var1.transpose();
      var1.mul(var0);
      Quaternionf var2 = eigenvalueJacobi(var1, 5);
      float var3 = var1.m00;
      float var4 = var1.m11;
      boolean var5 = (double)var3 < 1.0E-6;
      boolean var6 = (double)var4 < 1.0E-6;
      Matrix3f var8 = var0.rotate(var2);
      Quaternionf var9 = new Quaternionf();
      Quaternionf var10 = new Quaternionf();
      GivensParameters var11;
      if (var5) {
         var11 = qrGivensQuat(var8.m11, -var8.m10);
      } else {
         var11 = qrGivensQuat(var8.m00, var8.m01);
      }

      Quaternionf var12 = var11.aroundZ(var10);
      Matrix3f var13 = var11.aroundZ(var1);
      var9.mul(var12);
      var13.transpose().mul(var8);
      if (var5) {
         var11 = qrGivensQuat(var13.m22, -var13.m20);
      } else {
         var11 = qrGivensQuat(var13.m00, var13.m02);
      }

      var11 = var11.inverse();
      Quaternionf var14 = var11.aroundY(var10);
      Matrix3f var15 = var11.aroundY(var8);
      var9.mul(var14);
      var15.transpose().mul(var13);
      if (var6) {
         var11 = qrGivensQuat(var15.m22, -var15.m21);
      } else {
         var11 = qrGivensQuat(var15.m11, var15.m12);
      }

      Quaternionf var16 = var11.aroundX(var10);
      Matrix3f var17 = var11.aroundX(var13);
      var9.mul(var16);
      var17.transpose().mul(var15);
      Vector3f var18 = new Vector3f(var17.m00, var17.m11, var17.m22);
      return Triple.of(var9, var18, var2.conjugate());
   }

   public static boolean isIdentity(Matrix4f var0) {
      return (var0.properties() & 4) != 0;
   }

   public static boolean isPureTranslation(Matrix4f var0) {
      return (var0.properties() & 8) != 0;
   }

   public static boolean isOrthonormal(Matrix4f var0) {
      return (var0.properties() & 16) != 0;
   }
}
