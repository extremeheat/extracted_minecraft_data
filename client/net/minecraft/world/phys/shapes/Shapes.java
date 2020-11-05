package net.minecraft.world.phys.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.AxisCycle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class Shapes {
   private static final VoxelShape BLOCK = (VoxelShape)Util.make(() -> {
      BitSetDiscreteVoxelShape var0 = new BitSetDiscreteVoxelShape(1, 1, 1);
      var0.setFull(0, 0, 0, true, true);
      return new CubeVoxelShape(var0);
   });
   public static final VoxelShape INFINITY = box(-1.0D / 0.0, -1.0D / 0.0, -1.0D / 0.0, 1.0D / 0.0, 1.0D / 0.0, 1.0D / 0.0);
   private static final VoxelShape EMPTY = new ArrayVoxelShape(new BitSetDiscreteVoxelShape(0, 0, 0), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}));

   public static VoxelShape empty() {
      return EMPTY;
   }

   public static VoxelShape block() {
      return BLOCK;
   }

   public static VoxelShape box(double var0, double var2, double var4, double var6, double var8, double var10) {
      return create(new AABB(var0, var2, var4, var6, var8, var10));
   }

   public static VoxelShape create(AABB var0) {
      int var1 = findBits(var0.minX, var0.maxX);
      int var2 = findBits(var0.minY, var0.maxY);
      int var3 = findBits(var0.minZ, var0.maxZ);
      if (var1 >= 0 && var2 >= 0 && var3 >= 0) {
         if (var1 == 0 && var2 == 0 && var3 == 0) {
            return var0.contains(0.5D, 0.5D, 0.5D) ? block() : empty();
         } else {
            int var4 = 1 << var1;
            int var5 = 1 << var2;
            int var6 = 1 << var3;
            int var7 = (int)Math.round(var0.minX * (double)var4);
            int var8 = (int)Math.round(var0.maxX * (double)var4);
            int var9 = (int)Math.round(var0.minY * (double)var5);
            int var10 = (int)Math.round(var0.maxY * (double)var5);
            int var11 = (int)Math.round(var0.minZ * (double)var6);
            int var12 = (int)Math.round(var0.maxZ * (double)var6);
            BitSetDiscreteVoxelShape var13 = new BitSetDiscreteVoxelShape(var4, var5, var6, var7, var9, var11, var8, var10, var12);

            for(long var14 = (long)var7; var14 < (long)var8; ++var14) {
               for(long var16 = (long)var9; var16 < (long)var10; ++var16) {
                  for(long var18 = (long)var11; var18 < (long)var12; ++var18) {
                     var13.setFull((int)var14, (int)var16, (int)var18, false, true);
                  }
               }
            }

            return new CubeVoxelShape(var13);
         }
      } else {
         return new ArrayVoxelShape(BLOCK.shape, new double[]{var0.minX, var0.maxX}, new double[]{var0.minY, var0.maxY}, new double[]{var0.minZ, var0.maxZ});
      }
   }

   private static int findBits(double var0, double var2) {
      if (var0 >= -1.0E-7D && var2 <= 1.0000001D) {
         for(int var4 = 0; var4 <= 3; ++var4) {
            double var5 = var0 * (double)(1 << var4);
            double var7 = var2 * (double)(1 << var4);
            boolean var9 = Math.abs(var5 - Math.floor(var5)) < 1.0E-7D;
            boolean var10 = Math.abs(var7 - Math.floor(var7)) < 1.0E-7D;
            if (var9 && var10) {
               return var4;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   protected static long lcm(int var0, int var1) {
      return (long)var0 * (long)(var1 / IntMath.gcd(var0, var1));
   }

   public static VoxelShape or(VoxelShape var0, VoxelShape var1) {
      return join(var0, var1, BooleanOp.OR);
   }

   public static VoxelShape or(VoxelShape var0, VoxelShape... var1) {
      return (VoxelShape)Arrays.stream(var1).reduce(var0, Shapes::or);
   }

   public static VoxelShape join(VoxelShape var0, VoxelShape var1, BooleanOp var2) {
      return joinUnoptimized(var0, var1, var2).optimize();
   }

   public static VoxelShape joinUnoptimized(VoxelShape var0, VoxelShape var1, BooleanOp var2) {
      if (var2.apply(false, false)) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException());
      } else if (var0 == var1) {
         return var2.apply(true, true) ? var0 : empty();
      } else {
         boolean var3 = var2.apply(true, false);
         boolean var4 = var2.apply(false, true);
         if (var0.isEmpty()) {
            return var4 ? var1 : empty();
         } else if (var1.isEmpty()) {
            return var3 ? var0 : empty();
         } else {
            IndexMerger var5 = createIndexMerger(1, var0.getCoords(Direction.Axis.X), var1.getCoords(Direction.Axis.X), var3, var4);
            IndexMerger var6 = createIndexMerger(var5.getList().size() - 1, var0.getCoords(Direction.Axis.Y), var1.getCoords(Direction.Axis.Y), var3, var4);
            IndexMerger var7 = createIndexMerger((var5.getList().size() - 1) * (var6.getList().size() - 1), var0.getCoords(Direction.Axis.Z), var1.getCoords(Direction.Axis.Z), var3, var4);
            BitSetDiscreteVoxelShape var8 = BitSetDiscreteVoxelShape.join(var0.shape, var1.shape, var5, var6, var7, var2);
            return (VoxelShape)(var5 instanceof DiscreteCubeMerger && var6 instanceof DiscreteCubeMerger && var7 instanceof DiscreteCubeMerger ? new CubeVoxelShape(var8) : new ArrayVoxelShape(var8, var5.getList(), var6.getList(), var7.getList()));
         }
      }
   }

   public static boolean joinIsNotEmpty(VoxelShape var0, VoxelShape var1, BooleanOp var2) {
      if (var2.apply(false, false)) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException());
      } else if (var0 == var1) {
         return var2.apply(true, true);
      } else if (var0.isEmpty()) {
         return var2.apply(false, !var1.isEmpty());
      } else if (var1.isEmpty()) {
         return var2.apply(!var0.isEmpty(), false);
      } else {
         boolean var3 = var2.apply(true, false);
         boolean var4 = var2.apply(false, true);
         Direction.Axis[] var5 = AxisCycle.AXIS_VALUES;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction.Axis var8 = var5[var7];
            if (var0.max(var8) < var1.min(var8) - 1.0E-7D) {
               return var3 || var4;
            }

            if (var1.max(var8) < var0.min(var8) - 1.0E-7D) {
               return var3 || var4;
            }
         }

         IndexMerger var9 = createIndexMerger(1, var0.getCoords(Direction.Axis.X), var1.getCoords(Direction.Axis.X), var3, var4);
         IndexMerger var10 = createIndexMerger(var9.getList().size() - 1, var0.getCoords(Direction.Axis.Y), var1.getCoords(Direction.Axis.Y), var3, var4);
         IndexMerger var11 = createIndexMerger((var9.getList().size() - 1) * (var10.getList().size() - 1), var0.getCoords(Direction.Axis.Z), var1.getCoords(Direction.Axis.Z), var3, var4);
         return joinIsNotEmpty(var9, var10, var11, var0.shape, var1.shape, var2);
      }
   }

   private static boolean joinIsNotEmpty(IndexMerger var0, IndexMerger var1, IndexMerger var2, DiscreteVoxelShape var3, DiscreteVoxelShape var4, BooleanOp var5) {
      return !var0.forMergedIndexes((var5x, var6, var7) -> {
         return var1.forMergedIndexes((var6x, var7x, var8) -> {
            return var2.forMergedIndexes((var7, var8x, var9) -> {
               return !var5.apply(var3.isFullWide(var5x, var6x, var7), var4.isFullWide(var6, var7x, var8x));
            });
         });
      });
   }

   public static double collide(Direction.Axis var0, AABB var1, Stream<VoxelShape> var2, double var3) {
      for(Iterator var5 = var2.iterator(); var5.hasNext(); var3 = ((VoxelShape)var5.next()).collide(var0, var1, var3)) {
         if (Math.abs(var3) < 1.0E-7D) {
            return 0.0D;
         }
      }

      return var3;
   }

   public static double collide(Direction.Axis var0, AABB var1, LevelReader var2, double var3, CollisionContext var5, Stream<VoxelShape> var6) {
      return collide(var1, var2, var3, var5, AxisCycle.between(var0, Direction.Axis.Z), var6);
   }

   private static double collide(AABB var0, LevelReader var1, double var2, CollisionContext var4, AxisCycle var5, Stream<VoxelShape> var6) {
      if (var0.getXsize() >= 1.0E-6D && var0.getYsize() >= 1.0E-6D && var0.getZsize() >= 1.0E-6D) {
         if (Math.abs(var2) < 1.0E-7D) {
            return 0.0D;
         } else {
            AxisCycle var7 = var5.inverse();
            Direction.Axis var8 = var7.cycle(Direction.Axis.X);
            Direction.Axis var9 = var7.cycle(Direction.Axis.Y);
            Direction.Axis var10 = var7.cycle(Direction.Axis.Z);
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
            int var12 = Mth.floor(var0.min(var8) - 1.0E-7D) - 1;
            int var13 = Mth.floor(var0.max(var8) + 1.0E-7D) + 1;
            int var14 = Mth.floor(var0.min(var9) - 1.0E-7D) - 1;
            int var15 = Mth.floor(var0.max(var9) + 1.0E-7D) + 1;
            double var16 = var0.min(var10) - 1.0E-7D;
            double var18 = var0.max(var10) + 1.0E-7D;
            boolean var20 = var2 > 0.0D;
            int var21 = var20 ? Mth.floor(var0.max(var10) - 1.0E-7D) - 1 : Mth.floor(var0.min(var10) + 1.0E-7D) + 1;
            int var22 = lastC(var2, var16, var18);
            int var23 = var20 ? 1 : -1;
            int var24 = var21;

            while(true) {
               if (var20) {
                  if (var24 > var22) {
                     break;
                  }
               } else if (var24 < var22) {
                  break;
               }

               for(int var25 = var12; var25 <= var13; ++var25) {
                  for(int var26 = var14; var26 <= var15; ++var26) {
                     int var27 = 0;
                     if (var25 == var12 || var25 == var13) {
                        ++var27;
                     }

                     if (var26 == var14 || var26 == var15) {
                        ++var27;
                     }

                     if (var24 == var21 || var24 == var22) {
                        ++var27;
                     }

                     if (var27 < 3) {
                        var11.set(var7, var25, var26, var24);
                        BlockState var28 = var1.getBlockState(var11);
                        if ((var27 != 1 || var28.hasLargeCollisionShape()) && (var27 != 2 || var28.is(Blocks.MOVING_PISTON))) {
                           var2 = var28.getCollisionShape(var1, var11, var4).collide(var10, var0.move((double)(-var11.getX()), (double)(-var11.getY()), (double)(-var11.getZ())), var2);
                           if (Math.abs(var2) < 1.0E-7D) {
                              return 0.0D;
                           }

                           var22 = lastC(var2, var16, var18);
                        }
                     }
                  }
               }

               var24 += var23;
            }

            double[] var29 = new double[]{var2};
            var6.forEach((var3) -> {
               var29[0] = var3.collide(var10, var0, var29[0]);
            });
            return var29[0];
         }
      } else {
         return var2;
      }
   }

   private static int lastC(double var0, double var2, double var4) {
      return var0 > 0.0D ? Mth.floor(var4 + var0) + 1 : Mth.floor(var2 + var0) - 1;
   }

   public static boolean blockOccudes(VoxelShape var0, VoxelShape var1, Direction var2) {
      if (var0 == block() && var1 == block()) {
         return true;
      } else if (var1.isEmpty()) {
         return false;
      } else {
         Direction.Axis var3 = var2.getAxis();
         Direction.AxisDirection var4 = var2.getAxisDirection();
         VoxelShape var5 = var4 == Direction.AxisDirection.POSITIVE ? var0 : var1;
         VoxelShape var6 = var4 == Direction.AxisDirection.POSITIVE ? var1 : var0;
         BooleanOp var7 = var4 == Direction.AxisDirection.POSITIVE ? BooleanOp.ONLY_FIRST : BooleanOp.ONLY_SECOND;
         return DoubleMath.fuzzyEquals(var5.max(var3), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(var6.min(var3), 0.0D, 1.0E-7D) && !joinIsNotEmpty(new SliceShape(var5, var3, var5.shape.getSize(var3) - 1), new SliceShape(var6, var3, 0), var7);
      }
   }

   public static VoxelShape getFaceShape(VoxelShape var0, Direction var1) {
      if (var0 == block()) {
         return block();
      } else {
         Direction.Axis var4 = var1.getAxis();
         boolean var2;
         int var3;
         if (var1.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            var2 = DoubleMath.fuzzyEquals(var0.max(var4), 1.0D, 1.0E-7D);
            var3 = var0.shape.getSize(var4) - 1;
         } else {
            var2 = DoubleMath.fuzzyEquals(var0.min(var4), 0.0D, 1.0E-7D);
            var3 = 0;
         }

         return (VoxelShape)(!var2 ? empty() : new SliceShape(var0, var4, var3));
      }
   }

   public static boolean mergedFaceOccludes(VoxelShape var0, VoxelShape var1, Direction var2) {
      if (var0 != block() && var1 != block()) {
         Direction.Axis var3 = var2.getAxis();
         Direction.AxisDirection var4 = var2.getAxisDirection();
         VoxelShape var5 = var4 == Direction.AxisDirection.POSITIVE ? var0 : var1;
         VoxelShape var6 = var4 == Direction.AxisDirection.POSITIVE ? var1 : var0;
         if (!DoubleMath.fuzzyEquals(var5.max(var3), 1.0D, 1.0E-7D)) {
            var5 = empty();
         }

         if (!DoubleMath.fuzzyEquals(var6.min(var3), 0.0D, 1.0E-7D)) {
            var6 = empty();
         }

         return !joinIsNotEmpty(block(), joinUnoptimized(new SliceShape(var5, var3, var5.shape.getSize(var3) - 1), new SliceShape(var6, var3, 0), BooleanOp.OR), BooleanOp.ONLY_FIRST);
      } else {
         return true;
      }
   }

   public static boolean faceShapeOccludes(VoxelShape var0, VoxelShape var1) {
      if (var0 != block() && var1 != block()) {
         if (var0.isEmpty() && var1.isEmpty()) {
            return false;
         } else {
            return !joinIsNotEmpty(block(), joinUnoptimized(var0, var1, BooleanOp.OR), BooleanOp.ONLY_FIRST);
         }
      } else {
         return true;
      }
   }

   @VisibleForTesting
   protected static IndexMerger createIndexMerger(int var0, DoubleList var1, DoubleList var2, boolean var3, boolean var4) {
      int var5 = var1.size() - 1;
      int var6 = var2.size() - 1;
      if (var1 instanceof CubePointRange && var2 instanceof CubePointRange) {
         long var7 = lcm(var5, var6);
         if ((long)var0 * var7 <= 256L) {
            return new DiscreteCubeMerger(var5, var6);
         }
      }

      if (var1.getDouble(var5) < var2.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(var1, var2, false);
      } else if (var2.getDouble(var6) < var1.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(var2, var1, true);
      } else if (var5 == var6 && Objects.equals(var1, var2)) {
         if (var1 instanceof IdenticalMerger) {
            return (IndexMerger)var1;
         } else {
            return (IndexMerger)(var2 instanceof IdenticalMerger ? (IndexMerger)var2 : new IdenticalMerger(var1));
         }
      } else {
         return new IndirectMerger(var1, var2, var3, var4);
      }
   }

   public interface DoubleLineConsumer {
      void consume(double var1, double var3, double var5, double var7, double var9, double var11);
   }
}
