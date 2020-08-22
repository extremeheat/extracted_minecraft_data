package net.minecraft.world.phys;

import com.mojang.math.Vector3f;
import java.util.EnumSet;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;

public class Vec3 implements Position {
   public static final Vec3 ZERO = new Vec3(0.0D, 0.0D, 0.0D);
   public final double x;
   public final double y;
   public final double z;

   public Vec3(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
   }

   public Vec3(Vector3f var1) {
      this((double)var1.x(), (double)var1.y(), (double)var1.z());
   }

   public Vec3(Vec3i var1) {
      this((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
   }

   public Vec3 vectorTo(Vec3 var1) {
      return new Vec3(var1.x - this.x, var1.y - this.y, var1.z - this.z);
   }

   public Vec3 normalize() {
      double var1 = (double)Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return var1 < 1.0E-4D ? ZERO : new Vec3(this.x / var1, this.y / var1, this.z / var1);
   }

   public double dot(Vec3 var1) {
      return this.x * var1.x + this.y * var1.y + this.z * var1.z;
   }

   public Vec3 cross(Vec3 var1) {
      return new Vec3(this.y * var1.z - this.z * var1.y, this.z * var1.x - this.x * var1.z, this.x * var1.y - this.y * var1.x);
   }

   public Vec3 subtract(Vec3 var1) {
      return this.subtract(var1.x, var1.y, var1.z);
   }

   public Vec3 subtract(double var1, double var3, double var5) {
      return this.add(-var1, -var3, -var5);
   }

   public Vec3 add(Vec3 var1) {
      return this.add(var1.x, var1.y, var1.z);
   }

   public Vec3 add(double var1, double var3, double var5) {
      return new Vec3(this.x + var1, this.y + var3, this.z + var5);
   }

   public double distanceTo(Vec3 var1) {
      double var2 = var1.x - this.x;
      double var4 = var1.y - this.y;
      double var6 = var1.z - this.z;
      return (double)Mth.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double distanceToSqr(Vec3 var1) {
      double var2 = var1.x - this.x;
      double var4 = var1.y - this.y;
      double var6 = var1.z - this.z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double distanceToSqr(double var1, double var3, double var5) {
      double var7 = var1 - this.x;
      double var9 = var3 - this.y;
      double var11 = var5 - this.z;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public Vec3 scale(double var1) {
      return this.multiply(var1, var1, var1);
   }

   public Vec3 reverse() {
      return this.scale(-1.0D);
   }

   public Vec3 multiply(Vec3 var1) {
      return this.multiply(var1.x, var1.y, var1.z);
   }

   public Vec3 multiply(double var1, double var3, double var5) {
      return new Vec3(this.x * var1, this.y * var3, this.z * var5);
   }

   public double length() {
      return (double)Mth.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public double lengthSqr() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vec3)) {
         return false;
      } else {
         Vec3 var2 = (Vec3)var1;
         if (Double.compare(var2.x, this.x) != 0) {
            return false;
         } else if (Double.compare(var2.y, this.y) != 0) {
            return false;
         } else {
            return Double.compare(var2.z, this.z) == 0;
         }
      }
   }

   public int hashCode() {
      long var2 = Double.doubleToLongBits(this.x);
      int var1 = (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.y);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.z);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      return var1;
   }

   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   public Vec3 xRot(float var1) {
      float var2 = Mth.cos(var1);
      float var3 = Mth.sin(var1);
      double var4 = this.x;
      double var6 = this.y * (double)var2 + this.z * (double)var3;
      double var8 = this.z * (double)var2 - this.y * (double)var3;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 yRot(float var1) {
      float var2 = Mth.cos(var1);
      float var3 = Mth.sin(var1);
      double var4 = this.x * (double)var2 + this.z * (double)var3;
      double var6 = this.y;
      double var8 = this.z * (double)var2 - this.x * (double)var3;
      return new Vec3(var4, var6, var8);
   }

   public static Vec3 directionFromRotation(Vec2 var0) {
      return directionFromRotation(var0.x, var0.y);
   }

   public static Vec3 directionFromRotation(float var0, float var1) {
      float var2 = Mth.cos(-var1 * 0.017453292F - 3.1415927F);
      float var3 = Mth.sin(-var1 * 0.017453292F - 3.1415927F);
      float var4 = -Mth.cos(-var0 * 0.017453292F);
      float var5 = Mth.sin(-var0 * 0.017453292F);
      return new Vec3((double)(var3 * var4), (double)var5, (double)(var2 * var4));
   }

   public Vec3 align(EnumSet var1) {
      double var2 = var1.contains(Direction.Axis.X) ? (double)Mth.floor(this.x) : this.x;
      double var4 = var1.contains(Direction.Axis.Y) ? (double)Mth.floor(this.y) : this.y;
      double var6 = var1.contains(Direction.Axis.Z) ? (double)Mth.floor(this.z) : this.z;
      return new Vec3(var2, var4, var6);
   }

   public double get(Direction.Axis var1) {
      return var1.choose(this.x, this.y, this.z);
   }

   public final double x() {
      return this.x;
   }

   public final double y() {
      return this.y;
   }

   public final double z() {
      return this.z;
   }
}
