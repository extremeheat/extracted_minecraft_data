package it.unimi.dsi.fastutil.longs;

import java.util.Arrays;

public final class LongIndirectHeaps {
   private LongIndirectHeaps() {
      super();
   }

   public static int downHeap(long[] var0, int[] var1, int[] var2, int var3, int var4, LongComparator var5) {
      assert var4 < var3;

      int var6 = var1[var4];
      long var7 = var0[var6];
      int var9;
      int var10;
      int var11;
      if (var5 == null) {
         while((var9 = (var4 << 1) + 1) < var3) {
            var10 = var1[var9];
            var11 = var9 + 1;
            if (var11 < var3 && var0[var1[var11]] < var0[var10]) {
               var9 = var11;
               var10 = var1[var11];
            }

            if (var7 <= var0[var10]) {
               break;
            }

            var1[var4] = var10;
            var2[var1[var4]] = var4;
            var4 = var9;
         }
      } else {
         while((var9 = (var4 << 1) + 1) < var3) {
            var10 = var1[var9];
            var11 = var9 + 1;
            if (var11 < var3 && var5.compare(var0[var1[var11]], var0[var10]) < 0) {
               var9 = var11;
               var10 = var1[var11];
            }

            if (var5.compare(var7, var0[var10]) <= 0) {
               break;
            }

            var1[var4] = var10;
            var2[var1[var4]] = var4;
            var4 = var9;
         }
      }

      var1[var4] = var6;
      var2[var6] = var4;
      return var4;
   }

   public static int upHeap(long[] var0, int[] var1, int[] var2, int var3, int var4, LongComparator var5) {
      assert var4 < var3;

      int var6 = var1[var4];
      long var7 = var0[var6];
      int var9;
      int var10;
      if (var5 == null) {
         while(var4 != 0) {
            var9 = var4 - 1 >>> 1;
            var10 = var1[var9];
            if (var0[var10] <= var7) {
               break;
            }

            var1[var4] = var10;
            var2[var1[var4]] = var4;
            var4 = var9;
         }
      } else {
         while(var4 != 0) {
            var9 = var4 - 1 >>> 1;
            var10 = var1[var9];
            if (var5.compare(var0[var10], var7) <= 0) {
               break;
            }

            var1[var4] = var10;
            var2[var1[var4]] = var4;
            var4 = var9;
         }
      }

      var1[var4] = var6;
      var2[var6] = var4;
      return var4;
   }

   public static void makeHeap(long[] var0, int var1, int var2, int[] var3, int[] var4, LongComparator var5) {
      LongArrays.ensureOffsetLength(var0, var1, var2);
      if (var3.length < var2) {
         throw new IllegalArgumentException("The heap length (" + var3.length + ") is smaller than the number of elements (" + var2 + ")");
      } else if (var4.length < var0.length) {
         throw new IllegalArgumentException("The inversion array length (" + var3.length + ") is smaller than the length of the reference array (" + var0.length + ")");
      } else {
         Arrays.fill(var4, 0, var0.length, -1);

         int var6;
         for(var6 = var2; var6-- != 0; var4[var3[var6] = var1 + var6] = var6) {
         }

         var6 = var2 >>> 1;

         while(var6-- != 0) {
            downHeap(var0, var3, var4, var2, var6, var5);
         }

      }
   }

   public static void makeHeap(long[] var0, int[] var1, int[] var2, int var3, LongComparator var4) {
      int var5 = var3 >>> 1;

      while(var5-- != 0) {
         downHeap(var0, var1, var2, var3, var5, var4);
      }

   }
}
