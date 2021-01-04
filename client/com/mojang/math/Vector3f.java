package com.mojang.math;

import java.util.Arrays;
import net.minecraft.world.phys.Vec3;

public final class Vector3f {
   private final float[] values;

   public Vector3f(Vector3f var1) {
      super();
      this.values = Arrays.copyOf(var1.values, 3);
   }

   public Vector3f() {
      super();
      this.values = new float[3];
   }

   public Vector3f(float var1, float var2, float var3) {
      super();
      this.values = new float[]{var1, var2, var3};
   }

   public Vector3f(Vec3 var1) {
      super();
      this.values = new float[]{(float)var1.x, (float)var1.y, (float)var1.z};
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector3f var2 = (Vector3f)var1;
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

   public void mul(float var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         float[] var10000 = this.values;
         var10000[var2] *= var1;
      }

   }

   private static float clamp(float var0, float var1, float var2) {
      if (var0 < var1) {
         return var1;
      } else {
         return var0 > var2 ? var2 : var0;
      }
   }

   public void clamp(float var1, float var2) {
      this.values[0] = clamp(this.values[0], var1, var2);
      this.values[1] = clamp(this.values[1], var1, var2);
      this.values[2] = clamp(this.values[2], var1, var2);
   }

   public void set(float var1, float var2, float var3) {
      this.values[0] = var1;
      this.values[1] = var2;
      this.values[2] = var3;
   }

   public void add(float var1, float var2, float var3) {
      float[] var10000 = this.values;
      var10000[0] += var1;
      var10000 = this.values;
      var10000[1] += var2;
      var10000 = this.values;
      var10000[2] += var3;
   }

   public void sub(Vector3f var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         float[] var10000 = this.values;
         var10000[var2] -= var1.values[var2];
      }

   }

   public float dot(Vector3f var1) {
      float var2 = 0.0F;

      for(int var3 = 0; var3 < 3; ++var3) {
         var2 += this.values[var3] * var1.values[var3];
      }

      return var2;
   }

   public void normalize() {
      float var1 = 0.0F;

      int var2;
      for(var2 = 0; var2 < 3; ++var2) {
         var1 += this.values[var2] * this.values[var2];
      }

      for(var2 = 0; var2 < 3; ++var2) {
         float[] var10000 = this.values;
         var10000[var2] /= var1;
      }

   }

   public void cross(Vector3f var1) {
      float var2 = this.values[0];
      float var3 = this.values[1];
      float var4 = this.values[2];
      float var5 = var1.x();
      float var6 = var1.y();
      float var7 = var1.z();
      this.values[0] = var3 * var7 - var4 * var6;
      this.values[1] = var4 * var5 - var2 * var7;
      this.values[2] = var2 * var6 - var3 * var5;
   }

   public void transform(Quaternion var1) {
      Quaternion var2 = new Quaternion(var1);
      var2.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion var3 = new Quaternion(var1);
      var3.conj();
      var2.mul(var3);
      this.set(var2.i(), var2.j(), var2.k());
   }
}
