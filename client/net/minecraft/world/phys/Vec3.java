package net.minecraft.world.phys;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.joml.Vector3f;

public class Vec3 implements Position {
   public static final Codec<Vec3> CODEC;
   public static final StreamCodec<ByteBuf, Vec3> STREAM_CODEC;
   public static final Vec3 ZERO;
   public final double x;
   public final double y;
   public final double z;

   public static Vec3 fromRGB24(int var0) {
      double var1 = (double)(var0 >> 16 & 255) / 255.0;
      double var3 = (double)(var0 >> 8 & 255) / 255.0;
      double var5 = (double)(var0 & 255) / 255.0;
      return new Vec3(var1, var3, var5);
   }

   public static Vec3 atLowerCornerOf(Vec3i var0) {
      return new Vec3((double)var0.getX(), (double)var0.getY(), (double)var0.getZ());
   }

   public static Vec3 atLowerCornerWithOffset(Vec3i var0, double var1, double var3, double var5) {
      return new Vec3((double)var0.getX() + var1, (double)var0.getY() + var3, (double)var0.getZ() + var5);
   }

   public static Vec3 atCenterOf(Vec3i var0) {
      return atLowerCornerWithOffset(var0, 0.5, 0.5, 0.5);
   }

   public static Vec3 atBottomCenterOf(Vec3i var0) {
      return atLowerCornerWithOffset(var0, 0.5, 0.0, 0.5);
   }

   public static Vec3 upFromBottomCenterOf(Vec3i var0, double var1) {
      return atLowerCornerWithOffset(var0, 0.5, var1, 0.5);
   }

   public Vec3(double var1, double var3, double var5) {
      super();
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
      double var1 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return var1 < 9.999999747378752E-6 ? ZERO : new Vec3(this.x / var1, this.y / var1, this.z / var1);
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

   public Vec3 subtract(double var1) {
      return this.subtract(var1, var1, var1);
   }

   public Vec3 subtract(double var1, double var3, double var5) {
      return this.add(-var1, -var3, -var5);
   }

   public Vec3 add(double var1) {
      return this.add(var1, var1, var1);
   }

   public Vec3 add(Vec3 var1) {
      return this.add(var1.x, var1.y, var1.z);
   }

   public Vec3 add(double var1, double var3, double var5) {
      return new Vec3(this.x + var1, this.y + var3, this.z + var5);
   }

   public boolean closerThan(Position var1, double var2) {
      return this.distanceToSqr(var1.x(), var1.y(), var1.z()) < var2 * var2;
   }

   public double distanceTo(Vec3 var1) {
      double var2 = var1.x - this.x;
      double var4 = var1.y - this.y;
      double var6 = var1.z - this.z;
      return Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
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

   public boolean closerThan(Vec3 var1, double var2, double var4) {
      double var6 = var1.x() - this.x;
      double var8 = var1.y() - this.y;
      double var10 = var1.z() - this.z;
      return Mth.lengthSquared(var6, var10) < Mth.square(var2) && Math.abs(var8) < var4;
   }

   public Vec3 scale(double var1) {
      return this.multiply(var1, var1, var1);
   }

   public Vec3 reverse() {
      return this.scale(-1.0);
   }

   public Vec3 multiply(Vec3 var1) {
      return this.multiply(var1.x, var1.y, var1.z);
   }

   public Vec3 multiply(double var1, double var3, double var5) {
      return new Vec3(this.x * var1, this.y * var3, this.z * var5);
   }

   public Vec3 horizontal() {
      return new Vec3(this.x, 0.0, this.z);
   }

   public Vec3 offsetRandom(RandomSource var1, float var2) {
      return this.add((double)((var1.nextFloat() - 0.5F) * var2), (double)((var1.nextFloat() - 0.5F) * var2), (double)((var1.nextFloat() - 0.5F) * var2));
   }

   public double length() {
      return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public double lengthSqr() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public double horizontalDistance() {
      return Math.sqrt(this.x * this.x + this.z * this.z);
   }

   public double horizontalDistanceSqr() {
      return this.x * this.x + this.z * this.z;
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

   public Vec3 lerp(Vec3 var1, double var2) {
      return new Vec3(Mth.lerp(var2, this.x, var1.x), Mth.lerp(var2, this.y, var1.y), Mth.lerp(var2, this.z, var1.z));
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

   public Vec3 zRot(float var1) {
      float var2 = Mth.cos(var1);
      float var3 = Mth.sin(var1);
      double var4 = this.x * (double)var2 + this.y * (double)var3;
      double var6 = this.y * (double)var2 - this.x * (double)var3;
      double var8 = this.z;
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

   public Vec3 align(EnumSet<Direction.Axis> var1) {
      double var2 = var1.contains(Direction.Axis.X) ? (double)Mth.floor(this.x) : this.x;
      double var4 = var1.contains(Direction.Axis.Y) ? (double)Mth.floor(this.y) : this.y;
      double var6 = var1.contains(Direction.Axis.Z) ? (double)Mth.floor(this.z) : this.z;
      return new Vec3(var2, var4, var6);
   }

   public double get(Direction.Axis var1) {
      return var1.choose(this.x, this.y, this.z);
   }

   public Vec3 with(Direction.Axis var1, double var2) {
      double var4 = var1 == Direction.Axis.X ? var2 : this.x;
      double var6 = var1 == Direction.Axis.Y ? var2 : this.y;
      double var8 = var1 == Direction.Axis.Z ? var2 : this.z;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 relative(Direction var1, double var2) {
      Vec3i var4 = var1.getUnitVec3i();
      return new Vec3(this.x + var2 * (double)var4.getX(), this.y + var2 * (double)var4.getY(), this.z + var2 * (double)var4.getZ());
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

   public Vector3f toVector3f() {
      return new Vector3f((float)this.x, (float)this.y, (float)this.z);
   }

   public Vec3 projectedOn(Vec3 var1) {
      return var1.lengthSqr() == 0.0 ? var1 : var1.scale(this.dot(var1)).scale(1.0 / var1.lengthSqr());
   }

   static {
      CODEC = Codec.DOUBLE.listOf().comapFlatMap((var0) -> Util.fixedSize((List)var0, 3).map((var0x) -> new Vec3((Double)var0x.get(0), (Double)var0x.get(1), (Double)var0x.get(2))), (var0) -> List.of(var0.x(), var0.y(), var0.z()));
      STREAM_CODEC = new StreamCodec<ByteBuf, Vec3>() {
         public Vec3 decode(ByteBuf var1) {
            return FriendlyByteBuf.readVec3(var1);
         }

         public void encode(ByteBuf var1, Vec3 var2) {
            FriendlyByteBuf.writeVec3(var1, var2);
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (Vec3)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
      ZERO = new Vec3(0.0, 0.0, 0.0);
   }
}
