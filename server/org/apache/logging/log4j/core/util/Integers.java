package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.util.Strings;

public final class Integers {
   private static final int BITS_PER_INT = 32;

   private Integers() {
      super();
   }

   public static int parseInt(String var0, int var1) {
      return Strings.isEmpty(var0) ? var1 : Integer.parseInt(var0);
   }

   public static int parseInt(String var0) {
      return parseInt(var0, 0);
   }

   public static int ceilingNextPowerOfTwo(int var0) {
      return 1 << 32 - Integer.numberOfLeadingZeros(var0 - 1);
   }
}
