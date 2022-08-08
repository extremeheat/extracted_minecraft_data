package com.mojang.math;

import net.minecraft.util.Mth;

public class Vector4f {
   private float x;
   private float y;
   private float z;
   private float w;

   public Vector4f() {
      super();
   }

   public Vector4f(float var1, float var2, float var3, float var4) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = var4;
   }

   public Vector4f(Vector3f var1) {
      this(var1.x(), var1.y(), var1.z(), 1.0F);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector4f var2 = (Vector4f)var1;
         if (Float.compare(var2.x, this.x) != 0) {
            return false;
         } else if (Float.compare(var2.y, this.y) != 0) {
            return false;
         } else if (Float.compare(var2.z, this.z) != 0) {
            return false;
         } else {
            return Float.compare(var2.w, this.w) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = Float.floatToIntBits(this.x);
      var1 = 31 * var1 + Float.floatToIntBits(this.y);
      var1 = 31 * var1 + Float.floatToIntBits(this.z);
      var1 = 31 * var1 + Float.floatToIntBits(this.w);
      return var1;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public float z() {
      return this.z;
   }

   public float w() {
      return this.w;
   }

   public void mul(float var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
      this.w *= var1;
   }

   public void mul(Vector3f var1) {
      this.x *= var1.x();
      this.y *= var1.y();
      this.z *= var1.z();
   }

   public void set(float var1, float var2, float var3, float var4) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
      this.w = var4;
   }

   public void add(float var1, float var2, float var3, float var4) {
      this.x += var1;
      this.y += var2;
      this.z += var3;
      this.w += var4;
   }

   public float dot(Vector4f var1) {
      return this.x * var1.x + this.y * var1.y + this.z * var1.z + this.w * var1.w;
   }

   public boolean normalize() {
      float var1 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
      if ((double)var1 < 1.0E-5) {
         return false;
      } else {
         float var2 = Mth.fastInvSqrt(var1);
         this.x *= var2;
         this.y *= var2;
         this.z *= var2;
         this.w *= var2;
         return true;
      }
   }

   public void transform(Matrix4f var1) {
      float var2 = this.x;
      float var3 = this.y;
      float var4 = this.z;
      float var5 = this.w;
      this.x = var1.m00 * var2 + var1.m01 * var3 + var1.m02 * var4 + var1.m03 * var5;
      this.y = var1.m10 * var2 + var1.m11 * var3 + var1.m12 * var4 + var1.m13 * var5;
      this.z = var1.m20 * var2 + var1.m21 * var3 + var1.m22 * var4 + var1.m23 * var5;
      this.w = var1.m30 * var2 + var1.m31 * var3 + var1.m32 * var4 + var1.m33 * var5;
   }

   public void transform(Quaternion var1) {
      Quaternion var2 = new Quaternion(var1);
      var2.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion var3 = new Quaternion(var1);
      var3.conj();
      var2.mul(var3);
      this.set(var2.i(), var2.j(), var2.k(), this.w());
   }

   public void perspectiveDivide() {
      this.x /= this.w;
      this.y /= this.w;
      this.z /= this.w;
      this.w = 1.0F;
   }

   public void lerp(Vector4f var1, float var2) {
      float var3 = 1.0F - var2;
      this.x = this.x * var3 + var1.x * var2;
      this.y = this.y * var3 + var1.y * var2;
      this.z = this.z * var3 + var1.z * var2;
      this.w = this.w * var3 + var1.w * var2;
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
   }
}
