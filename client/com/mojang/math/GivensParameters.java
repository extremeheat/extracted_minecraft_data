package com.mojang.math;

import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

public record GivensParameters(float sinHalf, float cosHalf) {
   public GivensParameters(float var1, float var2) {
      super();
      this.sinHalf = var1;
      this.cosHalf = var2;
   }

   public static GivensParameters fromUnnormalized(float var0, float var1) {
      float var2 = Math.invsqrt(var0 * var0 + var1 * var1);
      return new GivensParameters(var2 * var0, var2 * var1);
   }

   public static GivensParameters fromPositiveAngle(float var0) {
      float var1 = Math.sin(var0 / 2.0F);
      float var2 = Math.cosFromSin(var1, var0 / 2.0F);
      return new GivensParameters(var1, var2);
   }

   public GivensParameters inverse() {
      return new GivensParameters(-this.sinHalf, this.cosHalf);
   }

   public Quaternionf aroundX(Quaternionf var1) {
      return var1.set(this.sinHalf, 0.0F, 0.0F, this.cosHalf);
   }

   public Quaternionf aroundY(Quaternionf var1) {
      return var1.set(0.0F, this.sinHalf, 0.0F, this.cosHalf);
   }

   public Quaternionf aroundZ(Quaternionf var1) {
      return var1.set(0.0F, 0.0F, this.sinHalf, this.cosHalf);
   }

   public float cos() {
      return this.cosHalf * this.cosHalf - this.sinHalf * this.sinHalf;
   }

   public float sin() {
      return 2.0F * this.sinHalf * this.cosHalf;
   }

   public Matrix3f aroundX(Matrix3f var1) {
      var1.m01 = 0.0F;
      var1.m02 = 0.0F;
      var1.m10 = 0.0F;
      var1.m20 = 0.0F;
      float var2 = this.cos();
      float var3 = this.sin();
      var1.m11 = var2;
      var1.m22 = var2;
      var1.m12 = var3;
      var1.m21 = -var3;
      var1.m00 = 1.0F;
      return var1;
   }

   public Matrix3f aroundY(Matrix3f var1) {
      var1.m01 = 0.0F;
      var1.m10 = 0.0F;
      var1.m12 = 0.0F;
      var1.m21 = 0.0F;
      float var2 = this.cos();
      float var3 = this.sin();
      var1.m00 = var2;
      var1.m22 = var2;
      var1.m02 = -var3;
      var1.m20 = var3;
      var1.m11 = 1.0F;
      return var1;
   }

   public Matrix3f aroundZ(Matrix3f var1) {
      var1.m02 = 0.0F;
      var1.m12 = 0.0F;
      var1.m20 = 0.0F;
      var1.m21 = 0.0F;
      float var2 = this.cos();
      float var3 = this.sin();
      var1.m00 = var2;
      var1.m11 = var2;
      var1.m01 = var3;
      var1.m10 = -var3;
      var1.m22 = 1.0F;
      return var1;
   }

   public float sinHalf() {
      return this.sinHalf;
   }

   public float cosHalf() {
      return this.cosHalf;
   }
}
