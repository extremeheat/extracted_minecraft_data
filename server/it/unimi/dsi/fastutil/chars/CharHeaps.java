package it.unimi.dsi.fastutil.chars;

public final class CharHeaps {
   private CharHeaps() {
      super();
   }

   public static int downHeap(char[] var0, int var1, int var2, CharComparator var3) {
      assert var2 < var1;

      char var4 = var0[var2];
      int var5;
      char var6;
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

   public static int upHeap(char[] var0, int var1, int var2, CharComparator var3) {
      assert var2 < var1;

      char var4 = var0[var2];
      int var5;
      char var6;
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

   public static void makeHeap(char[] var0, int var1, CharComparator var2) {
      int var3 = var1 >>> 1;

      while(var3-- != 0) {
         downHeap(var0, var1, var3, var2);
      }

   }
}
