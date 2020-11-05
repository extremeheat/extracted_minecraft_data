package net.minecraft.core;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.util.Mth;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Codec<Vec3i> CODEC;
   public static final Vec3i ZERO;
   private int x;
   private int y;
   private int z;

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

   protected void setX(int var1) {
      this.x = var1;
   }

   protected void setY(int var1) {
      this.y = var1;
   }

   protected void setZ(int var1) {
      this.z = var1;
   }

   public Vec3i above() {
      return this.above(1);
   }

   public Vec3i above(int var1) {
      return this.relative(Direction.UP, var1);
   }

   public Vec3i below() {
      return this.below(1);
   }

   public Vec3i below(int var1) {
      return this.relative(Direction.DOWN, var1);
   }

   public Vec3i relative(Direction var1, int var2) {
      return var2 == 0 ? this : new Vec3i(this.getX() + var1.getStepX() * var2, this.getY() + var1.getStepY() * var2, this.getZ() + var1.getStepZ() * var2);
   }

   public Vec3i cross(Vec3i var1) {
      return new Vec3i(this.getY() * var1.getZ() - this.getZ() * var1.getY(), this.getZ() * var1.getX() - this.getX() * var1.getZ(), this.getX() * var1.getY() - this.getY() * var1.getX());
   }

   public boolean closerThan(Vec3i var1, double var2) {
      return this.distSqr((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), false) < var2 * var2;
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
      float var2 = (float)Math.abs(var1.getX() - this.getX());
      float var3 = (float)Math.abs(var1.getY() - this.getY());
      float var4 = (float)Math.abs(var1.getZ() - this.getZ());
      return (int)(var2 + var3 + var4);
   }

   public int get(Direction.Axis var1) {
      return var1.choose(this.x, this.y, this.z);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   public String toShortString() {
      return "" + this.getX() + ", " + this.getY() + ", " + this.getZ();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((Vec3i)var1);
   }

   static {
      CODEC = Codec.INT_STREAM.comapFlatMap((var0) -> {
         return Util.fixedSize(var0, 3).map((var0x) -> {
            return new Vec3i(var0x[0], var0x[1], var0x[2]);
         });
      }, (var0) -> {
         return IntStream.of(new int[]{var0.getX(), var0.getY(), var0.getZ()});
      });
      ZERO = new Vec3i(0, 0, 0);
   }
}
