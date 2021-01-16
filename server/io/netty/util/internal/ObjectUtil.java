package io.netty.util.internal;

import java.util.Collection;

public final class ObjectUtil {
   private ObjectUtil() {
      super();
   }

   public static <T> T checkNotNull(T var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException(var1);
      } else {
         return var0;
      }
   }

   public static int checkPositive(int var0, String var1) {
      if (var0 <= 0) {
         throw new IllegalArgumentException(var1 + ": " + var0 + " (expected: > 0)");
      } else {
         return var0;
      }
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

   public static long checkPositiveOrZero(long var0, String var2) {
      if (var0 < 0L) {
         throw new IllegalArgumentException(var2 + ": " + var0 + " (expected: >= 0)");
      } else {
         return var0;
      }
   }

   public static <T> T[] checkNonEmpty(T[] var0, String var1) {
      checkNotNull(var0, var1);
      checkPositive(var0.length, var1 + ".length");
      return var0;
   }

   public static <T extends Collection<?>> T checkNonEmpty(T var0, String var1) {
      checkNotNull(var0, var1);
      checkPositive(var0.size(), var1 + ".size");
      return var0;
   }

   public static int intValue(Integer var0, int var1) {
      return var0 != null ? var0 : var1;
   }

   public static long longValue(Long var0, long var1) {
      return var0 != null ? var0 : var1;
   }
}
