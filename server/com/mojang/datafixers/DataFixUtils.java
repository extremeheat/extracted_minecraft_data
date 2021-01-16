package com.mojang.datafixers;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DataFixUtils {
   private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[]{0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

   private DataFixUtils() {
      super();
   }

   public static int smallestEncompassingPowerOfTwo(int var0) {
      int var1 = var0 - 1;
      var1 |= var1 >> 1;
      var1 |= var1 >> 2;
      var1 |= var1 >> 4;
      var1 |= var1 >> 8;
      var1 |= var1 >> 16;
      return var1 + 1;
   }

   private static boolean isPowerOfTwo(int var0) {
      return var0 != 0 && (var0 & var0 - 1) == 0;
   }

   public static int ceillog2(int var0) {
      var0 = isPowerOfTwo(var0) ? var0 : smallestEncompassingPowerOfTwo(var0);
      return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int)((long)var0 * 125613361L >> 27) & 31];
   }

   public static <T> T make(Supplier<T> var0) {
      return var0.get();
   }

   public static <T> T make(T var0, Consumer<T> var1) {
      var1.accept(var0);
      return var0;
   }

   public static <U> U orElse(Optional<? extends U> var0, U var1) {
      return var0.isPresent() ? var0.get() : var1;
   }

   public static <U> U orElseGet(Optional<? extends U> var0, Supplier<? extends U> var1) {
      return var0.isPresent() ? var0.get() : var1.get();
   }

   public static <U> Optional<U> or(Optional<? extends U> var0, Supplier<? extends Optional<? extends U>> var1) {
      return var0.isPresent() ? var0.map((var0x) -> {
         return var0x;
      }) : ((Optional)var1.get()).map((var0x) -> {
         return var0x;
      });
   }

   public static byte[] toArray(ByteBuffer var0) {
      byte[] var1;
      if (var0.hasArray()) {
         var1 = var0.array();
      } else {
         var1 = new byte[var0.capacity()];
         var0.get(var1, 0, var1.length);
      }

      return var1;
   }

   public static int makeKey(int var0) {
      return makeKey(var0, 0);
   }

   public static int makeKey(int var0, int var1) {
      return var0 * 10 + var1;
   }

   public static int getVersion(int var0) {
      return var0 / 10;
   }

   public static int getSubVersion(int var0) {
      return var0 % 10;
   }

   public static <T> UnaryOperator<T> consumerToFunction(Consumer<T> var0) {
      return (var1) -> {
         var0.accept(var1);
         return var1;
      };
   }
}
