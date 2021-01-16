package io.netty.util.internal.shaded.org.jctools.util;

public final class Pow2 {
   public static final int MAX_POW2 = 1073741824;

   public Pow2() {
      super();
   }

   public static int roundToPowerOfTwo(int var0) {
      if (var0 > 1073741824) {
         throw new IllegalArgumentException("There is no larger power of 2 int for value:" + var0 + " since it exceeds 2^31.");
      } else if (var0 < 0) {
         throw new IllegalArgumentException("Given value:" + var0 + ". Expecting value >= 0.");
      } else {
         int var1 = 1 << 32 - Integer.numberOfLeadingZeros(var0 - 1);
         return var1;
      }
   }

   public static boolean isPowerOfTwo(int var0) {
      return (var0 & var0 - 1) == 0;
   }

   public static long align(long var0, int var2) {
      if (!isPowerOfTwo(var2)) {
         throw new IllegalArgumentException("alignment must be a power of 2:" + var2);
      } else {
         return var0 + (long)(var2 - 1) & (long)(~(var2 - 1));
      }
   }
}
