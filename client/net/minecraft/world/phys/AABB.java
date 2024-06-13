package net.minecraft.world.phys;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.joml.Vector3f;

public class AABB {
   private static final double EPSILON = 1.0E-7;
   public final double minX;
   public final double minY;
   public final double minZ;
   public final double maxX;
   public final double maxY;
   public final double maxZ;

   public AABB(double var1, double var3, double var5, double var7, double var9, double var11) {
      super();
      this.minX = Math.min(var1, var7);
      this.minY = Math.min(var3, var9);
      this.minZ = Math.min(var5, var11);
      this.maxX = Math.max(var1, var7);
      this.maxY = Math.max(var3, var9);
      this.maxZ = Math.max(var5, var11);
   }

   public AABB(BlockPos var1) {
      this((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), (double)(var1.getX() + 1), (double)(var1.getY() + 1), (double)(var1.getZ() + 1));
   }

   public AABB(Vec3 var1, Vec3 var2) {
      this(var1.x, var1.y, var1.z, var2.x, var2.y, var2.z);
   }

   public static AABB of(BoundingBox var0) {
      return new AABB(
         (double)var0.minX(), (double)var0.minY(), (double)var0.minZ(), (double)(var0.maxX() + 1), (double)(var0.maxY() + 1), (double)(var0.maxZ() + 1)
      );
   }

   public static AABB unitCubeFromLowerCorner(Vec3 var0) {
      return new AABB(var0.x, var0.y, var0.z, var0.x + 1.0, var0.y + 1.0, var0.z + 1.0);
   }

   public static AABB encapsulatingFullBlocks(BlockPos var0, BlockPos var1) {
      return new AABB(
         (double)Math.min(var0.getX(), var1.getX()),
         (double)Math.min(var0.getY(), var1.getY()),
         (double)Math.min(var0.getZ(), var1.getZ()),
         (double)(Math.max(var0.getX(), var1.getX()) + 1),
         (double)(Math.max(var0.getY(), var1.getY()) + 1),
         (double)(Math.max(var0.getZ(), var1.getZ()) + 1)
      );
   }

