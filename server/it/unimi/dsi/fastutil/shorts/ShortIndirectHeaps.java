package it.unimi.dsi.fastutil.shorts;

import java.util.Arrays;

public final class ShortIndirectHeaps {
   private ShortIndirectHeaps() {
      super();
   }

   public static int downHeap(short[] var0, int[] var1, int[] var2, int var3, int var4, ShortComparator var5) {
      assert var4 < var3;

      int var6 = var1[var4];
      short var7 = var0[var6];
      int var8;
      int var9;
      int var10;
      if (var5 == null) {
         while((var8 = (var4 << 1) + 1) < var3) {
            var9 = var1[var8];
            var10 = var8 + 1;
            if (var10 < var3 && var0[var1[var10]] < var0[var9]) {
               var8 = var10;
               var9 = var1[var10];
            }

            if (var7 <= var0[var9]) {
               break;
            }

            var1[var4] = var9;
            var2[var1[var4]] = var4;
            var4 = var8;
         }
      } else {
         while((var8 = (var4 << 1) + 1) < var3) {
            var9 = var1[var8];
            var10 = var8 + 1;
            if (var10 < var3 && var5.compare(var0[var1[var10]], var0[var9]) < 0) {
               var8 = var10;
               var9 = var1[var10];
            }

            if (var5.compare(var7, var0[var9]) <= 0) {
               break;
            }

            var1[var4] = var9;
            var2[var1[var4]] = var4;
            var4 = var8;
         }
      }

      var1[var4] = var6;
      var2[var6] = var4;
      return var4;
   }

   public static int upHeap(short[] var0, int[] var1, int[] var2, int var3, int var4, ShortComparator var5) {
      assert var4 < var3;

      int var6 = var1[var4];
      short var7 = var0[var6];
      int var8;
      int var9;
      if (var5 == null) {
         while(var4 != 0) {
            var8 = var4 - 1 >>> 1;
            var9 = var1[var8];
            if (var0[var9] <= var7) {
               break;
            }

            var1[var4] = var9;
            var2[var1[var4]] = var4;
            var4 = var8;
         }
      } else {
         while(var4 != 0) {
            var8 = var4 - 1 >>> 1;
            var9 = var1[var8];
            if (var5.compare(var0[var9], var7) <= 0) {
               break;
            }

            var1[var4] = var9;
            var2[var1[var4]] = var4;
            var4 = var8;
         }
      }

      var1[var4] = var6;
      var2[var6] = var4;
      return var4;
   }

   public static void makeHeap(short[] var0, int var1, int var2, int[] var3, int[] var4, ShortComparator var5) {
      ShortArrays.ensureOffsetLength(var0, var1, var2);
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

   public static void makeHeap(short[] var0, int[] var1, int[] var2, int var3, ShortComparator var4) {
      int var5 = var3 >>> 1;

      while(var5-- != 0) {
         downHeap(var0, var1, var2, var3, var5, var4);
      }

   }
}
