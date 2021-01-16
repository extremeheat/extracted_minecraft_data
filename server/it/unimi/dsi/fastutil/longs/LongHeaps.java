package it.unimi.dsi.fastutil.longs;

public final class LongHeaps {
   private LongHeaps() {
      super();
   }

   public static int downHeap(long[] var0, int var1, int var2, LongComparator var3) {
      assert var2 < var1;

      long var4 = var0[var2];
      int var6;
      long var7;
      int var9;
      if (var3 == null) {
         while((var6 = (var2 << 1) + 1) < var1) {
            var7 = var0[var6];
            var9 = var6 + 1;
            if (var9 < var1 && var0[var9] < var7) {
               var6 = var9;
               var7 = var0[var9];
            }

            if (var4 <= var7) {
               break;
            }

            var0[var2] = var7;
            var2 = var6;
         }
      } else {
         while((var6 = (var2 << 1) + 1) < var1) {
            var7 = var0[var6];
            var9 = var6 + 1;
            if (var9 < var1 && var3.compare(var0[var9], var7) < 0) {
               var6 = var9;
               var7 = var0[var9];
            }

            if (var3.compare(var4, var7) <= 0) {
               break;
            }

            var0[var2] = var7;
            var2 = var6;
         }
      }

      var0[var2] = var4;
      return var2;
   }

   public static int upHeap(long[] var0, int var1, int var2, LongComparator var3) {
      assert var2 < var1;

      long var4 = var0[var2];
      int var6;
      long var7;
      if (var3 == null) {
         while(var2 != 0) {
            var6 = var2 - 1 >>> 1;
            var7 = var0[var6];
            if (var7 <= var4) {
               break;
            }

            var0[var2] = var7;
            var2 = var6;
         }
      } else {
         while(var2 != 0) {
            var6 = var2 - 1 >>> 1;
            var7 = var0[var6];
            if (var3.compare(var7, var4) <= 0) {
               break;
            }

            var0[var2] = var7;
            var2 = var6;
         }
      }

      var0[var2] = var4;
      return var2;
   }

   public static void makeHeap(long[] var0, int var1, LongComparator var2) {
      int var3 = var1 >>> 1;

      while(var3-- != 0) {
         downHeap(var0, var1, var3, var2);
      }

   }
}
