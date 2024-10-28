package net.minecraft;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class Optionull {
   public Optionull() {
      super();
   }

   @Nullable
   public static <T, R> R map(@Nullable T var0, Function<T, R> var1) {
      return var0 == null ? null : var1.apply(var0);
   }

   public static <T, R> R mapOrDefault(@Nullable T var0, Function<T, R> var1, R var2) {
      return var0 == null ? var2 : var1.apply(var0);
   }

   public static <T, R> R mapOrElse(@Nullable T var0, Function<T, R> var1, Supplier<R> var2) {
      return var0 == null ? var2.get() : var1.apply(var0);
   }

   @Nullable
   public static <T> T first(Collection<T> var0) {
      Iterator var1 = var0.iterator();
      return var1.hasNext() ? var1.next() : null;
   }

   public static <T> T firstOrDefault(Collection<T> var0, T var1) {
      Iterator var2 = var0.iterator();
      return var2.hasNext() ? var2.next() : var1;
   }

   public static <T> T firstOrElse(Collection<T> var0, Supplier<T> var1) {
      Iterator var2 = var0.iterator();
      return var2.hasNext() ? var2.next() : var1.get();
   }

   public static <T> boolean isNullOrEmpty(@Nullable T[] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(@Nullable boolean[] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(@Nullable byte[] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(@Nullable char[] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(@Nullable short[] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(@Nullable int[] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(@Nullable long[] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(@Nullable float[] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(@Nullable double[] var0) {
      return var0 == null || var0.length == 0;
   }
}
