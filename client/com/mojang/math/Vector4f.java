package com.mojang.math;

import java.util.Arrays;

public class Vector4f {
   private final float[] values;

   public Vector4f() {
      super();
      this.values = new float[4];
   }

   public Vector4f(float var1, float var2, float var3, float var4) {
      super();
      this.values = new float[]{var1, var2, var3, var4};
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector4f var2 = (Vector4f)var1;
         return Arrays.equals(this.values, var2.values);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.values);
   }

   public float x() {
      return this.values[0];
   }

   public float y() {
      return this.values[1];
   }

   public float z() {
      return this.values[2];
   }

   public float w() {
      return this.values[3];
   }

   public void mul(Vector3f var1) {
      float[] var10000 = this.values;
      var10000[0] *= var1.x();
      var10000 = this.values;
      var10000[1] *= var1.y();
      var10000 = this.values;
      var10000[2] *= var1.z();
   }

   public void set(float var1, float var2, float var3, float var4) {
      this.values[0] = var1;
      this.values[1] = var2;
      this.values[2] = var3;
      this.values[3] = var4;
   }

   public void transform(Quaternion var1) {
      Quaternion var2 = new Quaternion(var1);
      var2.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion var3 = new Quaternion(var1);
      var3.conj();
      var2.mul(var3);
      this.set(var2.i(), var2.j(), var2.k(), this.w());
   }
}
