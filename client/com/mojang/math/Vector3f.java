package com.mojang.math;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public final class Vector3f {
   public static final Codec<Vector3f> CODEC;
   public static Vector3f XN;
   public static Vector3f XP;
   public static Vector3f YN;
   public static Vector3f YP;
   public static Vector3f ZN;
   public static Vector3f ZP;
   public static Vector3f ZERO;
   private float x;
   private float y;
   private float z;

   public Vector3f() {
      super();
   }

   public Vector3f(float var1, float var2, float var3) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public Vector3f(Vector4f var1) {
      this(var1.x(), var1.y(), var1.z());
   }

   public Vector3f(Vec3 var1) {
      this((float)var1.x, (float)var1.y, (float)var1.z);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector3f var2 = (Vector3f)var1;
         if (Float.compare(var2.x, this.x) != 0) {
            return false;
         } else if (Float.compare(var2.y, this.y) != 0) {
            return false;
         } else {
            return Float.compare(var2.z, this.z) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = Float.floatToIntBits(this.x);
      var1 = 31 * var1 + Float.floatToIntBits(this.y);
      var1 = 31 * var1 + Float.floatToIntBits(this.z);
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

   public void mul(float var1) {
      this.x *= var1;
      this.y *= var1;
      this.z *= var1;
   }

   public void mul(float var1, float var2, float var3) {
      this.x *= var1;
      this.y *= var2;
      this.z *= var3;
   }

   public void clamp(Vector3f var1, Vector3f var2) {
      this.x = Mth.clamp(this.x, var1.x(), var2.x());
      this.y = Mth.clamp(this.y, var1.x(), var2.y());
      this.z = Mth.clamp(this.z, var1.z(), var2.z());
   }

   public void clamp(float var1, float var2) {
      this.x = Mth.clamp(this.x, var1, var2);
      this.y = Mth.clamp(this.y, var1, var2);
      this.z = Mth.clamp(this.z, var1, var2);
   }

   public void set(float var1, float var2, float var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public void load(Vector3f var1) {
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
   }

   public void add(float var1, float var2, float var3) {
      this.x += var1;
      this.y += var2;
      this.z += var3;
   }

   public void add(Vector3f var1) {
      this.x += var1.x;
      this.y += var1.y;
      this.z += var1.z;
   }

   public void sub(Vector3f var1) {
      this.x -= var1.x;
      this.y -= var1.y;
      this.z -= var1.z;
   }

   public float dot(Vector3f var1) {
      return this.x * var1.x + this.y * var1.y + this.z * var1.z;
   }

   public boolean normalize() {
      float var1 = this.x * this.x + this.y * this.y + this.z * this.z;
      if ((double)var1 < 1.0E-5) {
         return false;
      } else {
         float var2 = Mth.fastInvSqrt(var1);
         this.x *= var2;
         this.y *= var2;
         this.z *= var2;
         return true;
      }
   }

   public void cross(Vector3f var1) {
      float var2 = this.x;
      float var3 = this.y;
      float var4 = this.z;
      float var5 = var1.x();
      float var6 = var1.y();
      float var7 = var1.z();
      this.x = var3 * var7 - var4 * var6;
      this.y = var4 * var5 - var2 * var7;
      this.z = var2 * var6 - var3 * var5;
   }

   public void transform(Matrix3f var1) {
      float var2 = this.x;
      float var3 = this.y;
      float var4 = this.z;
      this.x = var1.m00 * var2 + var1.m01 * var3 + var1.m02 * var4;
      this.y = var1.m10 * var2 + var1.m11 * var3 + var1.m12 * var4;
      this.z = var1.m20 * var2 + var1.m21 * var3 + var1.m22 * var4;
   }

   public void transform(Quaternion var1) {
      Quaternion var2 = new Quaternion(var1);
      var2.mul(new Quaternion(this.x(), this.y(), this.z(), 0.0F));
      Quaternion var3 = new Quaternion(var1);
      var3.conj();
      var2.mul(var3);
      this.set(var2.i(), var2.j(), var2.k());
   }

   public void lerp(Vector3f var1, float var2) {
      float var3 = 1.0F - var2;
      this.x = this.x * var3 + var1.x * var2;
      this.y = this.y * var3 + var1.y * var2;
      this.z = this.z * var3 + var1.z * var2;
   }

   public Quaternion rotation(float var1) {
      return new Quaternion(this, var1, false);
   }

   public Quaternion rotationDegrees(float var1) {
      return new Quaternion(this, var1, true);
   }

   public Vector3f copy() {
      return new Vector3f(this.x, this.y, this.z);
   }

   public void map(Float2FloatFunction var1) {
      this.x = var1.get(this.x);
      this.y = var1.get(this.y);
      this.z = var1.get(this.z);
   }

   public String toString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

   static {
      CODEC = Codec.FLOAT.listOf().comapFlatMap((var0) -> {
         return Util.fixedSize((List)var0, 3).map((var0x) -> {
            return new Vector3f((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2));
         });
      }, (var0) -> {
         return ImmutableList.of(var0.x, var0.y, var0.z);
      });
      XN = new Vector3f(-1.0F, 0.0F, 0.0F);
      XP = new Vector3f(1.0F, 0.0F, 0.0F);
      YN = new Vector3f(0.0F, -1.0F, 0.0F);
      YP = new Vector3f(0.0F, 1.0F, 0.0F);
      ZN = new Vector3f(0.0F, 0.0F, -1.0F);
      ZP = new Vector3f(0.0F, 0.0F, 1.0F);
      ZERO = new Vector3f(0.0F, 0.0F, 0.0F);
   }
}