   public AABB setMinX(double var1) {
      return new AABB(var1, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public AABB setMinY(double var1) {
      return new AABB(this.minX, var1, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public AABB setMinZ(double var1) {
      return new AABB(this.minX, this.minY, var1, this.maxX, this.maxY, this.maxZ);
   }

   public AABB setMaxX(double var1) {
      return new AABB(this.minX, this.minY, this.minZ, var1, this.maxY, this.maxZ);
   }

   public AABB setMaxY(double var1) {
      return new AABB(this.minX, this.minY, this.minZ, this.maxX, var1, this.maxZ);
   }

   public AABB setMaxZ(double var1) {
      return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, var1);
   }

   public double min(Direction.Axis var1) {
      return var1.choose(this.minX, this.minY, this.minZ);
   }

   public double max(Direction.Axis var1) {
      return var1.choose(this.maxX, this.maxY, this.maxZ);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AABB var2)) {
         return false;
      } else if (Double.compare(var2.minX, this.minX) != 0) {
         return false;
      } else if (Double.compare(var2.minY, this.minY) != 0) {
         return false;
      } else if (Double.compare(var2.minZ, this.minZ) != 0) {
         return false;
      } else if (Double.compare(var2.maxX, this.maxX) != 0) {
         return false;
      } else {
         return Double.compare(var2.maxY, this.maxY) != 0 ? false : Double.compare(var2.maxZ, this.maxZ) == 0;
      }
   }

   @Override
   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.minX);
      int var3 = (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.minY);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.minZ);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.maxX);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.maxY);
      var3 = 31 * var3 + (int)(var1 ^ var1 >>> 32);
      var1 = Double.doubleToLongBits(this.maxZ);
      return 31 * var3 + (int)(var1 ^ var1 >>> 32);
   }

   public AABB contract(double var1, double var3, double var5) {
      double var7 = this.minX;
      double var9 = this.minY;
      double var11 = this.minZ;
      double var13 = this.maxX;
      double var15 = this.maxY;
      double var17 = this.maxZ;
      if (var1 < 0.0) {
         var7 -= var1;
      } else if (var1 > 0.0) {
         var13 -= var1;
      }

      if (var3 < 0.0) {
         var9 -= var3;
      } else if (var3 > 0.0) {
         var15 -= var3;
      }

      if (var5 < 0.0) {
         var11 -= var5;
      } else if (var5 > 0.0) {
         var17 -= var5;
      }

      return new AABB(var7, var9, var11, var13, var15, var17);
   }

   public AABB expandTowards(Vec3 var1) {
      return this.expandTowards(var1.x, var1.y, var1.z);
   }

   public AABB expandTowards(double var1, double var3, double var5) {
      double var7 = this.minX;
      double var9 = this.minY;
      double var11 = this.minZ;
      double var13 = this.maxX;
      double var15 = this.maxY;
      double var17 = this.maxZ;
      if (var1 < 0.0) {
         var7 += var1;
      } else if (var1 > 0.0) {
         var13 += var1;
      }

      if (var3 < 0.0) {
         var9 += var3;
      } else if (var3 > 0.0) {
         var15 += var3;
      }

      if (var5 < 0.0) {
         var11 += var5;
      } else if (var5 > 0.0) {
         var17 += var5;
      }

      return new AABB(var7, var9, var11, var13, var15, var17);
   }

   public AABB inflate(double var1, double var3, double var5) {
      double var7 = this.minX - var1;
      double var9 = this.minY - var3;
      double var11 = this.minZ - var5;
      double var13 = this.maxX + var1;
      double var15 = this.maxY + var3;
      double var17 = this.maxZ + var5;
      return new AABB(var7, var9, var11, var13, var15, var17);
   }

   public AABB inflate(double var1) {
      return this.inflate(var1, var1, var1);
   }

   public AABB intersect(AABB var1) {
      double var2 = Math.max(this.minX, var1.minX);
      double var4 = Math.max(this.minY, var1.minY);
      double var6 = Math.max(this.minZ, var1.minZ);
      double var8 = Math.min(this.maxX, var1.maxX);
      double var10 = Math.min(this.maxY, var1.maxY);
      double var12 = Math.min(this.maxZ, var1.maxZ);
      return new AABB(var2, var4, var6, var8, var10, var12);
   }

   public AABB minmax(AABB var1) {
      double var2 = Math.min(this.minX, var1.minX);
      double var4 = Math.min(this.minY, var1.minY);
      double var6 = Math.min(this.minZ, var1.minZ);
      double var8 = Math.max(this.maxX, var1.maxX);
      double var10 = Math.max(this.maxY, var1.maxY);
      double var12 = Math.max(this.maxZ, var1.maxZ);
      return new AABB(var2, var4, var6, var8, var10, var12);
   }

   public AABB move(double var1, double var3, double var5) {
      return new AABB(this.minX + var1, this.minY + var3, this.minZ + var5, this.maxX + var1, this.maxY + var3, this.maxZ + var5);
   }

   public AABB move(BlockPos var1) {
      return new AABB(
         this.minX + (double)var1.getX(),
         this.minY + (double)var1.getY(),
         this.minZ + (double)var1.getZ(),
         this.maxX + (double)var1.getX(),
         this.maxY + (double)var1.getY(),
         this.maxZ + (double)var1.getZ()
      );
   }

   public AABB move(Vec3 var1) {
      return this.move(var1.x, var1.y, var1.z);
   }

   public AABB move(Vector3f var1) {
      return this.move((double)var1.x, (double)var1.y, (double)var1.z);
   }

   public boolean intersects(AABB var1) {
      return this.intersects(var1.minX, var1.minY, var1.minZ, var1.maxX, var1.maxY, var1.maxZ);
   }

   public boolean intersects(double var1, double var3, double var5, double var7, double var9, double var11) {
      return this.minX < var7 && this.maxX > var1 && this.minY < var9 && this.maxY > var3 && this.minZ < var11 && this.maxZ > var5;
   }

   public boolean intersects(Vec3 var1, Vec3 var2) {
      return this.intersects(
         Math.min(var1.x, var2.x),
         Math.min(var1.y, var2.y),
         Math.min(var1.z, var2.z),
         Math.max(var1.x, var2.x),
         Math.max(var1.y, var2.y),
         Math.max(var1.z, var2.z)
      );
   }

   public boolean contains(Vec3 var1) {
      return this.contains(var1.x, var1.y, var1.z);
   }

   public boolean contains(double var1, double var3, double var5) {
      return var1 >= this.minX && var1 < this.maxX && var3 >= this.minY && var3 < this.maxY && var5 >= this.minZ && var5 < this.maxZ;
   }

   public double getSize() {
      double var1 = this.getXsize();
      double var3 = this.getYsize();
      double var5 = this.getZsize();
      return (var1 + var3 + var5) / 3.0;
   }

   public double getXsize() {
      return this.maxX - this.minX;
   }

   public double getYsize() {
      return this.maxY - this.minY;
   }

   public double getZsize() {
      return this.maxZ - this.minZ;
   }

   public AABB deflate(double var1, double var3, double var5) {
      return this.inflate(-var1, -var3, -var5);
   }

   public AABB deflate(double var1) {
      return this.inflate(-var1);
   }

   public Optional<Vec3> clip(Vec3 var1, Vec3 var2) {
      double[] var3 = new double[]{1.0};
      double var4 = var2.x - var1.x;
      double var6 = var2.y - var1.y;
      double var8 = var2.z - var1.z;
      Direction var10 = getDirection(this, var1, var3, null, var4, var6, var8);
      if (var10 == null) {
         return Optional.empty();
      } else {
         double var11 = var3[0];
         return Optional.of(var1.add(var11 * var4, var11 * var6, var11 * var8));
      }
   }

   @Nullable
   public static BlockHitResult clip(Iterable<AABB> var0, Vec3 var1, Vec3 var2, BlockPos var3) {
      double[] var4 = new double[]{1.0};
      Direction var5 = null;
      double var6 = var2.x - var1.x;
      double var8 = var2.y - var1.y;
      double var10 = var2.z - var1.z;

      for (AABB var13 : var0) {
         var5 = getDirection(var13.move(var3), var1, var4, var5, var6, var8, var10);
      }

      if (var5 == null) {
         return null;
      } else {
         double var14 = var4[0];
         return new BlockHitResult(var1.add(var14 * var6, var14 * var8, var14 * var10), var5, var3, false);
      }
   }

   @Nullable
   private static Direction getDirection(AABB var0, Vec3 var1, double[] var2, @Nullable Direction var3, double var4, double var6, double var8) {
      if (var4 > 1.0E-7) {
         var3 = clipPoint(var2, var3, var4, var6, var8, var0.minX, var0.minY, var0.maxY, var0.minZ, var0.maxZ, Direction.WEST, var1.x, var1.y, var1.z);
      } else if (var4 < -1.0E-7) {
         var3 = clipPoint(var2, var3, var4, var6, var8, var0.maxX, var0.minY, var0.maxY, var0.minZ, var0.maxZ, Direction.EAST, var1.x, var1.y, var1.z);
      }

      if (var6 > 1.0E-7) {
         var3 = clipPoint(var2, var3, var6, var8, var4, var0.minY, var0.minZ, var0.maxZ, var0.minX, var0.maxX, Direction.DOWN, var1.y, var1.z, var1.x);
      } else if (var6 < -1.0E-7) {
         var3 = clipPoint(var2, var3, var6, var8, var4, var0.maxY, var0.minZ, var0.maxZ, var0.minX, var0.maxX, Direction.UP, var1.y, var1.z, var1.x);
      }

      if (var8 > 1.0E-7) {
         var3 = clipPoint(var2, var3, var8, var4, var6, var0.minZ, var0.minX, var0.maxX, var0.minY, var0.maxY, Direction.NORTH, var1.z, var1.x, var1.y);
      } else if (var8 < -1.0E-7) {
         var3 = clipPoint(var2, var3, var8, var4, var6, var0.maxZ, var0.minX, var0.maxX, var0.minY, var0.maxY, Direction.SOUTH, var1.z, var1.x, var1.y);
      }

      return var3;
   }

   @Nullable
   private static Direction clipPoint(
      double[] var0,
      @Nullable Direction var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      Direction var18,
      double var19,
      double var21,
      double var23
   ) {
      double var25 = (var8 - var19) / var2;
      double var27 = var21 + var25 * var4;
      double var29 = var23 + var25 * var6;
      if (0.0 < var25 && var25 < var0[0] && var10 - 1.0E-7 < var27 && var27 < var12 + 1.0E-7 && var14 - 1.0E-7 < var29 && var29 < var16 + 1.0E-7) {
         var0[0] = var25;
         return var18;
      } else {
         return var1;
      }
   }

   public double distanceToSqr(Vec3 var1) {
      double var2 = Math.max(Math.max(this.minX - var1.x, var1.x - this.maxX), 0.0);
      double var4 = Math.max(Math.max(this.minY - var1.y, var1.y - this.maxY), 0.0);
      double var6 = Math.max(Math.max(this.minZ - var1.z, var1.z - this.maxZ), 0.0);
      return Mth.lengthSquared(var2, var4, var6);
   }

   @Override
   public String toString() {
      return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   public boolean hasNaN() {
      return Double.isNaN(this.minX)
         || Double.isNaN(this.minY)
         || Double.isNaN(this.minZ)
         || Double.isNaN(this.maxX)
         || Double.isNaN(this.maxY)
         || Double.isNaN(this.maxZ);
   }

   public Vec3 getCenter() {
      return new Vec3(Mth.lerp(0.5, this.minX, this.maxX), Mth.lerp(0.5, this.minY, this.maxY), Mth.lerp(0.5, this.minZ, this.maxZ));
   }

   public static AABB ofSize(Vec3 var0, double var1, double var3, double var5) {
      return new AABB(var0.x - var1 / 2.0, var0.y - var3 / 2.0, var0.z - var5 / 2.0, var0.x + var1 / 2.0, var0.y + var3 / 2.0, var0.z + var5 / 2.0);
   }
}
