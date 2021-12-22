package com.mojang.math;

import net.minecraft.util.Mth;

public final class Quaternion {
   public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   // $FF: renamed from: i float
   private float field_517;
   // $FF: renamed from: j float
   private float field_518;
   // $FF: renamed from: k float
   private float field_519;
   // $FF: renamed from: r float
   private float field_520;

   public Quaternion(float var1, float var2, float var3, float var4) {
      super();
      this.field_517 = var1;
      this.field_518 = var2;
      this.field_519 = var3;
      this.field_520 = var4;
   }

   public Quaternion(Vector3f var1, float var2, boolean var3) {
      super();
      if (var3) {
         var2 *= 0.017453292F;
      }

      float var4 = sin(var2 / 2.0F);
      this.field_517 = var1.method_82() * var4;
      this.field_518 = var1.method_83() * var4;
      this.field_519 = var1.method_84() * var4;
      this.field_520 = cos(var2 / 2.0F);
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
      this.field_517 = var5 * var8 * var10 + var6 * var7 * var9;
      this.field_518 = var6 * var7 * var10 - var5 * var8 * var9;
      this.field_519 = var5 * var7 * var10 + var6 * var8 * var9;
      this.field_520 = var6 * var8 * var10 - var5 * var7 * var9;
   }

   public Quaternion(Quaternion var1) {
      super();
      this.field_517 = var1.field_517;
      this.field_518 = var1.field_518;
      this.field_519 = var1.field_519;
      this.field_520 = var1.field_520;
   }

