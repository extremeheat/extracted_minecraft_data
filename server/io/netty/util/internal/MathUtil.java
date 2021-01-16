package io.netty.util.internal;

public final class MathUtil {
   private MathUtil() {
      super();
   }

   public static int findNextPositivePowerOfTwo(int var0) {
      assert var0 > -2147483648 && var0 < 1073741824;

      return 1 << 32 - Integer.numberOfLeadingZeros(var0 - 1);
   }

   public static int safeFindNextPositivePowerOfTwo(int var0) {
      return var0 <= 0 ? 1 : (var0 >= 1073741824 ? 1073741824 : findNextPositivePowerOfTwo(var0));
   }

   public static boolean isOutOfBounds(int var0, int var1, int var2) {
      return (var0 | var1 | var0 + var1 | var2 - (var0 + var1)) < 0;
   }

   public static int compare(int var0, int var1) {
      return var0 < var1 ? -1 : (var0 > var1 ? 1 : 0);
   }

   public static int compare(long var0, long var2) {
      return var0 < var2 ? -1 : (var0 > var2 ? 1 : 0);
   }
}
