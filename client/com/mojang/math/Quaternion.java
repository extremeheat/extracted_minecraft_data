package com.mojang.math;

import net.minecraft.util.Mth;

public final class Quaternion {
   public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   private float i;
   private float j;
   private float k;
   private float r;

   public Quaternion(float var1, float var2, float var3, float var4) {
      super();
      this.i = var1;
      this.j = var2;
      this.k = var3;
      this.r = var4;
   }

   public Quaternion(Vector3f var1, float var2, boolean var3) {
      super();
      if (var3) {
         var2 *= 0.017453292F;
      }

      float var4 = sin(var2 / 2.0F);
      this.i = var1.x() * var4;
      this.j = var1.y() * var4;
      this.k = var1.z() * var4;
      this.r = cos(var2 / 2.0F);
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
      this.i = var5 * var8 * var10 + var6 * var7 * var9;
      this.j = var6 * var7 * var10 - var5 * var8 * var9;
      this.k = var5 * var7 * var10 + var6 * var8 * var9;
      this.r = var6 * var8 * var10 - var5 * var7 * var9;
   }

   public Quaternion(Quaternion var1) {
      super();
      this.i = var1.i;
      this.j = var1.j;
      this.k = var1.k;
      this.r = var1.r;
   }

   public static Quaternion fromYXZ(float var0, float var1, float var2) {
      Quaternion var3 = ONE.copy();
      var3.mul(new Quaternion(0.0F, (float)Math.sin((double)(var0 / 2.0F)), 0.0F, (float)Math.cos((double)(var0 / 2.0F))));
      var3.mul(new Quaternion((float)Math.sin((double)(var1 / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(var1 / 2.0F))));
      var3.mul(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(var2 / 2.0F)), (float)Math.cos((double)(var2 / 2.0F))));
      return var3;
   }

   public static Quaternion fromXYZDegrees(Vector3f var0) {
      return fromXYZ((float)Math.toRadians((double)var0.x()), (float)Math.toRadians((double)var0.y()), (float)Math.toRadians((double)var0.z()));
   }

   public static Quaternion fromXYZ(Vector3f var0) {
      return fromXYZ(var0.x(), var0.y(), var0.z());
   }

   public static Quaternion fromXYZ(float var0, float var1, float var2) {
      Quaternion var3 = ONE.copy();
      var3.mul(new Quaternion((float)Math.sin((double)(var0 / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(var0 / 2.0F))));
      var3.mul(new Quaternion(0.0F, (float)Math.sin((double)(var1 / 2.0F)), 0.0F, (float)Math.cos((double)(var1 / 2.0F))));
      var3.mul(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(var2 / 2.0F)), (float)Math.cos((double)(var2 / 2.0F))));
      return var3;
   }

   public Vector3f toXYZ() {
      float var1 = this.r() * this.r();
      float var2 = this.i() * this.i();
      float var3 = this.j() * this.j();
      float var4 = this.k() * this.k();
      float var5 = var1 + var2 + var3 + var4;
      float var6 = 2.0F * this.r() * this.i() - 2.0F * this.j() * this.k();
      float var7 = (float)Math.asin((double)(var6 / var5));
      return Math.abs(var6) > 0.999F * var5 ? new Vector3f(2.0F * (float)Math.atan2((double)this.i(), (double)this.r()), var7, 0.0F) : new Vector3f((float)Math.atan2((double)(2.0F * this.j() * this.k() + 2.0F * this.i() * this.r()), (double)(var1 - var2 - var3 + var4)), var7, (float)Math.atan2((double)(2.0F * this.i() * this.j() + 2.0F * this.r() * this.k()), (double)(var1 + var2 - var3 - var4)));
   }

   public Vector3f toXYZDegrees() {
      Vector3f var1 = this.toXYZ();
      return new Vector3f((float)Math.toDegrees((double)var1.x()), (float)Math.toDegrees((double)var1.y()), (float)Math.toDegrees((double)var1.z()));
   }

   public Vector3f toYXZ() {
      float var1 = this.r() * this.r();
      float var2 = this.i() * this.i();
      float var3 = this.j() * this.j();
      float var4 = this.k() * this.k();
      float var5 = var1 + var2 + var3 + var4;
      float var6 = 2.0F * this.r() * this.i() - 2.0F * this.j() * this.k();
      float var7 = (float)Math.asin((double)(var6 / var5));
      return Math.abs(var6) > 0.999F * var5 ? new Vector3f(var7, 2.0F * (float)Math.atan2((double)this.j(), (double)this.r()), 0.0F) : new Vector3f(var7, (float)Math.atan2((double)(2.0F * this.i() * this.k() + 2.0F * this.j() * this.r()), (double)(var1 - var2 - var3 + var4)), (float)Math.atan2((double)(2.0F * this.i() * this.j() + 2.0F * this.r() * this.k()), (double)(var1 - var2 + var3 - var4)));
   }

   public Vector3f toYXZDegrees() {
      Vector3f var1 = this.toYXZ();
      return new Vector3f((float)Math.toDegrees((double)var1.x()), (float)Math.toDegrees((double)var1.y()), (float)Math.toDegrees((double)var1.z()));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Quaternion var2 = (Quaternion)var1;
         if (Float.compare(var2.i, this.i) != 0) {
            return false;
         } else if (Float.compare(var2.j, this.j) != 0) {
            return false;
         } else if (Float.compare(var2.k, this.k) != 0) {
            return false;
         } else {
            return Float.compare(var2.r, this.r) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = Float.floatToIntBits(this.i);
      var1 = 31 * var1 + Float.floatToIntBits(this.j);
      var1 = 31 * var1 + Float.floatToIntBits(this.k);
      var1 = 31 * var1 + Float.floatToIntBits(this.r);
      return var1;
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
      return this.i;
   }

   public float j() {
      return this.j;
   }

   public float k() {
      return this.k;
   }

   public float r() {
      return this.r;
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
      this.i = var5 * var6 + var2 * var9 + var3 * var8 - var4 * var7;
      this.j = var5 * var7 - var2 * var8 + var3 * var9 + var4 * var6;
      this.k = var5 * var8 + var2 * var7 - var3 * var6 + var4 * var9;
      this.r = var5 * var9 - var2 * var6 - var3 * var7 - var4 * var8;
   }

   public void mul(float var1) {
      this.i *= var1;
      this.j *= var1;
      this.k *= var1;
      this.r *= var1;
   }

   public void conj() {
      this.i = -this.i;
      this.j = -this.j;
      this.k = -this.k;
   }

   public void set(float var1, float var2, float var3, float var4) {
      this.i = var1;
      this.j = var2;
      this.k = var3;
      this.r = var4;
   }

   private static float cos(float var0) {
      return (float)Math.cos((double)var0);
   }

   private static float sin(float var0) {
      return (float)Math.sin((double)var0);
   }

   public void normalize() {
      float var1 = this.i() * this.i() + this.j() * this.j() + this.k() * this.k() + this.r() * this.r();
      if (var1 > 1.0E-6F) {
         float var2 = Mth.fastInvSqrt(var1);
         this.i *= var2;
         this.j *= var2;
         this.k *= var2;
         this.r *= var2;
      } else {
         this.i = 0.0F;
         this.j = 0.0F;
         this.k = 0.0F;
         this.r = 0.0F;
      }

   }

   public void slerp(Quaternion var1, float var2) {
      throw new UnsupportedOperationException();
   }

   public Quaternion copy() {
      return new Quaternion(this);
   }
}
