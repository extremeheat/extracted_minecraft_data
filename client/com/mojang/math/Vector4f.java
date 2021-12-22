package com.mojang.math;

import net.minecraft.util.Mth;

public class Vector4f {
   // $FF: renamed from: x float
   private float field_279;
   // $FF: renamed from: y float
   private float field_280;
   // $FF: renamed from: z float
   private float field_281;
   // $FF: renamed from: w float
   private float field_282;

   public Vector4f() {
      super();
   }

   public Vector4f(float var1, float var2, float var3, float var4) {
      super();
      this.field_279 = var1;
      this.field_280 = var2;
      this.field_281 = var3;
      this.field_282 = var4;
   }

   public Vector4f(Vector3f var1) {
      this(var1.method_82(), var1.method_83(), var1.method_84(), 1.0F);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector4f var2 = (Vector4f)var1;
         if (Float.compare(var2.field_279, this.field_279) != 0) {
            return false;
         } else if (Float.compare(var2.field_280, this.field_280) != 0) {
            return false;
         } else if (Float.compare(var2.field_281, this.field_281) != 0) {
            return false;
         } else {
            return Float.compare(var2.field_282, this.field_282) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = Float.floatToIntBits(this.field_279);
      var1 = 31 * var1 + Float.floatToIntBits(this.field_280);
      var1 = 31 * var1 + Float.floatToIntBits(this.field_281);
      var1 = 31 * var1 + Float.floatToIntBits(this.field_282);
      return var1;
   }

   // $FF: renamed from: x () float
   public float method_66() {
      return this.field_279;
   }

   // $FF: renamed from: y () float
   public float method_67() {
      return this.field_280;
   }

   // $FF: renamed from: z () float
   public float method_68() {
      return this.field_281;
   }

   // $FF: renamed from: w () float
   public float method_69() {
      return this.field_282;
   }

   public void mul(float var1) {
      this.field_279 *= var1;
      this.field_280 *= var1;
      this.field_281 *= var1;
      this.field_282 *= var1;
   }

   public void mul(Vector3f var1) {
      this.field_279 *= var1.method_82();
      this.field_280 *= var1.method_83();
      this.field_281 *= var1.method_84();
   }

   public void set(float var1, float var2, float var3, float var4) {
      this.field_279 = var1;
      this.field_280 = var2;
      this.field_281 = var3;
      this.field_282 = var4;
   }

   public void add(float var1, float var2, float var3, float var4) {
      this.field_279 += var1;
      this.field_280 += var2;
      this.field_281 += var3;
      this.field_282 += var4;
   }

   public float dot(Vector4f var1) {
      return this.field_279 * var1.field_279 + this.field_280 * var1.field_280 + this.field_281 * var1.field_281 + this.field_282 * var1.field_282;
   }

   public boolean normalize() {
      float var1 = this.field_279 * this.field_279 + this.field_280 * this.field_280 + this.field_281 * this.field_281 + this.field_282 * this.field_282;
      if ((double)var1 < 1.0E-5D) {
         return false;
      } else {
         float var2 = Mth.fastInvSqrt(var1);
         this.field_279 *= var2;
         this.field_280 *= var2;
         this.field_281 *= var2;
         this.field_282 *= var2;
         return true;
      }
   }

   public void transform(Matrix4f var1) {
      float var2 = this.field_279;
      float var3 = this.field_280;
      float var4 = this.field_281;
      float var5 = this.field_282;
      this.field_279 = var1.m00 * var2 + var1.m01 * var3 + var1.m02 * var4 + var1.m03 * var5;
      this.field_280 = var1.m10 * var2 + var1.m11 * var3 + var1.m12 * var4 + var1.m13 * var5;
      this.field_281 = var1.m20 * var2 + var1.m21 * var3 + var1.m22 * var4 + var1.m23 * var5;
      this.field_282 = var1.m30 * var2 + var1.m31 * var3 + var1.m32 * var4 + var1.m33 * var5;
   }

   public void transform(Quaternion var1) {
      Quaternion var2 = new Quaternion(var1);
      var2.mul(new Quaternion(this.method_66(), this.method_67(), this.method_68(), 0.0F));
      Quaternion var3 = new Quaternion(var1);
      var3.conj();
      var2.mul(var3);
      this.set(var2.method_129(), var2.method_130(), var2.method_131(), this.method_69());
   }

   public void perspectiveDivide() {
      this.field_279 /= this.field_282;
      this.field_280 /= this.field_282;
      this.field_281 /= this.field_282;
      this.field_282 = 1.0F;
   }

   public void lerp(Vector4f var1, float var2) {
      float var3 = 1.0F - var2;
      this.field_279 = this.field_279 * var3 + var1.field_279 * var2;
      this.field_280 = this.field_280 * var3 + var1.field_280 * var2;
      this.field_281 = this.field_281 * var3 + var1.field_281 * var2;
      this.field_282 = this.field_282 * var3 + var1.field_282 * var2;
   }

   public String toString() {
      return "[" + this.field_279 + ", " + this.field_280 + ", " + this.field_281 + ", " + this.field_282 + "]";
   }
}
