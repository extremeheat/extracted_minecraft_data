package it.unimi.dsi.fastutil.shorts;

public final class ShortHeaps {
   private ShortHeaps() {
      super();
   }

   public static int downHeap(short[] var0, int var1, int var2, ShortComparator var3) {
      assert var2 < var1;

      short var4 = var0[var2];
      int var5;
      short var6;
      int var7;
      if (var3 == null) {
         while((var5 = (var2 << 1) + 1) < var1) {
            var6 = var0[var5];
            var7 = var5 + 1;
            if (var7 < var1 && var0[var7] < var6) {
               var5 = var7;
               var6 = var0[var7];
            }

            if (var4 <= var6) {
               break;
            }

            var0[var2] = var6;
            var2 = var5;
         }
      } else {
         while((var5 = (var2 << 1) + 1) < var1) {
            var6 = var0[var5];
            var7 = var5 + 1;
            if (var7 < var1 && var3.compare(var0[var7], var6) < 0) {
               var5 = var7;
               var6 = var0[var7];
            }

            if (var3.compare(var4, var6) <= 0) {
               break;
            }

            var0[var2] = var6;
            var2 = var5;
         }
      }

      var0[var2] = var4;
      return var2;
   }

   public static int upHeap(short[] var0, int var1, int var2, ShortComparator var3) {
      assert var2 < var1;

      short var4 = var0[var2];
      int var5;
      short var6;
      if (var3 == null) {
         while(var2 != 0) {
            var5 = var2 - 1 >>> 1;
            var6 = var0[var5];
            if (var6 <= var4) {
               break;
            }

            var0[var2] = var6;
            var2 = var5;
         }
      } else {
         while(var2 != 0) {
            var5 = var2 - 1 >>> 1;
            var6 = var0[var5];
            if (var3.compare(var6, var4) <= 0) {
               break;
            }

            var0[var2] = var6;
            var2 = var5;
         }
      }

      var0[var2] = var4;
      return var2;
   }

   public static void makeHeap(short[] var0, int var1, ShortComparator var2) {
      int var3 = var1 >>> 1;

      while(var3-- != 0) {
         downHeap(var0, var1, var3, var2);
      }

   }
}
