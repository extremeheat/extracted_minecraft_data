package net.minecraft.world.phys;

import com.mojang.math.Vector3f;
import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

public class Vec3 implements Position {
   public static final Vec3 ZERO = new Vec3(0.0D, 0.0D, 0.0D);
   // $FF: renamed from: x double
   public final double field_414;
   // $FF: renamed from: y double
   public final double field_415;
   // $FF: renamed from: z double
   public final double field_416;

   public static Vec3 fromRGB24(int var0) {
      double var1 = (double)(var0 >> 16 & 255) / 255.0D;
      double var3 = (double)(var0 >> 8 & 255) / 255.0D;
      double var5 = (double)(var0 & 255) / 255.0D;
      return new Vec3(var1, var3, var5);
   }

   public static Vec3 atCenterOf(Vec3i var0) {
      return new Vec3((double)var0.getX() + 0.5D, (double)var0.getY() + 0.5D, (double)var0.getZ() + 0.5D);
   }

   public static Vec3 atLowerCornerOf(Vec3i var0) {
      return new Vec3((double)var0.getX(), (double)var0.getY(), (double)var0.getZ());
   }

   public static Vec3 atBottomCenterOf(Vec3i var0) {
      return new Vec3((double)var0.getX() + 0.5D, (double)var0.getY(), (double)var0.getZ() + 0.5D);
   }

   public static Vec3 upFromBottomCenterOf(Vec3i var0, double var1) {
      return new Vec3((double)var0.getX() + 0.5D, (double)var0.getY() + var1, (double)var0.getZ() + 0.5D);
   }

   public Vec3(double var1, double var3, double var5) {
      super();
      this.field_414 = var1;
      this.field_415 = var3;
      this.field_416 = var5;
   }

   public Vec3(Vector3f var1) {
      this((double)var1.method_82(), (double)var1.method_83(), (double)var1.method_84());
   }

   public Vec3 vectorTo(Vec3 var1) {
      return new Vec3(var1.field_414 - this.field_414, var1.field_415 - this.field_415, var1.field_416 - this.field_416);
   }

   public Vec3 normalize() {
      double var1 = Math.sqrt(this.field_414 * this.field_414 + this.field_415 * this.field_415 + this.field_416 * this.field_416);
      return var1 < 1.0E-4D ? ZERO : new Vec3(this.field_414 / var1, this.field_415 / var1, this.field_416 / var1);
   }

   public double dot(Vec3 var1) {
      return this.field_414 * var1.field_414 + this.field_415 * var1.field_415 + this.field_416 * var1.field_416;
   }

   public Vec3 cross(Vec3 var1) {
      return new Vec3(this.field_415 * var1.field_416 - this.field_416 * var1.field_415, this.field_416 * var1.field_414 - this.field_414 * var1.field_416, this.field_414 * var1.field_415 - this.field_415 * var1.field_414);
   }

   public Vec3 subtract(Vec3 var1) {
      return this.subtract(var1.field_414, var1.field_415, var1.field_416);
   }

   public Vec3 subtract(double var1, double var3, double var5) {
      return this.add(-var1, -var3, -var5);
   }

   public Vec3 add(Vec3 var1) {
      return this.add(var1.field_414, var1.field_415, var1.field_416);
   }

   public Vec3 add(double var1, double var3, double var5) {
      return new Vec3(this.field_414 + var1, this.field_415 + var3, this.field_416 + var5);
   }

   public boolean closerThan(Position var1, double var2) {
      return this.distanceToSqr(var1.method_2(), var1.method_3(), var1.method_4()) < var2 * var2;
   }

