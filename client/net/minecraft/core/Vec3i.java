package net.minecraft.core;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;
import net.minecraft.util.Mth;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Vec3i ZERO = new Vec3i(0, 0, 0);
   private final int x;
   private final int y;
   private final int z;

   public Vec3i(int var1, int var2, int var3) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public Vec3i(double var1, double var3, double var5) {
      this(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5));
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vec3i)) {
         return false;
      } else {
         Vec3i var2 = (Vec3i)var1;
         if (this.getX() != var2.getX()) {
            return false;
         } else if (this.getY() != var2.getY()) {
            return false;
         } else {
            return this.getZ() == var2.getZ();
         }
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i var1) {
      if (this.getY() == var1.getY()) {
         return this.getZ() == var1.getZ() ? this.getX() - var1.getX() : this.getZ() - var1.getZ();
      } else {
         return this.getY() - var1.getY();
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public Vec3i cross(Vec3i var1) {
      return new Vec3i(this.getY() * var1.getZ() - this.getZ() * var1.getY(), this.getZ() * var1.getX() - this.getX() * var1.getZ(), this.getX() * var1.getY() - this.getY() * var1.getX());
   }

   public boolean closerThan(Vec3i var1, double var2) {
      return this.distSqr((double)var1.x, (double)var1.y, (double)var1.z, false) < var2 * var2;
   }

   public boolean closerThan(Position var1, double var2) {
      return this.distSqr(var1.x(), var1.y(), var1.z(), true) < var2 * var2;
   }

   public double distSqr(Vec3i var1) {
      return this.distSqr((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), true);
   }

   public double distSqr(Position var1, boolean var2) {
      return this.distSqr(var1.x(), var1.y(), var1.z(), var2);
   }

   public double distSqr(double var1, double var3, double var5, boolean var7) {
      double var8 = var7 ? 0.5D : 0.0D;
      double var10 = (double)this.getX() + var8 - var1;
      double var12 = (double)this.getY() + var8 - var3;
      double var14 = (double)this.getZ() + var8 - var5;
      return var10 * var10 + var12 * var12 + var14 * var14;
   }

   public int distManhattan(Vec3i var1) {
      float var2 = (float)Math.abs(var1.getX() - this.x);
      float var3 = (float)Math.abs(var1.getY() - this.y);
      float var4 = (float)Math.abs(var1.getZ() - this.z);
      return (int)(var2 + var3 + var4);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((Vec3i)var1);
   }
}
