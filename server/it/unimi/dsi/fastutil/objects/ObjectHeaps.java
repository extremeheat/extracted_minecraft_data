package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public final class ObjectHeaps {
   private ObjectHeaps() {
      super();
   }

   public static <K> int downHeap(K[] var0, int var1, int var2, Comparator<? super K> var3) {
      assert var2 < var1;

      Object var4 = var0[var2];
      int var5;
      Object var6;
      int var7;
      if (var3 == null) {
         while((var5 = (var2 << 1) + 1) < var1) {
            var6 = var0[var5];
            var7 = var5 + 1;
            if (var7 < var1 && ((Comparable)var0[var7]).compareTo(var6) < 0) {
               var5 = var7;
               var6 = var0[var7];
            }

            if (((Comparable)var4).compareTo(var6) <= 0) {
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

   public static <K> int upHeap(K[] var0, int var1, int var2, Comparator<K> var3) {
      assert var2 < var1;

      Object var4 = var0[var2];
      int var5;
      Object var6;
      if (var3 == null) {
         while(var2 != 0) {
            var5 = var2 - 1 >>> 1;
            var6 = var0[var5];
            if (((Comparable)var6).compareTo(var4) <= 0) {
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

   public static <K> void makeHeap(K[] var0, int var1, Comparator<K> var2) {
      int var3 = var1 >>> 1;

      while(var3-- != 0) {
         downHeap(var0, var1, var3, var2);
      }

   }
}
