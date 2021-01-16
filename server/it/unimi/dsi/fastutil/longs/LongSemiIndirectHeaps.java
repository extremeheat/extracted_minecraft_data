package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.ints.IntArrays;

public final class LongSemiIndirectHeaps {
   private LongSemiIndirectHeaps() {
      super();
   }

   public static int downHeap(long[] var0, int[] var1, int var2, int var3, LongComparator var4) {
      assert var3 < var2;

      int var5 = var1[var3];
      long var6 = var0[var5];
      int var8;
      int var9;
      int var10;
      if (var4 == null) {
         while((var8 = (var3 << 1) + 1) < var2) {
            var9 = var1[var8];
            var10 = var8 + 1;
            if (var10 < var2 && var0[var1[var10]] < var0[var9]) {
               var8 = var10;
               var9 = var1[var10];
            }

            if (var6 <= var0[var9]) {
               break;
            }

            var1[var3] = var9;
            var3 = var8;
         }
      } else {
         while((var8 = (var3 << 1) + 1) < var2) {
            var9 = var1[var8];
            var10 = var8 + 1;
            if (var10 < var2 && var4.compare(var0[var1[var10]], var0[var9]) < 0) {
               var8 = var10;
               var9 = var1[var10];
            }

            if (var4.compare(var6, var0[var9]) <= 0) {
               break;
            }

            var1[var3] = var9;
            var3 = var8;
         }
      }

      var1[var3] = var5;
      return var3;
   }

   public static int upHeap(long[] var0, int[] var1, int var2, int var3, LongComparator var4) {
      assert var3 < var2;

      int var5 = var1[var3];
      long var6 = var0[var5];
      int var8;
      int var9;
      if (var4 == null) {
         while(var3 != 0) {
            var8 = var3 - 1 >>> 1;
            var9 = var1[var8];
            if (var0[var9] <= var6) {
               break;
            }

            var1[var3] = var9;
            var3 = var8;
         }
      } else {
         while(var3 != 0) {
            var8 = var3 - 1 >>> 1;
            var9 = var1[var8];
            if (var4.compare(var0[var9], var6) <= 0) {
               break;
            }

            var1[var3] = var9;
            var3 = var8;
         }
      }

      var1[var3] = var5;
      return var3;
   }

   public static void makeHeap(long[] var0, int var1, int var2, int[] var3, LongComparator var4) {
      LongArrays.ensureOffsetLength(var0, var1, var2);
      if (var3.length < var2) {
         throw new IllegalArgumentException("The heap length (" + var3.length + ") is smaller than the number of elements (" + var2 + ")");
      } else {
         int var5;
         for(var5 = var2; var5-- != 0; var3[var5] = var1 + var5) {
         }

         var5 = var2 >>> 1;

         while(var5-- != 0) {
            downHeap(var0, var3, var2, var5, var4);
         }

      }
   }

   public static int[] makeHeap(long[] var0, int var1, int var2, LongComparator var3) {
      int[] var4 = var2 <= 0 ? IntArrays.EMPTY_ARRAY : new int[var2];
      makeHeap(var0, var1, var2, var4, var3);
      return var4;
   }

   public static void makeHeap(long[] var0, int[] var1, int var2, LongComparator var3) {
      int var4 = var2 >>> 1;

      while(var4-- != 0) {
         downHeap(var0, var1, var2, var4, var3);
      }

   }

   public static int front(long[] var0, int[] var1, int var2, int[] var3) {
      long var4 = var0[var1[0]];
      int var6 = 0;
      int var7 = 0;
      int var8 = 1;
      int var9 = 0;

      for(int var10 = 0; var10 < var8; ++var10) {
         if (var10 == var9) {
            if (var7 >= var8) {
               break;
            }

            var9 = (var9 << 1) + 1;
            var10 = var7;
            var7 = -1;
         }

         if (var4 == var0[var1[var10]]) {
            var3[var6++] = var1[var10];
            if (var7 == -1) {
               var7 = var10 * 2 + 1;
            }

            var8 = Math.min(var2, var10 * 2 + 3);
         }
      }

      return var6;
   }

   public static int front(long[] var0, int[] var1, int var2, int[] var3, LongComparator var4) {
      long var5 = var0[var1[0]];
      int var7 = 0;
      int var8 = 0;
      int var9 = 1;
      int var10 = 0;

      for(int var11 = 0; var11 < var9; ++var11) {
         if (var11 == var10) {
            if (var8 >= var9) {
               break;
            }

            var10 = (var10 << 1) + 1;
            var11 = var8;
            var8 = -1;
         }

         if (var4.compare(var5, var0[var1[var11]]) == 0) {
            var3[var7++] = var1[var11];
            if (var8 == -1) {
               var8 = var11 * 2 + 1;
            }

            var9 = Math.min(var2, var11 * 2 + 3);
         }
      }

      return var7;
   }
}