   public double distanceTo(Vec3 var1) {
      double var2 = var1.field_414 - this.field_414;
      double var4 = var1.field_415 - this.field_415;
      double var6 = var1.field_416 - this.field_416;
      return Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double distanceToSqr(Vec3 var1) {
      double var2 = var1.field_414 - this.field_414;
      double var4 = var1.field_415 - this.field_415;
      double var6 = var1.field_416 - this.field_416;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double distanceToSqr(double var1, double var3, double var5) {
      double var7 = var1 - this.field_414;
      double var9 = var3 - this.field_415;
      double var11 = var5 - this.field_416;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public Vec3 scale(double var1) {
      return this.multiply(var1, var1, var1);
   }

   public Vec3 reverse() {
      return this.scale(-1.0D);
   }

   public Vec3 multiply(Vec3 var1) {
      return this.multiply(var1.field_414, var1.field_415, var1.field_416);
   }

   public Vec3 multiply(double var1, double var3, double var5) {
      return new Vec3(this.field_414 * var1, this.field_415 * var3, this.field_416 * var5);
   }

   public double length() {
      return Math.sqrt(this.field_414 * this.field_414 + this.field_415 * this.field_415 + this.field_416 * this.field_416);
   }

   public double lengthSqr() {
      return this.field_414 * this.field_414 + this.field_415 * this.field_415 + this.field_416 * this.field_416;
   }

   public double horizontalDistance() {
      return Math.sqrt(this.field_414 * this.field_414 + this.field_416 * this.field_416);
   }

   public double horizontalDistanceSqr() {
      return this.field_414 * this.field_414 + this.field_416 * this.field_416;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vec3)) {
         return false;
      } else {
         Vec3 var2 = (Vec3)var1;
         if (Double.compare(var2.field_414, this.field_414) != 0) {
            return false;
         } else if (Double.compare(var2.field_415, this.field_415) != 0) {
            return false;
         } else {
            return Double.compare(var2.field_416, this.field_416) == 0;
         }
      }
   }

   public int hashCode() {
      long var2 = Double.doubleToLongBits(this.field_414);
      int var1 = (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.field_415);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.field_416);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      return var1;
   }

   public String toString() {
      return "(" + this.field_414 + ", " + this.field_415 + ", " + this.field_416 + ")";
   }

   public Vec3 lerp(Vec3 var1, double var2) {
      return new Vec3(Mth.lerp(var2, this.field_414, var1.field_414), Mth.lerp(var2, this.field_415, var1.field_415), Mth.lerp(var2, this.field_416, var1.field_416));
   }

   public Vec3 xRot(float var1) {
      float var2 = Mth.cos(var1);
      float var3 = Mth.sin(var1);
      double var4 = this.field_414;
      double var6 = this.field_415 * (double)var2 + this.field_416 * (double)var3;
      double var8 = this.field_416 * (double)var2 - this.field_415 * (double)var3;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 yRot(float var1) {
      float var2 = Mth.cos(var1);
      float var3 = Mth.sin(var1);
      double var4 = this.field_414 * (double)var2 + this.field_416 * (double)var3;
      double var6 = this.field_415;
      double var8 = this.field_416 * (double)var2 - this.field_414 * (double)var3;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 zRot(float var1) {
      float var2 = Mth.cos(var1);
      float var3 = Mth.sin(var1);
      double var4 = this.field_414 * (double)var2 + this.field_415 * (double)var3;
      double var6 = this.field_415 * (double)var2 - this.field_414 * (double)var3;
      double var8 = this.field_416;
      return new Vec3(var4, var6, var8);
   }

   public static Vec3 directionFromRotation(Vec2 var0) {
      return directionFromRotation(var0.field_412, var0.field_413);
   }

   public static Vec3 directionFromRotation(float var0, float var1) {
      float var2 = Mth.cos(-var1 * 0.017453292F - 3.1415927F);
      float var3 = Mth.sin(-var1 * 0.017453292F - 3.1415927F);
      float var4 = -Mth.cos(-var0 * 0.017453292F);
      float var5 = Mth.sin(-var0 * 0.017453292F);
      return new Vec3((double)(var3 * var4), (double)var5, (double)(var2 * var4));
   }

   public Vec3 align(EnumSet<Direction.Axis> var1) {
      double var2 = var1.contains(Direction.Axis.field_500) ? (double)Mth.floor(this.field_414) : this.field_414;
      double var4 = var1.contains(Direction.Axis.field_501) ? (double)Mth.floor(this.field_415) : this.field_415;
      double var6 = var1.contains(Direction.Axis.field_502) ? (double)Mth.floor(this.field_416) : this.field_416;
      return new Vec3(var2, var4, var6);
   }

   public double get(Direction.Axis var1) {
      return var1.choose(this.field_414, this.field_415, this.field_416);
   }

   public Vec3 with(Direction.Axis var1, double var2) {
      double var4 = var1 == Direction.Axis.field_500 ? var2 : this.field_414;
      double var6 = var1 == Direction.Axis.field_501 ? var2 : this.field_415;
      double var8 = var1 == Direction.Axis.field_502 ? var2 : this.field_416;
      return new Vec3(var4, var6, var8);
   }

   // $FF: renamed from: x () double
   public final double method_2() {
      return this.field_414;
   }

   // $FF: renamed from: y () double
   public final double method_3() {
      return this.field_415;
   }

   // $FF: renamed from: z () double
   public final double method_4() {
      return this.field_416;
   }
}
