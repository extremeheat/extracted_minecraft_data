package com.mojang.math;

import java.util.Arrays;

public final class Quaternion {
   private final float[] values;

   public Quaternion() {
      super();
      this.values = new float[4];
      this.values[4] = 1.0F;
   }

   public Quaternion(float var1, float var2, float var3, float var4) {
      super();
      this.values = new float[4];
      this.values[0] = var1;
      this.values[1] = var2;
      this.values[2] = var3;
      this.values[3] = var4;
   }

   public Quaternion(Vector3f var1, float var2, boolean var3) {
      super();
      if (var3) {
         var2 *= 0.017453292F;
      }

      float var4 = sin(var2 / 2.0F);
      this.values = new float[4];
      this.values[0] = var1.x() * var4;
      this.values[1] = var1.y() * var4;
      this.values[2] = var1.z() * var4;
      this.values[3] = cos(var2 / 2.0F);
   }

   public Quaternion(float var1, float var2, float var3, boolean var4) {
      super();
      if (var4) {
         var1 *= 0.017453292F;
         var2 *= 0.017453292F;
         var3 *= 0.017453292F;
      }

      float var5 = sin(0.5F * var1);
      float var6 = cos(0.5F * var1);
      float var7 = sin(0.5F * var2);
      float var8 = cos(0.5F * var2);
      float var9 = sin(0.5F * var3);
      float var10 = cos(0.5F * var3);
      this.values = new float[4];
      this.values[0] = var5 * var8 * var10 + var6 * var7 * var9;
      this.values[1] = var6 * var7 * var10 - var5 * var8 * var9;
      this.values[2] = var5 * var7 * var10 + var6 * var8 * var9;
      this.values[3] = var6 * var8 * var10 - var5 * var7 * var9;
   }

   public Quaternion(Quaternion var1) {
      super();
      this.values = Arrays.copyOf(var1.values, 4);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Quaternion var2 = (Quaternion)var1;
         return Arrays.equals(this.values, var2.values);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.values);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Quaternion[").append(this.r()).append(" + ");
      var1.append(this.i()).append("i + ");
      var1.append(this.j()).append("j + ");
      var1.append(this.k()).append("k]");
      return var1.toString();
   }

   public float i() {
      return this.values[0];
   }

   public float j() {
      return this.values[1];
   }

   public float k() {
      return this.values[2];
   }

   public float r() {
      return this.values[3];
   }

   public void mul(Quaternion var1) {
      float var2 = this.i();
      float var3 = this.j();
      float var4 = this.k();
      float var5 = this.r();
      float var6 = var1.i();
      float var7 = var1.j();
      float var8 = var1.k();
      float var9 = var1.r();
      this.values[0] = var5 * var6 + var2 * var9 + var3 * var8 - var4 * var7;
      this.values[1] = var5 * var7 - var2 * var8 + var3 * var9 + var4 * var6;
      this.values[2] = var5 * var8 + var2 * var7 - var3 * var6 + var4 * var9;
      this.values[3] = var5 * var9 - var2 * var6 - var3 * var7 - var4 * var8;
   }

   public void conj() {
      this.values[0] = -this.values[0];
      this.values[1] = -this.values[1];
      this.values[2] = -this.values[2];
   }

   private static float cos(float var0) {
      return (float)Math.cos((double)var0);
   }

   private static float sin(float var0) {
      return (float)Math.sin((double)var0);
   }
}
