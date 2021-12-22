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
   // $FF: renamed from: XN com.mojang.math.Vector3f
   public static Vector3f field_289;
   // $FF: renamed from: XP com.mojang.math.Vector3f
   public static Vector3f field_290;
   // $FF: renamed from: YN com.mojang.math.Vector3f
   public static Vector3f field_291;
   // $FF: renamed from: YP com.mojang.math.Vector3f
   public static Vector3f field_292;
   // $FF: renamed from: ZN com.mojang.math.Vector3f
   public static Vector3f field_293;
   // $FF: renamed from: ZP com.mojang.math.Vector3f
   public static Vector3f field_294;
   public static Vector3f ZERO;
   // $FF: renamed from: x float
   private float field_295;
   // $FF: renamed from: y float
   private float field_296;
   // $FF: renamed from: z float
   private float field_297;

   public Vector3f() {
      super();
   }

   public Vector3f(float var1, float var2, float var3) {
      super();
      this.field_295 = var1;
      this.field_296 = var2;
      this.field_297 = var3;
   }

   public Vector3f(Vector4f var1) {
      this(var1.method_66(), var1.method_67(), var1.method_68());
   }

   public Vector3f(Vec3 var1) {
      this((float)var1.field_414, (float)var1.field_415, (float)var1.field_416);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Vector3f var2 = (Vector3f)var1;
         if (Float.compare(var2.field_295, this.field_295) != 0) {
            return false;
         } else if (Float.compare(var2.field_296, this.field_296) != 0) {
            return false;
         } else {
            return Float.compare(var2.field_297, this.field_297) == 0;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = Float.floatToIntBits(this.field_295);
      var1 = 31 * var1 + Float.floatToIntBits(this.field_296);
      var1 = 31 * var1 + Float.floatToIntBits(this.field_297);
      return var1;
   }

   // $FF: renamed from: x () float
   public float method_82() {
      return this.field_295;
   }

   // $FF: renamed from: y () float
   public float method_83() {
      return this.field_296;
   }

   // $FF: renamed from: z () float
   public float method_84() {
      return this.field_297;
   }

   public void mul(float var1) {
      this.field_295 *= var1;
      this.field_296 *= var1;
      this.field_297 *= var1;
   }

   public void mul(float var1, float var2, float var3) {
      this.field_295 *= var1;
      this.field_296 *= var2;
      this.field_297 *= var3;
   }

   public void clamp(Vector3f var1, Vector3f var2) {
      this.field_295 = Mth.clamp(this.field_295, var1.method_82(), var2.method_82());
      this.field_296 = Mth.clamp(this.field_296, var1.method_82(), var2.method_83());
      this.field_297 = Mth.clamp(this.field_297, var1.method_84(), var2.method_84());
   }

   public void clamp(float var1, float var2) {
      this.field_295 = Mth.clamp(this.field_295, var1, var2);
      this.field_296 = Mth.clamp(this.field_296, var1, var2);
      this.field_297 = Mth.clamp(this.field_297, var1, var2);
   }

   public void set(float var1, float var2, float var3) {
      this.field_295 = var1;
      this.field_296 = var2;
      this.field_297 = var3;
   }

   public void load(Vector3f var1) {
      this.field_295 = var1.field_295;
      this.field_296 = var1.field_296;
      this.field_297 = var1.field_297;
   }

   public void add(float var1, float var2, float var3) {
      this.field_295 += var1;
      this.field_296 += var2;
      this.field_297 += var3;
   }

   public void add(Vector3f var1) {
      this.field_295 += var1.field_295;
      this.field_296 += var1.field_296;
      this.field_297 += var1.field_297;
   }

   public void sub(Vector3f var1) {
      this.field_295 -= var1.field_295;
      this.field_296 -= var1.field_296;
      this.field_297 -= var1.field_297;
   }

   public float dot(Vector3f var1) {
      return this.field_295 * var1.field_295 + this.field_296 * var1.field_296 + this.field_297 * var1.field_297;
   }

   public boolean normalize() {
      float var1 = this.field_295 * this.field_295 + this.field_296 * this.field_296 + this.field_297 * this.field_297;
      if ((double)var1 < 1.0E-5D) {
         return false;
      } else {
         float var2 = Mth.fastInvSqrt(var1);
         this.field_295 *= var2;
         this.field_296 *= var2;
         this.field_297 *= var2;
         return true;
      }
   }

   public void cross(Vector3f var1) {
      float var2 = this.field_295;
      float var3 = this.field_296;
      float var4 = this.field_297;
      float var5 = var1.method_82();
      float var6 = var1.method_83();
      float var7 = var1.method_84();
      this.field_295 = var3 * var7 - var4 * var6;
      this.field_296 = var4 * var5 - var2 * var7;
      this.field_297 = var2 * var6 - var3 * var5;
   }

   public void transform(Matrix3f var1) {
      float var2 = this.field_295;
      float var3 = this.field_296;
      float var4 = this.field_297;
      this.field_295 = var1.m00 * var2 + var1.m01 * var3 + var1.m02 * var4;
      this.field_296 = var1.m10 * var2 + var1.m11 * var3 + var1.m12 * var4;
      this.field_297 = var1.m20 * var2 + var1.m21 * var3 + var1.m22 * var4;
   }

   public void transform(Quaternion var1) {
      Quaternion var2 = new Quaternion(var1);
      var2.mul(new Quaternion(this.method_82(), this.method_83(), this.method_84(), 0.0F));
      Quaternion var3 = new Quaternion(var1);
      var3.conj();
      var2.mul(var3);
      this.set(var2.method_129(), var2.method_130(), var2.method_131());
   }

   public void lerp(Vector3f var1, float var2) {
      float var3 = 1.0F - var2;
      this.field_295 = this.field_295 * var3 + var1.field_295 * var2;
      this.field_296 = this.field_296 * var3 + var1.field_296 * var2;
      this.field_297 = this.field_297 * var3 + var1.field_297 * var2;
   }

   public Quaternion rotation(float var1) {
      return new Quaternion(this, var1, false);
   }

   public Quaternion rotationDegrees(float var1) {
      return new Quaternion(this, var1, true);
   }

   public Vector3f copy() {
      return new Vector3f(this.field_295, this.field_296, this.field_297);
   }

   public void map(Float2FloatFunction var1) {
      this.field_295 = var1.get(this.field_295);
      this.field_296 = var1.get(this.field_296);
      this.field_297 = var1.get(this.field_297);
   }

   public String toString() {
      return "[" + this.field_295 + ", " + this.field_296 + ", " + this.field_297 + "]";
   }

   static {
      CODEC = Codec.FLOAT.listOf().comapFlatMap((var0) -> {
         return Util.fixedSize((List)var0, 3).map((var0x) -> {
            return new Vector3f((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2));
         });
      }, (var0) -> {
         return ImmutableList.of(var0.field_295, var0.field_296, var0.field_297);
      });
      field_289 = new Vector3f(-1.0F, 0.0F, 0.0F);
      field_290 = new Vector3f(1.0F, 0.0F, 0.0F);
      field_291 = new Vector3f(0.0F, -1.0F, 0.0F);
      field_292 = new Vector3f(0.0F, 1.0F, 0.0F);
      field_293 = new Vector3f(0.0F, 0.0F, -1.0F);
      field_294 = new Vector3f(0.0F, 0.0F, 1.0F);
      ZERO = new Vector3f(0.0F, 0.0F, 0.0F);
   }
}
