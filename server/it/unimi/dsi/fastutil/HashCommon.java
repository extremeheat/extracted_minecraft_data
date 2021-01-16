package it.unimi.dsi.fastutil;

public class HashCommon {
   private static final int INT_PHI = -1640531527;
   private static final int INV_INT_PHI = 340573321;
   private static final long LONG_PHI = -7046029254386353131L;
   private static final long INV_LONG_PHI = -1018231460777725123L;

   protected HashCommon() {
      super();
   }

   public static int murmurHash3(int var0) {
      var0 ^= var0 >>> 16;
      var0 *= -2048144789;
      var0 ^= var0 >>> 13;
      var0 *= -1028477387;
      var0 ^= var0 >>> 16;
      return var0;
   }

   public static long murmurHash3(long var0) {
      var0 ^= var0 >>> 33;
      var0 *= -49064778989728563L;
      var0 ^= var0 >>> 33;
      var0 *= -4265267296055464877L;
      var0 ^= var0 >>> 33;
      return var0;
   }

   public static int mix(int var0) {
      int var1 = var0 * -1640531527;
      return var1 ^ var1 >>> 16;
   }

   public static int invMix(int var0) {
      return (var0 ^ var0 >>> 16) * 340573321;
   }

   public static long mix(long var0) {
      long var2 = var0 * -7046029254386353131L;
      var2 ^= var2 >>> 32;
      return var2 ^ var2 >>> 16;
   }

   public static long invMix(long var0) {
      var0 ^= var0 >>> 32;
      var0 ^= var0 >>> 16;
      return (var0 ^ var0 >>> 32) * -1018231460777725123L;
   }

   public static int float2int(float var0) {
      return Float.floatToRawIntBits(var0);
   }

   public static int double2int(double var0) {
      long var2 = Double.doubleToRawLongBits(var0);
      return (int)(var2 ^ var2 >>> 32);
   }

   public static int long2int(long var0) {
      return (int)(var0 ^ var0 >>> 32);
   }

   public static int nextPowerOfTwo(int var0) {
      if (var0 == 0) {
         return 1;
      } else {
         --var0;
         var0 |= var0 >> 1;
         var0 |= var0 >> 2;
         var0 |= var0 >> 4;
         var0 |= var0 >> 8;
         return (var0 | var0 >> 16) + 1;
      }
   }

   public static long nextPowerOfTwo(long var0) {
      if (var0 == 0L) {
         return 1L;
      } else {
         --var0;
         var0 |= var0 >> 1;
         var0 |= var0 >> 2;
         var0 |= var0 >> 4;
         var0 |= var0 >> 8;
         var0 |= var0 >> 16;
         return (var0 | var0 >> 32) + 1L;
      }
   }

   public static int maxFill(int var0, float var1) {
      return Math.min((int)Math.ceil((double)((float)var0 * var1)), var0 - 1);
   }

   public static long maxFill(long var0, float var2) {
      return Math.min((long)Math.ceil((double)((float)var0 * var2)), var0 - 1L);
   }

   public static int arraySize(int var0, float var1) {
      long var2 = Math.max(2L, nextPowerOfTwo((long)Math.ceil((double)((float)var0 / var1))));
      if (var2 > 1073741824L) {
         throw new IllegalArgumentException("Too large (" + var0 + " expected elements with load factor " + var1 + ")");
      } else {
         return (int)var2;
      }
   }

   public static long bigArraySize(long var0, float var2) {
      return nextPowerOfTwo((long)Math.ceil((double)((float)var0 / var2)));
   }
}
