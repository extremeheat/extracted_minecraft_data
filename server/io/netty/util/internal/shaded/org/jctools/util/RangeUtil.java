package io.netty.util.internal.shaded.org.jctools.util;

public final class RangeUtil {
   public RangeUtil() {
      super();
   }

   public static long checkPositive(long var0, String var2) {
      if (var0 <= 0L) {
         throw new IllegalArgumentException(var2 + ": " + var0 + " (expected: > 0)");
      } else {
         return var0;
      }
   }

   public static int checkPositiveOrZero(int var0, String var1) {
      if (var0 < 0) {
         throw new IllegalArgumentException(var1 + ": " + var0 + " (expected: >= 0)");
      } else {
         return var0;
      }
   }

   public static int checkLessThan(int var0, int var1, String var2) {
      if (var0 >= var1) {
         throw new IllegalArgumentException(var2 + ": " + var0 + " (expected: < " + var1 + ')');
      } else {
         return var0;
      }
   }

   public static int checkLessThanOrEqual(int var0, long var1, String var3) {
      if ((long)var0 > var1) {
         throw new IllegalArgumentException(var3 + ": " + var0 + " (expected: <= " + var1 + ')');
      } else {
         return var0;
      }
   }

   public static int checkGreaterThanOrEqual(int var0, int var1, String var2) {
      if (var0 < var1) {
         throw new IllegalArgumentException(var2 + ": " + var0 + " (expected: >= " + var1 + ')');
      } else {
         return var0;
      }
   }
}
