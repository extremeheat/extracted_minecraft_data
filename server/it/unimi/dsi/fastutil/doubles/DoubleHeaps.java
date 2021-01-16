package it.unimi.dsi.fastutil.doubles;

public final class DoubleHeaps {
   private DoubleHeaps() {
      super();
   }

   public static int downHeap(double[] var0, int var1, int var2, DoubleComparator var3) {
      assert var2 < var1;

      double var4 = var0[var2];
      int var6;
      double var7;
      int var9;
      if (var3 == null) {
         while((var6 = (var2 << 1) + 1) < var1) {
            var7 = var0[var6];
            var9 = var6 + 1;
            if (var9 < var1 && Double.compare(var0[var9], var7) < 0) {
               var6 = var9;
               var7 = var0[var9];
            }

            if (Double.compare(var4, var7) <= 0) {
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

   public static int upHeap(double[] var0, int var1, int var2, DoubleComparator var3) {
      assert var2 < var1;

      double var4 = var0[var2];
      int var6;
      double var7;
      if (var3 == null) {
         while(var2 != 0) {
            var6 = var2 - 1 >>> 1;
            var7 = var0[var6];
            if (Double.compare(var7, var4) <= 0) {
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

   public static void makeHeap(double[] var0, int var1, DoubleComparator var2) {
      int var3 = var1 >>> 1;

      while(var3-- != 0) {
         downHeap(var0, var1, var3, var2);
      }

   }
}
