package net.minecraft.util.math.shapes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;

public final class VoxelShapes {
   private static final VoxelShape field_197886_a = new VoxelShapeArray(new VoxelShapePartBitSet(0, 0, 0), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}), new DoubleArrayList(new double[]{0.0D}));
   private static final VoxelShape field_197887_b = (VoxelShape)Util.func_199748_a(() -> {
      VoxelShapePartBitSet var0 = new VoxelShapePartBitSet(1, 1, 1);
      var0.func_199625_a(0, 0, 0, true, true);
      return new VoxelShapeCube(var0);
   });

   public static VoxelShape func_197880_a() {
      return field_197886_a;
   }

   public static VoxelShape func_197868_b() {
      return field_197887_b;
   }

   public static VoxelShape func_197873_a(double var0, double var2, double var4, double var6, double var8, double var10) {
      return func_197881_a(new AxisAlignedBB(var0, var2, var4, var6, var8, var10));
   }

   public static VoxelShape func_197881_a(AxisAlignedBB var0) {
      int var1 = func_197885_a(var0.field_72340_a, var0.field_72336_d);
      int var2 = func_197885_a(var0.field_72338_b, var0.field_72337_e);
      int var3 = func_197885_a(var0.field_72339_c, var0.field_72334_f);
      if (var1 >= 0 && var2 >= 0 && var3 >= 0) {
         if (var1 == 0 && var2 == 0 && var3 == 0) {
            return var0.func_197744_e(0.5D, 0.5D, 0.5D) ? func_197868_b() : func_197880_a();
         } else {
            int var4 = 1 << var1;
            int var5 = 1 << var2;
            int var6 = 1 << var3;
            int var7 = (int)Math.round(var0.field_72340_a * (double)var4);
            int var8 = (int)Math.round(var0.field_72336_d * (double)var4);
            int var9 = (int)Math.round(var0.field_72338_b * (double)var5);
            int var10 = (int)Math.round(var0.field_72337_e * (double)var5);
            int var11 = (int)Math.round(var0.field_72339_c * (double)var6);
            int var12 = (int)Math.round(var0.field_72334_f * (double)var6);
            VoxelShapePartBitSet var13 = new VoxelShapePartBitSet(var4, var5, var6, var7, var9, var11, var8, var10, var12);

            for(long var14 = (long)var7; var14 < (long)var8; ++var14) {
               for(long var16 = (long)var9; var16 < (long)var10; ++var16) {
                  for(long var18 = (long)var11; var18 < (long)var12; ++var18) {
                     var13.func_199625_a((int)var14, (int)var16, (int)var18, false, true);
                  }
               }
            }

            return new VoxelShapeCube(var13);
         }
      } else {
         return new VoxelShapeArray(field_197887_b.field_197768_g, new double[]{var0.field_72340_a, var0.field_72336_d}, new double[]{var0.field_72338_b, var0.field_72337_e}, new double[]{var0.field_72339_c, var0.field_72334_f});
      }
   }

   private static int func_197885_a(double var0, double var2) {
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

   protected static long func_197877_a(int var0, int var1) {
      return (long)var0 * (long)(var1 / IntMath.gcd(var0, var1));
   }

   public static VoxelShape func_197872_a(VoxelShape var0, VoxelShape var1) {
      return func_197878_a(var0, var1, IBooleanFunction.OR);
   }

   public static VoxelShape func_197878_a(VoxelShape var0, VoxelShape var1, IBooleanFunction var2) {
      return func_197882_b(var0, var1, var2).func_197753_c();
   }

   public static VoxelShape func_197882_b(VoxelShape var0, VoxelShape var1, IBooleanFunction var2) {
      if (var2.apply(false, false)) {
         throw new IllegalArgumentException();
      } else if (var0 == var1) {
         return var2.apply(true, true) ? var0 : func_197880_a();
      } else {
         boolean var3 = var2.apply(true, false);
         boolean var4 = var2.apply(false, true);
         if (var0.func_197766_b()) {
            return var4 ? var1 : func_197880_a();
         } else if (var1.func_197766_b()) {
            return var3 ? var0 : func_197880_a();
         } else {
            IDoubleListMerger var5 = func_199410_a(1, var0.func_197757_a(EnumFacing.Axis.X), var1.func_197757_a(EnumFacing.Axis.X), var3, var4);
            IDoubleListMerger var6 = func_199410_a(var5.func_212435_a().size() - 1, var0.func_197757_a(EnumFacing.Axis.Y), var1.func_197757_a(EnumFacing.Axis.Y), var3, var4);
            IDoubleListMerger var7 = func_199410_a((var5.func_212435_a().size() - 1) * (var6.func_212435_a().size() - 1), var0.func_197757_a(EnumFacing.Axis.Z), var1.func_197757_a(EnumFacing.Axis.Z), var3, var4);
            VoxelShapePartBitSet var8 = VoxelShapePartBitSet.func_197852_a(var0.field_197768_g, var1.field_197768_g, var5, var6, var7, var2);
            return (VoxelShape)(var5 instanceof DoubleCubeMergingList && var6 instanceof DoubleCubeMergingList && var7 instanceof DoubleCubeMergingList ? new VoxelShapeCube(var8) : new VoxelShapeArray(var8, var5.func_212435_a(), var6.func_212435_a(), var7.func_212435_a()));
         }
      }
   }

   public static boolean func_197879_c(VoxelShape var0, VoxelShape var1, IBooleanFunction var2) {
      if (var2.apply(false, false)) {
         throw new IllegalArgumentException();
      } else if (var0 == var1) {
         return var2.apply(true, true);
      } else if (var0.func_197766_b()) {
         return var2.apply(false, !var1.func_197766_b());
      } else if (var1.func_197766_b()) {
         return var2.apply(!var0.func_197766_b(), false);
      } else {
         boolean var3 = var2.apply(true, false);
         boolean var4 = var2.apply(false, true);
         EnumFacing.Axis[] var5 = AxisRotation.field_197521_d;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing.Axis var8 = var5[var7];
            if (var0.func_197758_c(var8) < var1.func_197762_b(var8) - 1.0E-7D) {
               return var3 || var4;
            }

            if (var1.func_197758_c(var8) < var0.func_197762_b(var8) - 1.0E-7D) {
               return var3 || var4;
            }
         }

         IDoubleListMerger var9 = func_199410_a(1, var0.func_197757_a(EnumFacing.Axis.X), var1.func_197757_a(EnumFacing.Axis.X), var3, var4);
         IDoubleListMerger var10 = func_199410_a(var9.func_212435_a().size() - 1, var0.func_197757_a(EnumFacing.Axis.Y), var1.func_197757_a(EnumFacing.Axis.Y), var3, var4);
         IDoubleListMerger var11 = func_199410_a((var9.func_212435_a().size() - 1) * (var10.func_212435_a().size() - 1), var0.func_197757_a(EnumFacing.Axis.Z), var1.func_197757_a(EnumFacing.Axis.Z), var3, var4);
         return func_197874_a(var9, var10, var11, var0.field_197768_g, var1.field_197768_g, var2);
      }
   }

   private static boolean func_197874_a(IDoubleListMerger var0, IDoubleListMerger var1, IDoubleListMerger var2, VoxelShapePart var3, VoxelShapePart var4, IBooleanFunction var5) {
      return !var0.func_197855_a((var5x, var6, var7) -> {
         return var1.func_197855_a((var6x, var7x, var8) -> {
            return var2.func_197855_a((var7, var8x, var9) -> {
               return !var5.apply(var3.func_197818_c(var5x, var6x, var7), var4.func_197818_c(var6, var7x, var8x));
            });
         });
      });
   }

   public static double func_212437_a(EnumFacing.Axis var0, AxisAlignedBB var1, Stream<VoxelShape> var2, double var3) {
      for(Iterator var5 = var2.iterator(); var5.hasNext(); var3 = ((VoxelShape)var5.next()).func_212430_a(var0, var1, var3)) {
         if (Math.abs(var3) < 1.0E-7D) {
            return 0.0D;
         }
      }

      return var3;
   }

   public static boolean func_197875_a(VoxelShape var0, VoxelShape var1, EnumFacing var2) {
      if (var0 == func_197868_b() && var1 == func_197868_b()) {
         return true;
      } else if (var1.func_197766_b()) {
         return false;
      } else {
         EnumFacing.Axis var3 = var2.func_176740_k();
         EnumFacing.AxisDirection var4 = var2.func_176743_c();
         VoxelShape var5 = var4 == EnumFacing.AxisDirection.POSITIVE ? var0 : var1;
         VoxelShape var6 = var4 == EnumFacing.AxisDirection.POSITIVE ? var1 : var0;
         IBooleanFunction var7 = var4 == EnumFacing.AxisDirection.POSITIVE ? IBooleanFunction.ONLY_FIRST : IBooleanFunction.ONLY_SECOND;
         return DoubleMath.fuzzyEquals(var5.func_197758_c(var3), 1.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(var6.func_197762_b(var3), 0.0D, 1.0E-7D) && !func_197879_c(new VoxelShapeSplit(var5, var3, var5.field_197768_g.func_197819_a(var3) - 1), new VoxelShapeSplit(var6, var3, 0), var7);
      }
   }

   public static boolean func_204642_b(VoxelShape var0, VoxelShape var1, EnumFacing var2) {
      if (var0 != func_197868_b() && var1 != func_197868_b()) {
         EnumFacing.Axis var3 = var2.func_176740_k();
         EnumFacing.AxisDirection var4 = var2.func_176743_c();
         VoxelShape var5 = var4 == EnumFacing.AxisDirection.POSITIVE ? var0 : var1;
         VoxelShape var6 = var4 == EnumFacing.AxisDirection.POSITIVE ? var1 : var0;
         if (!DoubleMath.fuzzyEquals(var5.func_197758_c(var3), 1.0D, 1.0E-7D)) {
            var5 = func_197880_a();
         }

         if (!DoubleMath.fuzzyEquals(var6.func_197762_b(var3), 0.0D, 1.0E-7D)) {
            var6 = func_197880_a();
         }

         return !func_197879_c(func_197868_b(), func_197882_b(new VoxelShapeSplit(var5, var3, var5.field_197768_g.func_197819_a(var3) - 1), new VoxelShapeSplit(var6, var3, 0), IBooleanFunction.OR), IBooleanFunction.ONLY_FIRST);
      } else {
         return true;
      }
   }

   @VisibleForTesting
   protected static IDoubleListMerger func_199410_a(int var0, DoubleList var1, DoubleList var2, boolean var3, boolean var4) {
      if (var1 instanceof DoubleRangeList && var2 instanceof DoubleRangeList) {
         int var5 = var1.size() - 1;
         int var6 = var2.size() - 1;
         long var7 = func_197877_a(var5, var6);
         if ((long)var0 * var7 <= 256L) {
            return new DoubleCubeMergingList(var5, var6);
         }
      }

      if (var1.getDouble(var1.size() - 1) < var2.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(var1, var2, false);
      } else if (var2.getDouble(var2.size() - 1) < var1.getDouble(0) - 1.0E-7D) {
         return new NonOverlappingMerger(var2, var1, true);
      } else if (Objects.equals(var1, var2)) {
         if (var1 instanceof SimpleDoubleMerger) {
            return (IDoubleListMerger)var1;
         } else {
            return (IDoubleListMerger)(var2 instanceof SimpleDoubleMerger ? (IDoubleListMerger)var2 : new SimpleDoubleMerger(var1));
         }
      } else {
         return new IndirectMerger(var1, var2, var3, var4);
      }
   }

   public interface LineConsumer {
      void consume(double var1, double var3, double var5, double var7, double var9, double var11);
   }
}
