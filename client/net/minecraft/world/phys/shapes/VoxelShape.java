package net.minecraft.world.phys.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class VoxelShape {
   protected final DiscreteVoxelShape shape;
   @Nullable
   private VoxelShape[] faces;

   protected VoxelShape(DiscreteVoxelShape var1) {
      super();
      this.shape = var1;
   }

   public double min(Direction.Axis var1) {
      int var2 = this.shape.firstFull(var1);
      return var2 >= this.shape.getSize(var1) ? 1.0 / 0.0 : this.get(var1, var2);
   }

   public double max(Direction.Axis var1) {
      int var2 = this.shape.lastFull(var1);
      return var2 <= 0 ? -1.0 / 0.0 : this.get(var1, var2);
   }

   public AABB bounds() {
      if (this.isEmpty()) {
         throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("No bounds for empty shape."));
      } else {
         return new AABB(this.min(Direction.Axis.X), this.min(Direction.Axis.Y), this.min(Direction.Axis.Z), this.max(Direction.Axis.X), this.max(Direction.Axis.Y), this.max(Direction.Axis.Z));
      }
   }

   public VoxelShape singleEncompassing() {
      return this.isEmpty() ? Shapes.empty() : Shapes.box(this.min(Direction.Axis.X), this.min(Direction.Axis.Y), this.min(Direction.Axis.Z), this.max(Direction.Axis.X), this.max(Direction.Axis.Y), this.max(Direction.Axis.Z));
   }

   protected double get(Direction.Axis var1, int var2) {
      return this.getCoords(var1).getDouble(var2);
   }

   public abstract DoubleList getCoords(Direction.Axis var1);

   public boolean isEmpty() {
      return this.shape.isEmpty();
   }

   public VoxelShape move(Vec3 var1) {
      return this.move(var1.x, var1.y, var1.z);
   }

   public VoxelShape move(double var1, double var3, double var5) {
      return (VoxelShape)(this.isEmpty() ? Shapes.empty() : new ArrayVoxelShape(this.shape, new OffsetDoubleList(this.getCoords(Direction.Axis.X), var1), new OffsetDoubleList(this.getCoords(Direction.Axis.Y), var3), new OffsetDoubleList(this.getCoords(Direction.Axis.Z), var5)));
   }

   public VoxelShape optimize() {
      VoxelShape[] var1 = new VoxelShape[]{Shapes.empty()};
      this.forAllBoxes((var1x, var3, var5, var7, var9, var11) -> {
         var1[0] = Shapes.joinUnoptimized(var1[0], Shapes.box(var1x, var3, var5, var7, var9, var11), BooleanOp.OR);
      });
      return var1[0];
   }

   public void forAllEdges(Shapes.DoubleLineConsumer var1) {
      this.shape.forAllEdges((var2, var3, var4, var5, var6, var7) -> {
         var1.consume(this.get(Direction.Axis.X, var2), this.get(Direction.Axis.Y, var3), this.get(Direction.Axis.Z, var4), this.get(Direction.Axis.X, var5), this.get(Direction.Axis.Y, var6), this.get(Direction.Axis.Z, var7));
      }, true);
   }

   public void forAllBoxes(Shapes.DoubleLineConsumer var1) {
      DoubleList var2 = this.getCoords(Direction.Axis.X);
      DoubleList var3 = this.getCoords(Direction.Axis.Y);
      DoubleList var4 = this.getCoords(Direction.Axis.Z);
      this.shape.forAllBoxes((var4x, var5, var6, var7, var8, var9) -> {
         var1.consume(var2.getDouble(var4x), var3.getDouble(var5), var4.getDouble(var6), var2.getDouble(var7), var3.getDouble(var8), var4.getDouble(var9));
      }, true);
   }

   public List<AABB> toAabbs() {
      ArrayList var1 = Lists.newArrayList();
      this.forAllBoxes((var1x, var3, var5, var7, var9, var11) -> {
         var1.add(new AABB(var1x, var3, var5, var7, var9, var11));
      });
      return var1;
   }

   public double min(Direction.Axis var1, double var2, double var4) {
      Direction.Axis var6 = AxisCycle.FORWARD.cycle(var1);
      Direction.Axis var7 = AxisCycle.BACKWARD.cycle(var1);
      int var8 = this.findIndex(var6, var2);
      int var9 = this.findIndex(var7, var4);
      int var10 = this.shape.firstFull(var1, var8, var9);
      return var10 >= this.shape.getSize(var1) ? 1.0 / 0.0 : this.get(var1, var10);
   }

   public double max(Direction.Axis var1, double var2, double var4) {
      Direction.Axis var6 = AxisCycle.FORWARD.cycle(var1);
      Direction.Axis var7 = AxisCycle.BACKWARD.cycle(var1);
      int var8 = this.findIndex(var6, var2);
      int var9 = this.findIndex(var7, var4);
      int var10 = this.shape.lastFull(var1, var8, var9);
      return var10 <= 0 ? -1.0 / 0.0 : this.get(var1, var10);
   }

   protected int findIndex(Direction.Axis var1, double var2) {
      return Mth.binarySearch(0, this.shape.getSize(var1) + 1, (var4) -> {
         return var2 < this.get(var1, var4);
      }) - 1;
   }

   @Nullable
   public BlockHitResult clip(Vec3 var1, Vec3 var2, BlockPos var3) {
      if (this.isEmpty()) {
         return null;
      } else {
         Vec3 var4 = var2.subtract(var1);
         if (var4.lengthSqr() < 1.0E-7) {
            return null;
         } else {
            Vec3 var5 = var1.add(var4.scale(0.001));
            return this.shape.isFullWide(this.findIndex(Direction.Axis.X, var5.x - (double)var3.getX()), this.findIndex(Direction.Axis.Y, var5.y - (double)var3.getY()), this.findIndex(Direction.Axis.Z, var5.z - (double)var3.getZ())) ? new BlockHitResult(var5, Direction.getApproximateNearest(var4.x, var4.y, var4.z).getOpposite(), var3, true) : AABB.clip(this.toAabbs(), var1, var2, var3);
         }
      }
   }

   public Optional<Vec3> closestPointTo(Vec3 var1) {
      if (this.isEmpty()) {
         return Optional.empty();
      } else {
         Vec3[] var2 = new Vec3[1];
         this.forAllBoxes((var2x, var4, var6, var8, var10, var12) -> {
            double var14 = Mth.clamp(var1.x(), var2x, var8);
            double var16 = Mth.clamp(var1.y(), var4, var10);
            double var18 = Mth.clamp(var1.z(), var6, var12);
            if (var2[0] == null || var1.distanceToSqr(var14, var16, var18) < var1.distanceToSqr(var2[0])) {
               var2[0] = new Vec3(var14, var16, var18);
            }

         });
         return Optional.of(var2[0]);
      }
   }

   public VoxelShape getFaceShape(Direction var1) {
      if (!this.isEmpty() && this != Shapes.block()) {
         VoxelShape var2;
         if (this.faces != null) {
            var2 = this.faces[var1.ordinal()];
            if (var2 != null) {
               return var2;
            }
         } else {
            this.faces = new VoxelShape[6];
         }

         var2 = this.calculateFace(var1);
         this.faces[var1.ordinal()] = var2;
         return var2;
      } else {
         return this;
      }
   }

   private VoxelShape calculateFace(Direction var1) {
      Direction.Axis var2 = var1.getAxis();
      if (this.isCubeLikeAlong(var2)) {
         return this;
      } else {
         Direction.AxisDirection var3 = var1.getAxisDirection();
         int var4 = this.findIndex(var2, var3 == Direction.AxisDirection.POSITIVE ? 0.9999999 : 1.0E-7);
         SliceShape var5 = new SliceShape(this, var2, var4);
         if (var5.isEmpty()) {
            return Shapes.empty();
         } else {
            return (VoxelShape)(var5.isCubeLike() ? Shapes.block() : var5);
         }
      }
   }

   protected boolean isCubeLike() {
      Direction.Axis[] var1 = Direction.Axis.VALUES;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Direction.Axis var4 = var1[var3];
         if (!this.isCubeLikeAlong(var4)) {
            return false;
         }
      }

      return true;
   }

   private boolean isCubeLikeAlong(Direction.Axis var1) {
      DoubleList var2 = this.getCoords(var1);
      return var2.size() == 2 && DoubleMath.fuzzyEquals(var2.getDouble(0), 0.0, 1.0E-7) && DoubleMath.fuzzyEquals(var2.getDouble(1), 1.0, 1.0E-7);
   }

   public double collide(Direction.Axis var1, AABB var2, double var3) {
      return this.collideX(AxisCycle.between(var1, Direction.Axis.X), var2, var3);
   }

   protected double collideX(AxisCycle var1, AABB var2, double var3) {
      if (this.isEmpty()) {
         return var3;
      } else if (Math.abs(var3) < 1.0E-7) {
         return 0.0;
      } else {
         AxisCycle var5 = var1.inverse();
         Direction.Axis var6 = var5.cycle(Direction.Axis.X);
         Direction.Axis var7 = var5.cycle(Direction.Axis.Y);
         Direction.Axis var8 = var5.cycle(Direction.Axis.Z);
         double var9 = var2.max(var6);
         double var11 = var2.min(var6);
         int var13 = this.findIndex(var6, var11 + 1.0E-7);
         int var14 = this.findIndex(var6, var9 - 1.0E-7);
         int var15 = Math.max(0, this.findIndex(var7, var2.min(var7) + 1.0E-7));
         int var16 = Math.min(this.shape.getSize(var7), this.findIndex(var7, var2.max(var7) - 1.0E-7) + 1);
         int var17 = Math.max(0, this.findIndex(var8, var2.min(var8) + 1.0E-7));
         int var18 = Math.min(this.shape.getSize(var8), this.findIndex(var8, var2.max(var8) - 1.0E-7) + 1);
         int var19 = this.shape.getSize(var6);
         int var20;
         int var21;
         int var22;
         double var23;
         if (var3 > 0.0) {
            for(var20 = var14 + 1; var20 < var19; ++var20) {
               for(var21 = var15; var21 < var16; ++var21) {
                  for(var22 = var17; var22 < var18; ++var22) {
                     if (this.shape.isFullWide(var5, var20, var21, var22)) {
                        var23 = this.get(var6, var20) - var9;
                        if (var23 >= -1.0E-7) {
                           var3 = Math.min(var3, var23);
                        }

                        return var3;
                     }
                  }
               }
            }
         } else if (var3 < 0.0) {
            for(var20 = var13 - 1; var20 >= 0; --var20) {
               for(var21 = var15; var21 < var16; ++var21) {
                  for(var22 = var17; var22 < var18; ++var22) {
                     if (this.shape.isFullWide(var5, var20, var21, var22)) {
                        var23 = this.get(var6, var20 + 1) - var11;
                        if (var23 <= 1.0E-7) {
                           var3 = Math.max(var3, var23);
                        }

                        return var3;
                     }
                  }
               }
            }
         }

         return var3;
      }
   }

   public String toString() {
      return this.isEmpty() ? "EMPTY" : "VoxelShape[" + String.valueOf(this.bounds()) + "]";
   }
}
