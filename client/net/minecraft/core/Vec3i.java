package net.minecraft.core;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.util.Mth;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Codec<Vec3i> CODEC = Codec.INT_STREAM
      .comapFlatMap(
         var0 -> Util.fixedSize(var0, 3).map(var0x -> new Vec3i(var0x[0], var0x[1], var0x[2])), var0 -> IntStream.of(var0.getX(), var0.getY(), var0.getZ())
      );
   public static final Vec3i ZERO = new Vec3i(0, 0, 0);
   private int x;
   private int y;
   private int z;

   public static Codec<Vec3i> offsetCodec(int var0) {
      return CODEC.validate(
         var1 -> Math.abs(var1.getX()) < var0 && Math.abs(var1.getY()) < var0 && Math.abs(var1.getZ()) < var0
               ? DataResult.success(var1)
               : DataResult.error(() -> "Position out of range, expected at most " + var0 + ": " + var1)
      );
   }

   public Vec3i(int var1, int var2, int var3) {
      super();
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Vec3i var2)) {
         return false;
      } else if (this.getX() != var2.getX()) {
         return false;
      } else {
         return this.getY() != var2.getY() ? false : this.getZ() == var2.getZ();
      }
   }

   @Override
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

   protected Vec3i setX(int var1) {
      this.x = var1;
      return this;
   }

   protected Vec3i setY(int var1) {
      this.y = var1;
      return this;
   }

   protected Vec3i setZ(int var1) {
      this.z = var1;
      return this;
   }

   public Vec3i offset(int var1, int var2, int var3) {
      return var1 == 0 && var2 == 0 && var3 == 0 ? this : new Vec3i(this.getX() + var1, this.getY() + var2, this.getZ() + var3);
   }

   public Vec3i offset(Vec3i var1) {
      return this.offset(var1.getX(), var1.getY(), var1.getZ());
   }

   public Vec3i subtract(Vec3i var1) {
      return this.offset(-var1.getX(), -var1.getY(), -var1.getZ());
   }

   public Vec3i multiply(int var1) {
      if (var1 == 1) {
         return this;
      } else {
         return var1 == 0 ? ZERO : new Vec3i(this.getX() * var1, this.getY() * var1, this.getZ() * var1);
      }
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

   public Vec3i north() {
      return this.north(1);
   }

   public Vec3i north(int var1) {
      return this.relative(Direction.NORTH, var1);
   }

   public Vec3i south() {
      return this.south(1);
   }

   public Vec3i south(int var1) {
      return this.relative(Direction.SOUTH, var1);
   }

   public Vec3i west() {
      return this.west(1);
   }

   public Vec3i west(int var1) {
      return this.relative(Direction.WEST, var1);
   }

   public Vec3i east() {
      return this.east(1);
   }

   public Vec3i east(int var1) {
      return this.relative(Direction.EAST, var1);
   }

   public Vec3i relative(Direction var1) {
      return this.relative(var1, 1);
   }

   public Vec3i relative(Direction var1, int var2) {
      return var2 == 0 ? this : new Vec3i(this.getX() + var1.getStepX() * var2, this.getY() + var1.getStepY() * var2, this.getZ() + var1.getStepZ() * var2);
   }

   public Vec3i relative(Direction.Axis var1, int var2) {
      if (var2 == 0) {
         return this;
      } else {
         int var3 = var1 == Direction.Axis.X ? var2 : 0;
         int var4 = var1 == Direction.Axis.Y ? var2 : 0;
         int var5 = var1 == Direction.Axis.Z ? var2 : 0;
         return new Vec3i(this.getX() + var3, this.getY() + var4, this.getZ() + var5);
      }
   }

   public Vec3i cross(Vec3i var1) {
      return new Vec3i(
         this.getY() * var1.getZ() - this.getZ() * var1.getY(),
         this.getZ() * var1.getX() - this.getX() * var1.getZ(),
         this.getX() * var1.getY() - this.getY() * var1.getX()
      );
   }

   public boolean closerThan(Vec3i var1, double var2) {
      return this.distSqr(var1) < Mth.square(var2);
   }

   public boolean closerToCenterThan(Position var1, double var2) {
      return this.distToCenterSqr(var1) < Mth.square(var2);
   }

   public double distSqr(Vec3i var1) {
      return this.distToLowCornerSqr((double)var1.getX(), (double)var1.getY(), (double)var1.getZ());
   }

   public double distToCenterSqr(Position var1) {
      return this.distToCenterSqr(var1.x(), var1.y(), var1.z());
   }

   public double distToCenterSqr(double var1, double var3, double var5) {
      double var7 = (double)this.getX() + 0.5 - var1;
      double var9 = (double)this.getY() + 0.5 - var3;
      double var11 = (double)this.getZ() + 0.5 - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double distToLowCornerSqr(double var1, double var3, double var5) {
      double var7 = (double)this.getX() - var1;
      double var9 = (double)this.getY() - var3;
      double var11 = (double)this.getZ() - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public int distManhattan(Vec3i var1) {
      float var2 = (float)Math.abs(var1.getX() - this.getX());
      float var3 = (float)Math.abs(var1.getY() - this.getY());
      float var4 = (float)Math.abs(var1.getZ() - this.getZ());
      return (int)(var2 + var3 + var4);
   }

   public int distChessboard(Vec3i var1) {
      int var2 = Math.abs(this.getX() - var1.getX());
      int var3 = Math.abs(this.getY() - var1.getY());
      int var4 = Math.abs(this.getZ() - var1.getZ());
      return Math.max(Math.max(var2, var3), var4);
   }

   public int get(Direction.Axis var1) {
      return var1.choose(this.x, this.y, this.z);
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   public String toShortString() {
      return this.getX() + ", " + this.getY() + ", " + this.getZ();
   }
}