   public static Quaternion fromYXZ(float var0, float var1, float var2) {
      Quaternion var3 = ONE.copy();
      var3.mul(new Quaternion(0.0F, (float)Math.sin((double)(var0 / 2.0F)), 0.0F, (float)Math.cos((double)(var0 / 2.0F))));
      var3.mul(new Quaternion((float)Math.sin((double)(var1 / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(var1 / 2.0F))));
      var3.mul(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(var2 / 2.0F)), (float)Math.cos((double)(var2 / 2.0F))));
      return var3;
   }

   public static Quaternion fromXYZDegrees(Vector3f var0) {
      return fromXYZ((float)Math.toRadians((double)var0.method_82()), (float)Math.toRadians((double)var0.method_83()), (float)Math.toRadians((double)var0.method_84()));
   }

   public static Quaternion fromXYZ(Vector3f var0) {
      return fromXYZ(var0.method_82(), var0.method_83(), var0.method_84());
   }

   public static Quaternion fromXYZ(float var0, float var1, float var2) {
      Quaternion var3 = ONE.copy();
      var3.mul(new Quaternion((float)Math.sin((double)(var0 / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(var0 / 2.0F))));
      var3.mul(new Quaternion(0.0F, (float)Math.sin((double)(var1 / 2.0F)), 0.0F, (float)Math.cos((double)(var1 / 2.0F))));
      var3.mul(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(var2 / 2.0F)), (float)Math.cos((double)(var2 / 2.0F))));
      return var3;
   }

   public Vector3f toXYZ() {
      float var1 = this.method_132() * this.method_132();
      float var2 = this.method_129() * this.method_129();
      float var3 = this.method_130() * this.method_130();
      float var4 = this.method_131() * this.method_131();
      float var5 = var1 + var2 + var3 + var4;
      float var6 = 2.0F * this.method_132() * this.method_129() - 2.0F * this.method_130() * this.method_131();
      float var7 = (float)Math.asin((double)(var6 / var5));
      return Math.abs(var6) > 0.999F * var5 ? new Vector3f(2.0F * (float)Math.atan2((double)this.method_129(), (double)this.method_132()), var7, 0.0F) : new Vector3f((float)Math.atan2((double)(2.0F * this.method_130() * this.method_131() + 2.0F * this.method_129() * this.method_132()), (double)(var1 - var2 - var3 + var4)), var7, (float)Math.atan2((double)(2.0F * this.method_129() * this.method_130() + 2.0F * this.method_132() * this.method_131()), (double)(var1 + var2 - var3 - var4)));
   }

   public Vector3f toXYZDegrees() {
      Vector3f var1 = this.toXYZ();
      return new Vector3f((float)Math.toDegrees((double)var1.method_82()), (float)Math.toDegrees((double)var1.method_83()), (float)Math.toDegrees((double)var1.method_84()));
   }

   public Vector3f toYXZ() {
      float var1 = this.method_132() * this.method_132();
      float var2 = this.method_129() * this.method_129();
      float var3 = this.method_130() * this.method_130();
      float var4 = this.method_131() * this.method_131();
      float var5 = var1 + var2 + var3 + var4;
      float var6 = 2.0F * this.method_132() * this.method_129() - 2.0F * this.method_130() * this.method_131();
      float var7 = (float)Math.asin((double)(var6 / var5));
      return Math.abs(var6) > 0.999F * var5 ? new Vector3f(var7, 2.0F * (float)Math.atan2((double)this.method_130(), (double)this.method_132()), 0.0F) : new Vector3f(var7, (float)Math.atan2((double)(2.0F * this.method_129() * this.method_131() + 2.0F * this.method_130() * this.method_132()), (double)(var1 - var2 - var3 + var4)), (float)Math.atan2((double)(2.0F * this.method_129() * this.method_130() + 2.0F * this.method_132() * this.method_131()), (double)(var1 - var2 + var3 - var4)));
   }

   public Vector3f toYXZDegrees() {
      Vector3f var1 = this.toYXZ();
      return new Vector3f((float)Math.toDegrees((double)var1.method_82()), (float)Math.toDegrees((double)var1.method_83()), (float)Math.toDegrees((double)var1.method_84()));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Quaternion var2 = (Quaternion)var1;
         if (Float.compare(var2.field_517, this.field_517) != 0) {
            return false;
         } else if (Float.compare(var2.field_518, this.field_518) != 0) {
            return false;
         } else if (Float.compare(var2.field_519, this.field_519) != 0) {
            return false;
         } else {
            return Float.compare(var2.field_520, this.field_520) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = Float.floatToIntBits(this.field_517);
      var1 = 31 * var1 + Float.floatToIntBits(this.field_518);
      var1 = 31 * var1 + Float.floatToIntBits(this.field_519);
      var1 = 31 * var1 + Float.floatToIntBits(this.field_520);
      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Quaternion[").append(this.method_132()).append(" + ");
      var1.append(this.method_129()).append("i + ");
      var1.append(this.method_130()).append("j + ");
      var1.append(this.method_131()).append("k]");
      return var1.toString();
   }

   // $FF: renamed from: i () float
   public float method_129() {
      return this.field_517;
   }

   // $FF: renamed from: j () float
   public float method_130() {
      return this.field_518;
   }

   // $FF: renamed from: k () float
   public float method_131() {
      return this.field_519;
   }

   // $FF: renamed from: r () float
   public float method_132() {
      return this.field_520;
   }

   public void mul(Quaternion var1) {
      float var2 = this.method_129();
      float var3 = this.method_130();
      float var4 = this.method_131();
      float var5 = this.method_132();
      float var6 = var1.method_129();
      float var7 = var1.method_130();
      float var8 = var1.method_131();
      float var9 = var1.method_132();
      this.field_517 = var5 * var6 + var2 * var9 + var3 * var8 - var4 * var7;
      this.field_518 = var5 * var7 - var2 * var8 + var3 * var9 + var4 * var6;
      this.field_519 = var5 * var8 + var2 * var7 - var3 * var6 + var4 * var9;
      this.field_520 = var5 * var9 - var2 * var6 - var3 * var7 - var4 * var8;
   }

   public void mul(float var1) {
      this.field_517 *= var1;
      this.field_518 *= var1;
      this.field_519 *= var1;
      this.field_520 *= var1;
   }

   public void conj() {
      this.field_517 = -this.field_517;
      this.field_518 = -this.field_518;
      this.field_519 = -this.field_519;
   }

   public void set(float var1, float var2, float var3, float var4) {
      this.field_517 = var1;
      this.field_518 = var2;
      this.field_519 = var3;
      this.field_520 = var4;
   }

   private static float cos(float var0) {
      return (float)Math.cos((double)var0);
   }

   private static float sin(float var0) {
      return (float)Math.sin((double)var0);
   }

   public void normalize() {
      float var1 = this.method_129() * this.method_129() + this.method_130() * this.method_130() + this.method_131() * this.method_131() + this.method_132() * this.method_132();
      if (var1 > 1.0E-6F) {
         float var2 = Mth.fastInvSqrt(var1);
         this.field_517 *= var2;
         this.field_518 *= var2;
         this.field_519 *= var2;
         this.field_520 *= var2;
      } else {
         this.field_517 = 0.0F;
         this.field_518 = 0.0F;
         this.field_519 = 0.0F;
         this.field_520 = 0.0F;
      }

   }

   public void slerp(Quaternion var1, float var2) {
      throw new UnsupportedOperationException();
   }

   public Quaternion copy() {
      return new Quaternion(this);
   }
}
