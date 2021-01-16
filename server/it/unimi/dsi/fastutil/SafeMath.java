package it.unimi.dsi.fastutil;

public final class SafeMath {
   private SafeMath() {
      super();
   }

   public static char safeIntToChar(int var0) {
      if (var0 >= 0 && 65535 >= var0) {
         return (char)var0;
      } else {
         throw new IllegalArgumentException(var0 + " can't be represented as char");
      }
   }

   public static byte safeIntToByte(int var0) {
      if (var0 >= -128 && 127 >= var0) {
         return (byte)var0;
      } else {
         throw new IllegalArgumentException(var0 + " can't be represented as byte (out of range)");
      }
   }

   public static short safeIntToShort(int var0) {
      if (var0 >= -32768 && 32767 >= var0) {
         return (short)var0;
      } else {
         throw new IllegalArgumentException(var0 + " can't be represented as short (out of range)");
      }
   }

   public static int safeLongToInt(long var0) {
      if (var0 >= -2147483648L && 2147483647L >= var0) {
         return (int)var0;
      } else {
         throw new IllegalArgumentException(var0 + " can't be represented as int (out of range)");
      }
   }

   public static float safeDoubleToFloat(double var0) {
      if (Double.isNaN(var0)) {
         return 0.0F / 0.0;
      } else if (Double.isInfinite(var0)) {
         return var0 < 0.0D ? -1.0F / 0.0 : 1.0F / 0.0;
      } else if (var0 >= 1.401298464324817E-45D && 3.4028234663852886E38D >= var0) {
         float var2 = (float)var0;
         if ((double)var2 != var0) {
            throw new IllegalArgumentException(var0 + " can't be represented as float (imprecise)");
         } else {
            return var2;
         }
      } else {
         throw new IllegalArgumentException(var0 + " can't be represented as float (out of range)");
      }
   }
}
