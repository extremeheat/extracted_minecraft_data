package net.minecraft.core;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.util.Mth;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Codec<Vec3i> CODEC;
   public static final Vec3i ZERO;
   // $FF: renamed from: x int
   private int field_283;
   // $FF: renamed from: y int
   private int field_284;
   // $FF: renamed from: z int
   private int field_285;

   private static Function<Vec3i, DataResult<Vec3i>> checkOffsetAxes(int var0) {
      return (var1) -> {
         return Math.abs(var1.getX()) < var0 && Math.abs(var1.getY()) < var0 && Math.abs(var1.getZ()) < var0 ? DataResult.success(var1) : DataResult.error("Position out of range, expected at most " + var0 + ": " + var1);
      };
   }

   public static Codec<Vec3i> offsetCodec(int var0) {
      return CODEC.flatXmap(checkOffsetAxes(var0), checkOffsetAxes(var0));
   }

   public Vec3i(int var1, int var2, int var3) {
      super();
      this.field_283 = var1;
      this.field_284 = var2;
      this.field_285 = var3;
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
      return this.field_283;
   }

   public int getY() {
      return this.field_284;
   }

   public int getZ() {
      return this.field_285;
   }

   protected Vec3i setX(int var1) {
      this.field_283 = var1;
      return this;
   }

   protected Vec3i setY(int var1) {
      this.field_284 = var1;
      return this;
   }

   protected Vec3i setZ(int var1) {
      this.field_285 = var1;
      return this;
   }

   public Vec3i offset(double var1, double var3, double var5) {
      return var1 == 0.0D && var3 == 0.0D && var5 == 0.0D ? this : new Vec3i((double)this.getX() + var1, (double)this.getY() + var3, (double)this.getZ() + var5);
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
      return this.relative(Direction.field_526, var1);
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
      return this.relative((Direction)var1, 1);
   }

   public Vec3i relative(Direction var1, int var2) {
      return var2 == 0 ? this : new Vec3i(this.getX() + var1.getStepX() * var2, this.getY() + var1.getStepY() * var2, this.getZ() + var1.getStepZ() * var2);
   }

   public Vec3i relative(Direction.Axis var1, int var2) {
      if (var2 == 0) {
         return this;
      } else {
         int var3 = var1 == Direction.Axis.field_500 ? var2 : 0;
         int var4 = var1 == Direction.Axis.field_501 ? var2 : 0;
         int var5 = var1 == Direction.Axis.field_502 ? var2 : 0;
         return new Vec3i(this.getX() + var3, this.getY() + var4, this.getZ() + var5);
      }
   }

   public Vec3i cross(Vec3i var1) {
      return new Vec3i(this.getY() * var1.getZ() - this.getZ() * var1.getY(), this.getZ() * var1.getX() - this.getX() * var1.getZ(), this.getX() * var1.getY() - this.getY() * var1.getX());
   }

   public boolean closerThan(Vec3i var1, double var2) {
      return this.distSqr((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), false) < var2 * var2;
   }

   public boolean closerThan(Position var1, double var2) {
      return this.distSqr(var1.method_2(), var1.method_3(), var1.method_4(), true) < var2 * var2;
   }

   public double distSqr(Vec3i var1) {
      return this.distSqr((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), true);
   }

   public double distSqr(Position var1, boolean var2) {
      return this.distSqr(var1.method_2(), var1.method_3(), var1.method_4(), var2);
   }

   public double distSqr(Vec3i var1, boolean var2) {
      return this.distSqr((double)var1.field_283, (double)var1.field_284, (double)var1.field_285, var2);
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
      return var1.choose(this.field_283, this.field_284, this.field_285);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   public String toShortString() {
      int var10000 = this.getX();
      return var10000 + ", " + this.getY() + ", " + this.getZ();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((Vec3i)var1);
   }

   static {
      CODEC = Codec.INT_STREAM.comapFlatMap((var0) -> {
         return Util.fixedSize((IntStream)var0, 3).map((var0x) -> {
            return new Vec3i(var0x[0], var0x[1], var0x[2]);
         });
      }, (var0) -> {
         return IntStream.of(new int[]{var0.getX(), var0.getY(), var0.getZ()});
      });
      ZERO = new Vec3i(0, 0, 0);
   }
}
